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






