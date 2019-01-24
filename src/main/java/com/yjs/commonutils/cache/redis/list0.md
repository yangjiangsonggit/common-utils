Redis list 之增删改查
一、增加

1、lpush [lpush key valus...]  类似于压栈操作，将元素放入头部

复制代码
127.0.0.1:6379> lpush plist ch0 ch1 ch2
(integer) 3
127.0.0.1:6379> lrange plist 0 3
1) "ch2"
2) "ch1"
3) "ch0"
127.0.0.1:6379> lpush plist ch4
(integer) 4
127.0.0.1:6379> lrange plist 0 4
1) "ch4"
2) "ch2"
3) "ch1"
4) "ch0"
复制代码
2 、lpushx [lpushx key valus]:只能插入已经存在的key,且一次只能插入一次

复制代码
127.0.0.1:6379> lpushx pl ch
(integer) 0
127.0.0.1:6379> lpushx plist ch5 ch6
(error) ERR wrong number of arguments for 'lpushx' command
127.0.0.1:6379> lpushx plist ch5
(integer) 5
127.0.0.1:6379> lrange plist 0 5
1) "ch5"
2) "ch4"
3) "ch2"
4) "ch1"
5) "ch0"
复制代码
3、rpush  [rpush key valus...] ：将元素push在list的尾部

复制代码
127.0.0.1:6379> rpush plist ch6
(integer) 6
127.0.0.1:6379> lrange plist 0 6
1) "ch5"
2) "ch4"
3) "ch2"
4) "ch1"
5) "ch0"
6) "ch6"
127.0.0.1:6379> rpush plist ch7 ch8
(integer) 8
127.0.0.1:6379> lrange plist 0 8
1) "ch5"
2) "ch4"
3) "ch2"
4) "ch1"
5) "ch0"
6) "ch6"
7) "ch7"
8) "ch8"
复制代码
4、rpushx [rpushx key valus...] :相对于lpushx

5、linsert [linsert key before/after pivot value]:将值插入到pivot的前面或后面。返回列表元素个数。如果参照点pivot不存在不插入。如果有多个pivot，以离表头最近的为准

复制代码
127.0.0.1:6379> linsert plist before ch1 chi
(integer) 9
127.0.0.1:6379> lrange plist 0 9
1) "ch5"
2) "ch4"
3) "ch2"
4) "chi"
5) "ch1"
6) "ch0"
7) "ch6"
8) "ch7"
9) "ch8"
127.0.0.1:6379> linsert plist before chii chi2
(integer) -1
127.0.0.1:6379> linsert plist after chi cha
(integer) 10
127.0.0.1:6379> lrange plist 0 10
 1) "ch5"
 2) "ch4"
 3) "ch2"
 4) "chi"
 5) "cha"
 6) "ch1"
 7) "ch0"
 8) "ch6"
 9) "ch7"
10) "ch8"
复制代码
//以上插入操作均是返回list的长度
 

二、删除

1、lpop 、rpop：分别为删除头部和尾部，返回被删除的元素

复制代码
127.0.0.1:6379> lpop plist
"ch5"
127.0.0.1:6379> lrange plist 0 10
1) "ch4"
2) "ch2"
3) "chi"
4) "cha"
5) "ch1"
6) "ch0"
7) "ch6"
8) "ch7"
9) "ch8"
127.0.0.1:6379> rpop plist
"ch8"
127.0.0.1:6379> lrange plist 0 10
1) "ch4"
2) "ch2"
3) "chi"
4) "cha"
5) "ch1"
6) "ch0"
7) "ch6"
8) "ch7"
复制代码
2 、ltrim [ltrim key  range_l range_r]:保留区域类的元素，其他的删除

复制代码
127.0.0.1:6379> ltrim plist 0 3
OK
127.0.0.1:6379> lrange plist 0 10
1) "ch4"
2) "ch2"
3) "chi"
4) "cha"
复制代码
3、lrem [lrem key count value] :移除等于value的元素，当count>0时，从表头开始查找，移除count个；当count=0时，从表头开始查找，移除所有等于value的；当count<0时，从表尾开始查找，移除|count| 个。

　　cout >0

复制代码
127.0.0.1:6379> lrange plist 0 10
 1) "ch0"
 2) "ch0"
 3) "ch0"
 4) "ch4"
 5) "chi"
 6) "cha"
 7) "ch0"
 8) "ch0"
 9) "ch0"
10) "ch0"
127.0.0.1:6379> lrem plist 5 ch0
(integer) 5
127.0.0.1:6379> lrange plist 0 10
1) "ch4"
2) "chi"
3) "cha"
4) "ch0"
5) "ch0"
复制代码
　　count <0

复制代码
127.0.0.1:6379> lrange plist 0 10
 1) "ch0"
 2) "ch9"
 3) "ch0"
 4) "ch0"
 5) "ch0"
 6) "ch4"
 7) "chi"
 8) "cha"
 9) "ch0"
10) "ch0"
127.0.0.1:6379> lrem plist -5 ch0
(integer) 5
127.0.0.1:6379> lrange plist 0 10
1) "ch0"
2) "ch9"
3) "ch4"
4) "chi"
5) "cha"
复制代码
三、修改
lset [lset key index value] : 设置列表指定索引的值，如果指定索引不存在则报错

复制代码
127.0.0.1:6379> lset plist 0 ch2
OK
127.0.0.1:6379> lrange plist 0 10
1) "ch2"
2) "ch9"
3) "ch4"
4) "chi"
5) "cha"
复制代码
四、查询
1、lindex [lindex key index]:通过索引index获取列表的元素、key>=0从头到尾，key<0从尾到头

复制代码
127.0.0.1:6379> lrange plist 0 10
1) "ch2"
2) "ch9"
3) "ch4"
4) "chi"
5) "cha"
127.0.0.1:6379> lindex plist 0
"ch2"
127.0.0.1:6379> lindex plist -0
"ch2"
127.0.0.1:6379> lindex plist -1
"cha"
127.0.0.1:6379> lindex plist 5
(nil)
复制代码
2、lrange [lrange key range_l range_r]:0 表头、-1表尾