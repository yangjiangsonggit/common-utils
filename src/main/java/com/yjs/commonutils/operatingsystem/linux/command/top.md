
##top

在Linux上通过top看到的RES和SHR的值很高，表示进程占用的内存很多吗？会有什么问题吗？

要搞清楚这个问题，首先从top输出开始研究：
top命令的输出中VIRT RES SHR这三列的意思我就不多解释了，man top 一下就明白了。
VIRT 是进程使用的总的虚拟内存大小。
RES 是常驻内存的大小（不能SWAP）。
SHR 是共享内存的大小（包括共享库等）。
然后看一下这几个值分别是从哪里来的？看了一下top命令的源代码，在m_linux.c中有这几个值的来源，简单说就是，
这些值都是从/proc/$pid 下面读出来的：
VIRT 是 /proc/$pid/stat 中的第23个数字。
RES 是/proc/$pid/stat 中的第24个数字，但是要注意，这个是用页来表示的，所以需要成字节。
SHR 是 /proc/$pid/statm 中的第三个数字，这个也是用页表示的，需要转换。
总的虚拟内存大小不用管它，这里主要关注RES 和 SHR。

查看linux内核代码可知
RES的值获取方式如下：
mm ? get_mm_rss(mm) : 0
而get_mm_rss的定义如下：
411 #define get_mm_rss(mm) \
412 (get_mm_counter(mm, file_rss) + get_mm_counter(mm, anon_rss))
而get_mm_counter也是一个宏，它获取了进程的内存结构体mm_struct中变量 _file_rss 和_anon_rss 成员。
SHR的获取代码如下：
*shared = get_mm_counter(mm, file_rss);
由此可见：
RES = mm_struct->_file_rss + mm_struct->_anon_rss
SHR = mm_struct->file_rss
那么，file_rss 和anon_rss是怎么计算出来的？它们代表的意思又是什么？要彻底搞清楚这两个东西，直接从代码上解释比较麻烦，比较不容易说清楚。
但是我们可以换个地方去寻找答案。
我们发现，在/proc/$pid 下面还有其他的一些文件，那么这些文件又是什么意思呢？有没有用的上的呢？其实是有的。
/proc/$pid/maps 这个文件描述了当前映射到进程的内存和它们的访问权限
/proc/$pid/smaps 这个文件是对/proc/$pid/maps更详细的描述，包括了每个映射内存的大小和使用情况 （具体参见文档 kernel/Documentation/filesystems/proc.txt）
在我的机器上将进程ID为50893，RES=2.3G的进程的 statm 和 smaps 保存到文件：
cat /proc/50893/statm > statm
cat /proc/50893/smaps > smaps
[ocsrun@OCSPROXY1 lqb]$ cat statm
1467525 602414 601853 29 0 559 0
这里重点关注第二个数字，这个数是什么意思以及怎么取到的后面会说。

[ocsrun@OCSPROXY1 lqb]$ grep Rss smaps |awk 'BEGIN {sum = 0;} {sum += $2} END{print sum}'
2409656
然后发现：602414 × 4K = 2409656K 与smaps中所有的Rss的和相等。
而2409656 / 1024 / 1024 = 2.298 = 2.3G 这不就是RES的大小吗？
现在来说statm 中的第二个数，这个数就是 file_rss + anon_rss，代码我就不贴了，参看/kernel/fs/proc/array.c 和 /kernel/fs/proc/task_mmu.c
而smaps中所有的Rss，是遍历进程所有的虚拟映射链表，一个一个累加出来的。以下是相关结构体的片段。
202 struct mm_struct {
203 struct vm_area_struct * mmap; /* list of VMAs */
134 struct vm_area_struct {
135 struct mm_struct * vm_mm; /* The address space we belong to. */
136 unsigned long vm_start; /* Our start address within vm_mm. */
137 unsigned long vm_end; /* The first byte after our end address
138 within vm_mm. */
至此我们知道file_rss + anon_rss 就是进程所有映射的页面的大小，注意这里说的都是虚拟地址空间，而不是物理内存（当然也包括了一部分的物理内存）。
前面那个关于file_rss 和 anon_rss的问题算是基本上搞清楚了，但是为什么有的进程的映射页面会突然变得很大呢？
为了搞清楚这个问题，我们仔细查看一下smaps文件：
仔细查看后发现，smaps里面包括了所有映射的动态库，heap，stack等等。当然也包括了共享内存。

共享内存是以下面的方式而存在的：
7f43d5445000-7f4515445000 rw-s 00000000 00:04 8749063 /SYSV00c9b457 (deleted)
Size: 5242880 kB
Rss: 2098384 kB
Pss: 1052946 kB
Shared_Clean: 541256 kB
Shared_Dirty: 1548472 kB
Private_Clean: 7656 kB
Private_Dirty: 1000 kB
Referenced: 2098364 kB
Swap: 0 kB
KernelPageSize: 4 kB
MMUPageSize: 4 kB

而普通共享库是这样的：
7f452c11d000-7f452c4c9000 r-xp 00000000 08:09 8172 /home/ocsrun/xerces-c/xerces-c-3.0.0/lib/libxerces-c-3.0.so
Size: 3760 kB
Rss: 1336 kB
Pss: 93 kB
Shared_Clean: 1336 kB
Shared_Dirty: 0 kB
Private_Clean: 0 kB
Private_Dirty: 0 kB
Referenced: 1336 kB
Swap: 0 kB
KernelPageSize: 4 kB
MMUPageSize: 4 kB

将上面共享内存的行移下来：
7f43d5445000-7f4515445000 rw-s 00000000 00:04 8749063 /SYSV00c9b457(deleted)
该行的意思如下：第一列是（虚拟）地址映射空间，第二列是访问权限，第三列是偏移量，第四列是设备号，以主设备号:次设备号的方式显示，第五列是inode，最后一列是路径。
这里为什么是deleted呢？因为对于共享内存来说，根本不存在实际的文件跟它对应。

运行ipcs输出如下（部分输出）：
[ocsrun@OCSPROXY1 ~]$ ipcs
------ Shared Memory Segments --------
key shmid owner perms bytes nattch status
0x00c9b455 8683525 ocsrun 660 103415808 5
0x00c9b456 8716294 ocsrun 660 279150592 5
0x00c9b457 8749063 ocsrun 660 5368709120 5
0x00c9b45f 8781832 ocsrun 660 103415808 8
可以看到，在smaps中共享内存的inode是8749063，也就是ipcs显示的shmid，而key就是文件路径的后面一段。
如上面所示，共享内存的映射有一个大于2G的，所以加上另外两块共享内存的映射，总的RES有2.3G也就不奇怪了。
而关于共享内存的分配，多说两句：sys v shared memory实现，shmget基本就是在ramfs上写一个名字是SYSV<shm_key>的文件（这里是/SYSV00c9457），
然后该文件的inode号是内核ipc核心数据结构的唯一id号，之后shmat基本就是mmap这个文件的过程，也就是说shmget返回的shmid其实就是用于存储内容的文件的inode编号。

所以：Linux上top输出中RES和SHR的值比较高是没有关系的，因为可能大部分都是共享内存。top和ps等的输出中关于内存占用的部分都包括了共享内存/共享库等占用的虚拟内存空间。