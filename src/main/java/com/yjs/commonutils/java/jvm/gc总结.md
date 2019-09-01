逐梦offer -- JVM知识总结
 
4. JVM
4.1 GC
1. 垃圾收集
基础 ： 可达性分析算法 GC ROOTS

复制算法
标记清除
标记整理
分代收集 – 1. 新生代 ; 2.3 老年代
注： Oop Map – 安全点 – 安全区
以下部分内容 来自 这个博主的文章

1. 3种基本算法
标记清除法/标记压缩法、复制收集算法、引用计数法

这里的 引用计数法 因为书中讲解少，所以讲一下：
引用计数法，它的基本原理是，在每个对象中保存该对象的引用计数，当引用发生增减时对计数进行更新。引用计数的增减，一般发生在变量赋值、对象内容更新、函数结束（局部变量不再被引用）等时间点。当一个对象的引用计数变为0时，则说明它将来不会再被引用，因此可以释放相应的内存空间。
缺点：

无法释放循环引用的对象。
《逐梦offer -- JVM知识总结》
必须在引用发生增减时对引用计数做出正确的增减，而如果漏掉了某个增减的话，就会引发很难找到原因的内存错误。引用数忘了增加的话，会对不恰当的对象进行释放；而引用数忘了减少的话，对象会一直残留在内存中，从而导致内存泄漏。
引用计数管理并不适合并行处理: 就如同 ConcurrenHashMap源码分析 中的算法一样，无法在并行情况下对数量进行准确的计算。
2. 3种进阶算法
分代回收
分代回收的目的，正是为了在程序运行期间，将GC所消耗的时间尽量缩短。
分代回收的基本思路，是利用了一般性程序所具备的性质，即大部分对象都会在短时间内成为垃圾，而经过一定时间依然存活的对象往往拥有较长的寿命。
HotSpot 虚拟机中，在新生代用复制算法，老年代使用标记清除/整理算法。
问题：如果存在老生代对象对新生代对象的引用。如果只扫描新生代区域的话，那么从老生代对新生代的引用就不会被检测到。
这样一来，如果一个年轻的对象只有来自老生代对象的引用，就会被误认为已经“死亡”了。
因此，在分代回收中，会对对象的更新进行监视，将从老生代对新生代的引用，
记录在一个叫做记录集 Rset（remembered set）的表中。在执行小回收(Minor Gc)的过程中，这个记录集也作为一个根来对待。

解决方案：在老生代到新生代的引用产生的瞬间，就必须对该引用进行记录，而负责执行这个操作的子程序，需要被嵌入到所有涉及对象更新操作的地方。
这个负责记录引用的子程序是这样工作的。设有两个对象：A和B，当对A的内容进行改写，并加入对B的引用时，
如果①A属于老生代对象，②B属于新生代对象，则将该引用添加到记录集中。
这种检查程序需要对所有涉及修改对象内容的地方进行保护，因此被称为写屏障（Write barrier）。

增量回收
为了维持程序的实时性，不等到GC全部完成，而是将GC操作细分成多个部分逐一执行。这种方式被称为增量回收

并行回收
并行回收的基本原理是，是在原有的程序运行的同时进行GC操作，这一点和增量回收是相似的。
不过，相对于在一个CPU上进行GC任务分割的增量回收来说，并行回收可以利用多CPU的性能，尽可能让这些GC任务并行（同时）进行。

3. Card Table 数据结构
为了支持高频率的新生代的回收，虚拟机使用一种叫做卡表（Card Table）的数据结构.
卡表作为一个比特位的集合，每一个比特位可以用来表示年老代的某一区域中的所有对象是否持有新生代对象的引用。
《逐梦offer -- JVM知识总结》

一、作用
卡表中每一个位表示年老代4K的空间，
卡表记录为 0 的年老代区域没有任何对象指向新生代，
卡表记录为 1 的区域才有对象包含新生代引用，
因此在新生代GC时，只需要扫描卡表位为1所在的年老代空间。使用这种方式，可以大大加快新生代的回收速度。

《逐梦offer -- JVM知识总结》

二、结构
卡表是个单字节数组，每个数组元素对应堆中的一张卡。
每次年老代对象中某个引用新生代的字段发生变化时，Hotspot VM就必须将该卡所对应的卡表元素设置为适当的值，从而将该引用字段所在的卡标记为脏。
如下图：
《逐梦offer -- JVM知识总结》
在Minor GC过程中，垃圾收集器只会在脏卡中扫描查找年老代-新生代引用。

Hotspot VM的字节码解释器和JIT编译器使用写屏障 维护卡表。
写屏障 (Write barrier) 是一小段将卡状态设置为脏的代码。 解释器每次执行更新引用的字节码时，都会执行一段写屏障，JIT编译器在生成更新引用的代码后，也会生成一段写屏障。
虽然写屏障使得应用线程增加了 – 性能开销，但Minor GC变快了许多，整体的垃圾收集效率也提高了许多，通常应用的吞吐量也会有所改善。

4. 评价指标
1、 吞吐量
应用系统的生命周期内，应用程序所花费的时间和系统总运行时间的比值
系统总运行时间=应用程序耗时+GC耗时
2、 垃圾回收器负载
垃圾回收器负载=GC耗时/系统总运行时间
3、 停顿时间
垃圾回收器运行时，应用程序的暂停时间
4、 垃圾回收频率
垃圾回收器多长时间运行一次。一般而言，频率越低越好，通常增大堆空间可以有效降低垃圾回收发生的频率，但是会增加回收时产生的停顿时间。
5、 反应时间
当一个对象成为垃圾后，多长时间内，它所占用的内存空间会被释放掉。

2. 内存分配
1. 基础知识
-Xms 堆大小
-Xmx 可扩展大小
-Xmn 老年代大小
-XX:SurvivorRatio Eden 区与 Survivor 区大小比例
注： surivor 区分为 from 区与 to 区

- 在GC开始的时候，对象只会存在于Eden区和名为“From”的Survivor区，Survivor区“To”是空的。
- 紧接着进行GC，Eden区中所有存活的对象都会被复制到“To”，而在“From”区中，仍存活的对象会根据他们的年龄值来决定去向。
- 年龄达到一定值(年龄阈值，可以通过-XX:MaxTenuringThreshold来设置)的对象会被移动到年老代中，没有达到阈值的对象会被复制到“To”区域
- 经过这次GC后，Eden区和From区已经被清空。这个时候，“From”和“To”会交换他们的角色，也就是新的“To”就是上次GC前的“From”
- 新的“From”就是上次GC前的“To”。
- 不管怎样，都会保证名为To的Survivor区域是空的。Minor GC会一直重复这样的过程，直到“To”区被填满，“To”区被填满之后，会将所有对象移动到年老代中。
大对象直接进入老年代 ：很长的字符串以及数组
长期存活的对象进入老年代 -XX:MaxTenuringThreshold
动态对象年龄判定 ：如果在Survivor 中，相同年龄所有对象的大小总和大于 Survivor 空间的一半， 大于或等于此年龄的对象就可以直接进入老年代。
分配担保机制
检查老年代最大可用连续空间 与 新生代所有对象的总空间 –> yes –> MinorGc
HandlePromotionFailure 是否允许担保失败 –> yes –> 检查老年代最大可用连续空间是否大于历次晋升到老年代对象的平均大小 –> MinorGC
2. Minor GC ，Full GC 触发条件
Minor GC触发条件：当Eden区满时，触发Minor GC。
Full GC触发条件：
（1）调用System.gc时，系统建议执行Full GC，但是不必然执行
（2）老年代空间不足
（3）方法去空间不足
（4）通过Minor GC后进入老年代的平均大小大于老年代的可用内存
（5）由Eden区、From Space区向To Space区复制时，对象大小大于To Space可用内存，则把该对象转存到老年代，且老年代的可用内存小于该对象大小

3. 垃圾收集器
1. CMS (Concurrent Mark Sweep)
4个步骤：
初始标记：标记 GC ROOTS 可以直接关联的对象
并发标记：GC TRACING
重新标记：修正并发标记期间，用户程序继续动作而导致的标记产生变动的那一部分对象的标记记录
并发清除
3个缺点：
对 CPU 资源非常敏感
无法处理浮动垃圾(并发清理阶段，用户线程仍旧在运行，因此一直在产生垃圾，而无法在当次收集中处理掉它们)
产生大量的空间碎片
2. G1 (Garbage-First)
4个特点：

并行与并发： 使用多个 CPU 或 CPU 核心来缩短 Stop-The-World 停顿的时间
分代收集
空间整合： 基于标记-整理算法
可预测的停顿： 可以建立可预测的停顿时间模型，让使用者明确指定在一个长度为 M 毫秒的时间片段内，消耗在垃圾收集上的时间不超过 N 毫秒。
4个步骤：

初始标记
并发标记
最终标记
筛选回收： 首先对各个 Region 的回收价值和成本进行排序，根据用户所期望的 GC 停顿时间来制定回收计划。
G1的GC模式

Young GC：选定所有年轻代里的Region。通过控制年轻代的region个数，即年轻代内存大小，来控制young GC的时间开销。
Mixed GC：选定所有年轻代里的Region，外加根据global concurrent marking统计得出收集收益高的若干老年代Region。在用户指定的开销目标范围内尽可能选择收益高的老年代Region。
注意：Mixed GC不是full GC，它只能回收部分老年代的Region，如果mixed GC实在无法跟上程序分配内存的速度，导致老年代填满无法继续进行Mixed GC，就会使用serial old GC（full GC）来收集整个GC heap。

global concurrent marking:类似CMS，为Mixed GC提供标记服务。
四个过程：

初始标记（initial mark，STW）。它标记了从GC Root开始直接可达的对象。
并发标记（Concurrent Marking）。这个阶段从GC Root开始对heap中的对象标记，标记线程与应用程序线程并行执行，并且收集各个Region的存活对象信息。
最终标记（Remark，STW）。标记那些在并发标记阶段发生变化的对象，将被回收。
清除垃圾（Cleanup）。清除空Region（没有存活对象的），加入到free list。
G1 中的几个重要概念 – 原文链接–美团点评

一、Region

传统的GC收集器将连续的内存空间划分为新生代、老年代和永久代（JDK 8去除了永久代，引入了元空间Metaspace），这种划分的特点是各代的存储地址（逻辑地址，下同）是连续的。

如下图所示：
《逐梦offer -- JVM知识总结》
而G1的各代存储地址是不连续的，每一代都使用了n个不连续的大小相同的Region，每个Region占有一块连续的虚拟内存地址。如下图所示：
《逐梦offer -- JVM知识总结》
在上图中，我们注意到还有一些Region标明了H，它代表Humongous，这表示这些Region存储的是巨大对象（humongous object，H-obj），即大小大于等于region一半的对象。H-obj有如下几个特征：

H-obj直接分配到了old gen (老年代)，防止了反复拷贝移动。
H-obj在global concurrent marking 阶段的 cleanup 和 full GC 阶段回收。
在分配H-obj之前先检查是否超过 initiating heap occupancy percent和the marking threshold, 如果超过的话，就启动 global concurrent marking，为的是提早回收，防止 evacuation failures 和 full GC。
为了减少连续H-objs分配对GC的影响，需要把大对象变为普通的对象，建议增大Region size。
二、SATB

全称是Snapshot-At-The-Beginning，由字面理解，是GC开始时活着的对象的一个快照。它是通过Root Tracing得到的，作用是维持并发GC的正确性。

那么它是怎么维持并发GC的正确性的呢？根据三色标记算法，我们知道对象存在三种状态：

白：对象没有被标记到，标记阶段结束后，会被当做垃圾回收掉。
灰：对象被标记了，但是它的field还没有被标记或标记完。
黑：对象被标记了，且它的所有field也被标记完了。
由于并发阶段的存在，Mutator(更改器和)Garbage Collector线程同时对对象进行修改，就会出现白对象漏标的情况，这种情况发生的前提是：

Mutator赋予一个黑对象该白对象的引用。
Mutator删除了所有从灰对象到该白对象的直接或者间接引用。
对于第一个条件，在并发标记阶段，如果该白对象是new出来的，并没有被灰对象持有，那么它会不会被漏标呢？Region中有两个top-at-mark-start（TAMS）指针，分别为prevTAMS和nextTAMS。在TAMS以上的对象是新分配的，这是一种隐式的标记。
对于在GC时已经存在的白对象，如果它是活着的，它必然会被另一个对象引用，即条件二中的灰对象。如果灰对象到白对象的直接引用或者间接引用被替换了，或者删除了，白对象就会被漏标，从而导致被回收掉，这是非常严重的错误，所以SATB破坏了第二个条件。
也就是说，一个对象的引用被替换时，可以通过 write barrier 将旧引用记录下来。(并没有 看懂在说什么)

SATB也是有副作用的，如果被替换的白对象就是要被收集的垃圾，这次的标记会让它躲过GC，这就是float garbage。因为SATB的做法精度比较低，所以造成的float garbage也会比较多。

三、RSet

全称是Remembered Set，是辅助GC过程的一种结构，典型的空间换时间工具，和Card Table有些类似。
还有一种数据结构也是辅助GC的：Collection Set（CSet），它记录了 GC要收集的Region集合 ，集合里的Region可以是任意年代的。
在GC的时候，对于old->young和old->old的跨代对象引用，只要扫描对应的CSet中的RSet即可。

Rset : 属于points-into结构（谁引用了我的对象）
Card Table : 则是一种points-out（我引用了谁的对象）的结构
G1的RSet是在Card Table的基础上实现的：每个Region会记录下别的Region有指向自己的指针，并标记这些指针分别在哪些Card的范围内。
这个RSet其实是一个Hash Table，Key – 别的Region的起始地址，Value是一个集合 – 里面的元素是Card Table的Index。
《逐梦offer -- JVM知识总结》
这里解释一下 :
上图有三个 Region 。红色代表 Rset ， 灰色大方框代表 Card Table。
Region2 的 Rset2 中有两个 Region 的起始地址，分别指向 Region1 , Region3。 – 代表 Region1 与 Region3 引用了我的对象。
Region1 的 Card Table 位置上，存在一个 对 Region2 的引用。 – 代表 Region1 引用了 Region2 的对象。
Region3 同理。

作用：
在做YGC(Minor GC)的时候，只需要选定young generation region的RSet作为根集，这些RSet记录了old->young的跨代引用，避免了扫描整个old generation。
而mixed gc的时候，old generation中记录了old->old的RSet，young->old的引用由扫描全部young generation region得到，这样也不用扫描全部old generation region。所以RSet的引入大大减少了GC的工作量。

四、Pause Prediction Model

G1 uses a pause prediction model to meet a user-defined pause time target and selects the number of regions to collect based on the specified pause time target.
G1 GC是一个响应时间优先的GC算法，它与CMS最大的不同是，用户可以设定整个GC过程的期望停顿时间，参数‘-XX:MaxGCPauseMillis’指定一个G1收集过程目标停顿时间，默认值200ms。
G1 通过这个模型统计计算出来的历史数据来预测本次收集需要选择的Region数量，从而尽量满足用户设定的目标停顿时间。
停顿预测模型是以衰减标准偏差为理论基础实现的。
这里就不详细介绍了，有兴趣的，可以看 美团大神的文章

4.2 Java 内存
程序计数器
虚拟机栈 ： 局部变量表、操作数栈、动态链接、方法出口
本地方法栈 ： native 方法
堆 ： 所有的对象实例以及数组
方法区 ： 已被加载的类信息、常量、静态变量、即时编译器编译后的代码
运行时常量池 ： 编译期生成的各种字面量和符号引用
直接内存 ： NIO类引入了一种基于通道（channel) 与 缓冲区(buffer) 的 I/O 方式，使用 Native 函数库直接分配堆外内存 ， 通过存储在 Java 堆中的 DirectByteBuffer 对象作为这块内存的引用进行操作。
1. Java 对象的内存布局
对象头 : 哈希码(2bit)-分代年龄(4)、轻量级锁定（标志位 00）、重量级锁定、GC标记、可偏向（标志位 01），补充： 类型指针、数组长度
实例数据 ：
对齐填充
2. OOM 异常
堆溢出： 不断创建对象，并且存在可达路径，不被清除。那么对象在达到最大堆容量限制后就会产生内存溢出
通过 内存映像分析工具 （Eclipse Memory Analyzer） 对 Dump 出来的堆转存储快照进行分析。判断是内存泄漏还是内存溢出。
虚拟机栈与本地方法栈溢出：
如果线程请求的栈深度大于虚拟机所允许的最大深度，将抛出 StackOverFlowError 异常。
如果虚拟机在扩展栈时无法申请到足够的内存空间，则抛出 OutOfMemoryError 异常。
方法区
方法区存放 Class 的相关信息。如果存在大量的类 填满 方法区。则会产生溢出。
通过 动态代理 或 通过 CGLIB 动态生成大量的类，以及大量 JSP与 动态JSP 文件的应用 。
3. OOM 异常的解决
一. 可通过命令定期抓取heap dump或者启动参数OOM时自动抓取heap dump文件。
二. 通过对比多个heap dump，以及heap dump的内容，分析代码找出内存占用最多的地方。
三. 分析占用的内存对象，是否是因为错误导致的内存未及时释放，或者数据过多导致的内存溢出。

4.3 类加载器
1. 类加载过程
加载 ：可以通过自定义类加载器参与
通过一个类的全限定名获取定义此类的二进制字节流
将这个字节流代表的静态存储结构转化为方法区的运行时数据结构
在内存中生成一个代表这个类的 java.lang.Class 对象，作为方法区这个类的各种数据的访问入口
验证
文件格式验证
元数据验证 : 语义校验
字节码验证 ：逻辑校验
符号引用验证 ：发生在解析阶段中，将符号引用转化为直接引用
准备 ： 为类变量分配内存并设置类变量初始值的阶段
解析 : 将符号引用 替换 为直接引用的过程。
初始化 ： () 类构造器 : 将类中的赋值语句与静态代码块合并而成 – () 实例构造器
2. 双亲委派模型
　　启动（Bootstrap）类加载器：采用 C++ 实现，它负责将 /lib下面的核心类库或-Xbootclasspath选项指定的jar包加载到内存中。由于启动类加载器到本地代码的实现，开发者无法直接获取到启动类加载器的引用，所以不允许直接通过引用进行操作。编写自定义类加载器时，如果需要把加载请求委派给启动类加载器，直接使用 null 代替.
　　扩展（Extension）类加载器：扩展类加载器是由Sun的ExtClassLoader（sun.misc.Launcher E x t C l a s s L o a d e r ） 实 现 的 。 它 负 责 将 < J a v a R u n t i m e H o m e > / l i b / e x t 或 者 由 系 统 变 量 − D j a v a . e x t . d i r 指 定 位 置 中 的 类 库 加 载 到 内 存 中 。 开 发 者 可 以 直 接 使 用 标 准 扩 展 类 加 载 器 。 　 　 系 统 （ S y s t e m ） 类 加 载 器 ： 系 统 类 加 载 器 是 由 S u n 的 A p p C l a s s L o a d e r （ s u n . m i s c . L a u n c h e r ” role=”presentation” style=”position: relative;”>ExtClassLoader）实现的。它负责将<JavaRuntimeHome>/lib/ext或者由系统变量−Djava.ext.dir指定位置中的类库加载到内存中。开发者可以直接使用标准扩展类加载器。　　系统（System）类加载器：系统类加载器是由Sun的AppClassLoader（sun.misc.Launcher E x t C l a s s L o a d e r ） 实 现 的 。 它 负 责 将 < J a v a R u n t i m e H o m e > / l i b / e x t 或 者 由 系 统 变 量 − D j a v a . e x t . d i r 指 定 位 置 中 的 类 库 加 载 到 内 存 中 。 开 发 者 可 以 直 接 使 用 标 准 扩 展 类 加 载 器 。 　 　 系 统 （ S y s t e m ） 类 加 载 器 ： 系 统 类 加 载 器 是 由 S u n 的 A p p C l a s s L o a d e r （ s u n . m i s c . L a u n c h e r AppClassLoader）实现的。它负责将系统类路径java -classpath或-Djava.class.path变量所指的目录下的类库加载到内存中。开发者可以直接使用系统类加载器。

工作过程：
如果一个类加载器收到了类的加载的请求，它首先不会自己去尝试加载这个类，而是把这个请求委派给父类加载器去完成。直到顶层的启动类加载器中，当父加载器反馈自己无法完成这个加载请求时，子加载器会尝试自己去加载。

3. 线程上下文类加载器
方便 JNDI 服务：SPI 的接口是 Java 核心库的一部分，是由引导类加载器来加载的；SPI 实现的 Java 类一般是由系统类加载器来加载的。引导类加载器是无法找到 SPI 的实现类的，因为它只加载 Java 的核心库。它也不能代理给系统类加载器，因为它是系统类加载器的祖先类加载器。也就是说，类加载器的代理模式无法解决这个问题。
解决方法：Java 应用的线程的上下文类加载器 默认 就是系统上下文类加载器。在 SPI 接口的代码中使用线程上下文类加载器，就可以成功的加载到 SPI 实现的类。线程上下文类加载器在很多 SPI 的实现中都会用到。
Java默认的线程上下文类加载器是系统类加载器(AppClassLoader)。以下代码摘自sun.misc.Launch的无参构造函数Launch()。
可以通过 java.lang.Thread类 的 setContextClassLoader() 设置。

4. OSGI（open service gataway initiative)
方便执部署的实现。可以在不重启服务器的情况下，对其中的逻辑代码进行更新。
由 父类加载器 与 Bundle 组成 , 每个 Bundle 的功能都是 发布 export 与依赖 import。从而形成复杂的网状结构
原理：
OSGi 中的每个模块都有对应的一个类加载器。它负责加载模块自己包含的 Java 包和类。
当它需要加载 Java 核心库的类时（以 java开头的包和类），它会代理给父类加载器（通常是启动类加载器）来完成。
当它需要加载所导入的 Java 类时，它会代理给导出此 Java 类的模块来完成加载。

5. Tomcat
在双亲委派模型的基础上加入了 Common类加载器，Catalina类加载器，Shared类加载器，WebApp类加载器，Jsp类加载器
Common类加载器， /common 目录 被 Tomcat 与 所以 Web 应用程序共同使用
Catalina类加载器， /server 目录中， 被 Tomcat 使用
Shared类加载器， /shared 目录中 ，被所有 Web 应用程序共同使用
WebApp类加载器，Jsp类加载器 ， /WebApp/WEB-INF 目录中，只能被此 Web 应用程序使用。

4.4 解释器与编译器
1. 编译模式
Mixed Mode – 混合模式
默认为混合模式，解释器与编译器搭配使用。
Interpreted Mode – 解释模式
使用 “-Xint” 参数。只使用解释。
Compiled Mode – 编译模式
使用 “-Xcomp” 参数。 优先采用编译，当编译无法进行时，使用解释。
-version 命令，可以输出显示这三种模式

2. 分层编译(Tiered Compilation)
JDK1.7 中的 Server 模式虚拟机中被作为默认编译策略。

0层，程序解释执行，解释器不开启性能监控功能(Profiling)，可触发第一层编译
1层，也叫C1 编译(下文有解释)，将字节码编译为本地代码，进行简单、可靠的优化
2层，C2编译。
3. OSR编译
因为存在多次执行的循环体，所以触发 OSR 编译，以整个方法 作为编译对象。
发生在方法执行过程中，所以叫( On Stack Replacement ) 方法栈帧还在栈上，方法就被替换了。

4. 编译对象以及触发条件
热点代码的分类：

被多次调用的方法
被多次执行的方法体 – OSR 编译
热点探测(Hot Spot Detection)

基于采样 : 如果周期性的检查各个线程的栈顶，如果发现某个方法经常出现在栈顶，则这个方法就是“热点方法”。
基于计数器 – HotSpot 虚拟机中采用。
原理： 为每个方法建立计数器，统计方法的次数，如果执行次数超过一定的阈值，就认为它是“热点方法”
计数器分类：
方法调用计数器(Invocation Counter) :
统计一段时间内，方法被调用的次数，如果超过时间限度，则将这个方法的调用计数器减少一半，称为衰减
回边计数器(Back Edge Counter) ： 统计一个方法中循环体被执行的次数 – OSR 编译
在字节码中遇到控制流向后跳转的指令，称为回边。
5. 优化措施
hotspot中内嵌有2个JIT编译器，分别为Client Compiler，Server Compiler，但大多数情况下我们称之为C1编译器和C2编译器。

5.1 C1 编译器
client compiler，又称C1编译器，较为轻量，只做少量性能开销比较高的优化，它占用内存较少，适合于桌面交互式应用。
在寄存器分配策略上，JDK6以后采用的为线性扫描寄存器分配算法，其他方面的优化，主要有方法内联、去虚拟化、冗余消除等。

A、方法内联

多个方法调用，执行时要经历多次参数传递，返回值传递及跳转等，C1采用方法内联，把调用到的方法的指令直接植入当前方法中。-XX:+PringInlining来查看方法内联信息，-XX:MaxInlineSize=35控制编译后文件大小。
B、去虚拟化
是指在装载class文件后，进行类层次的分析，如果发现类中的方法只提供一个实现类，那么对于调用了此方法的代码，也可以进行方法内联，从而提升执行的性能。
C、冗余消除
在编译时根据运行时状况进行代码折叠或消除。

5.2 C2 编译器
Server compiler，称为C2编译器，较为重量，采用了大量传统编译优化的技巧来进行优化，占用内存相对多一些，适合服务器端的应用。和C1的不同主要在于寄存器分配策略及优化范围.
寄存器分配策略上C2采用的为传统的图着色寄存器分配算法，由于C2会收集程序运行信息，因此其优化范围更多在于全局优化，不仅仅是一个方块的优化。
收集的信息主要有：分支的跳转/不跳转的频率、某条指令上出现过的类型、是否出现过空值、是否出现过异常等。

逃逸分析(Escape Analysis) 是C2进行很多优化的基础，它根据运行状态来判断方法中的变量是否会被外部读取，如不会则认为此变量是不会逃逸的，那么在编译时会做标量替换、栈上分配和同步消除等优化。
如果证明一个对象不会逃逸到方法或线程之外，则：

- 栈上分配(Stack Allocation) ：确定不会逃逸到**方法外**，让这个对象在栈上分配内存，对象占用的内存空间可以随栈帧的出栈而销毁。
- 同步消除(Synchronization Elimination) ：确定不会逃逸到**线程外**，则无法被其他线程访问，所以可以取消同步措施。
- 标量替换(Scalar Repalcement) : 
    标量(Scalar)指一个数据无法再分解成更小的数据来表示 -- Java 中的原始数据类型
    聚合量(Aggregate)指一个数据可以继续分解 -- Java 中的对象
    **原理：**直接创建若干个可以被方法使用的成员变量来替代。
5.3 其他措施（注： 不知是 C1 还是 C2)
语言无关的经典优化技术 – 公共子表达式消除(Common Subexpression Elimination)
如果一个表达式E 已经计算过，并且从先前的计算 到现在 值未曾改变，那么如果 E 再次出现，则可以直接使用之前的表达式结果，代替 E 。

语言相关的经典优化技术 – 数组边界检查消除(Array Bounds Checking Elimination)
这个不是很了解，做一个重点。。。 以后整理
4. 零散知识点
1. 静态多分派与动态单分派
静态分派 ： 依靠静态类型 定位方法。
编译阶段：Human man = new Man(); // 静态类型为 Human
运行阶段：man.sayHello() // 动态类型为 Man

重载的优先级
sayHello(char arg);
char -> int -> long -> float -> double // 不可转化为 byte short ， 因为char 转化是不安全的。
-> Character -> Serializable/Comparable -> Object -> char…(变长参数)

宗量：方法的接收者与方法的参数统称为宗量
单分派 根据一个宗量对目标方法进行选择
多分派 根据多个宗量对目标方法进行选择

public class QQ{};
public class _360{};
public static class Father {
    public void hardChoice(QQ arg);
    public void hardChoice(_360 arg);
}
public static class Son extends Father{
    public void hardChoice(QQ arg);
    public void hardChoice(_360 arg);
}
Father father = new Father();
Father son = new Son();
// 静态多分派 - 编译 ： 方法的接收者 Father - Son, 参数 QQ - _360
father.hardChoice(_360);
// 动态多分派 - 运行 ： 已经确定 参数为 QQ ，再判断 实际类型 , son的实际类型为 Son 。
son.hardChoice(QQ);
结语
都看到这里了，点个关注好不啦。
你的关注，是我最大的动力哦。
不定期干货更新。
一只相当程序员的1米88处女座大可爱。