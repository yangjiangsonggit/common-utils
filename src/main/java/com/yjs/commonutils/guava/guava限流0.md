使用Guava实现限流器
2018年06月17日 13:39:09 程序员囧辉 阅读数：2144
 版权声明：本文为博主原创文章，未经博主允许不得转载。	https://blog.csdn.net/v123411739/article/details/80718198
为什么需要限流？
在开发高并发系统时有三把利器用来保护系统：缓存、降级和限流。限流可以认为服务降级的一种，限流通过限制请求的流量以达到保护系统的目的。



一般来说，系统的吞吐量是可以计算出一个阈值的，为了保证系统的稳定运行，一旦达到这个阈值，就需要限制流量并采取一些措施以完成限制流量的目的。比如：延迟处理，拒绝处理，或者部分拒绝处理等等。否则，很容易导致服务器的宕机。



现有的方案
Google的Guava工具包中就提供了一个限流工具类——RateLimiter，本文也是通过使用该工具类来实现限流功能。RateLimiter是基于“令牌通算法”来实现限流的。



令牌桶算法
令牌桶算法是一个存放固定容量令牌（token）的桶，按照固定速率往桶里添加令牌。令牌桶算法基本可以用下面的几个概念来描述：

假如用户配置的平均发送速率为r，则每隔1/r秒一个令牌被加入到桶中。

桶中最多存放b个令牌，当桶满时，新添加的令牌被丢弃或拒绝。

当一个n个字节大小的数据包到达，将从桶中删除n个令牌，接着数据包被发送到网络上。

如果桶中的令牌不足n个，则不会删除令牌，且该数据包将被限流（要么丢弃，要么缓冲区等待）。



限流器实现
1.pom文件中引入Guava包
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>23.0</version>
</dependency>


2.自定义拦截器，并在拦截器中实现限流
a）定义一个拦截器抽象类，用于多个拦截器复用，主要是继承HandlerInterceptorAdapter，重写preHandle方法；并提供preFilter抽象方法，供子类实现。





b）定义流量控制拦截器，流量控制拦截器继承自上面的拦截器抽象类，在preFilter方法中进行流量控制。

@Component("rateLimitInterceptor")
public class RateLimitInterceptor extends AbstractInterceptor {
    /**
     * 单机全局限流器(限制QPS为1)
     */
    private static final RateLimiter rateLimiter = RateLimiter.create(1);
    @Override
    protected ResponseEnum preFilter(HttpServletRequest request) {
        if (!rateLimiter.tryAcquire()) {
            System.out.println("限流中......");
            return ResponseEnum.RATE_LIMIT;
        }
        System.out.println("请求成功");
        return ResponseEnum.OK;
    }
}
使用Guava提供的RateLimiter类来实现流量控制，过程很简单：定义了一个QPS为1的全局限流器（便于测试），使用tryAcquire()方法来尝试获取令牌，如果成功则返回ResponseEnum.OK，否则返回ResponseEnum.RATE_LIMIT。



3.继承WebMvcConfigurerAdapter来添加自定义拦截器




4.写一个Controller来提供一个简单的访问接口
@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserPOMapper userPOMapper;
    @RequestMapping("getUserList")
    @ResponseBody
    public ResponseDTO getUserList() {
        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setCode(ResponseEnum.OK.getCode());
        UserPOExample userPOExample = new UserPOExample();
        UserPOExample.Criteria criteria = userPOExample.createCriteria();
        // and Name = 'admin'
        criteria.andNameEqualTo("admin");
        criteria.andPasswordEqualTo("admin");
        // and Age Between 24, 26
        criteria.andAgeBetween(24, 26);
        try {
            List<UserPO> userPOList = userPOMapper.selectByExample(userPOExample);
            responseDTO.setContent(userPOList);
            return responseDTO;
        } catch (Exception e) {
            responseDTO.setCode(ResponseEnum.QUERY_USER_FAILED.getCode());
            responseDTO.setMsg(ResponseEnum.QUERY_USER_FAILED.getMsg());
            return responseDTO;
        }
    }
}


上文使用到的ResponseEnum是一个返回Code的枚举：





所有文件的目录结构如下：





5.使用Postman来测试接口
快速并且反复的调用接口，可以很容易的看到两种结果。

成功通过限流器的结果：





没有成功通过限流器的返回结果：





反复调用时，Console输出如下：





至此，简单的限流器实现完成。



错误情况
如果在测试时，出现以下错误。





解决：在pom依赖中添加以下jar包即可解决。