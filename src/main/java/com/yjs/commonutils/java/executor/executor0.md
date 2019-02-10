谈谈我所熟悉的java中的多线程
2017年11月06日 14:28:05 小菜鸡你好 阅读数：343
 版权声明：本文为博主原创文章，未经博主允许不得转载。	https://blog.csdn.net/s15951044971/article/details/78457324
前段时间读了《Java并发编程实战》感觉受益良多，以此博客来记录下学习的知识点，以及感受

首先什么是进程和线程
1.进程：计算机系统上运行的一个最小的独立的单元 
2.线程：是进程里面运行的一个最新的单元，是cpu调度分配的最小单元

线程的生命周期
1.如图所示 
这里写图片描述

2.状态描述

new新建创建线程
start是线程处于就绪状态，等待cpu分配时间片
run获得cpu时间片，线程处于运行状态
join等待某个线程执行完毕，自己线程再执行
sleep线程休眠一段时间自动运行
yeild让出时间片，等待cpu重新分配
wait线程处于等待状态，等待其他线程唤醒
notify唤醒线程，使得线程处于就绪状态 
（注：wait和notify是Object的方法，推荐使用java.util.conncurren下的Condition类）
线程的创建方法
继承Thread类
实现Runnable接口（推荐使用，便于共享同一个变量，符合面向接口编程的思想）
多线程相关的知识点
线程私有的： 
pc计数器、栈内存

线程共享的 
堆内存、方法区 
也就是共享有状态的单例，以及类的静态变量

所以多线程并发问题都是不安全的操作了这两个共享变量引起的

引起不安全的线程操作的原因
原子性 
主要就是i++的操作 
1.1. 竞态条件 
共享变量作为了判断条件，比如单例模式的实现就存在竞态条件 
1.2. 复合操作 
非原子性操作
可见性 
工作内存：线程私有 
主内存：线程共享 
步骤将主内存的数据拿入栈中，直到出栈的时候写入主内存
使得线程安全的操作
原子性：加锁，采用synchronized或者lock
可见性：volatile，作用直接从主内存中取数据和操作数据，防止指令重排序
锁优化技术
锁范围优化：主要是锁的颗粒度变小，例如单例：

    private CommonSingleton obj;

    private CommonSingleton() {

    }

    public synchronized CommonSingleton newSingletonInstance() {
        if (obj == null) {
            obj = new CommonSingleton();
        }

        return obj;
    }
}


锁范围变小的单例：

public class SafeSingleton {

    private SafeSingleton obj;

    private SafeSingleton() {

    }

    public SafeSingleton newSingletonInstance() {
        if (obj == null) {
            synchronized (SafeSingleton.class) {
                if (obj == null) {
                    obj = new SafeSingleton();
                }

            }
        }

        return obj;
    }
}

锁分段技术：具体可以参考HashTable和ConcurrentHashMap,后这就是多个HashTable，加锁锁住整个对象，而是锁住一部分
读写锁分离：java.util.concurrent.locks.ReentrantReadWriteLock
线程之间的通信
Object对象自带的wait(),sleep(),notify(),yeild()
jdk1.4后支持的java.util.concurrent下的工具

Condition:负责线程的等待和唤醒

Semaphere:控制线程执行的数量
CyclicBarrier:达到一定数目一起执行
CountDownLatch:降到0一起执行
Exchanger:线程之间交换数据
线程池技术
线程池产生的原因：线程的过程主要分为三块，线程创建T1,线程执行T2，线程销毁T3,由于频繁的创建销毁线程比较耗费时间和占用系统的内存，所以产生了线程池，由线程池来负责T1和T3的操作，实现线程的复用

常用的线程池Executors来创建

newFixedThreadPool：固定长度的线程池，缓存队列是无界队列LinkedBlockingQueue

newCachedThreadPool:无长度的线程池，没有缓存队列，用的是SynchronousQueue
newSingledThreadPool:独立的线程池，和newFixedThreadPool的区别是corePoolSize和maximumPoolSize的值都是1
newSingleThreadScheduledExecutor:时间调度的线程池，用的是DelayedWorkQueue
线程池中的关键的参数
corePoolSize：核心的池数量，也就是初始的
maximumPoolSize：最大线程池数量
keepalivedTime:多久时间判断线程是否已经执行结束
timeUnit:时间单位
workQueue:缓存的线程队列
ThreadFactory:创建线程的工厂
RejectedExecutionHandler:拒绝策略
HashSet workers：运行的线程集 
## 线程池的运行步骤 ## 
首先线程池里面默认是没有线程的，当用户执行execute，判断当前运行的线程数量是否小于corePoolSize，小于则交由workers去直接创建线程，并将当前运行线程加1，如果大于了corePoolSize，则将线程放入workQueue中，等待workers里面有线程空闲出来，则去取，如果阻塞队列里面的超过maximumPoolSize，则由RejectedExecutionHandler去拒绝接收线程
    public static ExecutorService threadPool = new ThreadPoolExecutor(MAX_QUERY_THREAD_NUM, 200, 0L,
            TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(1024), new ThreadFactoryBuilder().build(),
            new ThreadPoolExecutor.AbortPolicy());

注：并将线程池弄成单例，放在独立的类里面，防止简历多个线程池，造成浪费

Callable和Future
通过线程池的submit方法执行，与Runable不同的地方就是Callable的call方法会返回一个对象，可用take()方法去获取，take()方法里面可以控制时间
CompleteService阻塞队列，控制线程一起返回一起执行
Synchronized的实现原理
在java堆内存中，其实对象里面有个对象头的概念，对象头里面存储着锁的相关信息
轻量级锁
偏移锁
自旋锁
重量级锁