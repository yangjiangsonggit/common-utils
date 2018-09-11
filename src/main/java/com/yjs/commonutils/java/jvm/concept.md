-Xss规定了每个线程堆栈的大小。一般情况下256K是足够了。影响了此进程中并发线程数大小。

-Xms初始的Heap的大小。

-Xmx最大Heap的大小。

在很多情况下，-Xms和-Xmx设置成一样的。这么设置，是因为当Heap不够用时，会发生内存抖动，影响程序运行稳定性。



大牛解析:
https://blog.csdn.net/losetowin/article/details/78569001

https://blog.csdn.net/RickyIT/article/details/53895060

https://blog.csdn.net/m0_37698652/article/details/79690656