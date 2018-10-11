我们知道每一个topic有多个分区，这些分区leader副本分布在不同的broker上，分区的数量和leader副本可能是动态变化的。比如有可能增加topic的分区数以提高topic的并行处理能力或者leader所在副本宕机导致重新选举新的leader副本对外提供服务。
 
我们在创建ProducerRecord中的时候我们可以指定分区，也可以不指定分区，当不指定分区的时候，KafkaProducer要将消息追加到指定topic某个分区的leader副本，那么首先需要知道topic分区数量，经过路由后确定目标分区，之后KafkaProducer需要知道目标分区的leader副本所在的broker地址以及端口等信息才能建立连接，将消息发送到kafka中。因此KafkaProducer中维护了kafka集群的元数据，这些metadata记录了某个topic有哪些分区，每一个分区leader副本的所在的broker，follower副本分布在哪些broker上，哪些副本在ISR列表中以及这些broker的网络地址和端口信息等
 
一 核心字段
1.1 Cluster核心字段
List<Node> nodes: kafka集群节点信息列表
Set<String> unauthorizedTopics: 当前集群有哪些是未授权的topic
Set<String> internalTopics: 当前集群有哪些n内部的topic，比如offsets topic
Map<TopicPartition, PartitionInfo> partitionsByTopicPartition:Topic
Partition和PartitionInfo映射
Map<String, List<PartitionInfo>>partitionsByTopic: 每一个topic和他的PartitionInfo列表映射
Map<String, List<PartitionInfo>>availablePartitionsByTopic: 每一个topic和它的有效的PartitionInfo列表映射
Map<Integer, List<PartitionInfo>>partitionsByNode: 每一个节点和在它上面的分区信息的映射
Map<Integer, Node> nodesById: brokerId和节点的映射关系
ClusterResource: 集群资源
 
1.2 Metadata核心字段
long refreshBackoffMs: 两次发出更新集群保存的元数据信息的最小时间差，默认为100ms，这是为了防止更新操作太频繁而造成网络阻塞和增加服务器端的压力
long metadataExpireMs: 每隔多久更新一次,默认5分钟
int version: kafka集群元数据版本号，每次成功更新一次就加1，通过新旧版本号的比较判断集元数据是否更新完成
long lastRefreshMs: 记录上一次更新元数据的时间
long lastSuccessfulRefreshMs: 记录上一次成功更新元数据的时间，如果每一次都成功，那么和lastRefreshMs应该是相等的
Cluster: 记录kafka集群的元数据
boolean needUpdate: 是否强制更新cluster，这是触发sender线程更新集群元数据的条件之一
Map<String, Long> topics: topic和它的到期时间的映射
List<Listener> listeners: 监听metadata更新的监听器集合
ClusterResourceListeners: 集群资源监听器
boolean needMetadataForAllTopics: 是否需要更新全部的topic的元数据，一般情况下，只维护它所用到的topic的元数据
boolean topicExpiryEnabled: 是否启用topic到期机制，如果元数据启用了topic过期，那么在过期时间间隔内未使用的任何topic都将从更新后的元数据中删除，消费者是没有启用topic 过期的，因为他们显示管理topic,但是生产者却依赖topic 过期去限制更新集合
 
二 核心方法
2.1 kafkaPrpoducer回调用waitOnMetadata方法
private
ClusterAndWaitTime waitOnMetadata(String
topic, Integer
partition, 
long maxWaitMs) 
throws InterruptedException {
    // add topicto metadata topic list if it is not there already and reset expiry
    // 如果元数据不存在这个topic，则添加到元数据的topic集合中
    metadata.add(topic);
    // 根据元数据获取集群信息
    Cluster cluster
= metadata.fetch();
    // 获取指定topic的partition数量
    Integer partitionsCount
= cluster.partitionCountForTopic(topic);
    // 如果partition数量不为空，直接返回
    if (partitionsCount
!= null && (partition
== null || 
partition < partitionsCount))
        return new ClusterAndWaitTime(cluster,
0);
    long begin 
= time.milliseconds();
    // 最大的等待时间
    long remainingWaitMs
= maxWaitMs;
    long elapsed;
    do {
        log.trace("Requesting metadataupdate for topic {}.",
topic);
        // 设置needUpdate，获取当前元数据版本信息
        int version
= metadata.requestUpdate();
        // 唤醒sender线程
        sender.wakeup();
        try {
            // 等待元数据更新，直到当前版本大于我们所知道的最新版本,否则一直阻塞
            metadata.awaitUpdate(version,
remainingWaitMs);
        } catch (TimeoutException
ex) {
            throw new TimeoutException("Failedto update metadata after "
+ maxWaitMs + 
" ms.");
        }
        // metadata更新完了在获取一次集群信息
        cluster = 
metadata.fetch();
        elapsed = 
time.milliseconds() - 
begin;
        // 检测超时时间
        if (elapsed
>= maxWaitMs)
            throw new TimeoutException("Failedto update metadata after "
+ maxWaitMs + 
" ms.");
        // 如果集群未授权topics包含这个topic，也会抛出异常
        if (cluster.unauthorizedTopics().contains(topic))
            throw new TopicAuthorizationException(topic);
        remainingWaitMs= 
maxWaitMs - elapsed;
        // 在此获取该topic的partition数量
        partitionsCount= 
cluster.partitionCountForTopic(topic);
    } while (partitionsCount
== null);//
直到topic的partition数量不为空
    if (partition
!= null && 
partition >= partitionsCount) {
        throw new KafkaException(
                String.format("Invalid partition givenwith record: %d is not in the range [0...%d).",
partition, partitionsCount));
    }
    // 返回ClusterAndWaitTime
    return new ClusterAndWaitTime(cluster,
elapsed);
}
 
2.2添加topic到metadata,如果topic到期机制启用，到期时间将会在下一次更新的时候重设
public synchronized void add(String topic) {
    topics.put(topic, TOPIC_EXPIRY_NEEDS_UPDATE);
}
 
2.3下一次更新集群信息的时间是当前信息将过期的最大时间，以及当前信息可以更新的时间
public synchronized long timeToNextUpdate(long nowMs) {
    long timeToExpire = needUpdate ? 0 : Math.max(this.lastSuccessfulRefreshMs + this.metadataExpireMs - nowMs, 0);
    long timeToAllowUpdate = this.lastRefreshMs + this.refreshBackoffMs - nowMs;
    return Math.max(timeToExpire, timeToAllowUpdate);
}
 
2.4请求更新当前的集群元数据信息，在更新之前返回当前版本
public synchronized int requestUpdate() {
    this.needUpdate = true;
    return this.version;
}
 
2.5 判断是否强制更新
public synchronized boolean updateRequested() {
    return this.needUpdate;
}
 
2.6等待元数据更新，直到当前版本大于我们所知道的最新版本
public synchronized void awaitUpdate(final int lastVersion, final long maxWaitMs) throws InterruptedException {
    if (maxWaitMs < 0) {
        throw new IllegalArgumentException("Max time to wait for metadata updates should not be < 0 milli seconds");
    }
    long begin = System.currentTimeMillis();
    long remainingWaitMs = maxWaitMs;
    while (this.version <= lastVersion) {
        if (remainingWaitMs != 0)
            wait(remainingWaitMs);
        long elapsed = System.currentTimeMillis() - begin;
        if (elapsed >= maxWaitMs)
            throw new TimeoutException("Failed to update metadata after " + maxWaitMs + " ms.");
        remainingWaitMs = maxWaitMs - elapsed;
    }
}
 
2.7用新提供的topic集合替换当前的topic集合，如果启用了主题过期，主题的过期时间将在下一次更新中重新设置。
public synchronized void setTopics(Collection<String> topics) {
    if (!this.topics.keySet().containsAll(topics))
        requestUpdate();
    this.topics.clear();
    for (String topic : topics)
        this.topics.put(topic, TOPIC_EXPIRY_NEEDS_UPDATE);
}
 
2.8更新集群元数据。如果启用了主题过期，则在需要的时候为主题设置过期时间，并从元数据中删除过期的主题。
public synchronized void update(Cluster cluster, long now) {
    Objects.requireNonNull(cluster, "cluster should not be null");

    this.needUpdate = false;
    this.lastRefreshMs = now;
    this.lastSuccessfulRefreshMs = now;
    this.version += 1;

    if (topicExpiryEnabled) {
        // Handle expiry of topics from the metadata refresh set.
        for (Iterator<Map.Entry<String, Long>> it = topics.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, Long> entry = it.next();
            long expireMs = entry.getValue();
            if (expireMs == TOPIC_EXPIRY_NEEDS_UPDATE)
                entry.setValue(now + TOPIC_EXPIRY_MS);
            else if (expireMs <= now) {
                it.remove();
                log.debug("Removing unused topic {} from the metadata list, expiryMs {} now {}", entry.getKey(), expireMs, now);
            }
        }
    }

    for (Listener listener: listeners)
        listener.onMetadataUpdate(cluster);

    String previousClusterId = cluster.clusterResource().clusterId();

    if (this.needMetadataForAllTopics) {
        // the listener may change the interested topics, which could cause another metadata refresh.
        // If we have already fetched all topics, however, another fetch should be unnecessary.
        this.needUpdate = false;
        this.cluster = getClusterForCurrentTopics(cluster);
    } else {
        this.cluster = cluster;
    }

    // The bootstrap cluster is guaranteed not to have any useful information
    if (!cluster.isBootstrapConfigured()) {
        String clusterId = cluster.clusterResource().clusterId();
        if (clusterId == null ? previousClusterId != null : !clusterId.equals(previousClusterId))
            log.info("Cluster ID: {}", cluster.clusterResource().clusterId());
        clusterResourceListeners.onUpdate(cluster.clusterResource());
    }

    notifyAll();
    log.debug("Updated cluster metadata version {} to {}", this.version, this.cluster);
}
 
2.9记录尝试更新失败的元数据的尝试。我们需要跟踪它，以避免立即进行重新尝试。
public synchronized void failedUpdate(long now) {
    this.lastRefreshMs = now;
}
 
2.10获取当前集群的主题
private Cluster getClusterForCurrentTopics(Cluster cluster) {
    Set<String> unauthorizedTopics = new HashSet<>();
    Collection<PartitionInfo> partitionInfos = new ArrayList<>();
    List<Node> nodes = Collections.emptyList();
    Set<String> internalTopics = Collections.emptySet();
    String clusterId = null;
    if (cluster != null) {
        clusterId = cluster.clusterResource().clusterId();
        internalTopics = cluster.internalTopics();
        unauthorizedTopics.addAll(cluster.unauthorizedTopics());
        unauthorizedTopics.retainAll(this.topics.keySet());

        for (String topic : this.topics.keySet()) {
            List<PartitionInfo> partitionInfoList = cluster.partitionsForTopic(topic);
            if (partitionInfoList != null) {
                partitionInfos.addAll(partitionInfoList);
            }
        }
        nodes = cluster.nodes();
    }
    return new Cluster(clusterId, nodes, partitionInfos, unauthorizedTopics, internalTopics);
}

---------------------

https://blog.csdn.net/zhanglh046/article/details/72845517?utm_source=copy 
