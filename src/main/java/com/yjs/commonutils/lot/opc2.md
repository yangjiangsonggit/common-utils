##1 (closed).基础概念
    Unified Architecture
    OPC统一架构规范（UA）
          OPC UA是一个独立于平台的、面向服务的架构规范，集成了所有来自现有的OPC Classic规范的功能，为更安全和可扩展的解决方案
          提供了迁移路径。 该规范兼容OPC Classic规范。
    
    Classic
    OPC经典架构规范
          OPC Classic规范基于Microsoft Windows技术，使用COM / DCOM（分布式组件对象模型）在软件组件之间交换数据。

##2.OPC Server端的协议
    OPC Server端目前常见的有以下几种协议:
    
      OPC DA: Data Access协议，是最基本的OPC协议。OPC DA服务器本身不存储数据，只负责显示数据收集点的当前值。客户端可以设置一个refresh interval，定期刷新这个值。目前常见的协议版本号为2.0和3.0，两个协议不完全兼容。也就是用OPC DA 2.0协议的客户端连不上OPC DA 3.0的Server
    
      OPC HDA: Historical Data Access协议。前面说过DA只显示当前状态值，不存储数据。而HDA协议是由数据库提供，提供了历史数据访问的能力。比如价格昂贵的Historian数据库，就是提供HDA协议接口访问OPC的历史数据。HDA的Java客户端目前我没找到免费的。
    
      OPC A&E: Alarms and Events协议。OPC 报警与事件 (AE) 客户端可以接收和监控 Alarms and Events 直接发出的过程报警、操作员操作、信息类消息以及跟踪/审计消息。
    
      OPC UA: Unified Architecture统一架构协议。诞生于2008年，摒弃了前面老的OPC协议繁杂，互不兼容等劣势，并且不再需要COM口访问，大大简化了编程的难度。基于OPC UA的开源客户端非常多。不过由于诞生时间较晚，目前在国内工业上未大规模应用，并且这个协议本身就跟旧的DA协议不兼容，客户端没法通用。

##3.OPC和OPC UA的区别

###3.1 OPC UA的优势
      OPC UA接口协议包含了之前的 A&E, DA,OPC XML DA or HDA，只使用一个地址空间就能访问之前所有的对象，而且不受WINDOWS平台限制，因为它是从传输层Scoket以上来定义的，这点后面会提到，导致了灵活性和安全性比之前的OPC都提升了。
    
      1）一个通用接口集成了之前所有OPC的特性和信息，A&E, DA,OPC XML DA or HDA
      2）更加开放，平台无关性，WINDOWS,Linux都能兼容
      3）扩展了对象类型，支持更复杂的数据类型比如变量，方法和事件
      4）在协议和应用层集成了安全功能，更加安全
      5）易于配置和使用

###3.2 OPC和OPC UA的区别
    核心的区别是因为OPC和OPC UA协议使用的TCP层不一样，如下：
      OPC是基于DOM/COM上，应用层最顶层；OPC UA是基于TCP IP scoket 传输层.
    
      OPC虽然通过配置COM/DOM来提供数据加密和签名功能，配置防火墙，用户权限来让数据访问变得更加安全，但是会增加额外的工作量，尤其是对非IT的工程师来说；对于OPC UA，数据加密和签名，防火墙等都是默认的功能。比如基于DOM的OPC使用的动态端口分配，端口不固定，让防火墙难以确定，而OPC UA的端口都是唯一的，比如SINUMERIK 840D是PORT 4840，SIMATIC S7是PORT 4845。DOM/COM也可以生成不同级别的事件日志，但日志内容不够详细，只会提供“谁连接上服务器”这种，而对于OPC UA来说都是默认的功能，生成的日志内容更全面。

##4.技术选型
    我们的目标环境绝大多数是OPC DA 2.0的Server，和新的OPC UA。我们主要讨论这两种情况：

###4.1 OPC DA 2.0

####4.1.1 JEasyOPC Client
       1）底层依赖JNI，只能跑在windows环境，不能跨平台
       2）整个类库比较古老，使用的dll是32位的，整个项目只能使用32位的JRE运行

####4.1.2 Utgard
       1）OpenSCADA项目底下的子项目
       2）纯Java编写，具有跨平台特性
       3）全部基于DCOM实现(划重点)
    
      JEasyOPC底层用了JNI，调用代码量倒不是很大，使用也足够简单，坑也遇到了点，就是64位的JRE运行会报错，说dll是ia32架构的，不能运行于AMD64平台下，换了32位版本的JRE之后运行起来了，但是一直报错Unknown Error，从JNI报出来的，不明所以，实在无力解决，只能放弃，只剩下Utgard一种选择了。

###4.2 OPC UA

####4.2.1 milo 
       据了解该项目的Eclipse旗下的一个物联网的项目，是一个高性能的OPC UA栈，提供了一组客户端和服务端的API，支持对实时数据的访问，监控，报警，订阅数据，支持事件，历史数据访问，和数据建模。
     主要提供的功能有：
     （建议结合代码中client-examples模块看看，下面标下相应功能和demo的对应关系）
    
     1）搜索服务节点
     2）获取服务节点列表
     3）浏览节点   （BrowseAsyncExample，BrowseExample，BrowseNodeExample） 
     4）获取节点值 （ReadExample，ReadNodeExample，ReadWriteCustomDataTypeNodeExample，UnifiedAutomationReadCustomDataTypeExample）
     5）写变量 （WriteExample）
     6）订阅变量 （SubscriptionExample，ProsysEventSubscriptionExample）
     7）历史记录获取 （HistoryReadExampleProsys）




##写的好的
    OPCUA标准java实现 Milo库
    https://blog.csdn.net/q932104843/article/details/86664236
    
    Java OPC client开发踩坑记
    https://segmentfault.com/a/1190000010091596
    
    什么是OPC UA
    https://blog.csdn.net/zxf1242652895/article/details/82460954
    
    OPC UA基本概念结构
    https://blog.csdn.net/fsnAltria/article/details/79203924
