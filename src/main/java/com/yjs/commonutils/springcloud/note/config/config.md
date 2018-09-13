在分布式系统中，每一个功能模块都能拆分成一个独立的服务，一次请求的完成，可能会调用很多个服务协调来完成，为了方便服务配置文件统一管理，更易于部署、维护，所以就需要分布式配置中心组件了，在spring cloud中，有分布式配置中心组件spring cloud config，它支持配置文件放在在配置服务的内存中，也支持放在远程Git仓库里。引入spring cloud config后，我们的外部配置文件就可以集中放置在一个git仓库里，再新建一个config server，用来管理所有的配置文件，维护的时候需要更改配置时，只需要在本地更改后，推送到远程仓库，所有的服务实例都可以通过config server来获取配置文件，这时每个服务实例就相当于配置服务的客户端config client,为了保证系统的稳定，配置服务端config server可以进行集群部署，即使某一个实例，因为某种原因不能提供服务，也还有其他的实例保证服务的继续进行。

分享一个在网上看到架构图：



 

下面就在前几篇的文章的基础上，作一些修改，实际感受一下。

一、新建一个maven项目：configServer

完整的pom.xml如下：

<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
   xsi:schemaLocation="http://maven.apache.org/POM/4.0.0http://maven.apache.org/xsd/maven-4.0.0.xsd">
   <modelVersion>4.0.0</modelVersion>

   <groupId>com.gaox.configServer</groupId>
   <artifactId>configServer</artifactId>
   <version>0.0.1-SNAPSHOT</version>
   <packaging>jar</packaging>

   <name>configServer</name>
   <description>Demo project for Spring Boot</description>

   <parent>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-parent</artifactId>
      <version>1.5.10.RELEASE</version>
      <relativePath/> <!-- lookup parent from repository -->
   </parent>

   <properties>
     <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
     <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
      <java.version>1.8</java.version>
      <spring-cloud.version>Edgware.SR2</spring-cloud.version>
   </properties>

   <dependencies>
      <dependency>
         <groupId>org.springframework.boot</groupId>
         <artifactId>spring-boot-starter-web</artifactId>
      </dependency>
      <dependency>
         <groupId>org.springframework.cloud</groupId>
         <artifactId>spring-cloud-config-server</artifactId>
      </dependency>
      <dependency>
         <groupId>org.springframework.cloud</groupId>
         <artifactId>spring-cloud-starter-eureka</artifactId>
      </dependency>

      <dependency>
         <groupId>org.springframework.boot</groupId>
         <artifactId>spring-boot-starter-test</artifactId>
         <scope>test</scope>
      </dependency>
   </dependencies>

   <dependencyManagement>
      <dependencies>
         <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>${spring-cloud.version}</version>
            <type>pom</type>
            <scope>import</scope>
         </dependency>
      </dependencies>
   </dependencyManagement>

   <build>
      <plugins>
         <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
         </plugin>
      </plugins>
   </build>


</project>

在本地仓库创建一个lisiService-dev.properties和lisiService-prod.properties的配置文件，然后推送到远程的git仓库(这里贴出我在码云上新建的远程仓库https://gitee.com/fox9916/springCloudConfig),

lisiService-dev.properties内容如下：

       name=lisi
       age=18
       version=dev
lisiService-prod.properties内容如下：

       name=lisi
       age=18
       version=prod
 

完整的application.properties:

#服务端口
server.port=8091
#服务名称
spring.application.name=configServer
#服务注册中心
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
#服务的git仓库地址
spring.cloud.config.server.git.uri=https://gitee.com/fox9916/springCloudConfig
#配置文件所在的目录
spring.cloud.config.server.git.search-paths=/**
#配置文件所在的分支
spring.cloud.config.label=master
#git仓库的用户名
spring.cloud.config.username=fox9916
#git仓库的密码
spring.cloud.config.password=********
在启动类上开启配置中心的注解:@EnableConfigServer

package com.gaox.configServer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableConfigServer
@EnableEurekaClient
public class ConfigServerApplication {

   public static void main(String[] args) {
      SpringApplication.run(ConfigServerApplication.class, args);
   }
}
 

服务端配置完成，下面验证一下服务端的配置有没有什么问题，依次启动serviceCenter、configServer，在浏览器里输入http://localhost:8091/lisiService/dev或

http://localhost:8091/lisiService-dev.properties或http://localhost:8091/master/lisiService-dev.properties后，返回内容：

{"name":"lisiService","profiles":["dev"],"label":null,"version":"86e81c9a9c191738398d7390efa7518a90e641c3","state":null,"propertySources":[{"name":"https://gitee.com/fox9916/springCloudConfig/lisiService-dev.properties","source":{"version":"dev","age":"18","name":"lisi"}}]}
从里可以看到lisiService-dev.properties里的内容，则说明配置没有问题，如果返回内容类似，没有isiService-dev.properties里的内容，说明配置有问题，没有读取到配置文件。

证明配置服务中心可以从远程程序获取配置信息，http请求地址和资源文件映射如下:，可参考

·        /{application}/{profile}[/{label}]

·        /{application}-{profile}.yml

·        /{label}/{application}-{profile}.yml

·        /{application}-{profile}.properties

·        /{label}/{application}-{profile}.properties

 

 

二、对上一篇文章中新建的lisiService作一些改动，验证一下是否能从远程仓库里读取到配置内容

在pom.xml里添加配置：

<dependency>
   <groupId>org.springframework.cloud</groupId>
   <artifactId>spring-cloud-starter-config</artifactId>
</dependency>
在resources目录下新建文件bootstrap.properties，内容：

#开启配置服务发现
spring.cloud.config.discovery.enabled=true
#配置服务实例名称
spring.cloud.config.discovery.service-id=configServer
#配置文件所在分支
spring.cloud.config.label=master
spring.cloud.config.profile=prod
#配置服务中心
spring.cloud.config.uri=http://localhost:8091/
Application.properties配置内容不变：

server.port=8765
spring.application.name=lisiService
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
feign.hystrix.enabled=true

 

LisiServerApplication.java：

package com.gaox.lisiService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
@RestController
@EnableHystrixDashboard
@EnableCircuitBreaker
public class LisiServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LisiServiceApplication.class, args);
    }


    @FeignClient(value = "helloService" ,fallback = HelloError.class)
    public interface HelloService {
        @RequestMapping(value = "/hello", method = RequestMethod.GET)
         String hello(@RequestParam("name") String name);
    }
    @Component
    public class HelloError implements HelloService {
        @Override
        public String hello(String name){
            return "hello  ,"+name+"!  sorry ,error !";
        }
    }
    @Autowired
   private HelloService helloService;

    @RequestMapping("/hello")
    public String hello(@RequestParam("name")String name){
    return helloService.hello(name);
    }
    @Value("${name}")
    private  String name;
    @Value("${age}")
    private  String age;
    @Value("${version}")
    private  String version="开发环境";

    @RequestMapping("/test")
    public String test(){
        return "你好，我是"+name+",年龄："+age+"岁。当前环境："+version;
    }
}
依次启动serviceCenter ，configService, lisiService ，打开浏览器访问：Http://localhost:8765/test,返回内容：

你好，我是lisi,年龄：18岁。当前环境：prod

这说明lisiService通过configService配置中心获取到了远程仓库spingCloudConfig的配置文件内容