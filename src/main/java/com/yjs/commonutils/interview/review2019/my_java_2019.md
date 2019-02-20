

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
     
     
     
     *Lock类　
      lock: 在java.util.concurrent包内。共有三个实现：
      
      ReentrantLock
      ReentrantReadWriteLock.ReadLock
      ReentrantReadWriteLock.WriteLock
      
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

    bean生命周期
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
    
        nit-method
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
     
     
     Spring 框架的 AOP
        
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
            
            
     SpringMVC 流程
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
            
        
        Rebalance过程
        
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
    
    数据压缩降低网络负载
    
        Kafka从0.7开始，即支持将数据压缩后再传输给Broker。除了可以将每条消息单独压缩然后传输外，Kafka还支持在批量发送时，
        将整个Batch的消息一起压缩后传输。数据压缩的一个基本原理是，重复数据越多压缩效果越好。
        因此将整个Batch的数据一起压缩能更大幅度减小数据量，从而更大程度提高网络传输效率。
        
    
    kafka进阶
        https://mp.weixin.qq.com/s/3i51S1jDXbqvi6fv1cuQSg?
        https://www.infoq.cn/article/kafka-analysis-part-1
        https://blog.csdn.net/stark_summer/article/details/50203133(kafka性能参数和压力测试揭秘)
        http://zqhxuyuan.github.io/2017/12/31/Kafka-Book-Resources/(Kafka技术内幕拾遗)
    
##14 ZooKeeper

    ZooKeeper 概览
    https://github.com/llohellohe/zookeeper/blob/master/docs/overview.md
    
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
                EventType. NodeDataChanged (3)触发条件：所关注的节点的内容有更新。注意，这个地方说的内容是指数据的版本号dataVersion。因此，即使使用相同的数据内容来更新，还是会收到这个事件通知的。无论如何，调用了更新接口，就一定会更新dataVersion的。
                EventType. NodeChildrenChanged (4)触发条件：所关注的节点的子节点有变化。这里说的变化是指子节点的个数和组成，具体到子节点内容的变化是不会通知的。
                KeeperState. AuthFailed(4)认证失败
                EventType.None(-1)
                KeeperState. Expired(-112)session 超时
                EventType.None(-1)
                
        
        
##15 缓存


    guava cache
    https://blog.csdn.net/u012881904/article/details/79263787
    
    
    
    

     
     
    
     