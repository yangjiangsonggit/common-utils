微服务介绍
====
    微服务目前获得了很多的关注：文章，博客，社交媒体上的讨论，和会议演讲。他迅速走向Gartner炒作周期的高峰期。同时，在软件社区中有一部分怀疑论者斥责微服务其实并没有新东西。他们声称这个想法只是SOA（面向服务架构）的一个重塑。然而，尽管有炒作和值得怀疑的成分，微服务模式有其显著的好处 - 特别是需要实践敏捷开发和交付复杂企业应用的时候。
    
    该博客是关于设计、构建、部署微服务的7篇系列文章的第1篇。你将了解到微服务的实现方式，与传统的单体架构模式进行的比较。该系列将会介绍微服务架构中的各种元素。你将了解微服务的优点和缺点，评估该模式是否对你的项目有意义，以及如何应用它。
    
    让我们先看看为什么你应该考虑使用微服务。
    
    构建单体应用
    让我们假设你正在开始构建一个出租车应用，目的是为与Uber和Hailo竞争。通过初步的会议和需求收集，你可能会手工创建或者借助自动生成工具Rails、Spring Boot、Play，或者Maven生成一个新应用。这个新应用将拥有一个模块化的六边形结构，如同下图：
    
    diagram
    
    应用核心是业务逻辑，由一个定义了服务接口、域对象、事件消息的模块所实现。围绕核心的是外部世界接口的适配器。适配器示例包括数据库访问组件，生产和消费消息的消息组件，以及一个暴露内部接口和提供UI的web组件。
    
    尽管这个项目拥有一个逻辑的模块化的结构，但是这个项目通过单体的方式进行打包和部署。打包的实际格式取决于应用程序的语言和框架。例如，java应用程序通常打包为一个war包，在Tomcat或者Jetty上部署。其他java程序会打包为一个自包含的可执行JAR包。相似的，Rails和Node.js应用会打包为一个有层次结构的目录。
    
    应用程序使用这种风格来实现是很平常的。这种方式很容易开发，因为我们的IDE和其他工具专注于构建单一应用程序。这种类型的应用程序同时很容易测试。你可以简单的启动程序，使用Selenium来进行端到端的测试。单体应用同时还十分容易部署 - 你只需要将包拷贝到服务器上。你同时可以在一个负载均衡器后运行多个副本来扩展应用程序。
    
    走向单体模式地狱
    不幸的，这种简单的方式有巨大的局限性。成功的应用有一个随着时间增长的习惯，并最终变得巨大。在每个迭代里，你的研发团队会添加一些功能，这意味着添加几行代码。几年后，你原本小而简单的应用会成长为一头怪兽。有一个极端的例子，我最近与一个开发者交流，他在写一个工具，用于分析在一个应用中的数千个jar包的依赖关系，这个应用包含数百万行代码。我相信这需要许多许多的开发人员花费许多许多年的时间，才能创造出这样的野兽。
    
    一旦你的应用程序变成一个巨大而复杂的东西，你的开发工作可能只能用一个词来形容，“痛苦”。任何敏捷开发和交付的尝试都会岌岌可危。这里最主要的问题是，应用程序过于复杂。它是如此复杂以至于任意一个开发者都不能搞懂它。最终，修复问题和实现新功能变的不可能，而且耗时间。更严重的是，这种情况会变得更遭，因为它是一个螺旋向下的结构。如果代码无法理解，那么改变就无法正确的实施（你如何确信你所修改的一个小功能，不会对你所不能理解的其他部分造成影响？）。最终，你得到了一个怪异的、无法理解的大泥潭。
    
    应用程序的庞大规模同时将减慢研发速度。应用程序越大，启动时间越长。例如，在一份最近的调查中，一些开发人员报告启动时间长达12分钟。我也听说过应用程序需要长达40分钟才能启动的轶事。 如果开发人员必须经常性地重新启动服务器，那么他们大部分的时间都会等待，他们的生产力将受到影响。
    
    庞大而复杂的单体应用的另一个问题，是它的连续部署障碍。 今天，SaaS（软件即服务）的最新技术是，每天多次将更改推入生产环境（一天对项目进行多次迭代部署）。这对于复杂的单体应用来说是非常困难的，因为您必须重新部署整个应用程序以更新它的任何一个部分。我之前提到的冗长的启动时间会将情况变的更糟。 此外，由于更新造成的影响通常不是很清楚，所以很可能需要进行广泛的手动测试。 因此，连续部署是不可能做到的。
    
    当不同模块之间具有资源需求冲突时，单体应用可能难以扩展。 例如，一个模块可能实现了CPU密集型的图像处理逻辑，应该理想地部署在经过计算优化的Amazon EC2实例中。 另一个模块可能是内存数据库，最适合经过内存优化的EC2实例。 然而，由于这些模块需要一起部署，您必须在硬件的选择上妥协。
    
    单体应用的另一个问题是可靠性。 由于所有模块都在同一进程内运行，任何模块中的错误（例如内存泄漏）都可能会导致整个进程崩溃。 此外，由于应用程序的所有实例是相同的，该错误将影响整个应用程序的可用性。
    
    最后，如果想在单体应用中使用新框架和新语言将非常困难。 例如，让我们假设你有2百万行代码使用XYZ框架。 将整个应用程序重写以使用较新的ABC框架将是非常昂贵的（在时间和成本上），即使新框架明显更好。 因此，采用新技术有巨大的障碍。 你将被项目开始时所做的任何技术选择所困住。
    
    总而言之：你有一个成功而关键的业务型应用程序，并且已经发展成一个巨型的单体应用，只有很少（如果有的话）开发人员理解它。 它使用过时的，非生产性的技术，使招聘有才华的开发人员困难。 该应用程序难以扩展，并且不可靠。 因此，敏捷开发和应用程序的交付是不可能的。
    
    那么你将会怎么做？
    
    微服务 - 解决复杂性
    许多组织，如亚马逊，eBay和Netflix，通过采用被称为“微服务”的架构模式解决了这个问题。 这个想法是将您的应用程序分成一组较小的，互连的服务，取代过去构建一个怪异的，单体的应用程序的方式。
    
    服务通常实现一组不同的特征或功能，例如订单管理，客户管理等。每个微服务都是具有自己的六边形架构的小型应用，由业务逻辑以及各种适配器组成。 一些微服务会暴露一个接口供其他微服务或客户端使用。 其他微服务可能实现一个Web UI。 在运行时，每个实例通常是一个云虚拟机或者一个Docker容器。
    
    例如，上述系统的可能分解如下图所示：
    
    diagram
    
    应用程序的每个功能区域现在由其自己的微服务实现。 此外，Web应用程序被分成一组更简单的Web应用程序（例如一个用于乘客，一个用于我们出租车示例中的司机）。 这使得为特定用户，设备或特殊服务部署不同的环境更加容易。
    
    每个后端服务公开一个REST API，大多数服务使用其他服务提供的API。 例如，“司机管理”模块使用“通知服务”来通知感兴趣的司机有一个潜在的行程。 “UI服务”调用其他服务以便渲染网页。 服务还可以使用异步的基于消息的通信。 服务间通信将在本系列后面更详细地介绍。
    
    一些REST API也暴露给司机和乘客使用的移动应用程序。 但是，应用程序不能直接访问后端服务。 相反，他们之间通信由称为API网关的中介协调。 API网关负责负载平衡，缓存，访问控制，API计量和监控等任务，这些都可以使用NGINX有效地实现。 本系列的后续文章将涵盖API网关。
    
    diagram
    
    微服务架构模式对应于伸缩立方的Y轴缩放，伸缩立方是在经典书籍《The Art Of Scalability》中介绍的3D模型。 另外两个缩放轴是X轴缩放（在负载平衡器后运行应用程序的多个相同副本），以及Z轴缩放（或数据分区，约定使用请求中的某一属性，例如：行主键或用户id，经过一定的算法将请求路由到特定服务器）。
    
    应用程序通常会同时使用三种类型的缩放。 Y轴缩放将应用程序分解为微服务，如上面本节第一幅图所示。 在运行时，X轴缩放则在负载均衡器后运行每个服务的多个实例，以实现吞吐量和可用性。 某些应用程序也可能使用Z轴缩放来分割服务。 下图显示了Trip Management服务如何在Amazon EC2上运行、如何在Docker里部署。
    
    diagram
    
    在运行时，Trip Management服务由多个服务实例组成。 每个服务实例都是一个Docker容器。 为了实现高可用性，容器在多个云虚拟机上运行。 在服务实例前面是负载平衡器，例如NGINX，它将请求分配到不同实例。 负载均衡器还可以处理其他问题，如缓存，访问控制，API计量和监控。
    
    微服务架构模式显着的影响着应用程序和数据库之间的关系。 不同于单体架构中所有服务都共享单个数据库，微服务架构中每个服务都有自己的数据库。 一方面，这种方法与企业级数据模型的想法不一致。 另一方面，它经常导致重复数据。 但是，如果您希望从微服务中受益，则应该为每个服务提供单独的数据库，因为它确保了松散耦合。 下图显示了示例应用程序的数据库体系结构。
    
    diagram
    
    每个服务都有自己的数据库。 此外，服务可以使用最适合其需要的类型的数据库，即所谓的多态持久性。 例如，司机管理模块（查找离潜在乘客最近的司机），必须使用能高效的处理地理位置查询的数据库。
    
    表面上，微服务架构模式类似于SOA。 在两种架构中，都包含一系列服务。 然而，考虑微服务架构模式的另一种方式，它是没有商业化、没有一堆服务接口规范、没有企业服务总线的SOA。 基于微服务的应用程序更喜欢简单，轻量级的协议，如REST，而不是WS-。 他们还极力避免使用ESB，而是在微服务本身实现类似ESB的功能。 微服务架构模式也拒绝SOA的其他部分，例如规范模式的概念。
    
    微服务的优点
    微服务架构模式有很多重要的好处。 首先，它解决复杂性的问题。 它将一个怪异的单体应用程序分解为一组服务。 虽然功能的总量不变，但应用程序已分解为可管理的模块或服务集。 每个服务都自己的边界，由通过RPC（远程过程调用）或消息驱动的方式进行调用的API所确定。 微服务架构模式强制开发中实施模块化，而这点在实际中用单体代码库来实现极其困难。 因此，单个服务的开发速度更快，更容易理解和维护。
    
    其次，这种架构使得每个服务能够由专注于该服务的团队独立开发。 开发人员可以自由选择任何技术，只要服务符合API约束。 当然，大多数组织希望避免混乱并限制技术选择。 然而，这种自由意味着开发人员在新项目开始时可以不再需要使用过时的技术。 在编写新服务时，他们可以选择使用最新技术。 此外，由于服务相对较小，使用最新技术重写旧服务变得可行。
    
    第三，微服务架构模式使每个微服务能够独立部署。 当本地服务变更需要部署时，开发人员之间不需要协调。 这些变更可以在测试完成后立即部署。 例如，UI团队可以执行A | B测试并对UI更改进行快速迭代。 微服务架构模式使得连续部署成为可能。
    
    最后，微服务架构模式使得每个服务能够独立扩展。 您可以只部署刚好满足容量和可用性要求的服务实例数。 此外，您可以使用最符合服务资源要求的硬件。 例如，您可以在EC2计算优化实例上部署CPU密集型图像处理服务，并在EC2内存优化实例上部署内存数据库服务。
    
    微服务的缺点
    正如弗雷德·布鲁克斯在30年前写的一样，没有万灵药。 像其他技术一样，微服务架构也有缺点。 一个缺点就是它的名称本身。 微服务一词过分强调服务大小。 事实上，有些开发者主张构建极其细粒度的10-100行代码的服务。 虽然小型服务是更好的，但重要的是要记住，这是一种到达目的的手段，而不是主要目标。 微服务的目标是充分分解应用程序，以便于促进敏捷开发和部署。
    
    微服务的另一个主要缺点是，微服务应用程序是分布式系统，分布式意味着复杂性。 开发人员需要选择和实现基于消息传递或RPC的进程间通信机制。 此外，他们还必须编写额外的代码来处理失败，因为请求的目的地可能很慢或不可用。 虽然这些都不是高深的东西，但是，这的确比在单体应用中通过语言级方法（过程）调用更复杂。
    
    微服务的另一个挑战来自网络分区的数据库架构。 更新多个业务实体的事务很常见。 这些类型的事务在单体应用程序中实现非常简单，因为只有一个数据库。 但是，在基于微服务的应用程序中，您需要更新由不同服务所拥有的多个数据库。 使用分布式事务通常不是一个好的选择，不仅仅因为CAP定理。 它们根本不被许多高度可扩展的NoSQL数据库和消息代理组件所支持。 但您必须解决最终一致性，这对开发人员更具挑战性。
    
    测试微服务应用程序也要复杂得多。 例如，使用诸如Spring Boot的现代框架，编写一个测试类来启动一个单体Web应用程序并测试其REST API是很容易的。 相反，一个用于测试服务的测试类则需要启动该服务及其所依赖的任何服务（或至少为这些服务配置存根）。 再次，这些都不是高深的东西，但重要的是不要低估这样做的复杂性。
    
    微服务架构模式的另一个主要挑战是实施跨多个服务的更改。 例如，让我们假设您正在实现一个需要更改服务A，B和C的功能，其中A依赖于B，B依赖于C。在单体应用程序中，您可以简单地更改相应的模块，整合所有的更改， 并一次部署它们。 相比之下，在微服务架构模式中，您需要仔细规划和协调对每个服务的变更，并将对应的服务进行部署。 例如，您需要更新服务C，然后是服务B，最后是服务A。幸运的是，大多数更改通常只影响一个服务，需要协调的多服务更改相对较少。
    
    部署基于微服务的应用程序也要复杂得多。单体应用程序只需部署在传统负载均衡器后面的一组相同的服务器上。每个应用程序实例都配置有基础设施服务（如数据库和消息代理）的位置（主机和端口）。相比之下，微服务应用程序通常由大量服务组成。例如，根据Adrian Cockcroft，Hailo有160种不同的服务，Netflix有超过600种。每个服务将有多个运行时实例。这是相当大数量的需要进行配置、部署、扩展和监控的动态部件。此外，您还需要实现服务发现机制（在后续部分中讨论），使服务能够发现为了通信所需的任何其他服务的位置（主机和端口）。传统的故障解决方法和基于手动的部署方式无法满足这种复杂程度。因此，成功部署微服务应用程序需要开发人员更好地控制部署方法，以及高水平的自动化。
    
    一种自动化的方法是使用现成的PaaS，如Cloud Foundry。 PaaS为开发人员提供了一种轻松部署和管理其微服务的方法。 它使他们免受诸如采购和配置IT资源等问题的困扰。 同时，配置PaaS的系统和网络专业人员可以确保遵从最佳实践和遵守客户策略。 自动化微服务部署的另一种方法是开发基本上属于您自己的PaaS。 一个典型的起点是使用诸如Kubernetes之类的集群解决方案，结合诸如Docker之类的技术。 在本系列的后面，我们将看到为什么基于软件的应用程序交付方法（如NGINX，它可以在微服务级别轻松处理缓存，访问控制，API计量和监控），可以帮助解决这个问题。
    
    总结
    构建复杂应用程序本质上很困难。 单体式架构仅对简单，轻量级应用程序有意义。 如果你将它用于复杂的应用程序，你将会陷入一个痛苦的世界。 微服务架构模式是复杂的、不断发展的应用程序的更好的选择，尽管有缺点和实施挑战。
    
    在后面的博客文章中，我将深入介绍微服务架构模式各个方面的细节，并讨论诸如服务发现，服务部署以及将单体应用程序重构为微服务的策略等主题。