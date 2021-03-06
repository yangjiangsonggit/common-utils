#学习笔记
    Unified Architecture
    OPC统一架构规范（UA）
          OPC UA是一个独立于平台的、面向服务的架构规范，集成了所有来自现有的OPC Classic规范的功能，为更安全和可扩展的解决方案
          提供了迁移路径。 该规范兼容OPC Classic规范。
          
    Classic
    OPC经典架构规范
          OPC Classic规范基于Microsoft Windows技术，使用COM / DCOM（分布式组件对象模型）在软件组件之间交换数据。 
          规范仅供会员下载。请联系OPC基金会了解更多信息或了解如何加入基金会。
          
###OPC 客户程序和OPC 服务器
    
        与传统的通讯开发方式相比，OPC 技术具有以下优势：
            ·硬件厂商熟悉自己的硬件设备，因而设备驱动程序性能更可靠、效率更高。
            ·软件厂商可以减少复杂的设备驱动程序的开发周期，只需开发一
            套遵循OPC 标准的程序就可以实现与硬件设备的通信，因此可以把人
            力、物力资源投入到系统功能的完善中。
            ·可以实现软硬件的互操作性。
            ·OPC 把软硬件厂商区分开来，使得双方的工作效率有了很大的提高。
            
        目前OPC 规范主要有DA（Data Access）规范，AE(alarm and event)规范，HDA(history data access)规范等。
        
        计算机监控系统也普遍的采用了分布
        式结构，因而系统的异构性是一个非常显著的特点。OPC 技术本质是采
        用了Microsoft 的COM/DCOM（组件对象模型/分布式组件对象模型）技
        术，COM 主要是为了实现软件复用和互操作，并且为基于WINDOWS
        的程序提供了统一的、可扩充的、面向对象的通讯协议，DCOM 是COM
        技术在分布式计算领域的扩展，使COM 可以支持在局域网、广域网甚
        至Internet 上不同计算机上的对象之间的通讯。
        
        COM 的模型是C/S（客户/服务器）模型，OPC 技术的提出就是基
        于COM 的C/S 模式，因此OPC 的开发分为OPC 服务器开发和OPC 客
        户程序开发，对于硬件厂商，一般需要开发适用于硬件通讯的OPC 服务
        器，对于组态软件，一般需要开发OPC 客户程序。
        
        一个OPC 客户可以连接一个或多个OPC 服务器，而多个OPC 客
        户也可以同时连接同一个OPC 服务器
    
###OPC 服务器的对象组成
    
        一个OPC 服务器由三个对象组成：服务器(Server)，组(Group)，项
        (Item)。OPC 服务器对象用来提供关于服务器对象自身的相关信息，并
        且作为OPC 组对象的容器。OPC 组对象用来提供关于组对象自身的相关
        信息，并提供组织和管理项的机制。
    
        PLC（可编程控制器）
        
        OPC规范定义了2 种组对象：公共组和私有组。公共组由多个客户共享，局
        部组只隶属于一 OPC 客户。全局组对所有连接在服务器的应用程序都
        有效，而私有组只能对建立它的CLIENT 有效。在一个SERVER 中，可
        以有若干个组。
        
        OPC 项代表了OPC 服务器到数据源的一个物理连接。数据项是读写
        数据的最小逻辑单位。一个OPC 项不能被OPC 客户程序直接访问，因
        此在OPC 规范中没有对应于项的COM 接口，所有与项的访问需要通过
        包含项的OPC 组对象来实现。简单的讲，对于一个项而言，一个项可以
        是PLC 中的一个寄存器，也可以是PLC 中的一个寄存器的某一位。在一
        个组对象中，客户可以加入多个OPC 数据项。每个数据项包括3 个变量：
        值（Value）、品质(Quality)和时间戳（Time Stamp）。数据值是以VARIANT
        形式表示的。
        
        这里最需要注意的是项并不是数据源，项代表了到数据源的连接。
        例如一个在一个DCS 系统中的TAG 不论OPC 客户程序是否访问都是实
        际存在的。项应该被认为是到一个地址的数据。大家一定要注意项的概
        念。不同的组对象里可以拥有相同的项
        
        
###OPC 接口体系
        OPC 规范提供两种接口：自定义接口（the OPC Custom Interfaces），
        自动化接口（the OPC Automation interfaces）。
        
        象所有的COM结构一样，OPC是典型的CLIENT/SERVER
        结构，OPC 服务器提供标准的OPC 接口供OPC 客户程序访问。OPC 服
        务器必须提供自定义接口，对于自动化接口，在OPC 规范定义中是可选
        的。
        
##OPC 对象接口定义
                
        
        OPC 服务器对象提供一些方法去读取或连接一些数据源。OPC 客户
        程序连接到OPC 服务器对象，并通过标准接口与OPC 服务器联系。OPC
        服务器对象提供接口（AddGroup）供OPC 客户程序创建组对象并将需
        要操作的项添加到组对象中，并且组对象可以被激活，也可以被赋予未
        激活状态。对于OPC 客户程序而言，所有OPC 服务器和OPC 组对象可
        见的仅仅是COM 接口。
        
        IOPCServerPublicGroups，IOPCBrowseServerAddressSpace 和
        IPersistFile 为任选（optional）接口，OPC 服务器提供商可根据需要选择
        是否需要实现。其它接口为OPC 服务器必须实现的接口。其中：
        IOPCServerPublicGroups 接口用于对公共组进行管理。IPersistFile 接口允
        许用户装载和保存服务器的设置，这些设置包括服务器通信的波特率、
        现场设备的地址和名称等，这样用户就可以知道服务器启动和配置的改
        变而不需要启动其它的程序。
        
        IOPCServer 接口及成员函数主要用于对组对象进行创建、删除、枚
        举和获取当前状态等操作。是OPC 服务器对象的主要接口。接口及成员
        函数定义为：
        其中：IOPCItemMgt 接口及成员函数用于OPC 客户程序添加、删除
        和组对象中组员等控制操作。IOPCGroupStateMgt 接口及其成员函数允
        许OPC 客户程序操作或获取用户组对象的全部状态（主要是组对象的刷
        新率和活动状态，刷新率的单位为毫秒）。IOPCPublicGroupStateMgt 为
        任选接口，用于将私有组对象（private group）转化为公有组对象（public
        group），这个接口一般不用，在很多商业的OPC 服务器中，此接口都
        没有开发。可选接口IOPCAsyncIO 和IdataObject 接口用于异步数据传输
        （在OPC 数据访问规范1.0 中定义，现在其功能已经被IOPCAsyncIO2
        和IConnectionPointContainer 接口取代）。IOPCSyncIO 用于同步数据访
        问。IOPCAsyncIO2 用于异步数据访问。这两个接口是数据访问规范进行
        数据访问最重要的接口。
        
##OPC 同步异步通讯

        同步通讯时，OPC 客户程序对OPC 服务器进行相关操作时，OPC
        客户程序必须等到OPC 服务器对应的操作全部完成以后才能返回，在此
        期间OPC 客户程序一直处于等待状态，如进行读操作，那么必须等待
        OPC 服务器完成读后才返回。因此在同步通讯时，如果有大量数据进行
        操作或者有很多OPC 客户程序对OPC 服务器进行读、写操作，必然造
        成OPC 客户程序的阻塞现象。因此同步通讯适用于OPC 客户程序较少，
        数据量较小时的场合。
        
        异步通讯时，OPC 客户程序对服务器进行相关操作时，OPC 客户程
        序操作后立刻返回，不用等待OPC 服务器的操作，可以进行其他操作。
        当OPC 服务器完成操作后再通知OPC 客户程序，如进行读操作，OPC
        客户程序通知OPC 服务器后离开返回，不等待OPC 服务器的读完成，
        而OPC 服务器完成读后，会自动的通知OPC 客户程序，把读结果传送
        给OPC 客户程序。因此相对于同步通讯，异步通讯的效率更高，适用于
        多客户访问同一OPC 服务器和大量数据的场合。
        
        OPC 的异步通讯有四种方式：
        ·数据订阅，客户端通过订阅方式后，服务器端将变化的数据通过回
        调传送给客户程序。
        ·异步读，返回操作结果和数据值。
        ·异步写，返回操作结果，成功、失败。
        ·异步刷新，异步读所有Item 的值
        
        一个设备的OPC Server 主要有两部组成，一是OPC 标准接
        口的实现，二是与硬件设备的通信模块。
        
#opc ua
    https://www.jianshu.com/p/f331a3dbbe75
    
    
        OPC UA 独立于制造商，应用可以用他通信，开发者可以用不同编程语言对他开发，不同的操作系统上可以对他支持。OPC UA 弥补了
        已有 OPC 的不足，增加了诸如平台独立、可伸缩性、高可用性和因特网服务等重要特性。OPC UA 不再基于分布式组件对象模型（DCOM），
        而是以面向服务的架构（SOA）为基础。OPC UA 因此可以连接更多的设备。今天，OPC UA 已经成为连接企业级计算机与嵌入式自动化
        组件的桥梁 - 独立于微软、 UNIX 或其他操作系统。
        
        通过Web服务实现跨平台的OPC通信随着在2003年OPC XML-DA规范的发布，OPC基金会第一次展示了一种独立于视窗平台的方式和克服
        DCOM限制的方法。今天，很多OPC XML-DA产品演示了基于Web服务的OPC技术。但是XML-DA 通信的数据吞吐量还是比不上DCOM，
        通信速度要慢5到7倍。这个速度对于很多自动化的要求而言是太慢了。基于Web服务的OPC通信功能还是有用的，因为实现了跨越操作
        系统的能力，但还要进一步提高数据传输性能。
        
        PLC控制系统，Programmable Logic Controller，可编程逻辑控制器，专为工业生产设计的一种数字运算操作的电子装置，它采用一类
        可编程的存储器，用于其内部存储程序，执行逻辑运算，顺序控制，定时，计数与算术操作等面向用户的指令，并通过数字或模拟式\
        输入/输出控制各种类型的机械或生产过程。是工业控制的核心部分。
        
        
#opc 踩坑记录
    重要
    https://www.jianshu.com/p/26391f0cbb6f

    OPC Server端的协议
        OPC Server端目前常见的有以下几种协议:
        
        OPC DA: Data Access协议，是最基本的OPC协议。OPC DA服务器本身不存储数据，只负责显示数据收集点的当前值。客户端可以设置一个refresh interval，定期刷新这个值。目前常见的协议版本号为2.0和3.0，两个协议不完全兼容。也就是用OPC DA 2.0协议的客户端连不上OPC DA 3.0的Server
        OPC HDA: Historical Data Access协议。前面说过DA只显示当前状态值，不存储数据。而HDA协议是由数据库提供，提供了历史数据访问的能力。比如价格昂贵的Historian数据库，就是提供HDA协议接口访问OPC的历史数据。HDA的Java客户端目前我没找到免费的。
        OPC UA: Unified Architecture统一架构协议。诞生于2008年，摒弃了前面老的OPC协议繁杂，互不兼容等劣势，并且不再需要COM口访问，大大简化了编程的难度。基于OPC UA的开源客户端非常多。不过由于诞生时间较晚，目前在国内工业上未大规模应用，并且这个协议本身就跟旧的DA协议不兼容，客户端没法通用。
        
        我们的目标环境绝大多数是OPC DA 2.0的Server，极个别可能有OPC DA 3.0。当时找到的很多类库实现的都是OPC UA的。
    
        第一坑: 基于JAVA开发的OPC Client非常少，大部分是商业的，售价不菲。现场环境又是OPC DA的Server，开源client只有两个可选，找工具和评估就花了不少时间。
        
    OPC存储格式
        第二坑: 这种存储格式在其他数据库十分罕见，当时这里就迷茫了好一阵子，通过了解协议的人讲解，才明白原来客户端还可以维护
        一套存储结构。当时没理清楚Group和tag的关系，从服务端看不到Group，客户端却要填一个Group，不知道这个Group从哪来。后来才搞清楚。        
        
        
        
##资料
    OPCUA标准java实现 Milo库
    https://blog.csdn.net/q932104843/article/details/86664236
    
    X.509是一种非常通用的证书格式。所有的证书都符合ITU-T X.509国际标准，因此(理论上)为一种应用创建的证书可以用于任何其他符合X.509标准的应用。
    
    
    
##UA代码流程
    14:56:37.059 [main] INFO  o.e.m.opcua.sdk.server.OpcUaServer - Eclipse Milo OPC UA Stack version: 0.2.0
    14:56:37.072 [main] INFO  o.e.m.opcua.sdk.server.OpcUaServer - Eclipse Milo OPC UA Server SDK version: 0.2.0
    14:56:39.982 [main] INFO  o.e.m.o.s.s.n.OpcUaNamespace - Loaded nodes in 1859ms.
    14:56:40.468 [main] INFO  o.e.m.o.sdk.server.NamespaceManager - added namespace index=0, uri=http://opcfoundation.org/UA/
    14:56:40.489 [main] INFO  o.e.m.o.sdk.server.NamespaceManager - registered and added namespace index=1, uri=urn:hurence:opc:test-server:79ea1ee4-9a41-4990-9a47-e74acf0469f4
    14:56:40.512 [main] INFO  o.e.m.opcua.sdk.server.OpcUaServer - Binding endpoint opc.tcp://genuine.microsoft.com:51356/test to 127.0.0.1 [None/None]
    14:56:40.516 [main] INFO  o.e.m.opcua.sdk.server.OpcUaServer - Binding endpoint opc.tcp://genuine.microsoft.com:51356/test to 127.0.0.1 [Basic128Rsa15/SignAndEncrypt]
    14:56:40.516 [main] INFO  o.e.m.opcua.sdk.server.OpcUaServer - Binding endpoint opc.tcp://genuine.microsoft.com:51356/test to 127.0.0.1 [Basic256/SignAndEncrypt]
    14:56:40.517 [main] INFO  o.e.m.opcua.sdk.server.OpcUaServer - Binding endpoint opc.tcp://genuine.microsoft.com:51356/test to 127.0.0.1 [Basic256Sha256/SignAndEncrypt]
    14:56:40.518 [main] INFO  o.e.m.opcua.sdk.server.OpcUaServer - Binding endpoint opc.tcp://genuine.microsoft.com:51356/test to 127.0.0.1 [Aes128_Sha256_RsaOaep/SignAndEncrypt]
    14:56:40.518 [main] INFO  o.e.m.opcua.sdk.server.OpcUaServer - Binding endpoint opc.tcp://genuine.microsoft.com:51356/test to 127.0.0.1 [Aes256_Sha256_RsaPss/SignAndEncrypt]
    14:56:40.520 [main] INFO  o.e.m.opcua.sdk.server.OpcUaServer - Binding endpoint opc.tcp://127.0.0.1:51356/test to 127.0.0.1 [None/None]
    14:56:40.521 [main] INFO  o.e.m.opcua.sdk.server.OpcUaServer - Binding endpoint opc.tcp://127.0.0.1:51356/test to 127.0.0.1 [Basic128Rsa15/SignAndEncrypt]
    14:56:40.521 [main] INFO  o.e.m.opcua.sdk.server.OpcUaServer - Binding endpoint opc.tcp://127.0.0.1:51356/test to 127.0.0.1 [Basic256/SignAndEncrypt]
    14:56:40.521 [main] INFO  o.e.m.opcua.sdk.server.OpcUaServer - Binding endpoint opc.tcp://127.0.0.1:51356/test to 127.0.0.1 [Basic256Sha256/SignAndEncrypt]
    14:56:40.521 [main] INFO  o.e.m.opcua.sdk.server.OpcUaServer - Binding endpoint opc.tcp://127.0.0.1:51356/test to 127.0.0.1 [Aes128_Sha256_RsaOaep/SignAndEncrypt]
    14:56:40.521 [main] INFO  o.e.m.opcua.sdk.server.OpcUaServer - Binding endpoint opc.tcp://127.0.0.1:51356/test to 127.0.0.1 [Aes256_Sha256_RsaPss/SignAndEncrypt]
    14:56:40.646 [main] INFO  o.e.m.o.sdk.server.NamespaceManager - registered and added namespace index=2, uri=urn:test:namespace
    14:56:40.646 [main] INFO  com.hurence.opc.ua.TestOpcServer - Created OPC-UA server running on opc.tcp://localhost/127.0.0.1:51356
    14:56:41.076 [main] INFO  o.e.m.o.s.s.tcp.UaTcpStackServer - opc.tcp://genuine.microsoft.com:51356/test bound to 127.0.0.1:51356 [None/None]
    14:56:41.080 [main] DEBUG o.e.m.o.s.s.t.SocketServers$SocketServer - Added server at path: "test/discovery"
    14:56:41.080 [main] INFO  o.e.m.o.s.s.tcp.UaTcpStackServer - opc.tcp://genuine.microsoft.com:51356/test bound to 127.0.0.1:51356 [Basic128Rsa15/SignAndEncrypt]
    14:56:41.081 [main] INFO  o.e.m.o.s.s.tcp.UaTcpStackServer - opc.tcp://genuine.microsoft.com:51356/test bound to 127.0.0.1:51356 [Basic256/SignAndEncrypt]
    14:56:41.081 [main] INFO  o.e.m.o.s.s.tcp.UaTcpStackServer - opc.tcp://genuine.microsoft.com:51356/test bound to 127.0.0.1:51356 [Basic256Sha256/SignAndEncrypt]
    14:56:41.082 [main] INFO  o.e.m.o.s.s.tcp.UaTcpStackServer - opc.tcp://genuine.microsoft.com:51356/test bound to 127.0.0.1:51356 [Aes128_Sha256_RsaOaep/SignAndEncrypt]
    14:56:41.082 [main] INFO  o.e.m.o.s.s.tcp.UaTcpStackServer - opc.tcp://genuine.microsoft.com:51356/test bound to 127.0.0.1:51356 [Aes256_Sha256_RsaPss/SignAndEncrypt]
    14:56:41.082 [main] INFO  o.e.m.o.s.s.tcp.UaTcpStackServer - opc.tcp://genuine.microsoft.com:51356/test/discovery bound to 127.0.0.1:51356 [None/None]
    14:56:41.083 [main] INFO  o.e.m.o.s.s.tcp.UaTcpStackServer - opc.tcp://127.0.0.1:51356/test bound to 127.0.0.1:51356 [None/None]
    14:56:41.083 [main] INFO  o.e.m.o.s.s.tcp.UaTcpStackServer - opc.tcp://127.0.0.1:51356/test bound to 127.0.0.1:51356 [Basic128Rsa15/SignAndEncrypt]
    14:56:41.086 [main] INFO  o.e.m.o.s.s.tcp.UaTcpStackServer - opc.tcp://127.0.0.1:51356/test bound to 127.0.0.1:51356 [Basic256/SignAndEncrypt]
    14:56:41.091 [main] INFO  o.e.m.o.s.s.tcp.UaTcpStackServer - opc.tcp://127.0.0.1:51356/test bound to 127.0.0.1:51356 [Basic256Sha256/SignAndEncrypt]
    14:56:41.092 [main] INFO  o.e.m.o.s.s.tcp.UaTcpStackServer - opc.tcp://127.0.0.1:51356/test bound to 127.0.0.1:51356 [Aes128_Sha256_RsaOaep/SignAndEncrypt]
    14:56:41.092 [main] INFO  o.e.m.o.s.s.tcp.UaTcpStackServer - opc.tcp://127.0.0.1:51356/test bound to 127.0.0.1:51356 [Aes256_Sha256_RsaPss/SignAndEncrypt]
    14:56:41.092 [main] INFO  o.e.m.o.s.s.tcp.UaTcpStackServer - opc.tcp://127.0.0.1:51356/test/discovery bound to 127.0.0.1:51356 [None/None]
    14:56:41.096 [main] DEBUG o.e.m.o.s.s.t.SocketServers$SocketServer - Added server at path: "test"
    14:56:41.101 [main] INFO  o.e.m.o.s.s.tcp.UaTcpStackServer - opc.tcp://genuine.microsoft.com:51356/test bound to 127.0.0.1:51356 [None/None]
    14:56:41.103 [main] INFO  o.e.m.o.s.s.tcp.UaTcpStackServer - opc.tcp://genuine.microsoft.com:51356/test bound to 127.0.0.1:51356 [Basic128Rsa15/SignAndEncrypt]
    14:56:41.103 [main] INFO  o.e.m.o.s.s.tcp.UaTcpStackServer - opc.tcp://genuine.microsoft.com:51356/test bound to 127.0.0.1:51356 [Basic256/SignAndEncrypt]
    14:56:41.103 [main] INFO  o.e.m.o.s.s.tcp.UaTcpStackServer - opc.tcp://genuine.microsoft.com:51356/test bound to 127.0.0.1:51356 [Basic256Sha256/SignAndEncrypt]
    14:56:41.104 [main] INFO  o.e.m.o.s.s.tcp.UaTcpStackServer - opc.tcp://genuine.microsoft.com:51356/test bound to 127.0.0.1:51356 [Aes128_Sha256_RsaOaep/SignAndEncrypt]
    14:56:41.104 [main] INFO  o.e.m.o.s.s.tcp.UaTcpStackServer - opc.tcp://genuine.microsoft.com:51356/test bound to 127.0.0.1:51356 [Aes256_Sha256_RsaPss/SignAndEncrypt]
    14:56:41.104 [main] INFO  o.e.m.o.s.s.tcp.UaTcpStackServer - opc.tcp://127.0.0.1:51356/test bound to 127.0.0.1:51356 [None/None]
    14:56:41.104 [main] INFO  o.e.m.o.s.s.tcp.UaTcpStackServer - opc.tcp://127.0.0.1:51356/test bound to 127.0.0.1:51356 [Basic128Rsa15/SignAndEncrypt]
    14:56:41.104 [main] INFO  o.e.m.o.s.s.tcp.UaTcpStackServer - opc.tcp://127.0.0.1:51356/test bound to 127.0.0.1:51356 [Basic256/SignAndEncrypt]
    14:56:41.105 [main] INFO  o.e.m.o.s.s.tcp.UaTcpStackServer - opc.tcp://127.0.0.1:51356/test bound to 127.0.0.1:51356 [Basic256Sha256/SignAndEncrypt]
    14:56:41.106 [main] INFO  o.e.m.o.s.s.tcp.UaTcpStackServer - opc.tcp://127.0.0.1:51356/test bound to 127.0.0.1:51356 [Aes128_Sha256_RsaOaep/SignAndEncrypt]
    14:56:41.106 [main] INFO  o.e.m.o.s.s.tcp.UaTcpStackServer - opc.tcp://127.0.0.1:51356/test bound to 127.0.0.1:51356 [Aes256_Sha256_RsaPss/SignAndEncrypt]
    14:56:41.828 [main] INFO  com.hurence.opc.ua.OpcUaTemplate - Discovering OCP-UA endpoints from opc.tcp://genuine.microsoft.com:51356/test
    14:56:41.863 [main] DEBUG o.e.m.o.s.c.ClientChannelManager - connect(), currentState=NotConnected
    14:56:41.885 [main] DEBUG o.e.m.o.s.c.ClientChannelManager - connect() while NotConnected
    java.lang.Exception: null
    	at org.eclipse.milo.opcua.stack.client.ClientChannelManager.connect(ClientChannelManager.java:67) [stack-client-0.2.0.jar:0.2.0]
    	at org.eclipse.milo.opcua.stack.client.UaTcpStackClient.connect(UaTcpStackClient.java:127) [stack-client-0.2.0.jar:0.2.0]
    	at org.eclipse.milo.opcua.stack.client.UaTcpStackClient.getEndpoints(UaTcpStackClient.java:568) [stack-client-0.2.0.jar:0.2.0]
    	at com.hurence.opc.ua.OpcUaTemplate.discoverEndpoints(OpcUaTemplate.java:217) [classes/:na]
    	at com.hurence.opc.ua.OpcUaTemplate.clientConfig(OpcUaTemplate.java:151) [classes/:na]
    	at com.hurence.opc.ua.OpcUaTemplate.doConnect(OpcUaTemplate.java:279) [classes/:na]
    	at com.hurence.opc.ua.OpcUaTemplate.lambda$connect$3(OpcUaTemplate.java:261) [classes/:na]
    	at io.reactivex.internal.operators.completable.CompletableFromAction.subscribeActual(CompletableFromAction.java:34) ~[rxjava-2.2.4.jar:na]
    	at io.reactivex.Completable.subscribe(Completable.java:2255) ~[rxjava-2.2.4.jar:na]
    	at io.reactivex.internal.operators.completable.CompletableConcatArray$ConcatInnerObserver.next(CompletableConcatArray.java:89) ~[rxjava-2.2.4.jar:na]
    	at io.reactivex.internal.operators.completable.CompletableConcatArray.subscribeActual(CompletableConcatArray.java:33) ~[rxjava-2.2.4.jar:na]
    	at io.reactivex.Completable.subscribe(Completable.java:2255) ~[rxjava-2.2.4.jar:na]
    	at io.reactivex.internal.operators.single.SingleDelayWithCompletable.subscribeActual(SingleDelayWithCompletable.java:36) ~[rxjava-2.2.4.jar:na]
    	at io.reactivex.Single.subscribe(Single.java:3495) ~[rxjava-2.2.4.jar:na]
    	at io.reactivex.internal.operators.completable.CompletableFromSingle.subscribeActual(CompletableFromSingle.java:29) ~[rxjava-2.2.4.jar:na]
    	at io.reactivex.Completable.subscribe(Completable.java:2255) ~[rxjava-2.2.4.jar:na]
    	at io.reactivex.Completable.blockingAwait(Completable.java:1186) ~[rxjava-2.2.4.jar:na]
    	at com.hurence.opc.ua.OpcUaTemplateTest.connectionUserPasswordSuccessTest(OpcUaTemplateTest.java:111) ~[test-classes/:na]
    	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[na:1.8.0_144]
    	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) ~[na:1.8.0_144]
    	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[na:1.8.0_144]
    	at java.lang.reflect.Method.invoke(Method.java:498) ~[na:1.8.0_144]
    	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50) ~[junit-4.12.jar:4.12]
    	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12) ~[junit-4.12.jar:4.12]
    	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47) ~[junit-4.12.jar:4.12]
    	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17) ~[junit-4.12.jar:4.12]
    	at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325) ~[junit-4.12.jar:4.12]
    	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:78) ~[junit-4.12.jar:4.12]
    	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:57) ~[junit-4.12.jar:4.12]
    	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:290) ~[junit-4.12.jar:4.12]
    	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71) ~[junit-4.12.jar:4.12]
    	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288) ~[junit-4.12.jar:4.12]
    	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:58) ~[junit-4.12.jar:4.12]
    	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268) ~[junit-4.12.jar:4.12]
    	at org.junit.internal.runners.statements.RunBefores.evaluate(RunBefores.java:26) ~[junit-4.12.jar:4.12]
    	at org.junit.internal.runners.statements.RunAfters.evaluate(RunAfters.java:27) ~[junit-4.12.jar:4.12]
    	at org.junit.runners.ParentRunner.run(ParentRunner.java:363) ~[junit-4.12.jar:4.12]
    	at org.junit.runner.JUnitCore.run(JUnitCore.java:137) ~[junit-4.12.jar:4.12]
    	at com.intellij.junit4.JUnit4IdeaTestRunner.startRunnerWithArgs(JUnit4IdeaTestRunner.java:68) ~[junit-rt.jar:na]
    	at com.intellij.rt.execution.junit.IdeaTestRunner$Repeater.startRunnerWithArgs(IdeaTestRunner.java:47) ~[junit-rt.jar:na]
    	at com.intellij.rt.execution.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:242) ~[junit-rt.jar:na]
    	at com.intellij.rt.execution.junit.JUnitStarter.main(JUnitStarter.java:70) ~[junit-rt.jar:na]
    14:56:43.327 [main] WARN  com.hurence.opc.ua.OpcUaTemplate - Received 6 endpoint descriptions from opc.tcp://genuine.microsoft.com:51356/test
    14:56:43.750 [main] INFO  com.hurence.opc.ua.OpcUaTemplate - Connecting to OPC-UA endpoint
    Server: ApplicationDescription{ApplicationUri=urn:hurence:opc:test-server:79ea1ee4-9a41-4990-9a47-e74acf0469f4, ProductUri=urn:hurence:opc:test-server, ApplicationName=LocalizedText{text=Hurence OPC UA Test Server, locale=en}, ApplicationType=Server, GatewayServerUri=null, DiscoveryProfileUri=null, DiscoveryUrls=[opc.tcp://genuine.microsoft.com:51356/test]}
    Url: opc.tcp://genuine.microsoft.com:51356/test
    Security policy: http://opcfoundation.org/UA/SecurityPolicy#None
    Server identity: [
    [
      Version: V3
      Subject: C=FR, ST=, L=Lyon, OU=dev, O=Hurence, CN=Hurence test
      Signature Algorithm: SHA256withRSA, OID = 1.2.840.113549.1.1.11
    
      Key:  Sun RSA public key, 2048 bits
      modulus: 19702820052628541184822174161466051865214469911650300697380573025881852602918401839732645926052708764885841891116415883267858328254831015596211805766271982304176305280294913352565903195173595614009902127643814249022626030998471371132470925325653458988319412729615506521198659036867573615922083779404361501972943964866898002286646555251463115682078841509387273125981711718501686268893252811806761826116534325762249752595150759347564722469521067725101462873673462340097053956993084726792037551495045079574657367189019834591736688232785809955527924584407970300056484297345770425335866127851743283539276724501794766806253
      public exponent: 65537
      Validity: [From: Fri Apr 26 00:00:00 CST 2019,
                   To: Tue Apr 26 00:00:00 CST 2022]
      Issuer: C=FR, ST=, L=Lyon, OU=dev, O=Hurence, CN=Hurence test
      SerialNumber: [    016a586e 7d07]
    
    Certificate Extensions: 6
    [1]: ObjectId: 2.5.29.35 Criticality=false
    AuthorityKeyIdentifier [
    KeyIdentifier [
    0000: 0E F5 2C CB F3 72 EC 25   C3 41 D8 75 54 ED DD 54  ..,..r.%.A.uT..T
    0010: D1 5C 23 7C                                        .\#.
    ]
    ]
    
    [2]: ObjectId: 2.5.29.19 Criticality=false
    BasicConstraints:[
      CA:true
      PathLen:2147483647
    ]
    
    [3]: ObjectId: 2.5.29.37 Criticality=false
    ExtendedKeyUsages [
      clientAuth
      serverAuth
    ]
    
    [4]: ObjectId: 2.5.29.15 Criticality=false
    KeyUsage [
      DigitalSignature
      Non_repudiation
      Key_Encipherment
      Data_Encipherment
      Key_Agreement
      Key_CertSign
    ]
    
    [5]: ObjectId: 2.5.29.17 Criticality=false
    SubjectAlternativeName [
      URIName: urn:hurence:opc:test-server:79ea1ee4-9a41-4990-9a47-e74acf0469f4
    ]
    
    [6]: ObjectId: 2.5.29.14 Criticality=false
    SubjectKeyIdentifier [
    KeyIdentifier [
    0000: 0E F5 2C CB F3 72 EC 25   C3 41 D8 75 54 ED DD 54  ..,..r.%.A.uT..T
    0010: D1 5C 23 7C                                        .\#.
    ]
    ]
    
    ]
      Algorithm: [SHA256withRSA]
      Signature:
    0000: 65 1D 7B 13 DF 75 5B 73   12 FB A1 88 BB D9 D8 B8  e....u[s........
    0010: 66 89 AC 7F 20 7A 50 9B   75 4C 6C E2 41 18 82 CF  f... zP.uLl.A...
    0020: AE 9A E4 0F 1F AA 92 47   18 C8 12 ED 8E A5 90 75  .......G.......u
    0030: 76 21 3E 06 6B 25 4A 54   3B AA 49 3F DE 0D 16 9A  v!>.k%JT;.I?....
    0040: D6 B3 DD FF FA 36 79 F9   BB 20 7A B0 9D 00 A8 21  .....6y.. z....!
    0050: 7D 49 C8 7C 2B B6 15 AC   23 AC A9 D2 32 BB CC 75  .I..+...#...2..u
    0060: 59 9C 29 1A 61 00 0E 7B   31 3E 98 7D 94 2C 06 D4  Y.).a...1>...,..
    0070: 42 85 85 7A 04 83 9E CD   D1 49 90 20 D8 53 CE B5  B..z.....I. .S..
    0080: 4E 95 F1 28 2C 33 0C 69   E0 C1 B4 E6 07 1A 3C EB  N..(,3.i......<.
    0090: 73 44 13 AF 8E CD 1C A9   A5 E2 FA 24 30 87 E5 E2  sD.........$0...
    00A0: E3 C0 AD DD 12 41 5E 26   73 61 DC 04 2D 05 38 80  .....A^&sa..-.8.
    00B0: 7A B8 14 06 EA 04 84 82   5C 9B D2 B1 15 2E A3 07  z.......\.......
    00C0: FF EF ED 3A 83 3A 91 C5   3F 10 C7 CD 07 D2 03 2D  ...:.:..?......-
    00D0: 6D 6A 03 3F A7 98 B1 AA   FA 95 68 EA B2 62 89 3C  mj.?......h..b.<
    00E0: 45 09 01 52 77 77 23 BA   61 34 75 D5 01 08 9E 40  E..Rww#.a4u....@
    00F0: 24 08 0D 44 37 13 D7 05   2C 8B AC A9 5E E0 CC 86  $..D7...,...^...
    
    ]
    14:56:43.908 [main] INFO  o.e.m.opcua.sdk.client.OpcUaClient - Eclipse Milo OPC UA Stack version: 0.2.0
    14:56:43.908 [main] INFO  o.e.m.opcua.sdk.client.OpcUaClient - Eclipse Milo OPC UA Client SDK version: 0.2.0

    
    流程
        1.初始话server
            1.用户密码验证    UsernameIdentityValidator 
            2.x509证书验证    X509IdentityValidator
            3.获取ip和端口
            4.生产RSA KeyPair         SelfSignedCertificateGenerator.generateRsaKeyPair
            5.构建OpcUaServerConfig
            
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        