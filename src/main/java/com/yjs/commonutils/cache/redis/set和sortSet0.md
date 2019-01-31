Redis四（Set操作）
1、Set操作
　　Set集合就是不允许重复的列表

集合操作（无序）
sadd(name,values)

1
# name对应的集合中添加元素
scard(name)

1
获取name对应的集合中元素个数
sdiff(keys, *args)

1
在第一个name对应的集合中且不在其他name对应的集合的元素集合
sdiffstore(dest, keys, *args)

1
# 获取第一个name对应的集合中且不在其他name对应的集合，再将其新加入到dest对应的集合中
sinter(keys, *args)

1
# 获取多一个name对应集合的并集
sinterstore(dest, keys, *args)

1
# 获取多一个name对应集合的并集，再讲其加入到dest对应的集合中
sismember(name, value)

1
# 检查value是否是name对应的集合的成员
smembers(name)

1
# 获取name对应的集合的所有成员
smove(src, dst, value)

1
# 将某个成员从一个集合中移动到另外一个集合
spop(name)

1
# 从集合的右侧（尾部）移除一个成员，并将其返回
srandmember(name, numbers)

1
# 从name对应的集合中随机获取 numbers 个元素
srem(name, values)

1
# 在name对应的集合中删除某些值
sunion(keys, *args)

1
# 获取多一个name对应的集合的并集
sunionstore(dest,keys, *args)

1
# 获取多一个name对应的集合的并集，并将结果保存到dest对应的集合中
sscan(name, cursor=0, match=None, count=None)
sscan_iter(name, match=None, count=None)

1
# 同字符串的操作，用于增量迭代分批获取元素，避免内存消耗太大
有序集合
　　在集合的基础上，为每元素排序；元素的排序需要根据另外一个值来进行比较，所以，对于有序集合，每一个元素有两个值，即：值和分数，分数专门用来做排序。

zadd(name, *args, **kwargs)

1
2
3
4
5
# 在name对应的有序集合中添加元素
# 如：
     # zadd('zz', 'n1', 1, 'n2', 2)
     # 或
     # zadd('zz', n1=11, n2=22)
zcard(name)

1
# 获取name对应的有序集合元素的数量
zcount(name, min, max)

1
# 获取name对应的有序集合中分数 在 [min,max] 之间的个数
zincrby(name, value, amount)

1
# 自增name对应的有序集合的 name 对应的分数
r.zrange( name, start, end, desc=False, withscores=False, score_cast_func=float)

1
2
3
4
5
6
7
8
9
10
11
12
13
14
15
16
17
18
# 按照索引范围获取name对应的有序集合的元素
# 参数：
    # name，redis的name
    # start，有序集合索引起始位置（非分数）
    # end，有序集合索引结束位置（非分数）
    # desc，排序规则，默认按照分数从小到大排序
    # withscores，是否获取元素的分数，默认只获取元素的值
    # score_cast_func，对分数进行数据转换的函数
# 更多：
    # 从大到小排序
    # zrevrange(name, start, end, withscores=False, score_cast_func=float)
    # 按照分数范围获取name对应的有序集合的元素
    # zrangebyscore(name, min, max, start=None, num=None, withscores=False, score_cast_func=float)
    # 从大到小排序
    # zrevrangebyscore(name, max, min, start=None, num=None, withscores=False, score_cast_func=float)
zrank(name, value)

1
2
3
4
# 获取某个值在 name对应的有序集合中的排行（从 0 开始）
# 更多：
    # zrevrank(name, value)，从大到小排序
zrangebylex(name, min, max, start=None, num=None)

1
2
3
4
5
6
7
8
9
10
11
12
13
14
15
16
17
# 当有序集合的所有成员都具有相同的分值时，有序集合的元素会根据成员的 值 （lexicographical ordering）来进行排序，而这个命令则可以返回给定的有序集合键 key 中， 元素的值介于 min 和 max 之间的成员
# 对集合中的每个成员进行逐个字节的对比（byte-by-byte compare）， 并按照从低到高的顺序， 返回排序后的集合成员。 如果两个字符串有一部分内容是相同的话， 那么命令会认为较长的字符串比较短的字符串要大
# 参数：
    # name，redis的name
    # min，左区间（值）。 + 表示正无限； - 表示负无限； ( 表示开区间； [ 则表示闭区间
    # min，右区间（值）
    # start，对结果进行分片处理，索引位置
    # num，对结果进行分片处理，索引后面的num个元素
# 如：
    # ZADD myzset 0 aa 0 ba 0 ca 0 da 0 ea 0 fa 0 ga
    # r.zrangebylex('myzset', "-", "[ca") 结果为：['aa', 'ba', 'ca']
# 更多：
    # 从大到小排序
    # zrevrangebylex(name, max, min, start=None, num=None)
zrem(name, values)

1
2
3
# 删除name对应的有序集合中值是values的成员
# 如：zrem('zz', ['s1', 's2'])
zremrangebyrank(name, min, max)

1
# 根据排行范围删除
zremrangebyscore(name, min, max)

1
# 根据分数范围删除
zremrangebylex(name, min, max)

1
# 根据值返回删除
zscore(name, value)

1
# 获取name对应有序集合中 value 对应的分数
zinterstore(dest, keys, aggregate=None)

1
2
# 获取两个有序集合的交集，如果遇到相同值不同分数，则按照aggregate进行操作
# aggregate的值为:  SUM  MIN  MAX
zunionstore(dest, keys, aggregate=None)

1
2
# 获取两个有序集合的并集，如果遇到相同值不同分数，则按照aggregate进行操作
# aggregate的值为:  SUM  MIN  MAX
zscan(name, cursor=0, match=None, count=None, score_cast_func=float)
zscan_iter(name, match=None, count=None,score_cast_func=float)

1
# 同字符串相似，相较于字符串新增score_cast_func，用来对分数进行操作