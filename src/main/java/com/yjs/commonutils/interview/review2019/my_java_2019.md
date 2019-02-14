

## 1.java内存模型

        https://blog.csdn.net/tjiyu/article/details/53915869
        
        浅析java内存模型--JMM(Java Memory Model)
        https://www.cnblogs.com/lewis0077/p/5143268.html
        
        *主内存和工作内存
        *原子性,可见性,顺序性,happens-before
        *Java的内存结构，也就是运行时的数据区域
            1/PC寄存器/程序计数器
            2/Java栈 Java Stack
            3/堆 Heap
            4/方法区Method Area
            5/常量池Constant Pool
            6/本地方法栈Native Method Stack

## 2.类加载

     *JVM 类加载机制详解
     https://www.cnblogs.com/cxxjohnson/p/8653360.html
     
     启动类加载器(Bootstrap ClassLoader)：负责加载 JAVA_HOME\lib 目录中的，或通过-Xbootclasspath参数指定路径中的，
     且被虚拟机认可（按文件名识别，如rt.jar）的类。
     扩展类加载器(Extension ClassLoader)：负责加载 JAVA_HOME\lib\ext 目录中的，或通过java.ext.dirs系统变量指定路径中的类库。
     应用程序类加载器(Application ClassLoader)：负责加载用户路径（classpath）上的类库。


     当一个类加载器收到类加载任务，会先交给其父类加载器去完成，因此最终加载任务都会传递到顶层的启动类加载器，
     只有当父类加载器无法完成加载任务时，才会尝试执行加载任务。
     采用双亲委派的一个好处是比如加载位于rt.jar包中的类java.lang.Object，不管是哪个加载器加载这个类，
     最终都是委托给顶层的启动类加载器进行加载，这样就保证了使用不同的类加载器最终得到的都是同样一个Object对象。
     
     
     
     *JVM类加载机制与对象的生命周期
     https://www.cnblogs.com/cxxjohnson/p/8662370.html
     
     类的生命周期
         类的生命周期包括7个部分：加载——验证——准备——解析——初始化——使用——卸载
         
     类的初始化触发
         最常见的是前三种：实例化对象、读写静态对象、调用静态方法、反射机制调用类、调用子类触发父类初始化。
     
     *采用双亲委派模型的原因：避免同一个类被多个类加载器重复加载。

## 3.jvm

    *Java虚拟机垃圾回收(一) 基础 回收哪些内存/对象 
     引用计数算法 可达性分析算法 finalize()方法 HotSpot实现分析
        https://blog.csdn.net/tjiyu/article/details/53982412
        
        *判断对象可以回收
            引用计数算法
            
            可达性分析算法
                GC Roots对象
                可达性分析期间需要保证整个执行系统的一致性，对象的引用关系不能发生变化；
                导致GC进行时必须停顿所有Java执行线程（称为"Stop The World"）；
                
        *判断对象生存还是死亡
            第一次标记
            第二次标记
         
        *finalize()方法
            充当"安全网"
            与对象的本地对等体有关
         
        *安全点
            运行中，非常多的指令都会导致引用关系变化；
            如果为这些指令都生成对应的OopMap，需要的空间成本太高；     
            
            抢先式中断
            主动式中断
            
        *安全区域(解决安全点问题->线程sleep或者系统阻塞)
            
            指一段代码片段中，引用关系不会发生变化
            
            
        
     *Java虚拟机垃圾回收(二) 垃圾回收算法 
      标记-清除算法 复制算法 标记-整理算法 分代收集算法 火车算法
      https://blog.csdn.net/tjiyu/article/details/53983064
      
      
        *标记-清除算法
            针对老年代的CMS收集器；
        *复制算法算法
            Serial收集器
        *标记-整理算法
        *分代收集算法
        *火车算法
      
      
      
      
     *Java虚拟机垃圾回收(三) 7种垃圾收集器 
      主要特点 应用场景 设置参数 基本运行原理
      https://blog.csdn.net/tjiyu/article/details/53983650
      
      
         *吞吐量与收集器关注点说明
         
            吞吐量
            CPU用于运行用户代码的时间与CPU总消耗时间的比值；
            即吞吐量=运行用户代码时间/（运行用户代码时间+垃圾收集时间）；
            
            垃圾收集器期望的目标
            停顿时间 
            吞吐量
            覆盖区

      
      
      
     *Java虚拟机垃圾回收(四) 总结：
      内存分配与回收策略 方法区垃圾回收 以及 JVM垃圾回收的调优方法
      https://blog.csdn.net/tjiyu/article/details/54588494
      
         *内存分配与回收策略
            对象优先在Eden分配
            大对象直接进入老年代
            长期存活的对象将进入老年代(默认15)
            动态对象年龄判定
            
            
            
       
## 4.tomcat
        
     *Tomcat(一) Tomcat是什么：Tomcat与Java技术 Tomcat与Web应用 以及 Tomcat基本框架及相关配置
     https://blog.csdn.net/tjiyu/article/details/54590258
     
     *Tomcat(二) Tomcat实现：
      Servlet与web.xml介绍 以及 源码分析Tomcat实现细节
      https://blog.csdn.net/tjiyu/article/details/54590259
      
     *Tomcat(三) Tomcat安装配置：
      Tomcat+Nginx+keepalived 实现动静分离、Session会话保持的高可用集群
      https://blog.csdn.net/tjiyu/article/details/54591126
      
      

     
## 5.linux命令

     *Linux常用命令大全
     https://www.cnblogs.com/cxxjohnson/p/4973161.html
     

## 6.多线程

     https://www.cnblogs.com/wxd0108/p/5479442.html
     目的，那就是更好的利用cpu的资源
     
     并行：多个cpu实例或者多台机器同时执行一段处理逻辑，是真正的同时。
     并发：通过cpu调度算法，让用户看上去同时执行，实际上从cpu操作层面不是真正的同时。并发往往在场景中有公用的资源，
     那么针对这个公用的资源往往产生瓶颈，我们会用TPS或者QPS来反应这个系统的处理能力。
     
     *线程的状态
     
        new -> runable -> running -> Blocked -> Dead
        
        //当前线程可转让cpu控制权，让别的就绪状态线程运行（切换）
        public static Thread.yield() 
        //暂停一段时间
        public static Thread.sleep()  
        //在一个线程中调用other.join(),将等待other执行完后才继续本线程。　　　　
        public join()
        //后两个函数皆可以被打断
        public interrupte()
        
        Synchronized块中
            wait()
            notify()
            
     *volatile
     多线程的内存模型：main memory（主存）、working memory（线程栈），在处理数据时，线程会把值从主存load到本地栈，
     完成操作后再save回去(volatile关键词的作用：每次针对该变量的操作都激发一次load and save)。
     
     针对多线程使用的变量如果不是volatile或者final修饰的，很有可能产生不可预知的结果（另一个线程修改了这个值，
     但是之后在某线程看到的是修改之前的值）。其实道理上讲同一实例的同一属性本身只有一个副本。但是多线程是会缓存值的，
     本质上，volatile就是不去缓存，直接取值。在线程安全的情况下加volatile会牺牲性能。
     
     
     
     *如何获取线程中的异常
     setUncaughtExceptionHandler
     
     
     *线程组：线程组存在的意义，首要原因是安全。java默认创建的线程都是属于系统线程组，
     而同一个线程组的线程是可以相互修改对方的数据的。但如果在不同的线程组中，那么就不能“跨线程组”修改数据，
     可以从一定程度上保证数据安全。



     *Runnable
     *Callable
      future模式：并发模式的一种，可以有两种形式，即无阻塞和阻塞，分别是isDone和get。其中Future对象用来存放该线程的返回值以及状态
      
      ExecutorService e = Executors.newFixedThreadPool(3);
       //submit方法有多重参数版本，及支持callable也能够支持runnable接口类型.
      Future future = e.submit(new myCallable());
      future.isDone() //return true,false 无阻塞
      future.get() // return 返回值，阻塞直到该线程运行结束
           
         
           
     *ThreadLocal类
     用处：保存线程的独立变量。对一个线程类（继承自Thread)
     当使用ThreadLocal维护变量时，ThreadLocal为每个使用该变量的线程提供独立的变量副本，所以每一个线程都可以独立地改变自己的副本，
     而不会影响其它线程所对应的副本。常用于用户登录控制，如记录session信息。
     
     
     
     *原子类（AtomicInteger、AtomicBoolean……）
     如果使用atomic wrapper class如atomicInteger，或者使用自己保证原子的操作，则等同于synchronized
     
     //返回值为boolean
     AtomicInteger.compareAndSet(int expect,int update)
     该方法可用于实现乐观锁
     
     
     
     *Lock类　
      lock: 在java.util.concurrent包内。共有三个实现：
      
      ReentrantLock
      ReentrantReadWriteLock.ReadLock
      ReentrantReadWriteLock.WriteLock
      
      主要目的是和synchronized一样， 两者都是为了解决同步问题，处理资源争端而产生的技术。功能类似但有一些区别。
      
      区别如下：
      
      复制代码
      lock更灵活，可以自由定义多把锁的枷锁解锁顺序（synchronized要按照先加的后解顺序）
      提供多种加锁方案，lock 阻塞式, trylock 无阻塞式, lockInterruptily 可打断式， 还有trylock的带超时时间版本。
     
     
     ReentrantReadWriteLock
     
     可重入读写锁（读写锁的一个实现）　
     
     　ReentrantReadWriteLock lock = new ReentrantReadWriteLock()
     　　ReadLock r = lock.readLock();
     　　WriteLock w = lock.writeLock();
     两者都有lock,unlock方法。写写，写读互斥；读读不互斥。可以实现并发读的高效线程安全代码
     
     
     *容器类
     这里就讨论比较常用的两个：
     
     BlockingQueue
     ConcurrentHashMap
     
     
     
     *管理类
     管理类的概念比较泛，用于管理线程，本身不是多线程的，但提供了一些机制来利用上述的工具做一些封装。
     了解到的值得一提的管理类：ThreadPoolExecutor和 JMX框架下的系统级管理类 ThreadMXBean
     ThreadPoolExecutor
     如果不了解这个类，应该了解前面提到的ExecutorService，开一个自己的线程池非常方便：
     
     复制代码
     ExecutorService e = Executors.newCachedThreadPool();
         ExecutorService e = Executors.newSingleThreadExecutor();
         ExecutorService e = Executors.newFixedThreadPool(3);
         // 第一种是可变大小线程池，按照任务数来分配线程，
         // 第二种是单线程池，相当于FixedThreadPool(1)
         // 第三种是固定大小线程池。
         // 然后运行
         e.execute(new MyRunnableImpl());
         
     
     
     
     *读写锁
     https://blog.csdn.net/zwjyyy1203/article/details/80231303
     
     
     *CountDownLatch
     
     　　CountDownLatch可以理解为一个计数器在初始化时设置初始值，当一个线程需要等待某些操作先完成时，需要调用await()方法。
        这个方法让线程进入休眠状态直到等待的所有线程都执行完成。每调用一次countDown()方法，内部计数器减1，直到计数器为0时唤醒。
        这个可以理解为特殊的CyclicBarrier。
     
        使用场景
        
        有时候会有这样的需求，多个线程同时工作，然后其中几个可以随意并发执行，但有一个线程需要等其他线程工作结束后，
        才能开始。举个例子，开启多个线程分块下载一个大文件，每个线程只下载固定的一截，最后由另外一个线程来拼接所有的分段，
        那么这时候我们可以考虑使用CountDownLatch来控制并发。
        
     
    
## 7.线程池


    由浅入深理解Java线程池及线程池的如何使用
    https://www.cnblogs.com/superfj/p/7544971.html
    
    线程池参数及拒绝策略 
    https://blog.csdn.net/wang_rrui/article/details/78541786
    
    
## 8.面试题

    Java高级工程师面试题总结及参考答案
    https://www.cnblogs.com/cxxjohnson/p/10118067.html
    
    限流，分布式锁，UUID
    我现在要做一个限流功能, 怎么做?
    令牌桶
    这个限流要做成分布式的, 怎么做?
    令牌桶维护到 Redis 里，每个实例起一个线程抢锁，抢到锁的负责定时放令牌
    怎么抢锁?
    Redis setnx
    锁怎么释放?
    抢到锁后设置过期时间，线程本身退出时主动释放锁，假如线程卡住了，锁过期那么其它线程可以继续抢占
    加了超时之后有没有可能在没有释放的情况下, 被人抢走锁
    有可能，单次处理时间过长，锁泄露
    怎么解决?
    换 zk，用心跳解决
    不用 zk 的心跳, 可以怎么解决这个问题呢?
    每次更新过期时间时，Redis 用 MULTI 做 check-and-set 检查更新时间是否被其他线程修改了，假如被修改了，说明锁已经被抢走，放弃这把锁
    假如这个限流希望做成可配置的, 需要有一个后台管理系统随意对某个 api 配置全局流量, 怎么做？
    在 Redis 里存储每个 API 的令牌桶 key，假如存在这个 key，则需要按上述逻辑进行限流
    某一个业务中现在需要生成全局唯一的递增 ID, 并发量非常大, 怎么做
    snowflake (这个其实答得不好，snowflake 无法实现全局递增，只能实现全局唯一，单机递增，面试结束后就想到了类似 TDDL 那样一次取一个 ID 段，放在本地慢慢分配的策略）
    算法题, M*N 横向递增矩阵找指定数
    只想到 O(M+N)的解法
    
    Java 中 HashMap 的存储, 冲突, 扩容, 并发访问分别是怎么解决的
    Hash 表，拉链法（长度大于8变形为红黑树）,扩容*2 rehash，并发访问不安全
    
    拉链法中链表过长时变形为红黑树有什么优缺点?
    优点：O(LogN) 的读取速度更快；缺点：插入时有 Overhead，O(LogN) 插入，旋转维护平衡
    HashMap 的并发不安全体现在哪?
    拉链法解决冲突，插入链表时不安全，并发操作可能导致另一个插入失效
    https://www.cnblogs.com/qiumingcheng/p/5259892.html
    
    HashMap 在扩容时, 对读写操作有什么特殊处理?
    
    知道 CAS 吗? Java 中 CAS 是怎么实现的?
    Compare and Swap，一种乐观锁的实现，可以称为"无锁"(lock-free)，CAS 由于要保证原子性无法由 JVM 本身实现，
    需要调用对应 OS 的指令(这块其实我不了解细节)
    
    
## 9.设计模式
    
    策略模式
    https://www.cnblogs.com/lewis0077/p/5133812.html
    
## 10.网络等基础

    TCP和UDP的区别和优缺点
    https://blog.csdn.net/xiaobangkuaipao/article/details/76793702
    
    TCP三次握手与四次挥手过程
    https://blog.csdn.net/qq_35216516/article/details/80554575
    
    TCP 有哪些状态
    https://www.cnblogs.com/qingergege/p/6603488.html
    
    HTTP与HTTPS的区别
    https://www.cnblogs.com/wqhwe/p/5407468.html

## 11.spring

    


    
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     