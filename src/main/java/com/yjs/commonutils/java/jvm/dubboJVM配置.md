dubbo jvm 配置
    java version "1.6.0_18" Java(TM) SE Runtime Environment (build 1.6.0_18-b07) Java HotSpot(TM) 64-Bit Server VM (build 16.0-b13, mixed mode)	
    
    -server 
    -Xmx2g 
    -Xms2g 
    -Xmn256m 
    -XX:PermSize=128m 
    -Xss256k 
    -XX:+DisableExplicitGC 
    -XX:+UseConcMarkSweepGC 
    -XX:+CMSParallelRemarkEnabled 
    -XX:+UseCMSCompactAtFullCollection 
    -XX:LargePageSizeInBytes=128m 
    -XX:+UseFastAccessorMethods 
    -XX:+UseCMSInitiatingOccupancyOnly 
    -XX:CMSInitiatingOccupancyFraction=70
    
    
    https://www.cnblogs.com/parryyang/p/5750146.html



jvm配置（据说是阿里的）
    2016年12月30日 11:19:17 itegel84 阅读数：1544
    -XX:+CMSClassUnloadingEnabled
    对永久代进行垃圾回收，Hotspot虚拟机中，永久代即方法区
    
    
    -XX:+UseParNewGC
    新生代使用并行垃圾收集器
    
    
    -XX:ParallelGCThreads=4
    并行垃圾收集器的线程数，即同时多少个线程一起进行垃圾回收。最好配置与处理器核心数目相等。
    
    
    -XX:+UseCMSInitiatingOccupancyOnly
    该选项用来命令JVM不基于运行时收集的数据来启动CMS垃圾收集周期。而是基于CMSInitiatingOccupancyFraction选项的值进行每一次CMS收集。
    
    
    -XX:CMSInitiatingOccupancyFraction=80
    当老年代使用率达到80%是，CMS触发
    
    
    -XX:CMSMaxAbortablePrecleanTime=5000
    CMS在执行remark(也有叫做rescan)之前，执行的preclean阶段的控制时间，即CMS在执行remark的时候预清理的时间
    
    
    -XX:+ExplicitGCInvokesConcurrent
    JVM无论什么时候调用系统GC（即代码中调用system.gc(),该方法会默认出发一次full gc），都执行CMS GC，而不是Full GC。
    
    
    -XX:+HeapDumpOnOutOfMemoryError
    JVM发生OOM时，生成一个堆存储快照文件
    
    
    -XX:HeapDumpPath=/home/admin/logs/java.hprof
    OOM快照文件的地址
    
    
    -XX:InitialHeapSize=4294967296
    初始化堆内存容量，单位Byte
    
    
    -XX:MaxHeapSize=4294967296
    最大堆内存容量，单位Byte
    
    
    -XX:MaxDirectMemorySize=1073741824
    直接内存的最大容量
    
    
    -XX:MaxNewSize=2147483648
    新生代容量
    
    
    -XX:NewSize=2147483648
    新生代容量
    
    
    -XX:MaxPermSize=268435456
    永久代容量
    
    
    -XX:PermSize=268435456
    永久代容量
    
    
    -XX:OldPLABSize=16
    
    
    -XX:+PrintGC                 打印GC日志
    -XX:+PrintGCDateStamps
    -XX:+PrintGCDetails
    -XX:+PrintGCTimeStamps
    
    
    -XX:SurvivorRatio=10
    Eden与Survivor的占用比例,例如10表示，一个survivor区占用 1/10 的Eden内存，即1/12的新生代内存，
    新生代有2个survivor，即S0和S1。所以survivor总共是占用新生代内存的 2/12，Eden与新生代的占比则为 10/12
    
    
    -XX:+UseCMSCompactAtFullCollection
    CMS开启碎片整理，CMS默认不会整理堆碎片，因此为了防止堆碎片引起full gc，可以开启该选项
    
    
    -XX:+UseCompressedOops
    启用指针压缩，在64位HotSpot中使用32位指针，默认64位会比32位的内存使用多出1.5倍
    启用CompressOops后，会压缩的对象：1、每个Class的属性指针（静态成员变量），2、每个对象的属性指针，3、普通对象数组的每个元素指针