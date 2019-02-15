JVM参数MetaspaceSize的误解

前言
昨天谢照东大神在群里提出一个问题：怎么查看Metaspace里具体包含的是什么，起因是他的某个服务设置了

-XX:MetaspaceSize=512m 

-XX:MaxMetaspaceSize=512m

但是通过jstat -gcutil pid查看M的值为98（M的=MU/MC），即Metaspace区的使用量达到了512m*98%。遗憾的是，这个推算是错误的；

推理
以笔者测试环境上某个服务为例，配置了

-XX:MetaspaceSize=256m 

-XX:MaxMetaspaceSize=256m

通过jstat -gcutil pid查看M的值为98.32，即Meta区使用率也达到了98.32%：


然后，再通过jstat -gc 4210 2s 3命名查看，结果如下图所示，计算MU/MC即Meta区的使用率确实达到了98.32%，但是MC，即Metaspace Capacity只有55296k，并不是参数MetaspaceSize指定的256m：


那么-XX:MetaspaceSize=256m的含义到底是什么呢？其实，这个JVM参数是指Metaspace扩容时触发FullGC的初始化阈值，也是最小的阈值。这里有几个要点需要明确：

无论-XX:MetaspaceSize配置什么值，Metaspace的初始容量一定是21807104（约20.8m）；

Metaspace由于使用不断扩容到-XX:MetaspaceSize参数指定的量，就会发生FGC；且之后每次Metaspace扩容都会发生FGC；

如果Old区配置CMS垃圾回收，那么第2点的FGC也会使用CMS算法进行回收；

Meta区容量范围为[20.8m, MaxMetaspaceSize)；

如果MaxMetaspaceSize设置太小，可能会导致频繁FGC，甚至OOM；

任何一个JVM参数的默认值可以通过java -XX:+PrintFlagsFinal -version |grep JVMParamName获取，例如：java -XX:+PrintFlagsFinal -version |grep MetaspaceSize

验证
笔者的环境，服务启动后，MU的值稳定在55296k，那么设置-XX:MetaspaceSize=50m -XX:MaxMetaspaceSize=256m，按照上面的推理，会发生一次CMS GC，事实也确实如此，部分gc日志如下所示：

... ...
12.863: [GC (Allocation Failure) 2018-03-20T11:28:34.733+0800: 12.863: [ParNew: 114680K->10355K(118016K), 0.0222201 secs] 165666K->64213K(249088K), 0.0224408 secs] [Times: user=0.06 sys=0.00, real=0.02 secs] 
[Times: user=0.06 sys=0.00, real=0.02 secs] 
14.813: [GC (Allocation Failure) 2018-03-20T11:28:36.683+0800: 14.813: [ParNew: 115315K->10436K(118016K), 0.0263959 secs] 169173K->68441K(249088K), 0.0266341 secs] 169173K->68441K(249088K), 0.0266341 secs] [Times: user=0.08 sys=0.00, real=0.03 secs] 
14.841: [GC (CMS Initial Mark) [1 CMS-initial-mark: 58004K(131072K)] 70447K(249088K), 0.0055264 secs] [Times: user=0.01 sys=0.00, real=0.00 secs] 
14.847: [CMS-concurrent-mark-start]
14.931: [CMS-concurrent-mark: 0.084/0.084 secs] [Times: user=0.20 sys=0.01, real=0.09 secs] 
14.931: [CMS-concurrent-preclean-start]
14.933: [CMS-concurrent-preclean: 0.002/0.002 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
14.933: [CMS-concurrent-abortable-preclean-start]
15.378: [CMS-concurrent-abortable-preclean: 0.434/0.445 secs] [Times: user=1.47 sys=0.01, real=0.45 secs] 
15.379: [GC (CMS Final Remark) [YG occupancy: 69119 K (118016 K)]2018-03-20T11:28:37.249+0800: 15.379: [GC (CMS Final Remark) 2018-03-20T11:28:37.249+0800: 15.379: [ParNew: 69119K->5190K(118016K), 0.0214183 secs] 127124K->66735K(249088K), 0.0216553 secs] [Times: user=0.06 sys=0.00, real=0.02 secs] 
15.401: [Rescan (parallel) , 0.0066413 secs]2018-03-20T11:28:37.278+0800: 15.407: [weak refs processing, 0.0001017 secs]2018-03-20T11:28:37.278+0800: 15.408: [class unloading, 0.0001017 secs]2018-03-20T11:28:37.278+0800: 15.408: [class unloading, 0.0184354 secs]2018-03-20T11:28:37.296+0800: 15.426: [scrub symbol table, 0.0126010 secs]2018-03-20T11:28:37.309+0800: 15.439: [scrub string table, 0.0020576 secs][1 CMS-remark: 61544K(131072K)] 66735K(249088K), 0.0638636 secs] [Times: user=0.15 sys=0.00, real=0.06 secs] 
15.444: [CMS-concurrent-sweep-start]
15.479: [CMS-concurrent-sweep: 0.035/0.035 secs] [Times: user=0.14 sys=0.00, real=0.04 secs] 
15.479: [CMS-concurrent-reset-start]
15.483: [CMS-concurrent-reset: 0.004/0.004 secs] [Times: user=0.01 sys=0.01, real=0.00 secs]
... ...
通过14.841: [GC (CMS Initial Mark) [1 CMS-initial-mark: 58004K(131072K)] 70447K(249088K), 0.0055264 secs] [Times: user=0.01 sys=0.00, real=0.00 secs]这行日志可知：Old区还远远达不到70%（-XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=70）触发CMS GC的条件。所以，这次CMS GC是Metaspace区扩容达到-XX:MetaspaceSize=50m触发的。

建议
MetaspaceSize和MaxMetaspaceSize设置一样大；

具体设置多大，建议稳定运行一段时间后通过jstat -gc pid确认且这个值大一些，对于大部分项目256m即可。

JDK7的PermSize
JDK8+移除了Perm，引入了Metapsace，它们两者的区别是什么呢？Metasace上面已经总结了，无论-XX:MetaspaceSize和-XX:MaxMetaspaceSize两个参数如何设置，都会从20.8M开始，随着类加载越来越多不断扩容调整，上限是-XX:MaxMetaspaceSize，默认是几乎无穷大。而Perm的话，我们通过配置-XX:PermSize以及-XX:MaxPermSize来控制这块内存的大小，jvm在启动的时候会根据-XX:PermSize初始化分配一块连续的内存块，这样的话，如果-XX:PermSize设置过大，就是一种赤果果的浪费。很明显，Metapsace比Perm好多了^^；

JDK7 Perm 验证如下--设置-XX:PermSize=64m -XX:MaxPermSize=64m，那么PC初始化就是64m：