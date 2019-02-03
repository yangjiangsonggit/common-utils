zookeeper framework 之 Netflix curator（完美支持永久监听）
介绍
curator是Netflix公司开源的zookeeper client library
官方地址：https://github.com/Netflix/curator/wiki/Recipes
详细介绍1：http://macrochen.iteye.com/blog/1366136/
详细介绍2：http://blog.csdn.net/alivetime/article/details/7101014
Curator主要解决了三类问题:

封装ZooKeeper client与ZooKeeper server之间的连接处理;
提供了一套Fluent风格的操作API;
提供ZooKeeper各种应用场景(recipe, 比如共享锁服务, 集群领导选举机制)的抽象封装.
Curator几个组成部分

Client: 是ZooKeeper客户端的一个替代品, 提供了一些底层处理和相关的工具方法.
Framework: 用来简化ZooKeeper高级功能的使用, 并增加了一些新的功能, 比如管理到ZooKeeper集群的连接, 重试处理
Recipes: 实现了通用ZooKeeper的recipe, 该组件建立在Framework的基础之上
Utilities:各种ZooKeeper的工具类
Errors: 异常处理, 连接, 恢复等.
Extensions: recipe扩展
maven dependency
<dependency>
    <groupId>com.netflix.curator</groupId>
    <artifactId>curator-recipes</artifactId>
    <version>1.3.3</version>
</dependency>
curator framework 使用
String path = "/test_path";

CuratorFramework client = CuratorFrameworkFactory.builder()
        .connectString("test:2181").namespace("/test1")
        .retryPolicy(new RetryNTimes(Integer.MAX_VALUE, 1000))
        .connectionTimeoutMs(5000).build();

// start
client.start();

// create a node
client.create().forPath("/head", new byte[0]);

// delete a node in background
client.delete().inBackground().forPath("/head");

// create a EPHEMERAL_SEQUENTIAL
client.create().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath("/head/child", new byte[0]);

// get the data 
client.getData().watched().inBackground().forPath("/test");

// check the path exits
client.checkExists().forPath(path);
InterProcessMutex(进程间互斥锁)
String lockName = "/lock1";
InterProcessLock lock1 = new InterProcessMutex(this.curator, lockName);
InterProcessLock lock2 = new InterProcessMutex(this.curator, lockName);
lock1.acquire();
boolean result = lock2.acquire(1, TimeUnit.SECONDS);
assertFalse(result);
lock1.release();
result = lock2.acquire(1, TimeUnit.SECONDS);
assertTrue(result);
原理：每次调用acquire在lock1节点下使用createMode.EPHEMERAL_SEQUENTIAL创建的ephemeral节点，然后getChildren获取所有的children，判断刚刚创建的临时节点是否为第一个，如果是，则获取锁成功；如果不是则删除刚刚创建的临时节点。
注意：每次accquire操作，成功则请求zkserver 2次，一次写，一次getChildren；如果失败则请求zkserver三次（一次写，一次getChildren，一次删除）

InterProcessReadWriteLock(节点读写锁)
示例

    @Test
    public void testReadWriteLock() throws Exception{
        String readWriteLockPath = "/RWLock";
        InterProcessReadWriteLock readWriteLock1 = new InterProcessReadWriteLock(this.curator, readWriteLockPath);
        InterProcessMutex writeLock1 = readWriteLock1.writeLock();
        InterProcessMutex readLock1 = readWriteLock1.readLock();
        
        InterProcessReadWriteLock readWriteLock2 = new InterProcessReadWriteLock(this.curator, readWriteLockPath);
        InterProcessMutex writeLock2 = readWriteLock2.writeLock();
        InterProcessMutex readLock2 = readWriteLock2.readLock();
        writeLock1.acquire();
        
        // same with WriteLock, can read
        assertTrue(readLock1.acquire(1, TimeUnit.SECONDS));
        
        // different lock, can't read while writting
        assertFalse(readLock2.acquire(1, TimeUnit.SECONDS));
        
        // different write lock, can't write
        assertFalse(writeLock2.acquire(1, TimeUnit.SECONDS));
        
        // release the write lock
        writeLock1.release();
        
        //both read lock can read
        assertTrue(readLock1.acquire(1, TimeUnit.SECONDS));
        assertTrue(readLock2.acquire(1, TimeUnit.SECONDS));
    }
原理： 同InterProcessMutext，在ephemeral node的排序算法上做trick，write lock的排序在前。
注意： 同一个InterProcessReadWriteLock如果已经获取了write lock，则获取read lock也会成功

LeaderSelector(leader 选举)
    @Test
    public void testLeader() throws Exception{
        LeaderSelectorListener listener = new LeaderSelectorListener(){


            @Override
            public void takeLeadership(CuratorFramework client)
                    throws Exception {
                System.out.println("i'm leader");
            }

            @Override
            public void handleException(CuratorFramework client,
                    Exception exception) {
                
            }

            @Override
            public void notifyClientClosing(CuratorFramework client) {
                
            }};
        String leaderPath = "/leader";
        LeaderSelector selector1 = new LeaderSelector(this.curator, leaderPath, listener);
        selector1.start();
        LeaderSelector selector2 = new LeaderSelector(this.curator, leaderPath, listener);
        selector2.start();
        assertFalse(selector2.hasLeadership());
    }
原理：内部基于InterProcessMutex实现

NodeCache(监听节点数据)
/**
 * 在注册监听器的时候，如果传入此参数，当事件触发时，逻辑由线程池处理
 */
ExecutorService pool = Executors.newFixedThreadPool(2);

/**
 * 监听数据节点的变化情况
 */
final NodeCache nodeCache = new NodeCache(client, "/zk-huey/cnode", false);
nodeCache.start(true);
nodeCache.getListenable().addListener(
    new NodeCacheListener() {
        @Override
        public void nodeChanged() throws Exception {
            System.out.println("Node data is changed, new data: " + 
                new String(nodeCache.getCurrentData().getData()));
        }
    }, 
    pool
);
PathChildrenCache(监听子节点目录变化)
/**
 * 监听子节点的变化情况
 */
final PathChildrenCache childrenCache = new PathChildrenCache(client, "/zk-huey", true);
childrenCache.start(StartMode.POST_INITIALIZED_EVENT);
childrenCache.getListenable().addListener(
    new PathChildrenCacheListener() {
        @Override
        public void childEvent(CuratorFramework client, PathChildrenCacheEvent event)
                throws Exception {
                switch (event.getType()) {
                case CHILD_ADDED:
                    System.out.println("CHILD_ADDED: " + event.getData().getPath());
                    break;
                case CHILD_REMOVED:
                    System.out.println("CHILD_REMOVED: " + event.getData().getPath());
                    break;
                case CHILD_UPDATED:
                    System.out.println("CHILD_UPDATED: " + event.getData().getPath());
                    break;
                default:
                    break;
            }
        }
    },
    pool
);