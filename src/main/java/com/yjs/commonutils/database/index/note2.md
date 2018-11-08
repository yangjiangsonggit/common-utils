####MySQL 千万 级数据量根据（索引）优化 查询 速度
一、索引的作用
索引通俗来讲就相当于书的目录，当我们根据条件查询的时候，没有索引，便需要全表扫描，数据量少还可以，一旦数据量超过百万甚至千万，一条查询sql执行往往需要几十秒甚至更多，5秒以上就已经让人难以忍受了。

提升查询速度的方向一是提升硬件(内存、cpu、硬盘)，二是在软件上优化（加索引、优化sql；优化sql不在本文阐述范围之内）。

能在软件上解决的，就不在硬件上解决，毕竟硬件提升代码昂贵，性价比太低。代价小且行之有效的解决方法就是合理的加索引。

索引使用得当，能使查询速度提升上万倍，效果惊人。

二、MySQL索引类型：
mysql的索引有5种：主键索引、普通索引、唯一索引、全文索引、聚合索引（多列索引）。

唯一索引和全文索引用的很少，我们主要关注主键索引、普通索引和聚合索引。

1）主键索引：主键索引是加在主键上的索引，设置主键（primary key）的时候，mysql会自动创建主键索引；

2）普通索引：创建在非主键列上的索引；

3）聚合索引：创建在多列上的索引。

三、索引的语法：
查看某张表的索引：SHOW INDEX FROM 表名；

创建普通索引：ALTER TABLE 表名 ADD INDEX  索引名 (加索引的列) 

创建聚合索引：ALTER TABLE 表名 ADD INDEX 索引名 (加索引的列1,加索引的列2) 

删除某张表的索引：DROP INDEX 索引名 ON 表名;

四、EXPLAIN 分析SQL执行的状态
EXPLAIN列的解释

table                    显示这一行的数据是关于哪张表的

type                     这是重要的列，显示连接使用了何种类型。从最好到最差的连接类型为const、eq_reg、ref、range、indexhe和ALL

possible_keys     显示可能应用在这张表中的索引。如果为空，没有可能的索引。可以为相关的域从WHERE语句中选择一个合适的语句

key                      实际使用的索引。如果为NULL，则没有使用索引。

key_len               使用的索引的长度。在不损失精确性的情况下，长度越短越好

ref                       显示索引的哪一列被使用了，如果可能的话，是一个常数

rows                    MYSQL认为必须检查的用来返回请求数据的行数

Extra                   关于MYSQL如何解析查询的额外信息。

----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

Extra字段值含义：

Distinct                   一旦MYSQL找到了与行相联合匹配的行，就不再搜索了

Not exists               MYSQL优化了LEFT JOIN，一旦它找到了匹配LEFT JOIN标准的行，就不再搜索了

Range checked for each Record（index map:#）      没有找到理想的索引，因此对于从前面表中来的每一个行组合，MYSQL检查使用哪个索引，并用它来从表中返回行。这是使用索引的最慢的连接之一

Using filesort          看到这个的时候，查询就需要优化了。MYSQL需要进行额外的步骤来发现如何对返回的行排序。它根据连接类型以及存储排序键值和匹配条件的全部行的行指针来排序全部行

Using index            列数据是从仅仅使用了索引中的信息而没有读取实际的行动的表返回的，这发生在对表的全部的请求列都是同一个索引的部分的时候

Using temporary    看到这个的时候，查询需要优化了。这里，MYSQL需要创建一个临时表来存储结果，这通常发生在对不同的列集进行ORDER BY上，而不是GROUP BY上

Where used           使用了WHERE从句来限制哪些行将与下一张表匹配或者是返回给用户。如果不想返回表中的全部行，并且连接类型ALL或index，这就会发生，或者是查询有问题不同连接类型的解释（按照效率高低的顺序排序）


----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

type字段值含义：

const       表中的一个记录的最大值能够匹配这个查询（索引可以是主键或惟一索引）。因为只有一行，这个值实际就是常数，因为MYSQL先读这个值然后把它当做常数来对待

eq_ref     连接中，MYSQL在查询时，从前面的表中，对每一个记录的联合都从表中读取一个记录，它在查询使用了索引为主键或惟一键的全部时使用

ref           这个连接类型只有在查询使用了不是惟一或主键的键或者是这些类型的部分（比如，利用最左边前缀）时发生。对于之前的表的每一个行联合，全部记录都将从表中读出。这个类型严重依赖于根据索引匹配的记录多少—越少越好

range      这个连接类型使用索引返回一个范围中的行，比如使用>或<查找东西时发生的情况

index       这个连接类型对前面的表中的每一个记录联合进行完全扫描（比ALL更好，因为索引一般小于表数据）

ALL         这个连接类型对于前面的每一个记录联合进行完全扫描，这一般比较糟糕，应该尽量避免

五、性能测试
（一）、测试环境
测试环境：博主家用台式机

处理器为AMD FX(tm)-8300 Eight-Core Processor 3.2GHz;

内存8G;

64位 windows 7。

MySQL： 5.6.17

（二）、MyISAM引擎测试
1). 创建一张测试表

复制代码
DROP TABLE IF EXISTS `test_user`; 
CREATE TABLE `test_user` (  
    `id` bigint(20)  PRIMARY key not null AUTO_INCREMENT,  
    `username` varchar(50) DEFAULT NULL,  
    `email` varchar(30) DEFAULT NULL,  
    `password` varchar(32) DEFAULT NULL,
    `status`  tinyint(1) NULL DEFAULT 0
) ENGINE=MyISAM DEFAULT CHARSET=utf8; 
复制代码
 
存储引擎使用MyISAM是因为此引擎没有事务，插入速度极快，方便我们快速插入千万条测试数据，等我们插完数据，再把存储类型修改为InnoDB。

 

2).  使用存储过程插入1千万条数据

复制代码
create procedure myproc()
begin   
    declare num int;   
    set num=1;   
    while num <= 10000000 do   
        insert into test_user(username,email,password) values(CONCAT('username_',num), CONCAT(num ,'@qq.com'), MD5(num));   
        set num=num+1;  
    end while;  
end
复制代码
 

3).  执行  call myproc();  

由于使用的MyISAM引擎，插入1千万条数据，仅耗时246秒，若是InnoDB引擎，插入100万条数据就要花费数小时了。

MyISAM引擎之所以如此之快，一个原因是使用了三个文件来存储数据，frm后缀存储表结构、MYD存储真实数据、MYI存储索引数据。

每次进行插入时，MYD的内容是递增插入，MYI是一个B+树结构，每次的索引变更需要重新组织数据。

但相对于InnoDB来说，MyISAM更快。

 

4). sql测试

1. SELECT id,username,email,password FROM test_user WHERE id=999999

耗时：0.114s。

因为我们建表的时候，将id设成了主键，所以执行此sql的时候，走了主键索引，查询速度才会如此之快。

 

2. 我们再执行： SELECT id,username,email,password FROM test_user WHERE username='username_9000000'
耗时：4.613s。

用EXPLAIN分析一下：



信息显示进行了全表扫描。

 

3. 那我们给username列加上普通索引。

ALTER TABLE `test_user` ADD INDEX index_name(username) ;

此时，Mysql开始对test_user表建立索引，查看mysql 数据目录：



 



查看目录文件列表，可以看到新建了三个临时文件，新的临时数据表MYD文件大小并未变更，临时索引文件MYI文件大小增加了很多。

查看执行结果：



此过程大约耗时 221.792s,建索引的过程会全表扫描，逐条建索引，当然慢了。

等执行完毕后，mysql把旧的数据库文件删除，再用新建立的临时文件替换掉之。(删除索引过程也是同样的步骤)。

 

4. 再来执行：select id,username,email,password from test_user where username='username_9000000'
耗时：0.001s。

可见查询耗时提高的很可观。

用EXPLAIN分析一下：



Extra 字段告诉我们使用到了索引 index_name，和之前的EXPLAIN结果对比，未建立索引前进行了全部扫描，建立索引后使用到了索引，查询耗时对比明显。 

 

5. 再用username和password来联合查询

SELECT id, username, email, PASSWORD FROM test_user WHERE `password` = '7ece221bf3f5dbddbe3c2770ac19b419' AND username = 'username_9000000';

耗时：0.001s

执行 EXPLAIN ：



显示使用到了 index_name 索引，条件语句不分password、useranme先后顺序，结果都是一样。说明sql优化器优先用索引命中。

 

6. 我们再执行：SELECT id, username, email, PASSWORD FROM test_user WHERE `password` = '7ece221bf3f5dbddbe3c2770ac19b419' OR username = 'username_900000'

此时虽然我们已经对 username 加了索引，但是password列未加索引，索引执行password筛选的时候，还是会全表扫描，因此此时查询速度立马降了下来。



耗时：5.118s。

EXPLAIN一下：



使用OR条件的时候，虽然WHERE 语句中有用到索引字段，但还是进行了全表扫描。

 

 

7. 当我们的sql有多个列的筛选条件的时候，就需要对查询的多个列都加索引组成聚合索引：

加上聚合索引：ALTER TABLE `test_user` ADD INDEX index_union_name_password(username,password)



通过临时文件的大小来看，索引文件的大小已经超过了数据文件很多了。索引侧面来说，索引要合理利用，索引就是用空间换时间。

[SQL]ALTER TABLE `test_user` ADD INDEX index_union_name_password(username,password)

受影响的行: 10024725。
时间: 1399.785s。

 


8. 再来执行：[SQL] SELECT id, username, email, PASSWORD FROM test_user WHERE username = 'username_900000' OR `password` = '7ece221bf3f5dbddbe3c2770ac19b419'

耗时：4.416s。

EXPLAIN：



竟然是全表扫描，不可思议！！！ 使用 OR 语句竟然没有启用聚合索引，也没使用到单索引username，，，

 

 

9. 再来执行：[SQL] SELECT id, username, email, PASSWORD FROM test_user WHERE username = 'username_900000' AND `password` = '7ece221bf3f5dbddbe3c2770ac19b419'

耗时：0.001s。

EXPLAIN：



AND 语句才使用到了聚合索引，聚合索引必须使用AND条件，同时要符合最左原则，请戳我。

 

10. 主键区间查询

[SQL]EXPLAIN SELECT id, username, email, PASSWORD FROM test_user WHERE id > 8999990 AND id < 8999999
受影响的行: 0
时间: 0.001s。



命中7行，查询时间很短。

 

[SQL]SELECT id, username, email, PASSWORD FROM test_user WHERE id > 8999900 AND id < 8999999
受影响的行: 0
时间: 0.010s

 

[SQL]SELECT id, username, email, PASSWORD FROM test_user WHERE id > 8999000 AND id < 8999999
受影响的行: 0
时间: 0.029s

 

[SQL]SELECT id, username, email, PASSWORD FROM test_user WHERE id > 8990000 AND id < 8999999
受影响的行: 0
时间: 0.139s

 

通过不断加大区间来看，查询时间跟查询的数据量成相对的正比增长，同时使用到了主键索引。

 

11. 字符串区间查询

[SQL]SELECT id, username, email, PASSWORD FROM test_user WHERE username > 'username_800000' AND `password` > '7ece221bf3f5dbddbe3c2770ac19b419' 
受影响的行: 0
时间: 6.059s

EXPLAIN: 



未使用索引和聚合索引，进行了全表扫描。

 

[SQL]SELECT id, username, email, PASSWORD FROM test_user WHERE username > 'username_900000' AND `password` > '7ece221bf3f5dbddbe3c2770ac19b419'
受影响的行: 0
时间: 11.488s

EXPLAIN: 



也使用到了索引和聚合索引。

对比得出，字符串进行区间查询，是否能使用到索引的条件得看mysql是如何优化查询语句的。

 

12.最左原则

1]. 新建 A、B、C 聚合索引

[SQL]ALTER TABLE `test_user` ADD INDEX index_union_name_email_password(username,email,password)

受影响的行: 10024725
时间: 3171.056s

2]. SQL 测试 



慎用 OR 条件，可能将会导致全表扫描。

 

 

覆盖了 A、B、C 索引：



该语句使用了覆盖索引，WHERE 语句的先后顺序并不影响。MySQL会对SQL进行查询优化，最终命中ABC索引。

 

 

命中了 A、B、C 索引中的 AB组合，查询耗时很短：



 

 没有命中到 A、B、C 索引，所以进行了全表扫描，查询耗时长。



 

小结：

要使用覆盖索引必须都是 AND 条件，慎用 OR 条件。

要使用覆盖索引如ABC，需满足条件语句中有 A、AB、ABC才会使用覆盖索引，采用最左原则。

 

 

（三）、InnoDB引擎测试
1). 新建 InnoDB  表

根据上文的步骤，新建一个 test_user_innodb  表，引擎使用MyISAM，然后将存储引擎修改回InnDB。

使用如下命令：  ALTER TABLE test_user_innodb ENGINE=InnoDB; 此命令执行时间大约耗时5分钟，耐心等待。

[SQL]ALTER TABLE test_user_innodb ENGINE=InnoDB;
受影响的行: 10024725
时间: 692.475s

 

执行完毕后， test_user_innodb 表由之前的 三个文件 变为 两个文件，test_user_innodb.frm 和 test_user_innodb.idb。

其中frm文件记录表结构，idb文件记录表中的数据，其实就是一个B+树索引文件，不过该树的叶子节点中的数据域记录的是整行数据记录。

所以 Innodb 的查找次数比 MyISAM 表减少一次磁盘IO查找逻辑，但相对来说，插入数据也就没有MyISAM 快了，有所求就有所得吧！

同时 InnoDB 支持行锁、表锁，InnoDB 的锁机制是建立在索引上的，所以如果没命中索引，那么将是加表锁。

 

2). SQL 测试 

1. [SQL]SELECT id,username,email,password FROM test_user_innodb WHERE username='username_9000000'

受影响的行: 0
时间: 14.540s



显示进行了全表扫描，但跟MyISAM表对比来说，扫描的行数小了很多，可能这就是底层B+树布局不一样导致的吧。

 

2. 那我们给username列加上普通索引。

ALTER TABLE `test_user_innodb` ADD INDEX index_name(username) ;

此时，Mysql开始对 test_user_innodb 表建立索引，查看mysql 数据目录：



仔细观察，发现只生成了一个表结构临时文件。ibd文件容量在不断增大。这个跟MyISAM表加索引逻辑不一样。

[SQL]ALTER TABLE `test_user_innodb` ADD INDEX index_name(username) ;
受影响的行: 0
时间: 157.679s

此过程大约耗时 157.679s, 貌似建索引的过程未进行全表扫描，对比MyISAM表减少60s左右。为何如何？估计需要看底层实现了！ 

 

3. 再执行 SELECT id,username,email,password FROM test_user_innodb WHERE username='username_9000000'

[SQL]SELECT id,username,email,password FROM test_user_innodb WHERE username='username_9000000'

受影响的行: 0
时间: 0.001s

可见查询耗时减少的很可观，对比与未加索引。用EXPLAIN分析一下，和MyISAM表没有多少差别。



 

4. 再用username和password来联合查询

SELECT id, username, email, PASSWORD FROM test_user_innodb  WHERE `password` = '7ece221bf3f5dbddbe3c2770ac19b419' AND username = 'username_9000000';

耗时：0.001s

执行 EXPLAIN ：



 

显示使用到了 index_name 索引，条件语句不分password、useranme先后顺序，结果都是一样。说明sql优化器优先用索引命中。

 

5. 我们再执行：SELECT id, username, email, PASSWORD FROM test_user_innodb WHERE `password` = '7ece221bf3f5dbddbe3c2770ac19b419' OR username = 'username_900000'

此时虽然我们已经对 username 加了索引，但是password列未加索引，索引执行password筛选的时候，还是会全表扫描，因此此时查询速度立马降了下来。

[SQL]SELECT id, username, email, PASSWORD FROM test_user_innodb WHERE `password` = '7ece221bf3f5dbddbe3c2770ac19b419' OR username = 'username_900000'

受影响的行: 0
时间: 10.719s

EXPLAIN一下：



使用OR条件的时候，虽然WHERE 语句中有用到索引字段，但还是进行了全表扫描。

对比MyISAM 表来说，没有多大却别，唯一的就是rows行数不一样。

 

6. 加上聚合索引：ALTER TABLE `test_user_innodb` ADD INDEX index_union_name_password(username,password)

 此时，Mysql开始对 test_user_innodb 表建立索引，查看mysql 数据目录，和之前的一样，新增了一个临时表结构文件，ibd文件不断增大。

[SQL]ALTER TABLE `test_user_innodb` ADD INDEX index_union_name_password(username,password)

受影响的行: 0
时间: 348.613s

建立索引的时间比MyISAM 快。

 

7. 再来执行：[SQL]SELECT id, username, email, PASSWORD FROM test_user_innodb WHERE `password` = '7ece221bf3f5dbddbe3c2770ac19b419' OR username = 'username_900000'

受影响的行: 0
时间: 10.357s



对比MyISAM 竟然是慢了6s左右？ 和MyISAM 的全表扫描无差别。

InnoDB的OR查询性能没有MyISAM 快，应该是为了实现事务导致的性能损失？

 

8. 再来执行：[SQL] SELECT id, username, email, PASSWORD FROM test_user WHERE username = 'username_900000' AND `password` = '7ece221bf3f5dbddbe3c2770ac19b419'

耗时：0.001s。

EXPLAIN：



AND 语句才使用到了聚合索引，聚合索引必须使用AND条件，同时要符合最左原则，请戳我。

 

9. 主键区间查询

[SQL]SELECT id, username, email, PASSWORD FROM test_user_innodb WHERE id > 8999990 AND id < 8999999

受影响的行: 0
时间: 0.000s



 

[SQL]SELECT id, username, email, PASSWORD FROM test_user_innodb WHERE id > 8999900 AND id < 8999999

受影响的行: 0
时间: 0.001s

 

[SQL]SELECT id, username, email, PASSWORD FROM test_user_innodb WHERE id > 8999000 AND id < 8999999

受影响的行: 0
时间: 0.003s

 

[SQL]SELECT id, username, email, PASSWORD FROM test_user_innodb WHERE id > 8990000 AND id < 8999999

受影响的行: 0
时间: 0.022s

 

通过不断加大区间来看，查询时间跟查询的数据量成相对的正比增长，同时使用到了主键索引。

相对于MyISAM 表来说，主键区间查询的耗时小很多很多！看来只能用底层的B+树的实现不一样来解释了！

MyISAM 的B+树子节点的叶子节点数据域，存储的是数据在MYD文件中的数据地址。

InnoDB  的B+树子节点的叶子节点数据域，存储的是整行数据记录，这个节省了一次硬盘IO操作，应该是这个特点导致了主键区间查询比MyISAM 快的原因。

原因请戳我

 

10. 字符串区间查询

[SQL]SELECT id, username, email, PASSWORD FROM test_user_innodb WHERE username > 'username_800000' AND `password` > '7ece221bf3f5dbddbe3c2770ac19b419'

受影响的行: 0
时间: 12.506s



未使用索引和聚合索引，进行了全表扫描。

 

缩小区间在查询 

[SQL]SELECT id, username, email, PASSWORD FROM test_user_innodb WHERE username > 'username_900000' AND `password` > '7ece221bf3f5dbddbe3c2770ac19b419'

受影响的行: 0
时间: 12.213s

 

[SQL]SELECT id, username, email, PASSWORD FROM test_user_innodb WHERE username > 'username_1000000' AND `password` > '7ece221bf3f5dbddbe3c2770ac19b419'

受影响的行: 0
时间: 19.793s

 

11.最左原则

1]. 新建 A、B、C 聚合索引

[SQL]ALTER TABLE `test_user_innodb` ADD INDEX index_union_name_email_password(username,email,password)

受影响的行: 0
时间: 588.579s

 

对比MyISAM 表来说，建立该索引的时间是其的1/6之一。建立索引的时间相对可观。磁盘占用来说InnoDB总量更小。

 

2]. SQL 测试 



和MyISAM 表对比，竟然没使用到全表扫描，而且使用到了聚合索引。

 

覆盖了 A、B、C 索引：



该语句使用了覆盖索引，WHERE 语句的先后顺序并不影响。MySQL会对SQL进行查询优化，最终命中ABC索引。

 

命中了 A、B、C 索引中的 AB组合，查询耗时很短：



 

没有命中到 A、B、C 索引最左原则，竟然不是全表扫描，而是使用了索引。



和MyISAM 表对比，MyISAM 表是全表扫描，而InnoDB却是使用到了索引。

 

 

六、总结
两大引擎MyISAM、InnoDB分析：

背景：

数据记录：10024725行

表索引：  主键、A、AB、ABC

 

相同点：

1.都是B+树的底层实现。

2.WHERE条件都符合索引最左匹配原则。

 

不同点：

1.MyISAM的存储文件有三个，frm、MYD、MYI 文件；InnoDB的存储文件就两个，frm、ibd文件。总文件大小InnoDB引擎占用空间更小。

2.InnoDB的存储文件本身就是索引构成，建立新索引的时间比MyISAM快。

3.MyISAM比InnoDB查询速度快，插入速度也快。

4.主键区间查询，InnoDB查询更快。字符串区间查询，MyISAM相对更快。

5.有A、AB、ABC索引的情况下，A OR B 查询，InnoDB查询性能比MyISAM慢。不建议使用OR 条件进行查询。

6.InnoDB表没有命中到 A、B、C 索引最左原则时，BC组合查询命中了索引，但还是完全扫描，比全表扫描快些。MyISAM是全表扫描。

 

 

开篇也说过软件层面的优化一是合理加索引；二是优化执行慢的sql。

此二者相辅相成，缺一不可，如果加了索引，还是查询很慢，这时候就要考虑是sql的问题了，优化sql。

实际生产中的sql往往比较复杂，如果数据量过了百万，加了索引后效果还是不理想，使用集群、垂直或水平拆分。