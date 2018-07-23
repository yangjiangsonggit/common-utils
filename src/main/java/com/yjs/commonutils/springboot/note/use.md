##使用"default"包

    当类没有声明package时，它被认为处于default package下。通常不推荐使用default package，因为对于使用@ComponentScan，
    @EntityScan或@SpringBootApplication注解的Spring Boot应用来说，它会扫描每个jar中的类，这会造成一定的问题。
 
    注意: 我们建议你遵循Java推荐的包命名规范，使用一个反转的域名（例如com.example.project）。
    
##放置应用的main类
  
    通常建议将应用的main类放到其他类所在包的顶层(root package)，并将@EnableAutoConfiguration注解到你的main类上，这样就隐式地定义
    了一个基础的包搜索路径（search package），以搜索某些特定的注解实体（比如@Service，@Component等） 。例如，如果你正在编写一个
    JPA应用，Spring将搜索@EnableAutoConfiguration注解的类所在包下的@Entity实体。
      
    采用root package方式，你就可以使用@ComponentScan注解而不需要指定basePackage属性，也可以使用@SpringBootApplication注解，
    只要将main类放到root package中。
    
##导入其他配置类
  
      你不需要将所有的@Configuration放进一个单独的类，@Import注解可以用来导入其他配置类。另外，你也可以使用@ComponentScan注解自动收集
      所有Spring组件，包括@Configuration类。
      
##导入XML配置
  
    如果必须使用XML配置，建议你仍旧从一个@Configuration类开始，然后使用@ImportResource注解加载XML配置文件。
    
##自动配置
  
      Spring Boot自动配置（auto-configuration）尝试根据添加的jar依赖自动配置你的Spring应用。例如，如果classpath下存在HSQLDB，
      并且你没有手动配置任何数据库连接的beans，那么Spring Boot将自动配置一个内存型（in-memory）数据库。
      
      实现自动配置有两种可选方式，分别是将@EnableAutoConfiguration或@SpringBootApplication注解到@Configuration类上。
      
      注：你应该只添加一个@EnableAutoConfiguration注解，通常建议将它添加到主配置类（primary @Configuration）上。
      
##禁用特定的自动配置项
  
      如果发现启用了不想要的自动配置项，你可以使用@EnableAutoConfiguration注解的exclude属性禁用它们：
      
      import org.springframework.boot.autoconfigure.*;
      import org.springframework.boot.autoconfigure.jdbc.*;
      import org.springframework.context.annotation.*;
      
      @Configuration
      @EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
      public class MyConfiguration {
      }
      如果该类不在classpath中，你可以使用该注解的excludeName属性，并指定全限定名来达到相同效果。最后，你可以通过
      spring.autoconfigure.exclude属性exclude多个自动配置项（一个自动配置项集合）。
      
      注 通过注解级别或exclude属性都可以定义排除项。
      
##使用@SpringBootApplication注解
  
      很多Spring Boot开发者经常使用@Configuration，@EnableAutoConfiguration，@ComponentScan注解他们的main类，
      由于这些注解如此频繁地一块使用（特别是遵循以上[最佳实践](14. Structuring your code.md)的时候），Spring Boot就提供
      了一个方便的@SpringBootApplication注解作为代替。
      
      @SpringBootApplication注解等价于以默认属性使用@Configuration，@EnableAutoConfiguration和@ComponentScan：
      
      package com.example.myproject;
      
      import org.springframework.boot.SpringApplication;
      import org.springframework.boot.autoconfigure.SpringBootApplication;
      
      @SpringBootApplication // same as @Configuration @EnableAutoConfiguration @ComponentScan
      public class Application {
      
          public static void main(String[] args) {
              SpringApplication.run(Application.class, args);
          }
      
      }
      注 @SpringBootApplication注解也提供了用于自定义@EnableAutoConfiguration和@ComponentScan属性的别名（aliases）。
      
