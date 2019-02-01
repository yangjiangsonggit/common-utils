03.Curator深入使用
阅读目录

开始
1.Apache Curator简介
2.Apache Curator Recipes
3.Apache Curator Framework
4.Apache Curator Utilities
5.Apache Curator Client

回到顶部
1.Apache Curator简介
    Curator提供了一套Java类库，可以更容易的使用ZooKeeper。ZooKeeper本身提供了Java Client的访问类，但是API太底层，不宜使用，易出错。Curator提供了三个组件。Curator client用来替代ZOoKeeper提供的类，它封装了底层的管理并提供了一些有用的工具。Curator framework提供了高级的API来简化ZooKeeper的使用。它增加了很多基于ZooKeeper的特性，帮助管理ZooKeeper的连接以及重试操作。Curator Recipes提供了使用ZooKeeper的一些通用的技巧（方法）。除此之外，Curator Test提供了基于ZooKeeper的单元测试工具。
    所谓技巧(Recipes)，也可以称之为解决方案，或者叫实现方案，是指ZooKeeper的使用方法，比如分布式的配置管理，Leader选举等。
    Curator最初由Netflix的Jordan Zimmerman开发。20117月在github上基于Apache 2.0开源协议开源。之后发布了多个版本，并被广泛的应用。Curator作为Apache ZooKeeper天生配套的组件。ZooKeeper的Java开发者自然而然的会选择它在项目中使用。
1.Curator组件概览
Recipes：通用ZooKeeper技巧("recipes")的实现. 建立在Curator Framework之上
Framework：简化zookeeper使用的高级. 增加了很多建立在zooper之上的特性. 管理复杂连接处理和重试操作
Utilities：各种工具类
Client：ZooKeeper本身提供的类的替代者。负责底层的开销以及一些工具
Errors：Curator怎样来处理错误和异常
Extensions：curator-recipes包实现了通用的技巧，这些技巧在ZooKeeper文档中有介绍。为了避免是这个包(package)变得巨大，recipes/applications将会放入一个独立的extension包下。并使用命名规则curator-x-name
2.Maven/Artifacts
    Curator编译好的类库被发布到Maven Center中。Curator包含几个artifact. 你可以根据你的需要在你的项目中加入相应的依赖。对于大多数开发者来说，引入curator-recipes这一个就足够了。	
<dependency>
    <groupId>org.apache.curator</groupId>
    <artifactId>curator-client</artifactId>
    <version>2.9.0</version>
</dependency>
<dependency>
    <groupId>org.apache.curator</groupId>
    <artifactId>curator-framework</artifactId>
    <version>2.9.0</version>
</dependency>
<dependency>
    <groupId>org.apache.curator</groupId>
    <artifactId>curator-recipes</artifactId>
    <version>2.9.0</version>
</dependency>
<dependency>
    <groupId>org.apache.curator</groupId>
    <artifactId>curator-x-discovery</artifactId>
    <version>2.9.0</version>
</dependency>
<dependency>
    <groupId>org.apache.curator</groupId>
    <artifactId>curator-examples</artifactId>
    <version>2.9.0</version>
</dependency>
回到顶部
2.Apache Curator Recipes
    Curator实现了 ZooKeeper recipes文档中列出的所有技巧(除了两段提交two phase commit)。下面介绍Recipes的使用，参考官网：http://curator.apache.org/curator-recipes/index.html
1.Elections(选举)
Leader Latch - 在分布式计算中，leader选举是在几台节点中指派单一的进程作为任务组织者的过程。在任务开始前， 所有的网络节点都不知道哪一个节点会作为任务的leader或coordinator. 一旦leader选举算法被执行， 网络中的每个节点都将知道一个特别的唯一的节点作为任务leader
Leader Election - 初始的leader选举实现
2.Locks(锁)
Shared Reentrant Lock - 全功能的分布式锁。任何一刻不会有两个client同时拥有锁
Shared Lock - 与Shared Reentrant Lock类似但是不是重入的
Shared Reentrant Read Write Lock - 类似Java的读写锁，但是是分布式的
Shared Semaphore - 跨JVM的计数信号量
Multi Shared Lock - 将多个锁看成整体，要不全部acquire成功，要不acquire全部失败。release也是释放全部锁
3.Barriers(障碍)
Barrier - 分布式的barriers。会阻塞全部的节点的处理，直到条件满足，所有的节点会继续执行
Double Barrier - 双barrier允许客户端在一个计算开始点和结束点保持同步。当足够的进程加入barrier,进程开始它们的计算，当所有的进程完成计算才离开
4.Counters(计数器)
Shared Counter - 管理一个共享的整数integer.所有监控同一path的客户端都会得到最新的值(ZK的一致性保证)
Distributed Atomic Long - 尝试原子增加的计数器首先它尝试乐观锁.如果失败，可选的InterProcessMutex会被采用.不管是optimistic 还是 mutex,重试机制都被用来尝试增加值
5.Caches(缓存)
Path Cache - Path Cache是用来监控子节点的。每当一个子节点“曾加、更新或删除”，将会触发事件，事件就是注册了的PathChildrenCacheListener实例执行。Path Cache的功能主要由PathChildrenCache类提供。
Node Cache - 用于监控节点，当节点数据被修改或节点被删除，就会触发事件，事件就是注册了的NodeCacheListener实例执行。Node Cache的功能主要由NodeCache类提供。
6.Nodes(节点)
Persistent Ephemeral Node - 临时节点，可以通过连接或会话中断一个临时节点。
7.Queues(队列)
Distributed Queue - 分布式的ZK队列
Distributed Id Queue - 分布式的ZK队列，它支持分配ID来添加到队列中的项目的替换版本
Distributed Priority Queue - 分布式优先级的ZK队列
Distributed Delay Queue - 分布式的延迟ZK队列
Simple Distributed Queue - 一个简单的替代自带的ZK分布式队列(Distributed Queue)
其具体的实现示例，可参考官网：http://curator.apache.org/curator-recipes/index.html
回到顶部
3.Apache Curator Framework
    Curator framework提供了高级API， 极大的简化了ZooKeeper的使用。 它在ZooKeeper基础上增加了很多特性，可以管理与ZOoKeeper的连接和重试机制。这些特性包括：
自动连接管理：有些潜在的错误情况需要让ZooKeeper client重建连接和重试。Curator可以自动地和透明地处理这些情况
Cleaner API：简化原始的ZooKeeper方法，事件等 提供现代的流式接口
1.产生Curator framework实例
    使用CuratorFrameworkFactory产生framework实例。 CuratorFrameworkFactory 既提供了factory方法也提供了builder来创建实例。CuratorFrameworkFactory是线程安全的。你应该在应用中为单一的ZooKeeper集群共享唯一的CuratorFramework实例。
    工厂方法(newClient())提供了一个简单的方式创建实例。Builder可以使用更多的参数控制生成的实例。一旦生成framework实例， 必须调用start方法启动它。应用结束时应该调用close方法关闭它。
2.CuratorFramework API
    CuratorFramework 使用流程风格的接口。 代码胜于说教：
client.create().forPath("/head", new byte[0]);
client.delete().inBackground().forPath("/head");
client.create().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath("/head/child", new byte[0]);
client.getData().watched().inBackground().forPath("/test");
CuratorFramework类重要方法说明
create()        开始创建操作， 可以调用额外的方法(比如方式mode 或者后台执行background) 并在最后调用forPath()指定要操作的ZNode
delete()        开始删除操作. 可以调用额外的方法(版本或者后台处理version or background)并在最后调用forPath()指定要操作的ZNode
checkExists()   开始检查ZNode是否存在的操作. 可以调用额外的方法(监控或者后台处理)并在最后调用forPath()指定要操作的ZNode
getData()       开始获得ZNode节点数据的操作. 可以调用额外的方法(监控、后台处理或者获取状态watch, background or get stat) 并在最后调用forPath()指定要操作的ZNode
setData()       开始设置ZNode节点数据的操作. 可以调用额外的方法(版本或者后台处理) 并在最后调用forPath()指定要操作的ZNode
getChildren()   开始获得ZNode的子节点列表。 以调用额外的方法(监控、后台处理或者获取状态watch, background or get stat) 并在最后调用forPath()指定要操作的ZNode
inTransaction() 开始是原子ZooKeeper事务. 可以复合create, setData, check, and/or delete 等操作然后调用commit()作为一个原子操作提交
3.通知 Notifications
    服务于后台操作和监控(watch)的通知通过ClientListener接口发布。你通过CuratorFramework实例的addListener方法可以注册监听器。
其事件触发时间说明：
CuratorListenable：当使用后台线程操作时，后台线程执行完成就会触发，例如：client.getData().inBackground().forPath("/test");后台获取节点数据，获取完成之后触发。
ConnectionStateListenable：当连接状态变化时触发。
UnhandledErrorListenable：当后台操作发生异常时触发。
CuratorListenable事件触发返回的数据如下：
事件类型        事件返回数据
CREATE          getResultCode() and getPath()
DELETE          getResultCode() and getPath()
EXISTS          getResultCode(), getPath() and getStat()
GET_DATA        getResultCode(), getPath(), getStat() and getData()
SET_DATA        getResultCode(), getPath() and getStat()
CHILDREN        getResultCode(), getPath(), getStat(), getChildren()
SYNC            getResultCode(), getStat()
GET_ACL         getResultCode(), getACLList()
SET_ACL         getResultCode()
TRANSACTION     getResultCode(), getOpResults()
WATCHED         getWatchedEvent()
GET_CONFIG      getResultCode(), getData()
RECONFIG        getResultCode(), getData()
4.命名空间
    你可以使用命名空间Namespace避免多个应用的节点的名称冲突。 CuratorFramework提供了命名空间的概念，这样CuratorFramework会为它的API调用的path加上命名空间：
CuratorFramework client = CuratorFrameworkFactory.builder().namespace("MyApp") ... build();
5.临时客户端
    Curator还提供了临时的CuratorFramework：CuratorTempFramework，一定时间不活动后连接会被关闭。创建builder时不是调用build()而是调用buildTemp()。3分钟不活动连接就被关闭，你也可以指定不活动的时间。
CuratorTempFramework client = CuratorFrameworkFactory.builder()
    .connectString("127.0.0.1:2181")// 连接串
    .retryPolicy(new RetryNTimes(10, 5000))// 重试策略
    .connectionTimeoutMs(100) // 连接超时
    .sessionTimeoutMs(100) // 会话超时
    .buildTemp(100, TimeUnit.MINUTES); // 临时客户端并设置连接时间
它只提供了下面几个方法：
public void     close();
public CuratorTransaction inTransaction() throws Exception;
public TempGetDataBuilder getData() throws Exception;
6.Retry策略
    retry策略可以改变retry的行为。 它抽象出RetryPolicy接口， 包含一个方法public boolean allowRetry(int retryCount, long elapsedTimeMs);。 在retry被尝试执行前， allowRetry()被调用，并且将当前的重试次数和操作已用时间作为参数. 如果返回true， retry被执行。否则异常被抛出。
Curator本身提供了几个策略:
ExponentialBackoffRetry:重试一定次数，每次重试sleep更多的时间
RetryNTimes:重试N次
RetryOneTime:重试一次
RetryUntilElapsed:重试一定的时间
回到顶部
4.Apache Curator Utilities
    Curator提供了一组工具类和方法用来测试基于Curator的应用。 并且提供了操作ZNode辅助类以及其它一些数据结构
1.Test Server
    curator-test提供了TestingServer类。 这个类创建了一个本地的， 同进程的ZooKeeper服务器用来测试。
public static void main(String[] args) throws Exception
{
    TestingServer server = new TestingServer();
    CuratorFramework client = CuratorFrameworkFactory.newClient(server.getConnectString(), new ExponentialBackoffRetry(1000, 3));
    client.getConnectionStateListenable().addListener(new ConnectionStateListener()
    {
        @Override
        public void stateChanged(CuratorFramework client, ConnectionState newState)
        {
            System.out.println("连接状态:" + newState.name());
        }
    });
    client.start();
    System.out.println(client.getChildren().forPath("/"));
    client.create().forPath("/test");
    System.out.println(client.getChildren().forPath("/"));
    CloseableUtils.closeQuietly(client);
    CloseableUtils.closeQuietly(server);
    System.out.println("OK!");
}
2.Test Cluster
    curator-test提供了TestingCluster类。 这个类创建了一个内部的ZooKeeper集群用来测试。
public static void main(String[] args) throws Exception
{
    TestingCluster cluster = new TestingCluster(3);
    cluster.start();
    for (TestingZooKeeperServer server : cluster.getServers())
    {
        System.out.println(server.getInstanceSpec());
    }
    cluster.stop();
    CloseableUtils.closeQuietly(cluster);
    System.out.println("OK！");
}
3.ZKPaths
提供了各种静态方法来操作ZNode:
getNodeFromPath: 从一个全路径中得到节点名， 比如 "/one/two/three" 返回 "three"
mkdirs: 确保所有的节点都已被创建
getSortedChildren: 得到一个给定路径的子节点， 按照sequence number排序
makePath: 给定父路径和子节点，创建一个全路径
4.EnsurePath
    确保一个特定的路径被创建。当它第一次使用时，一个同步ZKPaths.mkdirs(ZooKeeper, String)调用被触发来确保完整的路径都已经被创建。后续的调用将不是同步操作.用法：
EnsurePath       ensurePath = new EnsurePath(aFullPathToEnsure);
...
String           nodePath = aFullPathToEnsure + "/foo";
ensurePath.ensure(zk);   // first time syncs and creates if needed
zk.create(nodePath, ...);
...
ensurePath.ensure(zk);   // subsequent times are NOPs
zk.create(nodePath, ...);
注意： 此方法namespace会参与路径名字的创建。
5.Blocking Queue Consumer(阻塞队列消费者)
    请参看Distributed Queue 和 Distributed Priority Queue。提供JDK BlockingQueue类似的行为。
6.Queue Sharder
    由于zookeeper传输层的限制，单一的队列如果超过10K的元素会被分割(break)。这个类为多个分布式队列提供了一个facade。它监控队列，如果一个队列超过这个阈值，一个新的队列就被创建。在这些队列中Put是分布式的。
7.Reaper and ChildReaper
Reaper
    可以用来删除锁的父路径。定时检查路径被加入到reaper中。当检查时，如果path没有子节点/路径，此路径将被删除。每个应用中CLient应该只创建一个reaper实例。必须将lock path加到这个readper中。reaper会定时的检查删除它们。
ChildReaper
    用来清除父节点下所有的空节点。定时的调用getChildren()并将空节点加入到内部管理的reaper中。
注意：应该考虑使用LeaderSelector来运行Reapers，因为它们不需要在每个client运行
回到顶部
5.Apache Curator Client
    Curator client使用底层的API， 强烈推荐你是用Curator Framework代替使用CuratorZookeeperClient
1.背景
CuratorZookeeperClient 是ZooKeeper client的包装类。但是提供了更简单方式， 而且可以减少错误的发生。它提供了下列的特性：
持续的连接管理 - ZooKeeper有很多的关于连接管理的警告（你可以到ZooKeeper FAQ查看细节）。CuratorZookeeperClient 可以自动的管理这些事情。
retry - 提供一个方式处理retry(重试)。
Test ZooKeeper server - 提供一个进程内的ZooKeeper测试服务器用来测试和实验。
2.方法
Constructor - 创建一个给定ZooKeeper集群的连接。 你可以传入一个可选的watcher. 必须提供Retry策略
getZooKeeper() - 返回管理的ZooKeeper实例. 重要提示: a) 它会花费些许时间等待连接来完成， 在使用其它方法之前你应该校验连接是否完成. b) 管理的ZooKeeper实例可以根据特定的事件而改变。 不要持有实例太长时间. 总是调用getZooKeeper()得到一个新的实例
isConnected() - 返回ZooKeeper client当前连接状态
blockUntilConnectedOrTimedOut() - block知道连接成功或者超时
close() - 关闭连接
setRetryPolicy() - 改变retry(重试)策略
newRetryLoop() - 分配一个新的Retry Loop(重试循环)
3.Retry Loop(重试循环)
    由于各种各样的原因，在zookeeper集群上的操作难免遇到失败的情况。最佳实践表明应该提供重试机制。Retry Loop为此而生。每个操作都被包装在一个Retry Loop中。下面是一个典型的处理流程：
RetryLoop retryLoop = client.newRetryLoop();
while ( retryLoop.shouldContinue() )
{
   try
   {
       // perform your work
       ...
       // it's important to re\-get the ZK instance as there may have been an error and the instance was re\-created
       ZooKeeper      zk = client.getZookeeper();
       retryLoop.markComplete();
   }
   catch ( Exception e )
   {
       retryLoop.takeException(e);
   }
}
    Retry Loop维护一定数量的retry， 它还决定一个错误是否可以要执行retry。 假如一个错误需要retry，Retry策略被调用来决定retry是要要执行，执行多少次才放弃。
    很方便地，RetryLoop 提供了一个静态方法使用Callable来执行一个完整retry loop。
RetryLoop.callWithRetry(client, new Callable<Void>()
{
      @Override
      public Void call() throws Exception
      {
          // do your work here - it will get retried if needed
          return null;
      }
});
4.Retry策略(重试策略)
    retry策略可以改变retry的行为。它抽象出RetryPolicy接口，包含一个方法public boolean allowRetry(int retryCount, long elapsedTimeMs)。在retry被尝试执行前，allowRetry()被调用，并且将当前的重试次数和操作已用时间作为参数.如果返回true， retry被执行。否则异常被抛出。
Curator本身提供了几个策略(在 com.netflix.curator.retry 包下):
ExponentialBackoffRetry:重试一定次数，每次重试sleep更多的时间
RetryNTimes:重试N次
RetryOneTime:重试一次
RetryUntilElapsed:重试一定的时间
------------------------------------------------------------------------------------------------------------------------------- 

来自为知笔记(Wiz)


如果，您认为阅读这篇博客让您有些收获，不妨点击一下右下角的推荐按钮。
如果，您希望更容易地发现我的新博客，不妨关注一下。因为，我的写作热情也离不开您的肯定支持。
感谢您的阅读，如果您对我的博客所讲述的内容有兴趣，请继续关注我的后续博客。