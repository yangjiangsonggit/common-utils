eureka 高可用
==============

 建立多个eureka server 服务,假设现在有两个eureka server注册中心

譬如eureka.client.register-with-eureka和fetch-registry是否要配置，配不配区别在哪里；eureka的客户端添加service-url时，是不是需要把所有的eureka的server地址都写上，还是只需要写一个server就可以了（因为server之间已经相互注册了）？如果写上了所有的server地址，那相当于将每个client服务都往所有的server都添加了一遍，那还配置server间的相互注册有什么意义？

上面的这些问题在多数讲eureka集群教程里都没有说明白，上来就是配server相互注册，client添加所有的server地址，大大的误导了我一把。专门从头新建了项目来看看到底eureka集群是该怎么配置。

server端配置
创建个eureka server项目
pom.xml如下：
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
 
	<groupId>com.tianyalei</groupId>
	<artifactId>eureka_server</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<packaging>jar</packaging>
 
	<name>eureka_server</name>
	<description>Demo project for Spring Boot</description>
 
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>1.5.7.RELEASE</version>
		<relativePath/> <!-- lookup parent from repository -->
	</parent>
 
	<properties>
		<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
		<project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
		<java.version>1.8</java.version>
		<spring-cloud.version>Dalston.SR4</spring-cloud.version>
	</properties>
 
	<dependencies>
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-eureka-server</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
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
application.yml如下
spring:
  application:
    name: eureka
  profiles:
    active: server1
我用同一个项目打算启动两个server服务，占用不同的端口，以此模拟eureka服务集群。
添加了一个application-server1.yml
server:
  port: 20001
eureka:
  instance:
    hostname: server1
  client:
    # 表示是否注册自身到eureka服务器
    # register-with-eureka: false
    # 是否从eureka上获取注册信息
    # fetch-registry: false
    service-url:
      defaultZone: http://server2:20002/eureka/
再添加一个application-server2.yml
server:
  port: 20002
eureka:
  instance:
    hostname: server2
  client:
    #register-with-eureka: false
    #fetch-registry: false
    service-url:
      defaultZone: http://server1:20001/eureka/
可以看到我指定了不同的端口，并且service-url这里是重点，我将server1的service-url设置为server2，将server2的设置为server1.以此完成两个server服务间的相互注册，这和别的文章里讲的是一样的，照着敲就OK。还有我把register和fetch那两个配置注释掉了，后面我们会放开，看看有没有这个配置的区别。
instance.hostname是唯一标识。
由于我们使用了http://server1这种写法，需要配一下host。Windows的host在/etc/host，mac的在/private/etc


然后在启动类上加上EnableEurekaServer注解即可。
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {
 
	public static void main(String[] args) {
		SpringApplication.run(EurekaServerApplication.class, args);
	}
}
下面我们来看如何分别用server1和server2两个配置启动两个server服务。
在idea右上角run，选择edit configrations 




原本应该只有一个启动项，点击+号，给两个启动项都设置一下Program arguments，就是--spring.profiles.active分别设置为server1和server2，代表分别以两个不同的配置来启动项目。
然后把两个启动项都启动起来，分别访问各自的端口





可以看到图上registered-replicas和available-replicas分别有了对方的地址。
eureka服务端的配置就这样就OK了。
client端配置
新建一个eureka client项目。
pom如下
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
 
	<groupId>com.tianyalei</groupId>
	<artifactId>eureka_client</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<packaging>jar</packaging>
 
	<name>eureka_client</name>
	<description>Demo project for Spring Boot</description>
 
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>1.5.7.RELEASE</version>
		<relativePath/> <!-- lookup parent from repository -->
	</parent>
 
	<properties>
		<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
		<project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
		<java.version>1.8</java.version>
		<spring-cloud.version>Dalston.SR4</spring-cloud.version>
	</properties>
 
	<dependencies>
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-eureka</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
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
application.yml如下
spring:
  application:
    name: eureka_client
eureka:
  client:
    service-url:
      defaultZone: http://server1:20001/eureka/
defaultZone这里代表eureka server的地址，很多文章在这里会将所有的server都配置上去，用逗号分隔，我们这里只配置一个server1，然后测试一下如果server1挂掉了，服务中心还能不能起作用。
在启动类上加注解eurekaClient注解
@SpringBootApplication
@EnableEurekaClient
public class EurekaClientApplication {
 
	public static void main(String[] args) {
		SpringApplication.run(EurekaClientApplication.class, args);
	}
}
然后启动项目
再看看server端的界面



可以看到eureka_client已经在两个server上都注册上了，虽然我们在client的yml里default_zone只配置了server1。这是因为eureka是通过在各个节点进行复制来达到高可用的目的。
测试很简单，我们直接关掉server1，然后看看server2是否还能维持住client的发现。
关掉server1，20001的网页已经打不开了，20002上也已经没有了20001的服务发现，控制台在一直报错。




但是我们看到client的注册信息还在，这说明server2还能继续提供注册发现的服务，这样就避免了单点故障后的整体服务发现的瘫痪。
但是需要注意的是，因为大家都在server1进行的注册，如果server1挂掉了，那么后续的client就无法再注册上来，重启Client后也无法再注册到eureka上来。
这就是为什么很多人在Client端添加多个eureka地址的原因！是避免自己注册的eureka单点挂掉。只要自己注册的eureka还在，那么后续添加N个eureka的其他server，所有注册信息都会被复制过去。

而事实上，eureka的注册信息不仅仅复制在server端，也会复制到client端。也就是说，即便eureka的server全部挂掉，client间任然是可以互通的！譬如client1 client2都已经注册在了server1 server2上，即便server1 server2全死掉了，Client1还是可以和client2进行互通，因为eureka的客户端也会复制所有的注册信息，当server全死掉后，客户端会根据自己本地的备份进行连接。

下面我们可以测试一下把server端yml里配置register-with-eureka: false的那两行注释给放开，看看eureka的server忽略自己后，是否能完成服务发现的高可用。
测试很简单，可以看到和上面的最终结果是一样的，都是server1关闭后，server2依旧能进行client的发现。区别在于


这个就是和之前注释掉后界面不同的地方。
至于在client端配置所有的server地址，各位可以自行尝试。