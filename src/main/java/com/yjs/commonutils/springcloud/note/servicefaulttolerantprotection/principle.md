一、hystrix 产生背景
微服务是解决复杂服务的一个方案，在功能不变的情况下，对一个复杂的单体服务分解为多个可管理的分支。每个服务作为轻量的子服务，通过RPC实现服务间的关联，将服务简单化。每个服务根据自己的需要选择技术栈，互不影响，方便开发、维护。例如S划分为a,b,c。微服务的好处是有效的拆分应用，实现敏捷开发和部署。
微服务一系列优势下，也给微服务的管理和稳定性带来挑战，比如一个服务依赖30个微服务，每个微服务的可用性是99.999%，在不加任何管理的情况下，该聚合服务的可用性将是99.999%的30次方=99.97%，系统的可用性直接降了两个数量级达到三个九。
且由于依赖的传递性，很容易产生雪崩效应。如下图所示：

Paste_Image.png
Paste_Image.png
Paste_Image.png
一个应用中，任意一个点的不可用或者响应延时都有可能造成服务不可用
更可怕的是，被hang住的请求会很快耗尽系统的资源，当该类请求越来越多，占用的计算机资源越来越多的时候，会导致系统瓶颈出现，造成其他的请求同样不可用，最终导致业务系统崩溃，又称：雪崩效应
造成雪崩原因可以归结为以下三个：

服务提供者不可用（硬件故障，程序Bug，缓存击穿，用户大量请求）
重试加大流量（用户重试，代码逻辑重试）
服务调用者不可用（同步等待造成的资源耗尽）
最终的结果就是一个服务不可用导致一系列服务的不可用，而往往这种后果往往无法预料的。
二、 hystrix实现原理
hystrix语义为“豪猪”，具有自我保护的能力。hystrix的出现即为解决雪崩效应，它通过四个方面的机制来解决这个问题

隔离（线程池隔离和信号量隔离）：限制调用分布式服务的资源使用，某一个调用的服务出现问题不会影响其他服务调用。
优雅的降级机制：超时降级、资源不足时(线程或信号量)降级，降级后可以配合降级接口返回托底数据。
融断：当失败率达到阀值自动触发降级(如因网络故障/超时造成的失败率高)，熔断器触发的快速失败会进行快速恢复。
缓存：提供了请求缓存、请求合并实现。
支持实时监控、报警、控制（修改配置）
2.1 隔离
Paste_Image.png
（1）线程池隔离模式：使用一个线程池来存储当前的请求，线程池对请求作处理，设置任务返回处理超时时间，堆积的请求堆积入线程池队列。这种方式需要为每个依赖的服务申请线程池，有一定的资源消耗，好处是可以应对突发流量（流量洪峰来临时，处理不完可将数据存储到线程池队里慢慢处理）
（2）信号量隔离模式：使用一个原子计数器（或信号量）来记录当前有多少个线程在运行，请求来先判断计数器的数值，若超过设置的最大线程个数则丢弃改类型的新请求，若不超过则执行计数操作请求来计数器+1，请求返回计数器-1。这种方式是严格的控制线程且立即返回模式，无法应对突发流量（流量洪峰来临时，处理的线程超过数量，其他的请求会直接返回，不继续去请求依赖的服务）

区别（两种隔离方式只能选其一）：

线程池隔离	信号量隔离
线程	与调用线程非相同线程	与调用线程相同（jetty线程）
开销	排队、调度、上下文开销等	无线程切换，开销低
异步	支持	不支持
并发支持	支持（最大线程池大小）	支持（最大信号量上限）
2.2 融断
正常状态下，电路处于关闭状态(Closed)，如果调用持续出错或者超时，电路被打开进入熔断状态(Open)，后续一段时间内的所有调用都会被拒绝(Fail Fast)，一段时间以后，保护器会尝试进入半熔断状态(Half-Open)，允许少量请求进来尝试，如果调用仍然失败，则回到熔断状态，如果调用成功，则回到电路闭合状态;

Paste_Image.png
HystrixCircuitBreaker（断路器的具体实现）：

Paste_Image.png
详细的工作流程：http://hot66hot.iteye.com/blog/2155036

2.3 降级
可能大家会混淆“融断”和“降级”两个概念。
在股票市场，熔断这个词大家都不陌生，是指当股指波幅达到某个点后，交易所为控制风险采取的暂停交易措施。相应的，服务熔断一般是指软件系统中，由于某些原因使得服务出现了过载现象，为防止造成整个系统故障，从而采用的一种保护措施，所以很多地方把熔断亦称为过载保护。
大家都见过女生旅行吧，大号的旅行箱是必备物，平常走走近处绰绰有余，但一旦出个远门，再大的箱子都白搭了，怎么办呢？常见的情景就是把物品拿出来分分堆，比了又比，最后一些非必需品的就忍痛放下了，等到下次箱子够用了，再带上用一用。而服务降级，就是这么回事，整体资源快不够了，忍痛将某些服务先关掉，待渡过难关，再开启回来。
二者的目标是一致的，目的都是保证上游服务的稳定性。但其关注的重点并不一样，融断对下层依赖的服务并不级（或者说孰轻孰重），一旦产生故障就断掉；而降级需要对下层依赖的业务分级，把产生故障的丢了，换一个轻量级的方案，是一种退而求其次的方法。
根据业务场景的不同，一般采用以下两种模式：
第一种（最常用）如果服务失败，则我们通过fallback进行降级，返回静态值。

Paste_Image.png
第二种采用服务级联的模式，如果第一个服务失败，则调用备用服务，例如失败重试或者访问缓存失败再去取数据库。服务级联的目的则是尽最大努力保证返回数据的成功性，但如果考虑不充分，则有可能导致级联的服务崩溃（比如，缓存失败了，把全部流量打到数据库，瞬间导致数据库挂掉）。因此级联模式，也要慎用，增加了管理的难度。

Paste_Image.png
2.4 缓存
不建议使用，对问题排查会造成很大的困扰，因此也不在这里讲了

三、hystrix应用
hystrix的运行流程如下所示：

Paste_Image.png
两个核心代理HystrixCommand,HystrixObservableCommand，任何依赖的服务只需要继承这两个类就可以了。其中HystrixObservableCommand使用观察者模式（不在此介绍范围之内，了解请移步RxJava）
HystrixCommand 可以采用同步调用和异步调用，异步返回Future对象（还未直接支持CompletebleFuture）
如果开启了缓存，则会根据GroupKey,Commandkey以及cachedKey确定是否存在缓存（不建议使用）
判断断路器是否开启，开启则直接调用getFallback,
判断是否满足信号量隔离或线程池隔离的条件，如果隔离则抛异常
执行run方法
metrics包含了一个计数器，用来计算当前服务的状态，无论是成功调用，还是抛异常都会记录数据（接下来再详细讲）
执行降级策略
3.1 代码实现

public class GetInfoFromSinaiCommand extends HystrixCommand<List<PoiInfo>> {
    private PoiClient poiClient;
    private List<Integer> poiIds;
    private static final List<String> FIELDS = ImmutableList.of("id", "cate", "subcate");

    public GetInfoFromSinaiCommand(PoiClient poiClient, List<Integer> poiIds) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("sinai"))
                //command配置
                .andCommandKey(HystrixCommandKey.Factory.asKey("GetInfoFromSinaiCommand"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.THREAD))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter().withRequestCacheEnabled(true))

                //融断器配置
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter().withCircuitBreakerEnabled(true))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter().withCircuitBreakerRequestVolumeThreshold(20))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter().withCircuitBreakerSleepWindowInMilliseconds(5000))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter().withCircuitBreakerErrorThresholdPercentage(50))

                //ThreadPool配置
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("GetInfoFromSinaiCommand"))
                .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter().withCoreSize(10))
                .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter().withCoreSize(-1))

        );



        this.poiClient = poiClient;
        this.poiIds = poiIds;

    }

    @Override
    public List<PoiInfo> run() throws Exception {
        if (poiIds.isEmpty()) {
            return Lists.newArrayList();
        }
        List<PoiModel> pioModels = poiClient.listPois(poiIds, FIELDS);
        return parseResult(pioModels);
    }

    @Override
    protected String getCacheKey() {
        return String.valueOf(poiIds);
    }

    @Override
    protected List<PoiInfo> getFallback() {
        return Lists.newArrayList();
    }

    private List<PoiInfo> parseResult(List<PoiModel> poiModels) {
        if (poiModels == null || poiModels.isEmpty()) {
            return Lists.newArrayList();
        }
        List<PoiInfo> res = Lists.newArrayList();
        for (PoiModel poiModel : poiModels) {
            PoiInfo poiInfo = new PoiInfo();
            poiInfo.setPoiId(poiModel.getId());

            if (poiModel.getCate() != null) {
                poiInfo.setCate(poiModel.getCate());
            }
            if (poiModel.getSubcate() != null) {
                poiInfo.setSubcate(poiModel.getSubcate());
            }
            res.add(poiInfo);
        }
        return res;
    }
}

3.2 参数说明
|参数类型|参数名|默认值|说明|
|---|---|---|---|---|
|command配置|executionIsolationStrategy|ExecutionIsolationStrategy.THREAD|信号隔离或线程隔离，默认:采用线程隔离,|
|| executionIsolationThreadTimeoutInMillisecond |1s|隔离时间大，即多长时间后进行重试|
|| executionIsolationSemaphoreMaxConcurrentRequests |10|使用信号量隔离时，命令调用最大的并发数,默认:10 |
| |fallbackIsolationSemaphoreMaxConcurrentRequests |10|使用信号量隔离时，命令fallback(降级)调用最大的并发数,默认:10|
|| fallbackEnabled |true|是否开启fallback降级策略|
|| executionIsolationThreadInterruptOnTimeout |true|使用线程隔离时，是否对命令执行超时的线程调用中断（Thread.interrupt()）操作|
|| metricsRollingStatisticalWindowInMilliseconds |10000ms|统计滚动的时间窗口,默认:10s|
|| metricsRollingStatisticalWindowBuckets |10|统计窗口的Buckets的数量,默认:10个
|| metricsRollingPercentileEnabled |true|是否开启监控统计功能,默认:true|
|| requestLogEnabled |true|是否开启请求日志|
|| requestCacheEnabled |true|是否开启请求缓存|
|熔断器配置|circuitBreakerRequestVolumeThreshold|20|主要用在小流量|
|| circuitBreakerSleepWindowInMilliseconds | 5000ms |熔断器默认工作时间,默认:5秒.熔断器中断请求5秒后会进入半打开状态,放部分流量过去重试|
|| circuitBreakerEnabled | true |是否启用熔断器,默认true. 启动 |
|| circuitBreakerErrorThresholdPercentage | 50 |默认:50%。当出错率超过50%后熔断器启动|
|| circuitBreakerForceOpen | false |是否强制开启熔断器阻断所有请求,默认:false,不开启|
|| circuitBreakerForceClosed | false |是否允许熔断器忽略错误,默认false, 不开启|
|线程池配置|HystrixThreadPoolProperties.Setter().withCoreSize(int value)|10|配置线程池大小,默认值10个|
||HystrixThreadPoolProperties.Setter().withMaxQueueSize(int value)|-1|配置线程值等待队列长度|

3.3监控上报
参考文章：
本文的很多图和文字都粘贴自网上文章，没有注明引用请包涵！如有任何问题请留言或者加群，我会及时回复
http://zhuanlan.51cto.com/art/201704/536307.htm

作者：oneWeekOneTopic
链接：https://www.jianshu.com/p/e07661b9bae8
來源：简书
简书著作权归作者所有，任何形式的转载都请联系作者获得授权并注明出处。