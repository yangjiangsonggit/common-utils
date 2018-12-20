1.由于jdk8开始，没有了永久区的概念，所以在jvm参数配置上不再需要

-XX:PermSize

-XX:MaxPermSize

的配置了

2.metaspace，元数据空间，专门用来存元数据的，它是jdk8里特有的数据结构用来替代perm

相关的参数有两个CompressedClassSpaceSize和MaxMetaspaceSize
（1）CompressedClassSpaceSize参数作用是设置Klass Metaspace的大小，默认1G
Klass Metaspace就是用来存klass的，klass是的class文件在jvm里的运行时数据结构，没有开启压缩指针，就不会有CompressedClassSpaceSize这块内存，但是jdk1.8里应该是默认开启的，并且，如果这块内存会如果没有满会一直增加。

但是-Xmx超过了32G，压缩指针是默认不开启的，而这个参数也就失去了设置的意义。

通过设置-XX:CompressedClassSpaceSize=128m来调节

 （2）MaxMetaspaceSize

默认基本是无穷大，这个参数很可能会因为没有限制而导致metaspace被无止境使用(一般是内存泄漏)而被OS Kill。这个参数会限制metaspace(包括了Klass Metaspace以及NoKlass Metaspace)被committed的内存大小，会保证committed的内存不会超过这个值，一旦超过就会触发GC，这里要注意和MaxPermSize的区别，MaxMetaspaceSize并不会在jvm启动的时候分配一块这么大的内存出来，而MaxPermSize是会分配一块这么大的内存的。

3.MaxDirectMemorySize

此参数主要影响的是非堆内存的direct byte buffer，jvm默认会设置64M，可根据功能适当加大此项参数，因为非堆内存，故而不会被GC回收掉，容易出现java.lang.OutOfMemoryError: Direct buffer memory错误

如出现以上错误，可通过以下参数打印log，之后用工具进行分析

-XX:-HeapDumpOnOutOfMemoryError
-XX:HeapDumpPath=logs/oom_dump.log

4.G1收集器参数

-XX:+UseG1GC
使用G1收集器
-XX:MaxGCPauseMillis=200 
用户设定的最大gc 停顿时间，默认是200ms. 
-XX:InitiatingHeapOccupancyPercent=45 
默认是45，也就是heap中45%的容量被使用，则会触发concurrent gc

-XX:NewRatio=n
新生代与老生代(new/old generation)的大小比例(Ratio). 默认值为 2.
-XX:SurvivorRatio=n    eden/survivor
空间大小的比例(Ratio). 默认值为 8.
-XX:MaxTenuringThreshold=n
提升年老代的最大临界值(tenuring threshold). 默认值为 15.
-XX:ParallelGCThreads=n
设置垃圾收集器在并行阶段使用的线程数,默认值随JVM运行的平台不同而不同.
-XX:ConcGCThreads=n
并发垃圾收集器使用的线程数量. 默认值随JVM运行的平台不同而不同.
-XX:G1ReservePercent=n
设置堆内存保留为假天花板的总量,以降低提升失败的可能性. 默认值是 10.
-XX:G1HeapRegionSize=n
使用G1时Java堆会被分为大小统一的的区(region)。此参数可以指定每个heap区的大小. 默认值将根据 heap size 算出最优解. 最小值为 1Mb, 最大值为 32Mb.