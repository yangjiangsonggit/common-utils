作者：追击者
链接：https://zhuanlan.zhihu.com/p/47767074
来源：知乎
著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。

部分技术面试题Java基础：
hashmap结构；
什么对象能做为key 
hashtable,concurrentHashMap,hashtable比较
String,StringBuilder,StringBuffer对象的深浅复制

多线程：
wait,sleep分别是谁的方法，
区别countLatch的await方法是否安全，
怎么改造线程池参数，整个流程描述
背后的底层原理aqs，cas
ThreadLocal原理，注意事项，参数传递
还有Java的锁，内置锁，显示锁，各种容器
及锁优化：锁消除，锁粗化，锁偏向，轻量级锁

web方面：servlet是否线程安全，如何改造
session与cookie的区别，
get和post区别，tcp3次握手，文件上传用post还是get
session的存储
如何防止表单重复提交

jvm:
jvm内存模型，
jvm问题工具,jps,jinfo,jmap...

数据库：

最重要的索性及底层实现
索性失效的场景
最左原则
查看执行计划
及carndiation
然后是锁的类型，行级表级
悲观乐观锁
解释数据库事物及特性
隔离级别及实现，redo log .undo log
bin log主从复制
mvcc,Next-Key Lock

分布式：
问了CAP，跟base
zookeeper满足了CAP的哪些特性，paxos
缓存穿透怎么解决
redis的io模型
如果保证redis高可用
redis是单线程还是多线程
线上cpu占比过高怎么排查
一致性hash
分库分表

spring:

ioc,aop原理
ioc初始化流程
springmvc的流程
springboot,spring cloud相关组件

项目友情提醒一下，对于做过的项目，最好梳理清理，可能会叫你画各种图。