1.集合：List HashMap currentHashMap  实现原理(源码)，扩容机制  是否线程安全
2.并发，线程（sleep与wait; join yield等线程方法），线程池，volitate（可见性） synchronized  Lock 实现原理
3.Http协议 tcp机制原理
4.Strng stringbuffer  stringbuild  区别
5.接口和抽象类的区别 使用场景
6.JVM内存分配 JVM性能调优（内存泄漏 内存溢出等）gc原理
7.spring ioc,aop 原理 ，利用@Configuration 和@Bean注解生产bean （重要），尤其是aop原理及哪些使用了aop（日志，事务，。。）；以及spring bean的单例模式 多例模式，创建Bean对象的几种方式
8.spring 事务机制 传播方式（重要）
9.springmvc流程，及整合mybatis
8.springboot优势，什么是spring cloud，还会涉及到一些微服务组件（eureka config  zuul rabbon,服务调用（Rest）,）springboot项目流程 引入依赖-》添加配置-》引入注解-》启动类
9.微服务：服务调用，分布式事务，熔断
10.Redis缓存使用（缓存类型） （redis单线程），利用Redis实现分布式session，涉及是先删缓存还是先更新再删缓存（应该是后删缓存），秒杀业务利用Redis缓存实现
11.Rabbitmq 机制，确认机制，路由策略（topic direct faunt）,如何保证消息的唯一性，如何确保不重复消费消息，消息积压
12.mysql:索引（实现原理，使用失效），sql性能调优,高并发下可能会涉及分库分表。笔试题中常见的有：部门表上下级关系；学生 老师 成绩 课程。
13.Java算法 ：一些常见的排序（了解），快速排序 冒泡；在一个数组中找出出现次数最多的字符
14，高并发下的场景，如何解决，项目中怎么实现，怎么解决（一般会有Redis  rabbitmq，多个进程，可以看看我们代码中如何用的，一般都会问到），项目中遇到的困难 如何解决；讲讲自己做的一个比较难的需求 功能 ，如何实现（趁现在赶紧看看我们代码中一个功能点，金融或非金融都可以，可能会问些业务的，挑一个最熟悉的）
15.Linux ：基本命令讲几个，还有chmor赋权限的命令，750权限意识，查看端口命令，top,可能会问生产中如何查看进程 排除问题 及使用情况
16.比如我们会接收行方的报文，涉及到一些加密 可以看看前置报文的一些处理
17.RPC 调用，Rabbitmq作用，解耦方式
18.设计模式：单列模式 代理 工厂等
19.JDK8的一些新特性可能会问点 
最多的还是一些线程安全 spring 集合 高并发场景 

还有就是spring事务场景几乎都会问到




























1.	map怎么实现hashcode和equals,为什么重写equals必须重写hashcode 
2.	使用过concurrent包下的哪些类，使用场景等等。 
3.	concurrentHashMap怎么实现？concurrenthashmap在1.8和1.7里面有什么区别 
4.	CountDownLatch、LinkedHashMap、AQS实现原理 
5.	线程池有哪些RejectedExecutionHandler,分别对应的使用场景 
6.	多线程的锁？怎么优化的？偏向锁、轻量级锁、重量级锁？ 
7.	组合索引？B+树如何存储的？ 
8.	为什么缓存更新策略是先更新数据库后删除缓存 
9.	OOM说一下？怎么排查？哪些会导致OOM? 
10.	OSI七层结构，每层结构都是干什么的？ 
11.	java的线程安全queue需要注意的点 
12.	死锁的原因，如何避免 
二面
1.	jvm虚拟机老年代什么情况下会发生gc，给你一个场景，一台4核8G的服务器，每隔两个小时就要出现一次老年代gc，现在有日志，怎么分析是哪里出了问题 
2.	数据库索引有哪些？底层怎么实现的？数据库怎么优化？ 
3.	数据库的事务，四个性质说一下，分别有什么用，怎么实现的？ 
4.	服务器如何负载均衡，有哪些算法，哪个比较好，一致性哈希原理，怎么避免DDOS攻击请求打到少数机器 
5.	volatile讲讲 
6.	哪些设计模式？装饰器、代理讲讲？ 
7.	redis集群会吗？ 
8.	mysql存储引擎 
9.	事务隔离级别 
10.	不可重复度和幻读，怎么避免，底层怎么实现（行锁表锁） 
三面
1.	项目介绍 
2.	分布式锁是怎么实现的 
3.	MySQL有哪几种join方式，底层原理是什么 
4.	Redis有哪些数据结构？底层的编码有哪些？有序链表采用了哪些不同的编码？ 
5.	Redis扩容，失效key清理策略 
6.	Redis的持久化怎么做，aof和rdb，有什么区别，有什么优缺点。 
7.	MySQL数据库怎么实现分库分表，以及数据同步？ 
8.	单点登录如何是实现？ 
9.	谈谈SpringBoot和SpringCloud的理解 
10.	未来的技术职业怎么规划？ 
11.	为什么选择阿里？ 

