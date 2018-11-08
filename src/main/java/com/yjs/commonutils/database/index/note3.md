###关于mysql的索引原理与慢查询优化
大多情况下我们都知道加索引能提高查询效率，但是应该如何加索引呢？索引的顺序如何呢？

大家看一下下面的sql语句（在没有看下面的优化的方法之前）应该如何优化加索引以及优化sql语句：

1、select count(*) from task where status=2 and operator_id=20839 and operate_time>1371169729  and operate_time<1371174603 and type=2;

2、1、select count(*) from task where from_unixtime(create_time) = ’2014-05-29’;

3、update test set name='zhangsan'  where sex=1 and age>20;(注意这种情况加索引与不加索引的区别是什么)

4、select distinct l.emp_id from log l inner join (select l2.id as emp_id,l2.id as cert_id from log2 l2  left join log3 l3 on l2.id = l3.emp_id where l2.is_deleted=0 ) l4 on ( l.ref_table='Employee' and l.ref_oid= l4.emp_id

 ) or (l.ref_table='EmpCertificate' and l.ref_oid= l2.cert_id) where l.last_upd_date >='2013-11-07 15:03:00' and l.last_upd_date<='2013-11-08 16:00:00'

下面说一下MySql索引原理

索引目的
索引的目的在于提高查询效率，可以类比字典，如果要查“mysql”这个单词，我们肯定需要定位到m字母，然后从下往下找到y字母，再找到剩下的sql。如果没有索引，那么你可能需要把所有单词看一遍才能找到你想要的，如果我想找到m开头的单词呢？或者ze开头的单词呢？是不是觉得如果没有索引，这个事情根本无法完成？

索引原理
除了词典，生活中随处可见索引的例子，如火车站的车次表、图书的目录等。它们的原理都是一样的，通过不断的缩小想要获得数据的范围来筛选出最终想要的结果，同时把随机的事件变成顺序的事件，也就是我们总是通过同一种查找方式来锁定数据。

数据库也是一样，但显然要复杂许多，因为不仅面临着等值查询，还有范围查询(>、<、between、in)、模糊查询(like)、并集查询(or)等等。数据库应该选择怎么样的方式来应对所有的问题呢？我们回想字典的例子，能不能把数据分成段，然后分段查询呢？最简单的如果1000条数据，1到100分成第一段，101到200分成第二段，201到300分成第三段……这样查第250条数据，只要找第三段就可以了，一下子去除了90%的无效数据。但如果是1千万的记录呢，分成几段比较好？稍有算法基础的同学会想到搜索树，其平均复杂度是lgN，具有不错的查询性能。但这里我们忽略了一个关键的问题，复杂度模型是基于每次相同的操作成本来考虑的，数据库实现比较复杂，数据保存在磁盘上，而为了提高性能，每次又可以把部分数据读入内存来计算，因为我们知道访问磁盘的成本大概是访问内存的十万倍左右，所以简单的搜索树难以满足复杂的应用场景。

 

索引的数据结构
前面讲了生活中索引的例子，索引的基本原理，数据库的复杂性，又讲了操作系统的相关知识，目的就是让大家了解，任何一种数据结构都不是凭空产生的，一定会有它的背景和使用场景，我们现在总结一下，我们需要这种数据结构能够做些什么，其实很简单，那就是：每次查找数据时把磁盘IO次数控制在一个很小的数量级，最好是常数数量级。那么我们就想到如果一个高度可控的多路搜索树是否能满足需求呢？就这样，b+树应运而生。

 

建索引的几大原则
1.最左前缀匹配原则，非常重要的原则，mysql会一直向右匹配直到遇到范围查询(>、<、between、like)就停止匹配，比如a = 1 and b = 2 and c > 3 and d = 4 如果建立(a,b,c,d)顺序的索引，d是用不到索引的，如果建立(a,b,d,c)的索引则都可以用到，a,b,d的顺序可以任意调整。

2.=和in可以乱序，比如a = 1 and b = 2 and c = 3 建立(a,b,c)索引可以任意顺序，mysql的查询优化器会帮你优化成索引可以识别的形式

3.尽量选择区分度高的列作为索引,区分度的公式是count(distinct col)/count(*)，表示字段不重复的比例，比例越大我们扫描的记录数越少，唯一键的区分度是1，而一些状态、性别字段可能在大数据面前区分度就是0，那可能有人会问，这个比例有什么经验值吗？使用场景不同，这个值也很难确定，一般需要join的字段我们都要求是0.1以上，即平均1条扫描10条记录

4.索引列不能参与计算，保持列“干净”，比如from_unixtime(create_time) = ’2014-05-29’就不能使用到索引，原因很简单，b+树中存的都是数据表中的字段值，但进行检索时，需要把所有元素都应用函数才能比较，显然成本太大。所以语句应该写成create_time = unix_timestamp(’2014-05-29’);

5.尽量的扩展索引，不要新建索引。比如表中已经有a的索引，现在要加(a,b)的索引，那么只需要修改原来的索引即可

 

回到上面的给出的mysql

1、根据最左匹配原则，第一条sql语句的索引应该是status、operator_id、type、operate_time的联合索引；其中status、operator_id、type的顺序可以颠倒，所以我才会说，把这个表的所有相关查询都找到，会综合分析；

2、根据索引列不能参与计算，保持列“干净”，第二条sql语句应该写成create_time = unix_timestamp(’2014-05-29’);

3、update 语句不加索引是锁表，加索引是锁行 where后面的条件根据第一条一样优化即可。

4、第4条sql语句就复杂了在没有做如何优化之前结果是：53 rows in set (1.87 sec)

    通过explain

   简述一下执行计划，首先mysql根据idx_last_upd_date索引扫描log表获得379条记录；然后查表扫描了63727条记录，分为两部分，derived表示构造表，也就是不存在的表，可以简单理解成是一个语句形成的结果集，后面的数字表示语句的ID。derived2表示的是ID = 2的查询构造了虚拟表，并且返回了63727条记录。我们再来看看ID = 2的语句究竟做了写什么返回了这么大量的数据，首先全表扫描log2表13317条记录，然后根据索引emp_certificate_empid关联log3表，rows = 1表示，每个关联都只锁定了一条记录，效率比较高。获得后，再和log的379条记录根据规则关联。从执行过程上可以看出返回了太多的数据，返回的数据绝大部分log都用不到，因为log只锁定了379条记录。

如何优化呢？可以看到我们在运行完后还是要和log做join,那么我们能不能之前和log做join呢？仔细分析语句不难发现，其基本思想是如果log的ref_table是EmpCertificate就关联log3表，如果ref_table是Employee就关联log2表，我们完全可以拆成两部分，并用union连接起来，注意这里用union，而不用union all是因为原语句有“distinct”来得到唯一的记录，而union恰好具备了这种功能。如果原语句中没有distinct不需要去重，我们就可以直接使用union all了，因为使用union需要去重的动作，会影响SQL性能。

优化过的语句如下

select

   l2.id

from

   log l

inner join

   log2 l2

      on l.ref_table = 'Employee'

      and l.ref_oid = emp.id  

where

   l.last_upd_date >='2013-11-07 15:03:00'

   and l.last_upd_date<='2013-11-08 16:00:00'

   and l2.is_deleted = 0  

union

select

   l2.id

from

   log l

inner join

   log3 l3

      on l.ref_table = 'EmpCertificate'

      and l.ref_oid = l3.id  

inner join

   log2 l2

      on l2.id = l3.emp_id  

where

   l.last_upd_date >='2013-11-07 15:03:00'

   and l.last_upd_date<='2013-11-08 16:00:00'

   and l2.is_deleted = 0