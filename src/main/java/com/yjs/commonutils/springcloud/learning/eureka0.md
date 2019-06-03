# eureka
> https://www.jianshu.com/p/5d29348a3435

## 1.基本架构
Eureka主要包括三种角色：
- Register Service：服务注册中心，他是一个Eureka Server，提供服务注册和发现的功能。
- Provider Service：服务提供者，它是一个Eureka Client，提供服务。
- Consumer Service：服务消费者，它是一个Eureka Client，消费服务。

## 2.服务消费的基本流程
首先需要启动一个Eureka Server，做为服务注册中心，服务提供者Eureka Client向服务注册中心Eureka Server注册，将自己的服务名和IP地址等信息通过REST API的形式提交给服务注册中心Eureka Server。同样，服务消费者Eureka Client也向服务注册中心Eureka Server注册，同时服务消费者获取一份服务注册列表的信息，该列表包含了所有向服务注册中心Eureka Server注册的服务信息。获取服务注册列表信息之后，服务消费者就知道服务提供者的IP地址，可以通过Http远程调度来消费服务提供者的服务。

## 3.Eureka的一些概念

- Register-服务注册
当Eureka Client向Eureka Server注册时，client提供自身的元数据，如IP地址、端口、运行状态指标的Url、主页地址等信息。
- Renew-服务续约
Eureka Client默认情况下每隔30秒发送一次心跳来进行服务续约。如果Eureka Server在90秒内没有收到Eureka Client的心跳，Eureka Server会将Eureka Client实例从注册列表中剔除。
- Fetch Registries-获取服务注册列表信息
Eureka Client从Eureka Server获取服务注册表信息，并缓存在本地，可根据注册表信息查找服务，从而进行远程调用。该注册表信息每30秒更新一次。如果由于某种原因导致服务注册列表信息不能及时匹配，Eureka Client会重新获取整个注册表信息。
- Cancel-服务下线
Eureka Client在程序关闭时可以向Eureka Server发送下线信息。发送请求后，该客户端的实例信息将从Eureka Server的服务注册列表中删除。该下线请求不会自动完成，需要在程序关闭时调用以下代码：
DiscoveryManager.getInstance().shutdownComponent();
- Eviction-服务剔除
默认情况下，当Eureka Client连续90秒没有向Eureka Server发送服务续约（即心跳）时，Eureka Server会将该服务实例从服务注册列表删除。
- Eureka的自我保护模式
当一个新的Eureka Server出现时，会尝试从相邻的Peer节点获取所有服务实例的注册信息。如果从相邻的Peer节点获取信息时出现了故障，Eureka Server会尝试其它的Peer节点。如果Eureka Server能够成功获取所有的服务实例信息，则根据配置信息设置服务续约的阈值。在任何时间，如果Eureka Server接收到的服务续约低于该配置的百分比（默认为15分钟内低于85%），则服务器开启自我保护模式，即不在剔除注册列表的信息。这样做的好处是如果是Eureka Server自身的网络问题而导致Eureka Client无法续约，这样在注册列表中不会剔除注册信息，这样Eureka Client还可以被其他服务消费。同时这个功能也是有坑的，如果在服务较少时，服务由于意外情况挂掉后很容易阈值就低于85%，由于自我保护导致挂掉的服务在注册列表中还是存在，但其它服务无法消费，这会迷惑开发人员排错的方向。如果需要关闭该功能，在配置文件中添加如下代码：

eureka:
    server:
        enable-self-preservation: false


