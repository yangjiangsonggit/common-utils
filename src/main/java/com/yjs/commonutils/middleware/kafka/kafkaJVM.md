##分布式系统Kafka和ES中，JVM内存越大越好吗？
    https://mp.weixin.qq.com/s/pA-rfvTqairhxTrmDLudxg

    这篇文章，给大家聊一个生产环境的实践经验：线上系统部署的时候，JVM 堆内存大小是越大越好吗？
    本文主要讨论的是 Kafka 和 Elasticsearch 两种分布式系统的线上部署情况，不是普通的 Java 应用系统。
    
    是否依赖 Java 系统自身内存处理数据？
    
        先说明一点，不管是我们自己开发的 Java 应用系统，还是一些中间件系统，在实现的时候都需要选择是否基于自己 Java 进程的内存来处理数据。
        大家应该都知道，Java、Scala 等编程语言底层依赖的都是 JVM，那么只要是使用 JVM，就可以考虑在 JVM 进程的内存中来放置大量的数据。
        还是给大家举个例子，大家应该还记得之前聊过消息中间件系统。
        
        比如说系统 A 可以给系统 B 发送一条消息，那么中间需要依赖一个消息中间件，系统 A 要先把消息发送到消息中间件，然后系统 B 从这个消息中间件消费到这条消息。
        大家看下面的示意图：
        
        一条消息发送到消息中间件之后，有一种处理方式，就是把这条数据先缓冲在自己的 JVM 内存里。
        然后过一段时间之后，再从自己的内存刷新到磁盘上去，这样可以持久化保存这条消息，如下图：
    
    依赖 Java 系统自身内存有什么缺陷？
    
        如果用类似上述的方式，依赖 Java 系统自身内存处理数据，比如说设计一个内存缓冲区，来缓冲住高并发写入的大量消息，那么是有其缺陷的。
        最大的缺陷，其实就是 JVM 的 GC 问题，这个 GC 就是垃圾回收，这里简单说一下它是怎么回事。
        大家可以想一下，如果一个 Java 进程里老是塞入很多的数据，这些数据都是用来缓冲在内存里的，但是过一会儿这些数据都会写入磁盘。
        那么写入磁盘之后，这些数据还需要继续放在内存里吗？明显是不需要的了，此时就会依托 JVM 垃圾回收机制，把内存里那些不需要的数据给回收掉，释放掉那些内存空间腾出来。
        但是 JVM 垃圾回收的时候，有一种情况叫做 stop the world，就是它会停止你的工作线程，就专门让它进行垃圾回收。
        这个时候，它在垃圾回收的时候，有可能你的这个中间件系统就运行不了了。
        比如你发送请求给它，它可能都没法响应给你，因为它的接收请求的工作线程都停了，现在人家后台的垃圾回收线程正在回收垃圾对象。
        
        虽然说现在 JVM 的垃圾回收器一直在不断的演进和发展，从 CMS 到 G1，尽可能的在降低垃圾回收的时候的影响，减少工作线程的停顿。
        但是你要是完全依赖 JVM 内存来管理大量的数据，那在垃圾回收的时候，或多或少总是有影响的。
        所以特别是对于一些大数据系统，中间件系统，这个 JVM 的 GC（Garbage Collector，垃圾回收）问题，真是最头疼的一个问题。
        优化为依赖 OS Cache 而不是 JVM
        所以类似 Kafka、Elasticsearch 等分布式中间件系统，虽然也是基于 JVM 运行的，但是它们都选择了依赖 OS Cache 来管理大量的数据。
        也就是说，是操作系统管理的内存缓冲，而不是依赖 JVM 自身内存来管理大量的数据。
        具体来说，比如说 Kafka 吧，如果你写一条数据到 Kafka，它实际上会直接写入磁盘文件。
        但是磁盘文件在写入之前其实会进入 OS Cache，也就是操作系统管理的内存空间，然后过一段时间，操作系统自己会选择把它的 OS Cache 的数据刷入磁盘。
        然后后续在消费数据的时候，其实也会优先从 OS Cache（内存缓冲）里来读取数据。
        相当于写数据和读数据都是依托于 OS Cache 来进行的，完全依托操作系统级别的内存区域来进行，读写性能都很高。
        此外，还有另外一个好处，就是不要依托自身 JVM 来缓冲大量的数据，这样可以避免复杂而且耗时的 JVM 垃圾回收操作。
        大家看下面的图，其实就是一个典型的 Kafka 的运行流程：
        然后比如 Elasticsearch，它作为一个现在最流行的分布式搜索系统，也是采用类似的机制。
        大量的依赖 OS Cache 来缓冲大量的数据，然后在进行搜索和查询的时候，也可以优先从 OS Cache（内存区域）中读取数据，这样就可以保证非常高的读写性能。
    
    
    
    依赖 OS Cache 的系统，JVM 内存越大越好？
    
    
    
    现在就可以进入我们的主题了，那么比如就以上述说的 Kafka、Elasticsearch 等系统而言，在线上生产环境部署的时候，你知道它们是大量依赖于 OS Cache 来缓冲大量数据的。那么，给它们分配 JVM 堆内存大小的时候是越大越好吗？
    
    
    
    明显不是的，假如说你有一台机器，有 32GB 的内存，现在你如果在搞不清楚状况的情况下，要是傻傻的认为还是给 JVM 分配越大内存越好，此时比如给了 16G 的堆内存空间给 JVM，那么 OS Cache 剩下的内存，可能就不到 10GB 了，因为本身其他的程序还要占用几个 GB 的内存。
    
    
    
    那如果是这样的话，就会导致你在写入磁盘的时候，OS Cache 能容纳的数据量很有限。
    
    
    
    比如说一共有 20G 的数据要写入磁盘，现在就只有 10GB 的数据可以放在 OS Cache 里，然后另外 10GB 的数据就只能放在磁盘上。
    
    
    
    此时在读取数据的时候，那么起码有一半的读取请求，必须从磁盘上去读了，没法从 OS Cache 里读，谁让你 OS Cache 里就只能放的下 10G 的一半大小的数据啊，另外一半都在磁盘里，这也是没办法的，如下图：
    
    
    
    那此时你有一半的请求都是从磁盘上在读取数据，必然会导致性能很差。
    
    
    
    所以很多人在用 Elasticsearch 的时候就是这样的一个问题，老是觉得 ES 读取速度慢，几个亿的数据写入 ES，读取的时候要好几秒。
    
    
    
    那能不花费好几秒吗？你要是 ES 集群部署的时候，给 JVM 内存过大，给 OS Cache 留了几个 GB 的内存，导致几亿条数据大部分都在磁盘上，不在 OS Cache 里，最后读取的时候大量读磁盘，耗费个几秒钟是很正常的。
    
    
    
    正确的做法：针对场景合理给 OS Cache 更大内存
    
    
    
    所以说，针对类似 Kafka、Elasticsearch 这种生产系统部署的时候，应该要给 JVM 比如 6GB 或者几个 GB 的内存就可以了。
    
    
    
    因为它们可能不需要耗费过大的内存空间，不依赖 JVM 内存管理数据，当然具体是设置多少，需要你精准的压测和优化。
    
    
    
    但是对于这类系统，应该给 OS Cache 留出来足够的内存空间，比如 32GB 内存的机器，完全可以给 OS Cache 留出来 20 多 G 的内存空间。
    
    
    
    那么此时假设你这台机器总共就写入了 20GB 的数据，就可以全部驻留在 OS Cache 里了。
    
    
    
    然后后续在查询数据的时候，不就可以全部从 OS Cache 里读取数据了，完全依托内存来走，那你的性能必然是毫秒级的，不可能出现几秒钟才完成一个查询的情况。
    
    
    
    整个过程，如下图所示：
    
    
    
    所以说，建议大家在线上生产系统引入任何技术的时候，都应该先对这个技术的原理，甚至源码进行深入的理解，知道它具体的工作流程是什么，然后针对性的合理设计生产环境的部署方案，保证最佳的生产性能。