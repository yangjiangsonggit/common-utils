eureka 服务治理机制
=====================

##服务提供者

        服务提供者在启动的时候会通过发送REST请求的方式将自己注册到Eureka Server上，同时带上了自身服务的一些元数据信息。Eureka Server 
        接收到这个REST请求之后，将元数据信息存储到了一个双层结构Map中，第一层的key是服务名，第二层的key是具体服务的实例名。

##服务同步

       当服务提供者发送注册请求到一个服务注册中心时，它会将请求转发到集群中相连的其他注册中心，从而实现注册中心之间服务的同步。

##服务续约

       注册完成后，服务提供者会维护一个心跳通知Eureka Server“我还活着”，防止从服务列表中剔除出去。我们称改操作为服务续约(Renew).

##服务消费者

      启动服务消费者时，他会发送一个REST请求给服务注册中心，获取上面注册的服务清单。为了性能考虑，Eureka Server会维护一份只读的服务清单
      来返回给客户端，同时缓存清单会30秒更新一次。获取服务是服务消费者的基础，必须确保 eureka.client.fetch-registry=true(默认为true)，
      修改缓存清单的更新时间： eureka.client.registry-fetch-interval-seconds=30

##服务调用

      服务消费者获取服务清单后，通过服务名可以获取提供服务的实例名和该实例的元数据信息。因为有这些服务实例的详细信息，所以客户端可以根据
      自己的需要决定具体调用哪个实例，在Ribbon中会默认采用轮询的方式进行调用，从而实现客户端的负载均衡。