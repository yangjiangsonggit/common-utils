Spring Cloud 客服端负载均衡 Ribbon
一、简介       

       Spring Cloud Ribbon 是一个基于Http和TCP的客服端负载均衡工具，它是基于Netflix Ribbon实现的。它不像服务注册中心、配置中心、API网关那样独立部署，但是它几乎存在于每个微服务的基础设施中。包括前面的提供的声明式服务调用也是基于该Ribbon实现的。理解Ribbon对于我们使用Spring Cloud来讲非常的重要，因为负载均衡是对系统的高可用、网络压力的缓解和处理能力扩容的重要手段之一。在上节的例子中，我们采用了声明式的方式来实现负载均衡。实际上，内部调用维护了一个RestTemplate对象，该对象会使用Ribbon的自动化配置，同时通过@LoadBalanced开启客户端负载均衡。其实RestTemplate是Spring自己提供的对象，不是新的内容。读者不知道RestTemplate可以查看相关的文档。

二、探索

我们可以从LoadBalanced开始追踪：

 

1
2
3
4
5
6
7
8
9
10
11
/**
 * Annotation to mark a RestTemplate bean to be configured to use a LoadBalancerClient
 * @author Spencer Gibb
 */
@Target({ ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Qualifier
public @interface LoadBalanced {
}
 

　　从其该注解的定义注释我们可以知道，该注解用来给RestTemplate做标记，以使用负载均衡的客户端（LoadBalancerClient）来配置RestTemplate。接着我们来查看一下LoadBalancerClient这个接口定义:

1
2
3
4
5
6
7
8
9
10
11
12
13
14
15
16
17
18
19
20
21
22
23
24
25
26
27
28
29
30
31
32
33
34
35
36
37
38
39
40
/**
 * Represents a client side load balancer
 * @author Spencer Gibb
 */
public interface LoadBalancerClient extends ServiceInstanceChooser {
 
    /**
     * execute request using a ServiceInstance from the LoadBalancer for the specified
     * service
     * @param serviceId the service id to look up the LoadBalancer
     * @param request allows implementations to execute pre and post actions such as
     * incrementing metrics
     * @return the result of the LoadBalancerRequest callback on the selected
     * ServiceInstance
     */
    <T> T execute(String serviceId, LoadBalancerRequest<T> request) throws IOException;
 
    /**
     * execute request using a ServiceInstance from the LoadBalancer for the specified
     * service
     * @param serviceId the service id to look up the LoadBalancer
     * @param serviceInstance the service to execute the request to
     * @param request allows implementations to execute pre and post actions such as
     * incrementing metrics
     * @return the result of the LoadBalancerRequest callback on the selected
     * ServiceInstance
     */
    <T> T execute(String serviceId, ServiceInstance serviceInstance, LoadBalancerRequest<T> request) throws IOException;
 
    /**
     * Create a proper URI with a real host and port for systems to utilize.
     * Some systems use a URI with the logical serivce name as the host,
     * such as http://myservice/path/to/service.  This will replace the
     * service name with the host:port from the ServiceInstance.
     * @param instance
     * @param original a URI with the host as a logical service name
     * @return a reconstructed URI
     */
    URI reconstructURI(ServiceInstance instance, URI original);
}
　　

1
<span style="font-size: 16px">LoadBalancerClient 是集成 ServiceInstanceChooser，接着我们查看一下该接口定义:</span>
1
2
3
4
5
6
7
8
9
public interface ServiceInstanceChooser {
 
    /**
     * Choose a ServiceInstance from the LoadBalancer for the specified service
     * @param serviceId the service id to look up the LoadBalancer
     * @return a ServiceInstance that matches the serviceId
     */
    ServiceInstance choose(String serviceId);
}
　　从上面的注解中，我们可以知道 choose()方法根据传入的serviceId服务Id，从负载均衡器选择一个一个对应的服务实例。execute()方法根据serviceId服务ID和请求request来执行请求内容。reconstructURI()方法构建出一个合适的Host:Port的URI。而 RibbonLoadBalancerClient就是LoadBalancerClient的具体实现。

     接着我们查看LoadBalancerClient所在的包，结构如下:



　　我们发现有一个类我们需要去关注一下:LoadBalancerAutoConfiguration ，从其源码注解中我们知道是一个为Ribbon 自动化配置类。注释如下：

1
2
3
4
5
6
7
8
9
10
11
12
13
14
15
16
17
18
19
20
21
22
23
24
25
26
27
28
29
30
31
32
33
34
35
36
37
38
39
40
41
42
43
44
45
46
47
48
49
50
51
52
53
54
55
56
57
58
59
60
61
62
63
64
65
66
67
68
69
70
71
72
73
74
75
76
77
78
79
80
81
82
83
84
85
86
87
88
89
90
91
92
93
94
95
96
97
98
99
100
101
102
103
104
105
106
107
108
109
/**
 * Auto configuration for Ribbon (client side load balancing).
 *
 * @author Spencer Gibb
 * @author Dave Syer
 * @author Will Tran
 */
@Configuration
@ConditionalOnClass(RestTemplate.class) //条件 : RestTemplate必须在工程的类路径下
@ConditionalOnBean(LoadBalancerClient.class)  //条件: Spring 容器中必须包含LoadBalancerClient的实现，即RibbonLoadBalancerClient
@EnableConfigurationProperties(LoadBalancerRetryProperties.class) //启动重试功能，可以spring.cloud.loadbalancer.retry=false,取消重试，默认参数为true
public class LoadBalancerAutoConfiguration {
 
    @LoadBalanced
    @Autowired(required = false)
    private List<RestTemplate> restTemplates = Collections.emptyList(); //维护一个RestTemplate列表，通过LoadBalanced来注解。
 
    @Bean
    public SmartInitializingSingleton loadBalancedRestTemplateInitializer( //加载初始话自定义的restTeplate,实质是初始化InterceptingHttpAccessor具体调用
            final List<RestTemplateCustomizer> customizers) {
        return new SmartInitializingSingleton() {
            @Override
            public void afterSingletonsInstantiated() {
                for (RestTemplate restTemplate : LoadBalancerAutoConfiguration.this.restTemplates) {
                    for (RestTemplateCustomizer customizer : customizers) {
                        customizer.customize(restTemplate);
                    }
                }
            }
        };
    }
 
    @Autowired(required = false)
    private List<LoadBalancerRequestTransformer> transformers = Collections.emptyList();
 
    @Bean
    @ConditionalOnMissingBean
    public LoadBalancerRequestFactory loadBalancerRequestFactory(
            LoadBalancerClient loadBalancerClient) {
        return new LoadBalancerRequestFactory(loadBalancerClient, transformers);
    }
 
    @Configuration
    @ConditionalOnMissingClass("org.springframework.retry.support.RetryTemplate")
    static class LoadBalancerInterceptorConfig {
        @Bean
        public LoadBalancerInterceptor ribbonInterceptor(
                LoadBalancerClient loadBalancerClient,
                LoadBalancerRequestFactory requestFactory) {
            return new LoadBalancerInterceptor(loadBalancerClient, requestFactory);
        }
 
        @Bean
        @ConditionalOnMissingBean
        public RestTemplateCustomizer restTemplateCustomizer(
                final LoadBalancerInterceptor loadBalancerInterceptor) {
            return new RestTemplateCustomizer() {
                @Override
                public void customize(RestTemplate restTemplate) {
                    List<ClientHttpRequestInterceptor> list = new ArrayList<>(
                            restTemplate.getInterceptors());
                    list.add(loadBalancerInterceptor);
                    restTemplate.setInterceptors(list);
                }
            };
        }
    }
 
    @Configuration
    @ConditionalOnClass(RetryTemplate.class)
    static class RetryAutoConfiguration {
        @Bean
        public RetryTemplate retryTemplate() {
            RetryTemplate template =  new RetryTemplate();
            template.setThrowLastExceptionOnExhausted(true);
            return template;
        }
 
        @Bean
        @ConditionalOnMissingBean
        public LoadBalancedRetryPolicyFactory loadBalancedRetryPolicyFactory() {
            return new LoadBalancedRetryPolicyFactory.NeverRetryFactory();
        }
 
        @Bean
        public RetryLoadBalancerInterceptor ribbonInterceptor(
                LoadBalancerClient loadBalancerClient, LoadBalancerRetryProperties properties,
                LoadBalancedRetryPolicyFactory lbRetryPolicyFactory,
                LoadBalancerRequestFactory requestFactory) {
            return new RetryLoadBalancerInterceptor(loadBalancerClient, retryTemplate(), properties,
                    lbRetryPolicyFactory, requestFactory);
        }
 
        @Bean
        @ConditionalOnMissingBean
        public RestTemplateCustomizer restTemplateCustomizer( //自定义RestTemplate ,实质是初始化InterceptingHttpAccessor
                final RetryLoadBalancerInterceptor loadBalancerInterceptor) {
            return new RestTemplateCustomizer() {
                @Override
                public void customize(RestTemplate restTemplate) {
                    List<ClientHttpRequestInterceptor> list = new ArrayList<>(
                            restTemplate.getInterceptors());
                    list.add(loadBalancerInterceptor);
                    restTemplate.setInterceptors(list);
                }
            };
        }
    }
}
　　从上面代码得知，我们需要了解一下LoadBalancerInterceptor，该类用于实现对客户端发起请求时进行拦截，以实现客户端的负载均衡。在需要更多的章节才能理清其关系，我还要细细的阅读，等阅读差不多在给大家讲讲。

三、配置

当于Spring Cloud应用引入Ribbon和Eureka的时候，会触发Eureka中实现的Ribbon的自动化配置。

 serverList 的维护机制是由 DiscoveryEnabledNIWSServerList的实例维护，该类会将服务清单列表交给Eureka的服务治理机制来维护。

IPing的实现由 NIWSDiscoveryPing 的实例维护，该类也将服务检查交给Eureka的服务治理机制来维护。

默认情况下，用于获取实例请求的ServiceList接口实现是采用Spring Cloud Eureka中封装的DomainExtractingServerList.由于Spring Cloud Ribbon默认实现了区域亲和策略，所以我们可以通过Eureka实例的元数据配置来实现区域化的实例配置方案。

比如 eureka.instance.metadataMap.zone=hangzhou. 通过zone参数来指定自己所在的区域。

在Spring Cloud Ribbon 与 Spring Cloud Eureka结合工程中，我们可以通过参数配置方式来禁用Eureka对Ribbon服务实例的维护实现。

ribbon.eureka.enabled=false