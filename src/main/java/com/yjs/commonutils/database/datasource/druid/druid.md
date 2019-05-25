#Druid连接池基本配置
> https://www.jianshu.com/p/4cb04939e370

以下为Spring Boot配置Druid

## 一、pom.xml配置

<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>druid</artifactId>
   //此版本有bug，部署多个项目到tomcat，会无法启动，报错名字冲突
    <!--<version>1.0.18</version>-->
    <version>1.0.25</version>
</dependency>

<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
</dependency>

## 二、数据库加密
java –cp druid-1.0.18.jar com.alibaba.druid.filter.config.ConfigTools 你的密码

## 三、application.properties配置
- 数据源
```text
    spring.datasource.type=com.alibaba.druid.pool.DruidDataSource
    spring.datasource.url= jdbc:mysql://10.105.10.33:3306/blacklist?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&allowMultiQueries=true
    spring.datasource.driverClassName=com.mysql.jdbc.Driver
    spring.datasource.username=bigdata
    spring.datasource.password=${password}
```    

- 数据库密码加密
```shell
java –cp druid-1.0.18.jar com.alibaba.druid.filter.config.ConfigTools 你的密码
privateKey:MIIBVQIBADANBgkqhkiG9w0BAQEFAASCAT8wggE7AgEAAkEAnSwp/IIJrOgv06BfLXxFhMUHoM+yK44fQnqV9A8P//WiG2SLD0lbGMkONtdwG6YDeAy/y3vlIZFgB7aDhSG+RwIDAQABAkB1jI1OXBdOaU0z0tK8WyBHP3EIFa5kouPAPZyfLxDBVlABdlnJyDXW6hhiWrkQS7LoH0YAOZ94RTigtBNa5g35AiEA32IvnrWvelOdGiFuDGyh9hcOVS+hFMcOfGRmjmnpzysCIQC0HxZ6SrSVqhS1xFi8k8DqibSPE+D1JOe3uBMq/F1fVQIhAKSpZrrR6ID+Y37gh5Nm/Fg/lJQcoNuFA9uT0rlFv1CzAiEAtAOl2U60MCetTwQOk1kvordBZwU8/IOHucsUDQ/u2YkCIGnkvkhFXbrhNxU0goqxJWVibcbleQf8c/xE8fpnBhpx
publicKey:MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAJ0sKfyCCazoL9OgXy18RYTFB6DPsiuOH0J6lfQPD//1ohtkiw9JWxjJDjbXcBumA3gMv8t75SGRYAe2g4UhvkcCAwEAAQ==
password:XLusBJvkC6kQkNWOv9dhR9ZuRijdyUqNxhux65GIpu8A4Br/Fv60g0UwxyLCXXeWo0bT4xVQMdNERUXFApBbDg==
```
- 下面为连接池的补充设置，应用到上面所有数据源中
```text
// 初始化大小，最小，最大
spring.datasource.initialSize=5
spring.datasource.minIdle=5
spring.datasource.maxActive=20
// 配置获取连接等待超时的时间
spring.datasource.maxWait=60000
// 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒 
spring.datasource.timeBetweenEvictionRunsMillis=60000
// 配置一个连接在池中最小生存的时间，单位是毫秒 
spring.datasource.minEvictableIdleTimeMillis=300000
spring.datasource.validationQuery=SELECT 1
spring.datasource.testWhileIdle=true
spring.datasource.testOnBorrow=false
spring.datasource.testOnReturn=false
// 打开PSCache，并且指定每个连接上PSCache的大小 
spring.datasource.poolPreparedStatements=true
spring.datasource.maxPoolPreparedStatementPerConnectionSize=20
// 配置监控统计拦截的filters，去掉后监控界面sql无法统计，'wall'用于防火墙 
spring.datasource.filters=stat,wall,log4j,config
// 通过connectProperties属性来打开mergeSql功能；慢SQL记录
spring.datasource.connectionProperties=druid.stat.mergeSql=true;druid.stat.slowSqlMillis=5000;config.decrypt=true;config.decrypt.key=${publicKey}
```

- config配置类
由于spring boot 1.40目前还不直接支持druid，所以需要手动配置DataSource

```java
package com.xxxx.config;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;

/**
 * Druid监控web配置
 * 
 * @author jinxiaoxin
 *
 */
@Configuration
public class DruidConfig {
    @Bean
    public ServletRegistrationBean druidServlet() {
        ServletRegistrationBean reg = new ServletRegistrationBean();
        reg.setServlet(new StatViewServlet());
        // 登录URL http://localhost:8080/d/login.html
        reg.addUrlMappings("/druid/*");
        // 设置白名单
        reg.addInitParameter("allow", "10.105.0.220");
        // 设置黑名单
        reg.addInitParameter("deny", "");
        // 设置登录查看信息的账号密码.
        reg.addInitParameter("loginUsername", "admin");
        reg.addInitParameter("loginPassword", "admin");
        return reg;
    }

    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new WebStatFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.addInitParameter("exclusions",
                "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
        return filterRegistrationBean;
    }

    @Bean
    public DataSource druidDataSource(
            @Value("${spring.datasource.driverClassName}") String driver,
            @Value("${spring.datasource.url}") String url,
            @Value("${spring.datasource.username}") String username,
            @Value("${spring.datasource.password}") String password,
            @Value("${publicKey}") String publicKey,
            @Value("${spring.datasource.initialSize}") int initialSize,
            @Value("${spring.datasource.minIdle}") int minIdle,
            @Value("${spring.datasource.maxActive}") int maxActive,
            @Value("${spring.datasource.maxWait}") long maxWait,
            @Value("${spring.datasource.timeBetweenEvictionRunsMillis}") long timeBetweenEvictionRunsMillis,
            @Value("${spring.datasource.minEvictableIdleTimeMillis}") long minEvictableIdleTimeMillis,
            @Value("${spring.datasource.validationQuery}") String validationQuery,
            @Value("${spring.datasource.testWhileIdle}") boolean testWhileIdle,
            @Value("${spring.datasource.testOnBorrow}") boolean testOnBorrow,
            @Value("${spring.datasource.testOnReturn}") boolean testOnReturn,
            @Value("${spring.datasource.poolPreparedStatements}") boolean poolPreparedStatements,
            @Value("${spring.datasource.maxPoolPreparedStatementPerConnectionSize}") int maxPoolPreparedStatementPerConnectionSize,
            @Value("${spring.datasource.filters}") String filters,
            @Value("${spring.datasource.connectionProperties}") String connectionProperties) {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setDriverClassName(driver);
        druidDataSource.setUrl(url);
        druidDataSource.setUsername(username);
        druidDataSource.setPassword(password);
        druidDataSource.setInitialSize(initialSize);
        druidDataSource.setMaxActive(maxActive);
        druidDataSource.setMaxWait(maxWait);
        druidDataSource
                .setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        druidDataSource
                .setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        druidDataSource.setValidationQuery(validationQuery);
        druidDataSource.setTestWhileIdle(testWhileIdle);
        druidDataSource.setTestOnBorrow(testOnBorrow);
        druidDataSource.setTestOnReturn(testOnReturn);
        druidDataSource.setPoolPreparedStatements(poolPreparedStatements);
        druidDataSource
                .setMaxPoolPreparedStatementPerConnectionSize(maxPoolPreparedStatementPerConnectionSize);
        druidDataSource.setConnectionProperties(connectionProperties);
        try {
            druidDataSource.setFilters(filters);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return druidDataSource;
    }
}
```
## 四、application配置
加上@ComponentScan注解

```java
package com.xxxx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.transaction.annotation.EnableTransactionManagement;

//默认属性使用 @Configuration ， @EnableAutoConfiguration 和 @ComponentScan 
@SpringBootApplication
// 启注解事务管理
@EnableTransactionManagement
@EnableConfigurationProperties
public class App extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(
            SpringApplicationBuilder application) {
        return application.sources(App.class);
    }

}
```
## 五、Druid web ui地址
http://localhost:8080/druid/login.html
用户名admin
密码admin


