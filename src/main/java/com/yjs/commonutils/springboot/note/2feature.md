##Application事件和监听器
  
      除了常见的Spring框架事件，比如ContextRefreshedEvent，SpringApplication也会发送其他的application事件。
      
      注 有些事件实际上是在ApplicationContext创建前触发的，所以你不能在那些事件（处理类）中通过@Bean注册监听器，只能通过
      SpringApplication.addListeners(…)或SpringApplicationBuilder.listeners(…)方法注册。如果想让监听器自动注册，而不关心
      应用的创建方式，你可以在工程中添加一个META-INF/spring.factories文件，并使用org.springframework.context.ApplicationListener
      作为key指向那些监听器，如下：
      
      org.springframework.context.ApplicationListener=com.example.project.MyListener
      应用运行时，事件会以下面的次序发送：
      
      在运行开始，但除了监听器注册和初始化以外的任何处理之前，会发送一个ApplicationStartedEvent。
      在Environment将被用于已知的上下文，但在上下文被创建前，会发送一个ApplicationEnvironmentPreparedEvent。
      在refresh开始前，但在bean定义已被加载后，会发送一个ApplicationPreparedEvent。
      在refresh之后，相关的回调处理完，会发送一个ApplicationReadyEvent，表示应用准备好接收请求了。
      启动过程中如果出现异常，会发送一个ApplicationFailedEvent。
      注 通常不需要使用application事件，但知道它们的存在是有用的（在某些场合可能会使用到），比如，在Spring Boot内部会使用事件处理各种任务。
      
##使用ApplicationRunner或CommandLineRunner
  
      如果需要在SpringApplication启动后执行一些特殊的代码，你可以实现ApplicationRunner或CommandLineRunner接口，这两个接口工作
      方式相同，都只提供单一的run方法，该方法仅在SpringApplication.run(…)完成之前调用。
      
      CommandLineRunner接口能够访问string数组类型的应用参数，而ApplicationRunner使用的是上面描述过的ApplicationArguments接口：
      
      import org.springframework.boot.*
      import org.springframework.stereotype.*
      
      @Component
      public class MyBean implements CommandLineRunner {
      
          public void run(String... args) {
              // Do something...
          }
      
      }
      如果某些定义的CommandLineRunner或ApplicationRunner beans需要以特定的顺序调用，你可以实现org.springframework.core.Ordered
      接口或使用org.springframework.core.annotation.Order注解。
      
##Admin特性
    
    通过设置spring.application.admin.enabled属性可以启用管理相关的（admin-related）特性，这将暴露SpringApplicationAdminMXBean到平台的MBeanServer，你可以使用该特性远程管理Spring Boot应用，这对任何service包装器（wrapper）实现也有用。
    
    注 通过local.server.port可以获取该应用运行的HTTP端口。启用该特性时需要注意MBean会暴露一个方法去关闭应用。
    
##加载YAML
  
      Spring框架提供两个便利的类用于加载YAML文档，YamlPropertiesFactoryBean会将YAML加载为Properties，YamlMapFactoryBean会将YAML加载为Map。
      
      例如，下面的YAML文档：
      
      environments:
          dev:
              url: http://dev.bar.com
              name: Developer Setup
          prod:
              url: http://foo.bar.com
              name: My Cool App
      会被转化到这些属性：
      
      environments.dev.url=http://dev.bar.com
      environments.dev.name=Developer Setup
      environments.prod.url=http://foo.bar.com
      environments.prod.name=My Cool App
      YAML列表被表示成使用[index]间接引用作为属性keys的形式，例如下面的YAML：
      
      my:
         servers:
             - dev.bar.com
             - foo.bar.com
      将会转化到这些属性:
      
      my.servers[0]=dev.bar.com
      my.servers[1]=foo.bar.com
      使用Spring DataBinder工具集绑定这些属性（这是@ConfigurationProperties做的事）时，你需要确保目标bean有个java.util.List
      或Set类型的属性，并且需要提供一个setter或使用可变的值初始化它，比如，下面的代码将绑定上面的属性：
      
      @ConfigurationProperties(prefix="my")
      public class Config {
          private List<String> servers = new ArrayList<String>();
          public List<String> getServers() {
              return this.servers;
          }
      }

##在Spring环境中使用YAML暴露属性
  
    YamlPropertySourceLoader类能够将YAML作为PropertySource导出到Sprig Environment，这允许你使用常用的@Value注解
    配合占位符语法访问YAML属性。
    
##YAML缺点
  
    YAML文件不能通过@PropertySource注解加载，如果需要使用该方式，那就必须使用properties文件。
    
## 类型安全的配置属性
  
      使用@Value("${property}")注解注入配置属性有时会比较麻烦（cumbersome），特别是需要使用多个properties，
      或数据本身有层次结构。Spring Boot提供一种使用配置的替代方法，这种方法允许强类型的beans以管理和校验应用的配置，例如：
      
      @Component
      @ConfigurationProperties(prefix="connection")
      public class ConnectionSettings {
          private String username;
          private InetAddress remoteAddress;
          // ... getters and setters
      }
      注 添加setter和getter是相当正确的，因为绑定是通过标准的Java Beans属性描述符进行的，跟Spring MVC一样，对于不可变类型
      或从String强制转换的也一样。只要它们初始化了，maps，collections，arrays只需要getter，setter不是必须的，因为
      绑定者（binder）能够改变它们。如果有setter，maps，collections，arrays就能够被创建。Maps和collections可以仅通过getter
      进行扩展，而arrays需要setter。嵌套的POJO属性只能通过默认的构造器，或接收一个单一的能够转换为string的值的构造器。有些人使用
      Project Lombok自动添加getters和setters。
      
      注 查看@Value和@ConfigurationProperties之间的区别。
      
      你需要在@EnableConfigurationProperties注解中列出要注册的属性类：
      
      @Configuration
      @EnableConfigurationProperties(ConnectionProperties.class)
      public class MyConfiguration {
      }
      注 当@ConfigurationProperties bean以这种方式注册时，该bean将有个约定的名称：<prefix>-<fqn>，<prefix>是
      @ConfigurationProperties注解中定义的environment key前缀，<fqn>是bean的全限定名。如果注解中没有提供任何前缀，那就只使用
      bean的全限定名。上述示例中的bean名称将是connection-com.example.ConnectionProperties，假定ConnectionProperties位于
      com.example包下。
      
      尽管上述配置为ConnectionProperties创建了一个常规的bean，不过我们建议@ConfigurationProperties只用来处理environment（只用于
      注入配置，系统环境之类的），特别是不要注入上下文中的其他beans。话虽如此，@EnableConfigurationProperties注解会自动应用到
      你的项目，任何存在的，注解@ConfigurationProperties的bean将会从Environment属性中得到配置。只要确定ConnectionProperties
      是一个已存在的bean，MyConfiguration就可以不用了。
      
      @Component
      @ConfigurationProperties(prefix="connection")
      public class ConnectionProperties {
      
          // ... getters and setters
      
      }
      这种配置风格跟SpringApplication的外部化YAML配置配合的很好：
      
      # application.yml
      
      connection:
          username: admin
          remoteAddress: 192.168.1.1
      
      # additional configuration as required
      为了使用@ConfigurationProperties beans，你可以像使用其他bean那样注入它们：
      
      @Service
      public class MyService {
      
          private final ConnectionProperties connection;
      
          @Autowired
          public MyService(ConnectionProperties connection) {
              this.connection = connection;
          }
      
           //...
      
          @PostConstruct
          public void openConnection() {
              Server server = new Server();
              this.connection.configure(server);
          }
      
      }
      注 使用@ConfigurationProperties能够产生可被IDEs使用的元数据文件，具体参考[Appendix B,
       Configuration meta-data](../X. Appendices/B. Configuration meta-data.md)。
       
       
##第三方配置
     
     @ConfigurationProperties不仅可以注解在类上，也可以注解在public @Bean方法上，当你需要为不受控的第三方组件绑定属性时，
     该方法将非常有用。
     
     为了从Environment属性中配置一个bean，你需要使用@ConfigurationProperties注解该bean：
     
     @ConfigurationProperties(prefix = "foo")
     @Bean
     public FooComponent fooComponent() {
         ...
     }
     和上面ConnectionSettings的示例方式相同，所有以foo为前缀的属性定义都会被映射到FooComponent上。
     
##Profiles
  
      Spring Profiles提供了一种隔离应用程序配置的方式，并让这些配置只在特定的环境下生效。任何@Component或@Configuration都能注解@Profile，从而限制加载它的时机：
      
      @Configuration
      @Profile("production")
      public class ProductionConfiguration {
      
          // ...
      
      }
      以正常的Spring方式，你可以使用spring.profiles.active的Environment属性来指定哪个配置生效。你可以使用通常的任何方式来指定该属性，例如，可以将它包含到application.properties中：
      
      spring.profiles.active=dev,hsqldb
      或使用命令行开关：
      
      --spring.profiles.active=dev,hsqldb
      
##Color-coded输出
  
      如果你的终端支持ANSI，Spring Boot将使用彩色编码（color output）输出日志以增强可读性，你可以将spring.output.ansi.enabled设置为一个支持的值来覆盖默认设置。
      
      彩色编码（Color coding）使用%clr表达式进行配置，在其最简单的形式中，转换器会根据日志级别使用不同的颜色输出日志，例如：
      
      %clr(%5p)
      日志级别到颜色的映射如下：
      
      Level	Color
      FATAL	Red
      ERROR	Red
      WARN	Yellow
      INFO	Green
      DEBUG	Green
      TRACE	Green
      另外，在转换时你可以设定日志展示的颜色或样式，例如，让文本显示成黄色：
      
      %clr(%d{yyyy-MM-dd HH:mm:ss.SSS}){yellow}
      支持的颜色，样式如下：
      
      blue
      cyan
      faint
      green
      magenta
      red
      yellow
      
##文件输出
  
      默认情况下，Spring Boot只会将日志记录到控制台，而不写进日志文件，如果需要，你可以设置logging.file或logging.path属性
      （例如application.properties）。
      
      下表展示如何组合使用logging.*：
      
      logging.file	logging.path	示例	描述
      (none)	(none)		只记录到控制台
      Specific file	(none)	my.log	写到特定的日志文件，名称可以是精确的位置或相对于当前目录
      (none)	Specific directory	/var/log	写到特定目录下的spring.log里，名称可以是精确的位置或相对于当前目录
      日志文件每达到10M就会被分割，跟控制台一样，默认记录ERROR, WARN和INFO级别的信息。
      
##日志级别
  
      所有Spring Boot支持的日志系统都可以在Spring Environment中设置级别（application.properties里也一样），设置格式为
      'logging.level.*=LEVEL'，其中LEVEL是TRACE, DEBUG, INFO, WARN, ERROR, FATAL, OFF之一：
      
      以下是application.properties示例：
      
      logging.level.root=WARN
      logging.level.org.springframework.web=DEBUG
      logging.level.org.hibernate=ERROR
      注 默认情况，Spring Boot会重新映射Thymeleaf的INFO信息到DEBUG级别，这能减少标准日志输出的噪声。查看LevelRemappingAppender
      可以按自己的配置设置映射。
      
##自定义日志配置
  
      通过将相应的库添加到classpath可以激活各种日志系统，然后在classpath根目录下提供合适的配置文件可以进一步定制日志系统，
      配置文件也可以通过Spring Environment的logging.config属性指定。
      
      使用org.springframework.boot.logging.LoggingSystem系统属性可以强制Spring Boot使用指定的日志系统，该属性值需要是
      LoggingSystem实现类的全限定名，如果值为none，则彻底禁用Spring Boot的日志配置。
      
      注 由于日志初始化早于ApplicationContext的创建，所以不可能通过@PropertySources指定的Spring @Configuration文件控制日志，
      系统属性和Spring Boot外部化配置可以正常工作。
      
      以下文件会根据你选择的日志系统进行加载：
      
      日志系统	定制配置
      Logback	logback-spring.xml,logback-spring.groovy,logback.xml或logback.groovy
      Log4j	log4j.properties或log4j.xml
      Log4j2	log4j2-spring.xml或log4j2.xml
      JDK (Java Util Logging)	logging.properties
      注 如果可能的话，建议你使用-spring变种形式定义日志配置（例如，使用logback-spring.xml而不是logback.xml）。如果你使用标准的
      配置路径，Spring可能不能够完全控制日志初始化。
      
      注 Java Util Logging从可执行jar运行时会导致一些已知的类加载问题，我们建议尽可能不使用它。
      
      以下是从Spring Envrionment转换为System properties的一些有助于定制的配置属性：
      
      Spring Environment	System Property	Comments
      logging.exception-conversion-word	LOG_EXCEPTION_CONVERSION_WORD	记录异常使用的关键字
      logging.file	LOG_FILE	如果指定就会在默认的日志配置中使用
      logging.path	LOG_PATH	如果指定就会在默认的日志配置中使用
      logging.pattern.console	CONSOLE_LOG_PATTERN	日志输出到控制台（stdout）时使用的模式（只支持默认的logback设置）
      logging.pattern.file	FILE_LOG_PATTERN	日志输出到文件时使用的模式（如果LOG_FILE启用，只支持默认的logback设置）
      logging.pattern.level	LOG_LEVEL_PATTERN	用来渲染日志级别的格式（默认%5p，只支持默认的logback设置）
      PID	PID	当前的处理进程(process)ID（能够找到，且还没有用作OS环境变量）
      所有支持的日志系统在解析配置文件时都能获取系统属性的值，具体可以参考spring-boot.jar中的默认配置。
      
      注 如果想在日志属性中使用占位符，你需要使用Spring Boot的语法，而不是底层框架的语法。尤其是使用Logback时，你需要使用:作为属性名
      和默认值的分隔符，而不是:-。
      
      注 通过覆盖LOG_LEVEL_PATTERN（Logback对应logging.pattern.level），你可以向日志中添加MDC和其他ad-hoc的内容。例如，将该值
      设置为logging.pattern.level=user:%X{user} %5p，则默认日志格式将包含一个"user"的MDC实体，如果存在的话，比如：
      
      2015-09-30 12:30:04.031 user:juergen INFO 22174 --- [  nio-8080-exec-0] demo.Controller
      Handling authenticated request
      
##Logback扩展
  
      Spring Boot包含很多有用的Logback扩展，你可以在logback-spring.xml配置文件中使用它们。
      
      注 你不能在标准的logback.xml配置文件中使用扩展，因为它加载的太早了，不过可以使用logback-spring.xml，或指定logging.config属性。
      
##Spring MVC自动配置
  
      Spring Boot为Spring MVC提供的auto-configuration适用于大多数应用，并在Spring默认功能上添加了以下特性：
      
      引入ContentNegotiatingViewResolver和BeanNameViewResolver beans。
      对静态资源的支持，包括对WebJars的支持。
      自动注册Converter，GenericConverter，Formatter beans。
      对HttpMessageConverters的支持。
      自动注册MessageCodeResolver。
      对静态index.html的支持。
      对自定义Favicon的支持。
      自动使用ConfigurableWebBindingInitializer bean。
      如果保留Spring Boot MVC特性，你只需添加其他的MVC配置（拦截器，格式化处理器，视图控制器等）。你可以添加自己的WebMvcConfigurerAdapter
      类型的@Configuration类，而不需要注解@EnableWebMvc。如果希望使用自定义的RequestMappingHandlerMapping，RequestMappingHandlerAdapter，
      或ExceptionHandlerExceptionResolver，你可以声明一个WebMvcRegistrationsAdapter实例提供这些组件。
      
      如果想全面控制Spring MVC，你可以添加自己的@Configuration，并使用@EnableWebMvc注解。
      
##HttpMessageConverters
    
    Spring MVC使用HttpMessageConverter接口转换HTTP请求和响应，合适的默认配置可以开箱即用，例如对象自动转换为JSON（使用Jackson库）
    或XML（如果Jackson XML扩展可用，否则使用JAXB），字符串默认使用UTF-8编码。
    
    可以使用Spring Boot的HttpMessageConverters类添加或自定义转换类：
    
    import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
    import org.springframework.context.annotation.*;
    import org.springframework.http.converter.*;
    
    @Configuration
    public class MyConfiguration {
    
        @Bean
        public HttpMessageConverters customConverters() {
            HttpMessageConverter<?> additional = ...
            HttpMessageConverter<?> another = ...
            return new HttpMessageConverters(additional, another);
        }
    }
    上下文中出现的所有HttpMessageConverter bean都将添加到converters列表，你可以通过这种方式覆盖默认的转换器列表（converters）。
    
##自定义JSON序列化器和反序列化器
  
      如果使用Jackson序列化，反序列化JSON数据，你可能想编写自己的JsonSerializer和JsonDeserializer类。自定义序列化器（serializers）
      通常通过Module注册到Jackson，但Spring Boot提供了@JsonComponent注解这一替代方式，它能轻松的将序列化器注册为Spring Beans。
      
##MessageCodesResolver
  
      Spring MVC有一个实现策略，用于从绑定的errors产生用来渲染错误信息的错误码：MessageCodesResolver。Spring Boot会自动为你创建该实现，
      只要设置spring.mvc.message-codes-resolver.format属性为PREFIX_ERROR_CODE或POSTFIX_ERROR_CODE
      （具体查看DefaultMessageCodesResolver.Format枚举值）。
      
##静态内容
  
      默认情况下，Spring Boot从classpath下的/static（/public，/resources或/META-INF/resources）文件夹，或从ServletContext
      根目录提供静态内容。这是通过Spring MVC的ResourceHttpRequestHandler实现的，你可以自定义WebMvcConfigurerAdapter并覆写
      addResourceHandlers方法来改变该行为（加载静态文件）。
      
      在单机web应用中，容器会启动默认的servlet，并用它加载ServletContext根目录下的内容以响应那些Spring不处理的请求。大多数情况下这都
      不会发生（除非你修改默认的MVC配置），因为Spring总能够通过DispatcherServlet处理这些请求。
      
      你可以设置spring.resources.staticLocations属性自定义静态资源的位置（配置一系列目录位置代替默认的值），如果你这样做，默认的欢迎
      页面将从自定义位置加载，所以只要这些路径中的任何地方有一个index.html，它都会成为应用的主页。
      
      此外，除了上述标准的静态资源位置，有个例外情况是Webjars内容。任何在/webjars/**路径下的资源都将从jar文件中提供，只要它们以Webjars
      的格式打包。
      
      注 如果你的应用将被打包成jar，那就不要使用src/main/webapp文件夹。尽管该文件夹是通常的标准格式，但它仅在打包成war的情况下起作用，
      在打包成jar时，多数构建工具都会默认忽略它。
      
      Spring Boot也支持Spring MVC提供的高级资源处理特性，可用于清除缓存的静态资源或对WebJar使用版本无感知的URLs。
      
      如果想使用针对WebJars版本无感知的URLs（version agnostic），只需要添加webjars-locator依赖，然后声明你的Webjar。以jQuery为例，
      "/webjars/jquery/dist/jquery.min.js"实际为"/webjars/jquery/x.y.z/dist/jquery.min.js"，x.y.z为Webjar的版本。
      
      注 如果使用JBoss，你需要声明webjars-locator-jboss-vfs依赖而不是webjars-locator，否则所有的Webjars将解析为404。
      
      以下的配置为所有的静态资源提供一种缓存清除（cache busting）方案，实际上是将内容hash添加到URLs中，比如
      <link href="/css/spring-2a2d595e6ed9a0b24f027f2b63b134d6.css"/>：
      
      spring.resources.chain.strategy.content.enabled=true
      spring.resources.chain.strategy.content.paths=/**
      注 实现该功能的是ResourceUrlEncodingFilter，它在模板运行期会重写资源链接，Thymeleaf，Velocity和FreeMarker会自动配置该filter，
      JSP需要手动配置。其他模板引擎还没自动支持，不过你可以使用ResourceUrlProvider自定义模块宏或帮助类。
      
      当使用比如JavaScript模块加载器动态加载资源时，重命名文件是不行的，这也是提供其他策略并能结合使用的原因。下面是一个"fixed"策略，
      在URL中添加一个静态version字符串而不需要改变文件名：
      
      spring.resources.chain.strategy.content.enabled=true
      spring.resources.chain.strategy.content.paths=/**
      spring.resources.chain.strategy.fixed.enabled=true
      spring.resources.chain.strategy.fixed.paths=/js/lib/
      spring.resources.chain.strategy.fixed.version=v12
      使用以上策略，JavaScript模块加载器加载"/js/lib/"下的文件时会使用一个固定的版本策略"/v12/js/lib/mymodule.js"，其他资源仍旧使用
      内容hash的方式<link href="/css/spring-2a2d595e6ed9a0b24f027f2b63b134d6.css"/>。查看ResourceProperties获取更多支持的选项。
      
      注 该特性在一个专门的博文和Spring框架参考文档中有透彻描述。
      
##错误处理
  
      Spring Boot默认提供一个/error映射用来以合适的方式处理所有的错误，并将它注册为servlet容器中全局的 错误页面。对于机器客户端
      （相对于浏览器而言，浏览器偏重于人的行为），它会产生一个具有详细错误，HTTP状态，异常信息的JSON响应。对于浏览器客户端，它会产生一个
      白色标签样式（whitelabel）的错误视图，该视图将以HTML格式显示同样的数据（可以添加一个解析为'error'的View来自定义它）。为了完全替换
      默认的行为，你可以实现ErrorController，并注册一个该类型的bean定义，或简单地添加一个ErrorAttributes类型的bean以使用现存的机制，
      只是替换显示的内容。
      
      注 BasicErrorController可以作为自定义ErrorController的基类，如果你想添加对新context type的处理（默认处理text/html），
      这会很有帮助。你只需要继承BasicErrorController，添加一个public方法，并注解带有produces属性的@RequestMapping，然后创建该
      新类型的bean。
      
      你也可以定义一个@ControllerAdvice去自定义某个特殊controller或exception类型的JSON文档：
      
      @ControllerAdvice(basePackageClasses = FooController.class)
      public class FooControllerAdvice extends ResponseEntityExceptionHandler {
      
          @ExceptionHandler(YourException.class)
          @ResponseBody
          ResponseEntity<?> handleControllerException(HttpServletRequest request, Throwable ex) {
              HttpStatus status = getStatus(request);
              return new ResponseEntity<>(new CustomErrorType(status.value(), ex.getMessage()), status);
          }
      
          private HttpStatus getStatus(HttpServletRequest request) {
              Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
              if (statusCode == null) {
                  return HttpStatus.INTERNAL_SERVER_ERROR;
              }
              return HttpStatus.valueOf(statusCode);
          }
      
      }
      在以上示例中，如果跟FooController相同package的某个controller抛出YourException，一个CustomerErrorType类型的POJO的json
      展示将代替ErrorAttributes展示。
      
      自定义错误页面
      
      如果想为某个给定的状态码展示一个自定义的HTML错误页面，你需要将文件添加到/error文件夹下。错误页面既可以是静态HTML（比如，任何
      静态资源文件夹下添加的），也可以是使用模板构建的，文件名必须是明确的状态码或一系列标签。
      
      例如，映射404到一个静态HTML文件，你的目录结构可能如下：
      
      src/
       +- main/
           +- java/
           |   + <source code>
           +- resources/
               +- public/
                   +- error/
                   |   +- 404.html
                   +- <other public assets>
      使用FreeMarker模板映射所有5xx错误，你需要如下的目录结构：
      
      src/
       +- main/
           +- java/
           |   + <source code>
           +- resources/
               +- templates/
                   +- error/
                   |   +- 5xx.ftl
                   +- <other templates>
      对于更复杂的映射，你可以添加实现ErrorViewResolver接口的beans：
      
      public class MyErrorViewResolver implements ErrorViewResolver {
      
          @Override
          public ModelAndView resolveErrorView(HttpServletRequest request,
                  HttpStatus status, Map<String, Object> model) {
              // Use the request or status to optionally return a ModelAndView
              return ...
          }
      
      }
      你也可以使用Spring MVC特性，比如@ExceptionHandler方法和@ControllerAdvice，ErrorController将处理所有未处理的异常。
      
      映射Spring MVC以外的错误页面
      
      对于不使用Spring MVC的应用，你可以通过ErrorPageRegistrar接口直接注册ErrorPages。该抽象直接工作于底层内嵌servlet容器，
      即使你没有Spring MVC的DispatcherServlet，它们仍旧可以工作。
      
      @Bean
      public ErrorPageRegistrar errorPageRegistrar(){
          return new MyErrorPageRegistrar();
      }
      
      // ...
      
      private static class MyErrorPageRegistrar implements ErrorPageRegistrar {
      
          @Override
          public void registerErrorPages(ErrorPageRegistry registry) {
              registry.addErrorPages(new ErrorPage(HttpStatus.BAD_REQUEST, "/400"));
          }
      
      }
      注.如果你注册一个ErrorPage，该页面需要被一个Filter处理（在一些非Spring web框架中很常见，比如Jersey，Wicket），那么该Filter
      需要明确注册为一个ERROR分发器（dispatcher），例如：
      
      @Bean
      public FilterRegistrationBean myFilter() {
          FilterRegistrationBean registration = new FilterRegistrationBean();
          registration.setFilter(new MyFilter());
          ...
          registration.setDispatcherTypes(EnumSet.allOf(DispatcherType.class));
          return registration;
      }
      （默认的FilterRegistrationBean不包含ERROR dispatcher类型）。
      
      WebSphere应用服务器的错误处理
      
      当部署到一个servlet容器时，Spring Boot通过它的错误页面过滤器将带有错误状态的请求转发到恰当的错误页面。request只有在response
      还没提交时才能转发（forwarded）到正确的错误页面，而WebSphere应用服务器8.0及后续版本默认情况会在servlet方法成功执行后提交response，
      你需要设置com.ibm.ws.webcontainer.invokeFlushAfterService属性为false来关闭该行为。
      
      
##CORS支持
    
    跨域资源共享（CORS）是一个大多数浏览器都实现了的W3C标准，它允许你以灵活的方式指定跨域请求如何被授权，而不是采用那些不安全，性能低的方式，比如IFRAME或JSONP。
    
    从4.2版本开始，Spring MVC对CORS提供开箱即用的支持。不用添加任何特殊配置，只需要在Spring Boot应用的controller方法上注解@CrossOrigin，并添加CORS配置。通过注册一个自定义addCorsMappings(CorsRegistry)方法的WebMvcConfigurer bean可以指定全局CORS配置：
    
    @Configuration
    public class MyConfiguration {
    
        @Bean
        public WebMvcConfigurer corsConfigurer() {
            return new WebMvcConfigurerAdapter() {
                @Override
                public void addCorsMappings(CorsRegistry registry) {
                    registry.addMapping("/api/**");
                }
            };
        }
    }
    
## 27.3.2 Servlet上下文初始化
  
    内嵌servlet容器不会直接执行Servlet 3.0+的`javax.servlet.ServletContainerInitializer`接口，
    或Spring的`org.springframework.web.WebApplicationInitializer`接口，这样设计的目的是降低war包内运行的第三方库
    破坏Spring Boot应用的风险。
    
    如果需要在Spring Boot应用中执行servlet上下文初始化，你需要注册一个实现`org.springframework.boot.context.embedded
    .ServletContextInitializer`接口的bean。`onStartup`方法可以获取`ServletContext`，如果需要的话可以轻松用来适配一个
    已存在的`WebApplicationInitializer`。
    
    **扫描Servlets, Filters和listeners**
    
    当使用一个内嵌容器时，通过`@ServletComponentScan`可以启用对注解`@WebServlet`，`@WebFilter`和`@WebListener`类的自动注册。
    
    **注** 在独立的容器（非内嵌）中`@ServletComponentScan`不起作用，取为代之的是容器内建的discovery机制。
    
## 27.3.3 EmbeddedWebApplicationContext

    Spring Boot底层使用一种新的`ApplicationContext`类型，用于对内嵌servlet容器的支持。`EmbeddedWebApplicationContext`
    是一种特殊类型的`WebApplicationContext`，它通过搜索到的单个`EmbeddedServletContainerFactory` bean来启动自己，通常
    `TomcatEmbeddedServletContainerFactory`，`JettyEmbeddedServletContainerFactory`或`UndertowEmbeddedServletContainerFactory`将被自动配置。
    
    **注** 你不需要关心这些实现类，大部分应用都能被自动配置，并根据你的行为创建合适的`ApplicationContext`和`EmbeddedServletContainerFactory`。
    
    
## 27.3.4 自定义内嵌servlet容器

    常见的Servlet容器配置可以通过Spring `Environment`进行设置，通常将这些属性定义到`application.properties`文件中。
    
    常见的服务器配置包括：
    
    1. 网络设置：监听进入Http请求的端口（`server.port`），接口绑定地址`server.address`等。
    2. Session设置：session是否持久化（`server.session.persistence`），session超时时间（`server.session.timeout`），
    session数据存放位置（`server.session.store-dir`），session-cookie配置（`server.session.cookie.*`）。
    3. Error管理：错误页面的位置（`server.error.path`）等。
    4. [SSL](http://docs.spring.io/spring-boot/docs/1.4.1.RELEASE/reference/htmlsingle/#howto-configure-ssl)。
    5. [HTTP压缩](http://docs.spring.io/spring-boot/docs/1.4.1.RELEASE/reference/htmlsingle/#how-to-enable-http-response-compression)
    
    Spring Boot会尽量暴露常用设置，但这并不总是可能的。对于不可能的情况，可以使用专用的命名空间提供server-specific配置
    （查看`server.tomcat`，`server.undertow`）。例如，可以根据内嵌servlet容器的特性对[access logs](http://docs.spring.io/spring-boot/docs/1.4.1.RELEASE/reference/htmlsingle/#howto-configure-accesslogs)进行不同的设置。
    
    **注** 具体参考[ServerProperties](https://github.com/spring-projects/spring-boot/tree/v1.4.1.RELEASE/
    spring-boot-autoconfigure/src/main/java/org/springframework/boot/autoconfigure/web/ServerProperties.java)。
    
    **编程方式的自定义**
    
    如果需要以编程方式配置内嵌servlet容器，你可以注册一个实现`EmbeddedServletContainerCustomizer`接口的Spring bean。
    `EmbeddedServletContainerCustomizer`能够获取到包含很多自定义setter方法的`ConfigurableEmbeddedServletContainer`，
    你可以通过这些setter方法对内嵌容器自定义。
    
    ```java
    import org.springframework.boot.context.embedded.*;
    import org.springframework.stereotype.Component;
    
    @Component
    public class CustomizationBean implements EmbeddedServletContainerCustomizer {
        @Override
        public void customize(ConfigurableEmbeddedServletContainer container) {
            container.setPort(9000);
        }
    }
    ```
    
    **直接自定义ConfigurableEmbeddedServletContainer**
    
    如果以上自定义手法过于受限，你可以自己注册`TomcatEmbeddedServletContainerFactory`，`JettyEmbeddedServletContainerFactory`
    或`UndertowEmbeddedServletContainerFactory`。
    
    ```java
    @Bean
    public EmbeddedServletContainerFactory servletContainer() {
        TomcatEmbeddedServletContainerFactory factory = new TomcatEmbeddedServletContainerFactory();
        factory.setPort(9000);
        factory.setSessionTimeout(10, TimeUnit.MINUTES);
        factory.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/notfound.html");
        return factory;
    }
    ```
    很多配置选项提供setter方法，有的甚至提供一些受保护的钩子方法以满足你的某些特殊需求，具体参考源码或相关文档。
    
    
### 28. 安全
    
    如果添加了Spring Security的依赖，那么web应用默认对所有的HTTP路径（也称为终点，端点，表示API的具体网址）使用'basic'认证。
    为了给web应用添加方法级别（method-level）的保护，你可以添加`@EnableGlobalMethodSecurity`并使用想要的设置，其他信息参考
    [Spring Security Reference](http://docs.spring.io/spring-security/site/docs/4.1.3.RELEASE/reference/htmlsingle#jc-method)。
    
    默认的`AuthenticationManager`只有一个用户（'user'的用户名和随机密码会在应用启动时以INFO日志级别打印出来），如下：
    ```java
    Using default security password: 78fa095d-3f4c-48b1-ad50-e24c31d5cf35
    ```
    **注** 如果你对日志配置进行微调，确保`org.springframework.boot.autoconfigure.security`类别记录日志级别为`INFO`，
    否则默认的密码不会打印出来。
    
    你可以通过设置`security.user.password`改变默认密码，这些和其他有用的属性通过[SecurityProperties](https://github.com/
    spring-projects/spring-boot/tree/v1.4.1.RELEASE/spring-boot-autoconfigure/src/main/java/org/springframework/boot/
    autoconfigure/security/SecurityProperties.java)（以"security"为前缀的属性）被外部化了。
    
    默认的安全配置是通过`SecurityAutoConfiguration`，`SpringBootWebSecurityConfiguration`（用于web安全），
    `AuthenticationManagerConfiguration`（可用于非web应用的认证配置）进行管理的。你可以添加一个`@EnableWebSecurity` bean来彻底
    关掉Spring Boot的默认配置。为了对它进行自定义，你需要使用外部的属性配置和`WebSecurityConfigurerAdapter`类型的beans（比如，添加基于表单的登陆）。
    想要关闭认证管理的配置，你可以添加一个`AuthenticationManager`类型的bean，或在`@Configuration`类的某个方法里注入
    `AuthenticationManagerBuilder`来配置全局的`AuthenticationManager`。这里有一些安全相关的[Spring Boot应用示例](https://github
    .com/spring-projects/spring-boot/tree/v1.4.1.RELEASE/spring-boot-samples/)可以拿来参考。
    
    在web应用中你能得到的开箱即用的基本特性如下：
    
    1. 一个使用内存存储的`AuthenticationManager` bean和一个用户（查看`SecurityProperties.User`获取user的属性）。
    2. 忽略（不保护）常见的静态资源路径（`/css/**, /js/**, /images/**`，`/webjars/**`和 `**/favicon.ico`）。
    3. 对其他所有路径实施HTTP Basic安全保护。
    4. 安全相关的事件会发布到Spring的`ApplicationEventPublisher`（成功和失败的认证，拒绝访问）。
    5. Spring Security提供的常见底层特性（HSTS, XSS, CSRF, 缓存）默认都被开启。
    
    上述所有特性都能通过外部配置（`security.*`）打开，关闭，或修改。想要覆盖访问规则而不改变其他自动配置的特性，你可以添加一个注解
    `@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)`的`WebSecurityConfigurerAdapter`类型的`@Bean`。
    
    **注** `WebSecurityConfigurerAdapter`默认会匹配所有路径，如果不想完全覆盖Spring Boot自动配置的访问规则，你可以精确的配置想要覆盖的路径。


### 28.1.1 授权服务器

    想要创建一个授权服务器，并授予access tokens，你需要使用`@EnableAuthorizationServer`，并提供`security.oauth2.client.client-id`
    和`security.oauth2.client.client-secret`配置。
    
    按以上操作后，你就能使用客户端证书创建一个access token，例如：
    ```shell
    $ curl client:secret@localhost:8080/oauth/token -d grant_type=password -d username=user -d password=pwd
    ```
    `/token`端点basic形式的认证证书是`client-id`和`client-secret`，用户证书通常是Spring Security的user详情（Spring Boot中默认是
    "user"和一个随机的密码）。
    
    想要关闭自动配置，自己配置授权服务器特性，你只需添加一个`AuthorizationServerConfigurer`类型的`@Bean`。
    
    
### 28.1.2 资源服务器

    为了使用access token，你需要一个资源服务器（可以跟授权服务器是同一个）。创建资源服务器很简单，只需要添加`@EnableResourceServer`，
    提供一些配置以允许服务器解码access token。如果应用也是授权服务器，由于它知道如何去解码tokens，所以也就不需要做其他事情。如果你的app
    是独立的服务，那你就需要给它添加以下可选配置中的某一项：
    
    * `security.oauth2.resource.user-info-uri`用于`/me`资源（例如，PWS的`https://uaa.run.pivotal.io/userinfo`）。
    * `security.oauth2.resource.token-info-uri`用于token解码端点（例如，PWS的`https://uaa.run.pivotal.io/check_token`）。
    
    如果`user-info-uri`和`token-info-uri`都指定了，你可以设置flag筛选出最想要的那个（默认`prefer-token-info=true`）。
    
    另外，如果token是JWTs，你可以配置`security.oauth2.resource.jwt.key-value`解码它们（key是验签的key）。验签的键值可以是一个
    对称密钥，也可以是PEM编码的RSA公钥。如果你没有key，并且它是公开的，你可以通过`security.oauth2.resource.jwt.key-uri`提供一个下载
    URI（有一个"value"字段的JSON对象），例如，在PWS平台上：
    ```
    $ curl https://uaa.run.pivotal.io/token_key
    {"alg":"SHA256withRSA","value":"-----BEGIN PUBLIC KEY-----\nMIIBI...\n-----END PUBLIC KEY-----\n"}
    ```
    **注** 如果你使用`security.oauth2.resource.jwt.key-uri`，授权服务器需要在应用启动时也运行起来，如果找不到key，它将输出warning，
    并告诉你如何解决。
    
###28.1 OAuth2

    如果添加了`spring-security-oauth2`依赖，你可以利用自动配置简化认证（Authorization）或资源服务器（Resource Server）的设置，
    详情参考[Spring Security OAuth 2 Developers Guide](http://projects.spring.io/spring-security-oauth/docs/oauth2.html)。

###28.2 User Info中的Token类型

    Google和其他一些第三方身份（identity）提供商对发送给user info端点的请求头中设置的token类型名有严格要求。默认的`Bearer`满足大多数
    提供商要求，如果需要你可以设置`security.oauth2.resource.token-type`来改变它。
    
### 28.3.1 客户端

    为了将web-app放入一个OAuth2客户端，你只需注解`@EnableOAuth2Client`，Spring Boot会创建`OAuth2ClientContext`和`OAuth2ProtectedResourceDetails`，这些是创建`OAuth2RestOperations`必需的。Spring Boot不会自动创建该bean，但你自己创建也不费力：
    ```java
    @Bean
    public OAuth2RestTemplate oauth2RestTemplate(OAuth2ClientContext oauth2ClientContext,
            OAuth2ProtectedResourceDetails details) {
        return new OAuth2RestTemplate(details, oauth2ClientContext);
    }
    ```
    **注** 你可能想添加一个限定名（qualifier），因为应用中可能定义多个`RestTemplate`。
    
    该配置使用`security.oauth2.client.*`作为证书（跟授权服务器使用的相同），此外，它也需要知道授权服务器中认证和token的URIs，例如：
    ```yml
    security:
        oauth2:
            client:
                clientId: bd1c0a783ccdd1c9b9e4
                clientSecret: 1a9030fbca47a5b2c28e92f19050bb77824b5ad1
                accessTokenUri: https://github.com/login/oauth/access_token
                userAuthorizationUri: https://github.com/login/oauth/authorize
                clientAuthenticationScheme: form
    ```
    具有该配置的应用在使用`OAuth2RestTemplate`时会重定向到GitHub以完成授权，如果已经登陆GitHub，你甚至不会注意到它已经授权过了。那些
    特殊的凭证（credentials）只在应用运行于8080端口时有效（为了更灵活，在GitHub或其他提供商上注册自己的客户端app）。
    
    在客户端获取access token时，你可以设置`security.oauth2.client.scope`（逗号分隔或一个YAML数组）来限制它请求的作用域（scope）。
    作用域默认是空的，默认值取决于授权服务器，通常依赖于它拥有的客户端在注册时的设置。
    
    **注** 对`security.oauth2.client.client-authentication-scheme`也有设置，默认为"header"（如果你的OAuth2提供商不喜欢header认证，
    例如Github，你可能需要将它设置为“form”）。实际上，`security.oauth2.client.*`属性绑定到一个`AuthorizationCodeResourceDetails`
    实例，所以它的所有属性都可以指定。
    
    **注** 在一个非web应用中，你仍旧可以创建一个`OAuth2RestOperations`，并且跟`security.oauth2.client.*`配置关联。在这种情况下，
    它是一个“client credentials token grant”，如果你使用它的话就需要获取（此处不需要注解`@EnableOAuth2Client`或`@EnableOAuth2Sso`）
    。为了防止基础设施定义，只需要将`security.oauth2.client.client-id`从配置中移除（或将它设为空字符串）。

### 28.3.2 单点登陆

    OAuth2客户端可用于从提供商抓取用户详情，然后转换为Spring Security需要的`Authentication` token。上述提到的资源服务器通过
    `user-info-uri`属性来支持该功能，这是基于OAuth2的单点登陆（SSO）协议最基本的，Spring Boot提供的`@EnableOAuth2Sso`注解让它更
    容易实践。通过添加该注解及端点配置（`security.oauth2.client.*`），Github客户端就可以使用`/user/`端点保护它的所有资源了：
    ```yaml
    security:
        oauth2:
    ...
        resource:
            userInfoUri: https://api.github.com/user
            preferTokenInfo: false
    ```
    由于所有路径默认都处于保护下，也就没有主页展示那些未授权的用户，进而邀请他们去登陆（通过访问`/login`路径，或`security.oauth2.sso
    .login-path`指定的路径）。
    
    为了自定义访问规则或保护的路径（这样你就可以添加主页），你可以将`@EnableOAuth2Sso`添加到一个`WebSecurityConfigurerAdapter`，
    该注解会包装它，增强需要的地方以使`/login`路径工作。例如，这里我们允许未授权的用户访问主页`/`，其他的依旧保持默认：
    ```java
    @Configuration
    public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {
    
        @Override
        public void init(WebSecurity web) {
            web.ignore("/");
        }
    
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/**").authorizeRequests().anyRequest().authenticated();
        }
    
    }
    ```
    
### 28.3 自定义User Info RestTemplate

    如果设置了`user-info-uri`，资源服务器在内部将使用一个`OAuth2RestTemplate`抓取用于认证的用户信息，这是一个id为
    `userInfoRestTemplate`的`@Bean`提供的，但你不需要了解这些，只需要用它即可。默认适用于大多数提供商，但偶尔你可能需要添加其他
    interceptors，或改变request的验证器（authenticator）。想要添加自定义，只需创建一个`UserInfoRestTemplateCustomizer`类型的
    bean —— 它只有单个方法，在bean创建后，初始化前会调用该方法。此处自定义的rest template仅用于内部执行认证。
    
    **注** 在YAML中设置RSA key时，需要使用管道符分割多行（“|”），记得缩进key value，例如：
    ```yaml
    security:
        oauth2:
            resource:
                jwt:
                    keyValue: |
                        -----BEGIN PUBLIC KEY-----
                        MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKC...
                        -----END PUBLIC KEY-----
    ```
    
### 29.1. 配置DataSource
 
    Java的`javax.sql.DataSource`接口提供了一个标准的使用数据库连接的方法。通常，DataSource使用`URL`和相应的凭证去初始化数据库连接。
    
### 29.1.1. 对内嵌数据库的支持

    开发应用时使用内存数据库是很方便的。显然，内存数据库不提供持久化存储；你只需要在应用启动时填充数据库，在应用结束前预先清除数据。
    
    Spring Boot可以自动配置的内嵌数据库包括[H2](http://www.h2database.com/), [HSQL](http://hsqldb.org/)和[Derby](http://
    db.apache.org/derby/)。你不需要提供任何连接URLs，只需要添加你想使用的内嵌数据库依赖。
    
    示例：典型的POM依赖如下：
    ```xml
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.hsqldb</groupId>
        <artifactId>hsqldb</artifactId>
        <scope>runtime</scope>
    </dependency>
    ```
    **注** 对于自动配置的内嵌数据库，你需要添加`spring-jdbc`依赖，在本示例中，`spring-boot-starter-data-jpa`已包含该依赖了。
    
    **注** 无论出于什么原因，你需要配置内嵌数据库的连接URL，一定要确保数据库的自动关闭是禁用的。如果使用H2，你需要设置
    `DB_CLOSE_ON_EXIT=FALSE`。如果使用HSQLDB，你需要确保没使用`shutdown=true`。禁用数据库的自动关闭可以让Spring Boot控制何时
    关闭数据库，因此在数据库不需要时可以确保关闭只发生一次。


### 29.1.2. 连接生产环境数据库

    生产环境的数据库连接可以通过池化的`DataSource`进行自动配置，下面是选取特定实现的算法：
    
    - 出于tomcat数据源连接池的优秀性能和并发，如果可用总会优先使用它。
    - 如果HikariCP可用，我们将使用它。
    - 如果Commons DBCP可用，我们将使用它，但生产环境不推荐。
    - 最后，如果Commons DBCP2可用，我们将使用它。
    
    如果使用`spring-boot-starter-jdbc`或`spring-boot-starter-data-jpa` 'starters'，你会自动添加`tomcat-jdbc`依赖。
    
    **注** 通过指定`spring.datasource.type`属性，你可以完全抛弃该算法，然后指定数据库连接池。如果你在tomcat容器中运行应用，
    由于默认提供`tomcat-jdbc`，这就很重要了。
    
    **注** 其他的连接池可以手动配置，如果你定义自己的`DataSource` bean，自动配置是不会发生的。
    
    DataSource配置被外部的`spring.datasource.*`属性控制，例如，你可能会在`application.properties`中声明以下片段：
    ```java
    spring.datasource.url=jdbc:mysql://localhost/test
    spring.datasource.username=dbuser
    spring.datasource.password=dbpass
    spring.datasource.driver-class-name=com.mysql.jdbc.Driver
    ```
    **注** 你应该至少使用`spring.datasource.url`属性指定url，或Spring Boot尝试自动配置内嵌数据库。
    
    **注** 你经常不需要指定`driver-class-name`，因为Spring boot可以从`url`推断大部分数据库。
    
    **注** 对于将要创建的池化`DataSource`，我们需要验证是否有一个可用的`Driver`，所以在做其他事前会校验它。比如，如果你设置
    `spring.datasource.driver-class-name=com.mysql.jdbc.Driver`，然后该class加载出来，否则就会出错。

    其他可选配置可以查看[DataSourceProperties](https://github.com/spring-projects/spring-boot/tree/v1.4.1.RELEASE/
    spring-boot-autoconfigure/src/main/java/org/springframework/boot/autoconfigure/jdbc/DataSourceProperties.java)，
    有些标准配置是跟实现无关的，对于实现相关的配置可以通过相应前缀进行设置（`spring.datasource.tomcat.*`，`spring.datasource.hikari.*`，
    `spring.datasource.dbcp.*`和`spring.datasource.dbcp2.*`），具体参考你使用的连接池文档。
    
    例如，如果正在使用[Tomcat连接池](http://tomcat.apache.org/tomcat-8.0-doc/jdbc-pool.html#Common_Attributes)，你可以自定义很多其他设置：
    ```properties
    # Number of ms to wait before throwing an exception if no connection is available.
    spring.datasource.tomcat.max-wait=10000
    
    # Maximum number of active connections that can be allocated from this pool at the same time.
    spring.datasource.tomcat.max-active=50
    
    # Validate the connection before borrowing it from the pool.
    spring.datasource.tomcat.test-on-borrow=true
    
### 29.2. 使用JdbcTemplate

    Spring的`JdbcTemplate`和`NamedParameterJdbcTemplate`类会被自动配置，你可以将它们直接`@Autowire`到自己的beans：
    ```java
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.jdbc.core.JdbcTemplate;
    import org.springframework.stereotype.Component;
    
    @Component
    public class MyBean {
    
        private final JdbcTemplate jdbcTemplate;
    
        @Autowired
        public MyBean(JdbcTemplate jdbcTemplate) {
            this.jdbcTemplate = jdbcTemplate;
        }
        // ...
    }

### 29.3. JPA和Spring Data

    Java持久化API是一个允许你将对象映射为关系数据库的标准技术，`spring-boot-starter-data-jpa` POM提供了一种快速上手的方式，它提供以下关键依赖：
    
    - Hibernate - 一个非常流行的JPA实现。
    - Spring Data JPA - 让实现基于JPA的repositories更容易。
    - Spring ORMs - Spring框架支持的核心ORM。
    
    **注** 我们不想在这涉及太多关于JPA或Spring Data的细节。你可以参考来自[spring.io](http://spring.io/)的指南[使用JPA获取数据]
    (http://spring.io/guides/gs/accessing-data-jpa/)，并阅读[Spring Data JPA](http://projects.spring.io/spring-data-jpa/)
    和[Hibernate](http://hibernate.org/orm/documentation/)的参考文档。
    
    **注** Spring Boot默认使用Hibernate 5.0.x，如果你希望的话也可以使用4.3.x或5.2.x，具体参考[Hibernate 4](https://github.com/
    spring-projects/spring-boot/tree/v1.4.1.RELEASE/spring-boot-samples/spring-boot-sample-hibernate4)和[Hibernate 5.2]
    (https://github.com/spring-projects/spring-boot/tree/v1.4.1.RELEASE/spring-boot-samples/spring-boot-sample-hibernate52)示例。

### 29.3.1. 实体类

    通常，JPA实体类被定义到一个`persistence.xml`文件，在Spring Boot中，这个文件被'实体扫描'取代。默认情况，Spring Boot会查找主配置类
    （被`@EnableAutoConfiguration`或`@SpringBootApplication`注解的类）下的所有包。
    
    任何被`@Entity`，`@Embeddable`或`@MappedSuperclass`注解的类都将被考虑，一个普通的实体类看起来像这样：
    ```java
    package com.example.myapp.domain;
    
    import java.io.Serializable;
    import javax.persistence.*;
    
    @Entity
    public class City implements Serializable {
    
        @Id
        @GeneratedValue
        private Long id;
    
        @Column(nullable = false)
        private String name;
    
        @Column(nullable = false)
        private String state;
    
        // ... additional members, often include @OneToMany mappings
    
        protected City() {
            // no-args constructor required by JPA spec
            // this one is protected since it shouldn't be used directly
        }
    
        public City(String name, String state) {
            this.name = name;
            this.country = country;
        }
    
        public String getName() {
            return this.name;
        }
    
        public String getState() {
            return this.state;
        }
        // ... etc
    }
    ```
    **注** 你可以使用`@EntityScan`注解自定义实体扫描路径，具体参考[Section 74.4, “Separate @Entity definitions from Spring 
    configuration”](../IX. ‘How-to’ guides/74.4. Separate @Entity definitions from Spring configuration.md)。
    
### 30. 使用NoSQL技术

    Spring Data提供其他项目，用来帮你使用各种各样的NoSQL技术，包括[MongoDB](http://projects.spring.io/spring-data-mongodb/), 
    [Neo4J](http://projects.spring.io/spring-data-neo4j/), [Elasticsearch](https://github.com/spring-projects/
    spring-data-elasticsearch/), [Solr](http://projects.spring.io/spring-data-solr/), [Redis](http://projects.spring.io/
    spring-data-redis/), [Gemfire](http://projects.spring.io/spring-data-gemfire/), [Couchbase](http://projects.spring.io/
    spring-data-couchbase/)和[Cassandra](http://projects.spring.io/spring-data-cassandra/)。Spring Boot为Redis, MongoDB,
     Elasticsearch, Solr和Cassandra提供自动配置。你也可以充分利用其他项目，但需要自己配置它们，具体查看[projects.spring.io/
     spring-data](http://projects.spring.io/spring-data/)中相应的参考文档。


### 30.1.1. 连接Redis

    你可以注入一个自动配置的`RedisConnectionFactory`，`StringRedisTemplate`或普通的`RedisTemplate`实例，或任何其他Spring Bean
    只要你愿意。默认情况下，这个实例将尝试使用`localhost:6379`连接Redis服务器：
    ```java
    @Component
    public class MyBean {
    
        private StringRedisTemplate template;
    
        @Autowired
        public MyBean(StringRedisTemplate template) {
            this.template = template;
        }
        // ...
    }
    ```
    如果你添加一个自己的，或任何自动配置类型的`@Bean`，它将替换默认实例（除了`RedisTemplate`的情况，它是根据`bean`的name 
    'redisTemplate'而不是类型进行排除的）。如果在classpath路径下存在`commons-pool2`，默认你会获得一个连接池工厂。


### 30.2.1. 连接MongoDB数据库

    你可以注入一个自动配置的`org.springframework.data.mongodb.MongoDbFactory`来访问Mongo数据库。默认情况下，该实例将尝试使用
    URL `mongodb://localhost/test`连接到MongoDB服务器：
    ```java
    import org.springframework.data.mongodb.MongoDbFactory;
    import com.mongodb.DB;
    
    @Component
    public class MyBean {
    
        private final MongoDbFactory mongo;
    
        @Autowired
        public MyBean(MongoDbFactory mongo) {
            this.mongo = mongo;
        }
    
        // ...
        public void example() {
            DB db = mongo.getDb();
            // ...
        }
    }
    ```
    你可以设置`spring.data.mongodb.uri`来改变该url，并配置其他的设置，比如副本集：
    ```properties
    spring.data.mongodb.uri=mongodb://user:secret@mongo1.example.com:12345,mongo2.example.com:23456/test
    ```
    另外，跟正在使用的Mongo 2.x一样，你可以指定`host`/`port`，比如，在`application.properties`中添加以下配置：
    ```java
    spring.data.mongodb.host=mongoserver
    spring.data.mongodb.port=27017
    ```
    **注** Mongo 3.0 Java驱动不支持`spring.data.mongodb.host`和`spring.data.mongodb.port`，对于这种情况，
    `spring.data.mongodb.uri`需要提供全部的配置信息。
    
    **注** 如果没有指定`spring.data.mongodb.port`，默认使用`27017`，上述示例中可以删除这行配置。
    
    **注** 如果不使用Spring Data Mongo，你可以注入`com.mongodb.Mongo beans`以代替`MongoDbFactory`。
    
    如果想完全控制MongoDB连接的建立过程，你可以声明自己的`MongoDbFactory`或`Mongo` bean。
    如果想全面控制MongoDB连接的建立，你也可以声明自己的MongoDbFactory或Mongo，@Beans。
    
    
### 30.2.2.  MongoDBTemplate

    Spring Data Mongo提供了一个[MongoTemplate](http://docs.spring.io/spring-data/mongodb/docs/current/api/org/
    springframework/data/mongodb/core/MongoTemplate.html)类，它的设计和Spring的`JdbcTemplate`很相似。跟`JdbcTemplate`一样，
    Spring Boot会为你自动配置一个bean，你只需简单的注入即可：
    ```java
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.data.mongodb.core.MongoTemplate;
    import org.springframework.stereotype.Component;
    
    @Component
    public class MyBean {
    
        private final MongoTemplate mongoTemplate;
    
        @Autowired
        public MyBean(MongoTemplate mongoTemplate) {
            this.mongoTemplate = mongoTemplate;
        }
        // ...
    }
    ```
    具体参考`MongoOperations` Javadoc。
    
### 30.2.3. Spring Data MongoDB仓库

    Spring Data包含的仓库也支持MongoDB，正如上面讨论的JPA仓库，基于方法名自动创建查询是基本的原则。
    
    实际上，不管是Spring Data JPA还是Spring Data MongoDB都共享相同的基础设施。所以你可以使用上面的JPA示例，并假设那个`City`现在是
    一个Mongo数据类而不是JPA `@Entity`，它将以同样的方式工作：
    ```java
    package com.example.myapp.domain;
    
    import org.springframework.data.domain.*;
    import org.springframework.data.repository.*;
    
    public interface CityRepository extends Repository<City, Long> {
    
        Page<City> findAll(Pageable pageable);
    
        City findByNameAndCountryAllIgnoringCase(String name, String country);
    
    }
    ```
    **注** 想详细了解Spring Data MongoDB，包括它丰富的对象映射技术，可以查看它的[参考文档](http://projects.spring.io/
    spring-data-mongodb/)。


### 30.2.4 内嵌的Mongo

    Spring Boot为[内嵌Mongo](https://github.com/flapdoodle-oss/de.flapdoodle.embed.mongo)提供自动配置，你需要添加
    `de.flapdoodle.embed:de.flapdoodle.embed.mongo`依赖才能使用它。
    
    `spring.data.mongodb.port`属性可用来配置Mongo监听的端口，将该属性值设为0，表示使用一个随机分配的可用端口。通过
    `MongoAutoConfiguration`创建的`MongoClient`将自动配置为使用随机分配的端口。
    
    如果classpath下存在SLF4J依赖，Mongo产生的输出将自动路由到一个名为`org.springframework.boot.autoconfigure.mongo.embedded
    .EmbeddedMongo`的logger。
    
    想要完全控制Mongo实例的配置和日志路由，你可以声明自己的`IMongodConfig`和`IRuntimeConfig` beans。
    
    
### 30.5.1 连接Solr

    你可以注入一个自动配置的`SolrClient`实例，就像其他Spring beans那样，该实例默认使用`localhost:8983/solr`连接Solr服务器：
    ```java
    @Component
    public class MyBean {
    
        private SolrClient solr;
    
        @Autowired
        public MyBean(SolrClient solr) {
            this.solr = solr;
        }
    
        // ...
    
    }
    ```
    如果你添加自己的`SolrClient`类型的`@Bean`，它将会替换默认实例。


### 30.6.1 使用Jest连接Elasticsearch

    如果添加`Jest`依赖，你可以注入一个自动配置的`JestClient`，默认目标为`http://localhost:9200/`，也可以进一步配置该客户端：
    ```properties
    spring.elasticsearch.jest.uris=http://search.example.com:9200
    spring.elasticsearch.jest.read-timeout=10000
    spring.elasticsearch.jest.username=user
    spring.elasticsearch.jest.password=secret
    ```
    定义一个`JestClient` bean以完全控制注册过程。
    
### 30.6.2 使用Spring Data连接Elasticsearch

    你可以注入一个自动配置的`ElasticsearchTemplate`或Elasticsearch `Client`实例，就想其他Spring Bean那样。
    该实例默认内嵌一个本地，内存型服务器（在Elasticsearch中被称为`Node`），并使用当前工作目录作为服务器的home目录。在这个步骤中，
    首先要做的是告诉Elasticsearch将文件存放到什么地方：
    ```properties
    spring.data.elasticsearch.properties.path.home=/foo/bar
    ```
    另外，你可以通过设置`spring.data.elasticsearch.cluster-nodes`（逗号分隔的‘host:port’列表）来切换为远程服务器：
    ```properties
    spring.data.elasticsearch.cluster-nodes=localhost:9300
    ```
    ```java
    @Component
    public class MyBean {
    
        private ElasticsearchTemplate template;
    
        @Autowired
        public MyBean(ElasticsearchTemplate template) {
            this.template = template;
        }
    
        // ...
    
    }
    ```
    如果添加自己的`ElasticsearchTemplate`类型的`@Bean`，它将覆盖默认实例。
    

### 30.6.3 Spring Data Elasticseach仓库

    Spring Data包含的仓库也支持Elasticsearch，正如前面讨论的JPA仓库，基于方法名自动创建查询是基本的原则。
    
    实际上，不管是Spring Data JPA还是Spring Data Elasticsearch都共享相同的基础设施。所以你可以使用前面的JPA示例，
    并假设那个`City`现在是一个Elasticsearch `@Document`类而不是JPA `@Entity`，它将以同样的方式工作。
    
    **注** 具体参考[Spring Data Elasticsearch文档](http://docs.spring.io/spring-data/elasticsearch/docs/)。


### 30.6 Elasticsearch

    [Elastic Search](http://www.elasticsearch.org/)是一个开源的，分布式，实时搜索和分析引擎。Spring Boot为Elasticsearch
    提供基本的自动配置，[Spring Data Elasticsearch](https://github.com/spring-projects/spring-data-elasticsearch)提供在它
    之上的抽象，还有用于收集依赖的`spring-boot-starter-data-elasticsearch`'Starter'。

###31.1.3 EhCache 2.x

    如果在classpath下的根目录可以找到一个名为`ehcache.xml`的文件，则缓存将使用EhCache 2.x。如果EhCache 2.x和这样的文件出现，
    那它们将用于启动缓存管理器，使用以下配置可提供替换的配置文件：
    ```properties
    spring.cache.ehcache.config=classpath:config/another-config.xml
    ```

###31.1.8 Caffeine

    Caffeine是Java8对Guava缓存的重写版本，在Spring Boot 2.0中将取代Guava。如果出现Caffeine，`CaffeineCacheManager`将会自动配置。
    使用`spring.cache.cache-names`属性可以在启动时创建缓存，并可以通过以下配置进行自定义（按顺序）：
    
    1. `spring.cache.caffeine.spec`定义的特殊缓存
    2. `com.github.benmanes.caffeine.cache.CaffeineSpec` bean定义
    3. `com.github.benmanes.caffeine.cache.Caffeine` bean定义
    
    例如，以下配置创建一个`foo`和`bar`缓存，最大数量为500，存活时间为10分钟：
    ```properties
    spring.cache.cache-names=foo,bar
    spring.cache.caffeine.spec=maximumSize=500,expireAfterAccess=600s
    ```
    除此之外，如果定义了`com.github.benmanes.caffeine.cache.CacheLoader`，它会自动关联到`CaffeineCacheManager`。
    由于该`CacheLoader`将关联被该缓存管理器管理的所有缓存，所以它必须定义为`CacheLoader<Object, Object>`，自动配置将忽略所有泛型类型。


###31.1.9 Guava

    如果存在Guava，`GuavaCacheManager`会自动配置。使用`spring.cache.cache-names`属性可以在启动时创建缓存，并通过以下方式之一自定义
    （按此顺序）：
    
    1. `spring.cache.guava.spec`定义的特殊缓存
    2. `com.google.common.cache.CacheBuilderSpec` bean定义的
    3. `com.google.common.cache.CacheBuilder` bean定义的
    
    例如，以下配置创建了一个`foo`和`bar`缓存，该缓存最大数量为500，存活时间为10分钟：
    ```properties
    spring.cache.cache-names=foo,bar
    spring.cache.guava.spec=maximumSize=500,expireAfterAccess=600s
    ```
    此外，如果定义`com.google.common.cache.CacheLoader` bean，它会自动关联到`GuavaCacheManager`。由于该`CacheLoader`
    将关联该缓存管理器管理的所有缓存，它必须定义为`CacheLoader<Object, Object>`，自动配置会忽略所有泛型类型。


###31.1.10 Simple

    如果以上选项都没有采用，一个使用`ConcurrentHashMap`作为缓存存储的简单实现将被配置，这是应用没有添加缓存library的默认设置。


###31.1.11 None

    如果配置类中出现`@EnableCaching`，一个合适的缓存配置也同样被期待。如果在某些环境需要禁用全部缓存，强制将缓存类型设为`none`
    将会使用一个no-op实现（没有任何实现的实现）：
    ```properties
    spring.cache.type=none
    ```
###31.1 支持的缓存提供商

    缓存抽象不提供实际的存储，而是依赖于`org.springframework.cache.Cache`和`org.springframework.cache.CacheManager`接口的实现。
    只要通过`@EnableCaching`注解开启缓存支持，Spring Boot就会根据实现自动配置一个合适的`CacheManager`。
    
    **注** 如果你使用的缓存设施beans不是基于接口的，确保启用`proxyTargetClass`，并设置其属性为`@EnableCaching`。
    
    **注** 使用`spring-boot-starter-cache`‘Starter’可以快速添加所需缓存依赖，如果你是手动添加依赖，需要注意一些实现只有`spring-context-support` jar才提供。
    
    如果你还没有定义一个`CacheManager`类型的bean，或一个名为`cacheResolver`的`CacheResolver`（查看`CachingConfigurer`），Spring Boot将尝试以下提供商（按这个顺序)：
     
     * [Generic](http://docs.spring.io/spring-boot/docs/1.4.1.RELEASE/reference/htmlsingle/#boot-features-caching-provider-generic)
     * [JCache (JSR-107)](http://docs.spring.io/spring-boot/docs/1.4.1.RELEASE/reference/htmlsingle/#boot-features-caching-provider-jcache)(EhCache 3, Hazelcast, Infinispan, etc)
     * [EhCache 2.x](http://docs.spring.io/spring-boot/docs/1.4.1.RELEASE/reference/htmlsingle/#boot-features-caching-provider-ehcache2)
     * [Hazelcast](http://docs.spring.io/spring-boot/docs/1.4.1.RELEASE/reference/htmlsingle/#boot-features-caching-provider-hazelcast)
     * [Infinispan](http://docs.spring.io/spring-boot/docs/1.4.1.RELEASE/reference/htmlsingle/#boot-features-caching-provider-infinispan)
     * [Couchbase](http://docs.spring.io/spring-boot/docs/1.4.1.RELEASE/reference/htmlsingle/#boot-features-caching-provider-couchbase)
     * [Redis](http://docs.spring.io/spring-boot/docs/1.4.1.RELEASE/reference/htmlsingle/#boot-features-caching-provider-redis)
     * [Caffeine](http://docs.spring.io/spring-boot/docs/1.4.1.RELEASE/reference/htmlsingle/#boot-features-caching-provider-caffeine)
     * [Guava](http://docs.spring.io/spring-boot/docs/1.4.1.RELEASE/reference/htmlsingle/#boot-features-caching-provider-guava)
     * [Simple](http://docs.spring.io/spring-boot/docs/1.4.1.RELEASE/reference/htmlsingle/#boot-features-caching-provider-simple)
     
     **注** `spring.cache.type`属性可强制指定使用的缓存提供商，如果需要在一些环境（比如，测试）中[禁用全部缓存]
     (http://docs.spring.io/spring-boot/docs/1.4.1.RELEASE/reference/htmlsingle/#boot-features-caching-provider-none)也可以使用该属性。
     
     如果`CacheManager`是Spring Boot自动配置的，你可以在它完全初始化前，通过实现`CacheManagerCustomizer`接口进一步配置，以下设置使用的缓存name：
     ```java
     @Bean
    public CacheManagerCustomizer<ConcurrentMapCacheManager> cacheManagerCustomizer() {
        return new CacheManagerCustomizer<ConcurrentMapCacheManager>() {
            @Override
            public void customize(ConcurrentMapCacheManager cacheManager) {
                cacheManager.setCacheNames(Arrays.asList("one", "two"));
            }
        };
    }
     ```
     **注** 在以上示例中，需要配置一个`ConcurrentMapCacheManager`，如果没有配置，则自定义器（customizer）将不会被调用。
     自定义器你添加多少都可以，并可以使用`@Order`或`Ordered`对它们进行排序。


### 32. 消息

    Spring Framework框架为集成消息系统提供了扩展（extensive）支持：从使用`JmsTemplate`简化JMS API，到实现一个能够异步接收消息的完整
    的底层设施。Spring AMQP提供一个相似的用于'高级消息队列协议'的特征集，并且Spring Boot也为`RabbitTemplate`和RabbitMQ提供了自动
    配置选项。Spring Websocket提供原生的STOMP消息支持，并且Spring Boot也提供了starters和自动配置支持。

### 32.1. JMS

    `javax.jms.ConnectionFactory`接口提供标准的用于创建`javax.jms.Connection`的方法，`javax.jms.Connection`用于和JMS代理（broker）交互。
    尽管Spring需要一个`ConnectionFactory`才能使用JMS，通常你不需要直接使用它，而是依赖于上层消息抽象（具体参考Spring框架的[相关章节]
    (http://docs.spring.io/spring/docs/4.1.4.RELEASE/spring-framework-reference/htmlsingle/#jms)），Spring Boot会自动配置
    发送和接收消息需要的设施（infrastructure）。

### 32.1.1 ActiveQ支持

    如果发现ActiveMQ在classpath下可用，Spring Boot会配置一个`ConnectionFactory`。如果需要代理，将会开启一个内嵌的，已经自动配置好的
    代理（只要配置中没有指定代理URL）。
    
    ActiveMQ是通过`spring.activemq.*`外部配置来控制的，例如，你可能在`application.properties`中声明以下片段：
    ```java
    spring.activemq.broker-url=tcp://192.168.1.210:9876
    spring.activemq.user=admin
    spring.activemq.password=secret
    ```
    具体参考[ActiveMQProperties](http://github.com/spring-projects/spring-boot/tree/master/spring-boot-autoconfigure/src
    /main/java/org/springframework/boot/autoconfigure/jms/activemq/ActiveMQProperties.java)。
    
    默认情况下，如果目标不存在，ActiveMQ将创建一个，所以目标是通过它们提供的名称解析出来的。

###32.1.2 Artemis支持

    Apache Artemis成立于2015年，那时HornetQ刚捐给Apache基金会，确保别使用了过期的HornetQ支持。
    **注** 不要尝试同时使用Artemis和HornetQ。
    
    如果发现classpath下存在Artemis依赖，Spring Boot将自动配置一个`ConnectionFactory`。如果需要broker，Spring Boot将启动内嵌的broker，
    并对其自动配置（除非模式mode属性被显式设置）。支持的modes包括：`embedded`（明确需要内嵌broker，如果classpath下不存在则出错），
    `native`（使用`netty`传输协议连接broker）。当配置`native`模式，Spring Boot将配置一个连接broker的`ConnectionFactory`，
    该broker使用默认的设置运行在本地机器。
    **注** 使用`spring-boot-starter-artemis` 'Starter'，则连接已存在的Artemis实例及Spring设施集成JMS所需依赖都会提供，添加
    `org.apache.activemq:artemis-jms-server`依赖，你可以使用内嵌模式。
    
    Artemis配置控制在外部配置属性`spring.artemis.*`中，例如，在`application.properties`声明以下片段：
    ```properties
    spring.artemis.mode=native
    spring.artemis.host=192.168.1.210
    spring.artemis.port=9876
    spring.artemis.user=admin
    spring.artemis.password=secret
    ```
    当使用内嵌模式时，你可以选择是否启用持久化，及目的地列表。这些可以通过逗号分割的列表来指定，也可以分别定义`org.apache.activemq
    .artemis.jms.server.config.JMSQueueConfiguration`或`org.apache.activemq.artemis.jms.server.config.TopicConfiguration`
    类型的bean来进一步配置队列和topic，具体支持选项可参考[ArtemisProperties](https://github.com/spring-projects/spring-boot/
    tree/v1.4.1.RELEASE/spring-boot-autoconfigure/src/main/java/org/springframework/boot/autoconfigure/jms/artemis/
    ArtemisProperties.java)。

###32.1.5 发送消息

    Spring的`JmsTemplate`会被自动配置，你可以将它直接注入到自己的beans中：
    ```java
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.jms.core.JmsTemplate;
    import org.springframework.stereotype.Component;
    @Component
    public class MyBean {
    private final JmsTemplate jmsTemplate;
    @Autowired
    public MyBean(JmsTemplate jmsTemplate) {
    this.jmsTemplate = jmsTemplate;
    }
    // ...
    }
    ```
    
    **注** 你可以使用相同方式注入[JmsMessagingTemplate](http://docs.spring.io/spring/docs/4.3.3.RELEASE/javadoc-api/org/springframework/jms/core/JmsMessagingTemplate.html)。如果定义了`DestinationResolver`或`MessageConverter` beans，它们将自动关联到自动配置的`JmsTemplate`。

###32.1.6 接收消息

    当JMS基础设施能够使用时，任何bean都能够被`@JmsListener`注解，以创建一个监听者端点。如果没有定义`JmsListenerContainerFactory`，将自动配置一个默认的。如果定义`DestinationResolver`或`MessageConverter` beans，它们将自动关联该默认factory。
    
    默认factory是事务性的，如果运行的设施出现`JtaTransactionManager`，它默认将关联到监听器容器。如果没有，`sessionTransacted`标记将启用。在后一场景中，你可以通过在监听器方法上添加`@Transactional`，以本地数据存储事务处理接收的消息，这可以确保接收的消息在本地事务完成后只确认一次。
    
    下面的组件创建了一个以`someQueue`为目标的监听器端点：
    ```java
    @Component
    public class MyBean {
    @JmsListener(destination = "someQueue")
    public void processMessage(String content) {
    // ...
    }
    }
    ```
    具体查看[@EnableJms javadoc](http://docs.spring.io/spring/docs/4.3.3.RELEASE/javadoc-api/org/springframework/jms/annotation/EnableJms.html)。
    
    如果想创建多个`JmsListenerContainerFactory`实例或覆盖默认实例，你可以使用Spring Boot提供的`DefaultJmsListenerContainerFactoryConfigurer`，通过它可以使用跟自动配置的实例相同配置来初始化一个`DefaultJmsListenerContainerFactory`。
    
    例如，以下使用一个特殊的`MessageConverter`创建另一个factory：
    ```java
    @Configuration
    static class JmsConfiguration {
    
        @Bean
        public DefaultJmsListenerContainerFactory myFactory(
                DefaultJmsListenerContainerFactoryConfigurer configurer) {
            DefaultJmsListenerContainerFactory factory =
                    new DefaultJmsListenerContainerFactory();
            configurer.configure(factory, connectionFactory());
            factory.setMessageConverter(myMessageConverter());
            return factory;
        }
    
    }
    ```
    然后，你可以像下面那样在任何`@JmsListener`注解中使用：
    ```java
    @Component
    public class MyBean {
    
        @JmsListener(destination = "someQueue", containerFactory="myFactory")
        public void processMessage(String content) {
            // ...
        }
    
    }
    ```

###32.2.1 RabbitMQ支持

    RabbitMQ是一个基于AMQP协议，轻量级的，可靠的，可扩展的和可移植的消息代理，Spring就使用它进行消息传递。RabbitMQ配置被外部属性
    `spring.rabbitmq.*`控制，例如，在`application.properties`中声明以下片段：
    ```properties
    spring.rabbitmq.host=localhost
    spring.rabbitmq.port=5672
    spring.rabbitmq.username=admin
    spring.rabbitmq.password=secret
    ```
    更多选项参考[RabbitProperties](https://github.com/spring-projects/spring-boot/tree/v1.4.1.RELEASE/
    spring-boot-autoconfigure/src/main/java/org/springframework/boot/autoconfigure/amqp/RabbitProperties.java)。


###32.2.2 发送消息

    Spring的`AmqpTemplate`和`AmqpAdmin`会被自动配置，你可以将它们直接注入beans中：
    ```java
    import org.springframework.amqp.core.AmqpAdmin;
    import org.springframework.amqp.core.AmqpTemplate;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Component;
    
    @Component
    public class MyBean {
    
        private final AmqpAdmin amqpAdmin;
        private final AmqpTemplate amqpTemplate;
    
        @Autowired
        public MyBean(AmqpAdmin amqpAdmin, AmqpTemplate amqpTemplate) {
            this.amqpAdmin = amqpAdmin;
            this.amqpTemplate = amqpTemplate;
        }
    
        // ...
    
    }
    ```
    **注** 可以使用相似方式注入`RabbitMessagingTemplate`，如果定义`MessageConverter` bean，它将自动关联到自动配置的`AmqpTemplate`。
    
    如果需要的话，所有定义为bean的`org.springframework.amqp.core.Queue`将自动在RabbitMQ实例中声明相应的队列。你可以启用
    `AmqpTemplate`的重试选项，例如代理连接丢失时，重试默认不启用。


###32.2.3 接收消息

    当Rabbit设施出现时，所有bean都可以注解`@RabbitListener`来创建一个监听器端点。如果没有定义`RabbitListenerContainerFactory`，
    Spring Boot将自动配置一个默认的。如果定义`MessageConverter` beans，它将自动关联到默认的factory。
    
    下面的组件创建一个`someQueue`队列上的监听器端点：
    ```java
    @Component
    public class MyBean {
    
        @RabbitListener(queues = "someQueue")
        public void processMessage(String content) {
            // ...
        }
    
    }
    ```
    **注** 具体参考[@EnableRabbit](http://docs.spring.io/spring-amqp/docs/current/api/org/springframework/amqp/rabbit/
    annotation/EnableRabbit.html)。
    
    如果需要创建多个`RabbitListenerContainerFactory`实例，或想覆盖默认实例，你可以使用Spring Boot提供的
    `SimpleRabbitListenerContainerFactoryConfigurer`，通过它可以使用跟自动配置实例相同的配置初始化`SimpleRabbitListenerContainerFactory`。
    
    例如，下面使用一个特殊的`MessageConverter`创建了另一个factory：
    ```java
    @Configuration
    static class RabbitConfiguration {
    
        @Bean
        public SimpleRabbitListenerContainerFactory myFactory(
                SimpleRabbitListenerContainerFactoryConfigurer configurer) {
            SimpleRabbitListenerContainerFactory factory =
                    new SimpleRabbitListenerContainerFactory();
            configurer.configure(factory, connectionFactory);
            factory.setMessageConverter(myMessageConverter());
            return factory;
        }
    
    }
    ```
    然后，你可以像下面那样在所有`@RabbitListener`注解方法中使用：
    ```java
    @Component
    public class MyBean {
    
        @RabbitListener(queues = "someQueue", containerFactory="myFactory")
        public void processMessage(String content) {
            // ...
        }
    
    }
    ```
    你可以启动重试处理那些监听器抛出异常的情况，当重试次数达到限制时，该消息将被拒绝，要不被丢弃，要不路由到一个dead-letter交换器，
    如果broker这样配置的话，默认禁用重试。

    **重要** 如果没启用重试，且监听器抛出异常，则Rabbit会不定期进行重试。你可以采用两种方式修改该行为：设置`defaultRequeueRejected`
    属性为`false`，这样就不会重试；或抛出一个`AmqpRejectAndDontRequeueException`异常表示该消息应该被拒绝，这是开启重试，且达到最大重试次数时使用的策略。

###32.2 AMQP

    高级消息队列协议（AMQP）是一个用于消息中间件的，平台无关的，线路级（wire-level）协议。Spring AMQP项目使用Spring的核心概念
    开发基于AMQP的消息解决方案，Spring Boot为通过RabbitMQ使用AMQP提供了一些便利，包括`spring-boot-starter-amqp`‘Starter’。


###33. 调用REST服务

    如果应用需要调用远程REST服务，你可以使用Spring框架的`RestTemplate`类。由于`RestTemplate`实例经常在使用前需要自定义，
    Spring Boot就没有提供任何自动配置的`RestTemplate` bean，不过你可以通过自动配置的`RestTemplateBuilder`创建自己需要的
    `RestTemplate`实例。自动配置的`RestTemplateBuilder`会确保应用到`RestTemplate`实例的`HttpMessageConverters`是合适的。
    
    以下是典型的示例：
    ```java
    @Service
    public class MyBean {
    
        private final RestTemplate restTemplate;
    
        public MyBean(RestTemplateBuilder restTemplateBuilder) {
            this.restTemplate = restTemplateBuilder.build();
        }
    
        public Details someRestCall(String name) {
            return this.restTemplate.getForObject("/{name}/details", Details.class, name);
        }
    
    }
    ```
    **注** `RestTemplateBuilder`包含很多有用的方法，可以用于快速配置一个`RestTemplate`。例如，你可以使用
    `builder.basicAuthorization("user", "password").build()`添加基本的认证支持（BASIC auth）。

###33.1 自定义RestTemplate

    当使用`RestTemplateBuilder`构建`RestTemplate`时，可以通过`RestTemplateCustomizer`进行更高级的定制，所有`RestTemplateCustomizer` beans将自动添加到自动配置的`RestTemplateBuilder`。此外，调用`additionalCustomizers(RestTemplateCustomizer…)`方法可以创建一个新的，具有其他customizers的`RestTemplateBuilder`。
    
    以下示例演示使用自定义器（customizer）配置所有hosts使用代理，除了`192.168.0.5`：
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
    
### 34. 发送邮件

    Spring框架通过`JavaMailSender`接口为发送邮件提供了一个简单的抽象，并且Spring Boot也为它提供了自动配置和一个starter模块。
    具体查看[JavaMailSender参考文档](http://docs.spring.io/spring/docs/4.3.3.RELEASE/spring-framework-reference/htmlsingle/#mail)。
    
    如果`spring.mail.host`和相关的libraries（通过`spring-boot-starter-mail`定义的）都可用，Spring Boot将创建一个默认的
    `JavaMailSender`，该sender可以通过`spring.mail`命名空间下的配置项进一步自定义，具体参考[MailProperties](http://github.com/
    spring-projects/spring-boot/tree/master/spring-boot-autoconfigure/src/main/java/org/springframework/boot/
    autoconfigure/mail/MailProperties.java)。

    
### 35. 使用JTA处理分布式事务

    Spring Boot通过[Atomkos](http://www.atomikos.com/)或[Bitronix](http://docs.codehaus.org/display/BTM/Home)的
    内嵌事务管理器支持跨多个XA资源的分布式JTA事务，当部署到恰当的J2EE应用服务器时也会支持JTA事务。
    
    当发现JTA环境时，Spring Boot将使用Spring的`JtaTransactionManager`来管理事务。自动配置的JMS，DataSource和JPA　beans将被升级
    以支持XA事务。你可以使用标准的Spring idioms，比如`@Transactional`，来参与到一个分布式事务中。如果处于JTA环境，但仍想使用本地事务，
    你可以将`spring.jta.enabled`属性设置为`false`来禁用JTA自动配置功能。

###35.1 使用Atomikos事务管理器

    Atomikos是一个非常流行的开源事务管理器，并且可以嵌入到你的Spring Boot应用中。你可以使用`spring-boot-starter-jta-atomikos`
    Starter去获取正确的Atomikos库。Spring Boot会自动配置Atomikos，并将合适的`depends-on`应用到你的Spring Beans上，确保它们以正确的顺序启动和关闭。
    
    默认情况下，Atomikos事务日志将被记录在应用home目录（你的应用jar文件放置的目录）下的`transaction-logs`文件夹中。你可以在
    `application.properties`文件中通过设置`spring.jta.log-dir`属性来定义该目录，以`spring.jta.atomikos.properties`开头的属性能
    用来定义Atomikos的`UserTransactionServiceIml`实现，具体参考[AtomikosProperties javadoc](http://docs.spring.io/spring-boot/
    docs/1.4.1.RELEASE/api/org/springframework/boot/jta/atomikos/AtomikosProperties.html)。
    
    **注** 为了确保多个事务管理器能够安全地和相应的资源管理器配合，每个Atomikos实例必须设置一个唯一的ID。默认情况下，该ID是Atomikos
    实例运行的机器上的IP地址。为了确保生产环境中该ID的唯一性，你需要为应用的每个实例设置不同的`spring.jta.transaction-manager-id`属性值。


###35.2 使用Bitronix事务管理器

    Bitronix是一个流行的开源JTA事务管理器实现，你可以使用`spring-boot-starter-jta-bitronix`starter为项目添加合适的Birtronix依赖。
    和Atomikos类似，Spring Boot将自动配置Bitronix，并对beans进行后处理（post-process）以确保它们以正确的顺序启动和关闭。
    
    默认情况下，Bitronix事务日志（`part1.btm`和`part2.btm`）将被记录到应用home目录下的`transaction-logs`文件夹中，你可以通过设置
    `spring.jta.log-dir`属性来自定义该目录。以`spring.jta.bitronix.properties`开头的属性将被绑定到`bitronix.tm.Configuration` 
    bean，你可以通过这完成进一步的自定义，具体参考[Bitronix文档](https://github.com/bitronix/btm/wiki/Transaction-manager-configuration)。
    
    **注** 为了确保多个事务管理器能够安全地和相应的资源管理器配合，每个Bitronix实例必须设置一个唯一的ID。默认情况下，该ID是Bitronix实例
    运行的机器上的IP地址。为了确保生产环境中该ID的唯一性，你需要为应用的每个实例设置不同的`spring.jta.transaction-manager-id`属性值。


###35.4 使用J2EE管理的事务管理器

    如果你将Spring Boot应用打包为一个`war`或`ear`文件，并将它部署到一个J2EE的应用服务器中，那你就能使用应用服务器内建的事务管理器。
    Spring Boot将尝试通过查找常见的JNDI路径（`java:comp/UserTransaction`, `java:comp/TransactionManager`等）来自动配置一个
    事务管理器。如果使用应用服务器提供的事务服务，你通常需要确保所有的资源都被应用服务器管理，并通过JNDI暴露出去。Spring Boot通过查找
    JNDI路径`java:/JmsXA`或`java:/XAConnectionFactory`获取一个`ConnectionFactory`来自动配置JMS，并且你可以使用
    `spring.datasource.jndi-name`属性配置你的`DataSource`。

###35.5 混合XA和non-XA的JMS连接

    当使用JTA时，primary JMS `ConnectionFactory`bean将能识别XA，并参与到分布式事务中。有些情况下，你可能需要使用non-XA的
    `ConnectionFactory`去处理一些JMS消息。例如，你的JMS处理逻辑可能比XA超时时间长。
    
    如果想使用一个non-XA的`ConnectionFactory`，你可以注入`nonXaJmsConnectionFactory`　bean而不是`@Primary`
     `jmsConnectionFactory` bean。为了保持一致，`jmsConnectionFactory`　bean将以别名`xaJmsConnectionFactor`来被使用。
    
    示例如下：
    ```java
    // Inject the primary (XA aware) ConnectionFactory
    @Autowired
    private ConnectionFactory defaultConnectionFactory;
    // Inject the XA aware ConnectionFactory (uses the alias and injects the same as above)
    @Autowired
    @Qualifier("xaJmsConnectionFactory")
    private ConnectionFactory xaConnectionFactory;
    // Inject the non-XA aware ConnectionFactory
    @Autowired
    @Qualifier("nonXaJmsConnectionFactory")
    private ConnectionFactory nonXaConnectionFactory;
    ```
    
###36. Hazelcast

    如果添加hazelcast依赖，Spring Boot将自动配置一个`HazelcastInstance`，你可以注入到应用中，`HazelcastInstance`实例只有存在相关
    配置时才会创建。
    如果定义了`com.hazelcast.config.Config` bean，则Spring Boot将使用它。如果你的配置指定了实例的名称，Spring Boot将尝试定位已
    存在的而不是创建一个新实例。你可以在配置中指定将要使用的`hazelcast.xml`配置文件：
    ```properties
    spring.hazelcast.config=classpath:config/my-hazelcast.xml
    ```
    否则，Spring Boot尝试从默认路径查找Hazelcast配置，也就是`hazelcast.xml`所在的工作路径或classpath的根路径。Spring Boot也会检查
    是否设置`hazelcast.config`系统属性，具体参考[Hazelcast文档](http://docs.hazelcast.org/docs/latest/manual/html-single/)。
    
    **注** Spring Boot为Hazelcast提供了缓存支持，如果开启缓存的话，`HazelcastInstance`实例将自动包装进一个`CacheManager`实现中。


###38. Spring Session

    Spring Boot为Spring Session自动配置了各种存储：
    
    * JDBC
    * MongoDB
    * Redis
    * Hazelcast
    * HashMap
    
    如果Spring Session可用，你只需选择想要的存储sessions的存储类型[StoreType](https://github.com/spring-projects/spring-boot/
    tree/v1.4.1.RELEASE/spring-boot-autoconfigure/src/main/java/org/springframework/boot/autoconfigure/session/
    StoreType.java)。例如，按如下配置将使用JDBC作为后端存储：
    ```properties
    spring.session.store-type=jdbc
    ```
    **注** 出于向后兼容，如果Redis可用，Spring Session将自动配置使用Redis存储。
    
    **注** 设置`store-type`为`none`可以禁用Spring Session。
    
    每个存储都有特殊设置，例如，对于jdbc存储可自定义表名：
    ```properties
    spring.session.jdbc.table-name=SESSIONS
    ```
    
###40. 测试

    Spring Boot提供很多有用的工具类和注解用于帮助你测试应用，主要分两个模块：`spring-boot-test`包含核心组件，
    `spring-boot-test-autoconfigure`为测试提供自动配置。
    
    大多数开发者只需要引用`spring-boot-starter-test` ‘Starter’，它既提供Spring Boot测试模块，也提供JUnit，AssertJ，
    Hamcrest和很多有用的依赖。

    
###40.1 测试作用域依赖

    如果使用`spring-boot-starter-test` ‘Starter’（在`test``scope`内），你将发现下列被提供的库：
    
    - [JUnit](http://junit.org/) - 事实上的(de-facto)标准，用于Java应用的单元测试。
    - [Spring Test](http://docs.spring.io/spring/docs/4.3.3.RELEASE/spring-framework-reference/htmlsingle/
    #integration-testing.html) & Spring Boot Test  - 对Spring应用的集成测试支持。
    - [AssertJ](http://joel-costigliola.github.io/assertj/) - 一个流式断言库。
    - [Hamcrest](http://hamcrest.org/JavaHamcrest/) - 一个匹配对象的库（也称为约束或前置条件）。
    - [Mockito](http://mockito.org/) - 一个Java模拟框架。
    - [JSONassert](https://github.com/skyscreamer/JSONassert) - 一个针对JSON的断言库。
    - [JsonPath](https://github.com/jayway/JsonPath) - 用于JSON的XPath。
    
    这是写测试用例经常用到的库，如果它们不能满足要求，你可以随意添加其他的依赖。

    
###40.3.1 发现测试配置

    如果熟悉Spring测试框架，你可能经常通过`@ContextConfiguration(classes=…)`指定加载哪些Spring `@Configuration`，也可能经常在
    测试类中使用内嵌`@Configuration`类。当测试Spring Boot应用时这些就不需要了，Spring Boot的`@*Test`注解会自动搜索主配置类，
    即使你没有显式定义它。
    
    搜索算法是从包含测试类的package开始搜索，直到发现`@SpringBootApplication`或`@SpringBootConfiguration`注解的类，只要按
    [恰当的方式组织代码](http://docs.spring.io/spring-boot/docs/1.4.1.RELEASE/reference/htmlsingle/
    #using-boot-structuring-your-code)，通常都会发现主配置类。
    
    如果想自定义主配置类，你可以使用一个内嵌的`@TestConfiguration`类。不像内嵌的`@Configuration`类（会替换应用主配置类），
    内嵌的`@TestConfiguration`类是可以跟应用主配置类一块使用的。
    
    **注** Spring测试框架在测试过程中会缓存应用上下文，因此，只要你的测试共享相同的配置（不管是怎么发现的），加载上下文的潜在时间消耗都只会发生一次。


###40.3.2 排除测试配置

    如果应用使用组件扫描，比如`@SpringBootApplication`或`@ComponentScan`，你可能发现为测试类创建的组件或配置在任何地方都可能偶然扫描到。
    为了防止这种情况，Spring Boot提供了`@TestComponent`和`@TestConfiguration`注解，可用在`src/test/java`目录下的类，以暗示它们不应该被扫描。
    
    **注** 只有上层类需要`@TestComponent`和`@TestConfiguration`注解，如果你在测试类（任何有`@Test`方法或`@RunWith`注解的类）中定义
    `@Configuration`或`@Component`内部类，它们将被自动过滤。
    
    **注** 如果直接使用`@ComponentScan`（比如不通过`@SpringBootApplication`），你需要为它注册`TypeExcludeFilter`，具体参考
    [Javadoc](http://docs.spring.io/spring-boot/docs/1.4.1.RELEASE/api/org/springframework/boot/context/TypeExcludeFilter.html)。


###40.3.3 使用随机端口

    如果你需要为测试启动一个完整运行的服务器，我们建议你使用随机端口。如果你使用`@SpringBootTest(webEnvironment=WebEnvironment
    .RANDOM_PORT)`，每次运行测试都会为你分配一个可用的随机端口。
    
    `@LocalServerPort`注解用于[注入测试用例实际使用的端口](http://docs.spring.io/spring-boot/docs/1.4.1.RELEASE/reference/
    htmlsingle/#howto-discover-the-http-port-at-runtime)，简单起见，需要发起REST调用到启动服务器的测试可以额外`@Autowire`一个
    `TestRestTemplate`，它可以解析到运行服务器的相关链接：
    ```java
    import org.junit.*;
    import org.junit.runner.*;
    import org.springframework.boot.test.context.web.*;
    import org.springframework.boot.test.web.client.*;
    import org.springframework.test.context.junit4.*;
    
    import static org.assertj.core.api.Assertions.*
    
    @RunWith(SpringRunner.class)
    @SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
    public class MyWebIntegrationTests {
    
        @Autowired
        private TestRestTemplate restTemplate;
    
        @Test
        public void exampleTest() {
            String body = this.restTemplate.getForObject("/", String.class);
            assertThat(body).isEqualTo("Hello World");
        }
    
    }
    ```

###40.3.4 模拟和监视beans

    有时候需要在运行测试用例时mock一些组件，例如，你可能需要一些远程服务的门面，但在开发期间不可用。Mocking在模拟真实环境很难复现的失败情况时非常有用。
    
    Spring Boot提供一个`@MockBean`注解，可用于为`ApplicationContext`中的bean定义一个Mockito mock，你可以使用该注解添加新beans，或替换已存在的bean定义。该注解可直接用于测试类，也可用于测试类的字段，或用于`@Configuration`注解的类和字段。当用于字段时，创建mock的实例也会被注入。Mock beans每次调用完测试方法后会自动重置。
    
    下面是一个典型示例，演示使用mock实现替换真实存在的`RemoteService` bean：
    ```java
    import org.junit.*;
    import org.junit.runner.*;
    import org.springframework.beans.factory.annotation.*;
    import org.springframework.boot.test.context.*;
    import org.springframework.boot.test.mock.mockito.*;
    import org.springframework.test.context.junit4.*;
    
    import static org.assertj.core.api.Assertions.*;
    import static org.mockito.BDDMockito.*;
    
    @RunWith(SpringRunner.class)
    @SpringBootTest
    public class MyTests {
    
        @MockBean
        private RemoteService remoteService;
    
        @Autowired
        private Reverser reverser;
    
        @Test
        public void exampleTest() {
            // RemoteService has been injected into the reverser bean
            given(this.remoteService.someCall()).willReturn("mock");
            String reverse = reverser.reverseSomeCall();
            assertThat(reverse).isEqualTo("kcom");
        }
    
    }
    ```
    此外，你可以使用`@SpyBean`和Mockito `spy`包装一个已存在的bean，具体参考文档。
    
###40.3.6 自动配置的JSON测试

    你可以使用`@JsonTest`测试对象JSON序列化和反序列化是否工作正常，该注解将自动配置Jackson `ObjectMapper`，`@JsonComponent`和
    Jackson `Modules`。如果碰巧使用gson代替Jackson，该注解将配置`Gson`。使用`@AutoConfigureJsonTesters`可以配置auto-configuration的元素。
    
    Spring Boot提供基于AssertJ的帮助类（helpers），可用来配合JSONassert和JsonPath libraries检测JSON是否为期望的，`JacksonHelper`，
    `GsonHelper`，`BasicJsonTester`分别用于Jackson，Gson，Strings。当使用`@JsonTest`时，你可以在测试类中`@Autowired`任何helper字段：
    ```java
    import org.junit.*;
    import org.junit.runner.*;
    import org.springframework.beans.factory.annotation.*;
    import org.springframework.boot.test.autoconfigure.json.*;
    import org.springframework.boot.test.context.*;
    import org.springframework.boot.test.json.*;
    import org.springframework.test.context.junit4.*;
    
    import static org.assertj.core.api.Assertions.*;
    
    @RunWith(SpringRunner.class)
    @JsonTest
    public class MyJsonTests {
    
        @Autowired
        private JacksonTester<VehicleDetails> json;
    
        @Test
        public void testSerialize() throws Exception {
            VehicleDetails details = new VehicleDetails("Honda", "Civic");
            // Assert against a `.json` file in the same package as the test
            assertThat(this.json.write(details)).isEqualToJson("expected.json");
            // Or use JSON path based assertions
            assertThat(this.json.write(details)).hasJsonPathStringValue("@.make");
            assertThat(this.json.write(details)).extractingJsonPathStringValue("@.make")
                    .isEqualTo("Honda");
        }
    
        @Test
        public void testDeserialize() throws Exception {
            String content = "{\"make\":\"Ford\",\"model\":\"Focus\"}";
            assertThat(this.json.parse(content))
                    .isEqualTo(new VehicleDetails("Ford", "Focus"));
            assertThat(this.json.parseObject(content).getMake()).isEqualTo("Ford");
        }
    
    }
    ```
    **注** JSON帮助类可用于标准单元测试类，如果没有使用`@JsonTest`，你需要在`@Before`方法中调用帮助类的`initFields`方法。
    
    在[附录](http://docs.spring.io/spring-boot/docs/1.4.1.RELEASE/reference/htmlsingle/#test-auto-configuration)中可以
    查看`@JsonTest`开启的自动配置列表。
    

###40.3.7 自动配置的Spring MVC测试

    你可以使用`@WebMvcTest`检测Spring MVC控制器是否工作正常，该注解将自动配置Spring MVC设施，并且只扫描注解`@Controller`，
    `@ControllerAdvice`，`@JsonComponent`，`Filter`，`WebMvcConfigurer`和`HandlerMethodArgumentResolver`的beans，
    其他常规的`@Component` beans将不会被扫描。
    
    通常`@WebMvcTest`只限于单个控制器（controller）使用，并结合`@MockBean`以提供需要的协作者（collaborators）的mock实现。
    `@WebMvcTest`也会自动配置`MockMvc`，Mock MVC为快速测试MVC控制器提供了一种强大的方式，并且不需要启动一个完整的HTTP服务器。
    
    **注** 使用`@AutoConfigureMockMvc`注解一个non-`@WebMvcTest`的类（比如`SpringBootTest`）也可以自动配置`MockMvc`。
    
    ```java
    import org.junit.*;
    import org.junit.runner.*;
    import org.springframework.beans.factory.annotation.*;
    import org.springframework.boot.test.autoconfigure.web.servlet.*;
    import org.springframework.boot.test.mock.mockito.*;
    
    import static org.assertj.core.api.Assertions.*;
    import static org.mockito.BDDMockito.*;
    import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
    import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
    
    @RunWith(SpringRunner.class)
    @WebMvcTest(UserVehicleController.class)
    public class MyControllerTests {
    
        @Autowired
        private MockMvc mvc;
    
        @MockBean
        private UserVehicleService userVehicleService;
    
        @Test
        public void testExample() throws Exception {
            given(this.userVehicleService.getVehicleDetails("sboot"))
                    .willReturn(new VehicleDetails("Honda", "Civic"));
            this.mvc.perform(get("/sboot/vehicle").accept(MediaType.TEXT_PLAIN))
                    .andExpect(status().isOk()).andExpect(content().string("Honda Civic"));
        }
    
    }
    ```
    **注** 如果需要定义自定配置（auto-configuration）的元素（比如什么时候使用servlet filters），你可以使用`@AutoConfigureMockMvc`的属性。
    
    如果你使用HtmlUnit或Selenium， 自动配置将提供一个`WebClient` bean和/或`WebDriver` bean，以下是使用HtmlUnit的示例：
    ```java
    import com.gargoylesoftware.htmlunit.*;
    import org.junit.*;
    import org.junit.runner.*;
    import org.springframework.beans.factory.annotation.*;
    import org.springframework.boot.test.autoconfigure.web.servlet.*;
    import org.springframework.boot.test.mock.mockito.*;
    
    import static org.assertj.core.api.Assertions.*;
    import static org.mockito.BDDMockito.*;
    
    @RunWith(SpringRunner.class)
    @WebMvcTest(UserVehicleController.class)
    public class MyHtmlUnitTests {
    
        @Autowired
        private WebClient webClient;
    
        @MockBean
        private UserVehicleService userVehicleService;
    
        @Test
        public void testExample() throws Exception {
            given(this.userVehicleService.getVehicleDetails("sboot"))
                    .willReturn(new VehicleDetails("Honda", "Civic"));
            HtmlPage page = this.webClient.getPage("/sboot/vehicle.html");
            assertThat(page.getBody().getTextContent()).isEqualTo("Honda Civic");
        }
    
    }
    ```
    在[附录](http://docs.spring.io/spring-boot/docs/1.4.1.RELEASE/reference/htmlsingle/#test-auto-configuration)中
    可以查看`@WebMvcTest`开启的自动配置列表。

###40.3.8 自动配置的Data JPA测试

    你可以使用`@DataJpaTest`测试JPA应用，它默认配置一个内存型的内嵌数据库，扫描`@Entity`类，并配置Spring Data JPA仓库，其他常规的
    `@Component` beans不会加载进`ApplicationContext`。
    
    Data JPA测试类是事务型的，默认在每个测试结束后回滚，具体查看Spring参考文档的[相关章节](http://docs.spring.io/spring/docs/
    4.3.3.RELEASE/spring-framework-reference/htmlsingle#testcontext-tx-enabling-transactions)。如果这不是你想要的结果，
    可以通过禁用事务管理器来改变：
    ```java
    import org.junit.Test;
    import org.junit.runner.RunWith;
    import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
    import org.springframework.test.context.junit4.SpringRunner;
    import org.springframework.transaction.annotation.Propagation;
    import org.springframework.transaction.annotation.Transactional;
    
    @RunWith(SpringRunner.class)
    @DataJpaTest
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public class ExampleNonTransactionalTests {
    
    }
    ```
    Data JPA测试类可能会注入一个专为测试设计的`[TestEntityManager](https://github.com/spring-projects/spring-boot/tree/
    v1.4.1.RELEASE/spring-boot-test-autoconfigure/src/main/java/org/springframework/boot/test/autoconfigure/orm/jpa/
    TestEntityManager.java)` bean以替换标准的JPA `EntityManager`。如果想在`@DataJpaTests`外使用`TestEntityManager`，你可以
    使用`@AutoConfigureTestEntityManager`注解。如果需要，`JdbcTemplate `也是可用的。
    ```java
    import org.junit.*;
    import org.junit.runner.*;
    import org.springframework.boot.test.autoconfigure.orm.jpa.*;
    
    import static org.assertj.core.api.Assertions.*;
    
    @RunWith(SpringRunner.class)
    @DataJpaTest
    public class ExampleRepositoryTests {
    
        @Autowired
        private TestEntityManager entityManager;
    
        @Autowired
        private UserRepository repository;
    
        @Test
        public void testExample() throws Exception {
            this.entityManager.persist(new User("sboot", "1234"));
            User user = this.repository.findByUsername("sboot");
            assertThat(user.getUsername()).isEqualTo("sboot");
            assertThat(user.getVin()).isEqualTo("1234");
        }
    
    }
    ```
    对于测试来说，内存型的内嵌数据库通常是足够的，因为它们既快又不需要任何安装。如果比较喜欢在真实数据库上运行测试，你可以使用
    `@AutoConfigureTestDatabase`注解：
    ```java
    @RunWith(SpringRunner.class)
    @DataJpaTest
    @AutoConfigureTestDatabase(replace=Replace.NONE)
    public class ExampleRepositoryTests {
    
        // ...
    
    }
    ```
    在[附录](http://docs.spring.io/spring-boot/docs/1.4.1.RELEASE/reference/htmlsingle/#test-auto-configuration)中可以
    查看`@DataJpaTest`开启的自动配置列表。
    
    
###40.3.9 自动配置的REST客户端

    你可以使用`@RestClientTest`测试REST客户端，它默认会自动配置Jackson和GSON，配置`RestTemplateBuilder`，并添加
    `MockRestServiceServer`支持。你需要将`@RestClientTest`的`value`或`components`属性值设置为待测试类：
    ```java
    @RunWith(SpringRunner.class)
    @RestClientTest(RemoteVehicleDetailsService.class)
    public class ExampleRestClientTest {
    
        @Autowired
        private RemoteVehicleDetailsService service;
    
        @Autowired
        private MockRestServiceServer server;
    
        @Test
        public void getVehicleDetailsWhenResultIsSuccessShouldReturnDetails()
                throws Exception {
            this.server.expect(requestTo("/greet/details"))
                    .andRespond(withSuccess("hello", MediaType.TEXT_PLAIN));
            String greeting = this.service.callRestService();
            assertThat(greeting).isEqualTo("hello");
        }
    
    }
    ```
    在[附录](http://docs.spring.io/spring-boot/docs/1.4.1.RELEASE/reference/htmlsingle/#test-auto-configuration)中
    可以查看`@RestClientTest`启用的自动配置列表。
    
###40.3.10 自动配置的Spring REST Docs测试

    如果想在测试类中使用Spring REST Docs，你可以使用`@AutoConfigureRestDocs`注解，它会自动配置`MockMvc`去使用Spring REST Docs，
    并移除对Spring REST Docs的JUnit规则的需要。
    ```java
    import org.junit.Test;
    import org.junit.runner.RunWith;
    
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
    import org.springframework.http.MediaType;
    import org.springframework.test.context.junit4.SpringRunner;
    import org.springframework.test.web.servlet.MockMvc;
    
    import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
    import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
    import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
    
    @RunWith(SpringRunner.class)
    @WebMvcTest(UserController.class)
    @AutoConfigureRestDocs("target/generated-snippets")
    public class UserDocumentationTests {
    
        @Autowired
        private MockMvc mvc;
    
        @Test
        public void listUsers() throws Exception {
            this.mvc.perform(get("/users").accept(MediaType.TEXT_PLAIN))
                    .andExpect(status().isOk())
                    .andDo(document("list-users"));
        }
    
    }
    ```
    此外，除了配置输出目录，`@AutoConfigureRestDocs`也能配置将出现在任何文档化的URLs中的部分，比如host，scheme和port等。
    如果需要控制更多Spring REST Docs的配置，你可以使用`RestDocsMockMvcConfigurationCustomizer` bean：
    ```java
    @TestConfiguration
    static class CustomizationConfiguration
            implements RestDocsMockMvcConfigurationCustomizer {
    
        @Override
        public void customize(MockMvcRestDocumentationConfigurer configurer) {
            configurer.snippets().withTemplateFormat(TemplateFormats.markdown());
        }
    
    }
    ```
    如果想充分利用Spring REST Docs对参数化输出目录的支持，你可以创建一个`RestDocumentationResultHandler` bean，自动配置将使用
    它调用`alwaysDo`方法，进而促使每个`MockMvc`调用都会自动产生默认片段：
    ```java
    @TestConfiguration
    static class ResultHandlerConfiguration {
    
        @Bean
        public RestDocumentationResultHandler restDocumentation() {
            return MockMvcRestDocumentation.document("{method-name}");
        }
    
    }
    ```
        
###40.3.11 使用Spock测试Spring Boot应用

    如果想使用Spock测试Spring Boot应用，你需要为应用添加Spock的`spock-spring`依赖，该依赖已将Spring测试框架集成进Spock，
    怎么使用Spock测试Spring Boot应用取决于你使用的Spock版本。
    
    **注** Spring Boot为Spock 1.0提供依赖管理，如果希望使用Spock 1.1，你需要覆盖`build.gradle`或`pom.xml`文件中的`spock.version`属性。
    
    当使用Spock 1.1时，只能使用[上述注解](http://docs.spring.io/spring-boot/docs/1.4.1.RELEASE/reference/htmlsingle/
    #boot-features-testing-spring-boot-applications)，你可以使用`@SpringBootTest`注解你的`Specification`以满足测试需求。
    
    当使用Spock 1.0时，`@SpringBootTest`将不能用于web项目，你需要使用`@SpringApplicationConfiguration`和`@WebIntegrationTest(randomPort = true)`。
    不能使用`@SpringBootTest`也就意味着你失去了自动配置的`TestRestTemplate` bean，不过可以通过以下配置创建一个等价的bean：
    ```java
    @Configuration
    static class TestRestTemplateConfiguration {
    
        @Bean
        public TestRestTemplate testRestTemplate(
                ObjectProvider<RestTemplateBuilder> builderProvider,
                Environment environment) {
            RestTemplateBuilder builder = builderProvider.getIfAvailable();
            TestRestTemplate template = builder == null ? new TestRestTemplate()
                    : new TestRestTemplate(builder.build());
            template.setUriTemplateHandler(new LocalHostUriTemplateHandler(environment));
            return template;
        }
    
    }
    ```

###40.3 测试Spring Boot应用

    Spring Boot应用只是一个Spring `ApplicationContext`，所以在测试时对它只需要像处理普通Spring context那样即可。唯一需要注意的是，
    如果你使用`SpringApplication`创建上下文，外部配置，日志和Spring Boot的其他特性只会在默认的上下文中起作用。
    
    Spring Boot提供一个`@SpringApplicationConfiguration`注解用于替换标准的`spring-test` `@ContextConfiguration`注解，
    该组件工作方式是通过`SpringApplication`创建用于测试的`ApplicationContext`。
    
    你可以使用`@SpringBootTest`的`webEnvironment`属性定义怎么运行测试：
    
    * `MOCK` - 加载`WebApplicationContext`，并提供一个mock servlet环境，使用该注解时内嵌servlet容器将不会启动。如果classpath下
    不存在servlet APIs，该模式将创建一个常规的non-web `ApplicationContext`。
    
    * `RANDOM_PORT` - 加载`EmbeddedWebApplicationContext`，并提供一个真实的servlet环境。使用该模式内嵌容器将启动，并监听在一个随机端口。
    
    * `DEFINED_PORT` - 加载`EmbeddedWebApplicationContext`，并提供一个真实的servlet环境。使用该模式内嵌容器将启动，并监听一个
    定义好的端口（比如`application.properties`中定义的或默认的`8080`端口）。
    
    * `NONE` - 使用`SpringApplication`加载一个`ApplicationContext`，但不提供任何servlet环境（不管是mock还是其他）。
    
    **注** 不要忘记在测试用例上添加`@RunWith(SpringRunner.class)`，否则该注解将被忽略。


###40.4.1 ConfigFileApplicationContextInitializer

    `ConfigFileApplicationContextInitializer`是一个`ApplicationContextInitializer`，可在测试类中用于加载Spring Boot的
    `application.properties`文件。当不需要使用`@SpringBootTest`提供的全部特性时，你可以使用它。
    
    ```java
    @ContextConfiguration(classes = Config.class,initializers = ConfigFileApplicationContextInitializer.class)
    ```
    **注** 单独使用`ConfigFileApplicationContextInitializer`不会提供`@Value("${…}")`注入支持，它只负责确保
    `application.properties`文件加载进Spring的`Environment`。为了`@Value`支持，你需要额外配置一个`PropertySourcesPlaceholderConfigurer`
    或使用`@SpringBootTest`为你自动配置一个。


###40.4.2 EnvironmentTestUtils

    使用简单的`key=value`字符串调用`EnvironmentTestUtils`就可以快速添加属性到`ConfigurableEnvironment`或`ConfigurableApplicationContext`：
    ```java
    EnvironmentTestUtils.addEnvironment(env, "org=Spring", "name=Boot");
    
###40.4.4 TestRestTemplate
    
    在集成测试中,`TestRestTemplate`是Spring `RestTemplate`的便利替代。你可以获取一个普通的或发送基本HTTP认证（使用用户名和密码）
    的模板，不管哪种情况，
    这些模板都有益于测试：不允许重定向（这样你可以对响应地址进行断言），忽略cookies（这样模板就是无状态的），对于服务端错误不会抛出异常。
    推荐使用Apache HTTP Client(4.3.2或更高版本)，但不强制这样做，如果相关库在classpath下存在，`TestRestTemplate`将以正确配置的client进行响应。
    ```java
    public class MyTest {
    RestTemplate template = new TestRestTemplate();
    @Test
    public void testRequest() throws Exception {
    HttpHeaders headers = template.getForEntity("http://myhost.com", String.class).getHeaders();
    assertThat(headers.getLocation().toString(), containsString("myotherhost"));
    }
    }
    ```
    如果正在使用`@SpringBootTest`，且设置了`WebEnvironment.RANDOM_PORT`或`WebEnvironment.DEFINED_PORT`属性，你可以注入一个
    配置完全的`TestRestTemplate`，并开始使用它。如果有需要，你还可以通过`RestTemplateBuilder` bean进行额外的自定义：
    ```java
    @RunWith(SpringRunner.class)
    @SpringBootTest
    public class MyTest {
    
        @Autowired
        private TestRestTemplate template;
    
        @Test
        public void testRequest() throws Exception {
            HttpHeaders headers = template.getForEntity("http://myhost.com", String.class).getHeaders();
            assertThat(headers.getLocation().toString(), containsString("myotherhost"));
        }
    
        @TestConfiguration
        static class Config {
    
            @Bean
            public RestTemplateBuilder restTemplateBuilder() {
                return new RestTemplateBuilder()
                    .additionalMessageConverters(...)
                    .customizers(...);
            }
    
        }
    
    }
    ```

###43. 创建自己的auto-configuration

    如果你在公司里开发共享libraries，或者正在开发一个开源或商业library，你可能想开发自己的自动配置（auto-configuration）。自动配置类
    可以打包到外部jars，并且依旧可以被Spring Boot识别。自动配置可以关联一个"starter"，用于提供auto-configuration的代码及需要引用的
    libraries。我们首先讲解构建自己的auto-configuration需要知道哪些内容，然后讲解[创建自定义starter的常见步骤](http://docs.spring
    .io/spring-boot/docs/1.4.1.RELEASE/reference/htmlsingle/#boot-features-custom-starter)。
    
    **注** 可参考[demo工程](https://github.com/snicoll-demos/spring-boot-master-auto-configuration)了解如何一步步创建一个starter。
    
###43.1 理解自动配置的beans

    从底层来讲，自动配置（auto-configuration）是通过标准的`@Configuration`类实现的。此外，`@Conditional`注解用来约束自动配置
    生效的条件。通常自动配置类需要使用`@ConditionalOnClass`和`@ConditionalOnMissingBean`注解，这是为了确保只有在相关的类被发现及
    没有声明自定义的`@Configuration`时才应用自动配置，具体查看[`spring-boot-autoconfigure`](https://github.com/spring-projects/
    spring-boot/tree/v1.4.1.RELEASE/spring-boot-autoconfigure/src/main/java/org/springframework/boot/autoconfigure)源码
    中的`@Configuration`类（`META-INF/spring.factories`文件）。


###43.2 定位自动配置候选者

    Spring Boot会检查你发布的jar中是否存在`META-INF/spring.factories`文件，该文件中以`EnableAutoConfiguration`为key的属性应该列出你的配置类：
    ```java
    org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
    com.mycorp.libx.autoconfigure.LibXAutoConfiguration,\
    com.mycorp.libx.autoconfigure.LibXWebAutoConfiguration
    ```
    你可以使用[`@AutoConfigureAfter`](http://github.com/spring-projects/spring-boot/tree/master/spring-boot-autoconfigure/
    src/main/java/org/springframework/boot/autoconfigure/AutoConfigureAfter.java)或[`@AutoConfigureBefore`](http://github
    .com/spring-projects/spring-boot/tree/master/spring-boot-autoconfigure/src/main/java/org/springframework/boot/
    autoconfigure/AutoConfigureBefore.java)注解为配置类指定特定的顺序。例如，如果你提供web-specific配置，你的类就需要应用在
    `WebMvcAutoConfiguration`后面。
    
    你也可以使用`@AutoconfigureOrder`注解为那些相互不知道存在的自动配置类提供排序，该注解语义跟常规的`@Order`注解相同，但专为自动配置类提供顺序。
    
    **注** 自动配置类只能通过这种方式加载，确保它们定义在一个特殊的package中，特别是不能成为组件扫描的目标。
    
       
###43.3.1 Class条件

    `@ConditionalOnClass`和`@ConditionalOnMissingClass`注解可以根据特定类是否出现来决定配置的包含，由于注解元数据是使用[ASM]
    (http://asm.ow2.org/)来解析的，所以你可以使用`value`属性来引用真正的类，即使该类没有出现在运行应用的classpath下，也可以使用
    `name`属性如果你倾向于使用字符串作为类名。
     
###43.4 创建自己的starter

    一个完整的Spring Boot starter可能包含以下组件：
    
    * `autoconfigure`模块，包含自动配置类的代码。
    * `starter`模块，提供自动配置模块及其他有用的依赖，简而言之，添加本starter就能开始使用该library。
    
    **注** 如果不需要将它们分离开来，你可以将自动配置代码和依赖管理放到一个单一模块中。

     
        
        
