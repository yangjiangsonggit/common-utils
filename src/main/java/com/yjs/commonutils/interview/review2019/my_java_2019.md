

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
     
     反射
         反射的定义如下：java程序在运行状态中，对于任意一个类，都能够知道这个类的所有属性和方法；对于任意一个对象，
         都能够调用它的任意方法和属性；这种动态获取信息以及动态调用对象方法的功能称为java语言的反射机制。
     
     反射原理 
         总结起来说就是，反射是通过Class类和java.lang.reflect类库一起支持而实现的，其中每一个Class类的对象都对应了一个类，
         这些信息在编译时期就已经被存在了.class文件里面了，Class 对象是在加载类时由 Java 虚拟机以及通过调用类加载器中
         的defineClass方法自动构造的。也就是这不需要我们自己去处理创建，JVM已经帮我们创建好了。对于我们定义的每一个类，
         在虚拟机中都有一个应的Class对象。
         
         总结: java虚拟机帮我们生成了类的class对象,而通过类的全限定名，我们可以去获取这个类的字节码.class文件
         ，然后再获取这个类对应的class对象，再通过class对象提供的方法结合类Method,Filed,Constructor，
         就能获取到这个类的所有相关信息.
         
         

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

     https://github.com/Snailclimb/JavaGuide/blob/master/Java%E7%9B%B8%E5%85%B3/Multithread/AQS.md
     
     对于CountDownLatch来说，重点是“一个线程（多个线程）等待”，而其他的N个线程在完成“某件事情”之后，可以终止，
     也可以等待。而对于CyclicBarrier，重点是多个线程，在任意一个线程没有完成，所有的线程都必须等待。
     CountDownLatch是计数器，线程完成一个记录一个，只不过计数不是递增而是递减，而CyclicBarrier更像是一个阀门，需要所有线程都到达，
     阀门才能打开，然后继续执行。
     
     AQS的全称为（AbstractQueuedSynchronizer）
     AQS核心思想是，如果被请求的共享资源空闲，则将当前请求资源的线程设置为有效的工作线程，并且将共享资源设置为锁定状态。
     如果被请求的共享资源被占用，那么就需要一套线程阻塞等待以及被唤醒时锁分配的机制，这个机制AQS是用CLH队列锁实现的，
     即将暂时获取不到锁的线程加入到队列中。
     CLH(Craig,Landin,and Hagersten)队列是一个虚拟的双向队列（虚拟的双向队列即不存在队列实例，仅存在结点之间的关联关系）。
     AQS是将每条请求共享资源的线程封装成一个CLH锁队列的一个结点（Node）来实现锁的分配。
     



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
     ABA问题
     
     
     
     *Lock类　
      lock: 在java.util.concurrent包内。共有三个实现：
      
      ReentrantLock
      ReentrantReadWriteLock.ReadLock
      ReentrantReadWriteLock.WriteLock
      jdk 1.8 StampedLock(解决读写锁,写锁饥饿问题)
      https://segmentfault.com/a/1190000015808032?utm_source=tag-newest
      
      多condition,sign(),await()
      https://www.cnblogs.com/Wanted-Tao/p/6378942.html
      
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
        
        
        
     并发容器
     https://github.com/Snailclimb/JavaGuide/blob/master/Java%E7%9B%B8%E5%85%B3/Multithread/%E5%B9%B6%E5%8F%91%E5%AE%B9%E5%99%A8%E6%80%BB%E7%BB%93.md
         ConcurrentHashMap： 线程安全的HashMap
         CopyOnWriteArrayList: 线程安全的List，在读多写少的场合性能非常好，远远好于Vector.
         **ConcurrentLinkedQueue：**高效的并发队列，使用链表实现。可以看做一个线程安全的 LinkedList，这是一个非阻塞队列。
         BlockingQueue: 这是一个接口，JDK内部通过链表、数组等方式实现了这个接口。表示阻塞队列，非常适合用于作为数据共享的通道。
         ConcurrentSkipListMap: 跳表的实现。这是一个Map，使用跳表的数据结构进行快速查找。
    
     如果让你设计一个计数器，你怎么实现？
     http://www.cnblogs.com/williamjie/p/9556079.html
        高性能计数器总结
        AtomicLong ：并发场景下读性能优秀，写性能急剧下降，不适合作为高性能的计数器方案。内存需求量少。
        
        LongAdder ：并发场景下写性能优秀，读性能由于组合求值的原因，不如直接读值的方案，但由于计数器场景写多读少的缘故，整体性能在几个方案中最优，是高性能计数器的首选方案。由于 Cells 数组以及缓存行填充的缘故，占用内存较大。
        
        ConcurrentAutoTable ：拥有和 LongAdder 相近的写入性能，读性能则更加不如 LongAdder。它的使用需要引入 JCTools 依赖，相比 Jdk 自带的 LongAdder 并没有优势。但额外说明一点，
        ConcurrentAutoTable 的使用并非局限于计数器场景，其仍然存在很大的价值。
    
        LongAdder newCounter = new LongAdder();
        newCounter.increment();
        System.out.println(newCounter.sum());
    
     CountDownLatch实现原理
     https://cloud.tencent.com/developer/article/1038486
    
    
     *总结
        你讲讲线程池的实现原理?比如现在设置coreSize=5，maxSize=10，blockQueueSize=10，依次提交6个比较耗时的任务，线程池是如何执行的？
        如果让你设计一个计数器，你怎么实现？
        多线程同步和互斥有哪几种实现方法？
        悲观锁和乐观锁有什么区别？
        Java的锁实现机制，使用场景分析
        ReentranLock源码，设计原理，整体过程
        volatile的实现原理
        AQS的实现过程
        CountDownLatch实现原理
    
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
    
    nginx
    https://github.com/dunwu/nginx-tutorial


## 11.java IO NIO

    https://mp.weixin.qq.com/s?__biz=MzU4NDQ4MzU5OA==&mid=2247483956&idx=1&sn=57692bc5b7c2c6dfb812489baadc29c9&chksm=fd985455caefdd4331d828d8e89b22f19b304aa87d6da73c5d8c66fcef16e4c0b448b1a6f791&scene=21#wechat_redirect
        
    
    

## 12.spring

    *bean生命周期
        Spring容器初始化
        =====================================
        调用GiraffeService无参构造函数
        GiraffeService中利用set方法设置属性值
        调用setBeanName:: Bean Name defined in context=giraffeService
        调用setBeanClassLoader,ClassLoader Name = sun.misc.Launcher$AppClassLoader
        调用setBeanFactory,setBeanFactory:: giraffe bean singleton=true
        调用setEnvironment
        调用setResourceLoader:: Resource File Name=spring-beans.xml
        调用setApplicationEventPublisher
        调用setApplicationContext:: Bean Definition Names=[giraffeService, org.springframework.context.annotation.CommonAnnotationBeanPostProcessor#0, com.giraffe.spring.service.GiraffeServicePostProcessor#0]
        执行BeanPostProcessor的postProcessBeforeInitialization方法,beanName=giraffeService
        调用PostConstruct注解标注的方法
        执行InitializingBean接口的afterPropertiesSet方法
        执行配置的init-method
        执行BeanPostProcessor的postProcessAfterInitialization方法,beanName=giraffeService
        Spring容器初始化完毕
        =====================================
        从容器中获取Bean
        giraffe Name=李光洙
        =====================================
        调用preDestroy注解标注的方法
        执行DisposableBean接口的destroy方法
        执行配置的destroy-method
        Spring容器关闭
            
            
            
    对于作用域为 prototype 的 bean ，其destroy方法并没有被调用。如果 bean 的 scope 设为prototype时，当容器关闭时，
    destroy 方法不会被调用。对于 prototype 作用域的 bean，有一点非常重要，
    那就是 Spring不能对一个 prototype bean 的整个生命周期负责：容器在初始化、配置、装饰或者是装配完一个prototype实例后，
    将它交给客户端，随后就对该prototype实例不闻不问了

    spring 官方文档中文版
    https://blog.csdn.net/tangtong1/article/details/51326887

    依赖注入（DI）和控制反转（IoC）
    你可以把不相干组件组合在一起，从而组成一个完整的可以使用的应用。Spring根据设计模式编码出了非常优秀的代码，
    所以可以直接集成到自己的应用中。因此，大量的组织机构都使用Spring来保证应用程序的健壮性和可维护性。

    模块
    
        核心容器（Core Container）
        
        核心容器包括spring-core，spring-beans，spring-context，spring-context-support和spring-expression（SpEL，Spring表达式语言，Spring Expression Language）等模块。
        spring-core和spring-beans模块是Spring框架的基础，包括控制反转和依赖注入等功能。BeanFactory是工厂模式的微妙实现，它移除了编码式单例的需要，并且可以把配置和依赖从实际编码逻辑中解耦。
        Context（spring-context）模块是在Core和Bean模块的基础上建立起来的，它以一种类似于JNDI注册的方式访问对象。Context模块继承自Bean模块，并且添加了国际化（比如，使用资源束）、事件传播、资源加载和透明地创建上下文（比如，通过Servelet容器）等功能。Context模块也支持Java EE的功能，比如EJB、JMX和远程调用等。ApplicationContext接口是Context模块的焦点。spring-context-support提供了对第三方库集成到Spring上下文的支持，比如缓存（EhCache, Guava, JCache）、邮件（JavaMail）、调度（CommonJ, Quartz）、模板引擎（FreeMarker, JasperReports, Velocity）等。
        spring-expression模块提供了强大的表达式语言用于在运行时查询和操作对象图。它是JSP2.1规范中定义的统一表达式语言的扩展，支持set和get属性值、属性赋值、方法调用、访问数组集合及索引的内容、逻辑算术运算、命名变量、通过名字从Spring IoC容器检索对象，还支持列表的投影、选择以及聚合等。
        

        AOP和检测（Instrumentation）

        spring-aop模块提供了面向切面编程（AOP）的实现，可以定义诸如方法拦截器和切入点等，从而使实现功能的代码彻底的解耦出来。使用源码级的元数据，可以用类似于.Net属性的方式合并行为信息到代码中。
        spring-aspects模块提供了对AspectJ的集成。
        spring-instrument模块提供了对检测类的支持和用于特定的应用服务器的类加载器的实现。spring-instrument-tomcat模块包含了用于tomcat的Spring检测代理。
     
     
        消息处理（messaging）
        
        Spring 4 包含的spring-messaging模块是从Spring集成项目的关键抽象中提取出来的，这些项目包括Message、MessageChannel、MessageHandler和其它服务于消息处理的项目。这个模块也包含一系列的注解用于映射消息到方法，这类似于Spring MVC基于编码模型的注解。
        
        
        数据访问与集成
        数据访问与集成层包含JDBC、ORM、OXM、JMS和事务模块。 
        （译者注：JDBC=Java Data Base Connectivity，ORM=Object Relational Mapping，OXM=Object XML Mapping，JMS=Java Message Service）
        spring-jdbc模块提供了JDBC抽象层，它消除了冗长的JDBC编码和对数据库供应商特定错误代码的解析。
        spring-tx模块支持编程式事务和声明式事务，可用于实现了特定接口的类和所有的POJO对象。 
        （译者注：编程式事务需要自己写beginTransaction()、commit()、rollback()等事务管理方法，声明式事务是通过注解或配置由spring自动处理，编程式事务粒度更细）
        spring-orm模块提供了对流行的对象关系映射API的集成，包括JPA、JDO和Hibernate等。通过此模块可以让这些ORM框架和spring的其它功能整合，比如前面提及的事务管理。
        spring-oxm模块提供了对OXM实现的支持，比如JAXB、Castor、XML Beans、JiBX、XStream等。
        spring-jms模块包含生产（produce）和消费（consume）消息的功能。从Spring 4.1开始，集成了spring-messaging模块。
        
        
        Web
        Web层包括spring-web、spring-webmvc、spring-websocket、spring-webmvc-portlet等模块。
        spring-web模块提供面向web的基本功能和面向web的应用上下文，比如多部分（multipart）文件上传功能、使用Servlet监听器初始化IoC容器等。它还包括HTTP客户端以及Spring远程调用中与web相关的部分。
        spring-webmvc模块（即Web-Servlet模块）为web应用提供了模型视图控制（MVC）和REST Web服务的实现。Spring的MVC框架可以使领域模型代码和web表单完全地分离，且可以与Spring框架的其它所有功能进行集成。
        spring-webmvc-portlet模块（即Web-Portlet模块）提供了用于Portlet环境的MVC实现，并反映了spring-webmvc模块的功能。
        
        
        Test
        spring-test模块通过JUnit和TestNG组件支持单元测试和集成测试。它提供了一致性地加载和缓存Spring上下文，也提供了用于单独测试代码的模拟对象（mock object）。
        
    
    
    http://wiki.jikexueyuan.com/project/spring/bean-life-cycle.html
    
    Bean 的作用域
        singleton	该作用域将 bean 的定义的限制在每一个 Spring IoC 容器中的一个单一实例(默认)。
        prototype	该作用域将单一 bean 的定义限制在任意数量的对象实例。
        request	该作用域将 bean 的定义限制为 HTTP 请求。只在 web-aware Spring ApplicationContext 的上下文中有效。
        session	该作用域将 bean 的定义限制为 HTTP 会话。 只在web-aware Spring ApplicationContext的上下文中有效。
        global-session	该作用域将 bean 的定义限制为全局 HTTP 会话。只在 web-aware Spring ApplicationContext 的上下文中有效。
        
    Bean 的生命周期
    https://github.com/crossoverJie/JCSprout/blob/master/MD/spring/spring-bean-lifecycle.md
    
        init-method
        destroy-method
        //默认
        default-init-method
        default-destroy-method
        
        
    Spring——Bean 后置处理器
        在初始化 bean 的之前和之后实现更复杂的逻辑，因为你有两个访问内置 bean 对象的后置处理程序的方法。
        public class InitHelloWorld implements BeanPostProcessor {
           public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
              System.out.println("BeforeInitialization : " + beanName);
              return bean;  // you can return any other object as well
           }
           public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
              System.out.println("AfterInitialization : " + beanName);
              return bean;  // you can return any other object as well
           }
        }
        
     Bean 定义模板
     你可以创建一个 Bean 定义模板，不需要花太多功夫它就可以被其他子 bean 定义使用。在定义一个 Bean 定义模板时，你不应该指定类的属性，而应该指定带 true 值的抽象属性，如下所示：
     
    <bean id="beanTeamplate" abstract="true">
    <bean id="helloIndia" class="com.tutorialspoint.HelloIndia" parent="beanTeamplate">
    
        

     Spring 中的事件处理
     
         ContextRefreshedEvent
         ApplicationContext 被初始化或刷新时，该事件被发布。这也可以在 ConfigurableApplicationContext 接口中使用 refresh() 方法来发生。
         
         ContextStartedEvent
         当使用 ConfigurableApplicationContext 接口中的 start() 方法启动 ApplicationContext 时，该事件被发布。你可以调查你的数据库，或者你可以在接受到这个事件后重启任何停止的应用程序。
         
         ContextStoppedEvent
         当使用 ConfigurableApplicationContext 接口中的 stop() 方法停止 ApplicationContext 时，发布这个事件。你可以在接受到这个事件后做必要的清理的工作。
         
         ContextClosedEvent
         当使用 ConfigurableApplicationContext 接口中的 close() 方法关闭 ApplicationContext 时，该事件被发布。一个已关闭的上下文到达生命周期末端；它不能被刷新或重启。
         
         RequestHandledEvent
         这是一个 web-specific 事件，告诉所有 bean HTTP 请求已经被服务。
     
     
     *Spring 框架的 AOP
        
        @AspectJ
        <aop:aspectj-autoproxy/>
        
        通知的类型
        Spring 方面可以使用下面提到的五种通知工作：
        
        通知	        描述
        前置通知	在一个方法执行之前，执行通知。
        后置通知	在一个方法执行之后，不考虑其结果，执行通知。
        返回后通知	在一个方法执行之后，只有在方法成功完成时，才能执行通知。
        抛出异常后通知	在一个方法执行之后，只有在方法退出抛出异常时，才能执行通知。
        环绕通知	在建议方法调用之前和之后，执行通知。
     
     JdbcTemplate
        
     编程式事务管理
        TransactionDefinition def = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(def);
        transactionManager.commit(status);
        transactionManager.rollback(status);
     
     
     Spring web MVC 
        框架提供了模型-视图-控制的体系结构和可以用来开发灵活、松散耦合的 web 应用程序的组件。
        
        DispatcherServlet
            HandlerMapping、Controller 和 ViewResolver 是 WebApplicationContext 的一部分，
            而 WebApplicationContext 是带有一些对 web 应用程序必要的额外特性的 ApplicationContext 的扩展。
            
            
     *SpringMVC 流程
        流程说明（重要）：
        
        （1）客户端（浏览器）发送请求，直接请求到 DispatcherServlet。
        
        （2）DispatcherServlet 根据请求信息调用 HandlerMapping，解析请求对应的 Handler。
        
        （3）解析到对应的 Handler（也就是我们平常说的 Controller 控制器）后，开始由 HandlerAdapter 适配器处理。
        
        （4）HandlerAdapter 会根据 Handler 来调用真正的处理器开处理请求，并处理相应的业务逻辑。
        
        （5）处理器处理完业务后，会返回一个 ModelAndView 对象，Model 是返回的数据对象，View 是个逻辑上的 View。
        
        （6）ViewResolver 会根据逻辑 View 查找实际的 View。
        
        （7）DispaterServlet 把返回的 Model 传给 View（视图渲染）。
        
        （8）把 View 返回给请求者（浏览器）
        
        
     SpringMVC 重要组件说明
     1、前端控制器DispatcherServlet（不需要工程师开发）,由框架提供（重要）
     
     作用：Spring MVC 的入口函数。接收请求，响应结果，相当于转发器，中央处理器。有了 DispatcherServlet 减少了其它组件之间的耦合度。
     用户请求到达前端控制器，它就相当于mvc模式中的c，DispatcherServlet是整个流程控制的中心，由它调用其它组件处理用户的请求，
     DispatcherServlet的存在降低了组件之间的耦合性。
     
     2、处理器映射器HandlerMapping(不需要工程师开发),由框架提供
     
     作用：根据请求的url查找Handler。HandlerMapping负责根据用户请求找到Handler即处理器（Controller），
     SpringMVC提供了不同的映射器实现不同的映射方式，例如：配置文件方式，实现接口方式，注解方式等。
     
     3、处理器适配器HandlerAdapter
     
     作用：按照特定规则（HandlerAdapter要求的规则）去执行Handler 通过HandlerAdapter对处理器进行执行，这是适配器模式的应用，
     通过扩展适配器可以对更多类型的处理器进行执行。
     
     4、处理器Handler(需要工程师开发)
     
     注意：编写Handler时按照HandlerAdapter的要求去做，这样适配器才可以去正确执行Handler Handler 是继DispatcherServlet前端控制器的
     后端控制器，在DispatcherServlet的控制下Handler对具体的用户请求进行处理。 由于Handler涉及到具体的用户业务请求，
     所以一般情况需要工程师根据业务需求开发Handler。
     
     5、视图解析器View resolver(不需要工程师开发),由框架提供
     
     作用：进行视图解析，根据逻辑视图名解析成真正的视图（view） View Resolver负责将处理结果生成View视图，View Resolver首先
     根据逻辑视图名解析成物理视图名即具体的页面地址，再生成View视图对象，最后对View进行渲染将处理结果通过页面展示给用户。 
     springmvc框架提供了很多的View视图类型，包括：jstlView、freemarkerView、pdfView等。 一般情况下需要通过页面标签或
     页面模版技术将模型数据通过页面展示给用户，需要由工程师根据业务需求开发具体的页面。
     
     6、视图View(需要工程师开发)
     
     View是一个接口，实现类支持不同的View类型（jsp、freemarker、pdf...）
     
     注意：处理器Handler（也就是我们平常说的Controller控制器）以及视图层view都是需要我们自己手动开发的。其他的一些组件比如：前端控制器
     DispatcherServlet、处理器映射器HandlerMapping、处理器适配器HandlerAdapter等等都是框架提供给我们的，不需要自己手动开发。
     
     
     问题:AOP实现原理、动态代理和静态代理、Spring IOC的初始化过程、IOC原理、自己实现怎么实现一个IOC容器？
     
     *Spirng IoC容器的初始化过程
     https://www.cnblogs.com/chenjunjie12321/p/6124649.html
     
        分为三步：
        
        1.Resource定位（Bean的定义文件定位）
        2.将Resource定位好的资源载入到BeanDefinition
        3.将BeanDefiniton注册到容器中
        
     *Spring aop 原理(代理模式)
        https://www.cnblogs.com/lcngu/p/5339555.html
        
     *总结
        1.bean的生命周期?
        2.ioc容器初始化流程?
        3.spring模块划分?
        4.spring事件类型?
        5.aop通知类型,原理?
        6.SpringMVC流程?

##14 kafka

    Kafka 生产者
    https://github.com/crossoverJie/JCSprout/blob/master/MD/kafka/kafka-product.md
    
            基本发送
            其实 send() 方法默认则是异步的，只要不手动调用 get() 方法。
            Future<RecordMetadata> send(ProducerRecord<K, V> producer, Callback callback);
            Callback 是一个回调接口，在消息发送完成之后可以回调我们自定义的实现。
            
        *发送流程
            初始化以及真正发送消息的 kafka-producer-network-thread IO 线程。
            将消息序列化。
            得到需要发送的分区。(路由)
            写入内部的一个缓存区中。
            初始化的 IO 线程不断的消费这个缓存来发送消息。
            
            路由分区
                指定分区
                可以在构建 ProducerRecord 为每条消息指定分区。
                
                自定义路由策略
                如果没有指定分区，则会调用 partitioner.partition 接口执行自定义分区策略。
                
                默认策略
                最后一种则是默认的路由策略，如果我们啥都没做就会执行该策略。
                该策略也会使得消息分配的比较均匀。
                
                    默认策略步骤
                    获取 Topic 分区数。
                    将内部维护的一个线程安全计数器 +1。
                    与分区数取模得到分区编号。
                    
            写入内部缓存
                在 send() 方法拿到分区后会调用一个 append() 函数,
                该函数中会调用一个 getOrCreateDeque() 写入到一个内部缓存中 batches。
                
            消费缓存
                在最开始初始化的 IO 线程其实是一个守护线程，它会一直消费这些数据。
                通过图中的几个函数会获取到之前写入的数据。这块内容可以不必深究，但其中有个 completeBatch 方法却非常关键。
                调用该方法时候肯定已经是消息发送完毕了，所以会调用 batch.done() 来完成之前我们在 send() 方法中定义的回调接口。
                  
                  
                            
        *Producer 参数解析
        
            acks
                acks 是一个影响消息吞吐量的一个关键参数。
                主要有 [all、-1, 0, 1] 这几个选项，默认为 1。
                由于 Kafka 不是采取的主备模式，而是采用类似于 Zookeeper 的主备模式。
                前提是 Topic 配置副本数量 replica > 1。
                
                当 acks = all/-1 时：
                意味着会确保所有的 follower 副本都完成数据的写入才会返回。
                这样可以保证消息不会丢失！
                但同时性能和吞吐量却是最低的。
                
                当 acks = 0 时：
                producer 不会等待副本的任何响应，这样最容易丢失消息但同时性能却是最好的！
        
                当 acks = 1 时：
                这是一种折中的方案，它会等待副本 Leader 响应，但不会等到 follower 的响应。
                一旦 Leader 挂掉消息就会丢失。但性能和消息安全性都得到了一定的保证。
    
            batch.size
                这个参数看名称就知道是内部缓存区的大小限制，对他适当的调大可以提高吞吐量。
                但也不能极端，调太大会浪费内存。小了也发挥不了作用，也是一个典型的时间和空间的权衡。
            
            retries(可能会导致消息重复和消息顺序不一致)
                retries 该参数主要是来做重试使用，当发生一些网络抖动都会造成重试。
                这个参数也就是限制重试次数。
                但也有一些其他问题。
                    1.因为是重发所以消息顺序可能不会一致，这也是上文提到就算是一个分区消息也不会是完全顺序的情况。
                    2.还是由于网络问题，本来消息已经成功写入了但是没有成功响应给 producer，进行重试时就可能会出现消息重复。
                    这种只能是消费者进行幂等处理。
            
        *高效的发送方式
            
            如果消息量真的非常大，同时又需要尽快的将消息发送到 Kafka。一个 producer 始终会收到缓存大小等影响。
            那是否可以创建多个 producer 来进行发送呢？
            配置一个最大 producer 个数。
            发送消息时首先获取一个 producer，获取的同时判断是否达到最大上限，没有就新建一个同时保存到内部的 List 中，保存时做好同步处理防止并发问题。
            获取发送者时可以按照默认的分区策略使用轮询的方式获取（保证使用均匀）。
            这样在大量、频繁的消息发送场景中可以提高发送效率减轻单个 producer 的压力。
            
        关闭 Producer
            最后则是 Producer 的关闭，Producer 在使用过程中消耗了不少资源（线程、内存、网络等）因此需要显式的关闭从而回收这些资源
            默认的 close() 方法和带有超时时间的方法都是在一定的时间后强制关闭。
            但在过期之前都会处理完剩余的任务。
            所以使用哪一个得视情况而定。
            
     
    kafka消费者
        https://www.cnblogs.com/huxi2b/p/6223228.html
     
        单线程消费
            由于数据散列在三个不同分区，所以单个线程需要遍历三个分区将数据拉取下来。
            取出的 100 条数据确实是分别遍历了三个分区。
            单线程消费虽然简单，但存在以下几个问题：
                效率低下。如果分区数几十上百个，单线程无法高效的取出数据。
                可用性很低。一旦消费线程阻塞，甚至是进程挂掉，那么整个消费程序都将出现问题。

        多线程消费
            在多线程之前不得不将消费模式分为两种进行探讨：消费组、独立消费者。
            
            独立消费者模式
                值得注意的是：独立消费者可以不设置 group.id 属性。
                通过 API 可以看出：我们可以手动指定需要消费哪些分区
                
                但这种方式有一个问题：可用性不高，当其中一个进程挂掉之后；该进程负责的分区数据没法转移给其他进程处理。
                
            消费组模式
            
                我们可以创建 N 个消费者实例（new KafkaConsumer()）,当这些实例都用同一个 group.id 来创建时，
                他们就属于同一个消费组。
                在同一个消费组中的消费实例可以收到消息，但一个分区的消息只会发往一个消费实例。
       
                消费组自平衡
                    消费组的优势
                    
                    我们可以在一个消费组中创建多个消费实例来达到高可用、高容错的特性，
                    不会出现单线程以及独立消费者挂掉之后数据不能消费的情况。同时基于多线程的方式也极大的提高了消费效率。
        
                    触发rebalance的情况
                    消费组中新增消费实例。
                    消费组中消费实例 down 掉。
                    订阅的 Topic 分区数发生变化。
                    如果是正则订阅 Topic 时，匹配的 Topic 数发生变化也会导致 Rebalance。
                    所以推荐使用这样的方式消费数据，同时扩展性也非常好。当性能不足新增分区时只需要启动新的消费实例加入到消费组中即可。
                    
                    
    为什么需要消息系统?
            削峰填谷
            解耦
            拓展性
            异步处理
            顺序性
            可恢复性(部分失效,仍然可用)
            
    相关概念
        1.producer：
        　　消息生产者，发布消息到 kafka 集群的终端或服务。
        2.broker：
        　　kafka 集群中包含的服务器。
        3.topic：
        　　每条发布到 kafka 集群的消息属于的类别，即 kafka 是面向 topic 的。
        4.partition：
        　　partition 是物理上的概念，每个 topic 包含一个或多个 partition。kafka 分配的单位是 partition。
        5.consumer：
        　　从 kafka 集群中消费消息的终端或服务。
        6.Consumer group：
        　　high-level consumer API 中，每个 consumer 都属于一个 consumer group，每条消息只能被 consumer group 中的一个 Consumer 消费，但可以被多个 consumer group 消费。
        7.replica：
        　　partition 的副本，保障 partition 的高可用。
        8.leader：
        　　replica 中的一个角色， producer 和 consumer 只跟 leader 交互。
        9.follower：
        　　replica 中的一个角色，从 leader 中复制数据。
        10.controller：
        　　kafka 集群中的其中一个服务器，用来进行 leader election 以及 各种 failover。
        12.zookeeper：
        　　kafka 通过 zookeeper 来存储集群的 meta 信息。

    
    位移管理(offset management)
        自动VS手动
        
            Kafka默认是定期帮你自动提交位移的(enable.auto.commit = true)，你当然可以选择手动提交位移实现自己控制。
            另外kafka会定期把group消费情况保存起来，做成一个offset map
            
        位移提交
        
            老版本的位移是提交到zookeeper中的，图就不画了，总之目录结构是：/consumers/<group.id>/offsets/<topic>/<partitionId>，
            但是zookeeper其实并不适合进行大批量的读写操作，尤其是写操作。因此kafka提供了另一种解决方案：增加__consumeroffsets topic，
            将offset信息写入这个topic，摆脱对zookeeper的依赖(指保存offset这件事情)。__consumer_offsets中的消息保存了每个
            consumer group某一时刻提交的offset信息。依然以上图中的consumer group为例，格式大概如下：
        

    
    Rebalance
    https://www.cnblogs.com/huxi2b/p/6223228.html
        
        什么是rebalance？
        
            rebalance本质上是一种协议，规定了一个consumer group下的所有consumer如何达成一致来分配订阅topic的每个分区。
            比如某个group下有20个consumer，它订阅了一个具有100个分区的topic。正常情况下，Kafka平均会为每个consumer分配5个分区。
            这个分配的过程就叫rebalance。
        
        什么时候rebalance？
        
            这也是经常被提及的一个问题。rebalance的触发条件有三种：
            
            组成员发生变更(新consumer加入组、已有consumer主动离开组或已有consumer崩溃了——这两者的区别后面会谈到)
            订阅主题数发生变更——这当然是可能的，如果你使用了正则表达式的方式进行订阅，那么新建匹配正则表达式的topic就会触发rebalance
            订阅主题的分区数发生变更
    
        如何进行组内分区分配?
            Kafka新版本consumer默认提供了两种分配策略：range和round-robin。当然Kafka采用了可插拔式的分配策略，
            你可以创建自己的分配器以实现不同的分配策略。实际上，由于目前range和round-robin两种分配器都有一些弊端，
            Kafka社区已经提出第三种分配器来实现更加公平的分配策略，只是目前还在开发中。我们这里只需要知道
            consumer group默认已经帮我们把订阅topic的分区分配工作做好了就行了。
            
        谁来执行rebalance和consumer group管理？
            
            Kafka提供了一个角色：coordinator来执行对于consumer group的管理。坦率说kafka对于coordinator的设计与修改是一个很长的故事。
            最新版本的coordinator也与最初的设计有了很大的不同。这里我只想提及两次比较大的改变。
            
            首先是0.8版本的coordinator，那时候的coordinator是依赖zookeeper来实现对于consumer group的管理的。
            Coordinator监听zookeeper的/consumers/<group>/ids的子节点变化以及/brokers/topics/<topic>数据变化来判断
            是否需要进行rebalance。group下的每个consumer都自己决定要消费哪些分区，并把自己的决定抢先在zookeeper中的
            /consumers/<group>/owners/<topic>/<partition>下注册。很明显，这种方案要依赖于zookeeper的帮助，
            而且每个consumer是单独做决定的，没有那种“大家属于一个组，要协商做事情”的精神。
            
            基于这些潜在的弊端，0.9版本的kafka改进了coordinator的设计，提出了group coordinator——每个consumer group
            都会被分配一个这样的coordinator用于组管理和位移管理。这个group coordinator比原来承担了更多的责任，
            比如组成员管理、位移提交保护机制等。当新版本consumer group的第一个consumer启动的时候，
            它会去和kafka server确定谁是它们组的coordinator。之后该group内的所有成员都会和该coordinator进行协调通信。
            显而易见，这种coordinator设计不再需要zookeeper了，性能上可以得到很大的提升。后面的所有部分我们都将讨论
            最新版本的coordinator设计。
            
        如何确定coordinator？
        
            上面简单讨论了新版coordinator的设计，那么consumer group如何确定自己的coordinator是谁呢？ 简单来说分为两步：
            
            确定consumer group位移信息写入__consumers_offsets的哪个分区。具体计算公式：
            　　__consumers_offsets partition# = Math.abs(groupId.hashCode() % groupMetadataTopicPartitionCount)   
            注意：groupMetadataTopicPartitionCount由offsets.topic.num.partitions指定，默认是50个分区。
            该分区leader所在的broker就是被选定的coordinator
            
        协议(protocol)
        
            前面说过了， rebalance本质上是一组协议。group与coordinator共同使用它来完成group的rebalance。
            目前kafka提供了5个协议来处理与consumer group coordination相关的问题：
            
            Heartbeat请求：consumer需要定期给coordinator发送心跳来表明自己还活着
            LeaveGroup请求：主动告诉coordinator我要离开consumer group
            SyncGroup请求：group leader把分配方案告诉组内所有成员
            JoinGroup请求：成员请求加入组
            DescribeGroup请求：显示组的所有信息，包括成员信息，协议名称，分配方案，订阅信息等。通常该请求是给管理员使用
            Coordinator在rebalance的时候主要用到了前面4种请求。
            
        liveness
        
            consumer如何向coordinator证明自己还活着？ 通过定时向coordinator发送Heartbeat请求。如果超过了设定的超时时间，
            那么coordinator就认为这个consumer已经挂了。一旦coordinator认为某个consumer挂了，那么它就会开启新一轮rebalance，
            并且在当前其他consumer的心跳response中添加“REBALANCE_IN_PROGRESS”，告诉其他consumer：不好意思各位，你们重新申请加入组吧！
            
        
        *Rebalance过程
        
            终于说到consumer group执行rebalance的具体流程了。很多用户估计对consumer内部的工作机制也很感兴趣。
            下面就跟大家一起讨论一下。当然我必须要明确表示，rebalance的前提是coordinator已经确定了。
            
            总体而言，rebalance分为2步：Join和Sync
            
            1 Join， 顾名思义就是加入组。这一步中，所有成员都向coordinator发送JoinGroup请求，请求入组。
            一旦所有成员都发送了JoinGroup请求，coordinator会从中选择一个consumer担任leader的角色，
            并把组成员信息以及订阅信息发给leader——注意leader和coordinator不是一个概念。leader负责消费分配方案的制定。
            
            2 Sync，这一步leader开始分配消费方案，即哪个consumer负责消费哪些topic的哪些partition。
            一旦完成分配，leader会将这个方案封装进SyncGroup请求中发给coordinator，非leader也会发SyncGroup请求，
            只是内容为空。coordinator接收到分配方案之后会把方案塞进SyncGroup的response中发给各个consumer。
            这样组内的所有成员就都知道自己应该消费哪些分区了。
        
            值得注意的是， 在coordinator收集到所有成员请求前，它会把已收到请求放入一个叫purgatory(炼狱)的地方。
            记得国内有篇文章以此来证明kafka开发人员都是很有文艺范的，写得也是比较有趣，有兴趣可以去搜搜。
            然后是分发分配方案的过程，即SyncGroup请求：
        
            注意！！ consumer group的分区分配方案是在客户端执行的！Kafka将这个权利下放给客户端主要是因为这样做可以有更好的灵活性。
            比如这种机制下我可以实现类似于Hadoop那样的机架感知(rack-aware)分配方案，即为consumer挑选同一个机架下的分区数据，
            减少网络传输的开销。Kafka默认为你提供了两种分配策略：range和round-robin。由于这不是本文的重点，这里就不再详细展开了，
            你只需要记住你可以覆盖consumer的参数：partition.assignment.strategy来实现自己分配策略就好了。
    
        consumer group状态机
        
            和很多kafka组件一样，group也做了个状态机来表明组状态的流转。coordinator根据这个状态机会对consumer group做不同的处理，
            如下图所示(完全是根据代码注释手动画的，多见谅吧)
            
            简单说明下图中的各个状态：
            
            Dead：组内已经没有任何成员的最终状态，组的元数据也已经被coordinator移除了。
            这种状态响应各种请求都是一个response： UNKNOWN_MEMBER_ID
            Empty：组内无成员，但是位移信息还没有过期。这种状态只能响应JoinGroup请求
            PreparingRebalance：组准备开启新的rebalance，等待成员加入
            AwaitingSync：正在等待leader consumer将分配方案传给各个成员
            Stable：rebalance完成！可以开始消费了~
            至于各个状态之间的流程条件以及action，这里就不具体展开了。
            
    *kafka leader选举机制原理
    https://www.cnblogs.com/smartloli/p/9826923.html
    
        kafka在所有broker中选出一个controller，所有Partition的Leader选举都由controller决定。
        controller会将Leader的改变直接通过RPC的方式（比Zookeeper Queue的方式更高效）通知需为此作出响应的Broker。
        同时controller也负责增删Topic以及Replica的重新分配。
        
        Kafka控制器，其实就是一个Kafka系统的Broker。它除了具有一般Broker的功能之外，还具有选举主题分区Leader节点的功能。
        在启动Kafka系统时，其中一个Broker会被选举为控制器，负责管理主题分区和副本状态，还会执行分区重新分配的管理任务。
        如果在Kafka系统运行过程中，当前的控制器出现故障导致不可用，那么Kafka系统会从其他正常运行的Broker中重新选举出新的控制器。
            
        控制器启动顺序
        
            在Kafka集群中，每个Broker在启动时会实例化一个KafkaController类。该类会执行一系列业务逻辑，选举出主题分区的Leader节点，步骤如下：
            
            第一个启动的代理节点，会在Zookeeper系统里面创建一个临时节点/controller，并写入该节点的注册信息，使该节点成为控制器；
            其他的代理节点陆续启动时，也会尝试在Zookeeper系统中创建/controller节点，但是由于/controller节点已经存在，
            所以会抛出“创建/controller节点失败异常”的信息。创建失败的代理节点会根据返回的结果，
            判断出在Kafka集群中已经有一个控制器被成功创建了，所以放弃创建/controller节点，这样就确保了Kafka集群控制器的唯一性；
            其他的代理节点，会在控制器上注册相应的监听器，各个监听器负责监听各自代理节点的状态变化。当监听到节点状态发生变化时，
            会触发相应的监听函数进行处理
    
        主题分区Leader节点的选举过程
            
            选举控制器的核心思路是：各个代理节点公平竞争抢占Zookeeper系统中创建/controller临时节点，
            最先创建成功的代理节点会成为控制器，并拥有选举主题分区Leader节点的功能。
            
            当Kafka系统实例化KafkaController类时，主题分区Leader节点的选举流程便会开始。其中涉及的核心类包含
            KafkaController、ZookeeperLeaderElector、LeaderChangeListener、SessionExpirationListener。
            
            KafkaController：在实例化ZookeeperLeaderElector类时，分别设置了两个关键的回调函数，
            即onControllerFailover和onControllerResignation；
            ZookeeperLeaderElector：实现主题分区的Leader节点选举功能，但是它并不会处理“代理节点与Zookeeper系统之间出现的会话超时”
            这种情况，它主要负责创建元数据存储路径、实例化变更监听器等，并通过订阅数据变更监听器来实时监听数据的变化，
            进而开始执行选举Leader的逻辑；
            LeaderChangeListener：如果节点数据发送变化，则Kafka系统中的其他代理节点可能已经成为Leader，
            接着Kafka控制器会调用onResigningAsLeader函数。当Kafka代理节点宕机或者被人为误删除时，
            则处于该节点上的Leader会被重新选举，通过调用onResigningAsLeader函数重新选择其他正常运行的代理节点成为新的Leader；
            SessionExpirationListener：当Kafka系统的代理节点和Zookeeper系统建立连接后，
            SessionExpirationListener中的handleNewSession函数会被调用，对于Zookeeper系统中会话过期的连接，会先进行一次判断
    
    下面主要对core目录模块进行说明，这块是kafka的核心。
    
        admin：管理员模块，操作和管理topic，paritions相关，包含create,delete topic,扩展patitions
        api：这块主要负责数据的组装，客户端和服务端数据交互的组装
        client：这个模块比较简单，只有一个类，主要是获取一些元数据，包括topic、broker等
        cluster：该模块定义了几个在kafka中比较重要的类：Broker，BrokerEndPoint，Cluster，EndPoint，Partition，Replica等，后续我们会对他们之间的关系进行分析
        common：通用类，定义了一些异常类等等
        consumer：comsumer处理模块，负责与消费者相关的操作
        controller：负责中央控制器选举，partition的leader选举，副本分配，副本重新分配，partition和replica扩容
        coordinator：协调器，rebalance的一些协调器，比如延迟心跳等
        javaapi：kafka提供出来的java生产消费的api
        log：文件存储模块，负责读写所有kafka的topic消息数据，也就是消息持久化模块
        message：封装多个消息组成一个“消息集”或压缩消息集
        metrics：内部状态监控模块
        network：kafka的网络处理模块，负责接受和处理客户端连接
        producer：生产者模块，包括同步和异步发送消息
        security.auth：安全认证模块
        serializer：序列化和反序列化工具
        server：kafka服务启动相关内容
        tools：工具模块，内容挺多，主要是与kafka相关的工具
        utils：通用工具模块，包括zk等等
        Kafka：程序入口
        
    
    kafka顺序写
        发到某个topic的消息会被均匀的分布到多个Partition上（随机或根据用户指定的回调函数进行分布），
        broker收到发布消息往对应Partition的最后一个segment上添加该消息，segment达到一定的大小后将不会再往该segment写数据，
        broker会创建新的segment。
        每条消息都被append到该Partition中，属于顺序写磁盘，因此效率非常高（经验证，顺序写磁盘效率比随机写内存还要高，
        这是Kafka高吞吐率的一个很重要的保证）。
        
        如何实现顺序写而非随机写。首先顺序写性能是随机写的万倍（300MB/S：30KB/S）；性能超过固态硬盘，是kafka高兴能的保证之一 ，
        其次还有buffer减少IO，以及零拷贝避免二次拷贝以及内核态到用户态的切换。但是我不懂顺序写是如何实现的，
        之前学习zookeeper知道follower的事物文件是先申请一块64M的连续磁盘空间，当不足4KB时再申请一块64M的连续磁盘空间，
        当有新事物来时，只在文件尾部做追加操作，可能达到磁盘顺序写的效果。好像中奖了。。
    
    Kafka集群会保留所有的消息，无论其被消费与否。两种策略删除旧数据：
    
        一基于时间的SLA(服务水平保证)，消息保存一定时间（通常为7天）后会被删除
        二是基于Partition文件大小，可以通过配置$KAFKA_HOME/config/server.properties
        
    消息的有序性
    
        总结：如果想保证消息的顺序，那就用一个 partition。 kafka 的每个 partition 只能同时被同一个 group 中的一个 consumer 消费
        
    
    充分利用Page Cache
    
        I/O Scheduler会将连续的小块写组装成大块的物理写从而提高性能
        
        I/O Scheduler会尝试将一些写操作重新按顺序排好，从而减少磁盘头的移动时间
        
        充分利用所有空闲内存（非JVM内存）。如果使用应用层Cache（即JVM堆内存），会增加GC负担
        
        读操作可直接在Page Cache内进行。如果消费和生产速度相当，甚至不需要通过物理磁盘（直接通过Page Cache）交换数据
        
        如果进程重启，JVM内的Cache会失效，但Page Cache仍然可用
        Broker收到数据后，写磁盘时只是将数据写入Page Cache，并不保证数据一定完全写入磁盘。从这一点看，可能会造成机器宕机时，
        Page Cache内的数据未写入磁盘从而造成数据丢失。但是这种丢失只发生在机器断电等造成操作系统不工作的场景，
        而这种场景完全可以由Kafka层面的Replication机制去解决。  
    
    支持多Disk Drive
    
        Broker的log.dirs配置项，允许配置多个文件夹。如果机器上有多个Disk Drive，可将不同的Disk挂载到不同的目录，
        然后将这些目录都配置到log.dirs里。Kafka会尽可能将不同的Partition分配到不同的目录，也即不同的Disk上，
        从而充分利用了多Disk的优势。
        
    零拷贝
    
        Kafka中存在大量的网络数据持久化到磁盘（Producer到Broker）和磁盘文件通过网络发送（Broker到Consumer）的过程。
        这一过程的性能直接影响Kafka的整体吞吐量。
        
        
        传统模式下的四次拷贝与四次上下文切换
        
        以将磁盘文件通过网络发送为例。传统模式下，一般使用如下伪代码所示的方法先将文件数据读入内存，然后通过Socket将内存中的数据发送出去。
        
        buffer = File.read
        Socket.send(buffer)
        这一过程实际上发生了四次数据拷贝。首先通过系统调用将文件数据读入到内核态Buffer（DMA拷贝），
        然后应用程序将内存态Buffer数据读入到用户态Buffer（CPU拷贝），接着用户程序通过Socket发送数据时将用户态Buffer数据
        拷贝到内核态Buffer（CPU拷贝），最后通过DMA拷贝将数据拷贝到NIC Buffer。同时，还伴随着四次上下文切换，如下图所示。
        
        sendfile和transferTo实现零拷贝
        
        Linux 2.4+内核通过sendfile系统调用，提供了零拷贝。数据通过DMA拷贝到内核态Buffer后，直接通过DMA拷贝到NIC Buffer，
        无需CPU拷贝。这也是零拷贝这一说法的来源。除了减少数据拷贝外，因为整个读文件-网络发送由一个sendfile调用完成，
        整个过程只有两次上下文切换，因此大大提高了性能。零拷贝过程如下图所示。
        
        从具体实现来看，Kafka的数据传输通过TransportLayer来完成，其子类PlaintextTransportLayer通过Java NIO的FileChannel的
        transferTo和transferFrom方法实现零拷贝，如下所示。
        
        @Override
        public long transferFrom(FileChannel fileChannel, long position, long count) throws IOException {
        return fileChannel.transferTo(position, count, socketChannel);
        }
        
        注： transferTo和transferFrom并不保证一定能使用零拷贝。实际上是否能使用零拷贝与操作系统相关，
        如果操作系统提供sendfile这样的零拷贝系统调用，则这两个方法会通过这样的系统调用充分利用零拷贝的优势，
        否则并不能通过这两个方法本身实现零拷贝。
        
    批处理
    
        批处理是一种常用的用于提高I/O性能的方式。对Kafka而言，批处理既减少了网络传输的Overhead，又提高了写磁盘的效率。
    
    选举
    https://blog.csdn.net/qq_27384769/article/details/80115392
        Kafka 集群controller的选举过程如下 ：
            每个Broker都会在Controller Path (/controller)上注册一个Watch。
            当前Controller失败时，对应的Controller Path会自动消失（因为它是ephemeral Node），此时该Watch被fire，所有“活”着的Broker都会去竞选成为新的Controller（创建新的Controller Path),但是只会有一个竞选成功（这点由Zookeeper保证）。
            竞选成功者即为新的Leader，竞选失败者则重新在新的Controller Path上注册Watch。因为Zookeeper的Watch是一次性的，被fire一次之后即失效，所以需要重新注册。
        
        Kafka partition leader的选举过程如下 (由controller执行)：
            从Zookeeper中读取当前分区的所有ISR(in-sync replicas)集合
            调用配置的分区选择算法选择分区的leader
        
    
    
    数据压缩降低网络负载
    
        Kafka从0.7开始，即支持将数据压缩后再传输给Broker。除了可以将每条消息单独压缩然后传输外，Kafka还支持在批量发送时，
        将整个Batch的消息一起压缩后传输。数据压缩的一个基本原理是，重复数据越多压缩效果越好。
        因此将整个Batch的数据一起压缩能更大幅度减小数据量，从而更大程度提高网络传输效率。
        
    *ISR机制(in-sync Replica)
    https://blog.csdn.net/qq_37502106/article/details/80271800
        kafka不是完全同步，也不是完全异步，是一种ISR机制： 
            1. leader会维护一个与其基本保持同步的Replica列表，该列表称为ISR(in-sync Replica)，每个Partition都会有一个ISR，而且是由leader动态维护 
            2. 如果一个flower比一个leader落后太多，或者超过一定时间未发起数据复制请求，则leader将其重ISR中移除 
            3. 当ISR中所有Replica都向Leader发送ACK时，leader才commit
        
    kafka进阶
        https://mp.weixin.qq.com/s/3i51S1jDXbqvi6fv1cuQSg?
        https://www.infoq.cn/article/kafka-analysis-part-1
        https://blog.csdn.net/stark_summer/article/details/50203133(kafka性能参数和压力测试揭秘)
        http://zqhxuyuan.github.io/2017/12/31/Kafka-Book-Resources/(Kafka技术内幕拾遗)
    
    *总结:
        1.为什么要使用消息系统?
        2.生产者发送流程?常用参数及意义?
        3.为什么要用消费者组?
        4.什么是rebalance?触发rebalance的情况?消费者组内分配分区的策略?谁负责管理rebalance和消费者组?
        5.如何确定coordinator?使用了哪些协议?
        6.consumer group状态机?
        7.controller选举?主题分区Leader节点的选举过程?
        8.分区和副本是在何处，以怎样的方式分配给Broker。
        9.kafka的Controller接收到Zookeeper的通知后做了哪些处理。
        10.分区的leader和follower是如何选举的。
        11.Data Replication何时Commit？
        12.Data Replication如何处理Replica恢复
        13.Data Replication如何处理Replica全部宕机
        
        
    
##14 ZooKeeper

    ZooKeeper 概览
    https://github.com/llohellohe/zookeeper/blob/master/docs/overview.md
    https://www.jianshu.com/p/eec133595c68
    
        ZooKeeper 是一个典型的分布式数据一致性解决方案，分布式应用程序可以基于 ZooKeeper 实现诸如数据发布/订阅、负载均衡、
        命名服务、分布式协调/通知、集群管理、Master 选举、分布式锁和分布式队列等功能。
        文件系统和通知机制 ZooKeeper的ZNode类似于文件系统，只不过每个节点还可以额外存放数据。
        当节点发生变化时（创建、删除、数据变更），可以通知各个客户端。
        
    为什么最好使用奇数台服务器构成 ZooKeeper 集群？
        假如我们的集群中有n台zookeeper服务器，那么也就是剩下的服务数必须大于n/2。先说一下结论，2n和2n-1的容忍度是一样的，
        都是n-1，大家可以先自己仔细想一想，这应该是一个很简单的数学问题了。 比如假如我们有3台，那么最大允许宕掉1台
        zookeeper服务器，如果我们有4台的的时候也同样只允许宕掉1台。

    重要概念总结
        
        ZooKeeper 本身就是一个分布式程序（只要半数以上节点存活，ZooKeeper 就能正常服务）。
        为了保证高可用，最好是以集群形态来部署 ZooKeeper，这样只要集群中大部分机器是可用的（能够容忍一定的机器故障），
        那么 ZooKeeper 本身仍然是可用的。
        ZooKeeper 将数据保存在内存中，这也就保证了 高吞吐量和低延迟（但是内存限制了能够存储的容量不太大，
        此限制也是保持znode中存储的数据量较小的进一步原因）。
        ZooKeeper 是高性能的。 在“读”多于“写”的应用程序中尤其地高性能，因为“写”会导致所有的服务器间同步状态。
        （“读”多于“写”是协调服务的典型场景。）
        ZooKeeper有临时节点的概念。 当创建临时节点的客户端会话一直保持活动，瞬时节点就一直存在。而当会话终结时，瞬时节点被删除。
        持久节点是指一旦这个ZNode被创建了，除非主动进行ZNode的移除操作，否则这个ZNode将一直保存在Zookeeper上。
        ZooKeeper 底层其实只提供了两个功能：①管理（存储、读取）用户程序提交的数据；②为用户程序提供数据节点监听服务。
        
    ZooKeeper可以应用的场景
    https://ronghao.iteye.com/blog/1461798
    
        统一配置：把配置放在ZooKeeper的节点中维护，当配置变更时，客户端可以收到变更的通知，并应用最新的配置。
        集群管理：集群中的节点，创建ephemeral的节点，一旦断开连接，ephemeral的节点会消失，其它的集群机器可以收到消息。
        分布式锁：多个客户端发起节点创建操作，只有一个客户端创建成功，从而获得锁。
        
    zk提供的原语包含：
    
        create
        delete
        exists
        get data
        set data
        get chiledren
        sync
            
    ZooKeeper保证了：
    
        顺序一致性：客户端的操作会被按照顺序执行
        原子性：操作要不失败要不成功
        可靠性：一旦写入成功，数据就会被保持，直到下次覆盖。
        实时性
        单一系统镜像(single system image)：不管连接到zk集群的那台机器，客户端看到的视图都是一致的
        
    实现 ZooKeeper的组件包含
    
        Replicated Database是个内存数据库，保存了所有数据。
        更新会被写到磁盘，以便恢复。写也会被先序列化到磁盘后，在应用到内存数据库中。
        读的时候，会从各自server的内存数据库中读数据，写则是通过一致性协议完成（leader/follwer）的。
        
        
    分布式与数据复制
    
        Zookeeper作为一个集群提供一致的数据服务，自然，它要在所有机器间做数据复制。数据复制的好处：
        1、 容错
        一个节点出错，不致于让整个系统停止工作，别的节点可以接管它的工作；
        2、提高系统的扩展能力
        把负载分布到多个节点上，或者增加节点来提高系统的负载能力；
        3、提高性能
        让客户端本地访问就近的节点，提高用户访问速度。
        
        从客户端读写访问的透明度来看，数据复制集群系统分下面两种：
        1、写主(WriteMaster)
        对数据的修改提交给指定的节点。读无此限制，可以读取任何一个节点。这种情况下客户端需要对读与写进行区别，俗称读写分离；
        2、写任意(Write Any)
        对数据的修改可提交给任意的节点，跟读一样。这种情况下，客户端对集群节点的角色与变化透明。
        
        对zookeeper来说，它采用的方式是写任意。通过增加机器，它的读吞吐能力和响应能力扩展性非常好，而写，
        随着机器的增多吞吐能力肯定下降（这也是它建立observer的原因），而响应能力则取决于具体实现方式，
        是延迟复制保持最终一致性，还是立即复制快速响应。
        我们关注的重点还是在如何保证数据在集群所有机器的一致性，这就涉及到paxos算法。
        
    数据一致性与paxos算法
    
        据说Paxos算法的难理解与算法的知名度一样令人敬仰，所以我们先看如何保持数据的一致性，这里有个原则就是：
        在一个分布式数据库系统中，如果各节点的初始状态一致，每个节点都执行相同的操作序列，那么他们最后能得到一个一致的状态。
        Paxos算法解决的什么问题呢，解决的就是保证每个节点执行相同的操作序列。好吧，这还不简单，master维护一个全局写队列，
        所有写操作都必须放入这个队列编号，那么无论我们写多少个节点，只要写操作是按编号来的，就能保证一致性。没错，就是这样，
        可是如果master挂了呢。
        Paxos算法通过投票来对写操作进行全局编号，同一时刻，只有一个写操作被批准，同时并发的写操作要去争取选票，
        只有获得过半数选票的写操作才会被批准（所以永远只会有一个写操作得到批准），其他的写操作竞争失败只好再发起一轮投票，
        就这样，在日复一日年复一年的投票中，所有写操作都被严格编号排序。编号严格递增，当一个节点接受了一个编号为100的写操作，
        之后又接受到编号为99的写操作（因为网络延迟等很多不可预见原因），它马上能意识到自己数据不一致了，
        自动停止对外服务并重启同步过程。任何一个节点挂掉都不会影响整个集群的数据一致性（总2n+1台，除非挂掉大于n台）。
    
    ZooKeeper Watcher
    
        Watcher接口 Watcher接口定义了process(WatchedEvent event) 方法，以及定义了接口Event。
        接口Event中定义了KeeperState和EventType。
        
        WatchedEvent WatchedEvent由KeeperState、EventType和path组成。
        
        它代表当前ZooKepper的连接状态，并且提供发生事件的znode路径以及时间类型。
        
        其中KeeperState代表ZooKeeper的连接状态，分别为：
            Disconnected
            NoSyncConnected
            SyncConnected
            AuthFailed
            ConnectedReadOnly
            SaslAuthenticated
            Expired
            
        EventType代表node的状态变更，分别为：
            None
            NodeCreated
            NodeDeleted
            NodeDataChanged，就算设置重复的数据也会有该事件
            NodeChildrenChanged
        
        Watcher 和 AsyncCallback 的区别
        
            Watcher：Watcher是用于监听节点，session 状态的，比如getData对数据节点a设置了watcher，那么当a的数据内容发生改变时，
            客户端会收到NodeDataChanged通知，然后进行watcher的回调。
            
            AsyncCallback:AsyncCallback是在以异步方式使用 ZooKeeper API 时，用于处理返回结果的。例如：getData同步调用的版本
            是：byte[] getData(String path, boolean watch,Stat stat)，异步调用的版本是：
            void getData(String path,Watcher watcher,AsyncCallback.DataCallback cb,Object ctx)，可以看到，
            前者是直接返回获取的结果，后者是通过AsyncCallback回调处理结果的。
        
        Watcher 的类型
            Watcher 主要是通过ClientWatchManager进行管理的。
            ClientWatchManager中有四种Watcher
            
                defaultWatcher：创建Zookeeper连接时传入的Watcher，用于监听 session 状态
                dataWatches：存放getData传入的Watcher
                existWatches：存放exists传入的Watcher，如果节点已存在，则Watcher会被添加到dataWatches
                childWatches：存放getChildren传入的Watcher
                
            从代码上可以发现，监听器是存在HashMap中的，key是节点名称path，value是Set<Watcher>
            private final Map<String, Set<Watcher>> dataWatches =
                    new HashMap<String, Set<Watcher>>();
            private final Map<String, Set<Watcher>> existWatches =
                    new HashMap<String, Set<Watcher>>();
            private final Map<String, Set<Watcher>> childWatches =
                    new HashMap<String, Set<Watcher>>();
            
            private volatile Watcher defaultWatcher;
            private final Map<String, Set<Watcher>> dataWatches =
                    new HashMap<String, Set<Watcher>>();
            private final Map<String, Set<Watcher>> existWatches =
                    new HashMap<String, Set<Watcher>>();
            private final Map<String, Set<Watcher>> childWatches =
                    new HashMap<String, Set<Watcher>>();
             
            private volatile Watcher defaultWatcher;
        
        通知的状态类型与事件类型
            在Watcher接口中，已经定义了所有的状态类型和事件类型
            
                KeeperState.Disconnected(0)此时客户端处于断开连接状态，和ZK集群都没有建立连接。
                EventType.None(-1)触发条件：一般是在与服务器断开连接的时候，客户端会收到这个事件。
                KeeperState. SyncConnected(3)此时客户端处于连接状态
                EventType.None(-1)触发条件：客户端与服务器成功建立会话之后，会收到这个通知。
                EventType. NodeCreated (1)触发条件：所关注的节点被创建。
                EventType. NodeDeleted (2)触发条件：所关注的节点被删除。
                EventType. NodeDataChanged (3)触发条件：所关注的节点的内容有更新。注意，这个地方说的内容是指数据的版本号dataVersion。
                因此，即使使用相同的数据内容来更新，还是会收到这个事件通知的。无论如何，调用了更新接口，就一定会更新dataVersion的。
                EventType. NodeChildrenChanged (4)触发条件：所关注的节点的子节点有变化。这里说的变化是指子节点的个数和组成，
                具体到子节点内容的变化是不会通知的。
                KeeperState. AuthFailed(4)认证失败
                EventType.None(-1)
                KeeperState. Expired(-112)session 超时
                EventType.None(-1)
                
            每次返回都会从HashMap中移除节点对应的Watcher，例如：addTo(dataWatches.remove(clientPath), result);，
            这就是为什么Watcher是一次性的原因（defaultWatcher除外）。值得注意的是，由于使用的是HashSet存储Watcher，
            重复添加同一个实例的Watcher也只会被触发一次。
        
        
        Zookeeper 客户端会启动两个常驻线程
        
            SendThread：负责 IO 操作，包括发送，接受响应，发送 ping 等。
            EventThread：负责处理事件，执行回调函数。
        
        
        总结
            Zookeeper 客户端中Watcher和AsyncCallback都是异步回调的方式，但它们回调的时机是不一样的，前者是由服务器发送事件触发客户端回调，
            后者是在执行了请求后得到响应后客户端主动触发的。它们的共同点在于都需要在获取了服务器响应之后，
            由SendThread写入EventThread的waitingEvents中，然后由EventThread逐个从事件队列中获取并处理。
        
    创建ZooKeeper Session
    
        ZooKeeper(String connectString, int sessionTimeout, Watcher watcher)
        就可以创建Zookeeper的一个Session。
        
        过程和原理
            初始化连接到ZooKeeper，并且注册一个监视器W
            W在接收到事件后，执行process()方法，根据事件的state()关闭或者重新启动额外的任务进程。
            如果发生事件的znode和注册的znode路径一致，则调用ZooKeeper的exist()方法，然后执行StatCallback这个回调方法。
            在StatCallback的回调方法中，获得znode对应的数据
            如果数据存在，则执行打印出响应的结果
        
    分布式进程的沟通方式 在分布式系统中的通常有两种通信方式：
        通过网络直接交换消息
        使用共享存储。
        ZooKeeper用的是共享存储的方式。
        
        在分布式系统中需要关注以下三个问题：
            消息延迟
            处理器速度
            时钟偏差
            
        Master-Worker架构 Master-Worker是典型的一种分布式系统的架构，它需要解决三个关键问题：
            Master Crash
            Worker Crash
            Master-Worker通信失败
        
        Master Crash 关键一：需要关注主Master crash后，备用master启动后。
        如何恢复到主Master crash时候的状态，由于主已经挂了，因此备用master不可能从主中恢复状态，而需要借助其它方式。
        关键二：避免出现脑裂的情况。
        比如某些情况下，由于主load很高，导致通信延迟严重，可能勿认为主挂了，从而启动另个主，这样会产生脑裂的情况，从而导致不一致。
        
        Worker Crash Master需要能检测出worker挂了，将任务重新分配。
        有时候还需要清理worker挂后产生的一些副作用，比如数据清理等。
        
    ZNode模式 目前ZNode包含持久模式和短暂模式ephemeral。
    
        ephemeral模式指的是这个节点在session断了之后就会消失。
        而持久模式的ZNode则会继续保持。
        在master-worker模式下，ephemeral模式可以用于检测master或者worker是否挂掉。
        如果使用持久模式的话，由于ZNode一直存在，无法感知到master或者worker是否已经crash。
        ephemeral模式的节点也可以被主动删除。
        除了持久模式和ephemeral模式外，ZNode还可以是有序的（自动分配自增ID到节点上，比如task-1,task-2,task-3）。
        因此ZNode一共有四种形态：
        
            持久
            持久有序
            ephemeral
            ephemeral有序

        
    分布式锁 
        1.利用节点名称的唯一性来实现共享锁
            ZooKeeper抽象出来的节点结构是一个和unix文件系统类似的小型的树状的目录结构。ZooKeeper机制规定：同一个目录下只能有一个唯一的文件名。
            例如：我们在Zookeeper目录/test目录下创建，两个客户端创建一个名为Lock节点，只有一个能够成功。
            算法思路: 利用名称唯一性，加锁操作时，只需要所有客户端一起创建/test/Lock节点，只有一个创建成功，成功者获得锁。解锁时，
            只需删除/test/Lock节点，其余客户端再次进入竞争创建节点，直到所有客户端都获得锁。
        2.利用临时顺序节点实现共享锁的一般做法
            Zookeeper中有一种节点叫做顺序节点，故名思议，假如我们在/lock/目录下创建节3个点，ZooKeeper集群会按照提起创建的顺序来创建节点，
            节点分别为/lock/0000000001、/lock/0000000002、/lock/0000000003。
            ZooKeeper中还有一种名为临时节点的节点，临时节点由某个客户端创建，当客户端与ZooKeeper集群断开连接，则开节点自动被删除。
            
            利用上面这两个特性，我们来看下获取实现分布式锁的基本逻辑：
                客户端调用create()方法创建名为“locknode/guid-lock-”的节点，需要注意的是，这里节点的创建类型需要设置为EPHEMERAL_SEQUENTIAL。
                客户端调用getChildren(“locknode”)方法来获取所有已经创建的子节点，同时在这个节点上注册上子节点变更通知的Watcher。
                客户端获取到所有子节点path之后，如果发现自己在步骤1中创建的节点是所有节点中序号最小的，那么就认为这个客户端获得了锁。
                如果在步骤3中发现自己并非是所有子节点中最小的，说明自己还没有获取到锁，就开始等待，直到下次子节点变更通知的时候，
                再进行子节点的获取，判断是否获取锁。
                释放锁的过程相对比较简单，就是删除自己创建的那个子节点即可。
        
            上面这个分布式锁的实现中，大体能够满足了一般的分布式集群竞争锁的需求。这里说的一般性场景是指集群规模不大，一般在10台机器以内。
            不过，细想上面的实现逻辑，我们很容易会发现一个问题，步骤4，“即获取所有的子点，判断自己创建的节点是否已经是序号最小的节点”，
            这个过程，在整个分布式锁的竞争过程中，大量重复运行，并且绝大多数的运行结果都是判断出自己并非是序号最小的节点，
            从而继续等待下一次通知——这个显然看起来不怎么科学。客户端无端的接受到过多的和自己不相关的事件通知，这如果在集群规模大的时候，
            会对Server造成很大的性能影响，并且如果一旦同一时间有多个节点的客户端断开连接，这个时候，
            服务器就会像其余客户端发送大量的事件通知——这就是所谓的惊群效应。而这个问题的根源在于，没有找准客户端真正的关注点。
            我们再来回顾一下上面的分布式锁竞争过程，它的核心逻辑在于：判断自己是否是所有节点中序号最小的。于是，很容易可以联想的到的是，
            每个节点的创建者只需要关注比自己序号小的那个节点。
        
        3.利用临时顺序节点实现共享锁的改进实现
            下面是改进后的分布式锁实现，和之前的实现方式唯一不同之处在于，这里设计成每个锁竞争者，只需要关注”locknode”节点下序号比
            自己小的那个节点是否存在即可。
            算法思路：对于加锁操作，可以让所有客户端都去/lock目录下创建临时顺序节点，如果创建的客户端发现自身创建节点序列号是
            /lock/目录下最小的节点，则获得锁。否则，监视比自己创建节点的序列号小的节点（比自己创建的节点小的最大节点），进入等待。
            对于解锁操作，只需要将自身创建的节点删除即可。
            
    如果创建了临时顺序节点，那么ZooKeeper会自动在节点后缀加上一个数字，并且在API接口的返回值中返回该数据节点的一个完整的节点路径。
    
    Watch和Notifications Watch可以避免每次主动去请求数据是否变化，而是当ZNode变化时，来通知。
    
        Watch是个一次性操作，每次收到通知后，必须重新watch，如果时间比较久或者ZNode更新频繁，在此时间之间，
        可能会有更新没有被通知到（还没来得急watch）。
        ZNode的创建、删除和修改都可以被watch到。
        遗留问题：ZK是否能得到每次节点通知？
        ZK有个保证是，每次通知被送到每个客户端前，ZNode不会发生下一次的变化。
        因此客户端可以保证看到的变化是顺序一致的。    
        
    版本号 
        每个ZNode都会带有一个版本号，每次节点变化，版本号就会变化。
        以此可以避免并发的更新问题（版本号不正确的更新会失败）。
        如果不管版本,可以设置为-1
        
    ZXID 为保证客户端重连到新的服务端时，不会看到过期的更新，保证至少新服务端上的更新比客户端之前看到的更新要新。
        ZK有个全局的事务ID，每次更新操作后，ZXID会自增。
        如果客户端连在S1上，并且执行了更新操作，那么ZXID将会+1,比如ZXID=2。
        当客户端尝试重连到S2时，若S2由于延迟等，导致上面的ZXID 依旧为1的话，客户端将重试连接到S3（ZXID>2）。
        
    
    Observer
        observer 是除了leader\follower的另种角色，它不参与投票过程。
        
    leader的选择机制
        zookeeper提供了三种方式：
        
            LeaderElection
            AuthFastLeaderElection
            FastLeaderElection
            
            默认的算法是FastLeaderElection，所以这篇主要分析它的选举机制。   
    
        选择机制中的概念
            服务器ID
            比如有三台服务器，编号分别是1,2,3。
            编号越大在选择算法中的权重越大。
            
            数据ID
            服务器中存放的最大数据ID.
            值越大说明数据越新，在选举算法中数据越新权重越大。
            
            逻辑时钟
            或者叫投票的次数，同一轮投票过程中的逻辑时钟值是相同的。每投完一次票这个数据就会增加，然后与接收到的其它服务器返回的投票信息中的数值相比，根据不同的值做出不同的判断。
            
            选举状态
            LOOKING，竞选状态。
            FOLLOWING，随从状态，同步leader状态，参与投票。
            OBSERVING，观察状态,同步leader状态，不参与投票。
            LEADING，领导者状态。
    
        选举消息内容
            在投票完成后，需要将投票信息发送给集群中的所有服务器，它包含如下内容。
            
            服务器ID
            数据ID
            逻辑时钟
            选举状态
    
        判断是否已经胜出
        默认是采用投票数大于半数则胜出的逻辑。
        
        选举流程简述
            目前有5台服务器，每台服务器均没有数据，它们的编号分别是1,2,3,4,5,按编号依次启动，它们的选择举过程如下：
            
            服务器1启动，给自己投票，然后发投票信息，由于其它机器还没有启动所以它收不到反馈信息，服务器1的状态一直属于Looking。
            服务器2启动，给自己投票，同时与之前启动的服务器1交换结果，由于服务器2的编号大所以服务器2胜出，但此时投票数没有大于半数，所以两个服务器的状态依然是LOOKING。
            服务器3启动，给自己投票，同时与之前启动的服务器1,2交换信息，由于服务器3的编号最大所以服务器3胜出，此时投票数正好大于半数，所以服务器3成为领导者，服务器1,2成为小弟。
            服务器4启动，给自己投票，同时与之前启动的服务器1,2,3交换信息，尽管服务器4的编号大，但之前服务器3已经胜出，所以服务器4只能成为小弟。
            服务器5启动，后面的逻辑同服务器4成为小弟。
    
    
        算法核心
        https://www.cnblogs.com/leesf456/p/6107600.html
        
        　　上图展示了FastLeaderElection模块是如何与底层网络I/O进行交互的。Leader选举的基本流程如下
        
        　　1. 自增选举轮次。Zookeeper规定所有有效的投票都必须在同一轮次中，在开始新一轮投票时，会首先对logicalclock进行自增操作。
        
        　　2. 初始化选票。在开始进行新一轮投票之前，每个服务器都会初始化自身的选票，并且在初始化阶段，每台服务器都会将自己推举为Leader。
        
        　　3. 发送初始化选票。完成选票的初始化后，服务器就会发起第一次投票。Zookeeper会将刚刚初始化好的选票放入sendqueue中，
            由发送器WorkerSender负责发送出去。
        
        　　4. 接收外部投票。每台服务器会不断地从recvqueue队列中获取外部选票。如果服务器发现无法获取到任何外部投票，
            那么就会立即确认自己是否和集群中其他服务器保持着有效的连接，如果没有连接，则马上建立连接，如果已经建立了连接，
            则再次发送自己当前的内部投票。
        
        　　5. 判断选举轮次。在发送完初始化选票之后，接着开始处理外部投票。在处理外部投票时，会根据选举轮次来进行不同的处理。
        
        　　　　· 外部投票的选举轮次大于内部投票。若服务器自身的选举轮次落后于该外部投票对应服务器的选举轮次，
                 那么就会立即更新自己的选举轮次(logicalclock)，并且清空所有已经收到的投票，然后使用初始化的投票来进行
                 PK以确定是否变更内部投票。最终再将内部投票发送出去。
        
        　　　　· 外部投票的选举轮次小于内部投票。若服务器接收的外选票的选举轮次落后于自身的选举轮次，
                 那么Zookeeper就会直接忽略该外部投票，不做任何处理，并返回步骤4。
        
        　　　　· 外部投票的选举轮次等于内部投票。此时可以开始进行选票PK。
        
        　　6. 选票PK。在进行选票PK时，符合任意一个条件就需要变更投票。
        
        　　　　· 若外部投票中推举的Leader服务器的选举轮次大于内部投票，那么需要变更投票。
        
        　　　　· 若选举轮次一致，那么就对比两者的ZXID，若外部投票的ZXID大，那么需要变更投票。
        
        　　　　· 若两者的ZXID一致，那么就对比两者的SID，若外部投票的SID大，那么就需要变更投票。
        
        　　7. 变更投票。经过PK后，若确定了外部投票优于内部投票，那么就变更投票，即使用外部投票的选票信息来覆盖内部投票，
            变更完成后，再次将这个变更后的内部投票发送出去。
        
        　　8. 选票归档。无论是否变更了投票，都会将刚刚收到的那份外部投票放入选票集合recvset中进行归档。recvset用于记录当前服务器
            在本轮次的Leader选举中收到的所有外部投票（按照服务队的SID区别，如{(1, vote1), (2, vote2)...}）。
        
        　　9. 统计投票。完成选票归档后，就可以开始统计投票，统计投票是为了统计集群中是否已经有过半的服务器认可了当前的内部投票，
            如果确定已经有过半服务器认可了该投票，则终止投票。否则返回步骤4。
        
        　　10. 更新服务器状态。若已经确定可以终止投票，那么就开始更新服务器状态，服务器首选判断当前被过半服务器认可的投票所对应的
            Leader服务器是否是自己，若是自己，则将自己的服务器状态更新为LEADING，若不是，则根据具体情况来确定自己是FOLLOWING或是OBSERVING。
        
        　　以上10个步骤就是FastLeaderElection的核心，其中步骤4-9会经过几轮循环，直到有Leader选举产生。
        
    网络I/O
        QuorumCnxManager：网络I/O
        
        　　每台服务器在启动的过程中，会启动一个QuorumPeerManager，负责各台服务器之间的底层Leader选举过程中的网络通信。
        
        　　(1) 消息队列。QuorumCnxManager内部维护了一系列的队列，用来保存接收到的、待发送的消息以及消息的发送器，除接收队列以外，其他队列都按照SID分组形成队列集合，如一个集群中除了自身还有3台机器，那么就会为这3台机器分别创建一个发送队列，互不干扰。
        
        　　　　· recvQueue：消息接收队列，用于存放那些从其他服务器接收到的消息。
        
        　　　　· queueSendMap：消息发送队列，用于保存那些待发送的消息，按照SID进行分组。
        
        　　　　· senderWorkerMap：发送器集合，每个SenderWorker消息发送器，都对应一台远程Zookeeper服务器，负责消息的发送，也按照SID进行分组。
        
        　　　　· lastMessageSent：最近发送过的消息，为每个SID保留最近发送过的一个消息。
        
        　　(2) 建立连接。为了能够相互投票，Zookeeper集群中的所有机器都需要两两建立起网络连接。QuorumCnxManager在启动时会创建一个
                ServerSocket来监听Leader选举的通信端口(默认为3888)。开启监听后，Zookeeper能够不断地接收到来自其他服务器的创建连接请求，
                在接收到其他服务器的TCP连接请求时，会进行处理。为了避免两台机器之间重复地创建TCP连接，Zookeeper只允许SID大的服务器主动
                和其他机器建立连接，否则断开连接。在接收到创建连接请求后，服务器通过对比自己和远程服务器的SID值来判断是否接收连接请求，
                如果当前服务器发现自己的SID更大，那么会断开当前连接，然后自己主动和远程服务器建立连接。一旦连接建立，
                就会根据远程服务器的SID来创建相应的消息发送器SendWorker和消息接收器RecvWorker，并启动。
        
        　　(3) 消息接收与发送。消息接收：由消息接收器RecvWorker负责，由于Zookeeper为每个远程服务器都分配一个单独的RecvWorker，
                因此，每个RecvWorker只需要不断地从这个TCP连接中读取消息，并将其保存到recvQueue队列中。消息发送：
                由于Zookeeper为每个远程服务器都分配一个单独的SendWorker，因此，每个SendWorker只需要不断地从对应的消息发送队列中
                获取出一个消息发送即可，同时将这个消息放入lastMessageSent中。在SendWorker中，一旦Zookeeper发现针对当前服务器的
                消息发送队列为空，那么此时需要从lastMessageSent中取出一个最近发送过的消息来进行再次发送，这是为了解决接收方在消息
                接收前或者接收到消息后服务器挂了，导致消息尚未被正确处理。同时，Zookeeper能够保证接收方在处理消息时，会对重复消息进行正确的处理。
        
    ZAB协议
    
        什么是 ZAB 协议？ ZAB 协议介绍
            
            ZAB 协议全称：Zookeeper Atomic Broadcast（Zookeeper 原子广播协议）。
            ZAB 协议定义：ZAB 协议是为分布式协调服务 Zookeeper 专门设计的一种支持 崩溃恢复 和 原子广播 协议。下面我们会重点讲这两个东西。
            
        Zookeeper 如何处理集群中的数据。所有客户端写入数据都是写入到 主进程（称为 Leader）中，然后，由 Leader 复制到
        备份进程（称为 Follower）中。从而保证数据一致性。从设计上看，和 Raft 类似。
        
        那么复制过程又是如何的呢？复制过程类似 2PC，ZAB 只需要 Follower 有一半以上返回 Ack 信息就可以执行提交，大大减小了同步阻塞。也提高了可用性。
        简单介绍完，开始重点介绍 消息广播 和 崩溃恢复。整个 Zookeeper 就是在这两个模式之间切换。 简而言之，当 Leader 服务可以正常使用，就进入消息广播模式，当 Leader 不可用时，则进入崩溃恢复模式。
    
    消息广播
        ZAB 协议的消息广播过程使用的是一个原子广播协议，类似一个 二阶段提交过程。对于客户端发送的写请求，全部由 Leader 接收，
        Leader 将请求封装成一个事务 Proposal，将其发送给所有 Follwer ，然后，根据所有 Follwer 的反馈，如果超过半数成功响应，
        则执行 commit 操作（先提交自己，再发送 commit 给所有 Follwer）。
        
        整个广播流程分为 3 步骤：
        
        1.将数据都复制到 Follwer 中
        2.等待 Follwer 回应 Ack，最低超过半数即成功
        3.当超过半数成功回应，则执行 commit ，同时提交自己
        
        还有一些细节：
        
            Leader 在收到客户端请求之后，会将这个请求封装成一个事务，并给这个事务分配一个全局递增的唯一 ID，称为事务ID（ZXID），
            ZAB 兮协议需要保证事务的顺序，因此必须将每一个事务按照 ZXID 进行先后排序然后处理。
            在 Leader 和 Follwer 之间还有一个消息队列，用来解耦他们之间的耦合，解除同步阻塞。
            zookeeper集群中为保证任何所有进程能够有序的顺序执行，只能是 Leader 服务器接受写请求，即使是 Follower 服务器接受到客户端的请求，
            也会转发到 Leader 服务器进行处理。
            实际上，这是一种简化版本的 2PC，不能解决单点问题。等会我们会讲述 ZAB 如何解决单点问题（即 Leader 崩溃问题）。

    崩溃恢复
        刚刚我们说消息广播过程中，Leader 崩溃怎么办？还能保证数据一致吗？如果 Leader 先本地提交了，然后 commit 请求没有发送出去，怎么办？
        实际上，当 Leader 崩溃，即进入我们开头所说的崩溃恢复模式（崩溃即：Leader 失去与过半 Follwer 的联系）。下面来详细讲述。
        
        假设1：Leader 在复制数据给所有 Follwer 之后崩溃，怎么办？
        假设2：Leader 在收到 Ack 并提交了自己，同时发送了部分 commit 出去之后崩溃怎么办？
        
        针对这些问题，ZAB 定义了 2 个原则：
        
        ZAB 协议确保那些已经在 Leader 提交的事务最终会被所有服务器提交。
        ZAB 协议确保丢弃那些只在 Leader 提出/复制，但没有提交的事务。
        所以，ZAB 设计了下面这样一个选举算法：
        能够确保提交已经被 Leader 提交的事务，同时丢弃已经被跳过的事务。
        
        针对这个要求，如果让 Leader 选举算法能够保证新选举出来的 Leader 服务器拥有集群总所有机器编号（即 ZXID 最大）的事务，
        那么就能够保证这个新选举出来的 Leader 一定具有所有已经提交的提案。
        而且这么做有一个好处是：可以省去 Leader 服务器检查事务的提交和丢弃工作的这一步操作。
        
    数据同步
        当崩溃恢复之后，需要在正式工作之前（接收客户端请求），Leader 服务器首先确认事务是否都已经被过半的 Follwer 提交了，
        即是否完成了数据同步。目的是为了保持数据一致。
        当所有的 Follwer 服务器都成功同步之后，Leader 会将这些服务器加入到可用服务器列表中。
        实际上，Leader 服务器处理或丢弃事务都是依赖着 ZXID 的，那么这个 ZXID 如何生成呢？
        答：在 ZAB 协议的事务编号 ZXID 设计中，ZXID 是一个 64 位的数字，其中低 32 位可以看作是一个简单的递增的计数器，
        针对客户端的每一个事务请求，Leader 都会产生一个新的事务 Proposal 并对该计数器进行 + 1 操作。
        而高 32 位则代表了 Leader 服务器上取出本地日志中最大事务 Proposal 的 ZXID，并从该 ZXID 中解析出对应的 epoch 值，
        然后再对这个值加一。
        高 32 位代表了每代 Leader 的唯一性，低 32 代表了每代 Leader 中事务的唯一性。同时，也能让 Follwer 通过高 32 位识别不同的 Leader。
        简化了数据恢复流程。
        基于这样的策略：当 Follower 链接上 Leader 之后，Leader 服务器会根据自己服务器上最后被提交的 ZXID 和 Follower 上的 ZXID 进行比对，
        比对结果要么回滚，要么和 Leader 同步。
    
    ZXID变化
        集群范围内，全局单调唯一递增；
        数据构成
        　　ZXID是一个64位的数字，高32位代表Leader周期,低32代表一个单调递增的计数器. 
        当有新的Leader产生时，Leader周期epoch加1，计数器从0开始；
    
    
    进阶
    https://blog.csdn.net/u010039929/article/details/70171754
    
    *总结
        1.zk保证了什么?数据结构?节点类型?
        2.WatchedEvent由什么组成?KeeperState包括哪些类型?EventType包括哪些类型?
        3.zk如何实现分布式锁?(两种方法)排它锁和共享锁?实现分布式队列?
        4.zab协议两种模式?
        5.zk选举流程?
        6.QuorumCnxManager：网络I/O?
    

##15 mybatis

    
    

##16 数据库
    
    什么是脏读？幻读？不可重复读？什么是事务的隔离级别？
        脏读：事务A读取了事务B更新的数据，然后B回滚操作，那么A读取到的数据是脏数据
        不可重复读：事务 A 多次读取同一数据，事务 B 在事务A多次读取的过程中，对数据作了更新并提交，
        导致事务A多次读取同一数据时，结果 不一致。
        幻读：系统管理员A将数据库中所有学生的成绩从具体分数改为ABCDE等级，但是系统管理员B就在这个时候插入了一条具体分数的记录，
        当系统管理员A改结束后发现还有一条记录没有改过来，就好像发生了幻觉一样，这就叫幻读。
    
    SQL的生命周期？关键字的先后顺序？
        1.应用服务器与数据库服务器建立一个连接
        2.数据库进程拿到请求sql
        3.解析并生成执行计划，执行
        4.读取数据到内存并进行逻辑处理
        5.通过步骤一的连接，发送结果到客户端
        
        
    *大数据情况下如何做分页？
    *https://www.cnblogs.com/geningchao/p/6649907.html
        ---方法3: 基于索引再排序
        ---语句样式: MySQL中,可用如下方法: SELECT * FROM 表名称 WHERE id_pk > (pageNum*10) ORDER BY id_pk ASC LIMIT M
        ---适应场景: 适用于数据量多的情况(元组数上万). 最好ORDER BY后的列对象是主键或唯一所以,使得ORDERBY操作能利用索引被消除但结果集是稳定的(稳定的含义,参见方法1)
        ---原因: 索引扫描,速度会很快. 但MySQL的排序操作,只有ASC没有DESC(DESC是假的,未来会做真正的DESC,期待...).
        
        ---方法4: 基于索引使用prepare（第一个问号表示pageNum，第二个？表示每页元组数）
        ---语句样式: MySQL中,可用如下方法: PREPARE stmt_name FROM SELECT * FROM 表名称 WHERE id_pk > (？* ？) ORDER BY id_pk ASC LIMIT M
        ---适应场景: 大数据量
        ---原因: 索引扫描,速度会很快. prepare语句又比一般的查询语句快一点。
        
        ---方法5: 利用MySQL支持ORDER操作可以利用索引快速定位部分元组,避免全表扫描
        比如: 读第1000到1019行元组(pk是主键/唯一键).
        SELECT * FROM your_table WHERE pk>=1000 ORDER BY pk ASC LIMIT 0,20
        
        ---方法6: 利用"子查询/连接+索引"快速定位元组的位置,然后再读取元组. 道理同方法5
        如(id是主键/唯一键,蓝色字体时变量):
        利用子查询示例:
        SELECT * FROM your_table WHERE id <= 
        (SELECT id FROM your_table ORDER BY id desc LIMIT ($page-1)*$pagesize ORDER BY id desc LIMIT $pagesize
        
        利用连接示例:
        SELECT * FROM your_table AS t1 
        JOIN (SELECT id FROM your_table ORDER BY id desc LIMIT ($page-1)*$pagesize AS t2 
        WHERE t1.id <= t2.id ORDER BY t1.id desc LIMIT $pagesize;
        
        mysql大数据量使用limit分页，随着页码的增大，查询效率越低下。

    *某个表有近千万数据，CRUD比较慢，如何优化？分库分表了是怎么做的？分表分库了有什么问题？有用到中间件么?他们的原理知道么？
        垂直分表
            也就是“大表拆小表”，基于列字段进行的。一般是表中的字段较多，将不常用的， 数据较大，长度较长（比如text类型字段）的拆分到“扩展表“。
             一般是针对那种几百列的大表，也避免查询时，数据量太大造成的“跨页”问题。
            
            垂直分库针对的是一个系统中的不同业务进行拆分，比如用户User一个库，商品Producet一个库，订单Order一个库。 切分后，
            要放在多个服务器上，而不是一个服务器上。为什么？ 我们想象一下，一个购物网站对外提供服务，会有用户，商品，
            订单等的CRUD。没拆分之前， 全部都是落到单一的库上的，这会让数据库的单库处理能力成为瓶颈。按垂直分库后，
            如果还是放在一个数据库服务器上， 随着用户量增大，这会让单个数据库的处理能力成为瓶颈，还有单个服务器的磁盘空间，
            内存，tps等非常吃紧。 所以我们要拆分到多个服务器上，这样上面的问题都解决了，以后也不会面对单机资源问题。
            
            数据库业务层面的拆分，和服务的“治理”，“降级”机制类似，也能对不同业务的数据分别的进行管理，维护，监控，扩展等。
            数据库往往最容易成为应用系统的瓶颈，而数据库本身属于“有状态”的，相对于Web和应用服务器来讲，是比较难实现“横向扩展”的。
            数据库的连接资源比较宝贵且单机处理能力也有限，在高并发场景下，垂直分库一定程度上能够突破IO、连接数及单机硬件资源的瓶颈。
            
        水平分表
            针对数据量巨大的单张表（比如订单表），按照某种规则（RANGE,HASH取模等），切分到多张表里面去。 但是这些表还是在同一个库中，
            所以库级别的数据库操作还是有IO瓶颈。不建议采用。
            
            水平分库分表
                将单张表的数据切分到多个服务器上去，每个服务器具有相应的库与表，只是表中数据集合不同。 水平分库分表能够有效的缓解单机和
                单库的性能瓶颈和压力，突破IO、连接数、硬件资源等的瓶颈。
                
                水平分库分表切分规则
                RANGE从
                    0到10000一个表，10001到20000一个表；
                HASH取模
                    一个商场系统，一般都是将用户，订单作为主表，然后将和它们相关的作为附表，这样不会造成跨库事务之类的问题。
                     取用户id，然后hash取模，分配到不同的数据库上。
                地理区域
                    比如按照华东，华南，华北这样来区分业务，七牛云应该就是如此。
                时间
                    按照时间切分，就是将6个月前，甚至一年前的数据切出去放到另外的一张表，因为随着时间流逝，这些表的数据 
                    被查询的概率变小，所以没必要和“热数据”放在一起，这个也是“冷热数据分离”。
        
        
        *如何设计可以动态扩容缩容的分库分表方案？       
            1.设定好几台数据库服务器，每台服务器上几个库，每个库多少个表，推荐是 32库 * 32表，对于大部分公司来说，可能几年都够了。
            2.路由的规则，orderId 模 32 = 库，orderId / 32 模 32 = 表
            3.扩容的时候，申请增加更多的数据库服务器，装好 mysql，呈倍数扩容，4 台服务器，扩到 8 台服务器，再到 16 台服务器。
            4.由 dba 负责将原先数据库服务器的库，迁移到新的数据库服务器上去，库迁移是有一些便捷的工具的。
            5.我们这边就是修改一下配置，调整迁移的库所在数据库服务器的地址。
            6.重新发布系统，上线，原先的路由规则变都不用变，直接可以基于 n 倍的数据库服务器的资源，继续进行线上系统的提供服务。
                    
        分库分表后面临的问题
            事务支持 
                分库分表后，就成了分布式事务了。如果依赖数据库本身的分布式事务管理功能去执行事务，将付出高昂的性能代价； 
                如果由应用程序去协助控制，形成程序逻辑上的事务，又会造成编程方面的负担。
                
            跨库join
                只要是进行切分，跨节点Join的问题是不可避免的。但是良好的设计和切分却可以减少此类情况的发生。
                解决这一问题的普遍做法是分两次查询实现。在第一次查询的结果集中找出关联数据的id,
                根据这些id发起第二次请求得到关联数据。 分库分表方案产品
                
            跨节点的count,order by,group by以及聚合函数问题 
                这些是一类问题，因为它们都需要基于全部数据集合进行计算。多数的代理都不会自动处理合并工作。
                解决方案：与解决跨节点join问题的类似，分别在各个节点上得到结果后在应用程序端进行合并。
                和join不同的是每个结点的查询可以并行执行，因此很多时候它的速度要比单一大表快很多。
                但如果结果集很大，对应用程序内存的消耗是一个问题。
                
            数据迁移，容量规划，扩容等问题
                来自淘宝综合业务平台团队，它利用对2的倍数取余具有向前兼容的特性（如对4取余得1的数对2取余也是1）来分配数据，
                避免了行级别的数据迁移，但是依然需要进行表级别的迁移，同时对扩容规模和分表数量都有限制。总得来说，
                这些方案都不是十分的理想，多多少少都存在一些缺点，这也从一个侧面反映出了Sharding扩容的难度。
                
            ID问题
                一旦数据库被切分到多个物理结点上，我们将不能再依赖数据库自身的主键生成机制。一方面，
                某个分区数据库自生成的ID无法保证在全局上是唯一的；另一方面，应用程序在插入数据之前需要先获得ID,
                以便进行SQL路由. 一些常见的主键生成策略
                
                Twitter的分布式自增ID算法Snowflake
                    在分布式系统中，需要生成全局UID的场合还是比较多的，twitter的snowflake解决了这种需求，
                    实现也还是很简单的，除去配置信息，核心代码就是毫秒级时间41位 机器ID 10位 毫秒内序列12位。
                    
                    snowflake 算法是 twitter 开源的分布式 id 生成算法，就是把一个 64 位的 long 型的 id，1 个 bit 是不用的，
                    用其中的 41 bit 作为毫秒数，用 10 bit 作为工作机器 id，12 bit 作为序列号。
                    1 bit：不用，为啥呢？因为二进制里第一个 bit 为如果是 1，那么都是负数，但是我们生成的 id 都是正数，所以第一个 bit 统一都是 0。
                    41 bit：表示的是时间戳，单位是毫秒。41 bit 可以表示的数字多达 2^41 - 1，也就是可以标识 2^41 - 1 个毫秒值，换算成年就是表示69年的时间。
                    10 bit：记录工作机器 id，代表的是这个服务最多可以部署在 2^10台机器上哪，也就是1024台机器。但是 10 bit 里 5 个 bit 代表机房 id，5 个 bit 代表机器 id。意思就是最多代表 2^5个机房（32个机房），每个机房里可以代表 2^5 个机器（32台机器）。
                    12 bit：这个是用来记录同一个毫秒内产生的不同 id，12 bit 可以代表的最大正整数是 2^12 - 1 = 4096，也就是说可以用这个 12 bit 代表的数字来区分同一个毫秒内的 4096 个不同的 id。
                    0 | 0001100 10100010 10111110 10001001 01011100 00 | 10001 | 1 1001 | 0000 00000000
                
            跨分片的排序分页
                一般来讲，分页时需要按照指定字段进行排序。当排序字段就是分片字段的时候，我们通过分片规则可以比较容易定位到指定的分片，
                而当排序字段非分片字段的时候，情况就会变得比较复杂了。为了最终结果的准确性，我们需要在不同的分片节点中将数据进行排序并返回，
                并将不同分片返回的结果集进行汇总和再次排序，最后再返回给用户。
            
        *主从同步/读写分离
            
            MySQL 主从复制原理的是啥？
                主库将变更写入 binlog 日志，然后从库连接到主库之后，从库有一个 IO 线程，将主库的 binlog 日志拷贝到自己本地，
                写入一个 relay 中继日志中。接着从库中有一个 SQL 线程会从中继日志读取 binlog，然后执行 binlog 日志中的内容，
                也就是在自己本地再次执行一遍 SQL，这样就可以保证自己跟主库的数据是一样的。
                
                这里有一个非常重要的一点，就是从库同步主库数据的过程是串行化的，也就是说主库上并行的操作，在从库上会串行执行。
                所以这就是一个非常重要的点了，由于从库从主库拷贝日志以及串行执行 SQL 的特点，在高并发场景下，
                从库的数据一定会比主库慢一些，是有延时的。所以经常出现，刚写入主库的数据可能是读不到的，要过几十毫秒，
                甚至几百毫秒才能读取到。
                
                而且这里还有另外一个问题，就是如果主库突然宕机，然后恰好数据还没同步到从库，那么有些数据可能在从库上是没有的，
                有些数据可能就丢失了。
                
                *所以 MySQL 实际上在这一块有两个机制，一个是半同步复制，用来解决主库数据丢失问题；
                一个是并行复制，用来解决主从同步延时问题。
                
                这个所谓半同步复制，也叫 semi-sync 复制，指的就是主库写入 binlog 日志之后，就会将强制此时立即将数据同步到从库，
                从库将日志写入自己本地的 relay log 之后，接着会返回一个 ack 给主库，主库接收到至少一个从库的 ack 之后才会认为写操作完成了。
                
                所谓并行复制，指的是从库开启多个线程，并行读取 relay log 中不同库的日志，然后并行重放不同库的日志，这是库级别的并行。
            
            show status

                查看 Seconds_Behind_Master，可以看到从库复制主库的数据落后了几 ms。
                
                一般来说，如果主从延迟较为严重，有以下解决方案：
                
                分库，将一个主库拆分为多个主库，每个主库的写并发就减少了几倍，此时主从延迟可以忽略不计。
                打开 MySQL 支持的并行复制，多个库并行复制。如果说某个库的写入并发就是特别高，单库写并发达到了 2000/s，并行复制还是没意义。
                重写代码，写代码的同学，要慎重，插入数据时立马查询可能查不到。
                如果确实是存在必须先插入，立马要求就查询到，然后立马就要反过来执行一些操作，对这个查询设置直连主库。不推荐这种方法，你这么搞导致读写分离的意义就丧失了。

            
                *基于gtid主从复制
                    GTID：
                    1）全局事务标识：global transaction identifiers。
                    2）GTID是一个事务一一对应，并且全局唯一ID。
                    3）一个GTID在一个服务器上只执行一次，避免重复执行导致数据混乱或者主从不一致。
                    4）GTID用来代替传统复制方法，不再使用MASTER_LOG_FILE+MASTER_LOG_POS开启复制。而是使用MASTER_AUTO_POSTION=1的方式开始复制。
                    5）MySQL-5.6.5开始支持的，MySQL-5.6.10后开始完善。
                    6）在传统的slave端，binlog是不用开启的，但是在GTID中slave端的binlog是必须开启的，目的是记录执行过的GTID（强制）。
                    
                    组成：
                    1）GTID = source_id：transaction_id
                    2）source_id：用于鉴别原服务器，即mysql服务器唯一的的server_uuid，由于GTID会传递到slave，所以也可以理解为源ID。
                    3）transaction_id：为当前服务器上已提交事务的一个序列号，通常从1开始自增长的序列，一个数值对应一个事务。
                    
                    原理：
                    1）master更新数据时，会在事务前产生GTID，一同记录到binlog日志中。
                    2）slave端的i/o 线程将变更的binlog，写入到本地的relay log中。
                    3）sql线程从relay log中获取GTID，然后对比slave端的binlog是否有记录。
                    4）如果有记录，说明该GTID的事务已经执行，slave会忽略。
                    5）如果没有记录，slave就会从relay log中执行该GTID的事务，并记录到binlog。
                    6）在解析过程中会判断是否有主键，如果没有就用二级索引，如果没有就用全部扫描。


                
            
        mysql中in 和exists 区别
            mysql中的in语句是把外表和内表作hash 连接，而exists语句是对外表作loop循环，每次loop循环再对内表进行查询。
            一直大家都认为exists比in语句的效率要高，这种说法其实是不准确的。这个是要区分环境的。
            
            1.如果查询的两个表大小相当，那么用in和exists差别不大。
            2.如果两个表中一个较小，一个是大表，则子查询表大的用exists，子查询表小的用in。
            3.not in 和not exists如果查询语句使用了not in 那么内外表都进行全表扫描，没有用到索引；而not extsts的子查询依然能用到表上的索引。
              所以无论那个表大，用not exists都比not in要快。
            
    
    
    关掉连接，释放资源
    **索引(聚簇索引,非聚簇索引 根据引擎不同区分)
        索引的数据结构(b+树)
        https://www.cnblogs.com/dreamworlds/p/5398535.html
        
    *聚集和非聚集索引
        聚集索引就是以主键创建的索引
        非聚集索引就是以非主键创建的索引
        
        聚集索引在叶子节点存储的是表中的数据
        非聚集索引在叶子节点存储的是主键和索引列
        使用非聚集索引查询出数据时，拿到叶子上的主键再去查到想要查找的数据。(拿到主键再查找这个过程叫做回表)
        非聚集索引也叫做二级索引
        
    索引主要数据结构
        Hash索引  底层数据结构是哈希表,对于记录大多数查询是单条记录的场景,可以采用
        Btree索引 两种引擎的的底层实现是不同的,myisam 叶子节点是内存地址的引用,innoDB是主键加记录的链表
        
    哈希索引的缺点    
        哈希索引也没办法利用索引完成排序
        不支持最左匹配原则
        在有大量重复键值情况下，哈希索引的效率也是极低的---->哈希碰撞问题。
        不支持范围查询
    
    覆盖索引
        创建多列索引中也涉及到了一种特殊的索引-->覆盖索引
        
        我们前面知道了，如果不是聚集索引，叶子节点存储的是主键+列值
        最终还是要“回表”，也就是要通过主键再查找一次。这样就会比较慢
        覆盖索引就是把要查询出的列和索引是对应的，不做回表操作！
        
    *最左匹配原则：
    
        索引可以简单如一个列(a)，也可以复杂如多个列(a, b, c, d)，即联合索引。
        如果是联合索引，那么key也由多个列组成，同时，索引只能用于查找key是否存在（相等），
        遇到范围查询(>、<、between、like左匹配)等就不能进一步匹配了，后续退化为线性查找。
        因此，列的排列顺序决定了可命中索引的列数。
        
        例子：
        如有索引(a, b, c, d)，查询条件a = 1 and b = 2 and c > 3 and d = 4，则会在每个节点依次命中
        a、b、c，无法命中d。(很简单：索引命中只能是相等的情况，不能是范围匹配)

    =、in自动优化顺序
        不需要考虑=、in等的顺序，mysql会自动优化这些条件的顺序，以匹配尽可能多的索引列。
        例子：
        
        如有索引(a, b, c, d)，查询条件c > 3 and b = 2 and a = 1 and d < 4与a = 1 and c > 3 and b = 2 and d < 4等
        顺序都是可以的，MySQL会自动优化为a = 1 and b = 2 and c > 3 and d < 4，依次命中a、b、c。
    
    *索引总结
        索引在数据库中是一个非常重要的知识点！上面谈的其实就是索引最基本的东西，要创建出好的索引要顾及到很多的方面：
        
        1，最左前缀匹配原则。这是非常重要、非常重要、非常重要（重要的事情说三遍）的原则，MySQL会一直向右匹配直到遇到范围查询（>,<,BETWEEN,LIKE）就停止匹配。
        3，尽量选择区分度高的列作为索引，区分度的公式是 COUNT(DISTINCT col) / COUNT(*)。表示字段不重复的比率，比率越大我们扫描的记录数就越少。
        4，索引列不能参与计算，尽量保持列“干净”。比如，FROM_UNIXTIME(create_time) = '2016-06-06' 就不能使用索引，原因很简单，B+树中存储的都是数据表中的字段值，但是进行检索时，需要把所有元素都应用函数才能比较，显然这样的代价太大。所以语句要写成 ： create_time = UNIX_TIMESTAMP('2016-06-06')。
        5，尽可能的扩展索引，不要新建立索引。比如表中已经有了a的索引，现在要加（a,b）的索引，那么只需要修改原来的索引即可。
        6，单个多列组合索引和多个单列索引的检索查询效果不同，因为在执行SQL时，MySQL只能使用一个索引，会从多个单列索引中选择一个限制最为严格的索引。
    
    选择索引和编写索引的原则
        1.一次查询尽可能多的包括所需要的行
        2.尽量用覆盖索引
        3.顺序I/O性能高
        
    
    *sql优化
        负向查询不能使用索引
        select name from user where id not in (1,3,4);
        应该修改为:
        
        select name from user where id in (2,5,6);
        前导模糊查询不能使用索引
        如:
        
        select name from user where name like '%zhangsan'
        非前导则可以:
        
        select name from user where name like 'zhangsan%'
        建议可以考虑使用 Lucene 等全文索引工具来代替频繁的模糊查询。
        
        数据区分不明显的不建议创建索引
        如 user 表中的性别字段，可以明显区分的才建议创建索引，如身份证等字段。
        
        字段的默认值不要为 null
        这样会带来和预期不一致的查询结果。
        
        在字段上进行计算不能命中索引
        select name from user where FROM_UNIXTIME(create_time) < CURDATE();
        应该修改为:
        
        select name from user where create_time < FROM_UNIXTIME(CURDATE());
        最左前缀问题
        如果给 user 表中的 username pwd 字段创建了复合索引那么使用以下SQL 都是可以命中索引:
        
        select username from user where username='zhangsan' and pwd ='axsedf1sd'
        
        select username from user where pwd ='axsedf1sd' and username='zhangsan'
        
        select username from user where username='zhangsan'
        但是使用
        
        select username from user where pwd ='axsedf1sd'
        是不能命中索引的。
        
        如果明确知道只有一条记录返回
        select name from user where username='zhangsan' limit 1
        可以提高效率，可以让数据库停止游标移动。
        
        不要让数据库帮我们做强制类型转换
        select name from user where telno=18722222222
        这样虽然可以查出数据，但是会导致全表扫描。
        
        需要修改为
        
        select name from user where telno='18722222222'
        如果需要进行 join 的字段两表的字段类型要相同
        不然也不会命中索引。
        
    
    *索引和锁基础知识
    *https://juejin.im/post/5b55b842f265da0f9e589e79    (必看重要)
        索引提高检索速度
        索引降低增删改的速度
        
    **锁
        锁的分类
            按使用方式
                乐观锁
                悲观锁
            按粒度
                表级锁(MyIsam),又分为表共享锁和表排他锁
                行级锁(InnoDB),又分共享锁和排他锁,都是间隙锁
                页级锁(DBD) 
                
                
        定心丸：即使我们不会这些锁知识，我们的程序在一般情况下还是可以跑得好好的。因为这些锁数据库隐式帮我们加了
            对于UPDATE、DELETE、INSERT语句，InnoDB会自动给涉及数据集加排他锁（X)
            MyISAM在执行查询语句SELECT前，会自动给涉及的所有表加读锁，在执行更新操作（UPDATE、DELETE、INSERT等）前，
            会自动给涉及的表加写锁，这个过程并不需要用户干预
        
        锁介绍
            表锁
                开销小，加锁快；不会出现死锁；锁定力度大，发生锁冲突概率高，并发度最低
            行锁
                开销大，加锁慢；会出现死锁；锁定粒度小，发生锁冲突的概率低，并发度高
            
            不同的存储引擎支持的锁粒度是不一样的：
                InnoDB行锁和表锁都支持！
                MyISAM只支持表锁！
            
            *InnoDB只有通过索引条件检索数据才使用行级锁，否则，InnoDB将使用表锁
            也就是说，InnoDB的行锁是基于索引的！
            
            表锁下又分为两种模式：
                表读锁（Table Read Lock）
                表写锁（Table Write Lock）
                在表读锁和表写锁的环境下：读读不阻塞，读写阻塞，写写阻塞！
        
          
       *InnoDB和MyISAM有两个本质的区别：
           InnoDB支持行锁
           InnoDB支持事务 
        
       为了允许行锁和表锁共存，实现多粒度锁机制，InnoDB还有两种内部使用的意向锁（Intention Locks），这两种意向锁都是表锁：
       
           意向共享锁（IS）：事务打算给数据行加行共享锁，事务在给一个数据行加共享锁前必须先取得该表的IS锁。
           意向排他锁（IX）：事务打算给数据行加行排他锁，事务在给一个数据行加排他锁前必须先取得该表的IX锁。
           意向锁也是数据库隐式帮我们做了，不需要程序员操心！
       
       尽可能少遇到死锁：
       
           1）以固定的顺序访问表和行。比如对两个job批量更新的情形，简单方法是对id列表先排序，后执行，这样就避免了交叉等待锁的情形；将两个事务的sql顺序调整为一致，也能避免死锁。
           2）大事务拆小。大事务更倾向于死锁，如果业务允许，将大事务拆小。
           3）在同一个事务中，尽可能做到一次锁定所需要的所有资源，减少死锁概率。
           4）降低隔离级别。如果业务允许，将隔离级别调低也是较好的选择，比如将隔离级别从RR调整为RC，可以避免掉很多因为gap锁造成的死锁。
           5）为表添加合理的索引。可以看到如果不走索引将会为表的每一行记录添加上锁，死锁的概率大大增大。
       
       MVCC(Multi-Version Concurrency Control)多版本并发控制，可以简单地认为：MVCC就是行级锁的一个变种(升级版)。
       
           事务的隔离级别就是通过锁的机制来实现，只不过隐藏了加锁细节
           在表锁中我们读写是阻塞的，基于提升并发性能的考虑，MVCC一般读写是不阻塞的(所以说MVCC很多情况下避免了加锁的操作)
           MVCC实现的读写不阻塞正如其名：多版本并发控制--->通过一定机制生成一个数据请求时间点的一致性数据快照（Snapshot)，
           并用这个快照来提供一定级别（语句级或事务级）的一致性读取。从用户的角度来看，好像是数据库可以提供同一数据的多个版本。
       
       
    explain和profile
       https://juejin.im/post/5c0891c7e51d4538994aefbd
       explain命令输出的结果有10列：id、select_type、table、type、possible_keys、key、key_len、ref、rows、Extra
       
       id
          如果id相同执行顺序由上至下。
          如果id不相同，id的序号会递增，id值越大优先级越高，越先被执行。
          (一般有子查询的SQL语句id就会不同)
       select_type
          SIMPLLE：简单查询，该查询不包含 UNION 或子查询
          PRIMARY：如果查询包含UNION 或子查询，则最外层的查询被标识为PRIMARY
          SUBQUERY：子查询中的第一个select语句(该子查询不在from子句中)
          DERIVED：包含在from子句中子查询(也称为派生表)
       table
          该列显示了对应行正在访问哪个表(有别名就显示别名)。
          当from子句中有子查询时，table列是 <derivenN>格式，表示当前查询依赖 id=N的查询，于是先执行 id=N 的查询
       *type
          该列称为关联类型或者访问类型，它指明了MySQL决定如何查找表中符合条件的行，同时是我们判断查询是否高效的重要依据。
          ALL：全表扫描，这个类型是性能最差的查询之一。通常来说，我们的查询不应该出现 ALL 类型，因为这样的查询，在数据量最大的情况下，对数据库的性能是巨大的灾难。
          index：全索引扫描，和 ALL 类型类似，只不过 ALL 类型是全表扫描，而 index 类型是扫描全部的索引，主要优点是避免了排序，但是开销仍然非常大。如果在 Extra 列看到 Using index，说明正在使用覆盖索引，只扫描索引的数据，它比按索引次序全表扫描的开销要少很多。
          range：范围扫描，就是一个有限制的索引扫描，它开始于索引里的某一点，返回匹配这个值域的行。这个类型通常出现在 =、<>、>、>=、<、<=、IS NULL、<=>、BETWEEN、IN() 的操作中，key 列显示使用了哪个索引，当 type 为该值时，则输出的 ref 列为 NULL，并且 key_len 列是此次查询中使用到的索引最长的那个。
          ref：一种索引访问，也称索引查找，它返回所有匹配某个单个值的行。此类型通常出现在多表的 join 查询, 针对于非唯一或非主键索引, 或者是使用了最左前缀规则索引的查询。
          eq_ref：使用这种索引查找，最多只返回一条符合条件的记录。在使用唯一性索引或主键查找时会出现该值，非常高效。
          const、system：该表至多有一个匹配行，在查询开始时读取，或者该表是系统表，只有一行匹配。其中 const 用于在和 primary key 或 unique 索引中有固定值比较的情形。
          NULL：在执行阶段不需要访问表。
          
       possible_keys
          这一列显示查询可能使用哪些索引来查找
       
       key
          这一列显示MySQL实际决定使用的索引。如果没有选择索引，键是NULL。
       
       key_len
          这一列显示了在索引里使用的字节数，当key列的值为 NULL 时，则该列也是 NULL
       
       ref
          这一列显示了哪些字段或者常量被用来和key配合从表中查询记录出来。
       
       rows
          这一列显示了估计要找到所需的行而要读取的行数，这个值是个估计值，原则上值越小越好。
       
       extra
          Using index：使用覆盖索引，表示查询索引就可查到所需数据，不用扫描表数据文件，往往说明性能不错。
          Using Where：在存储引擎检索行后再进行过滤，使用了where从句来限制哪些行将与下一张表匹配或者是返回给用户。
          Using temporary：在查询结果排序时会使用一个临时表，一般出现于排序、分组和多表 join 的情况，查询效率不高，建议优化。
          Using filesort：对结果使用一个外部索引排序，而不是按索引次序从表里读取行，一般有出现该值，都建议优化去掉，因为这样的查询 CPU 资源消耗大。
          
          
          
          
          
       
    
        
    *总结
    https://github.com/Snailclimb/JavaGuide/blob/master/%E6%95%B0%E6%8D%AE%E5%AD%98%E5%82%A8/MySQL%20Index.md
    *https://mp.weixin.qq.com/s?__biz=MzU3MTQwNDEyMg==&mid=2247483712&idx=1&sn=69d682415757d739437a81e46614010a&chksm=fce1fb75cb967263ec343596a94f52fcff84aeb56f95ced0534fdb739c2eab453f027f07a1c8&token=1140785409&lang=zh_CN#rd
    *https://juejin.im/post/5ba1f32ee51d450e805b43f2#heading-5
        1.为什么索引能提高查询速度?
        2.索引的优点这么多,为什么不每列都建立索引呢?
        3.索引是如何提高查询速度的?
        4.sql可以从哪些地方进行优化?
        5.mysql索引主要使用哪两种数据结构?
        6.myIsam和innoDB Btree实现方式的区别?
        7.什么是覆盖索引?
        8.选择索引和编写索引的原则?
        9.什么是脏读？幻读？不可重复读？什么是事务的隔离级别？
        10.大数据情况下如何做分页？
        11.某个表有近千万数据，CRUD比较慢，如何优化？分库分表了是怎么做的？分表分库了有什么问题？有用到中间件么?他们的原理知道么？
        12.你们有没有做 MySQL 读写分离？如何实现 MySQL 的读写分离？MySQL 主从复制原理的是啥？如何解决 MySQL 主从同步的延时问题？
        13.你能回答下MySQL有哪些引擎和各种引擎的执行原理，以及MySQL的呈现原理

        
        
             
##17 缓存


    guava cache
    https://blog.csdn.net/u012881904/article/details/79263787
    
    redis分布式锁实现
    https://www.cnblogs.com/0201zcr/p/5942748.html
    
    redis
    *https://juejin.im/post/5be3ba7a51882516bc477e1d    (必看)
        Redis常见的数据类型：string、list、hash、set、sortset。
        
        SDS简单动态字符串
            struct sdshdr{
            
                // 字节数组，用于保存字符串
                char buf[];
            
                // 记录buf数组中已使用的字节数量，也是字符串的长度
                int len;
            
                // 记录buf数组未使用的字节数量
                int free;
            }
        
        使用SDS的好处
            SDS与C的字符串表示比较
            
                sdshdr数据结构中用len属性记录了字符串的长度。那么获取字符串的长度时，时间复杂度只需要O(1)。
                SDS不会发生溢出的问题，如果修改SDS时，空间不足。先会扩展空间，再进行修改！(内部实现了动态扩展机制)。
                SDS可以减少内存分配的次数(空间预分配机制)。在扩展空间时，除了分配修改时所必要的空间，还会分配额外的空闲空间(free 属性)。
                SDS是二进制安全的，所有SDS API都会以处理二进制的方式来处理SDS存放在buf数组里的数据。
        
        redis数据结构
            string-->简单的key-value
            list-->有序列表(底层是双向链表)-->可做简单队列
            set-->无序列表(去重)-->提供一系列的交集、并集、差集的命令
            hash-->哈希表-->存储结构化数据
            sortset-->有序集合映射(member-score)-->排行榜
            
        Redis服务器的数据库
            Redis服务器用redisServer结构体来表示，其中redisDb是一个数组，用来保存所有的数据库，dbnum代表数据库的数量(这个可以配置，默认是16)
            
            Redis的数据库就是使用字典(哈希表)来作为底层实现的，对数据库的增删改查都是构建在字典(哈希表)的操作之上的。
            typedef struct redisDb { 
                int id;         // 数据库ID标识
                dict *dict;     // 键空间，存放着所有的键值对              
                dict *expires;  // 过期哈希表，保存着键的过期时间                          
                dict *watched_keys; // 被watch命令监控的key和相应client    
                long long avg_ttl;  // 数据库内所有键的平均TTL（生存时间）     
            } redisDb;
            
            
        *Redis对过期键的处理
            
            因为我们的内存是有限的。所以我们会干掉不常用的数据，保留常用的数据。这就需要我们设置一下键的过期(生存)时间了。
                设置键的生存时间可以通过EXPIRE或者PEXPIRE命令。
                设置键的过期时间可以通过EXPIREAT或者PEXPIREAT命令。
                其实EXPIRE、PEXPIRE、EXPIREAT这三个命令都是通过PEXPIREAT命令来实现的。
                我们在redisDb结构体中还发现了dict *expires;属性，存放所有键过期的时间。
            
            既然有设置过期(生存)时间的命令，那肯定也有移除过期时间，查看剩余生存时间的命令了：
                PERSIST(移除过期时间)
                TTL(Time To Live)返回剩余生存时间，以秒为单位
                PTTL以毫秒为单位返回键的剩余生存时间 
                
            *过期策略
                那这些过期键到了过期的时间，就会立马被删除掉吗？？
                    删除策略可分为三种
                    
                        定时删除(对内存友好，对CPU不友好),到时间点上就把所有过期的键删除了。
                        惰性删除(对CPU极度友好，对内存极度不友好),每次从键空间取键的时候，判断一下该键是否过期了，如果过期了就删除。
                        定期删除(折中),每隔一段时间去删除过期键，限制删除的执行时长和频率。
                    
                        Redis采用的是惰性删除+定期删除两种策略，所以说，在Redis里边如果过期键到了过期的时间了，未必被立马删除的！
                    
            *内存淘汰机制 
                如果定期删除漏掉了很多过期key，也没及时去查(没走惰性删除)，大量过期key堆积在内存里，导致redis内存块耗尽了，咋整？
                    我们可以设置内存最大使用量，当内存使用量超出时，会施行数据淘汰策略。
                    
                    allkeys-lru
                    使用 Redis 缓存数据时，为了提高缓存命中率，需要保证缓存数据都是热点数据。
                    可以将内存最大使用量设置为热点数据占用的内存量，然后启用allkeys-lru淘汰策略，
                    将最近最少使用的数据淘汰
            
        *Redis持久化策略(RDB和AOF)
            Redis是基于内存的，如果不想办法将数据保存在硬盘上，一旦Redis重启(退出/故障)，内存的数据将会全部丢失。
            
                我们肯定不想Redis里头的数据由于某些故障全部丢失(导致所有请求都走MySQL)，
                即便发生了故障也希望可以将Redis原有的数据恢复过来，这就是持久化的作用。
            
            Redis提供了两种不同的持久化方法来讲数据存储到硬盘里边：
            
                RDB(基于快照)，将某一时刻的所有数据保存到一个RDB文件中。
                AOF(append-only-file)，当Redis服务器执行写命令的时候，将执行的写命令保存到AOF文件中。
            
            RDB(快照持久化)
                RDB持久化可以手动执行，也可以根据服务器配置定期执行。RDB持久化所生成的RDB文件是一个经过压缩的二进制文件，
                Redis可以通过这个文件还原数据库的数据。
            
                有两个命令可以生成RDB文件：
                    SAVE会阻塞Redis服务器进程，服务器不能接收任何请求，直到RDB文件创建完毕为止。
                    BGSAVE创建出一个子进程，由子进程来负责创建RDB文件，服务器进程可以继续接收请求。
                
                Redis服务器在启动的时候，如果发现有RDB文件，就会自动载入RDB文件(不需要人工干预)
                服务器在载入RDB文件期间，会处于阻塞状态，直到载入工作完成。
                
                除了手动调用SAVE或者BGSAVE命令生成RDB文件之外，我们可以使用配置的方式来定期执行：
                在默认的配置下，如果以下的条件被触发，就会执行BGSAVE命令
                    save 900 1              #在900秒(15分钟)之后，至少有1个key发生变化，
                    save 300 10            #在300秒(5分钟)之后，至少有10个key发生变化
                    save 60 10000        #在60秒(1分钟)之后，至少有10000个key发生变化
                复制代码原理大概就是这样子的(结合上面的配置来看)：
                
                struct redisServer{
                	// 修改计数器
                	long long dirty;
                
                	// 上一次执行保存的时间
                	time_t lastsave;
                
                	// 参数的配置
                	struct saveparam *saveparams;
                };
                
                复制代码遍历参数数组，判断修改次数和时间是否符合，如果符合则调用besave()来生成RDB文件
                总结：通过手动调用SAVE或者BGSAVE命令或者配置条件触发，将数据库某一时刻的数据快照，生成RDB文件实现持久化。
                
                
            AOF(文件追加)
                从前面的示例看出，我们写了三条命令，AOF文件就保存了三条命令。如果我们的命令是这样子的：
                
                redis > RPUSH list "Java" "3y"
                (integer)2
                
                redis > RPUSH list "Java3y"
                integer(3)
                
                redis > RPUSH list "yyy"
                integer(4)
                
                复制代码同样地，AOF也会保存3条命令。我们会发现一个问题：上面的命令是可以合并起来成为1条命令的，并不需要3条。这样就可以让AOF文件的体积变得更小。
                AOF重写由Redis自行触发(参数配置)，也可以用BGREWRITEAOF命令手动触发重写操作。
                
                要值得说明的是：AOF重写不需要对现有的AOF文件进行任何的读取、分析。AOF重写是通过读取服务器当前数据库的数据来实现的！
                新的AOF文件的命令如下，没有一条是多余的！
                
            AOF后台重写
                Redis将AOF重写程序放到子进程里执行(BGREWRITEAOF命令)，像BGSAVE命令一样fork出一个子进程来完成重写AOF的操作，从而不会影响到主进程。
                AOF后台重写是不会阻塞主进程接收请求的，新的写命令请求可能会导致当前数据库和重写后的AOF文件的数据不一致！
                为了解决数据不一致的问题，Redis服务器设置了一个AOF重写缓冲区，这个缓存区会在服务器创建出子进程之后使用。
                
                
            RDB和AOF对过期键的策略
                RDB持久化对过期键的策略：
                    执行SAVE或者BGSAVE命令创建出的RDB文件，程序会对数据库中的过期键检查，已过期的键不会保存在RDB文件中。
                    载入RDB文件时，程序同样会对RDB文件中的键进行检查，过期的键会被忽略。
                
                AOF持久化对过期键的策略：
                    如果数据库的键已过期，但还没被惰性/定期删除，AOF文件不会因为这个过期键产生任何影响(也就说会保留)，当过期的键被删除了以后，会追加一条DEL命令来显示记录该键被删除了
                    重写AOF文件时，程序会对RDB文件中的键进行检查，过期的键会被忽略。
                
                复制模式：
                    主服务器来控制从服务器统一删除过期键(保证主从服务器数据的一致性)
                
            *RDB和AOF用哪个？
                rdb是Redis DataBase缩写 功能核心函数rdbSave(生成RDB文件)和rdbLoad（从文件加载内存）
                Aof是Append-only file缩写 
                
                RDB和AOF并不互斥，它俩可以同时使用。
                
                    RDB的优点：载入时恢复数据快、文件体积小。
                    RDB的缺点：会一定程度上丢失数据(因为系统一旦在定时持久化之前出现宕机现象，此前没有来得及写入磁盘的数据都将丢失。)
                    AOF的优点：丢失数据少(默认配置只丢失一秒的数据)。
                    AOF的缺点：恢复数据相对较慢，文件体积大
                    
                    aof文件比rdb更新频率高，优先使用aof还原数据。
                    aof比rdb更安全也更大
                    rdb性能比aof好
                    如果两个都配了优先加载AOF
                
                如果Redis服务器同时开启了RDB和AOF持久化，服务器会优先使用AOF文件来还原数据(因为AOF更新频率比RDB更新频率要高，还原的数据更完善)
                
                可能涉及到RDB和AOF的配置：
                
                    redis持久化，两种方式
                    1、rdb快照方式
                    2、aof日志方式
                    
                    ----------rdb快照------------
                    save 900 1
                    save 300 10
                    save 60 10000
                    
                    stop-writes-on-bgsave-error yes
                    rdbcompression yes
                    rdbchecksum yes
                    dbfilename dump.rdb
                    dir /var/rdb/
                    
                    -----------Aof的配置-----------
                    appendonly no # 是否打开 aof日志功能
                    
                    appendfsync always #每一个命令都立即同步到aof，安全速度慢
                    appendfsync everysec
                    appendfsync no 写入工作交给操作系统，由操作系统判断缓冲区大小，统一写入到aof  同步频率低，速度快
                    
                    no-appendfsync-on-rewrite yes 正在导出rdb快照的时候不要写aof
                    auto-aof-rewrite-percentage 100
                    auto-aof-rewrite-min-size 64mb 
                
                    ./bin/redis-benchmark -n 20000
                
            redis单线程为什么这么快?
                I/O多路复用的特点是通过一种机制一个进程能同时等待多个文件描述符，
                而这些文件描述符其中的任意一个进入读就绪状态、等等，select()函数就可以返回。
                select/epoll的优势并不是对于单个连接能处理得更快，而是在于能处理更多的连接。
                
                文件事件处理器使用I/O多路复用程序来同时监听多个Socket。当被监听的Socket准备好执行连接应答(accept)、
                读取(read)等等操作时，与操作相对应的文件事件就会产生，根据文件事件来为Socket关联对应的事件处理器，从而实现功能。  
                
                要值得注意的是：Redis中的I/O多路复用程序会将所有产生事件的Socket放到一个队列里边，
                然后通过这个队列以有序、同步、每次一个Socket的方式向文件事件分派器传送套接字。
                也就是说：当上一个Socket处理完毕后，I/O多路复用程序才会向文件事件分派器传送下一个Socket。
                
            Redis多线程为什么快？
                1）纯内存操作
                2）核心是基于非阻塞的IO多路复用机制
                3）单线程避免了多线程的频繁上下文切换问题
                
            
        redis主从        
            主从架构的特点
                下面我们来看看Redis的主从架构特点：
                
                主服务器负责接收写请求
                从服务器负责接收读请求
                从服务器的数据由主服务器复制过去。主从服务器的数据是一致的
                
            主从架构的好处：
                读写分离(主服务器负责写，从服务器负责读)
                高可用(某一台从服务器挂了，其他从服务器还能继续接收请求，不影响服务)
                处理更多的并发量(每台从服务器都可以接收读请求，读QPS就上去了)
               
            redis复制
                在Redis中，用户可以通过执行SALVEOF命令或者设置salveof选项，让一个服务器去复制(replicate)另一个服务器，
                我们称呼被复制的服务器为主服务器(master)，而对主服务器进行复制的服务器则被称为从服务器(salve)
                
                1.同步(sync)
                    将从服务器的数据库状态更新至主服务器的数据库状态
                2.命令传播(command propagate)
                    主服务器的数据库状态被修改，导致主从服务器的数据库状态不一致，让主从服务器的数据库状态重新回到一致状态
 
                从服务器对主服务器的同步又可以分为两种情况：
                
                    初次同步：从服务器没有复制过任何的主服务器，或者从服务器要复制的主服务器跟上次复制的主服务器不一样。
                    断线后同步：处于命令传播阶段的主从服务器因为网络原因中断了复制，从服务器通过自动重连重新连接主服务器，并继续复制主服务器
                
                *Redis从2.8版本开始，使用PSYNC命令来替代SYNC命令执行复制时同步的操作。
                
                PSYNC命令具有完整重同步和部分重同步两种模式(其实就跟上面所说的初次复制和断线后复制差不多个意思)。
                完整重同步
                    下面先来看看完整重同步是怎么实现的：
                    
                    从服务器向主服务器发送PSYNC命令
                    收到PSYNC命令的主服务器执行BGSAVE命令，在后台生成一个RDB文件。并用一个缓冲区来记录从现在开始执行的所有写命令。
                    当主服务器的BGSAVE命令执行完后，将生成的RDB文件发送给从服务器，从服务器接收和载入RBD文件。将自己的数据库状态更新至与主服务器执行BGSAVE命令时的状态。
                    主服务器将所有缓冲区的写命令发送给从服务器，从服务器执行这些写命令，达到数据最终一致性。
                
                部分重同步
                    接下来我们来看看部分重同步，部分重同步可以让我们断线后重连只需要同步缺失的数据(而不是Redis2.8之前的同步全部数据)，这是符合逻辑的！
                    部分重同步功能由以下部分组成：
                    
                    主从服务器的复制偏移量
                    主服务器的复制积压缓冲区
                    服务器运行的ID(run ID)
                    
            哨兵(Sentinal)机制
            https://juejin.im/post/5c061de7e51d451dcd3c2ab5
             
                Redis提供了哨兵(Sentinal)机制供我们解决上面的情况。如果主服务器挂了，我们可以将从服务器升级为主服务器，
                等到旧的主服务器(挂掉的那个)重连上来，会将它(挂掉的主服务器)变成从服务器。
                这个过程叫做主备切换(故障转移)                
                
                主服务器挂了，主从复制操作就中止了，并且哨兵系统是可以察觉出主服务挂了。
                Redis提供哨兵机制可以将选举一台从服务器变成主服务器
                然后旧的主服务器如果重连了，会变成从服务器
                
                首先我们要知道的是：Sentinel本质上只是一个运行在特殊模式下的Redis服务器。
                因为Sentinel做的事情和Redis服务器是不一样的，所以它们的初始化是有所区别的
                (比如，Sentinel在初始化的时候并不会载入AOF/RDB文件，因为Sentinel根本就不用数据库)。
                在启动的时候会将普通Redis服务器的代码替换成Sentinel专用代码。(所以Sentinel虽然作为Redis服务器，
                但是它不能执行SET、DBSIZE等等命令，因为命令表的代码被替换了
                
                Sentinel会创建两个连向主服务器的网络连接：
                
                命令连接(发送和接收命令)
                订阅连接(订阅主服务器的_sentinel_:hello频道)

            判断主服务器是否下线了
            判断主服务器是否下线有两种情况：
            
                主观下线
                    Sentinel会以每秒一次的频率向与它创建命令连接的实例(包括主从服务器和其他的Sentinel)发送PING命令，通过PING命令返回的信息判断实例是否在线
                    如果一个主服务器在down-after-milliseconds毫秒内连续向Sentinel发送无效回复，那么当前Sentinel就会主观认为该主服务器已经下线了。
            
            
                客观下线
                    当Sentinel将一个主服务器判断为主观下线以后，为了确认该主服务器是否真的下线，它会向同样监视该主服务器的Sentinel询问，看它们是否也认为该主服务器是否下线。
                    如果足够多的Sentinel认为该主服务器是下线的，那么就判定该主服务为客观下线，并对主服务器执行故障转移操作。
            
            
            选举领头Sentinel和故障转移
                选举领头Sentinel的规则也比较多，总的来说就是先到先得(哪个快，就选哪个)
                
            选举出领头的Sentinel之后，领头的Sentinel会对已下线的主服务器执行故障转移操作，包括三个步骤：
            
                在已下线主服务器属下的从服务器中，挑选一个转换为主服务器
                让已下线主服务器属下的所有从服务器改为复制新的主服务器
                已下线的主服务器重新连接时，让他成为新的主服务器的从服务器
            
                挑选某一个从服务器作为主服务器也是有策略的，大概如下：
            
                （1）跟master断开连接的时长
                （2）slave优先级
                （3）复制offset
                （4）run id
            
            tips:目前为止的主从+哨兵架构可以说Redis是高可用的，但要清楚的是：Redis还是会丢失数据的
            
                异步复制导致的数据丢失
                    有部分数据还没复制到从服务器，主服务器就宕机了，此时这些部分数据就丢失了
                
                
                脑裂导致的数据丢失
                    有时候主服务器脱离了正常网络，跟其他从服务器不能连接。此时哨兵可能就会认为主服务器下线了
                    (然后开启选举，将某个从服务器切换成了主服务器)，但是实际上主服务器还运行着。这个时候，
                    集群里就会有两个服务器(也就是所谓的脑裂)。
                    虽然某个从服务器被切换成了主服务器，但是可能客户端还没来得及切换到新的主服务器，客户端还继续写向旧主服务器写数据。
                    旧的服务器重新连接时，会作为从服务器复制新的主服务器(这意味着旧数据丢失)。
                
        缓存雪崩
            如果缓存数据设置的过期时间是相同的，并且Redis恰好将这部分数据全部删光了。这就会导致在这段时间内，这些缓存同时失效，全部请求到数据库中。
            这就是缓存雪崩：
            Redis挂掉了，请求全部走数据库。
            对缓存数据设置相同的过期时间，导致某段时间内缓存失效，请求全部走数据库。
            缓存雪崩如果发生了，很可能就把我们的数据库搞垮，导致整个服务瘫痪！
            
        如何解决缓存雪崩？
            对缓存数据设置相同的过期时间，导致某段时间内缓存失效，请求全部走数据库。
                解决方法：在缓存的时候给过期时间加上一个随机值，这样就会大幅度的减少缓存在同一时间过期。
            
            对于“Redis挂掉了，请求全部走数据库”这种情况，我们可以有以下的思路：
                事发前：实现Redis的高可用(主从架构+Sentinel 或者Redis Cluster)，尽量避免Redis挂掉这种情况发生。
                事发中：万一Redis真的挂了，我们可以设置本地缓存(ehcache)+限流(hystrix)，
                尽量避免我们的数据库被干掉(起码能保证我们的服务还是能正常工作的)
                事发后：redis持久化，重启后自动从磁盘上加载数据，快速恢复缓存数据。
                
                
        什么是缓存穿透?   
            缓存穿透是指查询一个一定不存在的数据。由于缓存不命中，并且出于容错考虑，如果从数据库查不到数据则不写入缓存，
            这将导致这个不存在的数据每次请求都要到数据库去查询，失去了缓存的意义。
            请求的数据在缓存大量不命中，导致请求走数据库。
            
            解决缓存穿透也有两种方案：
            
                由于请求的参数是不合法的(每次都请求不存在的参数)，于是我们可以使用布隆过滤器(BloomFilter)或者压缩filter提前拦截，不合法就不让这个请求到数据库层！
                当我们从数据库找不到的时候，我们也将这个空对象设置到缓存里边去。下次再请求的时候，就可以从缓存里边获取了。
                这种情况我们一般会将空对象设置一个较短的过期时间。
            
        缓存与数据库双写一致
            一般我们对读操作的时候有这么一个固定的套路：
                如果我们的数据在缓存里边有，那么就直接取缓存的。
                如果缓存里没有我们想要的数据，我们会先去查询数据库，然后将数据库查出来的数据写到缓存中。
                最后将数据返回给请求
                
        什么是缓存与数据库双写一致问题？    
            对于缓存在更新时而言，都是建议执行删除操作！
            
            正常的情况是这样的：
                先操作数据库，成功；
                再删除缓存，也成功；
            
            如果原子性被破坏了：
                第一步成功(操作数据库)，第二步失败(删除缓存)，会导致数据库里是新数据，而缓存里是旧数据。
                如果第一步(操作数据库)就失败了，我们可以直接返回错误(Exception)，不会出现数据不一致。
            
            对比两种策略
                我们可以发现，两种策略各自有优缺点：
            
                先删除缓存，再更新数据库
                    在高并发下表现不如意，在原子性被破坏时表现优异
            
                先更新数据库，再删除缓存(Cache Aside Pattern设计模式)
                    在高并发下表现优异，在原子性被破坏时表现不如意
            
        Redis Cluster深入与实践
        
            AOF文件刷新的方式，有三种，参考配置参数appendfsync ：
                appendfsync always每提交一个修改命令都调用fsync刷新到AOF文件，非常非常慢，但也非常安全；
                appendfsync everysec每秒钟都调用fsync刷新到AOF文件，很快，但可能会丢失一秒以内的数据；
                appendfsync no依靠OS进行刷新，redis不主动刷新AOF，这样最快，但安全性就差。默认并推荐每秒刷新，这样在速度和安全上都做到了兼顾。
        
            Master可以将数据保存操作交给Slaves完成，从而避免了在Master中要有独立的进程来完成此操作。
            Redis在master是非阻塞模式，也就是说在slave执行数据同步的时候，master是可以接受客户端的请求的，
            并不影响同步数据的一致性，然而在slave端是阻塞模式的，slave在同步master数据时，并不能够响应客户端的查询。
        
            *一致性哈希
            *https://www.cnblogs.com/lpfuture/p/5796398.html
                集群要实现的目的是要将不同的 key 分散放置到不同的 redis 节点，这里我们需要一个规则或者算法，
                通常的做法是获取 key 的哈希值，然后根据节点数来求模，但这种做法有其明显的弊端，当我们需要增加或减少一个节点时，会造成大量的 key 无法命中，这种比例是相当高的，所以就有人提出了一致性哈希的概念。
                
                一致性哈希有四个重要特征：
                    均衡性：也有人把它定义为平衡性，是指哈希的结果能够尽可能分布到所有的节点中去，这样可以有效的利用每个节点上的资源。
                    单调性：对于单调性有很多翻译让我非常的不解，而我想要的是当节点数量变化时哈希的结果应尽可能的保护已分配的内容不会被重新分派到新的节点。
                    分散性和负载：这两个其实是差不多的意思，就是要求一致性哈希算法对 key 哈希应尽可能的避免重复。
                
                *一致性哈希算法在服务节点太少时，容易因为节点分部不均匀而造成数据倾斜问题。例如系统中只有两台服务器
                为了解决这种数据倾斜问题，一致性哈希算法引入了虚拟节点机制，即对每一个服务节点计算多个哈希，
                每个计算结果位置都放置一个此服务节点，称为虚拟节点。具体做法可以在服务器ip或主机名的后面增加编号来实现。
                例如上面的情况，可以为每台服务器计算三个虚拟节点，于是可以分别计算 “Node A#1”、“Node A#2”、“Node A#3”、
                “Node B#1”、“Node B#2”、“Node B#3”的哈希值，于是形成六个虚拟节点
        
        
            从节点选举
                
                发送授权请求的是一个从节点，并且它所属的主节点处于 FAIL 状态。
                *在已下线主节点的所有从节点中，这个从节点的节点 ID 在排序中是最小的。
                从节点处于正常的运行状态：它没有被标记为 FAIL 状态，也没有被标记为 PFAIL 状态。
            
            
            从redis 3.0之后版本支持redis-cluster集群，Redis-Cluster采用无中心结构，每个节点保存数据和整个集群状态,每个节点都和其他所有节点连接。
            特点：
                无中心架构（不存在哪个节点影响性能瓶颈），少了 proxy 层。
                数据按照 slot 存储分布在多个节点，节点间数据共享，可动态调整数据分布。
                可扩展性，可线性扩展到 1000 个节点，节点可动态添加或删除。
                高可用性，部分节点不可用时，集群仍可用。通过增加 Slave 做备份数据副本
                实现故障自动 failover，节点之间通过 gossip 协议交换状态信息，用投票机制完成 Slave到 Master的角色提升。
            
            缺点：
                资源隔离性较差，容易出现相互影响的情况。
                数据通过异步复制,不保证数据的强一致性
            
                1、所有的redis节点彼此互联(PING-PONG机制),内部使用二进制协议优化传输速度和带宽。
                2、节点的fail是通过集群中超过半数的节点检测失效时才生效。
                3、客户端与redis节点直连,不需要中间proxy层.客户端不需要连接集群所有节点,连接集群中任何一个可用节点即可。
                4、redis-cluster把所有的物理节点映射到[0-16383]slot上（不一定是平均分配）,cluster 负责维护node<->slot<->value。
                5、Redis集群预分好16384个桶，当需要在 Redis 集群中放置一个 key-value 时，根据 CRC16(key) mod 16384的值，决定将一个key放到哪个桶中。



        
        
        
        
            
    *总结
        *redis常见面试题
        *https://juejin.im/post/5b99d4bce51d450e7a24b66e    (必读)
        为什么要用redis而不用map做缓存?
        redis常用的数据结构?
        redis过期策略?内存淘汰策略?
        Redis持久化策略?两种的优缺点?怎么选择?
        Redis单线程为什么快？
        redis主从复制?
        哨兵机制?
        什么是缓存雪崩？如何解决缓存雪崩？
        什么是缓存穿透?如何解决缓存穿透？
        什么是缓存与数据库双写一致问题？
        一致性hash算法?
        如何设计可以动态扩容缩容的分库分表方案？
        
        

     
     
    
     