    
##V2.0 基于SETNX
    
    tryLock(){  
        SETNX Key 1 Seconds
    }
    release(){  
      DELETE Key
    }
    
    Redis 2.6.12版本后SETNX增加过期时间参数，这样就解决了两条命令无法保证原子性的问题。但是设想下面一个场景：
    1、C1成功获取到了锁，之后C1因为GC进入等待或者未知原因导致任务执行过长，最后在锁失效前C1没有主动释放锁
    2、C2在C1的锁超时后获取到锁，并且开始执行，这个时候C1和C2都同时在执行，会因重复执行造成数据不一致等未知情况
    3、C1如果先执行完毕，则会释放C2的锁，此时可能导致另外一个C3进程获取到了锁
        
    存在问题：
    
        1、由于C1的停顿导致C1 和C2同都获得了锁并且同时在执行，在业务实现间接要求必须保证幂等性
        2、C1释放了不属于C1的锁
    
##V3.0
    
    
    tryLock(){  
        SETNX Key UnixTimestamp Seconds
    }
    release(){  
        EVAL(
          //LuaScript
          if redis.call("get",KEYS[1]) == ARGV[1] then
              return redis.call("del",KEYS[1])
          else
              return 0
          end
        )
    }
    
    这个方案通过指定Value为时间戳，并在释放锁的时候检查锁的Value是否为获取锁的Value，避免了V2.0版本中提到的C1释放了C2持有的锁的问题；
    另外在释放锁的时候因为涉及到多个Redis操作，并且考虑到Check And Set 模型的并发问题，所以使用Lua脚本来避免并发问题。
    
    存在问题：
    
        如果在并发极高的场景下，比如抢红包场景，可能存在UnixTimestamp重复问题，另外由于不能保证分布式环境下的物理时钟一致性，
        也可能存在UnixTimestamp重复问题，只不过极少情况下会遇到。
    
##V3.1
    
    tryLock(){  
        SET Key UniqId Seconds
    }
    release(){  
        EVAL(
          //LuaScript
          if redis.call("get",KEYS[1]) == ARGV[1] then
              return redis.call("del",KEYS[1])
          else
              return 0
          end
        )
    }
    
    Redis 2.6.12后SET同样提供了一个NX参数，等同于SETNX命令，官方文档上提醒后面的版本有可能去掉SETNX, SETEX, PSETEX,并用SET命令代替，
    另外一个优化是使用一个自增的唯一UniqId代替时间戳来规避V3.0提到的时钟问题。
    
    这个方案是目前最优的分布式锁方案，但是如果在Redis集群环境下依然存在问题：
    
        由于Redis集群数据同步为异步，假设在Master节点获取到锁后未完成数据同步情况下Master节点crash，此时在新的Master节点依然可以获取锁，
        所以多个Client同时获取到了锁
    
    
##分布式Redis锁：Redlock
    
    
    V3.1的版本仅在单实例的场景下是安全的，针对如何实现分布式Redis的锁，国外的分布式专家有过激烈的讨论， antirez提出了分布式锁算法Redlock，
    在distlock话题下可以看到对Redlock的详细说明，下面是Redlock算法的一个中文说明（引用）
    
    假设有N个独立的Redis节点
    
    获取当前时间（毫秒数）。
    
    按顺序依次向N个Redis节点执行获取锁的操作。这个获取操作跟前面基于单Redis节点的获取锁的过程相同，包含随机字符串myrandomvalue，
    也包含过期时间(比如PX 30000，即锁的有效时间)。为了保证在某个Redis节点不可用的时候算法能够继续运行，这个获取锁的操作还有一个
    超时时间(time out)，它要远小于锁的有效时间（几十毫秒量级）。客户端在向某个Redis节点获取锁失败以后，应该立即尝试下一个Redis节点。
    这里的失败，应该包含任何类型的失败，比如该Redis节点不可用，或者该Redis节点上的锁已经被其它客户端持有（注：Redlock原文中这里只提到
    了Redis节点不可用的情况，但也应该包含其它的失败情况）。
    
    计算整个获取锁的过程总共消耗了多长时间，计算方法是用当前时间减去第1步记录的时间。如果客户端从大多数Redis节点（>= N/2+1）成功获取到了锁，
    并且获取锁总共消耗的时间没有超过锁的有效时间(lock validity time)，那么这时客户端才认为最终获取锁成功；否则，认为最终获取锁失败。
    
    如果最终获取锁成功了，那么这个锁的有效时间应该重新计算，它等于最初的锁的有效时间减去第3步计算出来的获取锁消耗的时间。
    
    如果最终获取锁失败了（可能由于获取到锁的Redis节点个数少于N/2+1，或者整个获取锁的过程消耗的时间超过了锁的最初有效时间），
    那么客户端应该立即向所有Redis节点发起释放锁的操作（即前面介绍的Redis Lua脚本）。
    
    释放锁：对所有的Redis节点发起释放锁操作
    
    然而Martin Kleppmann针对这个算法提出了质疑，提出应该基于fencing token机制（每次对资源进行操作都需要进行token验证）
    
    1、Redlock在系统模型上尤其是在分布式时钟一致性问题上提出了假设，实际场景下存在时钟不一致和时钟跳跃问题，而Redlock恰恰是基于timing的分布式锁
    2、另外Redlock由于是基于自动过期机制，依然没有解决长时间的gc pause等问题带来的锁自动失效，从而带来的安全性问题。 接着antirez又回复
    了Martin Kleppmann的质疑，给出了过期机制的合理性，以及实际场景中如果出现停顿问题导致多个Client同时访问资源的情况下如何处理。
    
    针对Redlock的问题，基于Redis的分布式锁到底安全吗给出了详细的中文说明，并对Redlock算法存在的问题提出了分析。
    
