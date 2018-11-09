##排查mysql innodb Lock wait timeout exceeded; try restarting transaction的问题
OMG写的时候崩溃了一次。

触发关注这个问题的事情是 我们在使用pt-online-schedule 改表的时候总是拿不到锁，并且报出mysql innodb Lock wait timeout exceeded; try restarting transaction的问题，所以才想到要排查。

首先最先想到的肯定是

show processlist;
来查看当前正在运行的查询 或者等待休眠中的查询是哪些，包括使用了多少时间等，类似

复制代码
*************************** 582. row ***************************
           Id: 7485594
         User: xcf
         Host: cc:59580
           db: xx
      Command: Query
         Time: 0
        State: init
         Info: show processlist
    Rows_sent: 0
Rows_examined: 0
*************************** 583. row ***************************
           Id: 7485595
         User: xcf
         Host: cc-19:42614
           db: xx
      Command: Sleep
         Time: 0
        State:
         Info: NULL
    Rows_sent: 0
Rows_examined: 0
复制代码
但是可以看到,当有很多任务在执行的时候，processlist将会非常大，想要从这里面获取对我们有用的信息非常非常困难，最多也就简单的看下，哪个query耗时的确过长，会不会是因为事务没有提交导致的拿不到锁，来推测并且使用kill task_id来杀掉相应的task。

 

其次我们如果有权限，可以使用能看到相对来说更为复杂和详细的信息

SHOW ENGINE INNODB STATUS\G;
 通过innodb status 提供的详细的系统情况来分析问题。 

 

如果我没没有使用show engine innodb status的权限，退而求其次我们可以使用另外一种思路来找到是哪个表持续被锁，导致拿不到锁的问题。

show open tables where in_use>0;
查看现在系统正在使用的表，然后使用

show processlist;
查找正在query该表的任务，查看代码是否有一直没有提交事物，却没有commit的代码，用这个思路来找问题出现在哪儿。

 

另外在mysql5.5之后，information_schema数据库加了三个关于锁的表。

innodb_trx ## 当前运行的所有事务
innodb_locks ## 当前出现的锁
innodb_lock_waits ## 锁等待的对应关系
备忘一下他们的表结构

复制代码
mysql> desc information_schema.innodb_trx;
+----------------------------+---------------------+------+-----+---------------------+-------+
| Field                      | Type                | Null | Key | Default             |Extra |
+----------------------------+---------------------+------+-----+---------------------+-------+
| trx_id                     | varchar(18)         | NO   |     |                     |      |　　# 事务id
| trx_state                  | varchar(13)         | NO   |     |                     |      |　　# 事务状态
| trx_started                | datetime            | NO   |     | 0000-00-00 00:00:00 |      |　　# 事务开始的时间
| trx_requested_lock_id      | varchar(81)         | YES  |     | NULL                |      |　　# 事务请求到锁的id
| trx_wait_started           | datetime            | YES  |     | NULL                |      |　　# 事务开始等待的时间
| trx_weight                 | bigint(21) unsigned | NO   |     | 0                   |      |　　# 事务权重
| trx_mysql_thread_id        | bigint(21) unsigned | NO   |     | 0                   |      |　　# 事务线程的id
| trx_query                  | varchar(1024)       | YES  |     | NULL                |      |　　# 事务sql语句
| trx_operation_state        | varchar(64)         | YES  |     | NULL                |      |　　# 事务当前的操作状态
| trx_tables_in_use          | bigint(21) unsigned | NO   |     | 0                   |      |　　# 事务中有多少个表正在被使用
| trx_tables_locked          | bigint(21) unsigned | NO   |     | 0                   |      |　　# 事务拥有多少个锁
| trx_lock_structs           | bigint(21) unsigned | NO   |     | 0                   |      |　　
| trx_lock_memory_bytes      | bigint(21) unsigned | NO   |     | 0                   |      |　　# 事务锁住的内存大小
| trx_rows_locked            | bigint(21) unsigned | NO   |     | 0                   |      |　　# 事务锁住的行数
| trx_rows_modified          | bigint(21) unsigned | NO   |     | 0                   |      |　　# 事务改变的行数
| trx_concurrency_tickets    | bigint(21) unsigned | NO   |     | 0                   |      |　　 
| trx_isolation_level        | varchar(16)         | NO   |     |                     |      |　　# 事务隔离等级
| trx_unique_checks          | int(1)              | NO   |     | 0                   |      |　　# 唯一键检测
| trx_foreign_key_checks     | int(1)              | NO   |     | 0                   |      |　　# 外键检测
| trx_last_foreign_key_error | varchar(256)        | YES  |     | NULL                |      |
| trx_adaptive_hash_latched  | int(1)              | NO   |     | 0                   |      |
| trx_adaptive_hash_timeout  | bigint(21) unsigned | NO   |     | 0                   |      |
| trx_is_read_only           | int(1)              | NO   |     | 0                   |      |　　# 是否是只读事务
| trx_autocommit_non_locking | int(1)              | NO   |     | 0                   |      |
+----------------------------+---------------------+------+-----+---------------------+-------+
复制代码
这个表对于排查因为事务未提交引起的锁问题可以说是举足轻重。当我们有事务长时间未提交导致锁住数据库，其他程序拿不到锁的时候，因为对这张表进行排查。

比如我们获取一条记录的线程id， 即可拿着该线程id去information_schma.processlist中获取他的具体情况。

复制代码
mysql> select * from information_schema.processlist where id=701520;
+--------+------+----------------+-----------+---------+------+-------+------+---------+-----------+---------------+
| ID     | USER | HOST           | DB        | COMMAND | TIME | STATE | INFO | TIME_MS | ROWS_SENT | ROWS_EXAMINED |
+--------+------+----------------+-----------+---------+------+-------+------+---------+-----------+---------------+
| 701520 | ppp  | hazelnut:50308 | xxxxxxxxx | Sleep   | 5492 |       | NULL | 5492065 |         0 |             0 |
+--------+------+----------------+-----------+---------+------+-------+------+---------+-----------+---------------+
复制代码
然后找到在占用服务器50308端口的程序

netstat -nlatp |grep 50308
piperck@grape:~$ netstat -nlatp | grep 46698
(No info could be read for "-p": geteuid()=1025 but you should be root.)
tcp        0      0 192.168.2.79:46698      192.168.2.83:3306       ESTABLISHED -
可以看到协议 本机端口 去往mysql 也就是我们起初的数据库， 这里 established后面 本正常会显示占用程序的pid -p参数可以将其显示出来。

 

接下来看下记录锁信息的表 innodb_locks

复制代码
mysql> desc information_schema.innodb_locks;
+-------------+---------------------+------+-----+---------+-------+
| Field       | Type                | Null | Key | Default | Extra |
+-------------+---------------------+------+-----+---------+-------+
| lock_id     | varchar(81)         | NO   |     |         |       |　　# 锁id
| lock_trx_id | varchar(18)         | NO   |     |         |       |　　# 拥有锁的事务id　　　
| lock_mode   | varchar(32)         | NO   |     |         |       |　　# 锁模式
| lock_type   | varchar(32)         | NO   |     |         |       |　　# 锁类型
| lock_table  | varchar(1024)       | NO   |     |         |       |　　# 被锁的表
| lock_index  | varchar(1024)       | YES  |     | NULL    |       |　　# 被锁的索引
| lock_space  | bigint(21) unsigned | YES  |     | NULL    |       |　　# 被锁的表空间号
| lock_page   | bigint(21) unsigned | YES  |     | NULL    |       |　　# 被锁的页号
| lock_rec    | bigint(21) unsigned | YES  |     | NULL    |       |　　# 被锁的记录号
| lock_data   | varchar(8192)       | YES  |     | NULL    |       |　　# 被锁的数据
+-------------+---------------------+------+-----+---------+-------+
复制代码
如果 我们要排查的问题正锁死我们的某张表，那么该表的数据表就会有所体现。同时和这个表使用的 还有information_schema.innodb_lock_waits

复制代码
+-------------------+-------------+------+-----+---------+-------+
| Field             | Type        | Null | Key | Default | Extra |
+-------------------+-------------+------+-----+---------+-------+
| requesting_trx_id | varchar(18) | NO   |     |         |       |　　# 请求锁的事务id
| requested_lock_id | varchar(81) | NO   |     |         |       |　　# 请求锁的锁id
| blocking_trx_id   | varchar(18) | NO   |     |         |       |　　# 拥有锁的事务id
| blocking_lock_id  | varchar(81) | NO   |     |         |       |　　# 拥有锁的锁id
+-------------------+-------------+------+-----+---------+-------+
复制代码
结合以上两个表再查对应的信息  可以说已经很方便了。那天出问题，我回家的时候就已经被解决了。我并没有在线上环境尝试过，但是模拟了几次，使用这些办法提供的线索都能解决问题，等我有机会解决线上问题的时候，再补一个详细例子。

 

 

Reference:

http://stackoverflow.com/questions/5836623/getting-lock-wait-timeout-exceeded-try-restarting-transaction-even-though-im  getting-lock-wait-timeout-exceeded-try-restarting-transaction-even-though-im 
http://blog.csdn.net/hw_libo/article/details/39080809  MySQL锁阻塞分析
http://blog.sina.com.cn/s/blog_6bb63c9e0100s7cb.html  MySQL innodb_lock_wait 锁等待
http://dev.mysql.com/doc/refman/5.6/en/innodb-information-schema-understanding-innodb-locking.html  mysql5.6官方文档