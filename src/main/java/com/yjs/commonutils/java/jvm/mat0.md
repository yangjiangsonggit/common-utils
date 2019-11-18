### dump文件生成：

a）jvm内存溢出OutOfMemoryError自动生成dump内存快照

-XX:+HeapDumpOnOutOfMemoryError-XX:HeapDumpPath=/home/work/oom_error.hprof

b）手动触发生成

jps -v --查看jvm pid: 26041

生成jvm dump快照：
jmap -dump:format=b,file=/home/work/jinze/dump_26041_05231438.hprof 26041 --> 生成二进制堆栈文件。

### 基本概念：

Shallow Heap: 类对象本身占用内存大小，不包含其引用的对象内存-> List对象占用内存大小 4k

Retained Heap: 对象自己占用内存 + 关联引用对象占用大小 -> List对象占用内存大小 4k + User对象占用内存大小 123k注：如一个ArrayList持有100,000个对象，每一个占用16 bytes，移除这些ArrayList可以释放16 x 100,000 + X，X代表ArrayList的shallow大小。相对于shallow heap，RetainedHeap可以更精确的反映一个对象实际占用的大小（因为如果该对象释放，retained heap都可以被释放）。


### https://www.jianshu.com/p/82b25cf8cfde?utm
### https://blog.csdn.net/cn_honor/article/details/100143214 (重要)