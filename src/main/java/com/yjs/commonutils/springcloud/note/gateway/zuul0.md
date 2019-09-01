# zuul入门（1）zuul 的概念和原理

一、zuul是什么
zuul 是netflix开源的一个API Gateway 服务器, 本质上是一个web servlet应用。

Zuul 在云平台上提供动态路由，监控，弹性，安全等边缘服务的框架。Zuul 相当于是设备和 Netflix 流应用的 Web 网站后端所有请求的前门。

zuul的例子可以参考 netflix 在github上的 simple webapp，可以按照netflix 在github wiki 上文档说明来进行使用。

二、zuul的工作原理
1、过滤器机制
zuul的核心是一系列的filters, 其作用可以类比Servlet框架的Filter，或者AOP。

zuul把Request route到 用户处理逻辑 的过程中，这些filter参与一些过滤处理，比如Authentication，Load Shedding等。  



 

Zuul提供了一个框架，可以对过滤器进行动态的加载，编译，运行。

Zuul的过滤器之间没有直接的相互通信，他们之间通过一个RequestContext的静态类来进行数据传递的。RequestContext类中有ThreadLocal变量来记录每个Request所需要传递的数据。

Zuul的过滤器是由Groovy写成，这些过滤器文件被放在Zuul Server上的特定目录下面，Zuul会定期轮询这些目录，修改过的过滤器会动态的加载到Zuul Server中以便过滤请求使用。

下面有几种标准的过滤器类型：

Zuul大部分功能都是通过过滤器来实现的。Zuul中定义了四种标准过滤器类型，这些过滤器类型对应于请求的典型生命周期。

(1) PRE：这种过滤器在请求被路由之前调用。我们可利用这种过滤器实现身份验证、在集群中选择请求的微服务、记录调试信息等。

(2) ROUTING：这种过滤器将请求路由到微服务。这种过滤器用于构建发送给微服务的请求，并使用Apache HttpClient或Netfilx Ribbon请求微服务。

(3) POST：这种过滤器在路由到微服务以后执行。这种过滤器可用来为响应添加标准的HTTP Header、收集统计信息和指标、将响应从微服务发送给客户端等。

(4) ERROR：在其他阶段发生错误时执行该过滤器。

内置的特殊过滤器

zuul还提供了一类特殊的过滤器，分别为：StaticResponseFilter和SurgicalDebugFilter

StaticResponseFilter：StaticResponseFilter允许从Zuul本身生成响应，而不是将请求转发到源。

SurgicalDebugFilter：SurgicalDebugFilter允许将特定请求路由到分隔的调试集群或主机。

自定义的过滤器

除了默认的过滤器类型，Zuul还允许我们创建自定义的过滤器类型。

例如，我们可以定制一种STATIC类型的过滤器，直接在Zuul中生成响应，而不将请求转发到后端的微服务。

 

2、过滤器的生命周期
Zuul请求的生命周期如图，该图详细描述了各种类型的过滤器的执行顺序。



 

3、过滤器调度过程


 4、动态加载过滤器


 

三、zuul 能做什么？
Zuul可以通过加载动态过滤机制，从而实现以下各项功能：

验证与安全保障: 识别面向各类资源的验证要求并拒绝那些与要求不符的请求。
审查与监控: 在边缘位置追踪有意义数据及统计结果，从而为我们带来准确的生产状态结论。
动态路由: 以动态方式根据需要将请求路由至不同后端集群处。
压力测试: 逐渐增加指向集群的负载流量，从而计算性能水平。
负载分配: 为每一种负载类型分配对应容量，并弃用超出限定值的请求。
静态响应处理: 在边缘位置直接建立部分响应，从而避免其流入内部集群。
多区域弹性: 跨越AWS区域进行请求路由，旨在实现ELB使用多样化并保证边缘位置与使用者尽可能接近。
除此之外，Netflix公司还利用Zuul的功能通过金丝雀版本实现精确路由与压力测试。

四、zuul 与应用的集成方式
1、ZuulServlet - 处理请求（调度不同阶段的filters，处理异常等） 
ZuulServlet类似SpringMvc的DispatcherServlet，所有的Request都要经过ZuulServlet的处理

三个核心的方法preRoute(),route(), postRoute()，zuul对request处理逻辑都在这三个方法里

ZuulServlet交给ZuulRunner去执行。

由于ZuulServlet是单例，因此ZuulRunner也仅有一个实例。

ZuulRunner直接将执行逻辑交由FilterProcessor处理，FilterProcessor也是单例，其功能就是依据filterType执行filter的处理逻辑

FilterProcessor对filter的处理逻辑。

首先根据Type获取所有输入该Type的filter，List<ZuulFilter> list。
遍历该list，执行每个filter的处理逻辑，processZuulFilter(ZuulFilter filter)
RequestContext对每个filter的执行状况进行记录，应该留意，此处的执行状态主要包括其执行时间、以及执行成功或者失败，如果执行失败则对异常封装后抛出。 
到目前为止，zuul框架对每个filter的执行结果都没有太多的处理，它没有把上一filter的执行结果交由下一个将要执行的filter，仅仅是记录执行状态，如果执行失败抛出异常并终止执行。


2、ContextLifeCycleFilter - RequestContext 的生命周期管理 
ContextLifecycleFilter的核心功能是为了清除RequestContext； 请求上下文RequestContext通过ThreadLocal存储，需要在请求完成后删除该对象。 

RequestContext提供了执行filter Pipeline所需要的Context，因为Servlet是单例多线程，这就要求RequestContext即要线程安全又要Request安全。

context使用ThreadLocal保存，这样每个worker线程都有一个与其绑定的RequestContext，因为worker仅能同时处理一个Request，这就保证了Request Context 即是线程安全的由是Request安全的。



3、GuiceFilter - GOOLE-IOC(Guice是Google开发的一个轻量级，基于Java5（主要运用泛型与注释特性）的依赖注入框架(IOC)。Guice非常小而且快。) 


4、StartServer - 初始化 zuul 各个组件 （ioc、插件、filters、数据库等）


5、FilterScriptManagerServlet -  uploading/downloading/managing scripts， 实现热部署
Filter源码文件放在zuul 服务特定的目录， zuul server会定期扫描目录下的文件的变化，动态的读取\编译\运行这些filter,

如果有Filter文件更新，源文件会被动态的读取，编译加载进入服务，接下来的Request处理就由这些新加入的filter处理。



