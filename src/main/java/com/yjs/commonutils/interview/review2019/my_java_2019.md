

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


## 11.java IO NIO

    https://mp.weixin.qq.com/s?__biz=MzU4NDQ4MzU5OA==&mid=2247483956&idx=1&sn=57692bc5b7c2c6dfb812489baadc29c9&chksm=fd985455caefdd4331d828d8e89b22f19b304aa87d6da73c5d8c66fcef16e4c0b448b1a6f791&scene=21#wechat_redirect
        
    
    

## 12.spring

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
    
        

     
     
     
     
     
     
     
     
     
     
     