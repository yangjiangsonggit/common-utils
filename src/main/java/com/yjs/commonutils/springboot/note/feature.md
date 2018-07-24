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