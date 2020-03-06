为Ribbon Client自定义配置
原创卷毛·和尚 最后发布于2019-05-08 21:25:43 阅读数 630  收藏
展开
配置ribbon有两种方式：java代码方式&文件配置方式
1.java代码方式
第一步：创建ribbon的配置类如

@Configuration
public class RibbonConfiguration {
    @Bean
    public IRule ribbonRule(){
        // 负载均衡规则改为随机
        return new RandomRule();
    }
}
注意：该类不应该被应用程序上下文的@ComponentScan注解扫描到
 

第二步：创建一个空类，并在其上添加@Configuration注解和@RibbonClient注解

@Configuration
@RibbonClient(name = "life-base",configuration = RibbonConfiguration.class)
public class ConfigRibbonClient {
}
这样就可以为指定的Ribbon Client：life-base服务采用RibbonConfiguration对应的ribbon配置
 

2.文件配置方式
ribbon的配置格式：<clientName> ：ribbon :需要配置的属性，<clientName>是Ribbon的客户端的名称，如果省略表示配置所有客户端

配置的属性有：

NFLoadBalancerClassName : 配置ILoadBalancer的实现类

NFLoadBalancerRuleClassName : 配置IRule的实现类

NFLoadBalancerPingClassName : 配置IPing的实现类

NIWSServerListClassName: 配置ServerList的实现类

NIWSServerListFilterClassName: 配置ServerListtFilter的实现类

如配置life-base这个Ribbon Client的负载均衡规则，在yml文件中可以这样配置

life-base：
  ribbon：
    NFLoadBalancerRuleClassName：com.netflix.loadbalancer.RandomRule
 

 

3.配置的优先级
配置文件的优先级 > java代码的配置方式 > netflix自定义的配置方式
