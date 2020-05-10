FAQ
此处会列出关于 Sentinel 的常见问题以及相应的注意事项和解决方案。

配置相关
Q: 配置参数如何指定？

A: 可通过 JVM -D 参数指定。除 project.name 之外，其余参数还可通过 properties 文件指定，路径为 ${home}/logs/csp/${project.name}.properties。详见 启动项配置文档。

Q: 可否自定义 properties 文件的位置或日志文件的位置？

A: 1.3.0 版本开始支持配置日志文件的目录，可以参考 启动项配置文档；若希望自定义 properties 文件的位置或使用其它的加载方式，可以结合 Spring Cloud Alibaba Sentinel 使用。

Sentinel 核心功能相关
Q: Sentinel 可否用于生产环境？

A: 当然可以！Sentinel 可用于生产环境，但若要在生产环境中使用 Sentinel 控制台则还需要进行一些改造。

Q: 想从 Hystrix 迁移到 Sentinel，有没有相关指南？
A: 可以参考 Guideline: 从 Hystrix 迁移到 Sentinel。

Q: 我的服务是基于 Spring Boot 和 Dubbo 编写的，Sentinel 有相应的适配吗？

A: 当然啦，Sentinel 针对常用框架和库进行了适配，包括 Web Servlet、Dubbo、Spring Boot / Spring Cloud、gRPC 等，可以参见 主流框架的适配。

Q: Sentinel 的性能如何？只进行埋点不配置规则，仅统计指标信息会有多少性能损耗？

A: 引入 Sentinel 带来的性能损耗非常小。只有在业务单机量级超过 25W QPS 的时候才会有一些显著的影响（10% 左右），单机 QPS 不太大的时候损耗几乎可以忽略不计。性能测试报告见 Benchmark。

Q: 是否需要在初始化时手工调用 InitExecutor.doInit() 方法进行相应的初始化？

A: 不需要，sentinel-core 的 Env 类通过 static 块调用了此方法进行初始化，在首次调用资源时即可触发。一些 demo 中手动调用此方法是为了不让进程退出。

Q: 降级规则生效后，除了抛异常之外还有没有其它的处理方式？

A: 可以利用 Sentinel 注解 来配置 fallback 函数，在资源被降级时进行相应处理。另外，对于 Dubbo 服务，我们支持注册全局的 fallback 函数，可参考 Dubbo 适配文档。

Q: 怎么针对特定调用端限流？比如我想针对某个 IP 或者来源应用进行限流？规则里面 limitApp（流控应用）的作用？
A: Sentinel 支持按来源限流，可以参考 基于调用关系的限流。注意 origin 数量不能太多，否则会导致内存暴涨，并且目前不支持模式匹配。

若 Web 应用使用了 Sentinel Web Filter，并希望对 HTTP 请求按照来源限流，则可以自己实现 RequestOriginParser 接口从 HTTP 请求中解析 origin 并注册至 WebCallbackManager 中。示例代码：

WebCallbackManager.setRequestOriginParser(ServletRequest::getRemoteAddr);
特别地，流控应用如果使用了 Sentinel Dubbo Adapter，同时 Dubbo 的消费者也引入了 Sentinel Dubbo Adapter（用于透传 dubboApplication 这个参数），则填 Dubbo 调用方的 application name 就好（注意是 Dubbo 里配置的应用名而不是 Sentinel 的应用名），消费端引入 adapter 就会自动透传，否则需要自己传来源应用名。

注意来源信息（origin）一般是在入口处传入的（如 HTTP 入口或 Dubbo 服务入口），因此在链路中间再通过 ContextUtil.enter(xxx, origin) 传入可能不会生效。

Q: 很多开发通过错误码来处理流程，而非通过异常。这种写法，导致 Sentinel 不能拦截到异常，无法触发降级。对于这种情况，有没有什么好的处理方法？

A: 实际上 Sentinel 是通过 Tracer.trace(e) 来统计业务异常的，因此可以收到错误码就调用此函数来统计业务异常。

Q: 手动通过 SphU.entry(xxx) 进行埋点具有侵入性，有没有低侵入性的方案？

A: 可以利用 Sentinel 注解支持 或者 各种框架的适配。

Q: Sentinel 未来会考虑支持分布式链路调用跟踪吗？

A: Sentinel 的核心功能是流量控制和限流降级。分布式链路调用跟踪不是 Sentinel 关注的功能，可以参考其它分布式链路调用跟踪系统。

Q: Sentinel 支持集群限流吗？

A: 1.4.0 版本开始已经支持，可以参考 集群流控文档。

Q: Web 端的资源目前都是根据某个特定的 URL 限流，可不可以根据前缀匹配限流？

A: 对于 Sentinel Web Servlet Filter，可以借助 UrlCleaner 处理对应的 URL（如提取前缀的操作），这样对应的资源都会归到处理后的资源（如 /foo/1 和 /foo/2 都归到 /foo/* 资源里面）。UrlCleaner 实现 URL 前缀匹配只是个 trick，它会把对应的资源也给归一掉，直接在资源名粒度上实现模式匹配还是有很多顾虑的问题的。

Sentinel 集群限流相关
Q: 集群限流没有生效？
A: 请参考以下步骤排查：

请确保接入端引入了 Sentinel 集群流控相关的依赖；
检查 ~/logs/csp/sentinel-record.log 查看 Token Server / Client 是否加载成功，是否成功启动；客户端规则和集群规则是否都接收成功；
在 Token Client 端检查 ~/logs/csp/sentinel-cluster-client.log 日志，看一下是否是 Token Server 未启动或者规则未接收到的问题；
若从开源 Sentinel 控制台推送集群规则，请务必确保按照 Sentinel 集群流控控制台文档 进行了相应改造，并且接入端按照 集群规则配置文档 配置了相关动态规则源；
Q: 通过 Sentinel 控制台或 API 来分配管理 Token Server 时，返回错误：token client/server mode not available: no SPI found

A: 请确保接入端引入了 Sentinel 集群流控相关的依赖（嵌入模式两者都需要）：

Token Client: sentinel-cluster-client-default
Token Server: sentinel-cluster-server-default
更多信息可参见 集群流控文档。

Q: 集群流控规则中“单机均摊”阈值模式是什么意思？

A: 单机均摊模式下配置的阈值等同于单机能够承受的平均限额。Token Server 会根据客户端对应的 namespace（默认为 project.name 定义的应用名）下的连接数来计算总的阈值（比如独立模式下有 3 个 client 连接到了 token server，然后配的单机均摊阈值为 10，则计算出的集群总量就为 30）。单机均摊阈值仅用于计算总体阈值，不是说每台机器一定要控制在均摊阈值上。配置方式：若希望某个资源限制集群总量为 Q，服务实例为 N，则可以配置单机均摊阈值为 Q / N。

Q: 集群流控 Token Server 中的 namespace set 有什么用处？

A: namespace set 即 Token Server 服务的作用域（命名空间集合），用于指定该 Token Server 可以为哪些应用提供集群流控服务，一般设置为接入端应用名的集合。Token Client 在连接到 token server 后会上报自己的命名空间（默认为 project.name 配置的应用名），token server 会根据上报的命名空间名称统计连接数。

嵌入模式下一般仅服务自身，默认嵌入模式的 namespace set 为该应用本身
所有 Token Server 都自带一个默认的命名空间 default，一般情况下不会用到
Sentinel Transport/Dashboard 相关
Q：Sentinel Transport 同一台机器起相同的端口不报错？如果几个应用都没配 csp.sentinel.api.port 会出错吗？

A: 对于 sentinel-transport-simple-http，默认 8719 端口被占用以后会自动选择下一个端口，直到有可用的端口为止。选择端口时间会比较长，本地如果起多个应用建议自己通过 csp.sentinel.api.port 设置端口。

Q: 为什么 Sentinel Transport 模块里自己用原生 Socket 或 Netty 自己实现的 HTTP Server，为什么不用 Web 容器（如 Tomcat）？

A: 使用 Web 容器作为一个组件来说太过于重量级了，因此我们自己实现了简单的 sentinel-transport-simple-http 和 sentinel-transport-netty-http 模块，两个模块的功能是相同的，一般用 sentinel-transport-simple-http 即可，而 sentinel-transport-netty-http 是为了后续的可扩展性和兼容性而设计的。

Q: 目前 Sentinel Transport 里面可否自己扩展添加 command？

A: 可以的，用户可以自行实现 CommandHandler 接口，并在实现类上加上 @CommandMapping 注解（代表 command name）。接着在资源目录下的 META-INF/services 目录下添加 com.alibaba.csp.sentinel.command.CommandHandler 文件（如果没有的话），在文件里加上自己的实现类的全名即可。比如：

package io.test;

@CommandMapping(name = "test")
public class TestCommandHandler implements CommandHandler<String> {

    @Override
    public CommandResponse<String> handle(CommandRequest request) {
        return CommandResponse.ofSuccess("666");
    }
}
在自己项目目录下的 resources/META-INF/services/com.alibaba.csp.sentinel.command.CommandHandler 文件中添加：

io.test.TestCommandHandler
这样初始化 Sentinel Transport Server 的时候，对应的 command 就会自动注册，可以通过 curl host:ip/test 来验证是否成功。

Q: 想要在生产环境中使用 Sentinel 控制台，还需要进行哪些改造？
A: 在生产环境中使用 Sentinel 控制台需要考虑下面的问题：

监控数据持久化
规则推送支持应用维度，并且整合配置中心以便可以直接推送至远程配置中心
权限控制
详细指南请参考此 blog: 在生产环境中使用 Sentinel 控制台。

Q: 已经引入了适配模块（比如 sentinel-dubbo-adapter），然而在 Sentinel 控制台上没有找到我的应用？
A: 请确保对应的依赖和参数配置正确。接入控制台需要上报的客户端依赖，如 sentinel-transport-simple-http，不引入相关依赖则无法将相关信息上报到控制台。参数配置详情请参考 Sentinel 控制台文档。

详细的排查步骤请见下面。

Q: Sentinel 控制台没有显示我的应用，或者没有监控展示，如何排查？
A: Sentinel Dashboard 是一个单独启动的控制台，应用若想上报监控信息给 Sentinel 控制台，需要引入 Sentinel 上报信息的客户端。它们各自有自己的通信端口，其中控制台的端口可通过启动参数 -Dserver.port=xxxx 进行配置，而 Sentinel 客户端的端口可以通过启动参数 -Dcsp.sentinel.api.port 进行配置（默认是 8719）。两者都启动之后，Sentinel 客户端在首次访问资源时会初始化并给控制台发送心跳，之后控制台会通过客户端提供的端口对 Sentinel 客户端进行访问来拉取相关信息。基于此，接入控制台需要的步骤如下：

接入 Sentinel 的应用应该引入 Sentinel 客户端通信的基础 jar 包，如 sentinel-transport-simple-http
客户端启动时添加相应的 JVM 参数，包括：
应用名称：-Dproject.name=xxxx
控制台地址：-Dcsp.sentinel.dashboard.server=ip:port
本地的 Sentinel 客户端端口：-Dcsp.sentinel.api.port=xxxx（默认是 8719）
参数配置详情请参考 Sentinel 控制台文档。
启动控制台，运行应用，当首次访问对应的资源后 等待一段时间即可在控制台上看到对应的应用以及相应的监控信息。可以通过 curl http://ip:port/tree 命令查看调用链，正常情况下会显示出已访问资源的调用链。
注意：Sentinel 会在客户端首次调用时候进行初始化，开始向控制台发送心跳包。因此需要确保客户端有访问量，才能在控制台上看到监控数据。另外，还是期待大家养成看日志的好习惯，详见 日志文档。

控制台推送规则的日志默认位于 ${user.home}/logs/csp/sentinel-dashboard.log
客户端接收规则日志默认位于 ${user.home}/logs/csp/sentinel-record.log.xxx
常用排查问题列表：

确认 Dashboard 已经正常启动并可以正常访问
若是 Spring Boot / Dubbo 等应用，请务必检查是否引入了整合依赖并进行了相应配置
检查接入端的启动参数配置是否正确（如控制台地址是否配置正确）
通过 ~/logs/csp/sentinel-record.log 日志排查客户端发送心跳包是否正常，是否正常上报给 Dashboard
确保 fastjson 的版本和 Sentinel 的依赖版本保持一致
通过 curl IP:port/getRules?type=flow 等命令查看结果，查看规则是否推送成功
发送到客户端的规则格式是否正确，例如确认一下降级规则的表单是否填写完整
Q: 客户端和控制台不在一台机器上，客户端成功接入控制台后，控制台无法显示实时的监控数据？但簇点链路页面有实时请求数据（不为 0）？
A: 请确保 Sentinel 控制台所在的机器时间与自己应用的机器时间保持一致（通过 NTP 等同步）。Sentinel 控制台是通过控制台所在机器的时间戳拉取监控数据的，因此时间不一致会导致拉不到实时的监控数据。

Q: 客户端成功接入控制台后，控制台配置规则准确无误，但是客户端收不到规则或者报错？比如配置的规则 resourceName 正确但却报 resourceName 为空的错误？

A: 排查客户端是否使用了低版本的 fastjson，低版本的 fastjson 可能会有此问题，建议使用和 Sentinel 相关组件一致版本的 fastjson。

Q: Sentinel 监控数据能保留多久？控制台聚合的统计数据可否进行持久化？

A: Sentinel 对监控数据的做法是定时落盘在客户端，然后 Sentinel 提供接口去拉取日志文件。所以 Sentinel 在监控数据上理论上是最少存储 1 天以上的数据；然而作为控制台展示，则仅在内存中聚合 5 分钟以内的统计数据，不进行持久化。

我们鼓励大家对 Dashboard 进行改造实现指标信息的持久化，并从其它的存储中（如 RDBMS、时序数据库等）拉取的监控信息，包括实时的和历史的数据。

Q: Sentinel 控制台有登录或者细粒度的权限控制功能吗？

A: 目前是没有的，权限控制功能需要自己去定制。

Q: Sentinel Dashboard 中簇点链路页面里面资源名称为什么会有重复的？

A: 最顶层的是 Context 名，用于区分不同调用链路（入口）。

Q: 应用已经退出了，Sentinel 控制台的应用列表里还有显示？

A: 若应用已退出，过 5 分钟没有收到心跳，控制台就会标记对应机器为失联状态，但不会将对应应用从列表中移除。

Q: 为什么说 Sentinel 控制台集群资源汇总仅支持 500 台以下的应用集群？

A: 因为 Sentinel 控制台仅作为示范，其监控聚合能力非常有限，但是应用端是没有限制的。若希望支持更多的机器，可以使用动态规则数据源并改造控制台；同时改造实现监控数据持久化。

Q: Sentinel 控制台规则配置里面“流控应用”是什么？

A: 对应规则中的 limitApp，即请求来源，通过入口处 ContextUtil.enter(contextName, origin) 中的 origin 传入。参见上面的“按调用方进行限流”。

规则存储与动态规则数据源（DataSource）
Q: 动态规则数据源分为哪几种？各自的使用场景？

A: 数据源分为只读的数据源和可写入的数据源。

可写入的数据源一般对应 pull 模式的数据源（如本地文件、RDBMS），从控制台推送规则时可先经 Sentinel 客户端更新到内存中，然后经注册的 WritableDataSource 写入到本地。
只读的数据源一般对应 push 模式的数据源（如配置中心等）。对于配置中心，写入的操作不应由数据源进行，数据源仅负责获取配置中心推送的配置并更新到本地。从控制台推送规则的次序应该是 配置中心控制台/Sentinel 控制台 → 配置中心 → Sentinel 数据源 → Sentinel，而不是经 Sentinel 数据源推送至配置中心。注意由于不同的生产环境可能使用不同的数据源，从 Sentinel 控制台推送至配置中心的实现需要用户自行改造。
Q: 在控制台配置的规则存在哪里？为什么应用重启规则就消失了？
A: 在控制台配置的规则是存在内存里面的，没有进行持久化，因此应用重启后规则就消失了。

可以参考 动态规则文档 持久化存储规则。

Q: 我在客户端处配置了动态规则数据源（如基于 ZooKeeper / Nacos / Apollo 的数据源），然后在控制台处向客户端推送了规则，但是规则并没有写入到对应的数据源中？

A: push 模式的数据源（如配置中心）都是只读的。对于配置中心类型的数据源（如 ZooKeeper），我们推荐在推送规则时直接推送至配置中心，然后配置中心再自动推送至所有的客户端（即 Dashboard -> Config Center -> Sentinel DataSource -> Sentinel），目前需要自行改造控制台。可以参见：在生产环境中使用 Sentinel 控制台。

对于 pull 模式的数据源（如本地文件），则可以向 transport-common 模块的 WritableDataSourceRegistry 注册写入数据源，在推送规则时会一并推送至本地数据源中。

其它
Q: 编译打包的时候 Maven 报错？

A: 请将 Maven 版本升级至较新版本（3.5.x）再进行构建。