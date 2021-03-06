
##Netflix 云网关团队一直致力于帮助系统减少错误、提高可用性，增强 Netflix 应对故障的能力。



这么做是因为在每秒超过一百万次请求的这等规模下，哪怕很低的错误率也会影响会员的体验，所以降低错误率是必要的。



因此，我们开始向 Zuul 和其他团队取经，改进我们的负载均衡机制，进一步减少服务器过载导致的错误。



在 Zuul 中，我们历来使用 Ribbon 负载均衡器（https://github.com/Netflix/ribbon/），另外使用轮询调度（round-robin）算法和用于将连接故障率高的服务器列入黑名单的一些过滤机制。



这些年来，我们做了多次改进和定制，旨在向最近启动的服务器发送较少的流量以免过载。



这些取得了显著的成效，但对于一些特别麻烦的源集群，我们仍会看到与负载有关的错误率远高于预期。



如果集群中所有服务器过载，我们选择某一台服务器而不是另一台几乎没什么改进。



但我们常看到只有一部分服务器过载的情况，比如：

服务器在启动后（在红黑部署和自动扩展事件期间）。

服务器因交错的动态属性/脚本/数据更新或大型垃圾回收（GC）事件而暂时减速/阻塞。

坏的服务器硬件。我们会常看到一些服务器的运行速度永远不如其他服务器，无论原因是嘈杂的相邻系统还是不同的硬件。


指导原则



开始做项目时有必要恪守几个原则，以下是该项目遵循的几个原则。



注重现有负载均衡器框架的约束



我们将之前的负载均衡定制与 Zuul 代码库相结合，因而无法与 Netflix 的其他团队共享这些定制。



于是这回我们决定接受约束和所需的额外投入，一开始就牢记重用性。因而更容易被其他系统所采用，减小了重新发明轮子的机会。



向别人借鉴经验



试着借鉴别人的想法和技术。比如，之前在 Netflix 的其他 IPC 堆栈中考察过的 choice-of-2 和考察（probation）算法。



避免分布式状态



优先考虑本地决策，避免跨集群协调状态带来的弹性问题、复杂性和延滞。



避免客户端配置和手动调整



多年来我们在 Zuul 方面的运营经验表明，将服务配置的一部分放在不属于同一团队的客户端服务中会导致问题。



一个问题是，这些客户端配置往往与不断变化的服务器端配置不同步，或者需要结合属于不同团队的服务之间的变更管理。



比如说，升级用于服务 X 的 EC2 实例类型，导致该集群所需的节点更少。因此，现在应增加服务 Y 中“每个主机的最大连接数”客户端配置，以体现新增的容量。



应该先进行客户端更改？还是进行服务器端更改？还是同时进行？设置很有可能完全被遗忘，导致更多的问题。



如果可能的话，使用根据当前流量、性能和环境来更改的自适应机制，而不是配置静态阈值。



若确实需要静态阈值，让服务在运行时传达这一切，避免跨团队推送变更带来的问题，而不是让服务团队协调每个客户端的阈值配置。



负载均衡方法



一个总体思路是，虽然对服务器端延迟而言最佳的数据源是客户端视图，但服务器利用率方面的最佳数据源来自服务器本身。结合这两个数据源可为我们提供最有效的负载均衡。



我们结合使用了相互补充的机制，大多数机制之前由别人开发和使用：

在服务器之间选择的 choice-of-2 算法。

主要根据负载均衡器了解服务器利用率的情况进行均衡。

其次根据服务器了解利用率的情况进行均衡。

基于考察和服务器年限的机制，避免刚启动的服务器过载。

收集的服务器统计数据慢慢衰减为零。


结合加入最短队列和服务器报告的利用率



我们选择结合常用的加入最短队列（JSQ）算法和基于服务器报告的利用率的 choice-of-2 算法，试图集两者之所长。



JSQ 的问题



加入最短队列非常适用于单个负载均衡器，但如果单独用在负载均衡器集群中会有严重问题。



问题是，负载均衡器往往会放牧（herd），同时选择相同的低利用率服务器，因而过载，然后进入到下一台利用率最低的服务器并使它过载，依次类推.....



这可以通过结合使用 JSQ 和 choice-of-2 算法来解决。这基本上解决了放牧问题。



JSQ 一般通过计算仅从本地负载均衡器到服务器的使用中连接的数量来实现，但是负载均衡器节点有几十个至几百个时，本地视图可能会误导。



图 1：单个负载均衡器的视图可能与实际情况大不相同



比如在此图中，负载均衡器 A 对服务器 X 有 1 个正在处理（inflight）的请求，对服务器 Z 有 1 个请求，但对服务器 Y 没有请求。



因此它收到新请求、要选择哪台服务器的利用率最低时，从可用数据来看，它会选择服务器 Y。



虽然这不是正确的选择，服务器 Y 实际上是利用率最高的，因为另外两个负载均衡器目前都有正在处理的请求，但负载均衡器 A 无法知道这点。这表明了单个负载均衡器的视图如何与实际情况完全不同。



我们仅依赖客户端视图遇到的另一个问题是，对于庞大集群（尤其是结合低流量）而言，负载均衡器常常与几百台服务器中的一部分服务器只有几条正在使用的连接。



因此，当它选择哪台服务器的负载最小时，通常只能在零和零之间选择，即它没有它要选择的任何一台服务器的利用率方面的数据，因此只好随机猜测。



解决这个问题的一个办法是，与所有其他负载均衡器共享每个负载均衡器的正在处理的请求数量的状态，但那样就要解决分布式状态问题。



我们通常采用分布式可变状态作为最后的手段，因为获得的好处需要压倒涉及的实际成本：

给部署和金丝雀测试（canarying）等任务增添了运营开销和复杂性。

与数据损坏的影响范围有关的弹性风险（即 1% 负载均衡器上的坏数据很烦人，100% 负载均衡器上的坏数据是故障）。

在负载均衡器之间实现 P2P 分布式状态系统的成本，或运行拥有处理这庞大读写流量所需的性能和弹性的单独数据库的成本。



我们选择了另一个更简单的解决方案，改而依赖向每个负载均衡器报告服务器的利用率。



服务器报告的利用率



使用每台服务器了解的利用率有这个优点：它整合了使用该服务器的所有负载均衡器的情况，因此避免了 JSQ 的问题：了解的情况不完整。



我们有两种方法可以实现这一点：

使用健康状况检查端点，主动轮询每台服务器的当前利用率。

被动跟踪来自用当前利用率数据标注的服务器的响应。



我们选择了第二种方法，原因很简单，便于频繁更新这些数据，还避免了给服务器增添额外的负担：每隔几秒就让 N 个负载均衡器轮询 M 台服务器。



这种被动策略的一个影响是，负载均衡器向一台服务器发送请求的频次越高，它获得的该服务器利用率的视图越新。



因此，每秒请求数（RPS）越高，负载均衡的效果越好。但反过来，RPS 越低，负载均衡效果越差。



这对我们来说不是问题，但对于通过一个特定负载均衡器收到低 RPS（同时通过另一个负载均衡器收到高 RPS）的服务来说，主动轮询健康状况检查可能更有效。



转折点出现在负载均衡器向每台服务器发送的 RPS 低于用来健康状况检查的轮询频次。



服务器端实现



我们在服务器端实现了这个机制，只需跟踪正在处理的请求数，将其转换成该服务器的配置最大值百分比，然后将其作为 HTTP 响应头来写出：

X-Netflix.server.utilization: <current-utilization>[, target=<target-utilization>]


可以由服务器指定可选的目标利用率，表明它们在正常条件下打算以怎样的百分比利用率来运行。然后负载均衡器将其用于稍后介绍的粗粒度过滤。



我们用正在处理的请求数之外的指标进行了一番实验，比如操作系统报告的 CPU 利用率和平均负载，但发现它们引起了变动。因此，我们决定使用相对简单的实现方法：只计算正在处理的请求。



choice-of-2 算法而不是轮询调度算法



由于我们希望能够通过比较统计数据来选择服务器，因此弃用了现有的简单的轮询调度算法。



我们尝试的另一种方法是 JSQ 结合 ServerListSubsetFilter 减少 distributed-JSQ 的放牧问题。这得到了合理的结果，但是因而在目标服务器上的请求分布仍然太宽。



因此，我们改而运用了 Netflix 的另一个团队汲取的早期经验，实现了 choice-of-2 算法。优点是易于实现，使负载均衡器上的 CPU 成本保持很低，且请求分布良好。



基于多个因素进行选择



要在服务器之间进行选择，我们针对三个不同的因素来比较：

客户端健康状况：该服务器的连接相关错误的滚动百分比。

服务器利用率：该服务器提供的最新分数。

客户端利用率：从该负载均衡器到该服务器的正在处理的请求的当前数量。

 

这三个因素用于为每台服务器分配分数，然后比较总分数来选择获胜者。使用这样的多个因素确实加大了实现的复杂性，但可以防范仅仅依赖一个因素而出现的极端情况问题。



比如说，如果一台服务器开始失效、拒绝所有请求，它报告利用率会低得多（由于拒绝请求比接受请求更快）；如果这是唯一所用的因素，那么所有负载均衡器将开始向这台坏的服务器发送更多的请求。客户端健康状况因素可缓解这种情形。



过滤



随机选择要比较的 2 台服务器时，我们过滤掉高于针对利用率和健康状况而保守配置的阈值的任何服务器。



对每个请求进行这种过滤，避免只是定期过滤的过时问题。为避免在负载均衡器上造成高 CPU 负载，我们只尽力而为：作 N 次尝试以找到一台随机选择的可行服务器，然后必要时回退到非过滤的服务器。



服务器池中大部分服务器存在持久性问题时，这种过滤大有帮助。在这种情况下，随机选择 2 台服务器会经常导致选择 2 台糟糕的服务器进行比较，尽管有许多好的服务器可用。



但这么做的缺点是依赖静态配置的阈值，我们试图避免这种情况。不过，测试结果让我们相信值得添加这种方法，即便使用了一些通用的阈值。



考察



对于负载均衡器尚未收到响应的任何服务器而言，我们只允许每次只有一个正在处理的请求。我们过滤掉这些考察期（in-probation）的服务器，直至收到来自它们的响应。



这有助于在让刚启动的服务器有机会表明利用率如何之前，避免它们因大量请求而过载。



基于服务器年限的预热



我们利用服务器年限在前 90 秒内逐步加大发送到刚启动的服务器的流量。这是另一个类似考察的机制，进一步提醒在有时微妙的启动后状态避免服务器过载。



统计数据衰减



为了确保服务器不被永久性列入黑名单，我们对收集用于负载均衡中的所有统计数据使用了衰减率（decay rate，目前是 30 秒内的线性衰减）。



比如说，如果服务器的错误率上升到 80%、我们停止向它发送流量，我们使用的值将在 30 秒内衰减到零（即 15 秒后错误率将是 40%）。



运营影响



更宽的请求分布



不将轮询调度用于负载均衡的负面影响时，之前我们跨集群服务器有非常紧凑的请求分布，现在服务器之间的变化（delta）更大。



使用 choice-of-2 算法有助于大大缓解这种情况（与跨集群中所有或部分服务器的 JSQ 相比），但不可能完全避免。



因此需要在运营方面考虑这一点，对于我们通常比较请求数、错误率和 CPU 等指标的绝对值的金丝雀分析而言更是如此。



较慢的服务器收到较少的流量



很显然这是预期的效果，但对于过去常用轮询调度（流量平均分配）的团队来说，这在运营方面带来了一些连锁反应。



由于跨源服务器的流量分布现在依赖其利用率，如果一些服务器在运行效率更高或更低的不同构建（build），会收到更多或更少的流量，所以：

集群采用红黑部署时，如果新的服务器组出现了性能衰退，发送到该组的流量其比例就会小于 50%。

金丝雀系统方面可以看到相同的结果――基线系统可能收到与金丝雀集群不同大小的流量。因此查看指标时，最好看看 RPS 和 CPU 的组合（比如金丝雀系统中 RPS 可能较低，而 CPU 相同）。

不太高效的异常检测――我们通常有自动化技术来监控集群中的异常服务器（通常是启动后因某个硬件问题而立即慢下来的虚拟机），并终止它们。那些异常服务器因负载均衡而收到较少的流量时，这种检测更难了。



滚动动态数据更新



从轮询调度改用这种新负载均衡器的好处是，可以与分阶段部署动态数据和属性很好地结合起来。



我们的最佳实践是每次一个区域（数据中心）部署数据更新，限制意外问题的影响范围。



即使没有数据更新本身引起的任何问题，服务器进行更新这个行为也可能导致短暂的负载峰值（通常与垃圾回收有关）。



如果集群中所有服务器上同时出现该峰值，可能导致负载分流（load-shedding）出现较大的峰值，错误向上游传播。这种情况下，负载均衡器基本上没多大帮助，因为所有服务器都遇到高负载。



不过有一个解决办法（若与这样的自适应负载均衡器结合使用）是在集群服务器上进行滚动数据更新。



如果只有一小部分服务器同时进行更新，负载均衡器可暂时减少发送到它们的流量，只要集群中有足够的其他服务器来承担转移的流量。



合成负载测试结果



我们广泛使用合成负载测试场景，同时开发、测试和调整该负载均衡器的不同方面。



这对于验证实际集群和网络的效果非常有用，作为单元测试之上的可重现步骤，但尚未使用实际的用户流量。



图 2：结果比较



关于该测试的更多详细信息，要点总结如下：

与轮询调度方法相比，新负载均衡器在启用所有功能后，负载分流和连接错误减少了好几个数量级。

平均和长尾延迟有了实质性改进（与轮询调度方法相比减少 3 倍）。

单单添加服务器利用率特性就大有好处，错误减少了一个数量级，并大大降低了延迟。



对实际生产流量的影响



我们发现新负载均衡器在为每个源服务器分配尽可能多的流量这方面非常有效。



这么做的好处是，路由绕过性能间歇性降级的服务器和持续性降级的服务器，无需任何人工干预，这避免了半夜叫醒工程师、严重影响白天工作效率的问题。



正常运行期间很难表明这种影响，但在生产事故期间就能看清；对于一些服务而言，甚至在正常的稳态运行期间就能看清。



事故期间



最近发生了一起事故：服务中的一个 Bug 导致越来越多的服务器线程慢慢阻塞，即从已启动服务器的角度来看，几个线程每小时就会阻塞，直到服务器最终开始达到最大值并分流负载。



在显示每台服务器 RPS 的下图中，可以看到凌晨 3 点之前，服务器之间的分布很宽。这是由于负载均衡器向阻塞线程数较多的服务器发送的流量较少。



然后凌晨 3：25 之后，自动扩展机制开始启动更多的服务器，每台服务器收到的 RPS 是现有服务器的约两倍，因为它们还没有任何阻塞线程，因此可以成功地处理更多流量。



图 3：每台服务器的每秒请求数



现在如果看看同一时间范围内每台服务器的错误率这张图，可以看到整个事件中所有服务器上的错误分布呈均匀分布，尽管我们知道一些服务器的容量比其他服务器低得多。



这表明负载均衡器在有效运行。由于集群中总可用容量太少，所有服务器都在略高于有效容量的负载下运行。



然后自动扩展机制启动新服务器时，向这些服务器发送尽可能多的流量，直到出现的错误与集群中其余服务器一样少。



图 4：每台服务器的每秒错误数



总而言之，负载均衡在向服务器分配流量方面非常有效，但在这种情况下，并没有启动足够的新服务器将总错误量一路降低到零。



稳态



我们还看到，因垃圾回收事件而出现几秒负载分流的服务器的一些服务中的稳态噪声大幅减少。可以看到启用新负载均衡器后，错误大大减少：



图 5：启用新负载均衡器前后的几周内与负载有关的错误率



提醒不足



意想不到的是，我们的自动警报机制的一些不足暴露出来。一些基于服务错误率的现有警报（之前问题只影响集群的一小部分时就会触发）现在很久之后才触发，或根本不触发，因为错误率保持较低。



这意味着有时严重的问题在影响集群，团队却未收到相关通知。解决办法是，添加利用率指标（而不仅仅是错误指标）偏差方面的其他警报，以填补不足。