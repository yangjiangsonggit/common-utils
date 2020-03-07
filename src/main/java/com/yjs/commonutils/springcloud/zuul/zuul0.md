路由器和过滤器：Zuul
90%高可用的千亿级微服务架构之道深入学习一线大厂必备微服务架构技术。VIP 教程限时免费领取。 ⇐ 立即查看
路由在微服务体系结构的一个组成部分。例如，/可以映射到您的Web应用程序，/api/users映射到用户服务，并将/api/shop映射到商店服务。Zuul是Netflix的基于JVM的路由器和服务器端负载均衡器。

Netflix使用Zuul进行以下操作：

认证

洞察

压力测试

金丝雀测试

动态路由

服务迁移

负载脱落

安全

静态响应处理

主动/主动流量管理

Zuul的规则引擎允许基本上写任何JVM语言编写规则和过滤器，内置Java和Groovy。

注意
配置属性zuul.max.host.connections已被两个新属性zuul.host.maxTotalConnections和zuul.host.maxPerRouteConnections替换，分别默认为200和20。
注意
所有路由的默认Hystrix隔离模式（ExecutionIsolationStrategy）为SEMAPHORE。如果此隔离模式是首选，则zuul.ribbonIsolationStrategy可以更改为THREAD。
如何加入Zuul
要在您的项目中包含Zuul，请使用组org.springframework.cloud和artifact id spring-cloud-starter-zuul的启动器。有关 使用当前的Spring Cloud发布列表设置构建系统的详细信息，请参阅Spring Cloud项目页面。

嵌入式Zuul反向代理
Spring Cloud已经创建了一个嵌入式Zuul代理，以简化UI应用程序想要代理对一个或多个后端服务的呼叫的非常常见的用例的开发。此功能对于用户界面对其所需的后端服务进行代理是有用的，避免了对所有后端独立管理CORS和验证问题的需求。

要启用它，使用@EnableZuulProxy注释Spring Boot主类，并将本地调用转发到相应的服务。按照惯例，具有ID“用户”的服务将接收来自位于/users（具有前缀stripped）的代理的请求。代理使用Ribbon来定位一个通过发现转发的实例，并且所有请求都以 hystrix命令执行，所以故障将显示在Hystrix指标中，一旦电路打开，代理将不会尝试联系服务。

注意
Zuul启动器不包括发现客户端，因此对于基于服务ID的路由，您还需要在类路径中提供其中一个路由（例如Eureka）。
要跳过自动添加的服务，请将zuul.ignored-services设置为服务标识模式列表。如果一个服务匹配一个被忽略的模式，而且包含在明确配置的路由映射中，那么它将被无符号。例：

application.yml
 zuul:
  ignoredServices: '*'
  routes:
    users: /myusers/**
在此示例中，除 “用户” 之外，所有服务都被忽略。

要扩充或更改代理路由，可以添加如下所示的外部配置：

application.yml
 zuul:
  routes:
    users: /myusers/**
这意味着对“/ myusers”的http呼叫转发到“用户”服务（例如“/ myusers / 101”转发到“/ 101”）。

要获得对路由的更细粒度的控制，您可以独立地指定路径和serviceId：

application.yml
 zuul:
  routes:
    users:
      path: /myusers/**
      serviceId: users_service
这意味着对“/ myusers”的http呼叫转发到“users_service”服务。路由必须有一个“路径”，可以指定为蚂蚁样式模式，所以“/ myusers / *”只匹配一个级别，但“/ myusers / **”分层匹配。

后端的位置可以被指定为“serviceId”（用于发现的服务）或“url”（对于物理位置），例如

application.yml
 zuul:
  routes:
    users:
      path: /myusers/**
      url: http://example.com/users_service
这些简单的URL路由不会被执行为HystrixCommand，也不能使用Ribbon对多个URL进行负载平衡。为此，请指定service-route并为serviceId配置Ribbon客户端（目前需要在Ribbon中禁用Eureka支持：详见上文），例如

application.yml
zuul:
  routes:
    users:
      path: /myusers/**
      serviceId: users

ribbon:
  eureka:
    enabled: false

users:
  ribbon:
    listOfServers: example.com,google.com
您可以使用regexmapper在serviceId和路由之间提供约定。它使用名为group的正则表达式从serviceId中提取变量并将它们注入到路由模式中。

ApplicationConfiguration.java
@Bean
public PatternServiceRouteMapper serviceRouteMapper() {
    return new PatternServiceRouteMapper(
        "(?<name>^.+)-(?<version>v.+$)",
        "${version}/${name}");
}
这意味着serviceId“myusers-v1”将被映射到路由“/ v1 / myusers / **”。任何正则表达式都被接受，但所有命名组都必须存在于servicePattern和routePattern中。如果servicePattern与serviceId不匹配，则使用默认行为。在上面的示例中，serviceId“myusers”将被映射到路由“/ myusers / **”（检测不到版本）此功能默认禁用，仅适用于已发现的服务。

要为所有映射添加前缀，请将zuul.prefix设置为一个值，例如/api。默认情况下，请求被转发之前，代理前缀被删除（使用zuul.stripPrefix=false关闭此行为）。您还可以关闭从各路线剥离服务特定的前缀，例如

application.yml
 zuul:
  routes:
    users:
      path: /myusers/**
      stripPrefix: false
注意
zuul.stripPrefix仅适用于zuul.prefix中设置的前缀。它对给定路由path中定义的前缀有影响。
在本示例中，对“/ myusers / 101”的请求将转发到“/ myusers / 101”上的“users”服务。

zuul.routes条目实际上绑定到类型为ZuulProperties的对象。如果您查看该对象的属性，您将看到它还具有“可重试”标志。将该标志设置为“true”使Ribbon客户端自动重试失败的请求（如果需要，可以使用Ribbon客户端配置修改重试操作的参数）。

默认情况下，将X-Forwarded-Host标头添加到转发的请求中。关闭set zuul.addProxyHeaders = false。默认情况下，前缀路径被删除，对后端的请求会拾取一个标题“X-Forwarded-Prefix”（上述示例中的“/ myusers”）。

如果您设置默认路由（“/”），则@EnableZuulProxy的应用程序可以作为独立服务器，例如zuul.route.home: /将路由所有流量（即“/ **”）到“home”服务。

如果需要更细粒度的忽略，可以指定要忽略的特定模式。在路由位置处理开始时评估这些模式，这意味着前缀应包含在模式中以保证匹配。忽略的模式跨越所有服务，并取代任何其他路由规范。

application.yml
 zuul:
  ignoredPatterns: /**/admin/**
  routes:
    users: /myusers/**
这意味着诸如“/ myusers / 101”的所有呼叫将被转发到“用户”服务上的“/ 101”。但是包含“/ admin /”的呼叫将无法解决。

警告
如果您需要您的路由保留订单，则需要使用YAML文件，因为使用属性文件将会丢失订购。例如：
application.yml
 zuul:
  routes:
    users:
      path: /myusers/**
    legacy:
      path: /**
如果要使用属性文件，则legacy路径可能会在users路径前面展开，从而使users路径不可达。

Zuul Http客户端
zuul使用的默认HTTP客户端现在由Apache HTTP Client支持，而不是不推荐使用的Ribbon RestClient。要分别使用RestClient或使用okhttp3.OkHttpClient集合ribbon.restclient.enabled=true或ribbon.okhttp.enabled=true。

Cookie和敏感标题
在同一个系统中的服务之间共享标题是可行的，但是您可能不希望敏感标头泄漏到外部服务器的下游。您可以在路由配置中指定被忽略头文件列表。Cookies起着特殊的作用，因为它们在浏览器中具有明确的语义，并且它们总是被视为敏感的。如果代理的消费者是浏览器，则下游服务的cookie也会导致用户出现问题，因为它们都被混淆（所有下游服务看起来都是来自同一个地方）。

如果您对服务的设计非常谨慎，例如，如果只有一个下游服务设置了Cookie，那么您可能可以让他们从后台一直到调用者。另外，如果您的代理设置cookie和所有后台服务都是同一系统的一部分，那么简单地共享它们就可以自然（例如使用Spring Session将它们链接到一些共享状态）。除此之外，由下游服务设置的任何Cookie可能对呼叫者来说都不是很有用，因此建议您将（至少）“Set-Cookie”和“Cookie”设置为不属于您的域名。即使是属于您域名的路线，请尝试仔细考虑允许Cookie在代理之间流动的含义。

灵敏头可以配置为每个路由的逗号分隔列表，例如

application.yml
 zuul:
  routes:
    users:
      path: /myusers/**
      sensitiveHeaders: Cookie,Set-Cookie,Authorization
      url: https://downstream
注意
这是sensitiveHeaders的默认值，因此您不需要设置它，除非您希望它不同。注意这是Spring Cloud Netflix 1.1中的新功能（1.0中，用户无法控制标题，所有Cookie都在两个方向上流动）。
sensitiveHeaders是一个黑名单，默认值不为空，所以要使Zuul发送所有标题（“被忽略”除外），您必须将其显式设置为空列表。如果您要将Cookie或授权标头传递到后端，这是必要的。例：

application.yml
 zuul:
  routes:
    users:
      path: /myusers/**
      sensitiveHeaders:
      url: https://downstream
也可以通过设置zuul.sensitiveHeaders来全局设置敏感标题。如果在路由上设置sensitiveHeaders，则将覆盖全局sensitiveHeaders设置。

被忽略的标题
除了每个路由的敏感标头，您还可以为与下游服务交互期间应该丢弃的值（请求和响应）设置全局值为zuul.ignoredHeaders。默认情况下，如果Spring安全性不在类路径上，则它们是空的，否则它们被初始化为由Spring Security指定的一组众所周知的“安全性”头（例如涉及缓存）。在这种情况下的假设是下游服务可能也添加这些头，我们希望代理的值。为了不丢弃这些众所周知的安全标头，只要Spring安全性在类路径上，您可以将zuul.ignoreSecurityHeaders设置为false。如果您禁用Spring安全性中的HTTP安全性响应头，并希望由下游服务提供的值，这可能很有用

路线端点
如果您在Spring Boot执行器中使用@EnableZuulProxy，您将启用（默认情况下）另一个端点，通过HTTP可用/routes。到此端点的GET将返回映射路由的列表。POST将强制刷新现有路由（例如，如果服务目录中有更改）。您可以通过将endpoints.routes.enabled设置为false来禁用此端点。

注意
路由应自动响应服务目录中的更改，但POST到/路由是强制更改立即发生的一种方式。
扼杀模式和本地前进
迁移现有应用程序或API时的常见模式是“扼杀”旧端点，用不同的实现慢慢替换它们。Zuul代理是一个有用的工具，因为您可以使用它来处理来自旧端点的客户端的所有流量，但将一些请求重定向到新端点。

示例配置：

application.yml
 zuul:
  routes:
    first:
      path: /first/**
      url: http://first.example.com
    second:
      path: /second/**
      url: forward:/second
    third:
      path: /third/**
      url: forward:/3rd
    legacy:
      path: /**
      url: http://legacy.example.com
在这个例子中，我们扼杀了“遗留”应用程序，该应用程序映射到所有与其他模式不匹配的请求。/first/**中的路径已被提取到具有外部URL的新服务中。并且/second/**中的路径被转发，以便它们可以在本地处理，例如具有正常的Spring @RequestMapping。/third/**中的路径也被转发，但具有不同的前缀（即/third/foo转发到/3rd/foo）。

注意
被忽略的模式并不完全被忽略，它们只是不被代理处理（因此它们也被有效地转发到本地）。
通过Zuul上传文件
如果您@EnableZuulProxy您可以使用代理路径上传文件，只要文件很小，它就应该工作。对于大文件，有一个替代路径绕过“/ zuul / *”中的Spring DispatcherServlet（以避免多部分处理）。也就是说，如果zuul.routes.customers=/customers/**则可以将大文件发送到“/ zuul / customers / *”。servlet路径通过zuul.servletPath进行外部化。如果代理路由引导您通过Ribbon负载均衡器，例如，超大文件也将需要提升超时设置

application.yml
hystrix.command.default.execution.isolation.thread.timeoutInMilliseconds: 60000
ribbon:
  ConnectTimeout: 3000
  ReadTimeout: 60000
请注意，要使用大型文件进行流式传输，您需要在请求中使用分块编码（某些浏览器默认情况下不会执行）。例如在命令行：

$ curl -v -H "Transfer-Encoding: chunked" \
    -F "file=@mylarge.iso" localhost:9999/zuul/simple/file
查询字符串编码
处理传入的请求时，查询参数被解码，因此可以在Zuul过滤器中进行修改。然后在路由过滤器中构建后端请求时重新编码它们。如果使用Javascript的encodeURIComponent()方法编码，结果可能与原始输入不同。虽然这在大多数情况下不会出现任何问题，但一些Web服务器可以用复杂查询字符串的编码来挑选。

要强制查询字符串的原始编码，可以将特殊标志传递给ZuulProperties，以便查询字符串与HttpServletRequest::getQueryString方法相同：

application.yml
 zuul:
  forceOriginalQueryStringEncoding: true
注意：此特殊标志仅适用于SimpleHostRoutingFilter，您可以使用RequestContext.getCurrentContext().setRequestQueryParams(someOverriddenParameters)轻松覆盖查询参数，因为查询字符串现在直接在原始的HttpServletRequest上获取。

普通嵌入Zuul
如果您使用@EnableZuulServer（而不是@EnableZuulProxy），您也可以运行不带代理的Zuul服务器，或者有选择地切换代理平台的部分。您添加到ZuulFilter类型的应用程序的任何bean都将自动安装，与@EnableZuulProxy一样，但不会自动添加任何代理过滤器。

在这种情况下，仍然通过配置“zuul.routes。*”来指定进入Zuul服务器的路由，但没有服务发现和代理，所以“serviceId”和“url”设置将被忽略。例如：

application.yml
 zuul:
  routes:
    api: /api/**
将“/ api / **”中的所有路径映射到Zuul过滤器链。

禁用Zuul过滤器
Spring Cloud的Zuul在代理和服务器模式下默认启用了多个ZuulFilter bean。有关启用的可能过滤器，请参阅zuul过滤器包。如果要禁用它，只需设置zuul.<SimpleClassName>.<filterType>.disable=true。按照惯例，filters之后的包是Zuul过滤器类型。例如，禁用org.springframework.cloud.netflix.zuul.filters.post.SendResponseFilter设置zuul.SendResponseFilter.post.disable=true。

为路线提供Hystrix回退
当Zuul中给定路由的电路跳闸时，您可以通过创建类型为ZuulFallbackProvider的bean来提供回退响应。在这个bean中，您需要指定回退的路由ID，并提供返回的ClientHttpResponse作为后备。这是一个非常简单的ZuulFallbackProvider实现。

class MyFallbackProvider implements ZuulFallbackProvider {
    @Override
    public String getRoute() {
        return "customers";
    }

    @Override
    public ClientHttpResponse fallbackResponse() {
        return new ClientHttpResponse() {
            @Override
            public HttpStatus getStatusCode() throws IOException {
                return HttpStatus.OK;
            }

            @Override
            public int getRawStatusCode() throws IOException {
                return 200;
            }

            @Override
            public String getStatusText() throws IOException {
                return "OK";
            }

            @Override
            public void close() {

            }

            @Override
            public InputStream getBody() throws IOException {
                return new ByteArrayInputStream("fallback".getBytes());
            }

            @Override
            public HttpHeaders getHeaders() {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                return headers;
            }
        };
    }
}
这里是路由配置的样子。