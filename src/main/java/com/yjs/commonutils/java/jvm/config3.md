Java中堆是JVM所管理的最大的一块内存空间，主要用于存放各种类的实例对象和数组，如下图所示：

这里写图片描述

在Java中，堆被划分成两个不同的区域：年轻代、老年代。年轻代（Young）又被划分为三个区域：Eden、S0、S1。这样划分的目的是为了使JVM能够更好的
管理堆内存中的对象，包括内存的分派以及回收。 
堆是GC收集垃圾的主要区域。GC分为两种：Minor GC、Full GC。

1.年轻代
年轻代用来存放新近创建的对象，尺寸随堆大小的增加和减少而相应的变化，默认值是保持为堆的1/15.可以通过-Xmn参数设置年轻代为固定大小，
也可以通过 -XX:NewRatio 来设置年轻代与年老代的大小比例，年轻代的特点是对象更新速度快，在短时间内产生大量的“死亡对象”。

年轻代的特点是产生大量的死亡对象,并且要是产生连续可用的空间, 所以使用复制清除算法和并行收集器进行垃圾回收.对年轻代的垃圾回收称作初级回收 (minor gc)。

初级回收将年轻代分为三个区域，一个新生代，2个大小相同的复活代，应用程序只能使用一个新生代和复活代，当发生初级回收时，gc挂起程序，然后将新生代
和复活代中的存货对象复制到另外一个复活代中，然后一次性消除新生代和复活代，将原来的非复活代标记成活动复活代。将在指定次数回收后仍然存在的对象
移动到老年代中，初级回收后，得到一个空的可用的新生代。

新生代几乎是所有Java对象出生的地方，即Java对象申请的内存以及存放都是在这个地方。Java中的大部分对象通常不需长久存货，具有朝生夕灭的性质。
当一个对象呗判定为“死亡”的时候，GC就有责任来回收掉这部分对象的内存空间。新生代是GC收集的频繁区域。当对象在Eden出生后，在经过一次Minor GC后，
如果对象还存活，并且能够被另外一块Survivor区域所容纳，则使用复制算法将这些仍然还活着的对象复制到另外一块Survivor区域中，然后清理所使用过的
Eden和Survivor区，并且将这些对象的年龄设置为1，以后对象在Survivor区每经过一次Minor GC，就将对象的年龄加1，当对象的年龄达到某个值时
（默认是15岁）这些对象就会成为老年代。

2.老年代
Full GC 是发生在老年代的垃圾收集动作，所采用的是标记-清除算法。

现实的生活中，老年代的人通常会比新生代的人 “早死”。堆内存中的老年代(Old)不同于这个，老年代里面的对象几乎个个都是在 Survivor 区域中熬过来的，
它们是不会那么容易就 “死掉” 了的。因此，Full GC 发生的次数不会有 Minor GC 那么频繁，并且做一次 Full GC 要比进行一次 Minor GC 的时间更长。
 另外，标记-清除算法收集垃圾的时候会产生许多的内存碎片 ( 即不连续的内存空间 )，此后需要为较大的对象分配内存空间时，若无法找到足够的连续的内存空间，
 就会提前触发一次 GC 的收集动作。

3.永久代
永久代是Hotspot虚拟机特有的概念，是方法区的一种实现，别的JVM都没有这个东西。在Java 8中，永久代被彻底移除，取而代之的是另一块与堆不相连的
本地内存——元空间。 
永久代或者“Perm Gen”包含了JVM需要的应用元数据，这些元数据描述了在应用里使用的类和方法。注意，永久代不是Java堆内存的一部分。永久代存放
JVM运行时使用的类。永久代同样包含了Java SE库的类和方法。永久代的对象在full GC时进行垃圾收集。