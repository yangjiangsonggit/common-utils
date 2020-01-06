Spring的@Import注解与ImportAware接口


最近在使用Redisson做分布式会话管理时，为了扩展其功能，研究了它的@EnableRedissonHttpSession等部分源码，在其中发现了一个有趣的注解@Import和一个ImportAware接口

**遂查资料得到解释为：**

@Import接口的作用和Spring的xml配置文件中的<import>标签类似，可以导入另一个注解了@Configuration的配置类，也就是说，
如果项目中引用了一些第三方的类库，如我用到的Redisson库，其内部包含很多@Configuration注解的配置类，但是我的项目没有自动
扫描他的包，那么就可以用@Import(XXX.class)来导入其配置类使其生效。在Spring4.2以后，@Import还支持导入普通的没有
@Configuration注解的类，并将其实例化加入IOC容器中。而对于ImportAware接口，并没有查到其具体的相关资料。

查看@EnableRedissonHttpSession源码

```java
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Import({RedissonHttpSessionConfiguration.class})
@Configuration
public @interface EnableRedissonHttpSession {
    int maxInactiveIntervalInSeconds() default 1800;
 
    String keyPrefix() default "";
}
```

其上面包含了@Import({RedissonHttpSessionConfiguration.class}),所以,只要我们应用了这个注解,该配置类就会被自动引入,再看这段源代码,
上面有2个参数,一个是会话超时时间,另一个像是key的前缀,那么这两个参数是怎么生效的呢,继续查看RedissonHttpSessionConfiguration的源码

```java
@Configuration
public class RedissonHttpSessionConfiguration extends SpringHttpSessionConfiguration implements ImportAware {
    private Integer maxInactiveIntervalInSeconds;
    private String keyPrefix;
 
//...
    public void setImportMetadata(AnnotationMetadata importMetadata) {
        Map<String, Object> map = importMetadata.getAnnotationAttributes(EnableRedissonHttpSession.class.getName());
        AnnotationAttributes attrs = AnnotationAttributes.fromMap(map);
        this.keyPrefix = attrs.getString("keyPrefix");
        this.maxInactiveIntervalInSeconds = (Integer)attrs.getNumber("maxInactiveIntervalInSeconds");
    }
}
```

刚开始以为是由于这里面的两个参数与注解上同名,被自动赋值了,后来发现不对,注意到这个类注解了@Configuration并实现了ImportAware接口
的setImportMetadata方法,然后通过其metadata拿到了@EnableRedissonHttpSession注解上的这两个参数，这样就实现了通过注解参数来对
配置类作设置的功能（测试去除@Configuration注解，则ImportAware失效，无法触发setImportMetadata方法的调用）。
