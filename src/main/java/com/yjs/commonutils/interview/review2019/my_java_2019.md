## 0.jdk

###目录
    1.stream
    2.Fork/Join框架
    3.HashMap,ConcurrentHashMap

    stream
        https://www.cnblogs.com/Dorae/p/7779246.html
        Stream中的操作可以分为两大类：中间操作与结束操作，中间操作只是对操作进行了记录，只有结束操作才会触发实际的计算（即惰性求值），
        这也是Stream在迭代大集合时高效的原因之一。中间操作又可以分为无状态（Stateless）操作与有状态（Stateful）操作，
        前者是指元素的处理不受之前元素的影响；后者是指该操作只有拿到所有元素之后才能继续下去。结束操作又可以分为短路与非短路操作，
        这个应该很好理解，前者是指遇到某些符合条件的元素就可以得到最终结果；而后者是指必须处理所有元素才能得到最终结果。
    
        stream包
            其中各个部分的主要功能为：
            主要是各种操作的工厂类、数据的存储结构以及收集器的工厂类等；
            主要用于Stream的惰性求值实现；
            Stream的并行计算框架；
            存储并行流的中间结果；
            终结操作的定义
        影响并行流的因素
            数据大小；源数据结构（分割越容易越好），arraylist、数组比较好，hashSet、treeSet次之，linked最差；装箱；
            核的数量（可使用）；单元处理开销（越大越好）
        

            
        https://crossoverjie.top/categories/Java-%E8%BF%9B%E9%98%B6/    (推荐)
        
        
    Fork/Join框架详解
        fork/Join框架要完成两件事情：
        　　1.任务分割：首先Fork/Join框架需要把大的任务分割成足够小的子任务，如果子任务比较大的话还要对子任务进行继续分割
        　　2.执行任务并合并结果：分割的子任务分别放到双端队列里，然后几个启动线程分别从双端队列里获取任务执行。子任务执行完的结果
            都放在另外一个队列里，启动一个线程从队列里取数据，然后合并这些数据。
            
            我们进一步了解ForkJoinTask，ForkJoinTask与一般任务的主要区别在于它需要实现compute方法，在这个方法里，首先需要判断
            任务是否足够小，如果足够小就直接执行任务。如果不足够小，就必须分割成两个子任务，每个子任务在调用fork方法时，又会进入
            compute方法，看看当前子任务是否需要继续分割成子任务，如果不需要继续分割，则执行当前子任务并返回结果。使用join方法会等待
            子任务执行完并得到其结果。
            
            extends RecursiveTask<Integer>
    
    
    HashMap? ConcurrentHashMap
        HashMap1.7
            put 方法
                判断当前数组是否需要初始化。
                如果 key 为空，则 put 一个空值进去。
                根据 key 计算出 hashcode。
                根据计算出的 hashcode 定位出所在桶。
                如果桶是一个链表则需要遍历判断里面的 hashcode、key 是否和传入 key 相等，如果相等则进行覆盖，并返回原来的值。
                如果桶是空的，说明当前位置没有数据存入；新增一个 Entry 对象写入当前位置。
            get 方法
                首先也是根据 key 计算出 hashcode，然后定位到具体的桶中。
                判断该位置是否为链表。
                不是链表就根据 key、key 的 hashcode 是否相等来返回值。
                为链表则需要遍历直到 key 及 hashcode 相等时候就返回值。
                啥都没取到就直接返回 null 。  
                
        HashMap1.8
            当 Hash 冲突严重时，在桶上形成的链表会变的越来越长，这样在查询时的效率就会越来越低；时间复杂度为 O(N)。
            
            put 方法
                判断当前桶是否为空，空的就需要初始化（resize 中会判断是否进行初始化）。
                根据当前 key 的 hashcode 定位到具体的桶中并判断是否为空，为空表明没有 Hash 冲突就直接在当前位置创建一个新桶即可。
                如果当前桶有值（ Hash 冲突），那么就要比较当前桶中的 key、key 的 hashcode 与写入的 key 是否相等，相等就赋值给 e,在第 8 步的时候会统一进行赋值及返回。
                如果当前桶为红黑树，那就要按照红黑树的方式写入数据。
                如果是个链表，就需要将当前的 key、value 封装成一个新节点写入到当前桶的后面（形成链表）。
                接着判断当前链表的大小是否大于预设的阈值，大于时就要转换为红黑树。
                如果在遍历过程中找到 key 相同时直接退出遍历。
                如果 e != null 就相当于存在相同的 key,那就需要将值覆盖。
                最后判断是否需要进行扩容。 
            get 方法
                首先将 key hash 之后取得所定位的桶。
                如果桶为空则直接返回 null 。
                否则判断桶的第一个位置(有可能是链表、红黑树)的 key 是否为查询的 key，是就直接返回 value。
                如果第一个不匹配，则判断它的下一个是红黑树还是链表。
                红黑树就按照树的查找方式返回值。
                不然就按照链表的方式遍历匹配返回值。
                
            但是 HashMap 原有的问题也都存在，比如在并发场景下使用时容易出现死循环。
                看过上文的还记得在 HashMap 扩容的时候会调用 resize() 方法，就是这里的并发操作容易在一个桶上形成环形链表；
                这样当获取一个不存在的 key 时，计算出的 index 正好是环形链表的下标就会出现死循环。
               
               
        ConcurrentHashMap1.7
            是由 Segment 数组、HashEntry 组成，和 HashMap 一样，仍然是数组加链表。
            ConcurrentHashMap 采用了分段锁技术，其中 Segment 继承于 ReentrantLock。不会像 HashTable 那样不管是 put 
            还是 get 操作都需要做同步处理，理论上 ConcurrentHashMap 支持 CurrencyLevel (Segment 数组数量)的线程并发。
            每当一个线程占用锁访问一个 Segment 时，不会影响到其他的 Segment。
                
            put 方法
                将当前 Segment 中的 table 通过 key 的 hashcode 定位到 HashEntry。
                遍历该 HashEntry，如果不为空则判断传入的 key 和当前遍历的 key 是否相等，相等则覆盖旧的 value。
                不为空则需要新建一个 HashEntry 并加入到 Segment 中，同时会先判断是否需要扩容。
                最后会解除在 1 中所获取当前 Segment 的锁。
                
            get 方法
                1.7 已经解决了并发问题，并且能支持 N 个 Segment 这么多次数的并发，但依然存在 HashMap 在 1.7 版本中的问题。
                那就是查询遍历链表效率太低。
                因此 1.8 做了一些数据结构上的调整。
           
           
         ConcurrentHashMap1.8
            其中抛弃了原有的 Segment 分段锁，而采用了 CAS + synchronized 来保证并发安全性。
            put 方法
                根据 key 计算出 hashcode 。
                判断是否需要进行初始化。
                f 即为当前 key 定位出的 Node，如果为空表示当前位置可以写入数据，利用 CAS 尝试写入，失败则自旋保证成功。
                如果当前位置的 hashcode == MOVED == -1,则需要进行扩容。
                如果都不满足，则利用 synchronized 锁写入数据。
                如果数量大于 TREEIFY_THRESHOLD 则要转换为红黑树。     
            get 方法
                根据计算出来的 hashcode 寻址，如果就在桶上那么直接返回值。
                如果是红黑树那就按照树的方式获取值。
                就不满足那就按照链表的方式遍历获取值。
                
            1.8 在 1.7 的数据结构上做了大的改动，采用红黑树之后可以保证查询效率（O(logn)），甚至取消了 ReentrantLock 改为了 
            synchronized，这样可以看出在新版的 JDK 中对 synchronized 优化是很到位的。
                
                
                
      
## 1.java内存模型

        https://blog.csdn.net/tjiyu/article/details/53915869
        
        浅析java内存模型--JMM(Java Memory Model)
        https://www.cnblogs.com/lewis0077/p/5143268.html    (比较完善)
        
        *主内存和工作内存
        *原子性,可见性,顺序性,happens-before
        *Java的内存结构，也就是运行时的数据区域
            1/PC寄存器/程序计数器   (私有)
                严格来说是一个数据结构，用于保存当前正在执行的程序的内存地址，由于Java是支持多线程执行的，所以程序执行的轨迹不可能一直
                都是线性执行。当有多个线程交叉执行时，被中断的线程的程序当前执行到哪条内存地址必然要保存下来，以便用于被中断的线程恢复
                执行时再按照被中断时的指令地址继续执行下去。为了线程切换后能恢复到正确的执行位置，每个线程都需要有一个独立的程序计数器，
                各个线程之间计数器互不影响，独立存储，我们称这类内存区域为“线程私有”的内存,这在某种程度上有点类似于“ThreadLocal”，
                是线程安全的。

            2/Java栈 Java Stack  (私有)
                由于Java栈是与线程对应起来的，Java栈数据不是线程共有的，所以不需要关心其数据一致性，也不会存在同步锁的问题。
            3/堆 Heap     (共享)
                堆是JVM所管理的内存中国最大的一块，是被所有Java线程锁共享的，不是线程安全的，在JVM启动时创建
            4/方法区Method Area    (共享)
                方法区是被Java线程锁共享的.不像Java堆中其他部分一样会频繁被GC回收，它存储的信息相对比较稳定，在一定条件下会被GC，
                当方法区要使用的内存超过其允许的大小时，会抛出OutOfMemory的错误信息。方法区也是堆中的一部分，就是我们通常所说的
                Java堆中的永久区 Permanet Generation，大小可以通过参数来设置,可以通过-XX:PermSize指定初始值，
                -XX:MaxPermSize指定最大值。
            5/常量池Constant Pool  (共享)
                常量池本身是方法区中的一个数据结构。常量池中存储了如字符串、final变量值、类名和方法名常量。常量池在编译期间就被确定，
                并保存在已编译的.class文件中。一般分为两类：字面量和应用量。字面量就是字符串、final变量等。类名和方法名属于引用量。
                引用量最常见的是在调用方法的时候，根据方法名找到方法的引用，并以此定为到函数体进行函数代码的执行。引用量包含：类和接口
                的权限定名、字段的名称和描述符，方法的名称和描述符。
            6/本地方法栈Native Method Stack  (native方法使用)
                本地方法栈和Java栈所发挥的作用非常相似，区别不过是Java栈为JVM执行Java方法服务，而本地方法栈为JVM执行
                Native方法服务。本地方法栈也会抛出StackOverflowError和OutOfMemoryError异常。
                


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
         
     Tomcat 类加载器之为何违背双亲委派模型
        https://blog.csdn.net/dangwanma6489/article/details/80244981
        tomcat 违背了java 推荐的双亲委派模型了吗？
        答案是：违背了。 我们前面说过：
        双亲委派模型要求除了顶层的启动类加载器之外，其余的类加载器都应当由自己的父类加载器加载。
        很显然，tomcat 不是这样实现，tomcat 为了实现隔离性，没有遵守这个约定，每个webappClassLoader加载自己的目录下的class文件，不会传递给父类加载器。
        我们扩展出一个问题：如果tomcat 的 Common ClassLoader 想加载 WebApp ClassLoader 中的类，该怎么办？
        看了前面的关于破坏双亲委派模型的内容，我们心里有数了，我们可以使用线程上下文类加载器实现，
        使用线程上下文加载器，可以让父类加载器请求子类加载器去完成类加载的动作。
        
     
       

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
     https://blog.csdn.net/sinat_35512245/article/details/59056120  (常用题)
     
     对于CountDownLatch来说，重点是“一个线程（多个线程）等待”，而其他的N个线程在完成“某件事情”之后，可以终止，
     也可以等待。而对于CyclicBarrier，重点是多个线程，在任意一个线程没有完成，所有的线程都必须等待。
     CountDownLatch是计数器，线程完成一个记录一个，只不过计数不是递增而是递减，而CyclicBarrier更像是一个阀门，需要所有线程都到达，
     阀门才能打开，然后继续执行。
     
     AQS的全称为（AbstractQueuedSynchronizer）
        https://blog.csdn.net/zhangdong2012/article/details/79983404    (写得比较明白)
         AQS核心思想是，如果被请求的共享资源空闲，则将当前请求资源的线程设置为有效的工作线程，并且将共享资源设置为锁定状态。
         如果被请求的共享资源被占用，那么就需要一套线程阻塞等待以及被唤醒时锁分配的机制，这个机制AQS是用CLH队列锁实现的，
         即将暂时获取不到锁的线程加入到队列中。
         CLH(Craig,Landin,and Hagersten)队列是一个虚拟的双向队列（虚拟的双向队列即不存在队列实例，仅存在结点之间的关联关系）。
         AQS是将每条请求共享资源的线程封装成一个CLH锁队列的一个结点（Node）来实现锁的分配。
     
        什么是AQS
        AQS是AbustactQueuedSynchronizer的简称，它是一个Java提高的底层同步工具类，用一个int类型的变量表示同步状态，并提供了一系列的CAS操作来管理这个同步状态。AQS的主要作用是为Java中的并发同步组件提供统一的底层支持，例如ReentrantLock，CountdowLatch就是基于AQS实现的，用法是通过继承AQS实现其模版方法，然后将子类作为同步组件的内部类。
        
        同步队列
        同步队列是AQS很重要的组成部分，它是一个双端队列，遵循FIFO原则，主要作用是用来存放在锁上阻塞的线程，当一个线程尝试获取锁时，如果已经被占用，那么当前线程就会被构造成一个Node节点假如到同步队列的尾部，队列的头节点是成功获取锁的节点，当头节点线程是否锁时，会唤醒后面的节点并释放当前头节点的引用。 
        
        独占锁和共享锁在实现上的区别
            独占锁的同步状态值为1，即同一时刻只能有一个线程成功获取同步状态
            共享锁的同步状态>1，取值由上层同步组件确定
            独占锁队列中头节点运行完成后释放它的直接后继节点
            共享锁队列中头节点运行完成后释放它后面的所有节点
            共享锁中会出现多个线程（即同步队列中的节点）同时成功获取同步状态的情况
        
        重入锁的最主要逻辑就锁判断上次获取锁的线程是否为当前线程。
        
        非公平锁
            非公平锁是指当锁状态为可用时，不管在当前锁上是否有其他线程在等待，新近线程都有机会抢占锁。
            上述代码即为非公平锁和核心实现，可以看到只要同步状态为0，任何调用lock的线程都有可能获取到锁，而不是按照锁请求的FIFO原则来进行的。
        公平锁
            从上面的代码中可以看出，公平锁与非公平锁的区别仅在于是否判断当前节点是否存在前驱节点!hasQueuedPredecessors() &&，由AQS可知，如果当前线程获取锁失败就会被加入到AQS同步队列中，那么，如果同步队列中的节点存在前驱节点，也就表明存在线程比当前节点线程更早的获取锁，故只有等待前面的线程释放锁后才能获取锁。

        
        
     
     *ReentrantLock 实现原理
     https://crossoverjie.top/%2F2018%2F01%2F25%2FReentrantLock%2F


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
     用AtomicStampedReference解决
     https://www.cnblogs.com/java20130722/p/3206742.html
     
     
     
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
        
        LongAdder ：并发场景下写性能优秀，读性能由于组合求值的原因，不如直接读值的方案，但由于计数器场景写多读少的缘故，
        整体性能在几个方案中最优，是高性能计数器的首选方案。由于 Cells 数组以及缓存行填充的缘故，占用内存较大。
        
        ConcurrentAutoTable ：拥有和 LongAdder 相近的写入性能，读性能则更加不如 LongAdder。它的使用需要引入 JCTools 依赖，相比 Jdk 自带的 LongAdder 并没有优势。但额外说明一点，
        ConcurrentAutoTable 的使用并非局限于计数器场景，其仍然存在很大的价值。
    
        LongAdder newCounter = new LongAdder();
        newCounter.increment();
        System.out.println(newCounter.sum());
    
     CountDownLatch实现原理
     https://cloud.tencent.com/developer/article/1038486
    
     fail-fast：
        机制是java集合(Collection)中的一种错误机制。当多个线程对同一个集合的内容进行操作时，就可能会产生fail-fast事件。 
        例如：当某一个线程A通过iterator去遍历某集合的过程中，若该集合的内容被其他线程所改变了；那么线程A访问集合时，就会抛出ConcurrentModificationException异常，产生fail-fast事件
    
     happens-before:如果两个操作之间具有happens-before 关系，那么前一个操作的结果就会对后面一个操作可见。 
         1.程序顺序规则：一个线程中的每个操作，happens- before 于该线程中的任意后续操作。 
         2.监视器锁规则：对一个监视器锁的解锁，happens- before 于随后对这个监视器锁的加锁。 
         3.volatile变量规则：对一个volatile域的写，happens- before于任意后续对这个volatile域的读。 
         4.传递性：如果A happens- before B，且B happens- before C，那么A happens- before C。 
         5.线程启动规则：Thread对象的start()方法happens- before于此线程的每一个动作。

     Volatile和Synchronized四个不同点： 
         1 粒度不同，前者针对变量 ，后者锁对象和类 
         2 syn阻塞，volatile线程不阻塞 
         3 syn保证三大特性，volatile不保证原子性 
         4 syn编译器优化，volatile不优化 
         volatile具备两种特性： 
         1. 保证此变量对所有线程的可见性，指一条线程修改了这个变量的值，新值对于其他线程来说是可见的，但并不是多线程安全的。 
         2. 禁止指令重排序优化。 
         Volatile如何保证内存可见性: 
         1.当写一个volatile变量时，JMM会把该线程对应的本地内存中的共享变量刷新到主内存。 
         2.当读一个volatile变量时，JMM会把该线程对应的本地内存置为无效。线程接下来将从主内存中读取共享变量。
         
         同步：就是一个任务的完成需要依赖另外一个任务，只有等待被依赖的任务完成后，依赖任务才能完成。 
         异步：不需要等待被依赖的任务完成，只是通知被依赖的任务要完成什么工作，只要自己任务完成了就算完成了，
         被依赖的任务是否完成会通知回来。（异步的特点就是通知）。 
         打电话和发短信来比喻同步和异步操作。 
         阻塞：CPU停下来等一个慢的操作完成以后，才会接着完成其他的工作。 
         非阻塞：非阻塞就是在这个慢的执行时，CPU去做其他工作，等这个慢的完成后，CPU才会接着完成后续的操作。 
         非阻塞会造成线程切换增加，增加CPU的使用时间能不能补偿系统的切换成本需要考虑。
     
     
     CAS（Compare And Swap） 无锁算法： 
        CAS是乐观锁技术，当多个线程尝试使用CAS同时更新同一个变量时，只有其中一个线程能更新变量的值，而其它线程都失败，失败的线程并不会被挂起，
        而是被告知这次竞争中失败，并可以再次尝试。CAS有3个操作数，内存值V，旧的预期值A，要修改的新值B。当且仅当预期值A和内存值V相同时，
        将内存值V修改为B，否则什么都不做。

     https://www.cnblogs.com/lzh-blogs/p/7477157.html   (比较底层,自旋锁)
     理解锁的基础知识
        基础知识之一：锁的类型
            乐观锁
                CAS
            悲观锁
                悲观锁是就是悲观思想，即认为写多，遇到并发写的可能性高，每次去拿数据的时候都认为别人会修改，所以每次在读写数据的时候
                都会上锁，这样别人想读写这个数据就会block直到拿到锁。java中的悲观锁就是Synchronized,AQS框架下的锁则是先尝试cas
                乐观锁去获取锁，获取不到，才会转换为悲观锁，如RetreenLock。

        基础知识之二：java线程阻塞的代价
            java的线程是映射到操作系统原生线程之上的，如果要阻塞或唤醒一个线程就需要操作系统介入，需要在户态与核心态之间切换，这种切换
            会消耗大量的系统资源，因为用户态与内核态都有各自专用的内存空间，专用的寄存器等，用户态切换至内核态需要传递给许多变量、参数
            给内核，内核也需要保护好用户态在切换时的一些寄存器值、变量等，以便内核态调用结束后切换回用户态继续工作。
            
            如果线程状态切换是一个高频操作时，这将会消耗很多CPU处理时间；
            如果对于那些需要同步的简单的代码块，获取锁挂起操作消耗的时间比用户代码执行的时间还要长，这种同步策略显然非常糟糕的。
            synchronized会导致争用不到锁的线程进入阻塞状态，所以说它是java语言中一个重量级的同步操纵，被称为重量级锁，为了缓解上述性能问题，JVM从1.5开始，引入了轻量锁与偏向锁，默认启用了自旋锁，他们都属于乐观锁。
            
            明确java线程切换的代价，是理解java中各种锁的优缺点的基础之一
        
        *java中的锁
            自旋锁
                自旋锁原理非常简单，如果持有锁的线程能在很短时间内释放锁资源，那么那些等待竞争锁的线程就不需要做内核态和用户态之间的切换进入阻塞挂起状态，它们只需要等一等（自旋），等持有锁的线程释放锁后即可立即获取锁，这样就避免用户线程和内核的切换的消耗。
                但是线程自旋是需要消耗cup的，说白了就是让cup在做无用功，线程不能一直占用cup自旋做无用功，所以需要设定一个自旋等待的最大时间。
                如果持有锁的线程执行的时间超过自旋等待的最大时间扔没有释放锁，就会导致其它争用锁的线程在最大等待时间内还是获取不到锁，这时争用线程会停止自旋进入阻塞状态。
            
            自旋锁的优缺点
                自旋锁尽可能的减少线程的阻塞，这对于锁的竞争不激烈，且占用锁时间非常短的代码块来说性能能大幅度的提升，因为自旋的消耗会小于线程阻塞挂起操作的消耗！
                但是如果锁的竞争激烈，或者持有锁的线程需要长时间占用锁执行同步块，这时候就不适合使用自旋锁了，因为自旋锁在获取锁前一直都是占用cpu做无用功，占着XX不XX，线程自旋的消耗大于线程阻塞挂起操作的消耗，其它需要cup的线程又不能获取到cpu，造成cpu的浪费。
            
            自旋锁时间阈值
                自旋锁的目的是为了占着CPU的资源不释放，等到获取到锁立即进行处理。但是如何去选择自旋的执行时间呢？如果自旋执行时间太长，会有大量的线程处于自旋状态占用CPU资源，进而会影响整体系统的性能。因此自旋的周期选的额外重要！
                JVM对于自旋周期的选择，jdk1.5这个限度是一定的写死的，在1.6引入了适应性自旋锁，适应性自旋锁意味着自旋的时间不在是固定的了，而是由前一次在同一个锁上的自旋时间以及锁的拥有者的状态来决定，基本认为一个线程上下文切换的时间是最佳的一个时间，同时JVM还针对当前CPU的负荷情况做了较多的优化
                    如果平均负载小于CPUs则一直自旋
                    如果有超过(CPUs/2)个线程正在自旋，则后来线程直接阻塞
                    如果正在自旋的线程发现Owner发生了变化则延迟自旋时间（自旋计数）或进入阻塞
                    如果CPU处于节电模式则停止自旋
                    自旋时间的最坏情况是CPU的存储延迟（CPU A存储了一个数据，到CPU B得知这个数据直接的时间差）
                    自旋时会适当放弃线程优先级之间的差异
            
            自旋锁的开启
                JDK1.6中-XX:+UseSpinning开启； 
                JDK1.7后，去掉此参数，由jvm控制；
                
        Synchronized的作用
            在JDK1.5之前都是使用synchronized关键字保证同步的，Synchronized的作用相信大家都已经非常熟悉了；
            
            它可以把任意一个非NULL的对象当作锁。
            
            作用于方法时，锁住的是对象的实例(this)；
            当作用于静态方法时，锁住的是Class实例，又因为Class的相关数据存储在永久带PermGen（jdk1.8则是metaspace），永久带是全局共享的，因此静态方法锁相当于类的一个全局锁，会锁所有调用该方法的线程；
            synchronized作用于一个对象实例时，锁住的是所有以该对象为锁的代码块。
            Synchronized的实现

            它有多个队列，当多个线程一起访问某个对象监视器的时候，对象监视器会将这些线程存储在不同的容器中。
            Contention List：竞争队列，所有请求锁的线程首先被放在这个竞争队列中；
            Entry List：Contention List中那些有资格成为候选资源的线程被移动到Entry List中；
            Wait Set：哪些调用wait方法被阻塞的线程被放置在这里；
            OnDeck：任意时刻，最多只有一个线程正在竞争锁资源，该线程被成为OnDeck；
            Owner：当前已经获取到所资源的线程被称为Owner；
            !Owner：当前释放锁的线程。
            JVM每次从队列的尾部取出一个数据用于锁竞争候选者（OnDeck），但是并发情况下，ContentionList会被大量的并发线程进行CAS访问，为了降低对尾部元素的竞争，JVM会将一部分线程移动到EntryList中作为候选竞争线程。Owner线程会在unlock时，将ContentionList中的部分线程迁移到EntryList中，并指定EntryList中的某个线程为OnDeck线程（一般是最先进去的那个线程）。Owner线程并不直接把锁传递给OnDeck线程，而是把锁竞争的权利交给OnDeck，OnDeck需要重新竞争锁。这样虽然牺牲了一些公平性，但是能极大的提升系统的吞吐量，在JVM中，也把这种选择行为称之为“竞争切换”。
            OnDeck线程获取到锁资源后会变为Owner线程，而没有得到锁资源的仍然停留在EntryList中。如果Owner线程被wait方法阻塞，则转移到WaitSet队列中，直到某个时刻通过notify或者notifyAll唤醒，会重新进去EntryList中。
            处于ContentionList、EntryList、WaitSet中的线程都处于阻塞状态，该阻塞是由操作系统来完成的（Linux内核下采用pthread_mutex_lock内核函数实现的）。
            *Synchronized是非公平锁。 Synchronized在线程进入ContentionList时，等待的线程会先尝试自旋获取锁，如果获取不到就进入ContentionList，这明显对于已经进入队列的线程是不公平的，还有一个不公平的事情就是自旋获取锁的线程还可能直接抢占OnDeck线程的锁资源。
            
        偏向锁
            偏向锁获取过程：
            访问Mark Word中偏向锁的标识是否设置成1，锁标志位是否为01，确认为可偏向状态。
            如果为可偏向状态，则测试线程ID是否指向当前线程，如果是，进入步骤5，否则进入步骤3。
            如果线程ID并未指向当前线程，则通过CAS操作竞争锁。如果竞争成功，则将Mark Word中线程ID设置为当前线程ID，然后执行5；如果竞争失败，执行4。
            如果CAS获取偏向锁失败，则表示有竞争。当到达全局安全点（safepoint）时获得偏向锁的线程被挂起，偏向锁升级为轻量级锁，然后被阻塞在安全点的线程继续往下执行同步代码。（撤销偏向锁的时候会导致stop the word）
            执行同步代码。
            
            注意：第四步中到达安全点safepoint会导致stop the word，时间很短。
            
        偏向锁的释放：
            偏向锁的撤销在上述第四步骤中有提到。偏向锁只有遇到其他线程尝试竞争偏向锁时，持有偏向锁的线程才会释放锁，线程不会主动去释放偏向锁。偏向锁的撤销，需要等待全局安全点（在这个时间点上没有字节码正在执行），它会首先暂停拥有偏向锁的线程，判断锁对象是否处于被锁定状态，撤销偏向锁后恢复到未锁定（标志位为“01”）或轻量级锁（标志位为“00”）的状态。
            偏向锁的适用场景
            始终只有一个线程在执行同步块，在它没有执行完释放锁之前，没有其它线程去执行同步块，在锁无竞争的情况下使用，一旦有了竞争就升级为轻量级锁，升级为轻量级锁的时候需要撤销偏向锁，撤销偏向锁的时候会导致stop the word操作； 
            在有锁的竞争时，偏向锁会多做很多额外操作，尤其是撤销偏向所的时候会导致进入安全点，安全点会导致stw，导致性能下降，这种情况下应当禁用；
     轻量级锁
        轻量级锁是由偏向所升级来的，偏向锁运行在一个线程进入同步块的情况下，当第二个线程加入锁争用的时候，偏向锁就会升级为轻量级锁； 
        轻量级锁的加锁过程： 
     
        偏向锁,轻量锁,自旋锁总结
            上面几种锁都是JVM自己内部实现，当我们执行synchronized同步块的时候jvm会根据启用的锁和当前线程的争用情况，决定如何执行同步操作；
            在所有的锁都启用的情况下线程进入临界区时会先去获取偏向锁，如果已经存在偏向锁了，则会尝试获取轻量级锁，如果以上两种都失败，则启用自旋锁，如果自旋也没有获取到锁，则使用重量级锁，没有获取到锁的线程阻塞挂起，直到持有锁的线程执行完同步块唤醒他们；
            偏向锁是在无锁争用的情况下使用的，也就是同步开在当前线程没有执行完之前，没有其它线程会执行该同步快，一旦有了第二个线程的争用，偏向锁就会升级为轻量级锁，一点有两个以上线程争用，就会升级为重量级锁；
            
            如果线程争用激烈，那么应该禁用偏向锁。

    

    
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
    
## 10.网络IO/j2ee等基础

    位 bit 
    字节 byte 
    字 word 
    
    1字=2字节(1 word = 2 byte) 
    1字节=8位(1 byte = 8bit) 
     
    一个字的字长为16 
    一个字节的字长是8
    
    bps 是 bits per second 的简称。一般数据机及网络通讯的传输速率都是以「bps」为单位。如56Kbps、100.0Mbps 等等。 
    Bps即是Byte per second 的简称。而电脑一般都以Bps 显示速度，如1Mbps 大约等同 128 KBps。 
    bit 电脑记忆体中最小的单位，在二进位电脑系统中，每一bit 可以代表0 或 1 的数位讯号。 
    Byte一个Byte由8 bits 所组成，可代表一个字元(A~Z)、数字(0~9)、或符号(,.?!%&+-*/)，是记忆体储存资料的基本单位，
    至於每个中文字则须要两Bytes。当记忆体容量过大时，位元组这个单位就不够用，因此就有千位元组的单位KB出现，
    以下乃个记忆体计算单位之间的相关性：

    一个Http请求 
        DNS域名解析 –> 发起TCP的三次握手 –> 建立TCP连接后发起http请求 –> 服务器响应http请求，浏览器得到html代码 
        –> 浏览器解析html代码，并请求html代码中的资源（如javascript、css、图片等） –> 浏览器对页面进行渲染呈现给用户
        
        设计存储海量数据的存储系统：设计一个叫“中间层”的一个逻辑层，在这个层，将数据库的海量数据抓出来，做成缓存，运行在服务器的内存中，
        同理，当有新的数据到来，也先做成缓存，再想办法，持久化到数据库中，这是一个简单的思路。主要的步骤是负载均衡，
        将不同用户的请求分发到不同的处理节点上，然后先存入缓存，定时向主数据库更新数据。读写的过程采用类似乐观锁的机制，可以一直读
        （在写数据的时候也可以），但是每次读的时候会有个版本的标记，如果本次读的版本低于缓存的版本，会重新读数据，这样的情况并不多，可以忍受。
    

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
    
    分布式Session框架 
        1. 配置服务器，Zookeeper集群管理服务器可以统一管理所有服务器的配置文件 
        2. 共享这些Session存储在一个分布式缓存中，可以随时写入和读取，而且性能要很好，如Memcache，Tair。 
        3. 封装一个类继承自HttpSession，将Session存入到这个类中然后再存入分布式缓存中 
        4. 由于Cookie不能跨域访问，要实现Session同步，要同步SessionID写到不同域名下。

    字节流与字符流
        https://www.cnblogs.com/DONGb/p/7844123.html
        
    【问题1】为什么连接的时候是三次握手，关闭的时候却是四次握手？
        答：因为当Server端收到Client端的SYN连接请求报文后，可以直接发送SYN+ACK报文。其中ACK报文是用来应答的，SYN报文是用来同步的。
        但是关闭连接时，当Server端收到FIN报文时，很可能并不会立即关闭SOCKET，所以只能先回复一个ACK报文，告诉Client端，"你发的FIN
        报文我收到了"。只有等到我Server端所有的报文都发送完了，我才能发送FIN报文，因此不能一起发送。故需要四步握手。
    
    【问题2】为什么TIME_WAIT状态需要经过2MSL(最大报文段生存时间)才能返回到CLOSE状态？
        答：虽然按道理，四个报文都发送完毕，我们可以直接进入CLOSE状态了，但是我们必须假象网络是不可靠的，有可以最后一个ACK丢失。
        所以TIME_WAIT状态就是用来重发可能丢失的ACK报文。在Client发送出最后的ACK回复，但该ACK可能丢失。Server如果没有收到ACK，
        将不断重复发送FIN片段。所以Client不能立即关闭，它必须确认Server接收到了该ACK。Client会在发送出ACK之后进入到TIME_WAIT状态。
        Client会设置一个计时器，等待2MSL的时间。如果在该时间内再次收到FIN，那么Client会重发ACK并再次等待2MSL。所谓的2MSL是两倍的MSL
        (Maximum Segment Lifetime)。MSL指一个片段在网络中最大的存活时间，2MSL就是一个发送和一个回复所需的最大时间。如果直到2MSL，
        Client都没有再次收到FIN，那么Client推断ACK已经被成功接收，则结束TCP连接。
    
    【问题3】如果已经建立了连接，但是客户端突然出现故障了怎么办？
        TCP还设有一个保活计时器，显然，客户端如果出现故障，服务器不能一直等下去，白白浪费资源。服务器每收到一次客户端的请求后都会
        重新复位这个计时器，时间通常是设置为2小时，若两小时还没有收到客户端的任何数据，服务器就会发送一个探测报文段，以后每隔75分钟
        发送一次。若一连发送10个探测报文仍然没反应，服务器就认为客户端出了故障，接着就关闭连接。
    
    
    
    

    
## 11.java IO NIO

    https://mp.weixin.qq.com/s?__biz=MzU4NDQ4MzU5OA==&mid=2247483956&idx=1&sn=57692bc5b7c2c6dfb812489baadc29c9&chksm=fd985455caefdd4331d828d8e89b22f19b304aa87d6da73c5d8c66fcef16e4c0b448b1a6f791&scene=21#wechat_redirect
    
    比较完整
    https://github.com/Snailclimb/JavaGuide/blob/master/Java%E7%9B%B8%E5%85%B3/Java%20IO%E4%B8%8ENIO.md#%E4%B8%80-java-io%EF%BC%8C%E7%A1%AC%E9%AA%A8%E5%A4%B4%E4%B9%9F%E8%83%BD%E5%8F%98%E8%BD%AF
    代码demo
    https://github.com/wanwanpp/netty-demo

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
    
    Spring支持三种依赖注入方式，
        分别是属性（Setter方法）注入，构造注入和接口注入。
    
    在Spring中，那些组成应用的主体及由Spring IOC容器所管理的对象被称之为Bean。
    
    Spring的IOC容器通过反射的机制实例化Bean并建立Bean之间的依赖关系。 
    简单地讲，Bean就是由Spring IOC容器初始化、装配及被管理的对象。 
    获取Bean对象的过程，首先通过Resource加载配置文件并启动IOC容器，然后通过getBean方法获取bean对象，就可以调用他的方法。 
    Spring Bean的作用域： 
    Singleton：Spring IOC容器中只有一个共享的Bean实例，一般都是Singleton作用域。 
    Prototype：每一个请求，会产生一个新的Bean实例。 
    Request：每一次http请求会产生一个新的Bean实例。



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
        
        代理的共有优点：业务类只需要关注业务逻辑本身，保证了业务类的重用性。 
            Java静态代理： 
            代理对象和目标对象实现了相同的接口，目标对象作为代理对象的一个属性，具体接口实现中，代理对象可以在调用目标对象相应方法前后加上其他业务处理逻辑。 
            缺点：一个代理类只能代理一个业务类。如果业务类增加方法时，相应的代理类也要增加方法。 
            Java动态代理： 
            Java动态代理是写一个类实现InvocationHandler接口，重写Invoke方法，在Invoke方法可以进行增强处理的逻辑的编写，
            这个公共代理类在运行的时候才能明确自己要代理的对象，同时可以实现该被代理类的方法的实现，然后在实现类方法的时候可以进行增强处理。 
            实际上：代理对象的方法 = 增强处理 + 被代理对象的方法
        
        *JDK和CGLIB生成动态代理类的区别： 
            1.JDK动态代理只能针对实现了接口的类生成代理（实例化一个类）。此时代理对象和目标对象实现了相同的接口，目标对象作为代理对象的一个属性，
            具体接口实现中，可以在调用目标对象相应方法前后加上其他业务处理逻辑 
            2.CGLIB是针对类实现代理，主要是对指定的类生成一个子类（没有实例化一个类），覆盖其中的方法 。 
            
            Spring AOP应用场景 
            性能检测，访问控制，日志管理，事务等。 
            默认的策略是如果目标类实现接口，则使用JDK动态代理技术，如果目标对象没有实现接口，则默认会采用CGLIB代理
        
        

                
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
        14.消息中间件如何实现每秒几十万的高并发写入？    https://juejin.im/post/5c7bd09b6fb9a049ba424c15
        
        
        
        
    
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
        14.如何设计可以动态扩容缩容的分库分表方案？
        
        
             
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

        guava cache
            有两种方法可以实现"如果有缓存则返回；否则运算、缓存、然后返回"。
                CacheLoader
                Callable：所有类型的Guava Cache，不管有没有自动加载功能，都支持get(K, Callable<V>方法。

            *内存一直被占用？可怕：缓存回收
                上面的简单例子中，内存一直会被占用，这是不合理的，我们需要按照一定的规则将缓存清除，释放内存，防止发生OOM。
                guava提供了三种缓存回收的策略：基于容量回收、定时回收和基于引用回收。
                
                基于容量的回收:
                    规定缓存项的数目不超过固定值，只需使用CacheBuilder.maximumSize(long)。缓存将尝试回收最近没有使用或总体上很少
                    使用的缓存项。——警告：在缓存项的数目达到限定值之前，即缓存项的数目逼近限定值时缓存就可能进行回收操作。这个size指
                    的是cache中的条目数，不是内存大小或是其他.
                    
                    定一个权重函数，并且用CacheBuilder.maximumWeight(long)指定最大总重。
                    缓存回收也是在重量逼近限定值时就进行了，还要知道重量是在缓存创建时计算的，因此要考虑重量计算的复杂度。
        
                    CacheBuilder.maximumSize(long)，CacheBuilder.maximumWeight(long)是互斥的，只能二选一。
                    
                    CacheBuilder.maximumSize(long)中如果每个条目占用内存空间都是相同的，就等价于限制了缓存空间的总大小；
                    如果每个缓存条目大小不定，那么就没有办法限制总的内存大小。
                    CacheBuilder.maximumWeight(long)可以用来控制内存。比如我们将总权重设置为1G(代表内存空间大小)，
                    而每个缓存条目的权重都是缓存值实际占用的内存空间大小
                    
                基于时间的回收
                    guava 提供两种定时回收的方法
                    expireAfterAccess(long, TimeUnit):缓存项在给定时间内没有被读/写访问，则回收。请注意这种缓存的回收顺序
                    和基于大小回收一样。
                    expireAfterWrite(long, TimeUnit):缓存项在给定时间内没有被写访问（创建或覆盖），则回收。如果认为缓存数据
                    总是在固定时候后变得陈旧不可用，这种回收方式是可取的。
                    
                基于引用的回收
                    手动清除缓存
                    任何时候，你都可以显式地清除缓存项，而不是等到它被回收：
                    个别清除：Cache.invalidate(key)
                    批量清除：Cache.invalidateAll(keys)
                    清除所有缓存项：Cache.invalidateAll()
                
            *监听器
                .removalListener(new RemovalListener<Object, Object>() {
                            @Override
                            public void onRemoval(RemovalNotification<Object, Object> notification) {
                                System.out.println("remove key[" + notification.getKey() + "],value[" + notification.getValue() + "],remove reason[" + notification.getCause() + "]");
                            }
                        })
                        
            统计
                CacheBuilder.recordStats()用来开启Guava Cache的统计功能。统计打开后，Cache.stats()方法会返回对象以提供如下统计信息：
                hitRate()：缓存命中率；
                averageLoadPenalty()：加载新值的平均时间，单位为纳秒；
                evictionCount()：缓存项被回收的总数，不包括显式清除
                统计信息对于调整缓存设置是至关重要的，在性能要求高的应用中我们建议密切关注这些数据。
                其他
                asMap()方法获得缓存数据的ConcurrentMap<K, V>快照
                cleanUp()清空缓存
            
            *原理
                JVM 缓存
                    首先是 JVM 缓存，也可以认为是堆缓存。
                    其实就是创建一些全局变量，如 Map、List 之类的容器用于存放数据。
                    这样的优势是使用简单但是也有以下问题：
                        只能显式的写入，清除数据。
                        不能按照一定的规则淘汰数据，如 LRU，LFU，FIFO 等。
                        清除数据时的回调通知。
                        其他一些定制功能等。
                
                Ehcache、Guava Cache
                    所以出现了一些专门用作 JVM 缓存的开源工具出现了，如本文提到的 Guava Cache。
                    它具有上文 JVM 缓存不具有的功能，如自动清除数据、多种清除算法、清除回调等。
                    但也正因为有了这些功能，这样的缓存必然会多出许多东西需要额外维护，自然也就增加了系统的消耗。
                
                Guava Cache 其核心数据结构大体上和 ConcurrentHashMap 一致，具体细节上会有些区别。功能上，ConcurrentMap会一直
                保存所有添加的元素，直到显式地移除。相对地， Guava Cache 为了限制内存占用，通常都设定为自动回收元素。在某些场景下，
                尽管它不回收元素，也是很有用的，因为它会自动加载缓存。
                
                Guava Cache的实现,核心数据结构和算法都是和JDK 1.6版本的 ConcurrentHashMap 一致.因此,如果你熟悉
                ConcurrentHashMap实现原理,对Cache是很容易明白的.
                
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
        
        
        
        
        
##18.dubbo
    http://dubbo.apache.org/zh-cn/docs/user/demos/thread-model.html (必读)


    服务治理和配置管理
        服务治理
            服务治理主要作用是改变运行时服务的行为和选址逻辑，达到限流，权重配置等目的，主要有以下几个功能：
            
            应用级别的服务治理
                此Dubbo2.7版本中增加了应用粒度的服务治理操作，对于条件路由(包括黑白名单)，动态配置(包括权重，负载均衡)都可以做应用级别的配置：
                上图是条件路由的配置，可以按照应用名，服务名两个维度来填写，也可以按照这两个维度来查询。
                
            标签路由
                路由方法包括:条件路由和标签路由
                调用的时候，客户端可以通过setAttachment的方式，来设置不同的标签名称，比如本例中，setAttachment(tag1)，
                客户端的选址范围就在如图所示的三台机器中，可以通过这种方式来实现流量隔离，灰度发布等功能。
                
            黑白名单
                黑白名单是条件路由的一部分，规则存储和条件路由放在一起，为了方便配置所以单独拿出来，同样可以通过服务和应用两个维度，指定黑名单和白名单
                
            动态配置
                动态配置是和路由规则平行的另一类服务治理治理功能，主要作用是在不重启服务的情况下，动态改变调用行为，从Dubbo2.7版本开始，
                支持服务和应用两个维度的配置，采用yaml格式
                
            权重调节
                权重调节是动态配置的子功能，主要作用是改变服务端的权重，更大的权重会有更大的几率被客户端选中作为服务提供者，
                从而达到流量分配的目的
                
    dubbo流程
        
        启动时检查
            Dubbo 缺省会在启动时检查依赖的服务是否可用，不可用时会抛出异常，阻止 Spring 初始化完成，以便上线时，能及早发现问题，
            默认 check="true"。
            可以通过 check="false" 关闭检查，比如，测试时，有些服务不关心，或者出现了循环依赖，必须有一方先启动。
            另外，如果你的 Spring 容器是懒加载的，或者通过 API 编程延迟引用服务，请关闭 check，否则服务临时不可用时，
            会抛出异常，拿到 null 引用，如果 check="false"，总是会返回引用，当服务恢复时，能自动连上。

        *集群容错
            在集群调用失败时，Dubbo 提供了多种容错方案，缺省为 failover(故障转移) 重试。
            各节点关系：
            
            这里的 Invoker 是 Provider 的一个可调用 Service 的抽象，Invoker 封装了 Provider 地址及 Service 接口信息
            Directory 代表多个 Invoker，可以把它看成 List<Invoker> ，但与 List 不同的是，它的值可能是动态变化的，比如注册中心推送变更
            Cluster 将 Directory 中的多个 Invoker 伪装成一个 Invoker，对上层透明，伪装过程包含了容错逻辑，调用失败后，重试另一个
            Router 负责从多个 Invoker 中按路由规则选出子集，比如读写分离，应用隔离等
            LoadBalance 负责从多个 Invoker 中选出具体的一个用于本次调用，选的过程包含了负载均衡算法，调用失败后，需要重选
            
            集群容错模式
                Failover Cluster
                失败自动切换，当出现失败，重试其它服务器 [1]。通常用于读操作，但重试会带来更长延迟。可通过 retries="2" 来设置重试次数(不含第一次)。
                
                Failfast Cluster
                快速失败，只发起一次调用，失败立即报错。通常用于非幂等性的写操作，比如新增记录。
                
                Failsafe Cluster
                失败安全，出现异常时，直接忽略。通常用于写入审计日志等操作。
                
                Failback Cluster
                失败自动恢复，后台记录失败请求，定时重发。通常用于消息通知操作。
                
                Forking Cluster
                并行调用多个服务器，只要一个成功即返回。通常用于实时性要求较高的读操作，但需要浪费更多服务资源。可通过 forks="2" 来设置最大并行数。
                
                Broadcast Cluster
                广播调用所有提供者，逐个调用，任意一台报错则报错 [2]。通常用于通知所有提供者更新缓存或日志等本地资源信息。

            集群模式配置
                按照以下示例在服务提供方和消费方配置集群模式
                
                <dubbo:service cluster="failsafe" />
                或
                <dubbo:reference cluster="failsafe" />
        
        *负载均衡
            在集群负载均衡时，Dubbo 提供了多种均衡策略，缺省为 random 随机调用。
            负载均衡策略
                Random LoadBalance
                    随机，按权重设置随机概率。
                    在一个截面上碰撞的概率高，但调用量越大分布越均匀，而且按概率使用权重后也比较均匀，有利于动态调整提供者权重。
                RoundRobin LoadBalance
                    轮询，按公约后的权重设置轮询比率。
                    存在慢的提供者累积请求的问题，比如：第二台机器很慢，但没挂，当请求调到第二台时就卡在那，久而久之，所有请求都卡在调到第二台上。
                LeastActive LoadBalance
                    最少活跃调用数，相同活跃数的随机，活跃数指调用前后计数差。
                    使慢的提供者收到更少请求，因为越慢的提供者的调用前后计数差会越大。
                ConsistentHash LoadBalance
                    一致性 Hash，相同参数的请求总是发到同一提供者。
                    当某一台提供者挂时，原本发往该提供者的请求，基于虚拟节点，平摊到其它提供者，不会引起剧烈变动。
                    算法参见：http://en.wikipedia.org/wiki/Consistent_hashing
                    缺省只对第一个参数 Hash，如果要修改，请配置 <dubbo:parameter key="hash.arguments" value="0,1" />
                    缺省用 160 份虚拟节点，如果要修改，请配置 <dubbo:parameter key="hash.nodes" value="320" />

     
        线程模型
            如果事件处理的逻辑能迅速完成，并且不会发起新的 IO 请求，比如只是在内存中记个标识，则直接在 IO 线程上处理更快，因为减少了线程池调度。
            但如果事件处理逻辑较慢，或者需要发起新的 IO 请求，比如需要查询数据库，则必须派发到线程池，否则 IO 线程阻塞，将导致不能接收其它请求。
            如果用 IO 线程处理事件，又在事件处理过程中发起新的 IO 请求，比如在连接事件中发起登录请求，会报“可能引发死锁”异常，但不会真死锁。
            因此，需要通过不同的派发策略和不同的线程池配置的组合来应对不同的场景:
            
            <dubbo:protocol name="dubbo" dispatcher="all" threadpool="fixed" threads="100" />
            
            Dispatcher
                all 所有消息都派发到线程池，包括请求，响应，连接事件，断开事件，心跳等。
                direct 所有消息都不派发到线程池，全部在 IO 线程上直接执行。
                message 只有请求响应消息派发到线程池，其它连接断开事件，心跳等消息，直接在 IO 线程上执行。
                execution 只请求消息派发到线程池，不含响应，响应和其它连接断开事件，心跳等消息，直接在 IO 线程上执行。
                connection 在 IO 线程上，将连接断开事件放入队列，有序逐个执行，其它消息派发到线程池。
            
            ThreadPool
                fixed 固定大小线程池，启动时建立线程，不关闭，一直持有。(缺省)
                cached 缓存线程池，空闲一分钟自动删除，需要时重建。
                limited 可伸缩线程池，但池中的线程数只会增长不会收缩。只增长不收缩的目的是为了避免收缩时突然来了大流量引起的性能问题。
                eager 优先创建Worker线程池。在任务数量大于corePoolSize但是小于maximumPoolSize时，优先创建Worker来处理任务。
                当任务数量大于maximumPoolSize时，将任务放入阻塞队列中。阻塞队列充满时抛出RejectedExecutionException。
                (相比于cached:cached在任务数量超过maximumPoolSize时直接抛出异常而不是将任务放入阻塞队列)

            直连提供者
                在开发及测试环境下，经常需要绕过注册中心，只测试指定服务提供者，这时候可能需要点对点直连，点对点直连方式，
                将以服务接口为单位，忽略注册中心的提供者列表，A 接口配置点对点，不影响 B 接口从注册中心获取列表。
                如果是线上需求需要点对点，可在 <dubbo:reference> 中配置 url 指向提供者，将绕过注册中心，多个地址用分号隔开，配置如下
                <dubbo:reference id="xxxService" interface="com.alibaba.xxx.XxxService" url="dubbo://localhost:20890" />
                
            只订阅
                为方便开发测试，经常会在线下共用一个所有服务可用的注册中心，这时，如果一个正在开发中的服务提供者注册，可能会影响消费者不能正常运行。
                可以让服务提供者开发方，只订阅服务(开发的服务可能依赖其它服务)，而不注册正在开发的服务，通过直连测试正在开发的服务
                <dubbo:registry address="10.20.153.10:9090" register="false" />
                或者
                <dubbo:registry address="10.20.153.10:9090?register=false" />
                
            只注册
                如果有两个镜像环境，两个注册中心，有一个服务只在其中一个注册中心有部署，另一个注册中心还没来得及部署，而两个注册中心的其它应用都需要依赖此服务。这个时候，可以让服务提供者方只注册服务到另一注册中心，而不从另一注册中心订阅服务。
                禁用订阅配置
                
                <dubbo:registry id="hzRegistry" address="10.20.153.10:9090" />
                <dubbo:registry id="qdRegistry" address="10.20.141.150:9090" subscribe="false" />
                或者
                <dubbo:registry id="hzRegistry" address="10.20.153.10:9090" />
                <dubbo:registry id="qdRegistry" address="10.20.141.150:9090?subscribe=false" />
                
                
            静态服务
                有时候希望人工管理服务提供者的上线和下线，此时需将注册中心标识为非动态管理模式。
                <dubbo:registry address="10.20.141.150:9090" dynamic="false" />
                或者
                <dubbo:registry address="10.20.141.150:9090?dynamic=false" />
                服务提供者初次注册时为禁用状态，需人工启用。断线时，将不会被自动删除，需人工禁用。
            
            多协议
                Dubbo 允许配置多协议，在不同服务上支持不同协议或者同一服务上同时支持多种协议。
                
                不同服务不同协议
                不同服务在性能上适用不同协议进行传输，比如大数据用短连接协议，小数据大并发用长连接协议
                
                <?xml version="1.0" encoding="UTF-8"?>
                <beans xmlns="http://www.springframework.org/schema/beans"
                    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                    xmlns:dubbo="http://dubbo.apache.org/schema/dubbo"
                    xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-4.3.xsd http://dubbo.apache.org/schema/dubbo http://dubbo.apache.org/schema/dubbo/dubbo.xsd"> 
                    <dubbo:application name="world"  />
                    <dubbo:registry id="registry" address="10.20.141.150:9090" username="admin" password="hello1234" />
                    <!-- 多协议配置 -->
                    <dubbo:protocol name="dubbo" port="20880" />
                    <dubbo:protocol name="rmi" port="1099" />
                    <!-- 使用dubbo协议暴露服务 -->
                    <dubbo:service interface="com.alibaba.hello.api.HelloService" version="1.0.0" ref="helloService" protocol="dubbo" />
                    <!-- 使用rmi协议暴露服务 -->
                    <dubbo:service interface="com.alibaba.hello.api.DemoService" version="1.0.0" ref="demoService" protocol="rmi" /> 
                </beans>
                多协议暴露服务
                需要与 http 客户端互操作
                
                <?xml version="1.0" encoding="UTF-8"?>
                <beans xmlns="http://www.springframework.org/schema/beans"
                    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                    xmlns:dubbo="http://dubbo.apache.org/schema/dubbo"
                    xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-4.3.xsd http://dubbo.apache.org/schema/dubbo http://dubbo.apache.org/schema/dubbo/dubbo.xsd">
                    <dubbo:application name="world"  />
                    <dubbo:registry id="registry" address="10.20.141.150:9090" username="admin" password="hello1234" />
                    <!-- 多协议配置 -->
                    <dubbo:protocol name="dubbo" port="20880" />
                    <dubbo:protocol name="hessian" port="8080" />
                    <!-- 使用多个协议暴露服务 -->
                    <dubbo:service id="helloService" interface="com.alibaba.hello.api.HelloService" version="1.0.0" protocol="dubbo,hessian" />
                </beans>


            多注册中心
                Dubbo 支持同一服务向多注册中心同时注册，或者不同服务分别注册到不同的注册中心上去，甚至可以同时引用注册在不同注册中心上的同名服务。另外，注册中心是支持自定义扩展的 [1]。
            
            服务分组
                当一个接口有多种实现时，可以用 group 区分。
                
                服务
                <dubbo:service group="feedback" interface="com.xxx.IndexService" />
                <dubbo:service group="member" interface="com.xxx.IndexService" />
                引用
                <dubbo:reference id="feedbackIndexService" group="feedback" interface="com.xxx.IndexService" />
                <dubbo:reference id="memberIndexService" group="member" interface="com.xxx.IndexService" />
                
            多版本
                当一个接口实现，出现不兼容升级时，可以用版本号过渡，版本号不同的服务相互间不引用。
                可以按照以下的步骤进行版本迁移：
                0.在低压力时间段，先升级一半提供者为新版本
                1.再将所有消费者升级为新版本
                2.然后将剩下的一半提供者升级为新版本
                
                
            分组聚合
                按组合并返回结果 [1]，比如菜单服务，接口一样，但有多种实现，用group区分，现在消费方需从每种group中调用一次返回结果，
                合并结果返回，这样就可以实现聚合菜单项。
                
            结果缓存
                结果缓存 [1]，用于加速热门数据的访问速度，Dubbo 提供声明式缓存，以减少用户加缓存的工作量 [2]。
                
                缓存类型
                lru 基于最近最少使用原则删除多余缓存，保持最热的数据被缓存。
                threadlocal 当前线程缓存，比如一个页面渲染，用到很多 portal，每个 portal 都要去查用户信息，通过线程缓存，可以减少这种多余访问。
                jcache 与 JSR107 集成，可以桥接各种缓存实现。
                缓存类型可扩展，参见：缓存扩展
                
                配置
                <dubbo:reference interface="com.foo.BarService" cache="lru" />
                或：
                
                <dubbo:reference interface="com.foo.BarService">
                    <dubbo:method name="findBar" cache="lru" />
                </dubbo:reference>
                
    *整体设计
    http://dubbo.apache.org/zh-cn/docs/source_code_guide/service-invoking-process.html
        各层说明
            config 配置层：对外配置接口，以 ServiceConfig, ReferenceConfig 为中心，可以直接初始化配置类，也可以通过 spring 解析配置生成配置类
            proxy 服务代理层：服务接口透明代理，生成服务的客户端 Stub 和服务器端 Skeleton, 以 ServiceProxy 为中心，扩展接口为 ProxyFactory
            registry 注册中心层：封装服务地址的注册与发现，以服务 URL 为中心，扩展接口为 RegistryFactory, Registry, RegistryService
            cluster 路由层：封装多个提供者的路由及负载均衡，并桥接注册中心，以 Invoker 为中心，扩展接口为 Cluster, Directory, Router, LoadBalance
            monitor 监控层：RPC 调用次数和调用时间监控，以 Statistics 为中心，扩展接口为 MonitorFactory, Monitor, MonitorService
            protocol 远程调用层：封装 RPC 调用，以 Invocation, Result 为中心，扩展接口为 Protocol, Invoker, Exporter
            exchange 信息交换层：封装请求响应模式，同步转异步，以 Request, Response 为中心，扩展接口为 Exchanger, ExchangeChannel, ExchangeClient, ExchangeServer
            transport 网络传输层：抽象 mina 和 netty 为统一接口，以 Message 为中心，扩展接口为 Channel, Transporter, Client, Server, Codec
            serialize 数据序列化层：可复用的一些工具，扩展接口为 Serialization, ObjectInput, ObjectOutput, ThreadPool
           
        关系说明
            在 RPC 中，Protocol 是核心层，也就是只要有 Protocol + Invoker + Exporter 就可以完成非透明的 RPC 调用，然后在 Invoker 的主过程上 Filter 拦截点。
            图中的 Consumer 和 Provider 是抽象概念，只是想让看图者更直观的了解哪些类分属于客户端与服务器端，不用 Client 和 Server 的原因是 Dubbo 在很多场景下都使用 Provider, Consumer, Registry, Monitor 划分逻辑拓普节点，保持统一概念。
            而 Cluster 是外围概念，所以 Cluster 的目的是将多个 Invoker 伪装成一个 Invoker，这样其它人只要关注 Protocol 层 Invoker 即可，加上 Cluster 或者去掉 Cluster 对其它层都不会造成影响，因为只有一个提供者时，是不需要 Cluster 的。
            Proxy 层封装了所有接口的透明化代理，而在其它层都以 Invoker 为中心，只有到了暴露给用户使用时，才用 Proxy 将 Invoker 转成接口，或将接口实现转成 Invoker，也就是去掉 Proxy 层 RPC 是可以 Run 的，只是不那么透明，不那么看起来像调本地服务一样调远程服务。
            而 Remoting 实现是 Dubbo 协议的实现，如果你选择 RMI 协议，整个 Remoting 都不会用上，Remoting 内部再划为 Transport 传输层和 Exchange 信息交换层，Transport 层只负责单向消息传输，是对 Mina, Netty, Grizzly 的抽象，它也可以扩展 UDP 传输，而 Exchange 层是在传输层之上封装了 Request-Response 语义。
            Registry 和 Monitor 实际上不算一层，而是一个独立的节点，只是为了全局概览，用层的方式画在一起。
        
        经历多次调用，到这里请求数据的发送过程就结束了，过程漫长。为了便于大家阅读代码，这里以 DemoService 为例，将 sayHello 方法的整个调用路径贴出来。
        
        proxy0#sayHello(String)
          —> InvokerInvocationHandler#invoke(Object, Method, Object[])
            —> MockClusterInvoker#invoke(Invocation)
              —> AbstractClusterInvoker#invoke(Invocation)
                —> FailoverClusterInvoker#doInvoke(Invocation, List<Invoker<T>>, LoadBalance)
                  —> Filter#invoke(Invoker, Invocation)  // 包含多个 Filter 调用
                    —> ListenerInvokerWrapper#invoke(Invocation) 
                      —> AbstractInvoker#invoke(Invocation) 
                        —> DubboInvoker#doInvoke(Invocation)
                          —> ReferenceCountExchangeClient#request(Object, int)
                            —> HeaderExchangeClient#request(Object, int)
                              —> HeaderExchangeChannel#request(Object, int)
                                —> AbstractPeer#send(Object)
                                  —> AbstractClient#send(Object, boolean)
                                    —> NettyChannel#send(Object, boolean)
                                      —> NioClientSocketChannel#write(Object)
                                      
        
        
        上面的方法通过反序列化将诸如 path、version、调用方法名、参数列表等信息依次解析出来，并设置到相应的字段中，
        最终得到一个具有完整调用信息的 DecodeableRpcInvocation 对象。
        
        到这里，请求数据解码的过程就分析完了。此时我们得到了一个 Request 对象，这个对象会被传送到下一个入站处理器中，
        我们继续往下看。
        
        调用服务
        解码器将数据包解析成 Request 对象后，NettyHandler 的 messageReceived 方法紧接着会收到这个对象，并将这个对象继续向下传递。
        这期间该对象会被依次传递给 NettyServer、MultiMessageHandler、HeartbeatHandler 以及 AllChannelHandler。
        最后由 AllChannelHandler 将该对象封装到 Runnable 实现类对象中，并将 Runnable 放入线程池中执行后续的调用逻辑。整个调用栈如下：
        
        NettyHandler#messageReceived(ChannelHandlerContext, MessageEvent)
          —> AbstractPeer#received(Channel, Object)
            —> MultiMessageHandler#received(Channel, Object)
              —> HeartbeatHandler#received(Channel, Object)
                —> AllChannelHandler#received(Channel, Object)
                  —> ExecutorService#execute(Runnable)    // 由线程池执行后续的调用逻辑
                  
                  
        线程派发模型
        Dubbo 将底层通信框架中接收请求的线程称为 IO 线程。如果一些事件处理逻辑可以很快执行完，比如只在内存打一个标记，此时直接在
        IO 线程上执行该段逻辑即可。但如果事件的处理逻辑比较耗时，比如该段逻辑会发起数据库查询或者 HTTP 请求。此时我们就不应该让
        事件处理逻辑在 IO 线程上执行，而是应该派发到线程池中去执行。原因也很简单，IO 线程主要用于接收请求，如果 IO 线程被占满，
        将导致它不能接收新的请求。
        

        到这里，整个服务调用过程就分析完了。最后把调用过程贴出来，如下：
        
        ChannelEventRunnable#run()
          —> DecodeHandler#received(Channel, Object)
            —> HeaderExchangeHandler#received(Channel, Object)
              —> HeaderExchangeHandler#handleRequest(ExchangeChannel, Request)
                —> DubboProtocol.requestHandler#reply(ExchangeChannel, Object)
                  —> Filter#invoke(Invoker, Invocation)
                    —> AbstractProxyInvoker#invoke(Invocation)
                      —> Wrapper0#invokeMethod(Object, String, Class[], Object[])
                        —> DemoServiceImpl#sayHello(String)
                        
    dubbo 熔断，限流，降级
        超时（timeout）
            在接口调用过程中，consumer调用provider的时候，provider在响应的时候，有可能会慢，如果provider 10s响应，那么consumer也
            会至少10s才响应。如果这种情况频度很高，那么就会整体降低consumer端服务的性能。
            这种响应时间慢的症状，就会像一层一层波浪一样，从底层系统一直涌到最上层，造成整个链路的超时。
            所以，consumer不可能无限制地等待provider接口的返回，会设置一个时间阈值，如果超过了这个时间阈值，就不继续等待。
            这个超时时间选取，一般看provider正常响应时间是多少，再追加一个buffer即可。
        重试（retry）
            但是也有可能provider只是偶尔抖动，那么超时后直接放弃，不做后续处理，就会导致当前请求错误，也会带来业务方面的损失。
            那么，对于这种偶尔抖动，可以在超时后重试一下，重试如果正常返回了，那么这次请求就被挽救了，能够正常给前端返回数据，只不过比原来响应慢一点。
            重试时的一些细化策略：
            重试可以考虑切换一台机器来进行调用，因为原来机器可能由于临时负载高而性能下降，重试会更加剧其性能问题，而换一台机器，
            得到更快返回的概率也更大一些。
            
            幂等(idempotent)
                如果允许consumer重试，那么provider就要能够做到幂等。
                即，同一个请求被consumer多次调用，对provider产生的影响(这里的影响一般是指某些写入相关的操作) 是一致的。
                而且这个幂等应该是服务级别的，而不是某台机器层面的，重试调用任何一台机器，都应该做到幂等。
                
        熔断（circuit break）
            如果是一个不那么重要的服务，却因为这个服务一直响应时间长导致consumer里面的核心服务也拖慢，那么就得不偿失了。
            单纯超时也解决不了这种情况了，因为一般超时时间，都比平均响应时间长一些，现在所有的打到provider的请求都超时了，
            那么consumer请求provider的平均响应时间就等于超时时间了，负载也被拖下来了。
            而重试则会加重这种问题，使consumer的可用性变得更差。
            因此就出现了熔断的逻辑，也就是，如果检查出来频繁超时，就把consumer调用provider的请求，直接短路掉，不实际调用，
            而是直接返回一个mock的值。
            等provider服务恢复稳定之后，重新调用。
            
        限流(current limiting)
            上面几个策略都是consumer针对provider出现各种情况而设计的。
            而provider有时候也要防范来自consumer的流量突变问题。
            这样一个场景，provider是一个核心服务，给N个consumer提供服务，突然某个consumer抽风，流量飙升，占用了provider大部分
            机器时间，导致其他可能更重要的consumer不能被正常服务。
            所以，provider端，需要根据consumer的重要程度，以及平时的QPS大小，来给每个consumer设置一个流量上线，同一时间内
            只会给A consumer提供N个线程支持，超过限制则等待或者直接拒绝。
        
            资源隔离
                provider可以对consumer来的流量进行限流，防止provider被拖垮。 
                同样，consumer 也需要对调用provider的线程资源进行隔离。 这样可以确保调用某个provider逻辑不会耗光整个consumer的线程池资源。
        
        服务降级
            降级服务既可以代码自动判断，也可以人工根据突发情况切换。
        
            consumer 端
                consumer 如果发现某个provider出现异常情况，比如，经常超时(可能是熔断引起的降级)，数据错误，这是，consumer可以采取一定的
                策略，降级provider的逻辑，基本的有直接返回固定的数据。
            provider 端
                当provider 发现流量激增的时候，为了保护自身的稳定性，也可能考虑降级服务。 
                比如，1，直接给consumer返回固定数据，2，需要实时写入数据库的，先缓存到队列里，异步写入数据库。
                
##netty
    https://juejin.im/post/5a228cc15188254cc067aef8 (必看)
    
    Netty提供了高性能与易用性，它具有以下特点：
        1.拥有设计良好且统一的API，支持NIO与OIO（阻塞IO）等多种传输类型，支持真正的无连接UDP Socket。
        2.简单而强大的线程模型，可高度定制线程（池）。
        3.良好的模块化与解耦，支持可扩展和灵活的事件模型，可以很轻松地分离关注点以复用逻辑组件（可插拔的,解耦）。
        4.性能高效，拥有比Java核心API更高的吞吐量，通过zero-copy功能以实现最少的内存复制消耗。
        5.内置了许多常用的协议编解码器，如HTTP、SSL、WebScoket等常见协议可以通过Netty做到开箱即用。
        用户也可以利用Netty简单方便地实现自己的应用层协议。
        
    NIO可以称为New IO也可以称为Non-blocking IO，它比Java旧的阻塞IO在性能上要高效许多（如果让每一个连接中的IO操作都单独创建一个线程，
    那么阻塞IO并不会比NIO在性能上落后，但不可能创建无限多的线程，在连接数非常多的情况下会很糟糕）。
    
        1.ByteBuffer：NIO的数据传输是基于缓冲区的，ByteBuffer正是NIO数据传输中所使用的缓冲区抽象。ByteBuffer支持在堆外分配内存，
        并且尝试避免在执行I/O操作中的多余复制。一般的I/O操作都需要进行系统调用，这样会先切换到内核态，内核态要先从文件读取数据到它的缓冲区，
        只有等数据准备完毕后，才会从内核态把数据写到用户态，所谓的阻塞IO其实就是说的在等待数据准备好的这段时间内进行阻塞。
        如果想要避免这个额外的内核操作，可以通过使用mmap（虚拟内存映射）的方式来让用户态直接操作文件。
          
        2.Channel：它类似于文件描述符，简单地来说它代表了一个实体（如一个硬件设备、文件、Socket或者一个能够执行一个
        或多个不同的I/O操作的程序组件）。你可以从一个Channel中读取数据到缓冲区，也可以将一个缓冲区中的数据写入到Channel。
        
        3.Selector：选择器是NIO实现的关键，NIO采用的是I/O多路复用的方式来实现非阻塞，Selector通过在一个线程中监听每个Channel的IO事件
        来确定有哪些已经准备好进行IO操作的Channel，因此可以在任何时间检查任意的读操作或写操作的完成状态。这种方式避免了等待IO操作准备
        数据时的阻塞，使用较少的线程便可以处理许多连接，减少了线程切换与维护的开销。
        
    网络传输的基本单位是字节，在Java NIO中提供了ByteBuffer作为字节缓冲区容器，但该类的API使用起来不太方便，所以Netty实现了ByteBuf
    作为其替代品，下面是使用ByteBuf的优点：
        相比ByteBuffer使用起来更加简单。
        通过内置的复合缓冲区类型实现了透明的zero-copy。
        容量可以按需增长。
        读和写使用了不同的索引指针。
        支持链式调用。
        支持引用计数与池化。
        可以被用户自定义的缓冲区类型扩展。
        
    *ByteBuf
    
    在讨论ByteBuf之前，我们先需要了解一下ByteBuffer的实现，这样才能比较深刻地明白它们之间的区别。
    ByteBuffer继承于abstract class Buffer（所以还有LongBuffer、IntBuffer等其他类型的实现），
        本质上它只是一个有限的线性的元素序列，包含了三个重要的属性。
            1.Capacity：缓冲区中元素的容量大小，你只能将capacity个数量的元素写入缓冲区，一旦缓冲区已满就需要清理缓冲区才能继续写数据。
            2.Position：指向下一个写入数据位置的索引指针，初始位置为0，最大为capacity-1。当写模式转换为读模式时，position需要被重置为0。
            3.Limit：在写模式中，limit是可以写入缓冲区的最大索引，也就是说它在写模式中等价于缓冲区的容量。在读模式中，limit表示可以读取数据的最大索引。
        
        由于Buffer中只维护了position一个索引指针，所以它在读写模式之间的切换需要调用一个flip()方法来重置指针。使用Buffer的流程一般如下：
            写入数据到缓冲区。
            调用flip()方法。
            从缓冲区中读取数据
            调用buffer.clear()或者buffer.compact()清理缓冲区，以便下次写入数据。
        
        Java NIO中的Buffer API操作的麻烦之处就在于读写转换需要手动重置指针。而ByteBuf没有这种繁琐性，它维护了两个不同的索引，
        一个用于读取，一个用于写入。当你从ByteBuf读取数据时，它的readerIndex将会被递增已经被读取的字节数，同样的，
        当你写入数据时，writerIndex则会递增。readerIndex的最大范围在writerIndex的所在位置，如果试图移动readerIndex超过该值则会触发异常。
        ByteBuf中名称以read或write开头的方法将会递增它们其对应的索引，而名称以get或set开头的方法则不会。
        ByteBuf同样可以指定一个最大容量，试图移动writerIndex超过该值则会触发异常。
        
        1.ByteBuf同样支持在堆内和堆外进行分配。在堆内分配也被称为支撑数组模式，它能在没有使用池化的情况下提供快速的分配和释放。
        2.另一种模式为堆外分配，Java NIO ByteBuffer类在JDK1.4时就已经允许JVM实现通过JNI调用来在堆外分配内存（
        调用malloc()函数在JVM堆外分配内存），这主要是为了避免额外的缓冲区复制操作。
        3.ByteBuf还支持第三种模式，它被称为复合缓冲区，为多个ByteBuf提供了一个聚合视图。在这个视图中，
        你可以根据需要添加或者删除ByteBuf实例，ByteBuf的子类CompositeByteBuf实现了该模式。
            一个适合使用复合缓冲区的场景是HTTP协议，通过HTTP协议传输的消息都会被分成两部分——头部和主体，
            如果这两部分由应用程序的不同模块产生，将在消息发送时进行组装，并且该应用程序还会为多个消息复用相同的消息主体，
            这样对于每个消息都将会创建一个新的头部，产生了很多不必要的内存操作。使用CompositeByteBuf是一个很好的选择，
            它消除了这些额外的复制，以帮助你复用这些消息。
            
            CompositeByteBuf透明的实现了zero-copy，zero-copy其实就是避免数据在两个内存区域中来回的复制。从操作系统层面上来讲，
            zero-copy指的是避免在内核态与用户态之间的数据缓冲区复制（通过mmap避免），而Netty中的zero-copy更偏向于在用户态中的
            数据操作的优化，就像使用CompositeByteBuf来复用多个ByteBuf以避免额外的复制，也可以使用wrap()方法来将一个字节数组
            包装成ByteBuf，又或者使用ByteBuf的slice()方法把它分割为多个共享同一内存区域的ByteBuf，这些都是为了优化内存的使用率
            
    CompositeByteBuf实现零拷贝  
        zero copy
            OS层面
                即所谓的 Zero-copy, 就是在操作数据时, 不需要将数据 buffer 从一个内存区域拷贝到另一个内存区域. 因为少了一次内存的拷贝, 
                因此 CPU 的效率就得到的提升.
                在 OS 层面上的 Zero-copy 通常指避免在 用户态(User-space) 与 内核态(Kernel-space) 之间来回拷贝数据.
                 例如 Linux 提供的 mmap 系统调用, 它可以将一段用户空间内存映射到内核空间, 当映射成功后, 用户对这段内存区域的修改
                 可以直接反映到内核空间; 同样地, 内核空间对这段区域的修改也直接反映用户空间. 正因为有这样的映射关系, 我们就不需要在 
                 用户态(User-space) 与 内核态(Kernel-space) 之间拷贝数据, 提高了数据传输的效率.
                而需要注意的是, Netty 中的 Zero-copy 与上面我们所提到到 OS 层面上的 Zero-copy 不太一样,
                 Netty的 Zero-coyp 完全是在用户态(Java 层面)的, 它的 Zero-copy 的更多的是偏向于 优化数据操作 这样的概念.
            
            Netty 的 Zero-copy 体现在如下几个个方面:
                Netty 提供了 CompositeByteBuf 类, 它可以将多个 ByteBuf 合并为一个逻辑上的 ByteBuf, 避免了各个 ByteBuf 之间的拷贝.
                通过 wrap 操作, 我们可以将 byte[] 数组、ByteBuf、ByteBuffer等包装成一个 Netty ByteBuf 对象, 进而避免了拷贝操作.
                ByteBuf 支持 slice 操作, 因此可以将 ByteBuf 分解为多个共享同一个存储区域的 ByteBuf, 避免了内存的拷贝.
                通过 FileRegion 包装的FileChannel.tranferTo 实现文件传输, 可以直接将文件缓冲区的数据发送到目标 Channel,
                 避免了传统通过循环 write 方式导致的内存拷贝问题.

        不过需要注意的是, 虽然看起来 CompositeByteBuf 是由两个 ByteBuf 组合而成的, 不过在 CompositeByteBuf 内部, 这两个 
        ByteBuf 都是单独存在的, CompositeByteBuf 只是逻辑上是一个整体.
        那么其实 compositeByteBuf 的 writeIndex 仍然是0, 因此此时我们就不可能从 compositeByteBuf 中读取到数据, 
        这一点希望大家要特别注意.
        除了上面直接使用 CompositeByteBuf 类外, 我们还可以使用 Unpooled.wrappedBuffer 方法, 它底层封装了 CompositeByteBuf 操作,
        因此使用起来更加方便:
        
        通过 slice 操作实现零拷贝
            
        为了优化内存使用率，Netty提供了一套手动的方式来追踪不活跃对象，像UnpooledHeapByteBuf这种分配在堆内的对象得益于JVM的GC管理，
        无需额外操心，而UnpooledDirectByteBuf是在堆外分配的，它的内部基于DirectByteBuffer，DirectByteBuffer会先向Bits类申请
        一个额度（Bits还拥有一个全局变量totalCapacity，记录了所有DirectByteBuffer总大小），每次申请前都会查看是否已经超过
        -XX:MaxDirectMemorySize所设置的上限，如果超限就会尝试调用Sytem.gc()，以试图回收一部分内存，然后休眠100毫秒，
        如果内存还是不足，则只能抛出OOM异常。堆外内存的回收虽然有了这么一层保障，但为了提高性能与使用率，主动回收也是很有必要的。
        由于Netty还实现了ByteBuf的池化，像PooledHeapByteBuf和PooledDirectByteBuf就必须依赖于手动的方式来进行回收（放回池中）。
        
        Netty使用了引用计数器的方式来追踪那些不活跃的对象。引用计数的接口为ReferenceCounted，它的思想很简单，只要ByteBuf对象的引用计数大于0，
        就保证该对象不会被释放回收，可以通过手动调用release()与retain()方法来操作该对象的引用计数值递减或递增。用户也可以通过自定义
        一个ReferenceCounted的实现类，以满足自定义的规则。
        
    *Channel
        Netty中的Channel与Java NIO的概念一样，都是对一个实体或连接的抽象，但Netty提供了一套更加通用的API。
        
        每个Channel最终都会被分配一个ChannelPipeline和ChannelConfig，前者持有所有负责处理入站与出站数据以及事件的ChannelHandler，
        后者包含了该Channel的所有配置设置，并且支持热更新，由于不同的传输类型可能具有其特别的配置，所以该类可能会实现为ChannelConfig的
        不同子类。
        
        Channel是线程安全的（与之后要讲的线程模型有关），因此你完全可以在多个线程中复用同一个Channel，
        
    *ChannelHandler
        ChannelHandler充当了处理入站和出站数据的应用程序逻辑的容器，该类是基于事件驱动的，它会响应相关的事件然后去调用其关联的回调函数，
        例如当一个新的连接被建立时，ChannelHandler的channelActive()方法将会被调用。
        
        ChannelHandler的主要用途包括：
            对入站与出站数据的业务逻辑处理
            记录日志
            将数据从一种格式转换为另一种格式，实现编解码器。以一次HTTP协议（或者其他应用层协议）的流程为例，数据在网络传输时的单位为字节，
            当客户端发送请求到服务器时，服务器需要通过解码器（处理入站消息）将字节解码为协议的消息内容，服务器在发送响应的时候（处理出站消息），
            还需要通过编码器将消息内容编码为字节。
            捕获异常
            提供Channel生命周期内的通知，如Channel活动时与非活动时
        
        Netty中到处都充满了异步与事件驱动，而回调函数正是用于响应事件之后的操作。由于异步会直接返回一个结果，
        所以Netty提供了ChannelFuture（实现了java.util.concurrent.Future）来作为异步调用返回的占位符，
        真正的结果会在未来的某个时刻完成，到时候就可以通过ChannelFuture对其进行访问，每个Netty的出站I/O操作
        都将会返回一个ChannelFuture。
        Netty还提供了ChannelFutureListener接口来监听ChannelFuture是否成功，并采取对应的操作。
        Channel channel = ...
        ChannelFuture future = channel.connect(new InetSocketAddress("192.168.0.1",6666));
        // 注册一个监听器
        future.addListener(new ChannelFutureListener() {
        	@Override
        	public void operationComplete(ChannelFuture future) {
        		if (future.isSuccess()) {
        			// do something....
        		} else {
        			// 输出错误信息
        			Throwable cause = future.cause();
        			cause.printStackTrace();
        			// do something....
        		}
        	}
        });
        
        
        入站消息与出站消息由其对应的接口ChannelInboundHandler与ChannelOutboundHandler负责，
        这两个接口定义了监听Channel的生命周期的状态改变事件的回调函数。
        
        对于处理入站消息，另外一种选择是继承SimpleChannelInboundHandler，它是Netty的一个继承于ChannelInboundHandlerAdapter的抽象类，
        并在其之上实现了自动释放资源的功能。
        我们在了解ByteBuf时就已经知道了Netty使用了一套自己实现的引用计数算法来主动释放资源，假设你的ChannelHandler继承于
        ChannelInboundHandlerAdapter或ChannelOutboundHandlerAdapter，那么你就有责任去管理你所分配的ByteBuf，
        一般来说，一个消息对象（ByteBuf）已经被消费（或丢弃）了，并且不会传递给ChannelHandler链中的下一个处理器
        （如果该消息到达了实际的传输层，那么当它被写入或Channel关闭时，都会被自动释放），那么你就需要去手动释放它。通过一个简单的工具类
        ReferenceCountUtil的release方法，就可以做到这一点。
        
    *ChannelPipeline
        
        为了模块化与解耦合，不可能由一个ChannelHandler来完成所有应用逻辑，所以Netty采用了拦截器链的设计。ChannelPipeline就是用来管理
        ChannelHandler实例链的容器，它的职责就是保证实例链的流动。
        每一个新创建的Channel都将会被分配一个新的ChannelPipeline，这种关联关系是永久性的，一个Channel一生只能对应一个ChannelPipeline。
        
        一个入站事件被触发时，它会先从ChannelPipeline的最左端（头部）开始一直传播到ChannelPipeline的最右端（尾部），
        而出站事件正好与入站事件顺序相反（从最右端一直传播到最左端）。这个顺序是定死的，Netty总是将ChannelPipeline的入站口作为头部，
        而将出站口作为尾部。在事件传播的过程中，ChannelPipeline会判断下一个ChannelHandler的类型是否和事件的运动方向相匹配，如果不匹配，
        就跳过该ChannelHandler并继续检查下一个（保证入站事件只会被ChannelInboundHandler处理），一个ChannelHandler也可以同时实现
        ChannelInboundHandler与ChannelOutboundHandler，它在入站事件与出站事件中都会被调用。
        
        在阅读ChannelHandler的源码时，发现很多方法需要一个ChannelHandlerContext类型的参数，该接口是ChannelPipeline与
        ChannelHandler之间相关联的关键。ChannelHandlerContext可以通知ChannelPipeline中的当前ChannelHandler的下一个ChannelHandler，
        还可以动态地改变当前ChannelHandler在ChannelPipeline中的位置（通过调用ChannelPipeline中的各种方法来修改）。
        ChannelHandlerContext负责了在同一个ChannelPipeline中的ChannelHandler与其他ChannelHandler之间的交互，
        每个ChannelHandlerContext都对应了一个ChannelHandler。在DefaultChannelPipeline的源码中，已经表现的很明显了。
        
    *EventLoop
        为了最大限度地提供高性能和可维护性，Netty设计了一套强大又易用的线程模型。在一个网络框架中，最重要的能力是能够快速高效地处理
        在连接的生命周期内发生的各种事件，与之相匹配的程序构造被称为事件循环，Netty定义了接口EventLoop来负责这项工作。
                
        如果是经常用Java进行多线程开发的童鞋想必经常会使用到线程池，也就是Executor这套API。Netty就是从Executor（java.util.concurrent）
        之上扩展了自己的EventExecutorGroup（io.netty.util.concurrent），同时为了与Channel的事件进行交互，还扩展了EventLoopGroup接口
        （io.netty.channel）。在io.netty.util.concurrent包下的EventExecutorXXX负责实现线程并发相关的工作，而在io.netty.channel包下
        的EventLoopXXX负责实现网络编程相关的工作（处理Channel中的事件）。
        
        在Netty的线程模型中，一个EventLoop将由一个永远不会改变的Thread驱动，而一个Channel一生只会使用一个EventLoop
        （但是一个EventLoop可能会被指派用于服务多个Channel），在Channel中的所有I/O操作和事件都由EventLoop中的线程处理，
        也就是说一个Channel的一生之中都只会使用到一个线程。不过在Netty3，只有入站事件会被EventLoop处理，所有出站事件都会由调用线程处理，
        这种设计导致了ChannelHandler的线程安全问题。Netty4简化了线程模型，通过在同一个线程处理所有事件，既解决了这个问题，
        还提供了一个更加简单的架构。
        
        为了确保一个Channel的整个生命周期中的I/O事件会被一个EventLoop负责，Netty通过inEventLoop()方法来判断当前执行的线程的身份，
        确定它是否是分配给当前Channel以及它的EventLoop的那一个线程。如果当前（调用）线程正是EventLoop中的线程，那么所提交的任务将会被直接执行，
        否则，EventLoop将调度该任务以便稍后执行，并将它放入内部的任务队列（每个EventLoop都有它自己的任务队列，从SingleThreadEventLoop
        的源码就能发现很多用于调度内部任务队列的方法），在下次处理它的事件时，将会执行队列中的那些任务。这种设计可以让任何线程与Channel直接交互，
        而无需在ChannelHandler中进行额外的同步。
        从性能上来考虑，千万不要将一个需要长时间来运行的任务放入到任务队列中，它会影响到该队列中的其他任务的执行。解决方案是使用一个专门的
        EventExecutor来执行它（ChannelPipeline提供了带有EventExecutorGroup参数的addXXX()方法，该方法可以将传入的ChannelHandler
        绑定到你传入的EventExecutor之中），这样它就会在另一条线程中执行，与其他任务隔离。
        
        EventLoopGroup负责管理和分配EventLoop（创建EventLoop和为每个新创建的Channel分配EventLoop），根据不同的传输类型，
        EventLoop的创建和分配方式也不同。例如，使用NIO传输类型，EventLoopGroup就会只使用较少的EventLoop（一个EventLoop服务于多个Channel），
        这是因为NIO基于I/O多路复用，一个线程可以处理多个连接，而如果使用的是OIO，那么新创建一个Channel（连接）就需要分配一个EventLoop（线程）。
        
        EventLoopGroup是一组EventLoop的抽象，由于Netty对Reactor模式进行了变种，实际上为更好的利用多核CPU资源，
        Netty实例中一般会有多个EventLoop同时工作，每个EventLoop维护着一个Selector实例，类似单线程Reactor模式地工作着。
        至于多少线程可有用户决定，Netty也根据实际上的处理器核数提供了一个默认的数字，我们也建议使用这个数字
        Runtime.getRuntime().availableProcessors() * 2
        
        EventLoopGroup提供next接口，可以总一组EventLoop里面按照一定规则获取其中一个EventLoop来处理任务，
        对于EventLoopGroup这里需要了解的是在Netty中，在Netty服务器编程中我们需要BossEventLoopGroup和WorkerEventLoopGroup
        两个EventLoopGroup来进行工作。通常一个服务端口即一个ServerSocketChannel对应一个Selector和一个EventLoop线程，
        也就是我们建议BossEventLoopGroup的线程数参数这是为1。BossEventLoop负责接收客户端的连接并将SocketChannel交给
        WorkerEventLoopGroup来进行IO处理。
        
        如上图，BossEventLoopGroup通常是一个单线程的EventLoop，EventLoop维护着一个注册了ServerSocketChannel的Selector实例，
        BoosEventLoop不断轮询Selector将连接事件分离出来，通常是OP_ACCEPT事件，然后将accept得到的SocketChannel交给WorkerEventLoopGroup，
        WorkerEventLoopGroup会由next选择其中一个EventLoopGroup来将这个SocketChannel注册到其维护的Selector并对其后续的IO事件进行处理。
        在Reactor模式中BossEventLoopGroup主要是对多线程的扩展，而每个EventLoop的实现涵盖IO事件的分离，和分发（Dispatcher）。
        
        关于IO密集型和CPU密集型的思考
        
        Netty基于单线程设计的EventLoop能够同时处理成千上万的客户端连接的IO事件，缺点是单线程不能够处理时间过长的任务，
        这样会阻塞使得IO事件的处理被阻塞，严重的时候回造成IO事件堆积，服务不能够高效响应客户端请求。所谓时间过长的任务通常是占用CPU资源
        比较长的任务，也即CPU密集型，对于业务应用也可能是业务代码的耗时。这点和Node是极其相似的，我可以认为这是基于单线程的EventLoop
        模型的通病，我们不能够将过长的任务交给这个单线程来处理，也就是不适合CPU密集型应用。那么问题怎么解决呢，参照Node的解决方案，
        当我们遇到需要处理时间很长的任务的时候，我们可以将它交给子线程来处理，主线程继续去EventLoop，当子线程计算完毕再讲结果交给主线程。
        这也是通常基于Netty的应用的解决方案，通常业务代码执行时间比较长，我们不能够把业务逻辑交给这个单线程来处理，因此我们需要额外的
        线程池来分配线程资源来专门处理耗时较长的业务逻辑，这是比较通用的设计方案。
        
    *Bootstrap
        在深入了解地Netty的核心组件之后，发现它们的设计都很模块化，如果想要实现你自己的应用程序，就需要将这些组件组装到一起。
        Netty通过Bootstrap类，以对一个Netty应用程序进行配置（组装各个组件），并最终使它运行起来。对于客户端程序和服务器程序所使用到的
        Bootstrap类是不同的，后者需要使用ServerBootstrap，这样设计是因为，在如TCP这样有连接的协议中，服务器程序往往需要一个以上的Channel，
        通过父Channel来接受来自客户端的连接，然后创建子Channel用于它们之间的通信，而像UDP这样无连接的协议，它不需要每个连接都创建子Channel，
        只需要一个Channel即可。
        
    (推荐阅读)
    https://www.cnblogs.com/heavenhome/articles/6554262.html
        
                
                
##spark
https://blog.csdn.net/pengzonglu7292/article/details/80554507   (面试题)
https://juejin.im/post/58d8daedac502e0058d9edf0 (整体介绍)

    spark背景
        什么是spark
            Spark生态系统已经发展成为一个包含多个子项目的集合，其中包含SparkSQL、Spark Streaming、GraphX、MLlib等子项目，
            Spark是基于内存计算的大数据并行计算框架。Spark基于内存计算，提高了在大数据环境下数据处理的实时性，同时保证了高容错性和高可伸缩性，
            允许用户将Spark部署在大量廉价硬件之上，形成集群。
        Spark与Hadoop
            Spark是一个计算框架,而Hadoop中包含计算框架MapReduce和分布式文件系统HDFS,Hadoop更广泛地说还包括在其生态系统上的其他系统.
        为什么使用Spark?
            Hadoop的MapReduce计算模型存在问题:
            Hadoop的MapReduce的核心是Shuffle(洗牌).在整个Shuffle的过程中,至少产生6次I/O流.基于MapReduce计算引擎通常会将结果
            输出到次盘上,进行存储和容错.另外,当一些查询(如:hive)翻译到MapReduce任务是,往往会产生多个Stage,而这些Stage有依赖底层
            文件系统来存储每一个Stage的输出结果,而I/O的效率往往较低,从而影响MapReduce的运行速度.
        Spark的特点: 快, 易用, 通用,兼容性
            快：与Hadoop的MapReduce相比，Spark基于内存的运算要快100倍以上，基于硬盘的运算也要快10倍以上。
                Spark实现了高效的DAG执行引擎，可以通过基于内存来高效处理数据流。
            易用：Spark支持Java、Python和Scala的API，还支持超过80种高级算法，使用户可以快速构建不同的应用。而且Spark支持交互式
                的Python和Scala的shell，可以非常方便地在这些shell中使用Spark集群来验证解决问题的方法。
            通用：Spark提供了统一的解决方案。Spark可以用于批处理、交互式查询（Spark SQL）、实时流处理（Spark Streaming）、
                机器学习（Spark MLlib）和图计算（GraphX）。这些不同类型的处理都可以在同一个应用中无缝使用。Spark统一的解决方案非常具有吸引力，毕竟任何公司都想用统一的平台去处理遇到的问题，减少开发和维护的人力成本和部署平台的物力成本。
            兼容性：Spark 可以非常方便地与其他的开源产品进行融合。比如，Spark 可以使用Hadoop 的 YARN 和 Apache Mesos 作为它的
                资源管理和调度器.并且可以处理所有 Hadoop 支持的数据，包括 HDFS、HBase 和 Cassandra 等。这对于已经部署Hadoop 集群
                的用户特别重要，因为不需要做任何数据迁移就可以使用 Spark 的强大处理能力。Spark 也可以不依赖于第三方的资源管理和调度器，
                它实现了Standalone 作为其内置的资源管理和调度框架，这样进一步降低了 Spark 的使用门槛，使得所有人都可以非常容易地部署和使用
                 Spark。此外，Spark 还提供了在EC2 上部Standalone 的 Spark 集群的工具。
        Spark的生态系统
            1.Spark Streaming:
                Spark Streaming基于微批量方式的计算和处理,可以用于处理实时的流数据.它使用DStream,简单来说是一个弹性分布式数据集(RDD)系列,
                处理实时数据.数据可以从Kafka,Flume,Kinesis或TCP套接字等众多来源获取,并且可以使用由高级函数（如 map，reduce，join 和 window）
                开发的复杂算法进行流数据处理。最后，处理后的数据可以被推送到文件系统，数据库和实时仪表板。
            2.Spark SQL
                SPark SQL可以通过JDBC API将Spark数据集暴露出去,而且还可以用传统的BI和可视化工具在Spark数据上执行类似SQL的查询,
                用户哈可以用Spark SQL对不同格式的数据(如Json, Parque以及数据库等)执行ETl,将其转化,然后暴露特定的查询.
            3.Spark MLlib
                MLlib是一个可扩展的Spark机器学习库，由通用的学习算法和工具组成，包括二元分类、线性回归、聚类、协同过滤、梯度下降以及底层优化原语。
            4.Spark Graphx:
                GraphX是用于图计算和并行图计算的新的（alpha）Spark API。通过引入弹性分布式属性图（Resilient Distributed Property Graph），
                一种顶点和边都带有属性的有向多重图，扩展了Spark RDD。为了支持图计算，GraphX暴露了一个基础操作符集合（如subgraph，
                joinVertices和aggregateMessages）和一个经过优化的Pregel API变体。此外，GraphX还包括一个持续增长的用于简化图分析任务的图算法和构建器集合。
            5.Tachyon
                Tachyon是一个以内存为中心的分布式文件系统,能够提供内存级别速度的跨集群框架(如Spark和mapReduce)的可信文件共享.它将工作集文件缓存在内存中,从而避免到磁盘中加载需要经常读取的数据集,通过这一机制,不同的作业/查询和框架可以内存级的速度访问缓存文件.
                此外，还有一些用于与其他产品集成的适配器，如Cassandra（Spark Cassandra 连接器）和R（SparkR）。Cassandra Connector可用于访问存储在Cassandra数据库中的数据并在这些数据上执行数据分析。
            6.Mesos
                Mesos是一个资源管理框架
                提供类似于YARN的功能
                用户可以在其中插件式地运行Spark,MapReduce,Tez等计算框架任务
                Mesos对资源和任务进行隔离,并实现高效的资源任务调度
            7.BlinkDB
                BlinkDB是一个用于在海量数据上进行交互式SQL的近似查询引擎
                允许用户通过查询准确性和查询时间之间做出权衡,完成近似查询
                核心思想:通过一个自适应优化框架,随着时间的推移,从原始数据建立并维护一组多维样本,通过一个动态样本选择策略,选择一个适当大小的示例,然后基于查询的准确性和响应时间满足用户查询需求
    
        Spark架构采用了分布式计算中的Master-Slave模型。Master是对应集群中的含有Master进程的节点，Slave是集群中含有Worker进程的节点。
            Master作为整个集群的控制器，负责整个集群的正常运行；Worker相当于是计算节点，接收主节点命令与进行状态汇报；Executor负责任务的执行；
            Client作为用户的客户端负责提交应用，Driver负责控制一个应用的执行.
        Spark集群部署后,需要在主节点和从节点分别启动master进程和Worker进程,对整个集群进行控制.在一个Spark应用的执行程序中.
            Driver和Worker是两个重要的角色.Driver程序是应用逻辑执行的起点，负责作业的调度,即Task任务的发布,而多个Worker用来管理计算
            节点和创建Executor并行处理任务.在执行阶段,Driver会将Task和Task所依赖的file和jar序列化后传递给对应的Worker机器.
            同时Executor对相应数据分区的任务进行处理.
        Sparkde架构中的基本组件:
            ClusterManager:在standlone模式中即为Master(主节点),控制整个集群.监控Worker.在Yarn模式中为资源管理器.
            Worker:从节点,负责控制计算节点,启动Ex而粗投入或Driver
            NodeManager:负责计算节点的控制。
            Driver:运行Application的main() 函数并创建SparkContext
            Executor: 执行器,在worker node上执行任务组件,用于启动线程执行任务.每个Application拥有独立的一组Executors
            SparkContext: 整个应用的上下文,监控应用的生命周期
            RDD:弹性分布式集合,spark的基本计算单元，一组RDD可形成执行的有向无环图RDD Graph
            DAG Scheduler: 根据作业(Job)构建基于Stage的DAG,并交给Stage给TaskScheduler
            TaskScheduler：将任务（Task）分发给Executor执行
            SparkEnv：线程级别的上下文，存储运行时的重要组件的引用。SparkEnv内创建并包含如下一些重要组件的引用。
            MapOutPutTracker：负责Shuffle元信息的存储。
            BroadcastManager：负责广播变量的控制与元信息的存储。
            BlockManager：负责存储管理、创建和查找块。
            MetricsSystem：监控运行时性能指标信息。
            SparkConf：负责存储配置信息。
            *Spark的整体流程:client提交应用,Master找到一个Worker启动Driver,Driver向Master或者向资源管理器申请资源,之后将应用转化为RDD Graph，
            再由DAGScheduler将RDD Graph转化为Stage的有向无环图提交给TaskScheduler，由TaskScheduler提交任务给Executor执行。
            在任务执行的过程中，其他组件协同工作，确保整个应用顺利执行。

        spark解决单点问题
            启动后执行jps命令，主节点上有Master进程，其他子节点上有Work进行，登录Spark管理界面查看集群状态（主节点）：http://node-1:8080/
            到此为止，Spark集群安装完毕，但是有一个很大的问题，那就是Master节点存在单点故障，要解决此问题，就要借助zookeeper，
            并且启动至少两个Master节点来实现高可靠，配置方式比较简单：
            Spark集群规划：node-1，node-2是Master；node-3，node-4，node-5是Worker
            安装配置zk集群，并启动zk集群
            停止spark所有服务，修改配置文件spark-env.sh，在该配置文件中删掉SPARK_MASTER_IP并添加如下配置
            export SPARK_DAEMON_JAVA_OPTS="-Dspark.deploy.recoveryMode=ZOOKEEPER -Dspark.deploy.zookeeper.url=zk1,zk2,zk3 
            -Dspark.deploy.zookeeper.dir=/spark"
            1.在node1节点上修改slaves配置文件内容指定worker节点
            2.在node1上执行$SPARK_HOME/sbin/start-all.sh，然后在node2上执行$SPARK_HOME/sbin/start-master.sh启动第二个Master
    
    基本概念
        spark RDD
            RDD概述
                什么是RDD
                    RDD（Resilient Distributed Dataset）叫做分布式数据集，是Spark中最基本的数据抽象，它代表一个不可变、可分区、
                    里面的元素可并行计算的集合。RDD具有数据流模型的特点：自动容错、位置感知性调度和可伸缩性。RDD允许用户在执行多个查询时
                    显式地将工作集缓存在内存中，后续的查询能够重用工作集，这极大地提升了查询速度。
                RDD的属性
                    一组分片（Partition），即数据集的基本组成单位。对于RDD来说，每个分片都会被一个计算任务处理，并决定并行计算的粒度。
                    用户可以在创建RDD时指定RDD的分片个数，如果没有指定，那么就会采用默认值。默认值就是程序所分配到的CPU Core的数目。
                    一个计算每个分区的函数。Spark中RDD的计算是以分片为单位的，每个RDD都会实现compute函数以达到这个目的。compute函数
                    会对迭代器进行复合，不需要保存每次计算的结果。
                    RDD之间的依赖关系。RDD的每次转换都会生成一个新的RDD，所以RDD之间就会形成类似于流水线一样的前后依赖关系。在部分
                    分区数据丢失时，Spark可以通过这个依赖关系重新计算丢失的分区数据，而不是对RDD的所有分区进行重新计算。
                    一个Partitioner，即RDD的分片函数。当前Spark中实现了两种类型的分片函数，一个是基于哈希的HashPartitioner，
                    另外一个是基于范围的RangePartitioner。只有对于于key-value的RDD，才会有Partitioner，非key-value的RDD的
                    Parititioner的值是None。Partitioner函数不但决定了RDD本身的分片数量，也决定了parent RDD Shuffle输出时的分片数量。
                    一个列表，存储存取每个Partition的优先位置（preferred location）。对于一个HDFS文件来说，这个列表保存的就是每个
                    Partition所在的块的位置。按照“移动数据不如移动计算”的理念，Spark在进行任务调度的时候，会尽可能地将计算任务分配
                    到其所要处理数据块的存储位置。
                RDD创建
                    一旦分布式数据集（distData）被创建好，它们将可以被并行操作。例如，我们可以调用distData.reduce(lambda a, b: a + b)
                    来将数组的元素相加。我们会在后续的分布式数据集运算中进一步描述。
                    并行集合的一个重要参数是slices，表示数据集切分的份数。Spark将会在集群上为每一份数据起一个任务。典型地，
                    你可以在集群的每个CPU上分布2-4个slices. 一般来说，Spark会尝试根据集群的状况，来自动设定slices的数目。然而，
                    你也可以通过传递给parallelize的第二个参数来进行手动设置。（例如：sc.parallelize(data, 10)).

        Spark 分区(Partition)的认识、理解和应用
        https://blog.csdn.net/zhangzeyuan56/article/details/80935034
            什么是分区以及为什么要分区?
                Spark RDD 是一种分布式的数据集，由于数据量很大，因此要它被切分并存储在各个结点的分区当中。从而当我们对RDD进行操作时，
                实际上是对每个分区中的数据并行操作。
            分区的3种方式
                1、HashPartitioner
                scala> val counts = sc.parallelize(List((1,'a'),(1,'aa'),(2,'b'),(2,'bb'),(3,'c')), 3)
                .partitionBy(new HashPartitioner(3))
                HashPartitioner确定分区的方式：partition = key.hashCode () % numPartitions
                
                2、RangePartitioner
                scala> val counts = sc.parallelize(List((1,'a'),(1,'aa'),(2,'b'),(2,'bb'),(3,'c')), 3)
                .partitionBy(new RangePartitioner(3,counts))
                RangePartitioner会对key值进行排序，然后将key值被划分成3份key值集合。
                
                3、CustomPartitioner
                CustomPartitioner可以根据自己具体的应用需求，自定义分区。
                class CustomPartitioner(numParts: Int) extends Partitioner {
                 override def numPartitions: Int = numParts
                 override def getPartition(key: Any): Int =
                 {
                       if(key==1)){
                	0
                       } else if (key==2){
                       1} else{ 
                       2 }
                  } 
                }
                scala> val counts = sc.parallelize(List((1,'a'),(1,'aa'),(2,'b'),(2,'bb'),(3,'c')), 3).partitionBy(new CustomPartitioner(3))

            *理解从HDFS读入文件默认是怎样分区的
                Spark从HDFS读入文件的分区数默认等于HDFS文件的块数(blocks)，HDFS中的block是分布式存储的最小单元。
                如果我们上传一个30GB的非压缩的文件到HDFS，HDFS默认的块容量大小128MB，因此该文件在HDFS上会被分为235块(30GB/128MB)；
                Spark读取SparkContext.textFile()读取该文件，默认分区数等于块数即235。
                
                
            如何设置合理的分区数
                1、分区数越多越好吗？
                不是的，分区数太多意味着任务数太多，每次调度任务也是很耗时的，所以分区数太多会导致总体耗时增多。
                2、分区数太少会有什么影响？
                分区数太少的话，会导致一些结点没有分配到任务；另一方面，分区数少则每个分区要处理的数据量就会增大，从而对每个结点的内存
                要求就会提高；还有分区数不合理，会导致数据倾斜问题。
                3、合理的分区数是多少？如何设置？
                总核数=executor-cores * num-executor 
                一般合理的分区数设置为总核数的2~3倍
                
        *spark内存管理
            下文介绍的内存模型全部指 Executor 端的内存模型， Driver 端的内存模型本文不做介绍。统一内存管理模块包括了堆内内存
            (On-heap Memory)和堆外内存(Off-heap Memory)两大区域，下面对这两块区域进行详细的说明
            
            *堆内内存(On-heap Memory)
                默认情况下，Spark 仅仅使用了堆内内存。Executor 端的堆内内存区域大致可以分为以下四大块：
                
                1.Execution 内存：主要用于存放 Shuffle、Join、Sort、Aggregation 等计算过程中的临时数据
                2.Storage 内存：主要用于存储 spark 的 cache 数据，例如RDD的缓存、unroll数据；
                3.用户内存（User Memory）：主要用于存储 RDD 转换操作所需要的数据，例如 RDD 依赖等信息。
                4.预留内存（Reserved Memory）：系统预留内存，会用来存储Spark内部对象。
                
                systemMemory = Runtime.getRuntime.maxMemory，其实就是通过参数 spark.executor.memory 或 --executor-memory 配置的。
                reservedMemory 在 Spark 2.2.1 中是写死的，其值等于 300MB，这个值是不能修改的（如果在测试环境下，我们可以通过 spark.testing.reservedMemory 参数进行修改）；
                usableMemory = systemMemory - reservedMemory，这个就是 Spark 可用内存；
                
            堆外内存(Off-heap Memory)
                默认情况下，堆外内存是关闭的，我们可以通过 spark.memory.offHeap.enabled 参数启用，并且通过 
                spark.memory.offHeap.size 设置堆外内存大小，单位为字节。如果堆外内存被启用，那么 Executor 
                内将同时存在堆内和堆外内存，两者的使用互补影响，这个时候 Executor 中的 Execution 内存是堆内的 
                Execution 内存和堆外的 Execution 内存之和，同理，Storage 内存也一样。相比堆内内存，堆外内存只区分 
                Execution 内存和 Storage 内存，其内存分布如下图所示：

            *Execution 内存和 Storage 内存动态调整
            https://www.iteblog.com/archives/2342.html
                细心的同学肯定看到上面两张图中的 Execution 内存和 Storage 内存之间存在一条虚线，这是为什么呢？
                
                用过 Spark 的同学应该知道，在 Spark 1.5 之前，Execution 内存和 Storage 内存分配是静态的，换句话说就是如果
                 Execution 内存不足，即使 Storage 内存有很大空闲程序也是无法利用到的；反之亦然。这就导致我们很难进行内存的调优工作，
                 我们必须非常清楚地了解 Execution 和 Storage 两块区域的内存分布。而目前 Execution 内存和 Storage 内存可以互相共享的。
                 也就是说，如果 Execution 内存不足，而 Storage 内存有空闲，那么 Execution 可以从 Storage 中申请空间；反之亦然。
                 所以上图中的虚线代表 Execution 内存和 Storage 内存是可以随着运作动态调整的，这样可以有效地利用内存资源。
                 Execution 内存和 Storage 内存之间的动态调整可以概括如下：
                    
                具体的实现逻辑如下：
                
                程序提交的时候我们都会设定基本的 Execution 内存和 Storage 内存区域（通过 spark.memory.storageFraction 参数设置）；
                在程序运行时，如果双方的空间都不足时，则存储到硬盘；将内存中的块存储到磁盘的策略是按照 LRU 规则进行的。
                若己方空间不足而对方空余时，可借用对方的空间;（存储空间不足是指不足以放下一个完整的 Block）
                Execution 内存的空间被对方占用后，可让对方将占用的部分转存到硬盘，然后"归还"借用的空间
                Storage 内存的空间被对方占用后，目前的实现是无法让对方"归还"，因为需要考虑 Shuffle 过程中的很多因素，
                实现起来较为复杂；而且 Shuffle 过程产生的文件在后面一定会被使用到，而 Cache 在内存的数据不一定在后面使用。
                注意，上面说的借用对方的内存需要借用方和被借用方的内存类型都一样，都是堆内内存或者都是堆外内存，
                不存在堆内内存不够去借用堆外内存的空间。
            
            *Task 之间内存分布
                为了更好地使用使用内存，Executor 内运行的 Task 之间共享着 Execution 内存。具体的，Spark 内部维护了一个 HashMap
                 用于记录每个 Task 占用的内存。当 Task 需要在 Execution 内存区域申请 numBytes 内存，其先判断 HashMap 
                 里面是否维护着这个 Task 的内存使用情况，如果没有，则将这个 Task 内存使用置为0，并且以 TaskId 为 key，内存使用为 
                 value 加入到 HashMap 里面。之后为这个 Task 申请 numBytes 内存，如果 Execution 内存区域正好有大于 numBytes 
                 的空闲内存，则在 HashMap 里面将当前 Task 使用的内存加上 numBytes，然后返回；如果当前 Execution 内存区域
                 无法申请到每个 Task 最小可申请的内存，则当前 Task 被阻塞，直到有其他任务释放了足够的执行内存，该任务才可以被唤醒。
                 每个 Task 可以使用 Execution 内存大小范围为 1/2N ~ 1/N，其中 N 为当前 Executor 内正在运行的 Task 个数。一个 
                 Task 能够运行必须申请到最小内存为 (1/2N * Execution 内存)；当 N = 1 的时候，Task 可以使用全部的 Execution 内存。
                
                比如如果 Execution 内存大小为 10GB，当前 Executor 内正在运行的 Task 个数为5，则该 Task 可以申请的内存范围为
                 10 / (2 * 5) ~ 10 / 5，也就是 1GB ~ 2GB的范围。
        
        *shuffle
        https://blog.csdn.net/shujuelin/article/details/84100842    (比较详细,必看)
            Shuffle简介
                Shuffle描述着数据从map task输出到reduce task输入的这段过程。shuffle是连接Map和Reduce之间的桥梁，
                Map的输出要用到Reduce中必须经过shuffle这个环节，shuffle的性能高低直接影响了整个程序的性能和吞吐量。
                因为在分布式情况下，reduce task需要跨节点去拉取其它节点上的map task结果。这一过程将会产生网络资源消耗和内存，
                磁盘IO的消耗。通常shuffle分为两部分：Map阶段的数据准备和Reduce阶段的数据拷贝处理。一般将在map端的Shuffle称之
                为Shuffle Write，在Reduce端的Shuffle称之为Shuffle Read.
            Hadoop MapReduce Shuffle
                Apache Spark 的 Shuffle 过程与 Apache Hadoop 的 Shuffle 过程有着诸多类似，一些概念可直接套用，例如，
                Shuffle 过程中，提供数据的一端，被称作 Map 端，Map 端每个生成数据的任务称为 Mapper，对应的，接收数据的一端，
                被称作 Reduce 端，Reduce 端每个拉取数据的任务称为 Reducer，Shuffle 过程本质上都是将 Map 端获得的数据使用分区器
                进行划分，并将数据发送给对应的 Reducer 的过程。
        
            map端的Shuffle简述:
                1)input, 根据split输入数据，运行map任务;
                2)patition, 每个map task都有一个内存缓冲区，存储着map的输出结果;
                3)spill, 当缓冲区快满的时候需要将缓冲区的数据以临时文件的方式存放到磁盘;
                4)merge, 当整个map task结束后再对磁盘中这个map task产生的所有临时文件做合并，生成最终的正式输出文件，然后等待reduce task来拉数据。
            reduce 端的Shuffle简述:
                reduce task在执行之前的工作就是不断地拉取当前job里每个map task的最终结果，然后对从不同地方拉取过来的数据不断地做merge，也最终形成一个文件作为reduce task的输入文件。
                1) Copy过程，拉取数据。
                2)Merge阶段，合并拉取来的小文件
                3)Reducer计算
                4)Output输出计算结果
        
            什么时候需要 shuffle writer
            https://www.cnblogs.com/itboys/p/9201750.html
                
                中间就涉及到shuffle 过程，前一个stage 的 ShuffleMapTask 进行 shuffle write， 把数据存储在 blockManager 上面，
                 并且把数据位置元信息上报到 driver 的 mapOutTrack 组件中， 下一个 stage 根据数据位置元信息， 进行 shuffle read，
                  拉取上个stage 的输出数据。
                这篇文章讲述的就是其中的 shuffle write 过程。
                
            shuffle 写
                版本二的优点:就是为了减少这么多小文件的生成 
                bucket的数量=cpu*resultTask的个数 
                版本二设计的原理:一个shuffleMapTask还是会写入resultTask对应个数的本地文件，但是当下一个shuffleMapTask运行的时候会直接把数据写到之前已经建立好的本地文件，这个文件可以复用，这种复用机制叫做consolidation机制 
                我们把这一组的shuffle文件称为shuffleGroup,每个文件中都存储了很多shuffleMapTask对应的数据，这个文件叫做segment,这个时候因为不同的shuffleMapTask都是存在一个文件中 
                所以建立索引文件，来标记shuffleMapTask在shuffleBlockFile的位置+偏移量，这样就可以在一个文件里面把不同的shuffleMaptask数据分出来 
                spark shuffle的版本三 
                版本三的优点：是通过排序建立索引，相比较于版本二，它只有一个临时文件，不管有多少个resultTask都只有一个临时文件， 
                缺点:这个排序操作是一个消耗CPU的操作，代价是会消耗很多的cpu 
                版本二占用内存多，打开文件多，但不需排序，速度快。版本三占用内存少，打开文件少，速度相对慢。实践证明使用第二种方案的应用场景更多些。 
                shuffle的读流程 
                
            shuffle 读流程 
                1.有一个类blockManager，封装了临时文件的位置信息,resultTask先通过blockManager,就知道我从哪个节点拿数据 
                如果是远程，它就是发起一次socket请求，创建一个socket链接。然后发起一次远程调用，告诉远程的读取程序，读取哪些数据。读到的内容再通过socket传过来。 
                2.一条条读数据和一块块读数据的优缺点？ 
                如果是一条条读取的话，实时性好，性能低下
                
                一块块读取的话性能高，但是实时性不好
                Shuffle读由reduce这边发起，它需要先到临时文件中读，一般这个临时文件和reduce不在一台节点上，它需要跨网络去读。但也不排除在一台服务器。不论如何它需要知道临时文件的位置， 
                这个是谁来告诉它的呢？它有一个BlockManager的类。这里就知道将来是从本地文件中读取，还是需要从远程服务器上读取。 
                读进来后再做join或者combine的运算。 
                这些临时文件的位置就记录在Map结构中。 
                可以这样理解分区partition是RDD存储数据的地方，实际是个逻辑单位，真正要取数据时，它就调用BlockManage去读，它是以数据块的方式来读。 
                比如一次读取32k还是64k。它不是一条一条读，一条一条读肯定性能低。它读时首先是看本地还是远程，如果是本地就直接读这个文件了， 
                如果是远程，它就是发起一次socket请求，创建一个socket链接。然后发起一次远程调用，告诉远程的读取程序，读取哪些数据。读到的内容再通过socket传过来。
            
            Spark中的shuffle是在干嘛？
                Shuffle在Spark中即是把父RDD中的KV对按照Key重新分区，从而得到一个新的RDD。也就是说原本同属于父RDD同一个分区的数据需要进入到子RDD的不同的分区。
                但这只是shuffle的过程，却不是shuffle的原因。为何需要shuffle呢？
                
            Shuffle和Stage
                在分布式计算框架中，比如map-reduce，数据本地化是一个很重要的考虑，即计算需要被分发到数据所在的位置，从而减少数据的移动，提高运行效率。
                Map-Reduce的输入数据通常是HDFS中的文件，所以数据本地化要求map任务尽量被调度到保存了输入文件的节点执行。但是，
                有一些计算逻辑是无法简单地获取本地数据的，reduce的逻辑都是如此。对于reduce来说，处理函数的输入是key相同的所有value，
                但是这些value所在的数据集(即map的输出)位于不同的节点上，因此需要对map的输出进行重新组织，使得同样的key进入相同的reducer。
                 shuffle移动了大量的数据，对计算、内存、网络和磁盘都有巨大的消耗，因此，只有确实需要shuffle的地方才应该进行shuffle。
                
            Stage的划分
                对于Spark来说，计算的逻辑存在于RDD的转换逻辑中。Spark的调度器也是在依据数据本地化在调度任务，只不过此处的“本地”不仅包括磁盘文件，
                也包括RDD的分区， Spark会使得数据尽量少地被移动，据此，DAGScheduler把一个job划分为多个Stage，在一个Stage内部，
                数据是不需要移动地，数据会在本地经过一系列函数的处理，直至确实需要shuffle的地方。
        
        spark数据读取 
            CSV
            TSV 
                val spark = SparkSession.builder().appName("fileRead").getOrCreate()
                        import spark.implicits._
                        val data1 = spark.read
                            //          推断数据类型
                            .option("inferSchema", true)
                            //          设置空值
                            .option("nullValue", "?")
                            //          表示有表头，若没有则为false
                            .option("header", true)
                            //          文件路径
                            .csv("ds/block_10.csv")
                            //          缓存
                            .cache()
                        //          打印数据格式
                        data1.printSchema()
                        //      显示数据,false参数为不要把数据截断
                        data1.show(false)
                    
            JSON文件
                val jsonpath = "/home/wmx/hive/warehouse/trail/sample40.json"
                val data3 = spark.read.json(jsonpath).cache()
                data3.printSchema()
                // 因为有点多只显示1条，不截断
                data3.show(1,false)
                
                
        列式存储和行式存储相比有哪些优势呢？
            可以跳过不符合条件的数据，只读取需要的数据，降低IO数据量。 
            压缩编码可以降低磁盘存储空间。由于同一列的数据类型是一样的，可以使用更高效的压缩编码（例如Run Length Encoding和Delta Encoding）进一步节约存储空间。 
            只读取需要的列，支持向量运算，能够获取更好的扫描性能。
        
        spark 入门map reduce 最好的几个例子
        https://blog.csdn.net/u013851082/article/details/70142806
        
    广播变量和累加器
        1、能不能将一个RDD使用广播变量广播出去？
               不能，因为RDD是不存储数据的。可以将RDD的结果广播出去。
        2、 广播变量只能在Driver端定义，不能在Executor端定义。
        3、 在Driver端可以修改广播变量的值，在Executor端无法修改广播变量的值。
        4、如果executor端用到了Driver的变量，如果不使用广播变量在Executor有多少task就有多少Driver端的变量副本。
        5、如果Executor端用到了Driver的变量，如果使用广播变量在每个Executor中只有一份Driver端的变量副本。
        如何更新广播变量
        通过unpersist()将老的广播变量删除，然后重新广播一遍新的广播变量
        
        累加器在Driver端定义赋初始值，累加器只能在Driver端读取最后的值，在Excutor端更新。
        
    Spark job 的执行流程简介
        Spark job 的执行流程简介
        我们可以发现，Spark 应用程序在提交执行后，控制台会打印很多日志信息，这些信息看起来是杂乱无章的，但是却在一定程度上体现了一个被提交的 Spark job 在集群中是如何被调度执行的，那么在这一节，将会向大家介绍一个典型的 Spark job 是如何被调度执行的。
        
        我们先来了解以下几个概念：
        
        DAG: 即 Directed Acyclic Graph，有向无环图，这是一个图论中的概念。如果一个有向图无法从某个顶点出发经过若干条边回到该点，则这个图是一个有向无环图。
        
        Job：我们知道，Spark 的计算操作是 lazy 执行的，只有当碰到一个动作 (Action) 算子时才会触发真正的计算。一个 Job 就是由动作算子而产生包含一个或多个 Stage 的计算作业。
        
        Stage：Job 被确定后,Spark 的调度器 (DAGScheduler) 会根据该计算作业的计算步骤把作业划分成一个或者多个 Stage。Stage 又分为 ShuffleMapStage 和 ResultStage，前者以 shuffle 为输出边界，后者会直接输出结果，其边界可以是获取外部数据，也可以是以一个 ShuffleMapStage 的输出为边界。每一个 Stage 将包含一个 TaskSet。
        
        TaskSet： 代表一组相关联的没有 shuffle 依赖关系的任务组成任务集。一组任务会被一起提交到更加底层的 TaskScheduler。
        
        Task：代表单个数据分区上的最小处理单元。分为 ShuffleMapTask 和 ResultTask。ShuffleMapTask 执行任务并把任务的输出划分到 (基于 task 的对应的数据分区) 多个 bucket(ArrayBuffer) 中,ResultTask 执行任务并把任务的输出发送给驱动程序。
        
        Spark 的作业任务调度是复杂的，需要结合源码来进行较为详尽的分析，但是这已经超过本文的范围，所以这一节我们只是对大致的流程进行分析。
        
        Spark 应用程序被提交后，当某个动作算子触发了计算操作时，SparkContext 会向 DAGScheduler 提交一个作业，接着 DAGScheduler 会根据 RDD 生成的依赖关系划分 Stage，并决定各个 Stage 之间的依赖关系，Stage 之间的依赖关系就形成了 DAG。Stage 的划分是以 ShuffleDependency 为依据的，也就是说当某个 RDD 的运算需要将数据进行 Shuffle 时，这个包含了 Shuffle 依赖关系的 RDD 将被用来作为输入信息，进而构建一个新的 Stage。我们可以看到用这样的方式划分 Stage，能够保证有依赖关系的数据可以以正确的顺序执行。根据每个 Stage 所依赖的 RDD 数据的 partition 的分布，会产生出与 partition 数量相等的 Task，这些 Task 根据 partition 的位置进行分布。其次对于 finalStage 或是 mapStage 会产生不同的 Task，最后所有的 Task 会封装到 TaskSet 内提交到 TaskScheduler 去执行。有兴趣的读者可以通过阅读 DAGScheduler 和 TaskScheduler 的源码获取更详细的执行流程。
    
    数据源自并行集合
        调用 SparkContext 的 parallelize 方法，在一个已经存在的 Scala 集合上创建一个 Seq 对象
    
    外部数据源
        Spark支持任何 Hadoop InputFormat 格式的输入，如本地文件、HDFS上的文件、Hive表、HBase上的数据、Amazon S3、Hypertable等，
        以上都可以用来创建RDD。
        常用函数是 sc.textFile() ,参数是Path和最小分区数[可选]。Path是文件的 URI 地址，该地址可以是本地路径，或者 hdfs://、s3n:// 
        等 URL 地址。其次，使用本地文件时，如果在集群上运行要确保worker节点也能访问到文件
    
    提交应用的脚本和可选参数
        可以选择local模式下运行来测试程序，但要是在集群上运行还需要通过spark-submit脚本来完成。官方文档上的示例是这样写的（其中表明哪些是必要参数）：
        
        ./bin/spark-submit \
          --class <main-class> \
          --master <master-url> \
          --deploy-mode <deploy-mode> \
          --conf <key>=<value> \
          ... # other options
          <application-jar> \
          [application-arguments]
        常用参数如下：
        
        --master 参数来设置 SparkContext 要连接的集群，默认不写就是local[*]【可以不用在SparkContext中写死master信息】
        
        --jars 来设置需要添加到 classpath 中的 JAR 包，有多个 JAR 包使用逗号分割符连接
        
        --class 指定程序的类入口
        
        --deploy-mode 指定部署模式，是在 worker 节点（cluster）上还是在本地作为一个外部的客户端（client）部署您的 driver（默认 : client）
        
        这里顺便提一下yarn-client和yarn-cluster区别 cluster-client
        
        application-jar : 包括您的应用以及所有依赖的一个打包的 Jar 的路径。该Jar包的 URL 在您的集群上必须是全局可见的，例如，一个 hdfs:// path 或者一个 file:// path 在所有节点是可见的。
        
        application-arguments : 传递到您的 main class 的 main 方法的参数
        
        driver-memory是 driver 使用的内存，不可超过单机的最大可使用的
        
        num-executors是创建多少个 executor
        
        executor-memory是各个 executor 使用的最大内存，不可超过单机的最大可使用内存
        
        executor-cores是每个 executor 最大可并发执行的 Task 数目
        
        #如下是spark on yarn模式下运行计算Pi的测试程序
        # 有一点务必注意，每行最后换行时务必多敲个空格，否则解析该语句时就是和下一句相连的，不知道会爆些什么古怪的错误
        [hadoop@master spark-2.4.0-bin-hadoop2.6]$ ./bin/spark-submit \
        > --master yarn \
        > --class org.apache.spark.examples.SparkPi \
        > --deploy-mode client \
        > --driver-memory 1g \
        > --num-executors 2 \
        > --executor-memory 2g \
        > --executor-cores 2 \
        > examples/jars/spark-examples_2.11-2.4.0.jar \
        > 10
        每次提交都写这么多肯定麻烦，可以写个脚本
        
        从文件中加载配置
        spark-submit 脚本可以从一个 properties 文件加载默认的 Spark configuration values 并且传递它们到您的应用中去。默认情况下，它将从 Spark 目录下的 conf/spark-defaults.conf 读取配置。更多详细信息，请看 加载默认配置 部分。
        
        加载默认的 Spark 配置，这种方式可以消除某些标记到 spark-submit 的必要性。例如，如果 spark.master 属性被设置了，您可以在 spark-submit 中安全的省略。一般情况下，明确设置在 SparkConf 上的配置值的优先级最高，然后是传递给 spark-submit 的值，最后才是 default value（默认文件）中的值。
        
        如果您不是很清楚其中的配置设置来自哪里，您可以通过使用 --verbose 选项来运行 spark-submit 打印出细粒度的调试信息
        
        更多内容可参考文档：提交应用 ，Spark-Submit 参数设置说明和考虑
        
        配置参数优先级问题
        sparkConf中配置的参数优先级最高，其次是spark-submit脚本中，最后是默认属性文件（spark-defaults.conf）中的配置参数
        
        默认情况下，spark-submit也会从spark-defaults.conf中读取配置
    
    reduceByKey(func, numPartitions=None)
        也就是，reduceByKey用于对每个key对应的多个value进行merge操作，最重要的是它能够在本地先进行merge操作，并且merge操作可以通过函数自定义。
    groupByKey(numPartitions=None)
        也就是，groupByKey也是对每个key进行操作，但只生成一个sequence。需要特别注意“Note”中的话，它告诉我们：如果需要对sequence进行
        aggregation操作（注意，groupByKey本身不能自定义操作函数），那么，选择reduceByKey/aggregateByKey更好。这是因为groupByKey
        不能自定义函数，我们需要先用groupByKey生成RDD，然后才能对此RDD通过map进行自定义函数操作。

    Spark on Yarn模式下的不同之处
        之前提到过on Yarn有yarn-client和yarn-cluster两种模式，在spark-submit脚本中通过--master、--deploy-mode来区分以哪种方式运行 【具体可见：LearningSpark(2)spark-submit可选参数.md】
        其中，官方文档中所提及--deploy-mode 指定部署模式，是在 worker 节点（cluster）上还是在本地作为一个外部的客户端（client）部署您的 driver（默认 : client），这和接下来所提及的内容有关
        因为是运行在Yarn集群上，所有没有什么Master、Worker节点，取而代之是ResourceManager、NodeManager（下文会以RM、NM代替）
        
        yarn-cluster运行模式
            首先spark-submit提交Application后会向RM发送请求，请求启动ApplicationMaster（同standalone模式下的Master，但同时该节点也会运行Drive进程【这里和yarn-client有区别】）。RM就会分配container在某个NM上启动ApplicationMaster
            要执行task就得有Executor，所以ApplicationMaster要向RM申请container来启动Executor。RM分配一些container（就是一些NM节点）给ApplicationMaster用来启动Executor，ApplicationMaster就会连接这些NM（这里NM就如同Worker）。NM启动Executor后向ApplicationMaster注册
        
        yarn-client运行模式
            如上所提的，这种模式的不同在于Driver是部署在本地提交的那台机器上的。过程大致如yarn-cluster，不同在于ApplicationMaster实际上是ExecutorLauncher，而申请到的NodeManager所启动的Executor是要向本地的Driver注册的，而不是向ApplicationMaster注册

    
    *常见面试题   
        RDD五大特性？
            1、RDD是由一系列的分区组成。
            2、操作一个RDD实际上操作的是RDD的所有分区。
            3、RDD之间存在各种依赖关系。
            4、可选的特性，key-value型的RDD是通过hash进行分区。
            5、RDD的每一个分区在计算时会选择最佳的计算位置。
        
        什么是RDD？
            RDD产生的意义在于降低开发分布式应用程序的门槛和提高执行效率。RDD全称resilient distributed dataset（弹性分布式数据集），
            它是一个可以容错的不可变集合，集合中的元素可以进行并行化地处理，Spark是围绕RDDs的概念展开的。RDD可以通过有两种创建的方式，
            一种是通过已经存在的驱动程序中的集合进行创建，另一种是通引用外部存储系统中的数据集进行创建，这里的外部系统可以是像HDFS或HBase
            这样的共享文件系统，也可以是任何支持hadoop InputFormat的数据。
            在源码中，RDD是一个具备泛型的可序列化的抽象类。具备泛型意味着RDD内部存储的数据类型不定，大多数类型的数据都可以存储在RDD之中。
            RDD是一个抽象类则意味着RDD不能直接使用，我们使用的时候通常使用的是它的子类，如HadoopRDD,BlockRDD,JdbcRDD,MapPartitionsRDD,
            CheckpointRDD等。
            
        spark能都取代hadoop?
            Spark是一个计算框架，它没有自己的存储，它的存储还得借助于HDFS，所以说Spark不能取代Hadoop,要取代也是取代MapReduce
        
        Spark的特点？
            Apache Spark 是一个快速的处理大规模数据的通用工具。它是一个基于内存计算框架。它有以下的四个特点：
            1）快速：基于内存的计算比MapReduce快100倍，基于磁盘快10倍。
            2）易用：编写一个spark的应用程序可以使用 Java, Scala, Python, R，这就使得我们的开发非常地灵活。并且，对比于MapReduce,
            spark内置了80多个高级操作，这使得开发十分高效和简单。
            3）运行范围广：spark可以运行在local、yarn、mesos、standalone、kubernetes等多种平台之上。它可以访问诸如HDFS,
             Cassandra, HBase, S3等多种多样的数据源。
            4）通用：spark提供了SparkSQL、SparkStreaming、GraphX、MLlib等一系列的分析工具。
        
        大数据流式处理框架对比：Storm vs Spark Streaming
            a.Spark Streaming最低可在0.5秒~2秒内做一次处理，而Storm最快可达到0.1秒，在实时性和容错性上，Spark Streaming不如Strom.
            b.Spark Streaming的集成性优于Storm,可以通过RDD无缝对接Spark上的所有组件，还可以很容易的与kafka,flume等分布式框架进行集成。
            c.在数据吞吐量上，Spark Streaming要远远优于Storm。
            
            综上所诉，Spark Streaming更适用于大数据流式处理。
        
        cache()和persist()方法的区别？
            cache()在源码底层调用的是persist().

        spark中的join是宽依赖还是债依赖？
            如果JoinAPI之前被调用的RDD API是宽依赖(存在shuffle), 而且两个join的RDD的分区数量一致，join结果的rdd分区数量也一样，这个时候join api是窄依赖
            　　除此之外的，rdd 的join api是宽依赖
            
        Spark中的map和reduce和mapreduce有什么关系？
            首先了解一下Mapreduce，它最本质的两个过程就是Map和Reduce，Map的应用在于我们需要数据一对一的元素的映射转换，
            比如说进行截取，进行过滤，或者任何的转换操作，这些一对一的元素转换就称作是Map；Reduce主要就是元素的聚合，
            就是多个元素对一个元素的聚合，比如求Sum等，这就是Reduce。
            
            其实spark里面也可以实现Mapreduce，但是这里它并不是算法，只是提供了map阶段和reduce阶段，但是在两个阶段提供了很多算法。
            如Map阶段的map, flatMap, filter, keyBy，Reduce阶段的reduceByKey, sortByKey, mean, gourpBy, sort等。
            
        repartition和coalesce区别？
            他们两个都是RDD的分区进行重新划分
            一、repartition只是coalesce接口中shuffle为true的简易实现，（假设RDD有N个分区，需要重新划分成M个分区）
            二、 rdd.coalesce方法的作用是创建CoalescedRDD，
            假设RDD有N个分区，需要重新划分成M个分区
            1）如果N>M并且N和M相差不多，(假如N是1000，M是100)那么就可以将N个分区中的若干个分区合并成一个新的分区，最终合并为M个分区，
            这时可以将shuff设置为false，在shuffl为false的情况下，如果M>N时，coalesce为无效的，不进行shuffle过程，
            父RDD和子RDD之间是窄依赖关系。
            2）如果N>M并且两者相差悬殊，这时如果将shuffle设置为false，父子ＲＤＤ是窄依赖关系，他们同处在一个Stage中，
            就可能造成spark程序的并行度不够，从而影响性能，如果在M为1的时候，为了使coalesce之前的操作有更好的并行度，
            可以讲shuffle设置为true， 会增加一个shuffle的步骤。
            3）、N<M。一般情况下N个分区有数据分布不均匀的状况，利用HashPartitioner函数将数据重新分区为M个，
            这时需要将shuffle设置为true。


        DataFrame vs RDD vs DataSet
            a.基于RDD的编程，不同语言性能是不一样的，而DataFrame是一样的，因为底层会有一个优化器先将代码进行优化。
            b.对于RDD，暴露给执行引擎的信息只有数据的类型，如RDD[Student]装的是Student,而对于DataFrame,对于外部可见
                的信息有字段类型，字段key,字段value等。
            c.RDD是一个数组，DataFrame是一个列式表。

        Spark1.x和2.x的区别。
            在${SPARK_HOME}/jars目录下有许多jar包，而在spark1.0版本中只有一个大的jar包
            
        SparkContext?
            目前在一个JVM进程中可以创建多个SparkContext，但是只能有一个active级别的。如果你需要创建一个新的SparkContext实例，
            必须先调用stop方法停掉当前active级别的SparkContext实例。
            初始化一个SparkContext之前你需要构建一个SparkConf对象，初始化后，就可以使用SparkContext对象所包含的各种方法来创建和
            操作RDD和共享变量，Spark shell会自动初始化一个SparkContext。

        宽依赖和窄依赖？
            窄依赖是指父RDD的一个分区至多被子RDD的分区使用一次。(与数据规模无关)
            宽依赖是指父RDD的一个分区至少被子RDD的分区使用两次。(与数据规模有关)
            窄依赖的函数有：map, filter, union, join(父RDD是hash-partitioned ), mapPartitions, mapValues 
            宽依赖的函数有：xxxByKey, join(父RDD不是hash-partitioned ), partitionBy.
        
        
    行转列和列转行
    https://www.cnblogs.com/ken-jl/p/8570518.html
       

    parquet
    http://www.cnblogs.com/ITtangtang/p/7681019.html
    
    常用的Transformation：
        转换	含义
        map(func)	返回一个新的RDD，该RDD由每一个输入元素经过func函数转换后组成
        filter(func)	返回一个新的RDD，该RDD由经过func函数计算后返回值为true的输入元素组成
        flatMap(func)	类似于map，但是每一个输入元素可以被映射为0或多个输出元素（所以func应该返回一个序列，而不是单一元素）
        mapPartitions(func)	类似于map，但独立地在RDD的每一个分片上运行，因此在类型为T的RDD上运行时，func的函数类型必须是Iterator[T] => Iterator[U]
        mapPartitionsWithIndex(func)	类似于mapPartitions，但func带有一个整数参数表示分片的索引值，因此在类型为T的RDD上运行时，func的函数类型必须是 (Int, Interator[T]) => Iterator[U]
        sample(withReplacement, fraction, seed)	根据fraction指定的比例对数据进行采样，可以选择是否使用随机数进行替换，seed用于指定随机数生成器种子
        union(otherDataset)	对源RDD和参数RDD求并集后返回一个新的RDD
        intersection(otherDataset)	对源RDD和参数RDD求交集后返回一个新的RDD
        distinct([numTasks]))	对源RDD进行去重后返回一个新的RDD
        groupByKey([numTasks])	在一个(K,V)的RDD上调用，返回一个(K, Iterator[V])的RDD
        reduceByKey(func, [numTasks])	在一个(K,V)的RDD上调用，返回一个(K,V)的RDD，使用指定的reduce函数，将相同key的值聚合到一起，与groupByKey类似，reduce任务的个数可以通过第二个可选的参数来设置
        aggregateByKey(zeroValue)(seqOp, combOp, [numTasks])	
        sortByKey([ascending], [numTasks])	在一个(K,V)的RDD上调用，K必须实现Ordered接口，返回一个按照key进行排序的(K,V)的RDD
        sortBy(func,[ascending], [numTasks])	与sortByKey类似，但是更灵活
        join(otherDataset, [numTasks])	在类型为(K,V)和(K,W)的RDD上调用，返回一个相同key对应的所有元素对在一起的(K,(V,W))的RDD
        cogroup(otherDataset, [numTasks])	在类型为(K,V)和(K,W)的RDD上调用，返回一个(K,(Iterable,Iterable))类型的RDD
        cartesian(otherDataset)	笛卡尔积
        pipe(command, [envVars])	
        coalesce(numPartitions)	
        repartition(numPartitions)	
        repartitionAndSortWithinPartitions(partitioner)	
        
        Action
        动作	含义
        reduce(func)	通过func函数聚集RDD中的所有元素，这个功能必须是课交换且可并联的
        collect()	在驱动程序中，以数组的形式返回数据集的所有元素
        count()	返回RDD的元素个数
        first()	返回RDD的第一个元素（类似于take(1)）
        take(n)	返回一个由数据集的前n个元素组成的数组
        takeSample(withReplacement,num, [seed])	返回一个数组，该数组由从数据集中随机采样的num个元素组成，可以选择是否用随机数替换不足的部分，seed用于指定随机数生成器种子
        takeOrdered(n, [ordering])	
        saveAsTextFile(path)	将数据集的元素以textfile的形式保存到HDFS文件系统或者其他支持的文件系统，对于每个元素，Spark将会调用toString方法，将它装换为文件中的文本
        saveAsSequenceFile(path)	将数据集中的元素以Hadoop sequencefile的格式保存到指定的目录下，可以使HDFS或者其他Hadoop支持的文件系统。
        saveAsObjectFile(path)	
        countByKey()	针对(K,V)类型的RDD，返回一个(K,Int)的map，表示每一个key对应的元素个数。
        foreach(func)	在数据集的每一个元素上，运行函数func进行更新。

##drools
    Drools是一款基于Java的开源规则引擎
    　　实现了将业务决策从应用程序中分离出来。
    　　优点：
    　　　　1、简化系统架构，优化应用
    　　　　2、提高系统的可维护性和维护成本
    　　　　3、方便系统的整合
    　　　　4、减少编写“硬代码”业务规则的成本和风险
    
    Drools的基础语法：
        Drl文件内容：
            package：包路径，该路径是逻辑路径(可以随便写，但是不能不写，最好和文件目录同名，以(.)的方式隔开)，规则文件中永远是第一行
        　　rule：规则体，以rule开头，以end结尾，每个文件可以包含多个rule	，规则体分为3个部分：LHS，RHS，属性 三大部分
        　　LHS：(Left Hand Side)，条件部分，在一个规则当中“when”和“then”中间的部分就是LHS部分，在LHS当中，可以包含0~N个条件，如果
        　　　　LHS为空的话，那么引擎会自动添加一个eval(true)的条件，由于该条件总是返回true，所以LHS为空的规则总是返回true。
        　　RHS：(Right Hand Side)，在一个规则中“then”后面的部分就是RHS，只有在LHS的所有条件都满足的情况下，RHS部分才会执行。

    Drools的API调用
        <?xml version="1.0" encoding="UTF-8"?>
        <kmodule xmlns="http://jboss.org/kie/6.0.0/kmodule">
            <kbase name="rules" packages="rules.testword">
                <ksession name="session"/>
            </kbase>
        </kmodule>
        
    规则语言
    　　rule “name”
    　　　　attributes ---->属性
    　　　　when
    　　　　　　LHS ---->条件
    　　　　then
    　　　　　　RHS	---->结果
    　　end
    　　一个规则包含三部分：唯有attributes部分可选，其他都是必填信息
    　　　　定义当前规则执行的一些属性等，比如是否可被重复执行，过期时间，生效时间等
    　　　　LHS：定义当前规则的条件，如 when Message();判断当前workingMemory中是否存在Message对象	
    　　　　RHS：可以写java代码，即当前规则条件满足执行的操作，可以直接调用Fact对象的方法来操作应用

    *Drools 原理
        DRL 解释执行流程
             Drools 规则是在 Java 应用程序上运行的，其要执行的步骤顺序由代码确定。为了实现这一点，Drools 规则引擎将业务规则转换成执行树
            如上图所示，每个规则条件分为小块，在树结构中连接和重用。每次将数据添加到规则引擎中时，它将在与此类似的树中进行求值，
            并到达一个动作节点，在该节点处，它们将被标记为准备执行特定规则的数据。
        
        规则引擎工作方式
            规则引擎默认不会在规则评估时立即执行业务规则，除非我们强制指定。当我们到达一个事实(Fact)与规则相匹配的节点时，
            规则评估会将规则操作与触发数据添加到一个叫作议程(Agenda)的组件中，如果同一个事实(Fact)与多个规则相匹配，
            就认为这些规则是冲突的，议程(Agenda)使用冲突解决策略(Conflict Resolution strategy)管理这些冲突规则的执行顺序。
            整个生命周期中，规则评估与规则执行之间有着明确的分割。规则操作的执行可能会导致事实(Fact)的更新，从而与其它规则相匹配，
            导致它们的触发，称之为前向链接。

        规则引擎虽然非常强大，但并非所有场景都适用。一般来说，规则引擎适用的项目都具有以下一个或多个特征：
            1.存在一个非常复杂的场景，即使对于商业专家也难以完全定义
            2.没有已知或定义明确的算法解决方案
            3.有不稳定需求，需要经常更新
            4.需要快速做出决策，通常是基于部分数据量




##秒杀实现

    实现难点
        1. 超买超卖问题的解决。
        2. 订单持久化，多线程将订单信息写入数据库
    
    进阶方案
        1.访问量还是大。系统还是撑不住。
        2.防止用户刷新页面导致重复提交。
        3.脚本攻击
    
    解决思路：
        1.访问量还是过大的话，要看性能瓶颈在哪里，一般来说首先撑不住的是tomcat，考虑优化tomcat，单个tomcat经过实践并发量撑住1000是没有问题的。
        先搭建tomcat集群，如果瓶颈出现在redis上的话考虑集群redis，这时候消息队列也是必须的，至于采用哪种消息队列框架还是根据实际情况。
        2.问题2和问题3其实属于同一个问题。这个问题其实属于网络问题的范畴，和我们的秒杀系统不在一个层面上。因此不应该由我们来解决。
        很多交换机都有防止一个源IP发起过多请求的功能。开源软件也有不少能实现这点。如linux上的TC可以控制。流行的Web服务器Nginx
        （它也可以看做是一个七层软交换机）也可以通过配置做到这一点。一个IP，一秒钟我就允许你访问我2次，其他软件包直接给你丢了，你还能压垮我吗？
        交换机也不行了呢？
        可能你们的客户并发访问量实在太大了，交换机都撑不住了。 这也有办法。我们可以用多个交换机为我们的秒杀系统服务。
        原理就是DNS可以对一个域名返回多个IP，并且对不同的源IP，同一个域名返回不同的IP。如网通用户访问，就返回一个网通机房的IP；
        电信用户访问，就返回一个电信机房的IP。也就是用CDN了！ 我们可以部署多台交换机为不同的用户服务。 用户通过这些交换机访问后面数据中心的Redis Cluster进行秒杀作业。
    
    
    https://blog.csdn.net/qq_27631217/article/details/80657271  (redis缓存细节)
    
    https://www.jianshu.com/p/d789ea15d060  (整体架构)
    
    *秒杀系统的难点
        首先我们先看下秒杀场景的难点到底在哪？在秒杀场景中最大的问题在于容易产生大并发请求、产生超卖现象和性能问题，下面我们分别分析下下面这三个问题：
        1）瞬时大并发：一提到秒杀系统给人最深刻的印象是超大的瞬时并发，这时你可以联想到小米手机的抢购场景，在小米手机抢购的场景一般都会有
            10w＋的用户同时访问一个商品页面去抢购手机，这就是一个典型的瞬时大并发，如果系统没有经过限流或者熔断处理，那么系统瞬间就会崩掉，就好像被DDos攻击一样；
        2）超卖：秒杀除了大并发这样的难点，还有一个所有电商都会遇到的痛，那就是超卖，电商搞大促最怕什么？最怕的就是超卖，产生超卖了以后会影响
            到用户体验，会导致订单系统、库存系统、供应链等等，产生的问题是一系列的连锁反应，所以电商都不希望超卖发生，但是在大并发的场景最容易
            发生的就是超卖，不同线程读取到的当前库存数据可能下个毫秒就被其他线程修改了，如果没有一定的锁库存机制那么库存数据必然出错，
            都不用上万并发，几十并发就可以导致商品超卖；
        3）性能：当遇到大并发和超卖问题后，必然会引出另一个问题，那就是性能问题，如何保证在大并发请求下，系统能够有好的性能，让用户能够有
            更好的体验，不然每个用户都等几十秒才能知道结果，那体验必然是很糟糕的；
    
    *秒杀系统方案
        从整个秒杀系统的架构其实和一般的互联网系统架构本身没有太多的不同，核心理念还是通过缓存、异步、限流来保证系统的高并发和高可用。
        下面从一笔秒杀交易的流程来描述下秒杀系统架构设计的要点：
        
        
##算法

    实际中，我们一般仅考量算法在最坏情况下的运行情况，也就是对于规模为 n 的任何输入，算法的最长运行时间。这样做的理由是：
        1.一个算法的最坏情况运行时间是在任何输入下运行时间的一个上界（Upper Bound）。
        2.对于某些算法，最坏情况出现的较为频繁。
        3.大体上看，平均情况通常与最坏情况一样差。  
        
    算法分析要保持大局观（Big Idea），其基本思路：
        1.忽略掉那些依赖于机器的常量。
        2.关注运行时间的增长趋势  
        
    使用 O 记号法（Big O Notation）表示最坏运行情况的上界
        线性复杂度 O(n) 表示每个元素都要被处理一次。
        平方复杂度 O(n2) 表示每个元素都要被处理 n 次。
        
        复杂度                |  	标记符号	   |             描述
        -|-|-
        常量（Constant）	     |     O(1)        |  操作的数量为常数，与输入的数据的规模无关。n = 1,000,000 -> 1-2 operations 
        对数（Logarithmic）	 |     O(log2 n)   |  操作的数量与输入数据的规模 n 的比例是 log2 (n)。n = 1,000,000 -> 30 operations
        线性（Linear）	     |     O(n)        |  操作的数量与输入数据的规模 n 成正比。n = 10,000 -> 5000 operations
        平方（Quadratic）	     |     O(n2)	   |  操作的数量与输入数据的规模 n 的比例为二次平方。n = 500 -> 250,000 operations
        立方（Cubic）	         |     O(n3)       |  操作的数量与输入数据的规模 n 的比例为三次方。n = 200 -> 8,000,000 operations
        指数（Exponential）	 |O(2n),O(kn),O(n!)|  指数级的操作，快速的增长。n = 20 -> 1048576 operations
         
       
    注1：快速的数学回忆，logab = y 其实就是 ay = b。所以，log24 = 2，因为 22 = 4。同样 log28 = 3，因为 23 = 8。我们说，log2n 
        的增长速度要慢于 n，因为当 n = 8 时，log2n = 3。
    注2：通常将以 10 为底的对数叫做常用对数。为了简便，N 的常用对数 log10 N 简写做 lg N，例如 log10 5 记做 lg 5。
    注3：通常将以无理数 e 为底的对数叫做自然对数。为了方便，N 的自然对数 loge N 简写做 ln N，例如 loge 3 记做 ln 3。
    注4：在算法导论中，采用记号 lg n = log2 n ，也就是以 2 为底的对数。改变一个对数的底只是把对数的值改变了一个常数倍，所以当
        不在意这些常数因子时，我们将经常采用 "lg n"记号，就像使用 O 记号一样。计算机工作者常常认为对数的底取 2 最自然，因为很多算法和
        数据结构都涉及到对问题进行二分。 

    计算代码块的渐进运行时间的方法有如下步骤：
        1.确定决定算法运行时间的组成步骤。
        2.找到执行该步骤的代码，标记为 1。
        3.查看标记为 1 的代码的下一行代码。如果下一行代码是一个循环，则将标记 1 修改为 1 倍于循环的次数 1 * n。如果包含多个嵌套的循环，
            则将继续计算倍数，例如 1 * n * m。
        4.找到标记到的最大的值，就是运行时间的最大值，即算法复杂度描述的上界。
    
    
    时间复杂度
    https://juejin.im/post/58d15f1044d90400691834d4     (说的比较明白)
        时间频度一个算法执行所耗费的时间，从理论上是不能算出来的，必须上机运行测试才能知道。但我们不可能也没有必要对每个算法都上机测试，
        只需知道哪个算法花费的时间多，哪个算法花费的时间少就可以了。并且一个算法花费的时间与算法中语句的执行次数成正比例，哪个算法中
        语句执行次数多，它花费时间就多。一个算法中的语句执行次数称为语句频度或时间频度。记为T(n)。
        时间复杂度前面提到的时间频度T(n)中，n称为问题的规模，当n不断变化时，时间频度T(n)也会不断变化。但有时我们想知道它变化时呈现什么
        规律，为此我们引入时间复杂度的概念。一般情况下，算法中基本操作重复执行的次数是问题规模n的某个函数，用T(n)表示，若有某个辅助
        函数f(n)，使得当n趋近于无穷大时，T（n)/f(n)的极限值为不等于零的常数，则称f(n)是T(n)的同数量级函数，记作T(n)=O(f(n))，它称
        为算法的渐进时间复杂度，简称时间复杂度。
    
    大O表示法
        像前面用O( )来体现算法时间复杂度的记法，我们称之为大O表示法。算法复杂度可以从最理想情况、平均情况和最坏情况三个角度来评估，
        由于平均情况大多和最坏情况持平，而且评估最坏情况也可以避免后顾之忧，因此一般情况下，我们设计算法时都要直接估算最坏情况的
        复杂度。大O表示法O(f(n)中的f(n)的值可以为1、n、logn、n²等，因此我们可以将O(1)、O(n)、O(logn)、O(n²)分别可以称为常数阶、
        线性阶、对数阶和平方阶，那么如何推导出f(n)的值呢？我们接着来看推导大O阶的方法。
        推导大O阶推导大O阶，我们可以按照如下的规则来进行推导，得到的结果就是大O表示法：
        1.用常数1来取代运行时间中所有加法常数。
        2.修改后的运行次数函数中，只保留最高阶项
        3.如果最高阶项存在且不是1，则去除与这个项相乘的常数。
    
    *常用的时间复杂度按照耗费的时间从小到大依次是：
    O(1)<O(logn)<O(n)<O(nlogn)<O(n²)<O(n³)<O(2ⁿ)<O(n!)
    
    
    
    *八种排序算法
    https://www.jianshu.com/p/5e171281a387  
    
        1.选择排序
            选择排序可以说是最简单的一种排序方法：
            1.找到数组中最小的那个元素
            2.将最小的这个元素和数组中第一个元素交换位置
            3.在剩下的元素中找到最小的的元素，与数组第二个元素交换位置
            时间复杂度O(N^2)
    
        2.冒泡排序：
            据说是八大排序中的其一，通俗的意思就是讲，在一组数据中，相邻元素依次比较大小，最大的放后面，最小的冒上来
            时间复杂度O(N^2)
            
        3.直接插入排序
            1.首先设定插入次数，即循环次数，for(int i=1;i<length;i++)，1个数的那次不用插入。
            2.设定插入数和得到已经排好序列的最后一个数的位数。insertNum和j=i-1。
            3.从最后一个数开始向前循环，如果插入数小于当前数，就将当前数向后移动一位。
            4.将当前数放置到空着的位置，即j+1。
            时间复杂度O(N^2)
            
        4.希尔排序
            https://www.cnblogs.com/edwinchen/p/4782179.html    (原理)
            对于直接插入排序问题，数据量巨大时。
            1.将数的个数设为n，取奇数k=n/2，将下标差值为k的书分为一组，构成有序序列。
            2.再取k=k/2 ，将下标差值为k的书分为一组，构成有序序列。
            3.重复第二步，直到k=1执行简单插入排序。
            
            （1）希尔排序（shell sort）这个排序方法又称为缩小增量排序，是1959年D·L·Shell提出来的。该方法的基本思想是：设待排序元素序列有n个元素，首先取一个整数increment（小于n）作为间隔将全部元素分为increment个子序列，所有距离为increment的元素放在同一个子序列中，在每一个子序列中分别实行直接插入排序。然后缩小间隔increment，重复上述子序列划分和排序工作。直到最后取increment=1，将所有元素放在同一个子序列中排序为止。 
            （2）由于开始时，increment的取值较大，每个子序列中的元素较少，排序速度较快，到排序后期increment取值逐渐变小，子序列中元素个数逐渐增多，但由于前面工作的基础，大多数元素已经基本有序，所以排序速度仍然很快。 
            时间复杂度为O(nlogn)。
            
        5.归并排序
        https://blog.csdn.net/weixin_38108266/article/details/80949412
            速度仅次于快排，内存少的时候使用，可以进行并行计算的时候使用。
            
            1.选择相邻两个数组成一个有序序列。
            2.选择相邻的两个有序序列组成一个有序序列。
            3.重复第二步，直到全部组成一个有序序列。
            时间复杂度为O(nlogn)。
            
            递归：
            	private void MergeSort(int[] a, int p, int r) {
            		if(p<r){
            			int q = (p+r)/2;
            //			p表示从子序列的哪个索引开始，q表示子序列中间的位置
            			MergeSort(a,p,q);
            			MergeSort(a,q+1,r);
            			Merge(a,p,q,r);
            		}
            	
        6.堆排序
        https://www.cnblogs.com/MOBIN/p/5374217.html    (讲得比较清楚)
        https://www.cnblogs.com/MOBIN/p/5374217.html
            对简单选择排序的优化。
            
            1.将序列构建成大顶堆。
            2.将根节点与最后一个节点交换，然后断开最后一个节点。
            3.重复第一、二步，直到所有节点断开。
            
            作为选择排序的改进版，堆排序可以把每一趟元素的比较结果保存下来，以便我们在选择最小/大元素时对已经比较过的元素做出相应的调整。
            堆排序是一种树形选择排序，在排序过程中可以把元素看成是一颗完全二叉树，每个节点都大（小）于它的两个子节点，当每个节点都大于
            等于它的两个子节点时，就称为大顶堆，也叫堆有序； 当每个节点都小于等于它的两个子节点时，就称为小顶堆。

            算法思想(以大顶堆为例)：
            1.将长度为n的待排序的数组进行堆有序化构造成一个大顶堆
            2.将根节点与尾节点交换并输出此时的尾节点
            3.将剩余的n -1个节点重新进行堆有序化
            4.重复步骤2，步骤3直至构造成一个有序序列
            假设待排序数组为[20,50,10,30,70,20,80]
             
    
        7.快速排序
            要求时间最快时。
            选择第一个数为p，小于p的数放在左边，大于p的数放在右边。
            递归的将p左边和右边的数都按照第一步进行，直到不能递归。
            
            基本思想
            随机找出一个数，可以随机取，也可以取固定位置，一般是取第一个或最后一个称为基准，然后就是比基准小的在左边，比基准大的放到右边，
            如何放做，就是和基准进行交换，这样交换完左边都是比基准小的，右边都是比较基准大的，这样就将一个数组分成了两个子数组，
            然后再按照同样的方法把子数组再分成更小的子数组，直到不能分解为止。
            
        8.基数排序
          用于大量数，很长的数进行排序时。
          将所有的数的个位数取出，按照个位数进行排序，构成一个序列。
          将新构成的所有的数的十位数取出，按照十位数进行排序，构成一个序列。
          
    
    
    *查找
    https://blog.csdn.net/weixin_39241397/article/details/79344179  (推荐)
        二分查找
            基本思想：也称为是折半查找，属于有序查找算法。用给定值k先与中间结点的关键字比较，中间结点把线形表分成两个子表，
            若相等则查找成功；若不相等，再根据k与该中间结点关键字的比较结果确定下一步查找哪个子表，这样递归进行，直到查找
            到或查找结束发现表中没有这样的结点。
            望时间复杂度为O(logn)；
            
        二叉查找树（BinarySearch Tree，也叫二叉搜索树，或称二叉排序树Binary Sort Tree）或者是一棵空树，或者是具有下列性质的二叉树：
        
        　　1）若任意节点的左子树不空，则左子树上所有结点的值均小于它的根结点的值；
        　　2）若任意节点的右子树不空，则右子树上所有结点的值均大于它的根结点的值；
        　　3）任意节点的左、右子树也分别为二叉查找树。
        　　二叉查找树性质：对二叉查找树进行中序遍历，即可得到有序的数列。
        
            复杂度分析：它和二分查找一样，插入和查找的时间复杂度均为O(logn)，但是在最坏的情况下仍然会有O(n)的时间复杂度。
            原因在于插入和删除元素的时候，树没有保持平衡（比如，我们查找上图（b）中的“93”，我们需要进行n次查找操作）。我们追求的是在最坏
            的情况下仍然有较好的时间复杂度，这就是平衡查找树设计的初衷。

        平衡查找树之2-3查找树（2-3 Tree）
            基于二叉查找树进行优化，进而可以得到其他的树表查找算法，如平衡树、红黑树等高效算法
        　　2-3查找树定义：和二叉树不一样，2-3树运行每个节点保存1个或者两个的值。对于普通的2节点(2-node)，他保存1个key和左右两个
            自己点。对应3节点(3-node)，保存两个Key，2-3查找树的定义如下：
        　　1）要么为空，要么：
        　　2）对于2节点，该节点保存一个key及对应value，以及两个指向左右节点的节点，左节点也是一个2-3节点，所有的值都比key要小，
                右节点也是一个2-3节点，所有的值比key要大。
        　　3）对于3节点，该节点保存两个key及对应value，以及三个指向左中右的节点。左节点也是一个2-3节点，所有的值均比两个key中
                的最小的key还要小；中间节点也是一个2-3节点，中间节点的key值在两个跟节点key值之间；右节点也是一个2-3节点，节点
                的所有key值比两个key中的最大的key还要大。
        
            2-3查找树的性质：
            　　1）如果中序遍历2-3查找树，就可以得到排好序的序列；
            　　2）在一个完全平衡的2-3查找树中，根节点到每一个为空节点的距离都相同。（这也是平衡树中“平衡”一词的概念，根节点到叶节点的
                最长距离对应于查找算法的最坏情况，而平衡树中根节点到叶节点的距离都一样，最坏情况也具有对数复杂度。）
                
        平衡查找树之红黑树（Red-Black Tree）
        　　2-3查找树能保证在插入元素之后能保持树的平衡状态，最坏情况下即所有的子节点都是2-node，树的高度为lgn，从而保证了最坏情况下
            的时间复杂度。但是2-3树实现起来比较复杂，于是就有了一种简单实现2-3树的数据结构，即红黑树（Red-Black Tree）。
        
        　　基本思想：红黑树的思想就是对2-3查找树进行编码，尤其是对2-3查找树中的3-nodes节点添加额外的信息。红黑树中将节点之间的链接
            分为两种不同类型，红色链接，他用来链接两个2-nodes节点来表示一个3-nodes节点。黑色链接用来链接普通的2-3节点。特别的，使用
            红色链接的两个2-nodes来表示一个3-nodes节点，并且向左倾斜，即一个2-node是另一个2-node的左子节点。这种做法的好处是查找的
            时候不用做任何修改，和普通的二叉查找树相同。  
               
            红黑树的定义：
            　　红黑树是一种具有红色和黑色链接的平衡查找树，同时满足：
                红色节点向左倾斜
                一个节点不可能有两个红色链接
                整个树完全黑色平衡，即从根节点到所以叶子结点的路径上，黑色链接的个数都相同。 
                
            红黑树的性质：整个树完全黑色平衡，即从根节点到所以叶子结点的路径上，黑色链接的个数都相同（2-3树的第2）性质，从根节点到叶子节点的距离都相等）。咋·    
            　　复杂度分析：最坏的情况就是，红黑树中除了最左侧路径全部是由3-node节点组成，即红黑相间的路径长度是全黑路径长度的2倍。
            
            红黑树这种数据结构应用十分广泛，在多种编程语言中被用作符号表的实现，如：
            Java中的java.util.TreeMap,java.util.TreeSet；
                
    二叉树遍历
        //前序遍历递归的方式
               public void preOrder(BinaryTreeNode root){
                   if(null!=root){
                       System.out.print(root.getData()+"\t");
                       preOrder(root.getLeft());
                       preOrder(root.getRight());
                  }
              }
              
              //前序遍历非递归的方式
              public void preOrderNonRecursive(BinaryTreeNode root){
                  Stack<BinaryTreeNode> stack=new Stack<BinaryTreeNode>();
                  while(true){
                      while(root!=null){
                          System.out.print(root.getData()+"\t");
                          stack.push(root);
                          root=root.getLeft();
                      }
                      if(stack.isEmpty()) break;
                      root=stack.pop();
                      root=root.getRight();
                  }
              }
    
    
    B+树
        B+树的定义十分复杂，因此只简要地介绍B+树：B+树是为磁盘或其他直接存取辅助设备而设计的一种平衡查找树，在B+树中，所有记录节点
        都是按键值的大小顺序存放在同一层的叶节点中，各叶节点指针进行连接。      
          
         
          
##设计模式
    总体来说设计模式分为三大类：
        创建型模式，共五种：工厂方法模式、抽象工厂模式、单例模式、建造者模式、原型模式。
        结构型模式，共七种：适配器模式、装饰器模式、代理模式、外观模式、桥接模式、组合模式、享元模式。
        行为型模式，共十一种：策略模式、模板方法模式、观察者模式、迭代子模式、责任链模式、命令模式、备忘录模式、状态模式、访问者模式、
            中介者模式、解释器模式。
        其实还有两类：并发型模式和线程池模式。用一个图片来整体描述一下：
    
    设计模式的六大原则
        总原则：开闭原则（Open Close Principle）
        开闭原则就是说对扩展开放，对修改关闭。在程序需要进行拓展的时候，不能去修改原有的代码，而是要扩展原有代码，实现一个热插拔的效果。
        所以一句话概括就是：为了使程序的扩展性好，易于维护和升级。想要达到这样的效果，我们需要使用接口和抽象类等，后面的具体设计中我们会提到这点。
        
        1、单一职责原则
        不要存在多于一个导致类变更的原因，也就是说每个类应该实现单一的职责，如若不然，就应该把类拆分。
        
        2、里氏替换原则（Liskov Substitution Principle）
        里氏代换原则(Liskov Substitution Principle LSP)面向对象设计的基本原则之一。 里氏代换原则中说，任何基类可以出现的地方，
        子类一定可以出现。 LSP是继承复用的基石，只有当衍生类可以替换掉基类，软件单位的功能不受到影响时，基类才能真正被复用，而衍生类也
        能够在基类的基础上增加新的行为。里氏代换原则是对“开-闭”原则的补充。实现“开-闭”原则的关键步骤就是抽象化。而基类与子类的继承关系
        就是抽象化的具体实现，所以里氏代换原则是对实现抽象化的具体步骤的规范。—— From Baidu 百科
        历史替换原则中，子类对父类的方法尽量不要重写和重载。因为父类代表了定义好的结构，通过这个规范的接口与外界交互，子类不应该随便破坏它。
        
        3、依赖倒转原则（Dependence Inversion Principle）
        这个是开闭原则的基础，具体内容：面向接口编程，依赖于抽象而不依赖于具体。写代码时用到具体类时，不与具体类交互，而与具体类的上
        层接口交互。
        
        4、接口隔离原则（Interface Segregation Principle）
        这个原则的意思是：每个接口中不存在子类用不到却必须实现的方法，如果不然，就要将接口拆分。使用多个隔离的接口，比使用单个接口
        （多个接口方法集合到一个的接口）要好。
        
        5、迪米特法则（最少知道原则）（Demeter Principle）
        就是说：一个类对自己依赖的类知道的越少越好。也就是说无论被依赖的类多么复杂，都应该将逻辑封装在方法的内部，通过public方法提供
        给外部。这样当被依赖的类变化时，才能最小的影响该类。
        最少知道原则的另一个表达方式是：只与直接的朋友通信。类之间只要有耦合关系，就叫朋友关系。耦合分为依赖、关联、聚合、组合等。
        我们称出现为成员变量、方法参数、方法返回值中的类为直接朋友。局部变量、临时变量则不是直接的朋友。我们要求陌生的类不要作为局部变量出现在类中。
        
        6、合成复用原则（Composite Reuse Principle）
        原则是尽量首先使用合成/聚合的方式，而不是使用继承。
    
    设计模式
    http://www.cnblogs.com/geek6/p/3951677.html     (推荐,比较好理解)
        创建型
            静态工厂
            抽象工厂:一般一个抽象工厂里面包括多种维度的工厂,然后抽象工厂进行组装.
            单例模式
            建造者模式
            原型模式的适用环境：
            　　1：创建新对象成本较大（例如初始化时间长，占用CPU多或占太多网络资源），新对象可以通过复制已有对象来获得，如果相似对象，则可以对其成员变量稍作修改。
            　　2：系统要保存对象的状态，而对象的状态很小。
            　　3：需要避免使用分层次的工厂类来创建分层次的对象，并且类的实例对象只有一个或很少的组合状态，通过复制原型对象得到新实例可以比使用构造函数创建一个新实例更加方便。
        
        结构型
            代理
                静态代理    
                    缺点：我们得为每一个服务都得创建代理类，工作量太大，不易管理。同时接口一旦发生改变，代理类也得相应修改。
                动态代理
                    .CGLIB代理 JDK实现动态代理需要实现类通过接口定义业务方法，对于没有接口的类，如何实现动态代理呢，这就需要CGLib了。
                    CGLib采用了非常底层的字节码技术，其原理是通过字节码技术为一个类创建子类，并在子类中采用方法拦截的技术拦截所有父类
                    方法的调用，顺势织入横切逻辑。但因为采用的是继承，所以不能对final修饰的类进行代理。JDK动态代理与CGLib动态代理均
                    是实现Spring AOP的基础。
                CGLIB代理总结： CGLIB创建的动态代理对象比JDK创建的动态代理对象的性能更高，但是CGLIB创建代理对象时所花费的时间却比JDK
                多得多。所以对于单例的对象，因为无需频繁创建对象，用CGLIB合适，反之使用JDK方式要更为合适一些。同时由于CGLib由于是采用
                动态创建子类的方法，对于final修饰的方法无法进行代理。
          
            适配器
                适配器模式的优缺点
                　　优点：
                　　　　1：将目标类和适配者类解耦，通过引入一个适配器类来重用现有的适配者类，无需修改原有结构。
                　　　　2：增加了类的透明性和复用性，将具体的业务实现过程封装在适配者类中，对于客户端类而言是透明的，而且提高了适配者的复用性， 同一适配者类可以在多个不同的系统中复用。
                　　　　3：灵活性和扩展性都非常好，通过使用配置文件，可以很方便的更换适配器，也可以在不修改原有代码的基础上 增加新的适配器，完全复合开闭原则。
                　　缺点：
                　　　　1：一次最多只能适配一个适配者类，不能同时适配多个适配者。
                　　　　2：适配者类不能为最终类，在C#中不能为sealed类
                　　　　3：目标抽象类只能为接口，不能为类，其使用有一定的局限性。
          
            桥接模式
                桥接模式和抽象工厂有点类似
                
            装饰器模式
                和代理模式类似
            
            组合模式（Composite）
                组合模式有时又叫部分-整体模式在处理类似树形结构的问题时比较方便，看看关系图：
                    private String name;  
                    private TreeNode parent;  
                    private Vector<TreeNode> children = new Vector<TreeNode>();  
                使用场景：将多个对象组合在一起进行操作，常用于表示树形结构中，例如二叉树，数等。
                
            享元模式（Flyweight）
                FlyWeightFactory负责创建和管理享元单元，当一个客户端请求时，工厂需要检查当前对象池中是否有符合条件的对象，如果有，
                就返回已经存在的对象，如果没有，则创建一个新对象，FlyWeight是超类。一提到共享池，我们很容易联想到Java里面的JDBC连接池，
                想想每个连接的特点，我们不难总结出：适用于作共享的一些个对象，他们有一些共有的属性，就拿数据库连接池来说，url、
                driverClassName、username、password及dbname，这些属性对于每个连接来说都是一样的，所以就适合用享元模式来处理，
                建一个工厂类，将上述类似属性作为内部数据，其它的作为外部数据，在方法调用时，当做参数传进来，这样就节省了空间，减少了实例的数量。
                
        行为型
            *父类与子类关系
                策略模式（strategy）
                    这个模式涉及到三个角色：
                    　　●　　环境(Context)角色：持有一个Strategy的引用。
                    　　●　　抽象策略(Strategy)角色：这是一个抽象角色，通常由一个接口或抽象类实现。此角色给出所有的具体策略类所需的接口。
                    　　●　　具体策略(ConcreteStrategy)角色：包装了相关的算法或行为。

                模板方法模式（Template Method）
                    解决的问题 提高代码复用性 将相同部分的代码放在抽象的父类中，而将不同的代码放入不同的子类中 实现了反向控制 通过一个父类
                    调用其子类的操作，通过对子类的具体实现扩展不同的行为，实现了反向控制 & 符合“开闭原则”
            
            *类之间的关系
                观察者模式（Observer）
                    
                迭代子模式（Iterator）
                    
                责任链模式（Chain of Responsibility）
                    有多个对象，每个对象持有对下一个对象的引用，这样就会形成一条链，请求在这条链上传递，直到某一对象决定处理该请求。
                    但是发出者并不清楚到底最终那个对象会处理该请求，所以，责任链模式可以实现，在隐瞒客户端的情况下，对系统进行动态的调整。 
                
                    此处强调一点就是，链接上的请求可以是一条链，可以是一个树，还可以是一个环，模式本身不约束这个，需要我们自己去实现，
                    同时，在一个时刻，命令只允许由一个对象传给另一个对象，而不允许传给多个对象。
                    
                命令模式（Command）
                    命令模式很好理解，举个例子，司令员下令让士兵去干件事情，从整个事情的角度来考虑，司令员的作用是，发出口令，
                    口令经过传递，传到了士兵耳朵里，士兵去执行。这个过程好在，三者相互解耦，任何一方都不用去依赖其他人，只需要做好
                    自己的事儿就行，司令员要的是结果，不会去关注到底士兵是怎么实现的
                
                    Invoker是调用者（司令员），Receiver是被调用者（士兵），MyCommand是命令，实现了Command接口，持有接收对象
                    
            *类的状态
                备忘录模式（Memento）
                    主要目的是保存一个对象的某个状态，以便在适当的时候恢复对象，个人觉得叫备份模式更形象些，通俗的讲下：假设有原始类A，
                    A中有各种属性，A可以决定需要备份的属性，备忘录类B是用来存储A的一些内部状态，类C呢，就是一个用来存储备忘录的，
                    且只能存储，不能修改等操作
                    
                    简单描述下：新建原始类时，value被初始化为egg，后经过修改，将value的值置为niu，最后倒数第二行进行恢复状态，
                    结果成功恢复了。其实我觉得这个模式叫“备份-恢复”模式最形象
                
                状态模式（State)
                    根据这个特性，状态模式在日常开发中用的挺多的，尤其是做网站的时候，我们有时希望根据对象的某一属性，
                    区别开他们的一些功能，比如说简单的权限控制等。
                    public class Test {  
                      
                        public static void main(String[] args) {  
                              
                            State state = new State();  
                            Context context = new Context(state);  
                              
                            //设置第一种状态  
                            state.setValue("state1");  
                            context.method();  
                              
                            //设置第二种状态  
                            state.setValue("state2");  
                            context.method();  
                        }  
                    }  
                    
                    public class Context {  
                      
                        private State state;  
                      
                        public Context(State state) {  
                            this.state = state;  
                        }  
                      
                        public State getState() {  
                            return state;  
                        }  
                      
                        public void setState(State state) {  
                            this.state = state;  
                        }  
                      
                        public void method() {  
                            if (state.getValue().equals("state1")) {  
                                state.method1();  
                            } else if (state.getValue().equals("state2")) {  
                                state.method2();  
                            }  
                        }  
                    }  
                    
                    
            *通过中间类
                访问者模式（Visitor）
                    Subject类，accept方法，接受将要访问它的对象，getSubject()获取将要被访问的属性，
                
                中介者模式（Mediator）
                    中介者模式也是用来降低类类之间的耦合的，因为如果类类之间有依赖关系的话，不利于功能的拓展和维护，
                    因为只要修改一个对象，其它关联的对象都得进行修改。如果使用中介者模式，只需关心和Mediator类的关系，
                    具体类类之间的关系及调度交给Mediator就行，这有点像spring容器的作用
                
                解释器模式（Interpreter）
                    public static void main(String[] args) {  
                            // 计算9+2-8的值  
                            int result = new Minus().interpret((new Context(new Plus()  
                                    .interpret(new Context(9, 2)), 8)));  
                            System.out.println(result);  
                    } 
                
                    基本就这样，解释器模式用来做各种各样的解释器，如正则表达式等的解释器等等！
                


##限流、降级、熔断
    https://www.cnblogs.com/tietazhan/p/6249479.html
    
    限流
        一般开发高并发系统常见的限流有：限制总并发数（比如数据库连接池、线程池）、限制瞬时并发数（如nginx的limit_conn模块，
        用来限制瞬时并发连接数）、限制时间窗口内的平均速率（如Guava的RateLimiter、nginx的limit_req模块，限制每秒的平均速率）；
        其他还有如限制远程接口调用速率、限制MQ的消费速率。另外还可以根据网络连接数、网络流量、CPU或内存负载等来限流。
        那么接下来我们从限流算法、应用级限流、分布式限流、接入层限流来详细学习下限流技术手段。
        
        *限流算法
            常见的限流算法有：令牌桶、漏桶。计数器也可以进行粗暴限流实现。
            令牌桶算法
                令牌桶算法是一个存放固定容量令牌的桶，按照固定速率往桶里添加令牌。令牌桶算法的描述如下：
                
                    假设限制2r/s，则按照500毫秒的固定速率往桶中添加令牌；
                    桶中最多存放b个令牌，当桶满时，新添加的令牌被丢弃或拒绝；
                    当一个n个字节大小的数据包到达，将从桶中删除n个令牌，接着数据包被发送到网络上；
                    如果桶中的令牌不足n个，则不会删除令牌，且该数据包将被限流（要么丢弃，要么缓冲区等待）。 
                
            漏桶算法
                漏桶作为计量工具（The Leaky Bucket Algorithm as a Meter）时，可以用于流量整形（Traffic Shaping）和流量控制（TrafficPolicing），漏桶算法的描述如下：
                    一个固定容量的漏桶，按照常量固定速率流出水滴；
                    如果桶是空的，则不需流出水滴；
                    可以以任意速率流入水滴到漏桶；
                    如果流入水滴超出了桶的容量，则流入的水滴溢出了（被丢弃），而漏桶容量是不变的。
                
            令牌桶和漏桶对比：
            
                令牌桶是按照固定速率往桶中添加令牌，请求是否被处理需要看桶中令牌是否足够，当令牌数减为零时则拒绝新的请求；
                漏桶则是按照常量固定速率流出请求，流入请求速率任意，当流入的请求数累积到漏桶容量时，则新流入的请求被拒绝；
                令牌桶限制的是平均流入速率（允许突发请求，只要有令牌就可以处理，支持一次拿3个令牌，4个令牌），并允许一定程度突发流量；
                漏桶限制的是常量流出速率（即流出速率是一个固定常量值，比如都是1的速率流出，而不能一次是1，下次又是2），从而平滑突发流入速率；
                令牌桶允许一定程度的突发，而漏桶主要目的是平滑流入速率；
                两个算法实现可以一样，但是方向是相反的，对于相同的参数得到的限流效果是一样的。
                
                另外有时候我们还使用计数器来进行限流，主要用来限制总并发数，比如数据库连接池、线程池、秒杀的并发数；只要全局总请求数
                或者一定时间段的总请求数设定的阀值则进行限流，是简单粗暴的总数量限流，而不是平均速率限流。
                
                
            应用级限流
                限流总并发/连接/请求数
                
                    对于一个应用系统来说一定会有极限并发/请求数，即总有一个TPS/QPS阀值，如果超了阀值则系统就会不响应用户请求或响应
                    的非常慢，因此我们最好进行过载保护，防止大量请求涌入击垮系统。
                    如果你使用过Tomcat，其Connector其中一种配置有如下几个参数：
                    acceptCount：如果Tomcat的线程都忙于响应，新来的连接会进入队列排队，如果超出排队大小，则拒绝连接；
                    maxConnections：瞬时最大连接数，超出的会排队等待；
                    maxThreads：Tomcat能启动用来处理请求的最大线程数，如果请求处理量一直远远大于最大线程数则可能会僵死。
                    详细的配置请参考官方文档。另外如MySQL（如max_connections）、Redis（如tcp-backlog）都会有类似的限制连接数的配置。
                
                限流总资源数
                    如果有的资源是稀缺资源（如数据库连接、线程），而且可能有多个系统都会去使用它，那么需要限制应用；可以使用池化技术
                    来限制总资源数：连接池、线程池。比如分配给每个应用的数据库连接是100，那么本应用最多可以使用100个资源，超出了可
                    以等待或者抛异常。
                
                限流某个接口的总并发/请求数
                    如果接口可能会有突发访问情况，但又担心访问量太大造成崩溃，如抢购业务；这个时候就需要限制这个接口的总并发/请求数
                    总请求数了；因为粒度比较细，可以为每个接口都设置相应的阀值。可以使用Java中的AtomicLong进行限流：
                    
                    =================================
                    
                    try {
                        if(atomic.incrementAndGet() > 限流数) {
                            //拒绝请求
                        }
                        //处理请求
                    } finally {
                        atomic.decrementAndGet();
                    }
                    =================================
                    
                    适合对业务无损的服务或者需要过载保护的服务进行限流，如抢购业务，超出了大小要么让用户排队，要么告诉用户没货了，
                    对用户来说是可以接受的。而一些开放平台也会限制用户调用某个接口的试用请求量，也可以用这种计数器方式实现。这种
                    方式也是简单粗暴的限流，没有平滑处理，需要根据实际情况选择使用；
                    
                限流某个接口的时间窗请求数
                    即一个时间窗口内的请求数，如想限制某个接口/服务每秒/每分钟/每天的请求数/调用量。如一些基础服务会被很多其他系统调用，
                    比如商品详情页服务会调用基础商品服务调用，但是怕因为更新量比较大将基础服务打挂，这时我们要对每秒/每分钟的调用量进行
                    限速；一种实现方式如下所示：
                    =================================
                    LoadingCache<Long, AtomicLong> counter =
                            CacheBuilder.newBuilder()
                                    .expireAfterWrite(2, TimeUnit.SECONDS)
                                    .build(new CacheLoader<Long, AtomicLong>() {
                                        @Override
                                        public AtomicLong load(Long seconds) throws Exception {
                                            return new AtomicLong(0);
                                        }
                                    });
                    long limit = 1000;
                    while(true) {
                        //得到当前秒
                        long currentSeconds = System.currentTimeMillis() / 1000;
                        if(counter.get(currentSeconds).incrementAndGet() > limit) {
                            System.out.println("限流了:" + currentSeconds);
                            continue;
                        }
                        //业务处理
                    }
                    =================================
                    我们使用Guava的Cache来存储计数器，过期时间设置为2秒（保证1秒内的计数器是有的），然后我们获取当前时间戳然后取秒数来
                    作为KEY进行计数统计和限流，这种方式也是简单粗暴，刚才说的场景够用了。
                
                平滑限流某个接口的请求数
                    之前的限流方式都不能很好地应对突发请求，即瞬间请求可能都被允许从而导致一些问题；因此在一些场景中需要对突发请求进行
                    整形，整形为平均速率请求处理（比如5r/s，则每隔200毫秒处理一个请求，平滑了速率）。这个时候有两种算法满足我们的场景：
                    令牌桶和漏桶算法。Guava框架提供了令牌桶算法实现，可直接拿来使用。
                    Guava RateLimiter提供了令牌桶算法实现：平滑突发限流(SmoothBursty)和平滑预热限流(SmoothWarmingUp)实现。
                     
                    
            分布式限流
                分布式限流最关键的是要将限流服务做成原子化，而解决方案可以使使用Redis+lua或者nginx+lua技术进行实现，通过这两种
                技术可以实现的高并发和高性能。
                首先我们来使用redis+lua实现时间窗内某个接口的请求数限流，实现了该功能后可以改造为限流总并发/请求数和限制总资源数。
                Lua本身就是一种编程语言，也可以使用它实现复杂的令牌桶或漏桶算法。
                

##安全
    todo
    1.单项三列算法
        MD5
        SHA
    2.对称加密
        DES
    3.非对称加密
        RSA
        HTTPS
    
    
##搜索引擎
    elasticSearch
       
                
                
                
                
                
                