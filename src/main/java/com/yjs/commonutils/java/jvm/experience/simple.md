JVM内存占用情况深入分析，分分钟解开你的疑惑
阿飞的博客  占小狼的博客  昨天
很多同学都问过这个问题，为什么我的Xmx设置4g，但是TOP命令查询RES却占用5G，6G，甚至10G。这个正常吗？也可以说正常，也可以说不正常，怎么判断？笔者今天就要为你解答这个问题，叫你如何分析JVM占用的内存都分配到了哪里，哪些地方合理，哪些地方异常。

内存分布
首先，列举一下一个JVM进程主要占用内存的一些地方：

Young

Old

metaspace

java thread count * Xss

other thread count * stacksize （非Java线程）

Direct memory

native memory

codecache

说明：包括但不限于此。

接下来一步一步验证每个区域占用的内存。并且为了验证这个问题，写了一个工具类，里面有给每个区域分配内存的方法，源码在文末。

JVM参数

运行过程中的JVM参数如下：

-
verbose
:
gc 
-
XX
:+
PrintGCDetails
 
-
Xmx2g
 
-
Xms2g
 
-
Xmn1g
 

-
XX
:
PretenureSizeThreshold
=
2M
 
-
XX
:+
UseConcMarkSweepGC
 
-
XX
:+
UseParNewGC
  

-
XX
:
CMSInitiatingOccupancyFraction
=
90
 
-
XX
:+
UseCMSInitiatingOccupancyOnly
 

-
XX
:
MaxDirectMemorySize
=
512m
 
-
XX
:
MetaspaceSize
=
256m
 
-
XX
:
MaxMetaspaceSize
=
256m

Young+Old
我们先从最简单的堆占用内存开始，即Xmx和Xms参数申明，它包括young和old区。分别分配800M和200M内存，main方法如下：

public
 
static
 
void
 main
(
String
[]
 args
)
 
throws
 
Exception
{

    youngAllocate
(
800
);

    oldAllocate
(
200
);

    
Thread
.
sleep
(
300000
);

}

通过TOP命令查看，RES为1G：

 PID USER      PR  NI  VIRT  RES  SHR S 
%
CPU 
%
MEM    TIME
+
  COMMAND                                                                                   

 
22481
 afei      
20
   
0
 
4366m
 
1.0g
  
11m
 S  
0.5
 
27.0
   
0
:
02.41
 java   

通过jstat命令也能看到，Old和Eden分别占用200M和800M。

这里再增加一个有趣的测试，young和old区分别分配1000M和1000M内存，main方法如下：

public
 
static
 
void
 main
(
String
[]
 args
)
 
throws
 
Exception
{

    youngAllocate
(
1000
);

    oldAllocate
(
1000
);

    
// 为了CMS GC顺利触发，这里需要sleep 5s以上，建议时间长一点，让整个CMS GC顺利完成。

    
Thread
.
sleep
(
300000
);

}

这样就会导致发生一次YGC和一个CMS GC，那么你认为这时候通过TOP命令查看RES结果是多少呢？这时候应该是1.8G，除了S0/S1两个区域，eden和Old区域都写入过数据，而JVM使用过的内存就不会归还给操作系统，除非JVM进程宕机或者重启，这个结论很重要：

PID USER      PR  NI  VIRT  RES  SHR S 
%
CPU 
%
MEM    TIME
+
  COMMAND                                                                                   

 
22707
 afei      
20
   
0
 
4366m
 
1.8g
  
11m
 S  
0.0
 
48.7
   
0
:
00.90
 java

Young+Old+Metaspace
接下来，我们再通过程序在Metaspace中重复加载20w个对象，即metaspace分配200M左右的内存，main方法如下：

public
 
static
 
void
 main
(
String
[]
 args
)
 
throws
 
Exception
{

    youngAllocate
(
1000
);

    oldAllocate
(
1000
);

    metaspaceAllocate
(
200000
);

    
Thread
.
sleep
(
60000
);

}

通过TOP命令查看，RES为2.0G：

PID USER      PR  NI  VIRT  RES  SHR S 
%
CPU 
%
MEM    TIME
+
  COMMAND                                                                                   

 
22781
 afei      
20
   
0
 
4472m
 
2.0g
  
12m
 S  
0.0
 
54.7
   
0
:
07.51
 java 

即前面分析的1.8G+208M（213822/1024），在JVM进程退出时有一行这样的日志：

 
Metaspace
       used 
213822K
,
 capacity 
215618K
,
 committed 
215936K
,
 reserved 
1165312K

Young+Old+Metaspace+DirectMemory
接下来，我们再通过程序给堆外分配400M，main方法如下：

public
 
static
 
void
 main
(
String
[]
 args
)
 
throws
 
Exception
{

    youngAllocate
(
1000
);

    oldAllocate
(
1000
);

    metaspaceAllocate
(
200000
);

    directMemoryAllocate
(
400
);

    
Thread
.
sleep
(
60000
);

}

通过TOP命令查看，RES为2.4G：

  PID USER      PR  NI  VIRT  RES  SHR S 
%
CPU 
%
MEM    TIME
+
  COMMAND                                                                                   

 
23329
 afei      
20
   
0
 
4874m
 
2.4g
  
12m
 S  
0.0
 
65.2
   
0
:
12.67
 java 

Abount DirectMemory
在Java的上下文里，特指通过一组特定的API访问native memory，这组API主要由DirectByteBuffer暴露出来，其底层是通过c的malloc分配内存，API参考：ByteBuffer.allocateDirect(1024)，可以通过MaxDirectMemory限制分配上限。

这部分分配的内存可以通过VisualVM的MBeans查看，但是MBeans默认没有安装，需要我们自己安装。但是由于VisualVM的MBeans默认从https://github.com/visualvm/visualvm.src/releases/download/1.3.9/com-sun-tools-visualvm-modules-mbeans.nbm中下载visualvm插件，而这个路径已经不存在。所以建议去https://github.com/oracle/visualvm/releases上下载对应的版本，然后手动安装这个插件：工具-插件-已下载-添加插件，选择本地已经下载的插件，最后点击安装即可。笔者的JDK8默认下载1.3.9版本，那么就去github上下载1.3.9版本，只需要MBeans这个模块即可：



通过MBeans查看Direct Memory占用内存非常方便：

Young+Old+Metaspace+DirectMemory+线程栈
最后就是线程栈，笔者试图通过启动20个线程，并且设置-Xss10240k，但是并没有达到预期，这里作为一个遗留问题。等笔者哪天搞懂了，再发文说明。

Xss案例

曾经群里有一个朋友就是因为Xss配置相当大导致RES占用13G左右。大概情况是这样，-Xms4g，-Xss40940k，dubbo的provider服务。熟悉dubbo服务同学知道，dubbo服务provider默认采用固定200个线程处理的方式。所以200个线程占用8G，加上4G堆，以及一些其他内存，导致RSS高达13G，恐怖！！！

codecache
这部分内存一般占用比较少，在JVM崩溃的文件hserrpid18480.log中有其内存占用情况：

CodeCache
:
 size
=
245760Kb
 used
=
47868Kb
 max_used
=
47874Kb
 free
=
197891Kb

 bounds 
[
0x00007f00b4de4000
,
 
0x00007f00b7d54000
,
 
0x00007f00c3de4000
]

 total_blobs
=
12973
 nmethods
=
12383
 adapters
=
500

 compilation
:
 enabled

知识总结
HotSpot VM自己在JIT编译器、GC工作等的一些时候都会额外临时分配一些native memory，在JDK类库也有可能会有些功能分配长期存活或者临时的native memory，然后就是各种第三方库的native部分可能分配的native memory。

总之，RES占比异常时，一一排查，不要忽略任何一部分可能消耗的内存。

jvm使用了的内存，即使GC后也不会还给操作系统。

Direct Memory内存查看：如果是JDK 7及以上版本，可以用jconsole或者VisualVM的MBeans窗口查看java.nio.BufferPool.direct属性。

文末福利
最后笔者推荐一个JVM参数-XX:NativeMemoryTracking==[off|summary|detail]，可以窥探一些我们平常不怎么关注的内存占用部分，配置JVM参数后，执行如下命令即可：

jcmd 
23448
 VM
.
native_memory summary

命令执行结果如下：



测试源码
import
 java
.
io
.
File
;

import
 java
.
io
.
FileInputStream
;

import
 java
.
io
.
InputStream
;

import
 java
.
lang
.
reflect
.
Method
;

import
 java
.
nio
.
ByteBuffer
;


/**

 * 每个方法的参数m都是表示对应区间分配多少M内存

 * @author afei

 * @date 2018-09-28

 * @since 1.0.0

 */

public
 
class
 
MemoryTest
 
{

    
private
 
static
 
final
 
int
 _1m 
=
 
1024
*
1024
;


    
private
 
static
 
final
 
long
 THREAD_SLEEP_MS 
=
 
10
*
1000
;


    
public
 
static
 
void
 main
(
String
[]
 args
)
 
throws
 
Exception
{

        youngAllocate
(
1000
);

        oldAllocate
(
1000
);

        metaspaceAllocate
(
200000
);

        directMemoryAllocate
(
400
);

        
// threadStackAllocate(400);

        
Thread
.
sleep
(
60000
);

    
}


    
/**

     * @param count 重复定义的MyCalc对象数量

     */

    
private
 
static
 
void
 metaspaceAllocate
(
int
 count
)
 
throws
 
Exception
 
{

        
System
.
out
.
println
(
"metaspace object count: "
 
+
 count
);


        
Method
 declaredMethod 
=
 
ClassLoader
.
class
.
getDeclaredMethod
(
"defineClass"
,

                
new
 
Class
[]{
String
.
class
,
 
byte
[].
class
,
 
int
.
class
,
 
int
.
class
});

        declaredMethod
.
setAccessible
(
true
);


        
File
 classFile 
=
 
new
 
File
(
"/app/afei/MyCalc.class"
);

        
byte
[]
 bcs 
=
 
new
 
byte
[(
int
)
 classFile
.
length
()];

        
try
(
InputStream
 
is
 
=
 
new
 
FileInputStream
(
classFile
);){

            
// 将文件流读进byte数组

            
while
 
(
is
.
read
(
bcs
)!=-
1
){

            
}

        
}


        
int
 outputCount 
=
 count
/
10
;

        
for
 
(
int
 i
=
1
;
 i
<=
count
;
 i
++){

            
try
 
{

                
// 重复定义MyCalc这个类

                declaredMethod
.
invoke
(

                        
MemoryTest
.
class
.
getClassLoader
(),

                        
new
 
Object
[]{
"MyCalc"
,
 bcs
,
 
0
,
 bcs
.
length
});

            
}
catch
 
(
Throwable
 e
){

                
// 重复定义类会抛出LinkageError: attempted  duplicate class definition for name: "MyCalc"

                
// System.err.println(e.getCause().getLocalizedMessage());

            
}

            
if
 
(
i
>=
outputCount 
&&
 i
%
outputCount
==
0
){

                
System
.
out
.
println
(
"i = "
+
i
);

            
}

        
}

        
System
.
out
.
println
(
"metaspace end"
);

    
}


    
/**

     * @param m 分配多少M direct memory

     */

    
private
 
static
 
void
 directMemoryAllocate
(
int
 m
){

        
System
.
out
.
println
(
"direct memory: "
+
m
+
"m"
);

        
for
 
(
int
 i 
=
 
0
;
 i 
<
 m
;
 i
++)
 
{

            
ByteBuffer
.
allocateDirect
(
_1m
);

        
}

        
System
.
out
.
println
(
"direct memory end"
);

    
}


    
/**

     * @param m 给young区分配多少M的数据

     */

    
private
 
static
 
void
 youngAllocate
(
int
 m
){

        
System
.
out
.
println
(
"young: "
+
m
+
"m"
);

        
for
 
(
int
 i 
=
 
0
;
 i 
<
 m
;
 i
++)
 
{

            
byte
[]
 test 
=
 
new
 
byte
[
_1m
];

        
}

        
System
.
out
.
println
(
"young end"
);

    
}


    
/**

     * 需要配置参数: -XX:PretenureSizeThreshold=2M, 并且结合CMS

     * @param m 给old区分配多少M的数据

     */

    
private
 
static
 
void
 oldAllocate
(
int
 m
){

        
System
.
out
.
println
(
"old:   "
+
m
+
"m"
);

        
for
 
(
int
 i 
=
 
0
;
 i 
<
 m
/
5
;
 i
++)
 
{

            
byte
[]
 test 
=
 
new
 
byte
[
5
*
_1m
];

        
}

        
System
.
out
.
println
(
"old end"
);

    
}


    
// 需要配置参数: -Xss10240k, 这里的实验以失败告终

    
private
 
static
 
void
 threadStackAllocate
(
int
 m
){

        
int
 threadCount 
=
 m
/
10
;

        
System
.
out
.
println
(
"thread stack count:"
+
threadCount
);

        
for
 
(
int
 i 
=
 
0
;
 i 
<
 threadCount
;
 i
++)
 
{

            
new
 
Thread
(()
 
->
 
{

                
System
.
out
.
println
(
"thread name: "
 
+
 
Thread
.
currentThread
().
getName
());

                
try
 
{

                    
while
(
true
)
 
{

                        
Thread
.
sleep
(
THREAD_SLEEP_MS
);

                    
}

                
}
 
catch
 
(
InterruptedException
 e
)
 
{

                    e
.
printStackTrace
();

                
}

            
}).
start
();

        
}

        
System
.
out
.
println
(
"thread stack end:"
+
threadCount
);

    
}

}