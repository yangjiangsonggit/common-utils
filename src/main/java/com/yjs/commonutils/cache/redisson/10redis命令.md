Redis命令和Redisson对象匹配列表
顾瑞编辑了此页面 on 12 Jan 2019 · 5个修订
Redis命令	Redisson对象方法
认证	Config.setPassword（）;
附加	RBinaryStream.getOutputStream（）。write（）
位数	RBitSet.cardinality（），RBitSet.cardinalityAsync（），RBitSetReactive.cardinality（）
比特币	RBitSet.or（），RBitSet.orAsync（），RBitSetReactive.or（）;
RBitSet.and（），RBitSet.andAsync（），RBitSetReactive.and（）;
RBitSet.not（）;
RBitSet.xor（），RBitSet.xorAsync（），RBitSetReactive.xor（）
比特币	RBitSet.length（），RBitSet.lengthAsync（），RBitSetReactive.length（）
流行音乐	RBlockingQueue.take（），RBlockingQueue.takeAsync（），RBlockingQueueReactive.take（）;
RBlockingQueue.poll（），RBlockingQueue.pollAsync（），RBlockingQueueReactive.poll（）;
RBlockingQueue.pollFromAny（），RBlockingQueue.pollFromAnyAsync（），RBlockingQueueReactive.pollFromAny（）;
流行音乐	RBlockingDeque.takeLast（），RBlockingDeque.takeLastAsync（），RBlockingDequeReactive.takeLast（）;
流行音乐	RBlockingQueue.pollLastAndOfferFirstTo（），RBlockingQueue.pollLastAndOfferFirstToAsync（），RBlockingQueueReactive.pollLastAndOfferFirstTo（）;
复制	RObject.copy，RObject.copyAsync，RObjectReactive.copy（）;
客户名称	Config.setClientName（）;
集群信息	ClusterNode.info（）;
集群键槽	RKeys.getSlot（），RKeys.getSlotAsync（），RKeysReactive.getSlot（）;
集群节点	在ClusterConnectionManager中使用
倾倒	RObject.dump（），RObject.dumpAsync（），RObjectReactive.dump（）;
DBSIZE	RKeys.count（），RKeys.countAsync（），RKeysReactive.count（）;
解码器	RAtomicLong.decrementAndGet（），RAtomicLong.decrementAndGetAsync（），RAtomicLongReactive.decrementAndGetAsync（）;
德尔	RObject.delete（），RObject.deleteAsync（），RObjectReactive.delete（）;
RKeys.delete（），RKeys.deleteAsync（）;
斯特伦	RBucket.size（），RBucket.sizeAsync（），RBucketReactive.size（）;
评估	RScript.eval（），RScript.evalAsync（），RScriptReactive.eval（）;
客户回覆	RBatch.executeSkipResult（）;
评估	RScript.evalSha（），RScript.evalShaAsync（），RScriptReactive.evalSha（）;
执行	RBatch.execute（），RBatch.executeAsync（），RBatchReactive.execute（）;
存在	RObject.isExists（），RObject.isExistsAsync（），RObjectReactive.isExists（）;
冲洗	RKeys.flushall（），RKeys.flushallAsync（），RKeysReactive.flushall（）;
闪存盘	RKeys.flushdb（），RKeys.flushdbAsync（），RKeysReactive.flushdb（）;
土力工程处	RGeo.add（），RGeo.addAsync（），RGeoReactive.add（）;
地理学家	RGeo.dist（），RGeo.distAsync（），RGeoReactive.dist（）;
乔治·哈希	RGeo.hash（），RGeo.hashAsync（），RGeoReactive.hash（）;
GEOPOS	RGeo.pos（），RGeo.posAsync（），RGeoReactive.pos（）;
乔迪	RGeo.radius（），RGeo.radiusAsync（），RGeoReactive.radius（）;
RGeo.radiusWithDistance（），RGeo.radiusWithDistanceAsync（），RGeoReactive.radiusWithDistance（）;
RGeo.radiusWithPosition（），RGeo.radiusWithPositionAsync（），RGeoReactive.radiusWithPosition（）;
GEORADIUSBYMEMBER	RGeo.radius（），RGeo.radiusAsync（），RGeoReactive.radius（）;
RGeo.radiusWithDistance（），RGeo.radiusWithDistanceAsync（），RGeoReactive.radiusWithDistance（）;
RGeo.radiusWithPosition（），RGeo.radiusWithPositionAsync（），RGeoReactive.radiusWithPosition（）;
得到	RBucket.get（），RBucket.getAsync（），RBucketReactive.get（）;
捷比特	RBitSet.get（），RBitSet.getAsync（），RBitSetReactive.get（）;
GETSET	RBucket.getAndSet（），RBucket.getAndSetAsync（），RBucketReactive.getAndSet（）;
RAtomicLong.getAndSet（），RAtomicLong.getAndSetAsync（），RAtomicLongReactive.getAndSet（）;
RAtomicDouble.getAndSet（），RAtomicDouble.getAndSetAsync（），RAtomicDoubleReactive.getAndSet（）;
HDEL	RMap.fastRemove（），RMap.fastRemoveAsync（），RMapReactive.fastRemove（）;
十六进制	RMap.containsKey（），RMap.containsKeyAsync（），RMapReactive.containsKey（）;
HGET	RMap.get（），RMap.getAsync（），RMapReactive.get（）;
斯特伦	RMap.valueSize（），RMap.valueSizeAsync（），RMapReactive.valueSize（）;
HGETALL	RMap.readAllEntrySet（），RMap.readAllEntrySetAsync（），RMapReactive.readAllEntrySet（）;
辛比	RMap.addAndGet（），RMap.addAndGetAsync（），RMapReactive.addAndGet（）;
HINCRBYFLOAT	RMap.addAndGet（），RMap.addAndGetAsync（），RMapReactive.addAndGet（）;
按键	RMap.readAllKeySet（），RMap.readAllKeySetAsync（），RMapReactive.readAllKeySet（）;
海伦	RMap.size（），RMap.sizeAsync（），RMapReactive.size（）；
HMGET	RMap.getAll（），RMap.getAllAsync（），RMapReactive.getAll（）;
HMSET	RMap.putAll（），RMap.putAllAsync（），RMapReactive.putAll（）;
设置	RMap.put（），RMap.putAsync（），RMapReactive.put（）;
HSETNX	RMap.fastPutIfAbsent（），RMap.fastPutIfAbsentAsync，RMapReactive.fastPutIfAbsent（）;
HVALs	RMap.readAllValues（），RMap.readAllValuesAsync（），RMapReactive.readAllValues（）;
INCR	RAtomicLong.incrementAndGet（），RAtomicLong.incrementAndGetAsync（），RAtomicLongReactive.incrementAndGet（）;
恩比	RAtomicLong.addAndGet（），RAtomicLong.addAndGetAsync（），RAtomicLongReactive.addAndGet（）;
按键	RKeys.findKeysByPattern（），RKeys.findKeysByPatternAsync（），RKeysReactive.findKeysByPattern（）;
RedissonClient.findBuckets（）;
莱德克斯	RList.get（），RList.getAsync（），RListReactive.get（）;
伦	RList.size（），RList.sizeAsync（），RListReactive.Size（）;
持久性有机污染物	RQueue.poll（），RQueue.pollAsync（），RQueueReactive.poll（）;
脂蛋白	RDeque.addFirst（），RDeque.addFirstAsync（）;
RDequeReactive.addFirst（），RDeque.offerFirst（），RDeque.offerFirstAsync（），RDequeReactive.offerFirst（）;
范围	RList.readAll（），RList.readAllAsync（），RListReactive.readAll（）;
LREM	RList.fastRemove（），RList.fastRemoveAsync（），RList.remove（），RList.removeAsync（），RListReactive.remove（）;
RDeque.removeFirstOccurrence（），RDeque.removeFirstOccurrenceAsync（），RDequeReactive.removeFirstOccurrence（）;
RDeque.removeLastOccurrence（），RDeque.removeLastOccurrenceAsync（），RDequeReactive.removeLastOccurrence（）;
LSET	RList.fastSet（），RList.fastSetAsync（），RListReactive.fastSet（）;
LTRIM	RList.trim（），RList.trimAsync（），RListReactive.trim（）；
灵巧	RList.addBefore（），RList.addBeforeAsync（），RList.addAfter（），RList.addAfterAsync（），RListReactive.addBefore（），RListReactive.addAfter（）;
多	RBatch.execute（），RBatch.executeAsync（），RBatchReactive.execute（）;
MGET	RedissonClient.loadBucketValues（）;
迁移	RObject.migrate（），RObject.migrateAsync（）;
移动	RObject.move（），RObject.moveAsync（）;
MSET	RedissonClient.saveBuckets（）;
坚持	RExpirable.clearExpire（），RExpirable.clearExpireAsync（），RExpirableReactive.clearExpire（）;
百事可乐	RExpirable.expire（），RExpirable.expireAsync（），RExpirableReactive.expire（）;
百事可乐	RExpirable.expireAt（），RExpirable.expireAtAsync（），RExpirableReactive.expireAt（）;
PFADD	RHyperLogLog.add（），RHyperLogLog.addAsync（），RHyperLogLogReactive.add（）;
RHyperLogLog.addAll（），RHyperLogLog.addAllAsync（），RHyperLogLogReactive.addAll（）;
PFCOUNT	RHyperLogLog.count（），RHyperLogLog.countAsync（），RHyperLogLogReactive.count（）;
RHyperLogLog.countWith（），RHyperLogLog.countWithAsync（），RHyperLogLogReactive.countWith（）;
PFMERGE	RHyperLogLog.mergeWith（），RHyperLogLog.mergeWithAsync（），RHyperLogLogReactive.mergeWith（）;
平	Node.ping（）; NodesGroup.pingAll（）;
订阅	RPatternTopic.addListener（）;
PTTL	RExpirable.remainTimeToLive（），RExpirable.remainTimeToLiveAsync（），RExpirableReactive.remainTimeToLive（）;
发布	RTopic.publish
抽签	RPatternTopic.removeListener（）;
随机键	RKeys.randomKey（），RKeys.randomKeyAsync（），RKeysReactive.randomKey（）;
恢复	RObject.restore（），RObject.restoreAsync，RObjectReactive.restore（）;
改名	RObject.rename（），RObject.renameAsync（），RObjectReactive.rename（）;
重命名	RObject.renamenx（），RObject.renamenxAsync（），RObjectReactive.renamenx（）;
RPOP	RDeque.pollLast（），RDeque.pollLastAsync（），RDequeReactive.pollLast（）;
RDeque.removeLast（），RDeque.removeLastAsync（），RDequeReactive.removeLast（）;
流行音乐	RDeque.pollLastAndOfferFirstTo（），RDeque.pollLastAndOfferFirstToAsync（）;
RPUSH	RList.add（），RList.addAsync（），RListReactive.add（）;
萨德	RSet.add（），RSet.addAsync（），RSetReactive.add（）;
SCARD	RSet.size（），RSet.sizeAsync（），RSetReactive.size（）;
脚本存在	RScript.scriptExists（），RScript.scriptExistsAsync（），RScriptReactive.scriptExists（）;
脚本冲洗	RScript.scriptFlush（），RScript.scriptFlushAsync（），RScriptReactive.scriptFlush（）;
脚本杀	RScript.scriptKill（），RScript.scriptKillAsync（），RScriptReactive.scriptKill（）;
脚本负载	RScript.scriptLoad（），RScript.scriptLoadAsync（），RScriptReactive.scriptLoad（）;
分散存储	RSet.diff（），RSet.diffAsync（），RSetReactive.diff（）;
选择	Config.setDatabase（）;
组	RBucket.set（）; RBucket.setAsync（）; RBucketReactive.set（）;
塞比特	RBitSet.set（）; RBitSet.setAsync（）; RBitSet.clear（）; RBitSet.clearAsync（）;
SETEX	RBucket.set（）; RBucket.setAsync（）; RBucketReactive.set（）;
SETNX	RBucket.trySet（）; RBucket.trySetAsync（）; RBucketReactive.trySet（）;
SISMEMBER	RSet.contains（），RSet.containsAsync（），RSetReactive.contains（）;
中间商店	RSet.intersection（），RSet.intersectionAsync（），RSetReactive.intersection（）;
烧结矿	RSet.readIntersection（），RSet.readIntersectionAsync（），RSetReactive.readIntersection（）;
中小企业	RSet.readAll（），RSet.readAllAsync（），RSetReactive.readAll（）;
抽烟	RSet.move（），RSet.moveAsync（），RSetReactive.move（）;
流行音乐	RSet.removeRandom（），RSet.removeRandomAsync（），RSetReactive.removeRandom（）;
斯雷姆	RSet.remove（），RSet.removeAsync（），RSetReactive.remove（）;
订阅	RTopic.addListener（），RTopicReactive.addListener（）;
SUNION	RSet.readUnion（），RSet.readUnionAsync（），RSetReactive.readUnion（）;
SUNIONSTORE	RSet.union（），RSet.unionAsync（），RSetReactive.union（）;
TTL	RExpirable.remainTimeToLive（），RExpirable.remainTimeToLiveAsync（），RExpirableReactive.remainTimeToLive（）;
类型	RKeys.getType（）;
取消订阅	RTopic.removeListener（），RTopicReactive.removeListener（）;
等待	RBatch.syncSlaves，RBatchReactive.syncSlaves（）;
扎德	RScoredSortedSet.add（），RScoredSortedSet.addAsync（），RScoredSortedSetReactive.add（）；
卡	RScoredSortedSet.size（），RScoredSortedSet.sizeAsync（），RScoredSortedSetReactive.size（）；
ZCOUNT	RScoredSortedSet.count（），RScoredSortedSet.countAsync（）;
辛比	RScoredSortedSet.addScore（），RScoredSortedSet.addScoreAsync（），RScoredSortedSetReactive.addScore（）;
ZLEXCOUNT	RLexSortedSet.lexCount（），RLexSortedSet.lexCountAsync（），RLexSortedSetReactive.lexCount（）;
RLexSortedSet.lexCountHead（），RLexSortedSet.lexCountHeadAsync（），RLexSortedSetReactive.lexCountHead（）;
RLexSortedSet.lexCountTail（），RLexSortedSet.lexCountTailAsync（），RLexSortedSetReactive.lexCountTail（）;
ZRANGE	RScoredSortedSet.valueRange（），RScoredSortedSet.valueRangeAsync（），RScoredSortedSetReactive.valueRange（）;
ZREVRANGE	RScoredSortedSet.valueRangeReversed（），RScoredSortedSet.valueRangeReversedAsync（），RScoredSortedSetReactive.valueRangeReversed（）;
ZUNIONSTORE	RScoredSortedSet.union（），RScoredSortedSet.unionAsync（），RScoredSortedSetReactive.union（）;
ZINTERSTORE	RScoredSortedSet.intersection（），RScoredSortedSet.intersectionAsync（），RScoredSortedSetReactive.intersection（）;
ZRANGEBYLEX	RLexSortedSet.lexRange（），RLexSortedSet.lexRangeAsync（），RLexSortedSetReactive.lexRange（）;
RLexSortedSet.lexRangeHead（），RLexSortedSet.lexRangeHeadAsync（），RLexSortedSetReactive.lexRangeHead（）;
RLexSortedSet.lexRangeTail（），RLexSortedSet.lexRangeTailAsync（），RLexSortedSetReactive.lexRangeTail（）;
ZRANGEBYSCORE	RScoredSortedSet.valueRange（），RScoredSortedSet.valueRangeAsync（），RScoredSortedSetReactive.valueRange（）;
RScoredSortedSet.entryRange（），RScoredSortedSet.entryRangeAsync（），RScoredSortedSetReactive.entryRange（）；
时间	RedissonClient.getNodesGroup（）。getNode（）。time（），RedissonClient.getClusterNodesGroup（）。getNode（）。time（）;
ZRANK	RScoredSortedSet.rank（），RScoredSortedSet.rankAsync（），RScoredSortedSetReactive.rank（）；
ZREM	RScoredSortedSet.remove（），RScoredSortedSet.removeAsync（），RScoredSortedSetReactive.remove（）;
RScoredSortedSet.removeAll（），RScoredSortedSet.removeAllAsync（），RScoredSortedSetReactive.removeAll（）;
ZREMRANGEBYLEX	RLexSortedSet.removeRangeByLex（），RLexSortedSet.removeRangeByLexAsync（），RLexSortedSetReactive.removeRangeByLex（）;
RLexSortedSet.removeRangeHeadByLex（），RLexSortedSet.removeRangeHeadByLexAsync（），RLexSortedSetReactive.removeRangeHeadByLex（）;
RLexSortedSet.removeRangeTailByLex（），RLexSortedSet.removeRangeTailByLexAsync（），RLexSortedSetReactive.removeRangeTailByLex（）;
ZREMRANGEBYLEX	RScoredSortedSet.removeRangeByRank（），RScoredSortedSet.removeRangeByRankAsync（），RScoredSortedSetReactive.removeRangeByRank（）;
ZREMRANGEBYSCORE	RScoredSortedSet.removeRangeByScore（），RScoredSortedSet.removeRangeByScoreAsync（），RScoredSortedSetReactive.removeRangeByScore（）;
ZREVRANGEBYSCORE	RScoredSortedSet.entryRangeReversed（），RScoredSortedSet.entryRangeReversedAsync（），RScoredSortedSetReactive.entryRangeReversed（），RScoredSortedSet.valueRangeReversed（），RScoredSortedSet.valueRangeReactived。
ZREVRANK	RScoredSortedSet.revRank（），RScoredSortedSet.revRankAsync（），RScoredSortedSetReactive.revRank（）;
ZSCORE	RScoredSortedSet.getScore（），RScoredSortedSet.getScoreAsync（），RScoredSortedSetReactive.getScore（）;
扫描	RKeys.getKeys（），RKeysReactive.getKeys（）;
扫描仪	RSet.iterator（），RSetReactive.iterator（）;
高速扫描	RMap.keySet（）。iterator（），RMap.values（）。iterator（），RMap.entrySet（）。iterator（），RMapReactive.keyIterator（），RMapReactive.valueIterator（），RMapReactive.entryIterator（）;
扫描仪	RScoredSortedSet.iterator（），RScoredSortedSetReactive.iterator（）;