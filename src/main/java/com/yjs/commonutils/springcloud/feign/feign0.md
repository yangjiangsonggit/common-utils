# FeignClient中的Hystrix使用（SpirngCloud2.0）


1.官方文档的误解(springcloud2.0)
之前一直以为feign中hystrix默认是开启的，但通过实际例子一直不起作用，最后手动设置true后居然起作用了。汗。。。。

feign:
  client:
    config:
      remote-service:           #服务名，default为所有服务
        connectTimeout: 10000
        readTimeout: 12000
  hystrix:
      enabled: true             #启用hystrix

默认开启会引起一些问题，已经默认为关闭了：
https://github.com/spring-cloud/spring-cloud-netflix/issues/1277

2.hystrix的默认超时时间
feign和hystrix配合使用过程中，hystrix的默认超时时间为1s，feign的结果1s内没返回，即认为超时，在实际应用中不是很适用，需要修改hystrix的超时时间。
最好设置和feign的超时时间一致。

hystrix:
  command:
      default:
        execution:
          isolation:
            thread:
              timeoutInMilliseconds: 10000

3.hystrix的fallback
启用熔断后，可以在feignclient上加fallback，fallback类实现feignclient，可以在发生错误的时候返回默认值。

@FeignClient(name = "remote-service",url = "http://baidu.com:8080",fallback = FeignTestServiceImpl.class)
public interface FeignTestService {

    @RequestMapping("/est")
    String get();
}
1
2
3
4
5
6
@Component
public class FeignTestServiceImpl implements FeignTestService{
    private Logger logger= LoggerFactory.getLogger(getClass());

    @Override
    public String get() {
        logger.info("fallback");
        return null;
    }
}

3.fallback和fallbackFactory
fallbackFactory里会有具体的错误信息。

@Component
public class FeignTestServiceImpl2 implements FallbackFactory<FeignTestService> {
    private Logger logger= LoggerFactory.getLogger(getClass());

    @Override
    public FeignTestService create(Throwable cause) {
        return new FeignTestService() {
            @Override
            public String get() {
                logger.error(cause.getMessage(),cause);
                return null;
            }
        };
    }
}

https://blog.csdn.net/VitaminZH/article/details/80905520