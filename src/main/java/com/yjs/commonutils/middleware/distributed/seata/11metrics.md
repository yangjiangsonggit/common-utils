Metrics
设计思路
Seata作为一个被集成的数据一致性框架，Metrics模块将尽可能少的使用第三方依赖以降低发生冲突的风险；
Metrics模块将竭力争取更高的度量性能和更低的资源开销，尽可能降低开启后带来的副作用；
插件式——Metrics是否激活、数据如何发布，去取决于是否引入了对应的依赖，例如在TC Server中引入seata-metrics-prometheus，则自动启用并将度量数据发布到Prometheus；
不使用Spring，使用SPI(Service Provider Interface)加载扩展；
初始仅发布核心Transaction相关指标，之后结合社区的需求，逐步完善运维方面的所有其他指标。
模块说明
由1个核心API模块seata-metrics-api和N个对接实现模块如seata-metrics-prometheus构成：

seata-metrics-api模块
此模块是Metrics的核心，将作为Seata基础架构的一部分被TC、TM和RM引用，它内部没有任何具体实现代码，仅包含接口定义，定义的内容包括：

Meter类接口：Gauge、Counter、Timer...
注册容器接口Registry
Measurement发布接口Publisher
提示：Metrics本身在开源领域也已有很多实现，例如

Netflix-Spectator
Dropwizard-Metrics
Dubbo-Metrics
它们有的轻而敏捷，有的重而强大，由于也是“实现”，因此不会纳入seata-metrics-api中，避免实现绑定。

seata-metrics-prometheus模块
这是我们默认提供的Metrics实现，不使用其它Metrics开源实现，并轻量级的实现了以下三个Meter：

Meter类型	描述
Gauge	单一最新值度量器
Counter	单一累加度量器，可增可减
Summary	多Measurement输出计数器，将输出total(合计)、count(计数)、max(最大)、average(合计/计数)和tps(合计/时间间隔)，无单位
Timer	多Measurement输出计时器，将输出total(合计)、count(计数)、max(最大)和average(合计/计数)，支持微秒为单位累计
说明：

未来可能增加更丰富复杂的度量器例如Histogram，这是一种可以本地统计聚合75th, 90th, 95th, 98th, 99th,99.9th...的度量器，适合某些场合，但需要更多内存。
所有的计量器都将继承自Meter，所有的计量器执行measure()方法后，都将归一化的生成1或N个Measurement结果。
它也会实现一个内存的Registry和PrometheusExporter，将度量数据同步给Prometheus。

说明：不同的监控系统，采集度量数据的方式不尽相同，例如Zabbix支持用zabbix-agent推送，Prometheus则推荐使用prometheus-server拉取的方式；同样数据交换协议也不同，因此往往需要逐一适配。

如何使用
引入依赖
如果需要开启TC的Metrics，只需要在seata-server的pom中增加：

<dependencies>
	<dependency>
		<groupId>${project.groupId}</groupId>
		<artifactId>seata-core</artifactId>
	</dependency>
	<!--导入依赖，启用Metrics-->
	<dependency>
		<groupId>${project.groupId}</groupId>
		<artifactId>seata-metrics-prometheus</artifactId>
	</dependency>
	<dependency>
		<groupId>commons-lang</groupId>
		<artifactId>commons-lang</artifactId>
	</dependency>
	<dependency>
		<groupId>org.testng</groupId>
		<artifactId>testng</artifactId>
		<scope>test</scope>
	</dependency>
</dependencies>
之后启动TC，即可在http://tc-server-ip:9898/metrics上获取到Metrics的文本格式数据。

提示：默认使用9898端口，Prometheus已登记的端口列表在此，如果想更换端口，可通过metrics.exporter.prometheus.port配置修改。

下载并启动Prometheus
下载完毕后，修改Prometheus的配置文件prometheus.yml，在scrape_configs中增加一项抓取Seata的度量数据：

scrape_configs:
  # The job name is added as a label `job=<job_name>` to any timeseries scraped from this config.
  - job_name: 'prometheus'

    # metrics_path defaults to '/metrics'
    # scheme defaults to 'http'.

    static_configs:
    - targets: ['localhost:9090']

  - job_name: 'seata'

    # metrics_path defaults to '/metrics'
    # scheme defaults to 'http'.

    static_configs:
    - targets: ['tc-server-ip:9898']
查看数据输出
推荐结合配置Grafana获得更好的查询效果，初期Seata导出的Metrics包括：

TC :
Metrics	描述
seata.transaction(role=tc,meter=counter,status=active/committed/rollback)	当前活动中/已提交/已回滚的事务总数
seata.transaction(role=tc,meter=summary,statistic=count,status=committed/rollback)	当前周期内提交/回滚的事务数
seata.transaction(role=tc,meter=summary,statistic=tps,status=committed/rollback)	当前周期内提交/回滚的事务TPS(transaction per second)
seata.transaction(role=tc,meter=timer,statistic=total,status=committed/rollback)	当前周期内提交/回滚的事务耗时总和
seata.transaction(role=tc,meter=timer,statistic=count,status=committed/rollback)	当前周期内提交/回滚的事务数
seata.transaction(role=tc,meter=timer,statistic=average,status=committed/rollback)	当前周期内提交/回滚的事务平均耗时
seata.transaction(role=tc,meter=timer,statistic=max,status=committed/rollback)	当前周期内提交/回滚的事务最大耗时
提示：seata.transaction(role=tc,meter=summary,statistic=count,status=committed/rollback)和seata.transaction(role=tc,meter=timer,statistic=count,status=committed/rollback)的值可能相同，但它们来源于两个不同的度量器。

TM：
稍后实现，包括诸如： seata.transaction(role=tm,name={GlobalTransactionalName},meter=counter,status=active/committed/rollback) : 以GlobalTransactionalName为维度区分不同Transactional的状态。

RM：
稍后实现，包括诸如： seata.transaction(role=rm,name={BranchTransactionalName},mode=at/mt,meter=counter,status=active/committed/rollback)：以BranchTransactionalName为维度以及AT/MT维度区分不同分支Transactional的状态。

如何扩展
如果有下面几种情况：

您不是使用Prometheus作为运维监控系统，但希望能够将Seata的Metrics数据集成进Dashboard中；
您需要更复杂强大的度量器类型，这些度量器在其他Metrics实现库中已有，希望集成这些第三方依赖直接使用；
您需要改变默认Metric的Measurement输出，例如在Timer中增加一个min或sd(方差)；
...
那么需要自行扩展Metrics的实现，请创建新的模块项目例如seata-metrics-xxxx，之后：

针对1：您需要实现新的Exporter；
针对2：您可以改变默认Registry的实现，返回第三方的Meter计量器实现；
针对3：您可以修改对应Meter的实现，包括measure()方法返回的Measurement列表。
