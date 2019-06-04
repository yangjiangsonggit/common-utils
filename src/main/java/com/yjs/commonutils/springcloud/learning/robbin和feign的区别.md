# Ribbon 和 Feign 的区别

pring cloud的 Netflix 中提供了两个组件实现软负载均衡调用：ribbon 和 feign 。

## Ribbon
是一个基于 HTTP 和 TCP 客户端 的负载均衡的工具。
它可以 在客户端 配置 RibbonServerList（服务端列表），使用 HttpClient 或 RestTemplate 模拟http请求，步骤相当繁琐。

## Feign
Feign 是在 Ribbon的基础上进行了一次改进，是一个使用起来更加方便的 HTTP 客户端。

采用接口的方式， 只需要创建一个接口，然后在上面添加注解即可 ，将需要调用的其他服务的方法定义成抽象方法即可， 不需要自己构建http请求。

然后就像是调用自身工程的方法调用，而感觉不到是调用远程方法，使得编写 客户端变得非常容易。

类似于 mybatis 的 @Mapper注解 。

注意：spring-cloud-starter-feign 里面已经包含了 spring-cloud-starter-ribbon（Feign 中也使用了 Ribbon）

## 代码区别
### Ribbon 的用法
用 REST_URL_PREFIX 指定请求地址 ， 使用 restTemplate 模拟 http 请求。
下面是一个 部门的例子：

@RestController
public class DeptController_Consumer {
	
	private static final String REST_URL_PREFIX = "http://MICROSERVICECLOUD-DEPT";

	@Autowired
	private RestTemplate restTemplate;

	@RequestMapping(value = "/consumer/dept/add")
	public boolean add(Dept dept) {
		return restTemplate.postForObject(REST_URL_PREFIX + "/dept/add", dept, Boolean.class);
	}
	@RequestMapping(value = "/consumer/dept/get/{id}")
	public Dept get(@PathVariable("id") Long id) {
		return restTemplate.getForObject(REST_URL_PREFIX + "/dept/get/" + id, Dept.class);
	}	
	@RequestMapping(value = "/consumer/dept/list")
	public List<Dept> list() {
		return restTemplate.getForObject(REST_URL_PREFIX + "/dept/list", List.class);
	}
}

其他的注解 和 配置 省略。

### Feign
在 api 工程 定义一个接口API，
a) 添加@FeignClient 注解，指定微服务名称 MICROSERVICECLOUD-DEPT
b) 指定请求地址 @RequestMapping

下面是一个 部门接口的例子：

@FeignClient(value = "MICROSERVICECLOUD-DEPT")
public interface DeptClientService {
	
	@RequestMapping(value = "/dept/get/{id}", method = RequestMethod.GET)
	public Dept get(@PathVariable("id") long id);

	@RequestMapping(value = "/dept/list", method = RequestMethod.GET)
	public List<Dept> list();

	@RequestMapping(value = "/dept/add", method = RequestMethod.POST)
	public boolean add(Dept dept);
}

在 客户端的工程 引入 DeptClientService 接口，调用对应的方法。DeptClientService 类似于 mybatis的@Mappper 注解。

下面是一个 部门模块Controller的伪代码：

@RestController
public class DeptController_Consumer {

	@Autowired
	private DeptClientService service = null;

	@RequestMapping(value = "/consumer/dept/get/{id}")
	public Dept get(@PathVariable("id") Long id) {
		return this.service.get(id);
	}

	@RequestMapping(value = "/consumer/dept/list")
	public List<Dept> list() {
		return this.service.list();
	}

	@RequestMapping(value = "/consumer/dept/add")
	public Object add(Dept dept) {
		return this.service.add(dept);
	}
}

application.yml

server:
  port: 80
  
eureka:
  client:
    register-with-eureka: false
    service-url: 
      defaultZone: http://eurekaHostIP1:7001/eureka/,http://eurekaHostIP2:7002/eureka/,http://eurekaHostIP3:7003/eureka/  
