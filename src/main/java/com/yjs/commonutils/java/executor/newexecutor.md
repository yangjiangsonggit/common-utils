一、简单来说使用线程池有三个好处：

1、降低资源消耗：通过重复利用已创建的线程降低线程创建和销毁造成的消耗。

2、提高响应速度：当任务到达时，任务可以不需要的等到线程创建就能立即执行。

3、提高线程的可管理性：线程是稀缺资源，如果无限制的创建，不仅会消耗系统资源，还会降低系统的稳定性，使用线程池可以进行

统一的分配、调优和监控。

二、线程池的实现原理

当一个新任务提交到线程池时，简单来说线程池的处理流程如下：

#1、判断核心线程池里的线程是否都在执行任务，如果不是则创建一个新的工作线程处理任务，否则进入下个流程

#2、判断工作队列是否已满，如果未满则将新提交的任务存储在该工作队列，否则进入下个流程

#3、判断线程池里的线程是否都处于工作状态，如果不是则创建一个新的工作线程来执行任务，否则交由饱和策略处理

流程图如下：



三、一般情况下会使用Executors创建线程池，目前不推荐，线程池不允许使用Executors去创建，而是通过ThreadPoolExecutor方式，

这样的处理方式可以更加明确线程池的运行规则，规避资源耗尽的风险。

1、newFixedThreadPool和newSingleThreadExecutor： 主要问题是堆积的请求处理队列可能会耗费非常大的内存，甚至OOM

2、newCachedThreadPool和newScheduledThreadPool： 主要问题是线程数最大数是Integer.MAX_VALUE，可能会创建数量非常多的线程，甚

至OOM

ThreadPoolExecutor执行execute方法分以下4种情况：

  #1、如果当前运行的线程少于corePoolSize，则创建新的核心线程来执行任务，当前步骤需获取全局锁

  #2、如果运行的线程>=corePoolSize，则将任务加入阻塞队列

  #3、如果队列已满，则创建新的非核心线程来处理任务，该步骤也需要获取全局锁

  #4、如果创建新线程使当前运行的线程数>maxinumPoolSize，则任务将被拒绝并执行拒绝策略

根据阿里巴巴java开发规范，推荐了3种线程池创建方式，如下：

推荐方式1（使用了com.google.guava包）


	ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
            .setNameFormat("demo-pool-%d").build();
        ExecutorService executorService = new ThreadPoolExecutor(5, 10,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingDeque<Runnable>(1024), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());
推荐方式2（使用了commons-lang3包）：

	ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1,
        	new BasicThreadFactory.Builder().namingPattern("example-schedule-pool-%d").daemon(true).build());
推荐方式3（spring配置线程池方式）：自定义线程工厂bean需要实现ThreadFactory，可参考该接口的其它默认实现类，使用方式直接注入bean
调用execute(Runnable task)方法即可


    <!-- 添加默认实现 -->
    <bean id="threadFactory" class="java.util.concurrent.Executors$DefaultThreadFactory"/>
    <!-- 添加自定义实现 -->
    <bean id="threadFactoryNew" class="com.fc.provider.ThreadFactoryConsumer"/>
    <!-- 创建线程池 -->
    <bean id="userThreadPool" class="org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor">
        <property name="corePoolSize" value="5" />
        <property name="maxPoolSize" value="50" />
        <property name="queueCapacity" value="1000" />
        <property name="keepAliveSeconds" value="3000"/>
        <property name="threadFactory" ref="threadFactoryNew"/>
        <property name="rejectedExecutionHandler">
            <bean class="java.util.concurrent.ThreadPoolExecutor$CallerRunsPolicy"/>
        </property>
    </bean>
一个完整示例如下：

public class ThreadPoolDemo {
 
    /**
     * 定义静态内部线程类
     */
    public static class MyTask implements Runnable {
 
        @Override
        public void run() {
            System.out.println(System.currentTimeMillis() + ":Thread name:"
                + Thread.currentThread().getName());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
 
    /**
     * @param args
     */
    public static void main(String[] args) {
        MyTask myTask = new MyTask();
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
            .setNameFormat("demo-pool-%d").build();
        ExecutorService executorService = new ThreadPoolExecutor(5, 10,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingDeque<Runnable>(1024), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());
        for (int i = 0; i < 10; i++) {
            executorService.execute(myTask);
        }
        executorService.shutdown();
    }
}
