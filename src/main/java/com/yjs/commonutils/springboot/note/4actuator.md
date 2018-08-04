### 45. 开启production-ready特性

    [spring-boot-actuator](http://github.com/spring-projects/spring-boot/tree/master/spring-boot-actuator)模块提供
    Spring Boot所有的production-ready特性，启用该特性的最简单方式是添加`spring-boot-starter-actuator` ‘Starter’依赖。
    
    **执行器（Actuator）的定义**：执行器是一个制造业术语，指的是用于移动或控制东西的一个机械装置，一个很小的改变就能让执行器产生大量的运动。
    
    按以下配置为Maven项目添加执行器：
    ```xml
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
    </dependencies>
    ```
    对于Gradle，使用下面的声明：
    ```java
    dependencies {
        compile("org.springframework.boot:spring-boot-starter-actuator")
    }
    ```


### 46. 端点

    执行器端点（endpoints）可用于监控应用及与应用进行交互，Spring Boot包含很多内置的端点，你也可以添加自己的。例如，`health`端点提
    供了应用的基本健康信息。
    端点暴露的方式取决于你采用的技术类型，大部分应用选择HTTP监控，端点的ID映射到一个URL。例如，`health`端点默认映射到`/health`。
    
    下面的端点都是可用的：
    
    | ID | 描述　|是否敏感|
    | ---- | :----- | :----- |
    |`actuator`|为其他端点提供基于超文本的导航页面，需要添加Spring HATEOAS依赖|true|
    |`autoconfig`|显示一个自动配置类的报告，该报告展示所有自动配置候选者及它们被应用或未被应用的原因|true|
    |`beans`|显示一个应用中所有Spring Beans的完整列表|true|
    |`configprops`|显示一个所有`@ConfigurationProperties`的集合列表|true|
    |`dump`|执行一个线程转储|true|
    |`env`|暴露来自Spring `ConfigurableEnvironment`的属性|true|
    |`flyway`|显示数据库迁移路径，如果有的话|true|
    |`health`|展示应用的健康信息（当使用一个未认证连接访问时显示一个简单的'status'，使用认证连接访问则显示全部信息详情）|false|
    |`info`|显示任意的应用信息|false|
    |`liquibase`|展示任何Liquibase数据库迁移路径，如果有的话|true|
    |`metrics`|展示当前应用的'metrics'信息|true|
    |`mappings`|显示一个所有`@RequestMapping`路径的集合列表|true|
    |`shutdown`|允许应用以优雅的方式关闭（默认情况下不启用）|true|
    |`trace`|显示trace信息（默认为最新的100条HTTP请求）|true|
    
    如果使用Spring MVC，你还可以使用以下端点：
    
    | ID | 描述　|是否敏感|
    | ---- | :----- | :----- |
    |`docs`|展示Actuator的文档，包括示例请求和响应，需添加`spring-boot-actuator-docs`依赖|false|
    |`heapdump`|返回一个GZip压缩的`hprof`堆转储文件|true|
    |`jolokia`|通过HTTP暴露JMX beans（依赖Jolokia）|true|
    |`logfile`|返回日志文件内容（如果设置`logging.file`或`logging.path`属性），支持使用HTTP `Range`头接收日志文件内容的部分信息||
    
    **注**：根据端点暴露的方式，`sensitive`属性可用做安全提示，例如，在使用HTTP访问敏感（sensitive）端点时需要提供用户名/密码
    （如果没有启用web安全，可能会简化为禁止访问该端点）。



### 46.1 自定义端点

    使用Spring属性可以自定义端点，你可以设置端点是否开启（`enabled`），是否敏感（`sensitive`），甚至改变它的`id`。例如，下面的
    `application.properties`改变`beans`端点的敏感性及id，并启用`shutdown`：
    ```java
    endpoints.beans.id=springbeans
    endpoints.beans.sensitive=false
    endpoints.shutdown.enabled=true
    ```
    **注**：前缀`endpoints + . + name`用于被配置端点的唯一标识。
    
    默认情况，所有端点除了`shutdown`以外都是开启的，你可以使用`endpoints.enabled`属性指定可选端点是否启用。例如，所有端点除`info`外
    都被禁用：
    ```java
    endpoints.enabled=false
    endpoints.info.enabled=true
    ```
    同样地，你可以全局范围内设置所有端点的`sensitive`标记，敏感标记默认取决于端点类型（查看上面表格）。例如，所有端点除`info`外都标记为敏感：
    ```java
    endpoints.sensitive=true
    endpoints.info.sensitive=false
    ```


###46.2 执行器MVC端点的超媒体支持

    如果classpath下存在[Spring HATEOAS](http://projects.spring.io/spring-hateoas)库（比如，通过`spring-boot-starter-hateoas`
    或使用[Spring Data REST](http://projects.spring.io/spring-data-rest)），来自执行器（Actuator）的HTTP端点将使用超媒体链接
    进行增强（hypermedia links），也就是使用一个“导航页”汇总所有端点链接，该页面默认路径为`/actuator`。该实现也是一个端点，可以通过
    属性配置它的路径（`endpoints.actuator.path`）及是否开启（`endpoints.actuator.enabled`）。
    
    当指定了一个自定义管理上下文路径时，“导航页”路径自动从`/actuator`迁移到管理上下文根目录。例如，如果管理上下文路径为`/management`，
    那就可以通过`/management`访问“导航页”。
    
    如果classpath下存在[HAL Browser](https://github.com/mikekelly/hal-browser)（通过webjar：`org.webjars:hal-browser`，
    或`spring-data-rest-hal-browser`），Spring Boot将提供一个以HAL Browser格式的HTML“导航页”。


###46.3 CORS支持

    [跨域资源共享](http://en.wikipedia.org/wiki/Cross-origin_resource_sharing)（CORS）是一个[W3C规范](http://www.w3.org/
    TR/cors/)，用于以灵活的方式指定跨域请求的认证类型，执行器的MVC端点也可以配置成支持该场景。
    
    CORS支持默认是禁用的，只有在`endpoints.cors.allowed-origins`属性设置时才启用。以下配置允许来自`example.com`域的`GET`
    和`POST`调用：
    ```properties
    endpoints.cors.allowed-origins=http://example.com
    endpoints.cors.allowed-methods=GET,POST
    ```
    
    **注** 查看[EndpointCorsProperties](https://github.com/spring-projects/spring-boot/tree/v1.4.1.RELEASE/
    spring-boot-actuator/src/main/java/org/springframework/boot/actuate/autoconfigure/EndpointCorsProperties.java
    )获取完整的配置选项列表。
    
    
###46.4 添加自定义端点

    如果添加一个`Endpoint`类型的`@Bean`，Spring Boot会自动通过JMX和HTTP（如果有可用服务器）将该端点暴露出去。通过创建`MvcEndpoint`
    类型的bean可进一步定义HTTP端点，虽然该bean不是`@Controller`，但仍能使用`@RequestMapping`（和`@Managed*`）暴露资源。
    
    **注** 如果你的用户需要一个单独的管理端口或地址，你可以将注解`@ManagementContextConfiguration`的配置类添加到`/META-INF/
    spring.factories`中，且key为`org.springframework.boot.actuate.autoconfigure.ManagementContextConfiguration`，
    这样该端点将跟其他MVC端点一样移动到一个子上下文中，通过`WebConfigurerAdapter`可以为管理端点添加静态资源。
    
### 46.5 健康信息

    健康信息可以检查应用的运行状态，它经常被监控软件用来提醒人们生产环境是否存在问题。`health`端点暴露的默认信息取决于端点是如何被访问的。
    对于一个非安全，未认证的连接只返回一个简单的'status'信息。对于一个安全或认证过的连接其他详细信息也会展示（具体参考[章节47.7, 
    “HTTP健康端点访问限制” ](47.7. HTTP Health endpoint access restrictions.md)）。
    
    健康信息是从你的`ApplicationContext`中定义的所有[HealthIndicator](http://github.com/spring-projects/spring-boot/
    tree/master/spring-boot-actuator/src/main/java/org/springframework/boot/actuate/health/HealthIndicator.java) beans
    收集过来的。Spring Boot包含很多自动配置的`HealthIndicators`，你也可以写自己的。
    
### 46.6.1 自动配置的HealthIndicators

    Spring Boot在合适的时候会自动配置以下`HealthIndicators`：
    
    |名称|描述|
    |----|:-----|
    |[`CassandraHealthIndicator`](https://github.com/spring-projects/spring-boot/tree/v1.4.1.RELEASE/
    spring-boot-actuator/src/main/java/org/springframework/boot/actuate/health/CassandraHealthIndicator.java)
    |检查Cassandra数据库状况|
    |[`DiskSpaceHealthIndicator`](https://github.com/spring-projects/spring-boot/tree/v1.4.1.RELEASE/
    spring-boot-actuator/src/main/java/org/springframework/boot/actuate/health/DiskSpaceHealthIndicator.java)
    |低磁盘空间检查|
    |[`DataSourceHealthIndicator`](https://github.com/spring-projects/spring-boot/tree/v1.4.1.RELEASE/
    spring-boot-actuator/src/main/java/org/springframework/boot/actuate/health/DataSourceHealthIndicator.java)
    |检查是否能从`DataSource`获取连接|
    |[`ElasticsearchHealthIndicator`](https://github.com/spring-projects/spring-boot/tree/v1.4.1.RELEASE/
    spring-boot-actuator/src/main/java/org/springframework/boot/actuate/health/ElasticsearchHealthIndicator.java)
    |检查Elasticsearch集群状况|
    |[`JmsHealthIndicator`](https://github.com/spring-projects/spring-boot/tree/v1.4.1.RELEASE/
    spring-boot-actuator/src/main/java/org/springframework/boot/actuate/health/JmsHealthIndicator.java)
    |检查JMS消息代理状况|
    |[`MailHealthIndicator`](https://github.com/spring-projects/spring-boot/tree/v1.4.1.RELEASE/
    spring-boot-actuator/src/main/java/org/springframework/boot/actuate/health/MailHealthIndicator.java)
    |检查邮件服务器状况|
    |[`MongoHealthIndicator`](https://github.com/spring-projects/spring-boot/tree/v1.4.1.RELEASE/
    spring-boot-actuator/src/main/java/org/springframework/boot/actuate/health/MongoHealthIndicator.java)
    |检查Mongo数据库状况|
    |[`RabbitHealthIndicator`](https://github.com/spring-projects/spring-boot/tree/v1.4.1.RELEASE/
    spring-boot-actuator/src/main/java/org/springframework/boot/actuate/health/MongoHealthIndicator.java)
    |检查Rabbit服务器状况|
    |[`RedisHealthIndicator`](https://github.com/spring-projects/spring-boot/tree/v1.4.1.RELEASE/
    spring-boot-actuator/src/main/java/org/springframework/boot/actuate/health/RedisHealthIndicator.java)
    |检查Redis服务器状况|
    |[`SolrHealthIndicator`](https://github.com/spring-projects/spring-boot/tree/v1.4.1.RELEASE/
    spring-boot-actuator/src/main/java/org/springframework/boot/actuate/health/SolrHealthIndicator.java)
    |检查Solr服务器状况|
    
    **注** 使用`management.health.defaults.enabled`属性可以禁用以上全部`HealthIndicators`。

### 46.6.2 编写自定义HealthIndicators

    你可以注册实现[HealthIndicator](https://github.com/spring-projects/spring-boot/tree/v1.4.1.RELEASE/
    spring-boot-actuator/src/main/java/org/springframework/boot/actuate/health/HealthIndicator.java)接口的Spring beans
    来提供自定义健康信息。你需要实现`health()`方法，并返回一个`Health`响应，该响应需要包含一个`status`和其他用于展示的详情。
    ```java
    import org.springframework.boot.actuate.health.HealthIndicator;
    import org.springframework.stereotype.Component;
    
    @Component
    public class MyHealth implements HealthIndicator {
    
        @Override
        public Health health() {
            int errorCode = check(); // perform some specific health check
            if (errorCode != 0) {
                return Health.down().withDetail("Error Code", errorCode).build();
            }
            return Health.up().build();
        }
    
    }
    ```
    **注** 对于给定`HealthIndicator`的标识是bean name去掉`HealthIndicator`后缀剩下的部分。在以上示例中，可以在`my`的实体中获取健康信息。
    
    除Spring Boot预定义的[`Status`](http://github.com/spring-projects/spring-boot/tree/master/spring-boot-actuator/src/
    main/java/org/springframework/boot/actuate/health/Status.java)类型，`Health`也可以返回一个代表新的系统状态的自定义`Status`。
    在这种情况下，你需要提供一个[`HealthAggregator`](http://github.com/spring-projects/spring-boot/tree/master/
    spring-boot-actuator/src/main/java/org/springframework/boot/actuate/health/HealthAggregator.java)接口的自定义实现，
    或使用`management.health.status.order`属性配置默认实现。
    
    例如，假设一个新的，代码为`FATAL`的`Status`被用于你的一个`HealthIndicator`实现中。为了配置严重性级别，你需要将以下配置添加到
    application属性文件中：
    ```java
    management.health.status.order=DOWN, OUT_OF_SERVICE, UNKNOWN, UP
    ```
    如果使用HTTP访问health端点，你可能想要注册自定义的status，并使用`HealthMvcEndpoint`进行映射。例如，你可以将`FATAL`映射为
    `HttpStatus.SERVICE_UNAVAILABLE`。
    
    
### 46.6 安全与HealthIndicators

    `HealthIndicators`返回的信息通常有点敏感，例如，你可能不想将数据库服务器的详情发布到外面。因此，在使用一个未认证的HTTP连接时，
    默认只会暴露健康状态（health status）。如果想将所有的健康信息暴露出去，你可以把`endpoints.health.sensitive`设置为`false`。
    
    为防止'拒绝服务'攻击，Health响应会被缓存，你可以使用`endpoints.health.time-to-live`属性改变默认的缓存时间（1000毫秒）。
    
        
###46.7.1 自动配置的InfoContributors

    Spring Boot会在合适的时候自动配置以下`InfoContributors`：
    
    |名称|描述|
    |:----|:----|
    |[`EnvironmentInfoContributor`](https://github.com/spring-projects/spring-boot/tree/v1.4.1.RELEASE/
    spring-boot-actuator/src/main/java/org/springframework/boot/actuate/info/EnvironmentInfoContributor.java)
    |暴露`Environment`中key为`info`的所有key|
    |[`GitInfoContributor`](https://github.com/spring-projects/spring-boot/tree/v1.4.1.RELEASE/
    spring-boot-actuator/src/main/java/org/springframework/boot/actuate/info/GitInfoContributor.java)
    |暴露git信息，如果存在`git.properties`文件|
    |[`BuildInfoContributor`](https://github.com/spring-projects/spring-boot/tree/v1.4.1.RELEASE/
    spring-boot-actuator/src/main/java/org/springframework/boot/actuate/info/BuildInfoContributor.java)
    |暴露构建信息，如果存在`META-INF/build-info.properties`文件|
    
    **注** 使用`management.info.defaults.enabled`属性可禁用以上所有`InfoContributors`。
     

### 46.7.2 自定义应用info信息

    通过设置Spring属性`info.*`，你可以定义`info`端点暴露的数据。所有在`info`关键字下的`Environment`属性都将被自动暴露，例如，
    你可以将以下配置添加到`application.properties`：
    ```properties
    info.app.encoding=UTF-8
    info.app.java.source=1.8
    info.app.java.target=1.8
    ```
    **注** 你可以[在构建时扩展info属性](http://docs.spring.io/spring-boot/docs/1.4.1.RELEASE/reference/htmlsingle/
    #howto-automatic-expansion)，而不是硬编码这些值。假设使用Maven，你可以按以下配置重写示例：
    ```properties
    info.app.encoding=@project.build.sourceEncoding@
    info.app.java.source=@java.version@
    info.app.java.target=@java.version@
    ```


### 46.7.3 Git提交信息

    `info`端点的另一个有用特性是，在项目构建完成后发布`git`源码仓库的状态信息。如果`GitProperties` bean可用，Spring Boot将暴露
    `git.branch`，`git.commit.id`和`git.commit.time`属性。
    
    **注** 如果classpath根目录存在`git.properties`文件，Spring Boot将自动配置`GitProperties` bean。查看
    [Generate git information](http://docs.spring.io/spring-boot/docs/1.4.1.RELEASE/reference/htmlsingle/
    #howto-git-info)获取更多详细信息。
    
    使用`management.info.git.mode`属性可展示全部git信息（比如`git.properties`全部内容）：
    ```properties
    management.info.git.mode=full
    ```

###46.7.5 编写自定义的InfoContributors

    你可以注册实现了[`InfoContributor`](https://github.com/spring-projects/spring-boot/tree/v1.4.1.RELEASE/
    spring-boot-actuator/src/main/java/org/springframework/boot/actuate/info/InfoContributor.java)
    接口的Spring beans来提供自定义应用信息。以下示例暴露一个只有单个值的`example`实体：
    ```java
    import java.util.Collections;
    
    import org.springframework.boot.actuate.info.Info;
    import org.springframework.boot.actuate.info.InfoContributor;
    import org.springframework.stereotype.Component;
    
    @Component
    public class ExampleInfoContributor implements InfoContributor {
    
        @Override
        public void contribute(Info.Builder builder) {
            builder.withDetail("example",
                    Collections.singletonMap("key", "value"));
        }
    
    }
    ```
    如果点击`info`端点，你应该可以看到包含以下实体的响应：
    ```json
    {
        "example": {
            "key" : "value"
        }
    }
    ```


###46.7 应用信息

    应用信息会暴露所有[`InfoContributor`](https://github.com/spring-projects/spring-boot/tree/v1.4.1.RELEASE/
    spring-boot-actuator/src/main/java/org/springframework/boot/actuate/info/InfoContributor.java) beans收集的各种信息，
    Spring Boot包含很多自动配置的`InfoContributors`，你也可以编写自己的实现。


### 47. 基于HTTP的监控和管理

    如果你正在开发一个Spring MVC应用，Spring Boot执行器自动将所有启用的端点通过HTTP暴露出去。默认约定使用端点的`id`作为URL路径，
    例如，`health`暴露为`/health`。

### 47.1 保护敏感端点

    如果你的项目添加了‘Spring Security’依赖，所有通过HTTP暴露的敏感端点都会受到保护，默认情况下会使用用户名为`user`的基本认证
    （basic authentication），产生的密码会在应用启动时打印到控制台上。
    
    **注** 在应用启动时会记录生成的密码，具体搜索`Using default security password`。
    
    你可以使用Spring属性改变用户名，密码和访问端点需要的安全角色。例如，你可以将以下配置添加到`application.properties`中：
    ```java
    security.user.name=admin
    security.user.password=secret
    management.security.role=SUPERUSER
    ```
    
    **注** 如果不使用Spring Security，并且公开暴露HTTP端点，你应该慎重考虑启用哪些端点，具体参考[Section 46.1,
     “Customizing endpoints”](40.1. Customizing endpoints.md)。


### 47.2 自定义管理端点路径

    有时将所有管理端点划分到单个路径下是有用的。例如，`/info`可能已被应用占用，你可以用`management.contextPath`属性为管理端点设置一个前缀：
    ```java
    management.context-path=/manage
    ```
    以上的`application.properties`示例将把端点从`/{id}`改为`/manage/{id}`（比如`/manage/info`）。
    
    你也可以改变端点的`id`（使用`endpoints.{name}.id`）来改变MVC端点的默认资源路径，合法的端点ids只能由字母数字组成
    （因为它们可以暴露到很多地方，包括不允许特殊字符的JMX对象name）。MVC路径也可以通过配置`endpoints.{name}.path`来单独改变，
    Spring Boot不会校验这些值（所以你可以使用URL中任何合法的字符）。例如，想要改变`/health`端点路径为`/ping/me`，你可以设置`endpoints.health.path=/ping/me`。
    
    **注** 如果你提供一个自定义`MvcEndpoint`，记得包含一个可设置的`path`属性，并像标准MVC端点那样将该属性默认设置为`/{id}`
    （具体可参考`HealthMvcEndpoint`）。如果你的自定义端点是一个`Endpoint`（不是`MvcEndpoint`），Spring Boot将会为你分配路径。
    
    
### 47.3  自定义管理服务器端口

    对于基于云的部署，使用默认的HTTP端口暴露管理端点（endpoints）是明智的选择。然而，如果你的应用是在自己的数据中心运行，那你可能倾向于
    使用一个不同的HTTP端口来暴露端点。`management.port`属性可以用来改变HTTP端口：
    ```java
    management.port=8081
    ```
    由于你的管理端口经常被防火墙保护，不对外暴露也就不需要保护管理端点，即使你的主应用是受保护的。在这种情况下，classpath下会存在
    Spring Security库，你可以设置以下属性来禁用安全管理策略（management security）：
    ```java
    management.security.enabled=false
    ```
    （如果classpath下不存在Spring Security，那也就不需要显式的以这种方式来禁用安全管理策略，它甚至可能会破坏应用程序。）

###47.4 配置管理相关的SSL

    当配置使用一个自定义端口时，管理服务器可以通过各种`management.ssl.*`属性配置自己的SSL。例如，以下配置允许通过HTTP访问管理服务器，
    通过HTTPS访问主应用：
    ```properties
    server.port=8443
    server.ssl.enabled=true
    server.ssl.key-store=classpath:store.jks
    server.ssl.key-password=secret
    management.port=8080
    management.ssl.enable=false
    ```
    或者，主应用服务器和管理服务器都使用SSL，但key stores不一样：
    ```properties
    server.port=8443
    server.ssl.enabled=true
    server.ssl.key-store=classpath:main.jks
    server.ssl.key-password=secret
    management.port=8080
    management.ssl.enable=true
    management.ssl.key-store=classpath:management.jks
    management.ssl.key-password=secret
    ```

### 47.6 禁用HTTP端点

    如果不想通过HTTP暴露端点，你可以将管理端口设置为-1：
    `management.port=-1`
    
### 47.7 HTTP health端点访问限制

    `health`端点暴露的信息依赖于是否为匿名访问，应用是否受保护。默认情况下，当匿名访问一个受保护的应用时，任何有关服务器的健康详情
    都被隐藏了，该端点只简单的展示服务器运行状况（up或down）。此外，响应会被缓存一个可配置的时间段以防止端点被用于'拒绝服务'攻击，
    你可以通过`endpoints.health.time-to-live`属性设置缓存时间（单位为毫秒），默认为1000毫秒，也就是1秒。
    
    你可以增强上述限制，从而只允许认证用户完全访问一个受保护应用的`health`端点，将`endpoints.health.sensitive`设为`true`
    可以实现该效果，具体可查看以下总结（`sensitive`标识值为"false"的默认加粗）：
    
    |`management.security.enabled`|`endpoints.health.sensitive`|未认证|认证|
    |:----|:----|:----|:-----|
    |false|**false**|全部内容|全部内容|
    |false|true|只能查看Status|全部内容|
    |true|**false**|只能查看Status|全部内容|
    |true|true|不能查看任何内容|全部内容|
    

### 48.1 自定义MBean名称

    MBean的名称通常产生于端点的id，例如，`health`端点被暴露为`org.springframework.boot/Endpoint/healthEndpoint`。
    
    如果应用包含多个Spring `ApplicationContext`，你会发现存在名称冲突。为了解决这个问题，你可以将`endpoints.jmx.uniqueNames`
    设置为`true`，这样MBean的名称总是唯一的。
    
    你也可以自定义端点暴露的JMX域，具体可参考以下`application.properties`示例：
    ```properties
    endpoints.jmx.domain=myapp
    endpoints.jmx.uniqueNames=true


### 48.2 禁用JMX端点

    如果不想通过JMX暴露端点，你可以将`endpoints.jmx.enabled`属性设置为`false`：
    ```java
    endpoints.jmx.enabled=false
    ```

### 48.3.1 自定义Jolokia

    Jolokia有很多配置，通常使用servlet参数进行设置，跟Spring Boot一块使用时可以在`application.properties`中添加`jolokia.config.`
    前缀的属性进行配置：
    ```java
    jolokia.config.debug=true
    ```
### 48.3 使用Jolokia通过HTTP实现JMX远程管理

    Jolokia是一个JMX-HTTP桥，它提供了一种访问JMX beans的替代方法。想要使用Jolokia，只需添加`org.jolokia:jolokia-core`的依赖。
    例如，使用Maven需要添加以下配置：
    ```xml
    <dependency>
        <groupId>org.jolokia</groupId>
        <artifactId>jolokia-core</artifactId>
     </dependency>
    ```
    然后在你的管理HTTP服务器上可以通过`/jolokia`访问Jolokia。


### 49. 使用远程shell进行监控和管理

    Spring Boot支持集成一个称为'CRaSH'的Java shell，你可以在CRaSH中使用ssh或telnet命令连接到运行的应用，项目中添加以下依赖可以
    启用远程shell支持：
    ```xml
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-remote-shell</artifactId>
     </dependency>
    ```
    **注** 如果想使用telnet访问，你还需添加对`org.crsh:crsh.shell.telnet`的依赖。
    
    **注** CRaSH运行时需要JDK，因为它要动态编译命令。如果一个基本的`help`命令都运行失败，你很可能使用的是JRE。
    
    
### 49.1.1 远程shell证书

    你可以使用`management.shell.auth.simple.user.name`和`management.shell.auth.simple.user.password`属性配置自定义的连接证书，
    也可以使用Spring Security的`AuthenticationManager`处理登录职责，具体参考[CrshAutoConfiguration](http://docs.spring.io/
    spring-boot/docs/1.4.1.RELEASE/api/org/springframework/boot/actuate/autoconfigure/CrshAutoConfiguration.html)和
    [ShellProperties](http://docs.spring.io/spring-boot/docs/1.4.1.RELEASE/api/org/springframework/boot/actuate/
    autoconfigure/ShellProperties.html)的Javadoc。

### 49.1 连接远程shell

    远程shell默认监听端口为`2000`，默认用户名为`user`，密码为随机生成的，并且在输出日志中会显示。如果应用使用Spring Security，
    该shell默认使用[相同的配置](../IV. Spring Boot features/28. Security.md)。如果不是，将使用一个简单的认证策略，
    你可能会看到类似这样的信息：
    ```java
    Using default password for shell access: ec03e16c-4cf4-49ee-b745-7c8255c1dd7e
    ```
    Linux和OSX用户可以使用`ssh`连接远程shell，Windows用户可以下载并安装[PuTTY](http://www.putty.org/)。
    ```shell
    $ ssh -p 2000 user@localhost
    
    user@localhost's password:
      .   ____          _            __ _ _
     /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
    ( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
     \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
      '  |____| .__|_| |_|_| |_\__, | / / / /
     =========|_|==============|___/=/_/_/_/
     :: Spring Boot ::  (v1.4.1.RELEASE) on myhost
    ```
    输入`help`可以获取命令列表，Spring Boot提供`metrics`，`beans`，`autoconfig`和`endpoint`命令。
    
### 49.2.1 远程shell命令

    你可以使用Groovy或Java编写其他的shell命令（具体参考CRaSH文档），Spring Boot默认会搜索以下路径的命令：
    
    * `classpath*:/commands/**`
    * `classpath*:/crash/commands/**`
    
    **注** 设置`shell.command-path-patterns`属性可以改变搜索路径。
    **注** 如果使用可执行存档（archive），shell依赖的所有类都必须打包进一个内嵌的jar，而不是直接打包进可执行jar或war。
    
    下面是一个从`src/main/resources/commands/hello.groovy`加载的'hello'命令：
    ```java
    package commands
    
    import org.crsh.cli.Usage
    import org.crsh.cli.Command
    
    class hello {
    
        @Usage("Say Hello")
        @Command
        def main(InvocationContext context) {
            return "Hello"
        }
    
    }
    ```
    Spring Boot为`InvocationContext`添加一些其他属性，你可以在命令中访问它们：
    
    |属性名称|描述|
    |------|:------|
    |`spring.boot.version`|Spring Boot的版本|
    |`spring.version`|Spring核心框架的版本|
    |`spring.beanfactory`|获取Spring的`BeanFactory`|
    |`spring.environment`|获取Spring的`Environment`|
         


### 50. 度量指标（Metrics）

    Spring Boot执行器包含一个支持'gauge'和'counter'级别的度量指标服务，'gauge'记录一个单一值，'counter'记录一个增量（增加或减少）。
    同时，Spring Boot提供一个[PublicMetrics](https://github.com/spring-projects/spring-boot/tree/v1.4.1.RELEASE/
    spring-boot-actuator/src/main/java/org/springframework/boot/actuate/endpoint/PublicMetrics.java)接口，你可以实现它，
    从而暴露以上两种机制不能记录的指标，具体参考[SystemPublicMetrics](https://github.com/spring-projects/spring-boot/
    tree/v1.4.1.RELEASE/spring-boot-actuator/src/main/java/org/springframework/boot/actuate/endpoint/PublicMetrics.java)。
    
    所有HTTP请求的指标都被自动记录，所以如果点击`metrics`端点，你可能会看到类似以下的响应：
    ```javascript
    {
        "counter.status.200.root": 20,
        "counter.status.200.metrics": 3,
        "counter.status.200.star-star": 5,
        "counter.status.401.root": 4,
        "gauge.response.star-star": 6,
        "gauge.response.root": 2,
        "gauge.response.metrics": 3,
        "classes": 5808,
        "classes.loaded": 5808,
        "classes.unloaded": 0,
        "heap": 3728384,
        "heap.committed": 986624,
        "heap.init": 262144,
        "heap.used": 52765,
        "mem": 986624,
        "mem.free": 933858,
        "processors": 8,
        "threads": 15,
        "threads.daemon": 11,
        "threads.peak": 15,
        "uptime": 494836,
        "instance.uptime": 489782,
        "datasource.primary.active": 5,
        "datasource.primary.usage": 0.25
    }
    ```
    此处，我们可以看到基本的`memory`，`heap`，`class loading`，`processor`和`thread pool`信息，连同一些HTTP指标。在该实例中，
    `root`('/')，`/metrics` URLs分别返回`20`次，`3`次`HTTP 200`响应，同时可以看到`root` URL返回了`4`次`HTTP 401`
    （unauthorized）响应。双星号（`star-star`）来自于被Spring MVC `/**`匹配到的请求（通常为静态资源）。
    
    
    `gauge`展示了一个请求的最后响应时间，所以`root`的最后请求响应耗时`2毫秒`，`/metrics`耗时`3毫秒`。
    
    **注** 在该示例中，我们实际是通过HTTP的`/metrics`路径访问该端点的，这也就是响应中出现`metrics`的原因。
      

### 50.1 系统指标

    Spring Boot会暴露以下系统指标：
    - 系统内存总量（`mem`），单位:KB
    - 空闲内存数量（`mem.free`），单位:KB
    - 处理器数量（`processors`）
    - 系统正常运行时间（`uptime`），单位:毫秒
    - 应用上下文（应用实例）正常运行时间（`instance.uptime`），单位:毫秒
    - 系统平均负载（`systemload.average`）
    - 堆信息（`heap`，`heap.committed`，`heap.init`，`heap.used`），单位:KB
    - 线程信息（`threads`，`thread.peak`，`thead.daemon`）
    - 类加载信息（`classes`，`classes.loaded`，`classes.unloaded`）
    - 垃圾收集信息（`gc.xxx.count`, `gc.xxx.time`）
    

### 50.2 数据源指标

    Spring Boot会为应用中定义的每个支持的`DataSource`暴露以下指标：
    - 活动连接数（`datasource.xxx.active`）
    - 连接池当前使用情况（`datasource.xxx.usage`）
    
    所有数据源指标共用`datasoure.`前缀，该前缀适用于每个数据源：
    - 如果是主数据源（唯一可用的数据源或注解`@Primary`的数据源）前缀为`datasource.primary`。
    - 如果数据源bean名称以`DataSource`结尾，前缀就是bean的名称去掉`DataSource`的部分（比如，`batchDataSource`的前缀是
    `datasource.batch`）。
    - 其他情况使用bean的名称作为前缀。
    
    通过注册自定义版本的`DataSourcePublicMetrics` bean，你可以覆盖部分或全部的默认行为。Spring Boot默认提供支持所有数据源的元数据，
    如果喜欢的数据源恰好不被支持，你可以添加其他的`DataSourcePoolMetadataProvider` beans，具体参考`DataSourcePoolMetadataProvidersConfiguration`。

###50.3 缓存指标

    Spring Boot会为应用中定义的每个支持的缓存暴露以下指标：
    - cache当前大小（`cache.xxx.size`）
    - 命中率（`cache.xxx.hit.ratio`）
    - 丢失率（`cache.xxx.miss.ratio`）
    
    **注** 缓存提供商没有以一致的方式暴露命中/丢失率，有些暴露的是聚合（aggregated）值（比如，自从统计清理后的命中率），
    而其他暴露的是时序（temporal）值
    （比如，最后一秒的命中率），具体查看缓存提供商的文档。
    
    如果两个不同的缓存管理器恰巧定义了相同的缓存，缓存name将以`CacheManager` bean的name作为前缀。
    
    注册自定义版本的`CachePublicMetrics`可以部分或全部覆盖这些默认值，Spring Boot默认为EhCache，Hazelcast，Infinispan，
    JCache和Guava提供统计。如果喜欢的缓存库没被支持，你可以添加其他`CacheStatisticsProvider` beans，具体可参考
    `CacheStatisticsAutoConfiguration`。

### 50.4 Tomcat session指标

    如果你使用Tomcat作为内嵌的servlet容器，Spring Boot将自动暴露session指标，
    `httpsessions.active`和`httpsessions.max`分别提供活动的和最大的session数量。


### 50.5 记录自己的指标

    将[CounterService](https://github.com/spring-projects/spring-boot/blob/master/spring-boot-actuator/src/main/
    java/org/springframework/boot/actuate/metrics/CounterService.java)或[GaugeService](http://github.com/
    spring-projects/spring-boot/tree/master/spring-boot-actuator/src/main/java/org/springframework/boot/actuate/
    metrics/GaugeService.java)注入到你的bean中可以记录自己的度量指标：`CounterService`暴露`increment`，`decrement`和
    `reset`方法；`GaugeService`提供一个`submit`方法。
    
    下面是一个简单的示例，它记录了方法调用的次数：
    ```java
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.boot.actuate.metrics.CounterService;
    import org.springframework.stereotype.Service;
    
    @Service
    public class MyService {
    
        private final CounterService counterService;
    
        @Autowired
        public MyService(CounterService counterService) {
            this.counterService = counterService;
        }
    
        public void exampleMethod() {
            this.counterService.increment("services.system.myservice.invoked");
        }
    
    }
    ```
    **注** 你可以将任何字符串用作度量指标的名称，但最好遵循所选存储/图形技术的指南，[Matt Aimonetti’s Blog]
    (http://matt.aimonetti.net/posts/2013/06/26/practical-guide-to-graphite-monitoring/)中有一些好的关于Graphite的指南。


### 50.6 添加自己的公共指标

    只要注册其他的`PublicMetrics`实现beans，你就可以添加其他的度量指标，比如计算metrics端点每次调用的次数。默认情况下，
    端点会聚合所有这样的beans，通过定义自己的`MetricsEndpoint`可以轻易改变这种情况。


### 50.7 使用Java8的特性

    Spring Boot提供的`GaugeService`和`CounterService`默认实现依赖于你使用的Java版本。如果使用Java8（或更高版本），
    Spring Boot将实现切换为一个高性能版本，该版本优化了写速度，底层使用原子内存buffers，而不是通过不可变但相对昂贵的`Metric<?>`类型
    （跟基于仓库的实现相比，counters大概快5倍，gauges大概快2倍）。对于Java7，Dropwizard指标服务也是很有效的（使用了某些Java8并发库），
    但它不记录指标值的时间戳。如果需要关注指标采集的性能，建议你使用高性能的选项，并不要频繁读取指标信息，这样写入会本地缓存，只有在需要时读取。
    
    **注** 如果使用Java8或Dropwizard，Spring Boot默认不会使用老的`MetricRepository`和它的`InMemoryMetricRepository`实现。

###50.8.1 示例: 导出到Redis

    如果提供一个`RedisMetricRepository`类型的`@Bean`并注解`@ExportMetricWriter`，指标将导出到Redis缓存完成聚合。
    `RedisMetricRepository`有两个重要参数用于配置实现这样的目的：`prefix`和`key`（传递给构造器）。最好使用应用实例唯一的前缀
    （比如，使用一个随机值及应用的逻辑name，这样可以关联相同应用的其他实例）。“key”用来保持所有指标name的全局索引，所以它应该全局唯一，
    不管这对于你的应用意味着什么（比如，相同系统的两个实例可以共享一个Redis缓存，如果它们有不同的keys）。
    
    示例：
    ```java
    @Bean
    @ExportMetricWriter
    MetricWriter metricWriter(MetricExportProperties export) {
        return new RedisMetricRepository(connectionFactory,
          export.getRedis().getPrefix(), export.getRedis().getKey());
    }
    ```
    `application.properties`：
    ```properties
    spring.metrics.export.redis.prefix: metrics.mysystem.${spring.application.name:application}.${random.value:0000}
    spring.metrics.export.redis.key: keys.metrics.mysystem
    ```
    前缀最后由应用名和id组成，所以它可以用来标识具有相同逻辑名的processes分组。
    
    **注** 设置`key`和`prefix`都是非常重要的。key用于所有的仓库操作，并可以被多个仓库共享。如果多个仓库共享一个key
    （比如你需要聚合它们的时候），你通常有一个只读“master”仓库，它有一个简短的但可辨识的前缀（比如`metrics.mysystem`），
    还有很多只写的仓库，这些仓库以master前缀开头（比如以上示例中为`metrics.mysystem.*`）。这样从一个"master"仓库读取所有keys是
    相当高效的，但使用较长的前缀读取一个子集就比较低效了（比如使用一个写仓库）。
    
    **注** 以上示例使用`MetricExportProperties`去注入和提取key和前缀，这是Spring Boot提供的便利设施，用于配置合适的默认值，
    你也可以自己设值。
    
###50.9 聚合多个来源的指标

    Spring Boot提供一个`AggregateMetricReader`，用于合并来自不同物理来源的指标。具有相同逻辑指标的来源只需将指标加上以句号分隔的
    前缀发布出去，reader会聚合它们（通过截取指标名并丢掉前缀），计数器被求和，所有东西（比如gauges）都采用最近的值。
    
    这非常有用，特别是当有多个应用实例反馈数据到中央仓库（比如Redis），并且你想展示结果。推荐将`MetricReaderPublicMetrics`
    结果连接到`/metrics`端点。
    
    示例：
    ```java
    @Autowired
    private MetricExportProperties export;
    
    @Bean
    public PublicMetrics metricsAggregate() {
      return new MetricReaderPublicMetrics(aggregatesMetricReader());
    }
    
    private MetricReader globalMetricsForAggregation() {
      return new RedisMetricRepository(this.connectionFactory,
          this.export.getRedis().getAggregatePrefix(), this.export.getRedis().getKey());
    }
    
    private MetricReader aggregatesMetricReader() {
      AggregateMetricReader repository = new AggregateMetricReader(
          globalMetricsForAggregation());
      return repository;
    }
    ```
    **注** 上面的示例使用`MetricExportProperties`注入和提取key和前缀，这是Spring Boot提供的便利设施，并且默认值是合适的，
    它们是在`MetricExportAutoConfiguration`中设置的。
    
    **注** 上面的`MetricReaders`不是`@Beans`，也没注解`@ExportMetricReader`，因为它们只收集和分析来自其他仓库的数据，
    不需要暴露自己的值。
    
### 50.10 Dropwizard指标

    当你声明对`io.dropwizard.metrics:metrics-core`的依赖时，Spring Boot会创建一个默认的`MetricRegistry` bean。如果需要自定义，
    你可以注册自己的`@Bean`实例。使用[Dropwizard ‘Metrics’ library](https://dropwizard.github.io/metrics/)的用户会发现
    Spring Boot指标自动发布到`com.codahale.metrics.MetricRegistry`，来自`MetricRegistry`的指标也自动暴露到`/metrics`端点。
    
    使用Dropwizard指标时，默认的`CounterService`和`GaugeService`被`DropwizardMetricServices`替换，它是一个`MetricRegistry`
    的包装器（所以你可以`@Autowired`其中任意services，并像平常那么使用它）。通过使用恰当的前缀类型标记你的指标名可以创建特殊的
    Dropwizard指标服务（比如，gauges使用`timer.*`，`histogram.*`，counters使用`meter.*`）。


### 51. 审计

    Spring Boot执行器有一个灵活的审计框架，一旦Spring Security处于活动状态（默认抛出'authentication success'，'failure'和
    'access denied'异常），它就会发布事件。这对于报告非常有用，同时可以基于认证失败实现一个锁定策略。为了自定义发布的安全事件，
    你可以提供自己的`AbstractAuthenticationAuditListener`，`AbstractAuthorizationAuditListener`实现。你也可以使用审计
    服务处理自己的业务事件。为此，你可以将存在的`AuditEventRepository`注入到自己的组件，并直接使用它，或者只是简单地通过Spring `ApplicationEventPublisher`发布`AuditApplicationEvent`（使用`ApplicationEventPublisherAware`）。


### 52. 追踪（Tracing）

    对于所有的HTTP请求Spring Boot自动启用追踪，你可以查看`trace`端点获取最近100条请求的基本信息：
    ```javascript
    [{
        "timestamp": 1394343677415,
        "info": {
            "method": "GET",
            "path": "/trace",
            "headers": {
                "request": {
                    "Accept": "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
                    "Connection": "keep-alive",
                    "Accept-Encoding": "gzip, deflate",
                    "User-Agent": "Mozilla/5.0 Gecko/Firefox",
                    "Accept-Language": "en-US,en;q=0.5",
                    "Cookie": "_ga=GA1.1.827067509.1390890128; ..."
                    "Authorization": "Basic ...",
                    "Host": "localhost:8080"
                },
                "response": {
                    "Strict-Transport-Security": "max-age=31536000 ; includeSubDomains",
                    "X-Application-Context": "application:8080",
                    "Content-Type": "application/json;charset=UTF-8",
                    "status": "200"
                }
            }
        }
    },{
        "timestamp": 1394343684465,
        ...
    }]
    ```
    
### 52.1 自定义追踪

    如果需要追踪其他事件，你可以注入[TraceRepository](http://github.com/spring-projects/spring-boot/tree/master/
    spring-boot-actuator/src/main/java/org/springframework/boot/actuate/trace/TraceRepository.java)到你的Spring Beans中，
    `add`方法接收一个`Map`结构的参数，该数据将转换为JSON并被记录下来。
    
    默认使用`InMemoryTraceRepository`存储最新的100个事件，如果需要扩充容量，你可以定义自己的`InMemoryTraceRepository`实例，
    甚至创建自己的`TraceRepository`实现。
    

### 53. 进程监控

    在Spring Boot执行器中，你可以找到几个类，它们创建的文件利于进程监控：
    - `ApplicationPidFileWriter`创建一个包含应用PID的文件（默认位于应用目录，文件名为`application.pid`）。
    - `EmbeddedServerPortFileWriter`创建一个或多个包含内嵌服务器端口的文件（默认位于应用目录，文件名为`application.port`）。
    
    这些writers默认没被激活，但你可以使用以下描述的任何方式来启用它们。


### 53.1 扩展配置

    在`META-INF/spring.factories`文件中，你可以激活创建PID文件的`listener(s)`，示例：
    ```java
    org.springframework.context.ApplicationListener=\
    org.springframework.boot.actuate.system.ApplicationPidFileWriter,
    org.springframework.boot.actuate.system.EmbeddedServerPortFileWriter
    ```

### 53.2 以编程方式

    你也可以通过调用`SpringApplication.addListeners(…)`方法并传递相应的`Writer`对象来激活一个监听器，
    该方法允许你通过`Writer`构造器自定义文件名和路径。





