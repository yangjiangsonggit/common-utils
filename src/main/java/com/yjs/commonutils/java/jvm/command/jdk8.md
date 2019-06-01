## 总结常用、重要的命令
> 假设tomcat进程PID为16818
- 统计进程打开的句柄数:ls /proc/16818/fd |wc -l
- 统计进程打开的线程数:ls /proc/16818/task |wc -l   

- 查看cpu占用率高的线程
    - 查看cpu占用率高的线程：top -Hp 16818    
    - 转换线程id为16进制：printf 0x%x 16819
    - 查看线程堆栈：jstack 16818 | grep 0x64f2
    
    - cat /proc/interrupts 
    
- 进程内存统计
    cat /proc/16818/status
    
- jstack 分析
    - Deadlock：死锁线程，一般指多个线程调用间，进入相互资源占用，导致一直等待无法释放的情况。
    - TIMED_WAITING (parking)”中的 timed_waiting 指等待状态，但这里指定了时间，到达指定的时间后自动退出等待状态；parking指线程处于挂起中。
    - Waiting on condition：等待资源，或等待某个条件的发生。具体原因需结合 stacktrace来分析。
        - 如果堆栈信息明确是应用代码，则证明该线程正在等待资源。一般是大量读取某资源，且该资源采用了资源锁的情况下，线程进入等待状态，等待资源的读取。
        - 又或者，正在等待其他线程的执行等。
        - 如果发现有大量的线程都在处在 Wait on condition，从线程 stack看，正等待网络读写，这可能是一个网络瓶颈的征兆。因为网络阻塞导致线程无法执行。
            - 一种情况是网络非常忙，几乎消耗了所有的带宽，仍然有大量数据等待网络读写；
            - 另一种情况也可能是网络空闲，但由于路由等问题，导致包无法正常的到达。
        - 另外一种出现 Wait on condition的常见情况是该线程在 sleep，等待 sleep的时间到了时候，将被唤醒。
    - Blocked：线程阻塞，是指当前线程执行过程中，所需要的资源长时间等待却一直未能获取到，被容器的线程管理器标识为阻塞状态，可以理解为等待资源超时的线程。
    
-------------------------------------------------------------------


确认是不是内存本身分配过小:jmap -heap 16818

找到最耗内存的对象:jmap -histo 16818 (带上:live则表示先进行一次FGC再统计,如jmap -histo:live16818)

导出内存转储快照:jmap -dump:live,format=b,file=heap.bin 16818 (使用Eclipse mat分析)

统计进程打开的句柄数:ls /proc/16818/fd |wc -l

#统计进程打开的线程数:ls /proc/16818/task |wc -l   top -Hp pid

jstat命令可以查看堆内存各部分的使用量，以及加载类的数量。命令的格式如下：

jstat [-命令选项] [vmid] [间隔时间/毫秒] [查询次数]

注意：使用的jdk版本是jdk8.

类加载统计：
C:\Users\Administrator>jstat -class 2060
Loaded  Bytes  Unloaded  Bytes     Time
 15756 17355.6        0     0.0      11.29
1
2
3
Loaded:加载class的数量
Bytes：所占用空间大小
Unloaded：未加载数量
Bytes:未加载占用空间
Time：时间
编译统计
C:\Users\Administrator>jstat -compiler 2060
Compiled Failed Invalid   Time   FailedType FailedMethod
    9142      1       0     5.01          1 org/apache/felix/resolver/ResolverImpl mergeCandidatePackages
1
2
3
Compiled：编译数量。
Failed：失败数量
Invalid：不可用数量
Time：时间
FailedType：失败类型
FailedMethod：失败的方法
垃圾回收统计
C:\Users\Administrator>jstat -gc 2060
 S0C    S1C    S0U    S1U      EC       EU        OC         OU          MC     MU    CCSC      CCSU   YGC     YGCT    FGC    FGCT     GCT
20480.0 20480.0  0.0   13115.3 163840.0 113334.2  614400.0   436045.7  63872.0 61266.5  0.0    0.0      149    3.440   8      0.295    3.735
1
2
3
S0C：第一个幸存区的大小
S1C：第二个幸存区的大小
S0U：第一个幸存区的使用大小
S1U：第二个幸存区的使用大小
EC：伊甸园区的大小
EU：伊甸园区的使用大小
OC：老年代大小
OU：老年代使用大小
MC：方法区大小
MU：方法区使用大小
CCSC:压缩类空间大小
CCSU:压缩类空间使用大小
YGC：年轻代垃圾回收次数
YGCT：年轻代垃圾回收消耗时间
FGC：老年代垃圾回收次数
FGCT：老年代垃圾回收消耗时间
GCT：垃圾回收消耗总时间
堆内存统计
C:\Users\Administrator>jstat -gccapacity 2060
 NGCMN    NGCMX     NGC     S0C     S1C       EC      OGCMN      OGCMX       OGC         OC          MCMN     MCMX      MC     CCSMN    CCSMX     CCSC    YGC    FGC
204800.0 204800.0 204800.0 20480.0 20480.0 163840.0   614400.0   614400.0   614400.0   614400.0      0.0    63872.0  63872.0      0.0      0.0      0.0    149     8
1
2
3
NGCMN：新生代最小容量
NGCMX：新生代最大容量
NGC：当前新生代容量
S0C：第一个幸存区大小
S1C：第二个幸存区的大小
EC：伊甸园区的大小
OGCMN：老年代最小容量
OGCMX：老年代最大容量
OGC：当前老年代大小
OC:当前老年代大小
MCMN:最小元数据容量
MCMX：最大元数据容量
MC：当前元数据空间大小
CCSMN：最小压缩类空间大小
CCSMX：最大压缩类空间大小
CCSC：当前压缩类空间大小
YGC：年轻代gc次数
FGC：老年代GC次数
新生代垃圾回收统计
C:\Users\Administrator>jstat -gcnew 7172
 S0C    S1C    S0U    S1U   TT MTT  DSS      EC       EU     YGC     YGCT
40960.0 40960.0 25443.1    0.0 15  15 20480.0 327680.0 222697.8     12    0.736
1
2
3
S0C：第一个幸存区大小
S1C：第二个幸存区的大小
S0U：第一个幸存区的使用大小
S1U：第二个幸存区的使用大小
TT:对象在新生代存活的次数
MTT:对象在新生代存活的最大次数
DSS:期望的幸存区大小
EC：伊甸园区的大小
EU：伊甸园区的使用大小
YGC：年轻代垃圾回收次数
YGCT：年轻代垃圾回收消耗时间
新生代内存统计
C:\Users\Administrator>jstat -gcnewcapacity 7172
  NGCMN      NGCMX       NGC      S0CMX     S0C     S1CMX     S1C       ECMX        EC      YGC   FGC
  409600.0   409600.0   409600.0  40960.0  40960.0  40960.0  40960.0   327680.0   327680.0    12     0
1
2
3
NGCMN：新生代最小容量
NGCMX：新生代最大容量
NGC：当前新生代容量
S0CMX：最大幸存1区大小
S0C：当前幸存1区大小
S1CMX：最大幸存2区大小
S1C：当前幸存2区大小
ECMX：最大伊甸园区大小
EC：当前伊甸园区大小
YGC：年轻代垃圾回收次数
FGC：老年代回收次数
老年代垃圾回收统计
C:\Users\Administrator>jstat -gcold 7172
   MC       MU      CCSC     CCSU       OC          OU       YGC    FGC    FGCT     GCT
 33152.0  31720.8      0.0      0.0    638976.0    184173.0     12     0    0.000    0.736
1
2
3
MC：方法区大小
MU：方法区使用大小
CCSC:压缩类空间大小
CCSU:压缩类空间使用大小
OC：老年代大小
OU：老年代使用大小
YGC：年轻代垃圾回收次数
FGC：老年代垃圾回收次数
FGCT：老年代垃圾回收消耗时间
GCT：垃圾回收消耗总时间
老年代内存统计
C:\Users\Administrator>jstat -gcoldcapacity 7172
   OGCMN       OGCMX        OGC         OC       YGC   FGC    FGCT     GCT
   638976.0    638976.0    638976.0    638976.0    12     0    0.000    0.736
1
2
3
OGCMN：老年代最小容量
OGCMX：老年代最大容量
OGC：当前老年代大小
OC：老年代大小
YGC：年轻代垃圾回收次数
FGC：老年代垃圾回收次数
FGCT：老年代垃圾回收消耗时间
GCT：垃圾回收消耗总时间
元数据空间统计
C:\Users\Administrator>jstat -gcmetacapacity 7172
   MCMN       MCMX        MC       CCSMN      CCSMX       CCSC     YGC   FGC    FGCT     GCT
   0.0    33152.0    33152.0        0.0        0.0        0.0    12     0    0.000    0.736
1
2
3
MCMN:最小元数据容量
MCMX：最大元数据容量
MC：当前元数据空间大小
CCSMN：最小压缩类空间大小
CCSMX：最大压缩类空间大小
CCSC：当前压缩类空间大小
YGC：年轻代垃圾回收次数
FGC：老年代垃圾回收次数
FGCT：老年代垃圾回收消耗时间
GCT：垃圾回收消耗总时间
总结垃圾回收统计
C:\Users\Administrator>jstat -gcutil 7172
  S0     S1     E      O      M     CCS    YGC     YGCT    FGC    FGCT     GCT
 62.12   0.00  81.36  28.82  95.68      -     12    0.736     0    0.000    0.736
1
2
3
S0：幸存1区当前使用比例
S1：幸存2区当前使用比例
E：伊甸园区使用比例
O：老年代使用比例
M：元数据区使用比例
CCS：压缩使用比例
YGC：年轻代垃圾回收次数
FGC：老年代垃圾回收次数
FGCT：老年代垃圾回收消耗时间
GCT：垃圾回收消耗总时间
JVM编译方法统计
C:\Users\Administrator>jstat -printcompilation 7172
Compiled  Size  Type Method
    4608     16    1 org/eclipse/emf/common/util/SegmentSequence$SegmentSequencePool$SegmentsAccessUnit reset
1
2
3
Compiled：最近编译方法的数量
Size：最近编译方法的字节码数量
Type：最近编译方法的编译类型。
Method：方法名标识。
--------------------- 
作者：褚金辉 
来源：CSDN 
原文：https://blog.csdn.net/maosijunzi/article/details/46049117?utm_source=copy 
版权声明：本文为博主原创文章，转载请附上博文链接！


###########################



 现实企业级Java开发中，有时候我们会碰到下面这些问题：

OutOfMemoryError，内存不足

内存泄露

线程死锁

锁争用（Lock Contention）

Java进程消耗CPU过高

......

    这些问题在日常开发中可能被很多人忽视（比如有的人遇到上面的问题只是重启服务器或者调大内存，而不会深究问题根源），但能够理解并解决这些问题是Java程序员进阶的必备要求。本文将对一些常用的JVM性能调优监控工具进行介绍，希望能起抛砖引玉之用。本文参考了网上很多资料，难以一一列举，在此对这些资料的作者表示感谢！关于JVM性能调优相关的资料，请参考文末。



A、 jps(Java Virtual Machine Process Status Tool)      

    jps主要用来输出JVM中运行的进程状态信息。语法格式如下：

jps [options] [hostid]
    如果不指定hostid就默认为当前主机或服务器。

    命令行参数选项说明如下：

-q 不输出类名、Jar名和传入main方法的参数
-m 输出传入main方法的参数
-l 输出main类或Jar的全限名
-v 输出传入JVM的参数
   比如下面：

root@ubuntu:/# jps -m -l
2458 org.artifactory.standalone.main.Main /usr/local/artifactory-2.2.5/etc/jetty.xml
29920 com.sun.tools.hat.Main -port 9998 /tmp/dump.dat
3149 org.apache.catalina.startup.Bootstrap start
30972 sun.tools.jps.Jps -m -l
8247 org.apache.catalina.startup.Bootstrap start
25687 com.sun.tools.hat.Main -port 9999 dump.dat
21711 mrf-center.jar

B、 jstack

    jstack主要用来查看某个Java进程内的线程堆栈信息。语法格式如下：

jstack [option] pid
jstack [option] executable core
jstack [option] [server-id@]remote-hostname-or-ip
    命令行参数选项说明如下：

-l long listings，会打印出额外的锁信息，在发生死锁时可以用jstack -l pid来观察锁持有情况
-m mixed mode，不仅会输出Java堆栈信息，还会输出C/C++堆栈信息（比如Native方法）
    jstack可以定位到线程堆栈，根据堆栈信息我们可以定位到具体代码，所以它在JVM性能调优中使用得非常多。下面我们来一个实例找出某个Java进程中最耗费CPU的Java线程并定位堆栈信息，用到的命令有ps、top、printf、jstack、grep。

    第一步先找出Java进程ID，我部署在服务器上的Java应用名称为mrf-center：

root@ubuntu:/# ps -ef | grep mrf-center | grep -v grep
root     21711     1  1 14:47 pts/3    00:02:10 java -jar mrf-center.jar
    得到进程ID为21711，第二步找出该进程内最耗费CPU的线程，可以使用ps -Lfp pid或者ps -mp pid -o THREAD, tid, time或者top -Hp pid，我这里用第三个，输出如下：



    TIME列就是各个Java线程耗费的CPU时间，CPU时间最长的是线程ID为21742的线程，用

printf "%x\n" 21742
    得到21742的十六进制值为54ee，下面会用到。    

    OK，下一步终于轮到jstack上场了，它用来输出进程21711的堆栈信息，然后根据线程ID的十六进制值grep，如下：

root@ubuntu:/# jstack 21711 | grep 54ee
"PollIntervalRetrySchedulerThread" prio=10 tid=0x00007f950043e000 nid=0x54ee in Object.wait() [0x00007f94c6eda000]
    可以看到CPU消耗在PollIntervalRetrySchedulerThread这个类的Object.wait()，我找了下我的代码，定位到下面的代码：

// Idle wait
getLog().info("Thread [" + getName() + "] is idle waiting...");
schedulerThreadState = PollTaskSchedulerThreadState.IdleWaiting;
long now = System.currentTimeMillis();
long waitTime = now + getIdleWaitTime();
long timeUntilContinue = waitTime - now;
synchronized(sigLock) {
	try {
    	if(!halted.get()) {
    		sigLock.wait(timeUntilContinue);
    	}
    } 
	catch (InterruptedException ignore) {
    }
}
    它是轮询任务的空闲等待代码，上面的sigLock.wait(timeUntilContinue)就对应了前面的Object.wait()。


C、 jmap（Memory Map）和jhat（Java Heap Analysis Tool）

    jmap用来查看堆内存使用状况，一般结合jhat使用。

    jmap语法格式如下：

jmap [option] pid
jmap [option] executable core
jmap [option] [server-id@]remote-hostname-or-ip
    如果运行在64位JVM上，可能需要指定-J-d64命令选项参数。

jmap -permstat pid
    打印进程的类加载器和类加载器加载的持久代对象信息，输出：类加载器名称、对象是否存活（不可靠）、对象地址、父类加载器、已加载的类大小等信息，如下图：



   使用jmap -heap pid查看进程堆内存使用情况，包括使用的GC算法、堆配置参数和各代中堆内存使用情况。比如下面的例子：

root@ubuntu:/# jmap -heap 21711
Attaching to process ID 21711, please wait...
Debugger attached successfully.
Server compiler detected.
JVM version is 20.10-b01

using thread-local object allocation.
Parallel GC with 4 thread(s)

Heap Configuration:
   MinHeapFreeRatio = 40
   MaxHeapFreeRatio = 70
   MaxHeapSize      = 2067791872 (1972.0MB)
   NewSize          = 1310720 (1.25MB)
   MaxNewSize       = 17592186044415 MB
   OldSize          = 5439488 (5.1875MB)
   NewRatio         = 2
   SurvivorRatio    = 8
   PermSize         = 21757952 (20.75MB)
   MaxPermSize      = 85983232 (82.0MB)

Heap Usage:
PS Young Generation
Eden Space:
   capacity = 6422528 (6.125MB)
   used     = 5445552 (5.1932830810546875MB)
   free     = 976976 (0.9317169189453125MB)
   84.78829520089286% used
From Space:
   capacity = 131072 (0.125MB)
   used     = 98304 (0.09375MB)
   free     = 32768 (0.03125MB)
   75.0% used
To Space:
   capacity = 131072 (0.125MB)
   used     = 0 (0.0MB)
   free     = 131072 (0.125MB)
   0.0% used
PS Old Generation
   capacity = 35258368 (33.625MB)
   used     = 4119544 (3.9287033081054688MB)
   free     = 31138824 (29.69629669189453MB)
   11.683876009235595% used
PS Perm Generation
   capacity = 52428800 (50.0MB)
   used     = 26075168 (24.867218017578125MB)
   free     = 26353632 (25.132781982421875MB)
   49.73443603515625% used
   ....
    使用jmap -histo[:live] pid查看堆内存中的对象数目、大小统计直方图，如果带上live则只统计活对象，如下：

root@ubuntu:/# jmap -histo:live 21711 | more

 num     #instances         #bytes  class name
----------------------------------------------
   1:         38445        5597736  <constMethodKlass>
   2:         38445        5237288  <methodKlass>
   3:          3500        3749504  <constantPoolKlass>
   4:         60858        3242600  <symbolKlass>
   5:          3500        2715264  <instanceKlassKlass>
   6:          2796        2131424  <constantPoolCacheKlass>
   7:          5543        1317400  [I
   8:         13714        1010768  [C
   9:          4752        1003344  [B
  10:          1225         639656  <methodDataKlass>
  11:         14194         454208  java.lang.String
  12:          3809         396136  java.lang.Class
  13:          4979         311952  [S
  14:          5598         287064  [[I
  15:          3028         266464  java.lang.reflect.Method
  16:           280         163520  <objArrayKlassKlass>
  17:          4355         139360  java.util.HashMap$Entry
  18:          1869         138568  [Ljava.util.HashMap$Entry;
  19:          2443          97720  java.util.LinkedHashMap$Entry
  20:          2072          82880  java.lang.ref.SoftReference
  21:          1807          71528  [Ljava.lang.Object;
  22:          2206          70592  java.lang.ref.WeakReference
  23:           934          52304  java.util.LinkedHashMap
  24:           871          48776  java.beans.MethodDescriptor
  25:          1442          46144  java.util.concurrent.ConcurrentHashMap$HashEntry
  26:           804          38592  java.util.HashMap
  27:           948          37920  java.util.concurrent.ConcurrentHashMap$Segment
  28:          1621          35696  [Ljava.lang.Class;
  29:          1313          34880  [Ljava.lang.String;
  30:          1396          33504  java.util.LinkedList$Entry
  31:           462          33264  java.lang.reflect.Field
  32:          1024          32768  java.util.Hashtable$Entry
  33:           948          31440  [Ljava.util.concurrent.ConcurrentHashMap$HashEntry;
    class name是对象类型，说明如下：

B  byte
C  char
D  double
F  float
I  int
J  long
Z  boolean
[  数组，如[I表示int[]
[L+类名 其他对象
    还有一个很常用的情况是：用jmap把进程内存使用情况dump到文件中，再用jhat分析查看。jmap进行dump命令格式如下：

jmap -dump:format=b,file=dumpFileName pid
    我一样地对上面进程ID为21711进行Dump：

root@ubuntu:/# jmap -dump:format=b,file=/tmp/dump.dat 21711     
Dumping heap to /tmp/dump.dat ...
Heap dump file created
   dump出来的文件可以用MAT、VisualVM等工具查看，这里用jhat查看：

root@ubuntu:/# jhat -port 9998 /tmp/dump.dat
Reading from /tmp/dump.dat...
Dump file created Tue Jan 28 17:46:14 CST 2014
Snapshot read, resolving...
Resolving 132207 objects...
Chasing references, expect 26 dots..........................
Eliminating duplicate references..........................
Snapshot resolved.
Started HTTP server on port 9998
Server is ready.
     注意如果Dump文件太大，可能需要加上-J-Xmx512m这种参数指定最大堆内存，即jhat -J-Xmx512m -port 9998 /tmp/dump.dat。然后就可以在浏览器中输入主机地址:9998查看了：



    上面红线框出来的部分大家可以自己去摸索下，最后一项支持OQL（对象查询语言）。


D、jstat（JVM统计监测工具）

    语法格式如下：

jstat [ generalOption | outputOptions vmid [interval[s|ms] [count]] ]
    vmid是Java虚拟机ID，在Linux/Unix系统上一般就是进程ID。interval是采样时间间隔。count是采样数目。比如下面输出的是GC信息，采样时间间隔为250ms，采样数为4：

root@ubuntu:/# jstat -gc 21711 250 4
 S0C    S1C    S0U    S1U      EC       EU        OC         OU       PC     PU    YGC     YGCT    FGC    FGCT     GCT   
192.0  192.0   64.0   0.0    6144.0   1854.9   32000.0     4111.6   55296.0 25472.7    702    0.431   3      0.218    0.649
192.0  192.0   64.0   0.0    6144.0   1972.2   32000.0     4111.6   55296.0 25472.7    702    0.431   3      0.218    0.649
192.0  192.0   64.0   0.0    6144.0   1972.2   32000.0     4111.6   55296.0 25472.7    702    0.431   3      0.218    0.649
192.0  192.0   64.0   0.0    6144.0   2109.7   32000.0     4111.6   55296.0 25472.7    702    0.431   3      0.218    0.649
    要明白上面各列的意义，先看JVM堆内存布局：



    可以看出：

堆内存 = 年轻代 + 年老代 + 永久代
年轻代 = Eden区 + 两个Survivor区（From和To）
    现在来解释各列含义：

S0C、S1C、S0U、S1U：Survivor 0/1区容量（Capacity）和使用量（Used）
EC、EU：Eden区容量和使用量
OC、OU：年老代容量和使用量
PC、PU：永久代容量和使用量
YGC、YGT：年轻代GC次数和GC耗时
FGC、FGCT：Full GC次数和Full GC耗时
GCT：GC总耗时

E、hprof（Heap/CPU Profiling Tool）

    hprof能够展现CPU使用率，统计堆内存使用情况。

    语法格式如下：

java -agentlib:hprof[=options] ToBeProfiledClass
java -Xrunprof[:options] ToBeProfiledClass
javac -J-agentlib:hprof[=options] ToBeProfiledClass
    完整的命令选项如下：

Option Name and Value  Description                    Default
---------------------  -----------                    -------
heap=dump|sites|all    heap profiling                 all
cpu=samples|times|old  CPU usage                      off
monitor=y|n            monitor contention             n
format=a|b             text(txt) or binary output     a
file=<file>            write data to file             java.hprof[.txt]
net=<host>:<port>      send data over a socket        off
depth=<size>           stack trace depth              4
interval=<ms>          sample interval in ms          10
cutoff=<value>         output cutoff point            0.0001
lineno=y|n             line number in traces?         y
thread=y|n             thread in traces?              n
doe=y|n                dump on exit?                  y
msa=y|n                Solaris micro state accounting n
force=y|n              force output to <file>         y
verbose=y|n            print messages about dumps     y
    来几个官方指南上的实例。

    CPU Usage Sampling Profiling(cpu=samples)的例子：

java -agentlib:hprof=cpu=samples,interval=20,depth=3 Hello
    上面每隔20毫秒采样CPU消耗信息，堆栈深度为3，生成的profile文件名称是java.hprof.txt，在当前目录。 

    CPU Usage Times Profiling(cpu=times)的例子，它相对于CPU Usage Sampling Profile能够获得更加细粒度的CPU消耗信息，能够细到每个方法调用的开始和结束，它的实现使用了字节码注入技术（BCI）：

javac -J-agentlib:hprof=cpu=times Hello.java
    Heap Allocation Profiling(heap=sites)的例子：

javac -J-agentlib:hprof=heap=sites Hello.java
    Heap Dump(heap=dump)的例子，它比上面的Heap Allocation Profiling能生成更详细的Heap Dump信息：

javac -J-agentlib:hprof=heap=dump Hello.java
    虽然在JVM启动参数中加入-Xrunprof:heap=sites参数可以生成CPU/Heap Profile文件，但对JVM性能影响非常大，不建议在线上服务器环境使用。



其他JVM性能调优参考资料：

《Java虚拟机规范》

《Java Performance》

《Trouble Shooting Guide for JavaSE 6 with HotSpot VM》: http://www.oracle.com/technetwork/java/javase/tsg-vm-149989.pdf 

《Effective Java》

VisualVM: http://docs.oracle.com/javase/7/docs/technotes/guides/visualvm/

jConsole: http://docs.oracle.com/javase/1.5.0/docs/guide/management/jconsole.html

Monitoring and Managing JavaSE 6 Applications: http://www.oracle.com/technetwork/articles/javase/monitoring-141801.html

BTrace：https://kenai.com/projects/btrace