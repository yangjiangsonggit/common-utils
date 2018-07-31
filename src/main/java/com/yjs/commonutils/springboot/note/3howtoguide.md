###69.1.1. 使用Maven自动暴露属性

    你可以使用Maven的资源过滤（resource filter）自动暴露来自Maven项目的属性，如果使用`spring-boot-starter-parent`，
    你可以通过`@..@`占位符引用Maven项目的属性，例如：
    ```properties
    app.encoding=@project.build.sourceEncoding@
    app.java.version=@java.version@
    ```
    **注** 如果启用`addResources`标识，`spring-boot:run`可以将`src/main/resources`直接添加到classpath（出于热加载目的），
    这就绕过了资源过滤和本特性。你可以使用`exec:java`目标进行替代，或自定义该插件的配置，具体查看[插件使用页面](http://docs.spring.io/
    spring-boot/docs/1.4.1.RELEASE/maven-plugin/usage.html)。
    
    如果不使用starter parent，你需要将以下片段添加到`pom.xml`中（`<build/>`元素内）：
    ```xml
    <resources>
        <resource>
            <directory>src/main/resources</directory>
            <filtering>true</filtering>
        </resource>
    </resources>
    ```
    和（`<plugins/>`元素内）：
    ```xml
    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-resources-plugin</artifactId>
        <version>2.7</version>
        <configuration>
            <delimiters>
                <delimiter>@</delimiter>
            </delimiters>
            <useDefaultDelimiters>false</useDefaultDelimiters>
        </configuration>
    </plugin>
    ```
    
    **注** 如果你在配置中使用标准的Spring占位符（比如`${foo}`）且没有将`useDefaultDelimiters`属性设置为`false`，
    那构建时这些属性将被暴露出去。


###69.1.2. 使用Gradle自动暴露属性

    你可以通过配置Java插件的`processResources`任务自动暴露来自Gradle项目的属性：
    ```gradle
    processResources {
        expand(project.properties)
    }
    ```
    然后你可以通过占位符引用Gradle项目的属性：
    ```properties
    app.name=${name}
    app.description=${description}
    ```
    
    **注** Gradle的`expand`方法使用Groovy的`SimpleTemplateEngine`转换`${..}`占位符，`${..}`这种格式跟Spring自身的属性占位符机制冲突，
    想要自动暴露Spring属性占位符，你需要将其进行编码，比如`\${..}`。



### 69.3 改变应用程序外部配置文件的位置

    默认情况下，来自不同源的属性以一个定义好的顺序添加到Spring的`Environment`中（精确顺序可查看'Sprin Boot特性'章节的[Chapter 24, 
    Externalized Configuration](../IV. Spring Boot features/24. Externalized Configuration.md)）。
    
    为应用程序源添加`@PropertySource`注解是一种很好的添加和修改源顺序的方法。传递给`SpringApplication`静态便利设施（convenience）
    方法的类和使用`setSources()`添加的类都会被检查，以查看它们是否有`@PropertySources`，如果有，这些属性会被尽可能早的添加到
    `Environment`里，以确保`ApplicationContext`生命周期的所有阶段都能使用。以这种方式添加的属性优先级低于任何使用默认位置
    （比如`application.properties`）添加的属性，系统属性，环境变量或命令行参数。
    
    你也可以提供系统属性（或环境变量）来改变该行为：
    
    * `spring.config.name`（`SPRING_CONFIG_NAME`）是根文件名，默认为`application`。
    * `spring.config.location`（`SPRING_CONFIG_LOCATION`）是要加载的文件（例如，一个classpath资源或URL）。Spring Boot为该文档
    设置一个单独的`Environment`属性，它可以被系统属性，环境变量或命令行参数覆盖。
    
    不管你在environment设置什么，Spring Boot都将加载上面讨论过的`application.properties`。如果使用YAML，那具有`.yml`扩展的文件默认
    也会被添加到该列表，详情参考[ConfigFileApplicationListener](http://github.com/spring-projects/spring-boot/tree/master/
    spring-boot/src/main/java/org/springframework/boot/context/config/ConfigFileApplicationListener.java)


### 69.4 使用'short'命令行参数

    有些人喜欢使用（例如）`--port=9000`代替`--server.port=9000`来设置命令行配置属性。你可以通过在`application.properties`
    中使用占位符来启用该功能，比如：
    ```properties
    server.port=${port:8080}
    ```
    **注** 如果你继承自`spring-boot-starter-parent` POM，为了防止和Spring格式的占位符产生冲突，`maven-resources-plugins`
    默认的过滤令牌（filter token）已经从`${*}`变为`@`（即`@maven.token@`代替`${maven.token}`）。如果直接启用maven对
    `application.properties`的过滤，你可能想使用[其他的分隔符](http://maven.apache.org/plugins/maven-resources-plugin/
    resources-mojo.html#delimiters)替换默认的过滤令牌。
    
    **注** 在这种特殊的情况下，端口绑定能够在一个PaaS环境下工作，比如Heroku和Cloud Foundry，因为在这两个平台中`PORT`环境变量是
    自动设置的，并且Spring能够绑定`Environment`属性的大写同义词。



###69.5 使用YAML配置外部属性

    YAML是JSON的一个超集，可以非常方便的将外部配置以层次结构形式存储起来，比如：
    ```json
    spring:
        application:
            name: cruncher
        datasource:
            driverClassName: com.mysql.jdbc.Driver
            url: jdbc:mysql://localhost/test
    server:
        port: 9000
    ```
    创建一个`application.yml`文件，将它放到classpath的根目录下，并添加`snakeyaml`依赖（Maven坐标为`org.yaml:snakeyaml`，
    如果你使用`spring-boot-starter`那就已经包含了）。一个YAML文件会被解析为一个Java `Map<String,Object>`（和一个JSON对象类似），
    Spring Boot会平伸该map，这样它就只有1级深度，并且有period-separated的keys，跟人们在Java中经常使用的`Properties`文件非常类似。
    上面的YAML示例对应于下面的`application.properties`文件：
    ```java
    spring.application.name=cruncher
    spring.datasource.driverClassName=com.mysql.jdbc.Driver
    spring.datasource.url=jdbc:mysql://localhost/test
    server.port=9000
    ```
    查看'Spring Boot特性'章节的[Section 24.6, “Using YAML instead of Properties”](../IV. Spring Boot features/24.6. 
    Using YAML instead of Properties.md)可以获取更多关于YAML的信息。


### 69.6 设置生效的Spring profiles

    Spring `Environment`有一个API可以设置生效的profiles，但通常你会通过系统属性（`spring.profiles.active`）或OS环境变量
    （`SPRING_PROFILES_ACTIVE`）设置。比如，使用一个`-D`参数启动应用程序（记着把它放到`main`类或jar文件之前）：
    ```shell
    $ java -jar -Dspring.profiles.active=production demo-0.0.1-SNAPSHOT.jar
    ```
    在Spring Boot中，你也可以在`application.properties`里设置生效的profile，例如：
    ```java
    spring.profiles.active=production
    ```
    通过这种方式设置的值会被系统属性或环境变量替换，但不会被`SpringApplicationBuilder.profiles()`方法替换。因此，后面的Java API
    可用来在不改变默认设置的情况下增加profiles。
    
    想要获取更多信息可查看'Spring Boot特性'章节的[Chapter 25, Profiles](..//IV. Spring Boot features/25. Profiles.md)。


### 69.7 根据环境改变配置

    一个YAML文件实际上是一系列以`---`线分割的文档，每个文档都被单独解析为一个平坦的（flattened）map。
    
    如果一个YAML文档包含一个`spring.profiles`关键字，那profiles的值（以逗号分割的profiles列表）将被传入Spring的`Environment
    .acceptsProfiles()`方法，并且如果这些profiles的任何一个被激活，对应的文档被包含到最终的合并中（否则不会）。
    
    示例：
    ```json
    server:
        port: 9000
    ---
    
    spring:
        profiles: development
    server:
        port: 9001
    
    ---
    
    spring:
        profiles: production
    server:
        port: 0
    ```
    在这个示例中，默认的端口是`9000`，但如果Spring profile `development`生效则该端口是`9001`，如果`production`生效则它是`0`。
    
    YAML文档以它们出现的顺序合并，所以后面的值会覆盖前面的值。
    
    想要使用profiles文件完成同样的操作，你可以使用`application-${profile}.properties`指定特殊的，profile相关的值。



### 69.8 发现外部属性的内置选项

    Spring Boot在运行时会将来自`application.properties`（或`.yml`）的外部属性绑定到应用，因为不可能将所有支持的属性放到一个地方，
    classpath下的其他jar也有支持的属性。
    
    每个运行中且有Actuator特性的应用都会有一个`configprops`端点，它能够展示所有边界和可通过`@ConfigurationProperties`绑定的属性。
    
    附录中包含一个[application.properties](http://docs.spring.io/spring-boot/docs/1.4.1.RELEASE/reference/htmlsingle/
    #common-application-properties)示例，它列举了Spring Boot支持的大多数常用属性，查看`@ConfigurationProperties`，`@Value`，
    还有不经常使用的`RelaxedEnvironment`的源码可获取最权威的属性列表。
    
###70.1.1 使用Spring bean添加Servlet, Filter或Listener

    想要添加`Servlet`，`Filter`或Servlet`*Listener`，你只需要为它提供一个`@Bean`定义，这种方式很适合注入配置或依赖。不过，需要注意
    的是它们不会导致其他很多beans的热初始化，因为它们需要在应用生命周期的早期进行安装（让它依赖`DataSource`或JPA配置不是好主意），
    你可以通过懒加载突破该限制（在第一次使用时才初始化）。
    
    对于`Filters`或`Servlets`，你可以通过`FilterRegistrationBean`或`ServletRegistrationBean`添加映射和初始化参数。
    
    **注** 在一个filter注册时，如果没指定`dispatcherType`，它将匹配`FORWARD`，`INCLUDE`和`REQUEST`。如果启用异步，它也将匹配
    `ASYNC`。如果迁移`web.xml`中没有`dispatcher`元素的filter，你需要自己指定一个`dispatcherType`：
    ```java
    @Bean
    public FilterRegistrationBean myFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setDispatcherTypes(DispatcherType.REQUEST);
        ....
    
        return registration;
    }
    ```
    
    **禁止Servlet或Filter的注册**
    
    如上所述，任何`Servlet`或`Filter` beans都将自动注册到servlet容器。不过，为特定的`Filter`或`Servlet` bean创建一个registration，
    并将它标记为disabled，可以禁用该filter或servlet。例如：
    ```java
    @Bean
    public FilterRegistrationBean registration(MyFilter filter) {
        FilterRegistrationBean registration = new FilterRegistrationBean(filter);
        registration.setEnabled(false);
        return registration;
    }
    ```
    
### 70.5 配置SSL

    你可以以声明方式配置SSL，一般通过在`application.properties`或`application.yml`设置各种各样的`server.ssl.*`属性，例如：
    ```json
    server.port = 8443
    server.ssl.key-store = classpath:keystore.jks
    server.ssl.key-store-password = secret
    server.ssl.key-password = another-secret
    ```
    查看[Ssl](https://github.com/spring-projects/spring-boot/tree/v1.4.1.RELEASE/spring-boot/src/main/java/org/
    springframework/boot/context/embedded/Ssl.java)获取所有支持的配置。
    
    使用类似于以上示例的配置意味着该应用将不支持端口为8080的普通HTTP连接。Spring Boot不支持通过`application.properties`同时配置
    HTTP连接器和HTTPS连接器。如果你两个都想要，那就需要以编程的方式配置它们中的一个。推荐使用`application.properties`配置HTTPS，
    因为HTTP连接器是两个中最容易以编程方式进行配置的，查看[spring-boot-sample-tomcat-multi-connectors](https://github.com/
    spring-projects/spring-boot/tree/v1.4.1.RELEASE/spring-boot-samples/spring-boot-sample-tomcat-multi-connectors)
    可获取示例项目。


###70.6 配置访问日志

    通过相应的命令空间可以为Tomcat和Undertow配置访问日志，例如下面是为Tomcat配置的一个[自定义模式](https://tomcat.apache.org/
    tomcat-8.0-doc/config/valve.html#Access_Logging)的访问日志：
    ```properties
    server.tomcat.basedir=my-tomcat
    server.tomcat.accesslog.enabled=true
    server.tomcat.accesslog.pattern=%t %a "%r" %s (%D ms)
    ```
    **注** 日志默认路径为tomcat基础路径下的`logs`目录，该dir默认是个临时目录，所以你可能想改变Tomcat的base目录或为日志指定绝对路径。
    上述示例中，你可以在相对于应用工作目录的`my-tomcat/logs`访问到日志。
    
    Undertow的访问日志配置方式类似：
    ```properties
    server.undertow.accesslog.enabled=true
    server.undertow.accesslog.pattern=%t %a "%r" %s (%D ms)
    ```
    日志存储在相对于应用工作目录的`logs`目录下，可以通过`server.undertow.accesslog.directory`自定义。


###70.7.1 自定义Tomcat代理配置

    如果使用的是Tomcat，你可以配置用于传输"forwarded"信息的headers名：
    ```properties
    server.tomcat.remote-ip-header=x-your-remote-ip-header
    server.tomcat.protocol-header=x-your-protocol-header
    ```
    你也可以为Tomcat配置一个默认的正则表达式，用来匹配内部信任的代理。默认情况下，IP地址`10/8`，`192.168/16`，
    `169.254/16`和`127/8`是被信任的。通过设置`server.tomcat.internal-proxies`属性可以自定义，比如：
    ```properties
    server.tomcat.internal-proxies=192\\.168\\.\\d{1,3}\\.\\d{1,3}
    ```
    **注** 只有在使用配置文件时才需要双反斜线，如果使用YAML，只需要单个反斜线，比如`192\.168\.\d{1,3}\.\d{1,3}`。
    
    **注** 将`internal-proxies`设置为空表示信任所有代理，不要在生产环境使用。
    
    你可以完全控制Tomcat的`RemoteIpValve`配置，只要关掉自动配置（比如设置`server.use-forward-headers=false`）
    并在`TomcatEmbeddedServletContainerFactory` bean添加一个新value实例。

###70.7 在前端代理服务器后使用

    你的应用可能需要发送`302`跳转或使用指向自己的绝对路径渲染内容。当在代理服务器后面运行时，调用者需要的是代理服务器链接而不是部署应用的
    实际物理机器地址，通常的解决方式是代理服务器将前端地址放到headers并告诉后端服务器如何拼装链接。
    
    如果代理添加约定的`X-Forwarded-For`和`X-Forwarded-Proto` headers（大多数都是开箱即用的），只要将`application.properties`
    中的`server.use-forward-headers`设置为`true`，绝对链接就能正确的渲染。
    
    **注** 如果应用运行在Cloud Foundry或Heroku，`server.use-forward-headers`属性没指定的话默认为`true`，其他实例默认为`false`。
    
### 70.9 启用Tomcat的多连接器

    你可以将`org.apache.catalina.connector.Connector`添加到`TomcatEmbeddedServletContainerFactory`，这就能够允许多连接器，
    比如HTTP和HTTPS连接器：
    ```java
    @Bean
    public EmbeddedServletContainerFactory servletContainer() {
        TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory();
        tomcat.addAdditionalTomcatConnectors(createSslConnector());
        return tomcat;
    }
    
    private Connector createSslConnector() {
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        Http11NioProtocol protocol = (Http11NioProtocol) connector.getProtocolHandler();
        try {
            File keystore = new ClassPathResource("keystore").getFile();
            File truststore = new ClassPathResource("keystore").getFile();
            connector.setScheme("https");
            connector.setSecure(true);
            connector.setPort(8443);
            protocol.setSSLEnabled(true);
            protocol.setKeystoreFile(keystore.getAbsolutePath());
            protocol.setKeystorePass("changeit");
            protocol.setTruststoreFile(truststore.getAbsolutePath());
            protocol.setTruststorePass("changeit");
            protocol.setKeyAlias("apitester");
            return connector;
        }
        catch (IOException ex) {
            throw new IllegalStateException("can't access keystore: [" + "keystore"
                    + "] or truststore: [" + "keystore" + "]", ex);
        }
    }
    
    ```
    
### 70.15 启用Undertow的多监听器

    将`UndertowBuilderCustomizer`添加到`UndertowEmbeddedServletContainerFactory`，然后使用`Builder`添加一个listener：
    ```java
    @Bean
    public UndertowEmbeddedServletContainerFactory embeddedServletContainerFactory() {
        UndertowEmbeddedServletContainerFactory factory = new UndertowEmbeddedServletContainerFactory();
        factory.addBuilderCustomizers(new UndertowBuilderCustomizer() {
    
            @Override
            public void customize(Builder builder) {
                builder.addHttpListener(8080, "0.0.0.0");
            }
    
        });
        return factory;
    }
    ```


### 70.20 启用HTTP响应压缩

    Jetty，Tomcat和Undertow支持HTTP响应压缩，你可以通过设置`server.compression.enabled`启用它：
    ```properties
    server.compression.enabled=true
    ```
    默认情况下，响应信息长度至少2048字节才能触发压缩，通过`server.compression.min-response-size`属性可以改变该长度。另外，
    只有响应的content type为以下其中之一时才压缩：
    
    - `text/html`
    - `text/xml`
    - `text/plain`
    - `text/css`
    
    你可以通过`server.compression.mime-types`属性配置。
    
        
### 71.1 编写JSON REST服务

    只要添加的有Jackson2依赖，Spring Boot应用中的任何`@RestController`默认都会渲染为JSON响应，例如：
    ```java
    @RestController
    public class MyController {
    
        @RequestMapping("/thing")
        public MyThing thing() {
                return new MyThing();
        }
    
    }
    ```
    只要`MyThing`能够通过Jackson2序列化（比如，一个标准的POJO或Groovy对象），默认[localhost:8080/thing](http://localhost:8080/
    thing)将响应一个JSON数据。有时在浏览器中你可能看到XML响应，因为浏览器倾向于发送XML accept headers。

    
### 71.2 编写XML REST服务

    如果classpath下存在Jackson XML扩展（`jackson-dataformat-xml`），它会被用来渲染XML响应，示例和JSON的非常相似。想要使用它，
    只需为你的项目添加以下依赖：
    ```xml
    <dependency>
        <groupId>com.fasterxml.jackson.dataformat</groupId>
        <artifactId>jackson-dataformat-xml</artifactId>
    </dependency>
    ```
    你可能还需要添加Woodstox的依赖，它比JDK提供的默认StAX实现快很多，并且支持良好的格式化输出，提高了namespace处理能力：
    ```xml
    <dependency>
        <groupId>org.codehaus.woodstox</groupId>
        <artifactId>woodstox-core-asl</artifactId>
    </dependency>
    ```
    如果Jackson的XML扩展不可用，Spring Boot将使用JAXB（JDK默认提供），不过`MyThing`需要注解`@XmlRootElement`：
    ```java
    @XmlRootElement
    public class MyThing {
        private String name;
        // .. getters and setters
    }
    ```
    想要服务器渲染XML而不是JSON，你可能需要发送一个`Accept: text/xml`头部（或使用浏览器）。

### 71.3 自定义Jackson ObjectMapper

    在一个HTTP交互中，Spring MVC（客户端和服务端）使用`HttpMessageConverters`协商内容转换。如果classpath下存在Jackson，
    你就获取到`Jackson2ObjectMapperBuilder`提供的默认转换器，这是Spring Boot为你自动配置的实例。
    
    创建的`ObjectMapper`（或用于Jackson XML转换的`XmlMapper`）实例默认有以下自定义属性：
    
    - `MapperFeature.DEFAULT_VIEW_INCLUSION`，默认是禁用的
    - `DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES`，默认是禁用的
    
    Spring Boot也有一些用于简化自定义该行为的特性。
    
    你可以使用当前的environment配置`ObjectMapper`和`XmlMapper`实例。Jackson提供一个扩展套件，可以用来关闭或开启一些特性，
    你可以用它们配置Jackson以处理不同方面。这些特性在Jackson中是使用6个枚举进行描述的，并被映射到environment的属性上：
    
    |Jackson枚举|Environment属性|
    |------|:-------|
    |`com.fasterxml.jackson.databind.DeserializationFeature`|`spring.jackson.deserialization.<feature_name>=true|false`|
    |`com.fasterxml.jackson.core.JsonGenerator.Feature`|`spring.jackson.generator.<feature_name>=true|false`|
    |`com.fasterxml.jackson.databind.MapperFeature`|`spring.jackson.mapper.<feature_name>=true|false`|
    |`com.fasterxml.jackson.core.JsonParser.Feature`|`spring.jackson.parser.<feature_name>=true|false`|
    |`com.fasterxml.jackson.databind.SerializationFeature`|`spring.jackson.serialization.<feature_name>=true|false`|
    |`com.fasterxml.jackson.annotation.JsonInclude.Include`|`spring.jackson.serialization-inclusion=always|non_null|
    non_absent|non_default|non_empty`|
    
    例如，设置`spring.jackson.serialization.indent_output=true`可以美化打印输出（pretty print）。注意，由于[松散绑定]
    (../IV. Spring Boot features/24.7.2. Relaxed binding.md)的使用，`indent_output`不必匹配对应的枚举常量`INDENT_OUTPUT`。
    
    基于environment的配置会应用到自动配置的`Jackson2ObjectMapperBuilder` bean，然后应用到通过该builder创建的mappers，包括自动
    配置的`ObjectMapper` bean。
    
    `ApplicationContext`中的`Jackson2ObjectMapperBuilder`可以通过`Jackson2ObjectMapperBuilderCustomizer` bean自定义。
    这些customizer beans可以排序，Spring Boot自己的customizer序号为0，其他自定义可以应用到Spring Boot自定义之前或之后。
    
    所有类型为`com.fasterxml.jackson.databind.Module`的beans都会自动注册到自动配置的`Jackson2ObjectMapperBuilder`，并应用到
    它创建的任何`ObjectMapper`实例。这提供了一种全局机制，用于在为应用添加新特性时贡献自定义模块。
    
    如果想完全替换默认的`ObjectMapper`，你既可以定义该类型的`@Bean`并注解`@Primary`，也可以定义`Jackson2ObjectMapperBuilder` 
    `@Bean`，通过builder构建。注意不管哪种方式都会禁用所有的自动配置`ObjectMapper`。
    
    如果你提供`MappingJackson2HttpMessageConverter`类型的`@Bean`，它们将替换MVC配置中的默认值。Spring Boot也提供了一个
    `HttpMessageConverters`类型的便利bean（如果你使用MVC默认配置，那它就总是可用的），它提供了一些有用的方法来获取默认和用户增强的
    消息转换器（message converters）。具体详情可参考[Section 71.4, “Customize the @ResponseBody rendering”](./71.4 Customize
     the @ResponseBody rendering.md)及[WebMvcAutoConfiguration](https://github.com/spring-projects/spring-boot/tree/
     v1.4.1.RELEASE/spring-boot-autoconfigure/src/main/java/org/springframework/boot/autoconfigure/web/WebMvcAutoConfiguration.java)源码。
    
        
### 71.4 自定义@ResponseBody渲染

    Spring使用`HttpMessageConverters`渲染`@ResponseBody`（或来自`@RestController`的响应），你可以通过在Spring Boot上下文中添加
    该类型的beans来贡献其他的转换器。如果你添加的bean类型默认已经包含了（像用于JSON转换的`MappingJackson2HttpMessageConverter`），
    那它将替换默认的。Spring Boot提供一个方便的`HttpMessageConverters`类型的bean，它有一些有用的方法可以访问默认的和用户增强的message
    转换器（比如你想要手动将它们注入到一个自定义的`RestTemplate`时就很有用）。
    
    在通常的MVC用例中，任何你提供的`WebMvcConfigurerAdapter` beans通过覆盖`configureMessageConverters`方法也能贡献转换器，
    但不同于通常的MVC，你可以只提供你需要的转换器（因为Spring Boot使用相同的机制来贡献它默认的转换器）。最终，如果你通过提供自己的`
     @EnableWebMvc`注解覆盖Spring Boot默认的MVC配置，那你就可以完全控制，并使用来自`WebMvcConfigurationSupport`的`getMessageConverters`手动做任何事。
    
    更多详情可参考[WebMvcAutoConfiguration](https://github.com/spring-projects/spring-boot/tree/v1.4.1.RELEASE/
    spring-boot-autoconfigure/src/main/java/org/springframework/boot/autoconfigure/web/WebMvcAutoConfiguration.java)源码。

### 71.5 处理Multipart文件上传

    Spring Boot采用Servlet 3 `javax.servlet.http.Part` API来支持文件上传。默认情况下，Spring Boot配置Spring MVC在单个请求中只
    处理每个文件最大1Mb，最多10Mb的文件数据。你可以覆盖那些值，也可以设置临时文件存储的位置（比如，存储到`/tmp`文件夹下）及传递数据刷新
    到磁盘的阀值（通过使用`MultipartProperties`类暴露的属性）。如果你需要设置文件不受限制，可以设置`spring.http.multipart.max-file-size`属性值为`-1`。
    
    当你想要接收multipart编码文件数据作为Spring MVC控制器（controller）处理方法中被`@RequestParam`注解的`MultipartFile`类型的
    参数时，multipart支持就非常有用了。
    
    更多详情可参考[MultipartAutoConfiguration](https://github.com/spring-projects/spring-boot/tree/v1.4.1.RELEASE/
    spring-boot-autoconfigure/src/main/java/org/springframework/boot/autoconfigure/web/MultipartAutoConfiguration.java)源码。


### 71.8 自定义ViewResolvers

    `ViewResolver`是Spring MVC的核心组件，它负责转换`@Controller`中的视图名称到实际的`View`实现。注意`ViewResolvers`主要用在
    UI应用中，而不是REST风格的服务（`View`不是用来渲染`@ResponseBody`的）。Spring有很多你可以选择的`ViewResolver`实现，并且Spring
    自己对如何选择相应实现也没发表意见。另一方面，Spring Boot会根据classpath上的依赖和应用上下文为你安装一或两个`ViewResolver`实现。
    `DispatcherServlet`使用所有在应用上下文中找到的解析器（resolvers），并依次尝试每一个直到它获取到结果，所以如果你正在添加自己的
    解析器，那就要小心顺序和你的解析器添加的位置。
    
    `WebMvcAutoConfiguration`将会为你的上下文添加以下`ViewResolvers`：
    
    - bean id为`defaultViewResolver`的`InternalResourceViewResolver`，它会定位可以使用`DefaultServlet`渲染的物理资源（比如
    静态资源和JSP页面）。它在视图名上应用了一个前缀和后缀（默认都为空，但你可以通过`spring.view.prefix`和`spring.view.suffix`设置），
    然后查找在servlet上下文中具有该路径的物理资源，可以通过提供相同类型的bean覆盖它。
    - id为`beanNameViewResolver`的`BeanNameViewResolver`，它是视图解析器链的一个非常有用的成员，可以在`View`解析时收集任何具有
    相同名称的beans，没必要覆盖或替换它。
    - id为`viewResolver`的`ContentNegotiatingViewResolver`，它只会在实际`View`类型的beans出现时添加。这是一个'master'解析器，
    它的职责会代理给其他解析器，它会尝试找到客户端发送的一个匹配'Accept'的HTTP头部。这有一篇关于[ContentNegotiatingViewResolver]
    (https://spring.io/blog/2013/06/03/content-negotiation-using-views)的博客，你也可以也查看下源码。通过定义一个名叫
    'viewResolver'的bean，你可以关闭自动配置的`ContentNegotiatingViewResolver`。
    - 如果使用Thymeleaf，你将有一个id为`thymeleafViewResolver`的`ThymeleafViewResolver`，它会通过加前缀和后缀的视图名来查找
    资源（外部配置为`spring.thymeleaf.prefix`和`spring.thymeleaf.suffix`，对应的默认为'classpath:/templates/'和'.html'）。
    你可以通过提供相同名称的bean来覆盖它。
    - 如果使用FreeMarker，你将有一个id为`freeMarkerViewResolver`的`FreeMarkerViewResolver`，它会使用加前缀和后缀（外部配置为
    `spring.freemarker.prefix`和`spring.freemarker.suffix`，对应的默认值为空和'.ftl'）的视图名从加载路径（外部配置为
    `spring.freemarker.templateLoaderPath`，默认为'classpath:/templates/'）下查找资源。你可以通过提供相同名称的bean来覆盖它。
    - 如果使用Groovy模板（实际上只要你把groovy-templates添加到classpath下），你将有一个id为`groovyTemplateViewResolver`的
    `Groovy TemplateViewResolver`，它会使用加前缀和后缀（外部属性为`spring.groovy.template.prefix`和`spring.groovy.
    template.suffix`，对应的默认值为'classpath:/templates/'和'.tpl'）的视图名从加载路径下查找资源。你可以通过提供相同名称的bean来覆盖它。
    - 如果使用Velocity，你将有一个id为`velocityViewResolver`的`VelocityViewResolver`，它会使用加前缀和后缀（外部属性为
    `spring.velocity.prefix`和`spring.velocity.suffix`，对应的默认值为空和'.vm'）的视图名从加载路径（外部属性为`spring.
    velocity.resourceLoaderPath`，默认为'classpath:/templates/'）下查找资源。你可以通过提供相同名称的bean来覆盖它。
    
    更多详情可查看源码：  [WebMvcAutoConfiguration](https://github.com/spring-projects/spring-boot/tree/v1.4.1.RELEASE/
    spring-boot-autoconfigure/src/main/java/org/springframework/boot/autoconfigure/web/WebMvcAutoConfiguration.java)，
    [ThymeleafAutoConfiguration](https://github.com/spring-projects/spring-boot/tree/v1.4.1.RELEASE/
    spring-boot-autoconfigure/src/main/java/org/springframework/boot/autoconfigure/thymeleaf/
    ThymeleafAutoConfiguration.java)，[FreeMarkerAutoConfiguration](https://github.com/spring-projects/spring-boot/
    tree/v1.4.1.RELEASE/spring-boot-autoconfigure/src/main/java/org/springframework/boot/autoconfigure/freemarker/
    FreeMarkerAutoConfiguration.java)，[GroovyTemplateAutoConfiguration](https://github.com/spring-projects/spring-boot/
    tree/v1.4.1.RELEASE/spring-boot-autoconfigure/src/main/java/org/springframework/boot/autoconfigure/groovy/template/
    GroovyTemplateAutoConfiguration.java)，[VelocityAutoConfiguration](https://github.com/spring-projects/spring-boot/
    tree/v1.4.1.RELEASE/spring-boot-autoconfigure/src/main/java/org/springframework/boot/autoconfigure/velocity/
    VelocityAutoConfiguration.java)。

###71.9 Velocity

    默认情况下，Spring Boot会配置一个`VelocityViewResolver`，如果需要的是`VelocityLayoutViewResolver`，你可以自己创建一个名
    为`velocityViewResolver`的bean。你也可以将`VelocityProperties`实例注入到自定义视图解析器以获取基本的默认设置。
    
    以下示例使用`VelocityLayoutViewResolver`替换自动配置的velocity视图解析器，并自定义`layoutUrl`及应用所有自动配置的属性：
    ```java
    @Bean(name = "velocityViewResolver")
    public VelocityLayoutViewResolver velocityViewResolver(VelocityProperties properties) {
        VelocityLayoutViewResolver resolver = new VelocityLayoutViewResolver();
        properties.applyToViewResolver(resolver);
        resolver.setLayoutUrl("layout/default.vm");
        return resolver;
    }
    ```
    
###71.10 使用Thymeleaf 3

    默认情况下，`spring-boot-starter-thymeleaf`使用的是Thymeleaf 2.1，你可以通过覆盖`thymeleaf.version`和
    `thymeleaf-layout-dialect.version`属性使用Thymeleaf 3，例如：
    ```properties
    <properties>
        <thymeleaf.version>3.0.0.RELEASE</thymeleaf.version>
        <thymeleaf-layout-dialect.version>2.0.0</thymeleaf-layout-dialect.version>
    </dependency>
    ```
    为了避免关于HTML 5模板模式过期，将使用HTML模板模式的警告提醒，你需要显式配置`spring.thymeleaf.mode`为`HTML`，例如：
    ```properties
    spring.thymeleaf.mode: HTML
    ```
    具体操作可查看[Thymeleaf 3示例](https://github.com/spring-projects/spring-boot/tree/v1.4.1.RELEASE/spring-boot-samples/
    spring-boot-sample-web-thymeleaf3)。
    
    如果正在使用其他自动配置的Thymeleaf附加组件（Spring Security，Data Attribute或Java 8 Time），你需要使用兼容Thymeleaf 3.0的
    版本覆盖它们现在的版本。
    
    
###72.1 配置RestTemplate使用代理

    正如[Section 33.1, “RestTemplate customization”](../IV. Spring Boot features/33.1 RestTemplate customization.md)
    描述的那样，你可以使用`RestTemplateCustomizer`和`RestTemplateBuilder`构建一个自定义的`RestTemplate`，这是创建使用代理的
    `RestTemplate`的推荐方式。
    
    代理配置的确切细节取决于底层使用的客户端请求factory，这里有个示例演示`HttpClient`配置的`HttpComponentsClientRequestFactory`
    对所有hosts都使用代理，除了`192.168.0.5`。
    ```java
    static class ProxyCustomizer implements RestTemplateCustomizer {
    
        @Override
        public void customize(RestTemplate restTemplate) {
            HttpHost proxy = new HttpHost("proxy.example.com");
            HttpClient httpClient = HttpClientBuilder.create()
                    .setRoutePlanner(new DefaultProxyRoutePlanner(proxy) {
    
                        @Override
                        public HttpHost determineProxy(HttpHost target,
                                HttpRequest request, HttpContext context)
                                        throws HttpException {
                            if (target.getHostName().equals("192.168.0.5")) {
                                return null;
                            }
                            return super.determineProxy(target, request, context);
                        }
    
                    }).build();
            restTemplate.setRequestFactory(
                    new HttpComponentsClientHttpRequestFactory(httpClient));
        }
    
    }
    ```

### 73. 日志

    Spring Boot除了`commons-logging`API外没有其他强制性的日志依赖，你有很多可选的日志实现。想要使用[Logback](http://
    logback.qos.ch/)，你需要包含它及`jcl-over-slf4j`（它实现了Commons Logging API）。最简单的方式是通过依赖
    `spring-boot-starter-logging`的starters。对于一个web应用程序，你只需添加`spring-boot-starter-web`依赖，因为它依赖于
    logging starter。例如，使用Maven：
    ```xml
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    ```
    Spring Boot有一个`LoggingSystem`抽象，用于尝试通过classpath上下文配置日志系统。如果Logback可用，则首选它。如果你唯一
    需要做的就是设置不同日志级别，那可以通过在`application.properties`中使用`logging.level`前缀实现，比如：
    ```java
    logging.level.org.springframework.web=DEBUG
    logging.level.org.hibernate=ERROR
    ```
    你也可以使用`logging.file`设置日志文件的位置（除控制台之外，默认会输出到控制台）。
    
    想要对日志系统进行更细粒度的配置，你需要使用`LoggingSystem`支持的原生配置格式。默认情况下，Spring Boot从系统的默认位置
    加载原生配置（比如对于Logback为`classpath:logback.xml`），但你可以使用`logging.config`属性设置配置文件的位置。


###73.1.1 配置logback只输出到文件

    如果想禁用控制台日志记录，只将输出写入文件中，你需要一个只导入`file-appender.xml`而不是`console-appender.xml`
    的自定义`logback-spring.xml`：
    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <configuration>
        <include resource="org/springframework/boot/logging/logback/defaults.xml" />
        <property name="LOG_FILE" value="${LOG_FILE:-${LOG_PATH:-${LOG_TEMP:-${java.io.tmpdir:-/tmp}}/}
        spring.log}"/>
        <include resource="org/springframework/boot/logging/logback/file-appender.xml" />
        <root level="INFO">
            <appender-ref ref="FILE" />
        </root>
    </configuration>
    ```
    你还需要将`logging.file`添加到`application.properties`：
    ```properties
    logging.file=myapplication.log
    ```
    
### 73.1 配置Logback

    如果你将`logback.xml`放到classpath根目录下，那它将会被从这加载（或`logback-spring.xml`充分利用Boot提供的模板特性）。
    Spring Boot提供一个默认的基本配置，如果你只是设置日志级别，那你可以包含它，比如：
    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <configuration>
        <include resource="org/springframework/boot/logging/logback/base.xml"/>
        <logger name="org.springframework.web" level="DEBUG"/>
    </configuration>
    ```
    如果查看spring-boot jar中的`base.xml`，你将会看到`LoggingSystem`为你创建的很多有用的系统属性，比如：
    - `${PID}`，当前进程id。
    - `${LOG_FILE}`，如果在Boot外部配置中设置了`logging.file`。
    - `${LOG_PATH}`，如果设置了`logging.path`（表示日志文件产生的目录）。
    - `${LOG_EXCEPTION_CONVERSION_WORD}`，如果在Boot外部配置中设置了`logging.exception-conversion-word`。
    
    Spring Boot也提供使用自定义的Logback转换器在控制台上输出一些漂亮的彩色ANSI日志信息（不是日志文件），具体参考默认的`base.xml`配置。
    
    如果Groovy在classpath下，你也可以使用`logback.groovy`配置Logback。


### 73.2.1 使用YAML或JSON配置Log4j2

    除了它的默认XML配置格式，Log4j 2也支持YAML和JSON配置文件。想使用其他配置文件格式配置Log4j 2，你需要添加合适的依赖到classpath，
    并以匹配所选格式的方式命名配置文件：
    
    |格式|依赖|文件名|
    |:----|:----|:---|
    |YAML|`com.fasterxml.jackson.core:jackson-databind` `com.fasterxml.jackson.dataformat:jackson-dataformat-yaml`|
    `log4j2.yaml` `log4j2.yml`|
    |JSON|`com.fasterxml.jackson.core:jackson-databind`|`log4j2.json` `log4j2.jsn`|

### 73.2 配置Log4j

    如果[Log4j 2](http://logging.apache.org/log4j/2.x)出现在classpath下，Spring Boot会将其作为日志配置。如果你正在使用starters
    进行依赖装配，这意味着你需要排除Logback，然后包含log4j 2。如果不使用starters，除了添加Log4j 2，你还需要提供`jcl-over-slf4j`依赖（至少）。
    
    最简单的方式可能就是通过starters，尽管它需要排除一些依赖，比如，在Maven中：
    ```xml
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter</artifactId>
        <exclusions>
            <exclusion>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-logging</artifactId>
            </exclusion>
        </exclusions>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-log4j2</artifactId>
    </dependency>
    ```
    
    **注** Log4j starters会收集好依赖以满足普通日志记录的需求（比如，Tomcat中使用`java.util.logging`，但使用Log4j 2作为输出），
    具体查看Actuator Log4j 2的示例，了解如何将它用于实战。


### 74.1 配置数据源

    自定义`DataSource`类型的`@Bean`可以覆盖默认设置，正如[Section 24.7.1, “Third-party configuration”](../IV. Spring Boot 
    features/24.7.1. Third-party configuration.md)解释的那样，你可以很轻松的将它跟一系列`Environment`属性绑定：
    ```java
    @Bean
    @ConfigurationProperties(prefix="datasource.fancy")
    public DataSource dataSource() {
        return new FancyDataSource();
    }
    ```
    ```properties
    datasource.fancy.jdbcUrl=jdbc:h2:mem:mydb
    datasource.fancy.username=sa
    datasource.fancy.poolSize=30
    ```
    Spring Boot也提供了一个工具类`DataSourceBuilder`用来创建标准的数据源。如果需要重用`DataSourceProperties`的配置，你可以从
    它初始化一个`DataSourceBuilder`：
    ```java
    @Bean
    @ConfigurationProperties(prefix="datasource.mine")
    public DataSource dataSource(DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder()
                // additional customizations
                .build();
    }
    ```
    在此场景中，你保留了通过Spring Boot暴露的标准属性，通过添加`@ConfigurationProperties`，你可以暴露在相应的命命名空间暴露其他
    特定实现的配置，
    具体详情可参考'Spring Boot特性'章节中的[Section 29.1, “Configure a DataSource”](../IV. Spring Boot features/29.1. 
    Configure a DataSource.md)和[DataSourceAutoConfiguration](https://github.com/spring-projects/spring-boot/tree/
    v1.4.1.RELEASE/spring-boot-autoconfigure/src/main/java/org/springframework/boot/autoconfigure/jdbc/
    DataSourceAutoConfiguration.java)类源码。
    
    
    
### 74.2 配置两个数据源

    创建多个数据源和创建一个工作都是一样的，如果使用JDBC或JPA的默认自动配置，你需要将其中一个设置为`@Primary`（然后它就能被任何
    `@Autowired`注入获取）。
    ```java
    @Bean
    @Primary
    @ConfigurationProperties(prefix="datasource.primary")
    public DataSource primaryDataSource() {
        return DataSourceBuilder.create().build();
    }
    
    @Bean
    @ConfigurationProperties(prefix="datasource.secondary")
    public DataSource secondaryDataSource() {
        return DataSourceBuilder.create().build();
    }
    ```
    
### 74.3 使用Spring Data仓库

    Spring Data可以为你的`@Repository`接口创建各种风格的实现。Spring Boot会为你处理所有事情，只要那些`@Repositories`接口跟你的
    `@EnableAutoConfiguration`类处于相同的包（或子包）。
    
    对于很多应用来说，你需要做的就是将正确的Spring Data依赖添加到classpath下（JPA对应`spring-boot-starter-data-jpa`，
    Mongodb对应`spring-boot-starter-data-mongodb`），创建一些repository接口来处理`@Entity`对象，相应示例可参考[JPA sample](https://github.com/spring-projects/spring-boot/tree/v1.4.1.RELEASE/spring-boot-samples/spring-boot-sample-data-jpa)或[Mongodb sample](https://github.com/spring-projects/spring-boot/tree/v1.4.1.RELEASE/spring-boot-samples/spring-boot-sample-data-mongodb)。
    
    Spring Boot会基于它找到的`@EnableAutoConfiguration`来尝试猜测你的`@Repository`定义的位置。想要获取更多控制，可以使用
    `@EnableJpaRepositories`注解（来自Spring Data JPA）。

### 74.4 从Spring配置分离`@Entity`定义

    Spring Boot会基于它找到的`@EnableAutoConfiguration`来尝试猜测`@Entity`定义的位置，想要获取更多控制可以使用`@EntityScan`注解，比如：
    ```java
    @Configuration
    @EnableAutoConfiguration
    @EntityScan(basePackageClasses=City.class)
    public class Application {
    
        //...
    
    }
    ```
    
### 74.5 配置JPA属性

    Spring Data JPA已经提供了一些独立的配置选项（比如，针对SQL日志），并且Spring Boot会暴露它们，针对hibernate的外部配置属性也更多些，
    最常见的选项如下：
    ```java
    spring.jpa.hibernate.ddl-auto=create-drop
    spring.jpa.hibernate.naming.physical-strategy=com.example.MyPhysicalNamingStrategy
    spring.jpa.database=H2
    spring.jpa.show-sql=true
    ```
    `ddl-auto`配置是个特殊情况，它的默认设置取决于是否使用内嵌数据库（是则默认值为`create-drop`，否则为`none`）。当本地
    `EntityManagerFactory`被创建时，所有`spring.jpa.properties.*`属性都被作为正常的JPA属性（去掉前缀）传递进去了。
    
    Spring Boot提供一致的命名策略，不管你使用什么Hibernate版本。如果使用Hibernate 4，你可以使用`spring.jpa.hibernate.naming.strategy`
    进行自定义；Hibernate 5定义一个`Physical`和`Implicit`命名策略：Spring Boot默认配置`SpringPhysicalNamingStrategy`，
    该实现提供跟Hibernate 4相同的表结构。如果你情愿使用Hibernate 5默认的，可以设置以下属性：
    ```properties
    spring.jpa.hibernate.naming.physical-strategy=org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl
    ```
    具体详情可参考[HibernateJpaAutoConfiguration](https://github.com/spring-projects/spring-boot/tree/v1.4.1.RELEASE/
    spring-boot-autoconfigure/src/main/java/org/springframework/boot/autoconfigure/orm/jpa/
    HibernateJpaAutoConfiguration.java)和[JpaBaseConfiguration](https://github.com/spring-projects/spring-boot/tree/
    v1.4.1.RELEASE/spring-boot-autoconfigure/src/main/java/org/springframework/boot/autoconfigure/orm/jpa/JpaBaseConfiguration.java)。


### 74.6 使用自定义EntityManagerFactory

    为了完全控制`EntityManagerFactory`的配置，你需要添加一个名为`entityManagerFactory`的`@Bean`，Spring Boot自动配置会根据是否存
    在该类型的bean来关闭它的实体管理器（entity manager）。

### 74.7 使用两个EntityManagers

    即使默认的`EntityManagerFactory`工作的很好，你也需要定义一个新的`EntityManagerFactory`，因为一旦出现第二个该类型的bean，
    默认的将会被关闭。为了轻松的实现该操作，你可以使用Spring Boot提供的`EntityManagerBuilder`，或者如果你喜欢的话可以直接使用来自
    Spring ORM的`LocalContainerEntityManagerFactoryBean`。
    
    示例：
    ```java
    // add two data sources configured as above
    
    @Bean
    public LocalContainerEntityManagerFactoryBean customerEntityManagerFactory(
            EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(customerDataSource())
                .packages(Customer.class)
                .persistenceUnit("customers")
                .build();
    }
    
    @Bean
    public LocalContainerEntityManagerFactoryBean orderEntityManagerFactory(
            EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(orderDataSource())
                .packages(Order.class)
                .persistenceUnit("orders")
                .build();
    }
    ```
    上面的配置靠自己基本可以运行，想要完成作品你还需要为两个`EntityManagers`配置`TransactionManagers`。其中的一个会被
    Spring Boot默认的`JpaTransactionManager`获取，如果你将它标记为`@Primary`。另一个需要显式注入到一个新实例。或你可以使用一个
    JTA事物管理器生成它两个。
    
    如果使用Spring Data，你需要相应地需要配置`@EnableJpaRepositories`：
    ```java
    @Configuration
    @EnableJpaRepositories(basePackageClasses = Customer.class,
            entityManagerFactoryRef = "customerEntityManagerFactory")
    public class CustomerConfiguration {
        ...
    }
    
    @Configuration
    @EnableJpaRepositories(basePackageClasses = Order.class,
            entityManagerFactoryRef = "orderEntityManagerFactory")
    public class OrderConfiguration {
        ...
    }
    ```

### 74.8 使用普通的persistence.xml

    Spring不要求使用XML配置JPA提供者（provider），并且Spring Boot假定你想要充分利用该特性。如果你倾向于使用`persistence.xml`，
    那你需要定义你自己的id为`entityManagerFactory`的`LocalEntityManagerFactoryBean`类型的`@Bean`，并在那设置持久化单元的名称，
    默认设置可查看[JpaBaseConfiguration](https://github.com/spring-projects/spring-boot/blob/master/spring-boot-autoconfigure/
    src/main/java/org/springframework/boot/autoconfigure/orm/jpa/JpaBaseConfiguration.java)。


### 74.9 使用Spring Data JPA和Mongo仓库

    Spring Data JPA和Spring Data Mongo都能自动为你创建`Repository`实现。如果它们同时出现在classpath下，你可能需要添加额外的配置来
    告诉Spring Boot你想要哪个（或两个）为你创建仓库。最明确地方式是使用标准的Spring Data `@Enable*Repositories`，然后告诉它你的
    `Repository`接口的位置（此处`*`即可以是Jpa，也可以是Mongo，或者两者都是）。
    
    这里也有`spring.data.*.repositories.enabled`标志，可用来在外部配置中开启或关闭仓库的自动配置，这在你想关闭Mongo仓库但仍使用
    自动配置的`MongoTemplate`时非常有用。
    
    相同的障碍和特性也存在于其他自动配置的Spring Data仓库类型（Elasticsearch, Solr），只需要改变对应注解的名称和标志。
    

### 74.10 将Spring Data仓库暴露为REST端点

    Spring Data REST能够将`Repository`的实现暴露为REST端点，只要该应用启用Spring MVC。Spring Boot暴露一系列来自`spring.data.rest`
    命名空间的有用属性来定制化[RepositoryRestConfiguration](http://docs.spring.io/spring-data/rest/docs/current/api/org/
    springframework/data/rest/core/config/RepositoryRestConfiguration.html)，你可以使用[`RepositoryRestConfigurer`]
    (http://docs.spring.io/spring-data/rest/docs/current/api/org/springframework/data/rest/webmvc/config/
    RepositoryRestConfigurer.html)提供其他定制。


###74.11 配置JPA使用的组件

    如果想配置一个JPA使用的组件，你需要确保该组件在JPA之前初始化。组件如果是Spring Boot自动配置的，Spring Boot会为你处理。例如，
    Flyway是自动配置的，Hibernate依赖于Flyway，这样Hibernate有机会在使用数据库前对其进行初始化。
    
    如果自己配置组件，你可以使用`EntityManagerFactoryDependsOnPostProcessor`子类设置必要的依赖，例如，如果你正使用Hibernate搜索，
    并将Elasticsearch作为它的索引管理器，这样任何`EntityManagerFactory` beans必须设置为依赖`elasticsearchClient` bean：
    ```java
    /**
     * {@link EntityManagerFactoryDependsOnPostProcessor} that ensures that
     * {@link EntityManagerFactory} beans depend on the {@code elasticsearchClient} bean.
     */
    @Configuration
    static class ElasticsearchJpaDependencyConfiguration
            extends EntityManagerFactoryDependsOnPostProcessor {
    
        ElasticsearchJpaDependencyConfiguration() {
            super("elasticsearchClient");
        }
    
    }
    ```



