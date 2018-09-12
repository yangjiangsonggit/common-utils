基于Spring的微服务结点在能力上没有高低贵贱之分，但是在角色上会分为边缘服务和内部服务两部分。内部服务顾名思义是为对内暴露服务的结点，供架构内部来调用；边缘服务是对外部网络暴露的服务结点，也就是对外API接口。

 

开发人员头疼的地方：为了防止我的程序在网络上被人攻击，我们需要写各种权限机制，这些机制在每个微服务结点都要实现一次。一旦鉴权上有什么bug，又要全部节点上推倒重来，噩梦。

运维人员头疼的地方：边缘服务前段都会架一个F5或者Nginx等负载均衡的代理，需要手动维护一份服务列表和服务地址的路由信息，随着结点的扩展或地址调整这份列表要变来变去。

 

为了解决鉴权重复的问题，使业务结点本身只关心实现自己的业务，将对权限的处理抽离到上层。外部客户先请求到Zuul上，在Zuul服务上对权限进行统一实现和过滤，以实现微服务结点的过滤和验证。

为了解决请求路由和安全过滤，Spring Cloud推出了一个API gateway组件：Spring Cloud Zuul。

在路由方面，Zuul将自己作为一个微服务结点注册到Eureka上，就获取了所有微服务的实例信息，同时又以服务名为ContextPath的方式创建路由映射。



案例代码：

Eureka：https://github.com/yejingtao/forblog/tree/master/demo-eureka-register

内部服务：https://github.com/yejingtao/forblog/tree/master/demo-feign-freeservice

外部服务：https://github.com/yejingtao/forblog/tree/master/demo-feign-freeconsumer

Zuul网关：https://github.com/yejingtao/forblog/tree/master/demo-zuul

其中前3个项目是前面一篇博客介绍的内容，我们这里把它们拿过来跑个龙套，这里主要介绍zuul



Pom依赖

<dependencies>
  		<dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-eureka</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-zuul</artifactId>
        </dependency>
  </dependencies>
包含了zuul-core、hystrix、ribbon、actuator

Application主程序

@EnableZuulProxy
@SpringCloudApplication
public class DemoFeignApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(DemoFeignApplication.class, args);
	}
	
	@Bean
	public AccessFilter accessFilter() {
		return new AccessFilter();
	}
 
}
新注解：@EnableZuulProxy，标识启动zuul网管控制；

创建过滤器的实例，启动过滤器AccessFilter。

application.yml参数配置：

server:
  port: 9053
 
spring:
  application:
    name: demo-zuul
    
eureka:
  client:
    serviceUrl:
      defaultZone: http://peer1:1111/eureka/,http://peer2:1112/eureka/
zuul:
  routes:
    demo-feign-freeservice:
      path: /api-service/**
    user-service:
      path: /api-consumer/**
      serviceId: demo-feign-freeconsumer
    163:
      path: /163
      url: http://www.163.com/
这里对路由的配置用了3种格式，体现了API Gateway的路由分发功能：

第一种格式（path-url）：如果请求/163这个地址，将会转发到http://www.163.com上去

第二种(path-serviceId)：如果请求/api-consumer开头的地址，将会转发到eureka上serviceId为“demo-feign-freeconsumer”这个服务上去。

第三种(给微服务名指定path)：给demo-feign-freeservice这个微服务指定了它的请求地址是/api-service/**

（其实这里还隐藏了第四种：什么都不配，默认给注册到eureka上的所有服务以第三种方式进行路由，path就是微服务自己的名字，但是此种方式又是不安全的，相当于将非边缘服务也暴露给外网，一般需要关闭这个默认配置zuul.ignored-services=*，或者至少将受保护的微服务列表维护到zuul.ignored-services中）


过滤器代码：
public class AccessFilter extends ZuulFilter{
	
 
	@Override
	public Object run() {
		RequestContext ctx = RequestContext.getCurrentContext();
		HttpServletRequest request = ctx.getRequest();
		
		Object accessToken = request.getParameter("accessToken");
		if(accessToken==null) {
			ctx.setSendZuulResponse(false);
			ctx.setResponseStatusCode(401);
			//为了被error过滤器捕获
			ctx.set("error.status_code", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			ctx.set("error.exception",new RuntimeException("AccessToken is null"));
			return null;
		}
		return null;
	}
 
	@Override
	public boolean shouldFilter() {
		RequestContext ctx = RequestContext.getCurrentContext();
		HttpServletRequest request = ctx.getRequest();
		if(request.getRequestURI().equals("/163")) {
			return false;
		}else {
			return true;
		}		
	}
 
	@Override
	public int filterOrder() {
		return 0;
	}
 
	@Override
	public String filterType() {
		return "pre";
	}
 
}
继承抽象类zuulFilter，有4个方法需要实现：

filterType:过滤器类型，决定了过滤器在哪个周期生效。类型有pre、route、post、error，对应Spring AOP里的前加强、前后加强、后加强、异常处理。

filterOrder:过滤器的执行顺序，多个过滤器同时存在时根据这个order来决定先后顺序，越小优先级越高

shouldFilter:过滤器是否被执行，只有true时才会执行run()里的代码。我们这里除开访问163会放行其他情况都需要进行过滤判断，在生产环境一般是要根据函数条件来判断的。

run:过滤逻辑。

整个代码的zuul的代码逻辑很简单，对外统一的访问路径是zuul服务的地址，如果直接访问163过滤器不生效直接放行，访问其它内部服务结点需要判断是否有accessToken。

由于我内部服务设置了随机sleep时间，测试时发现请求consumer的时候页面会TIMEOUT，检查日志发现service和consumer都正常，再回来看zuul的日志：

Caused by: com.netflix.hystrix.exception.HystrixRuntimeException:demo-feign-freeservice timed-out and no fallback available.

再回头细想Zuul的原理，它包含了Hystrix的部分，怀疑是我被请求的服务处理超时了引起了客户端Zuul的服务降级，但是我又没给Zuul开发fallback代码，所以请求失败报了这个错。

为了验证我的观点给zuul的application.yml添加如下内容：

hystrix:
  command:
    default:
      execution:
        timeout:
          enabled: false
重启Zuul后再试下：搞定



再验证下zuul默认的路由规则：

http://127.0.0.1:9053/demo-feign-freeservice/feign-service/serviceGet?name=yuanyuan&accessToken=123

http://127.0.0.1:9053/api-service/feign-service/serviceGet?name=yuanyuan&accessToken=123

两个是等效的。



路由匹配规则：

/user-service/? 很少用，支持/user-service/后拼接任意一个字符，例如/user-service/a、/user-service/1，不支持/user-service/123

/user-service/* 较少用，支持/user-service/后拼接任意一个字符例如/user-service/abc、/user-service/1，不支持多级目录/user-service/abc/b

/user-service/** 通用，只要以/user-service/开头就可以，后面路径没要求。

如果路由规则有交集的部分，只能使用YAML文件来做application的配置文件，不能使用properties，因为YAML是有序的（流解析），properties无序的（HashMap）。

例如:

zuul:
  routes:
    user-service-ext:
      path: /user-service/ext/**
      serviceId: user-service-ext
    user-service:
      path: /user-service/**
      serviceId: user-service
