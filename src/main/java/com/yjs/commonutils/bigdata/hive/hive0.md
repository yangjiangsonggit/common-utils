Hive的使用


Hive是一个构建在Hadoop上的数据仓库框架，是一个通用的、可伸缩的数据处理平台，它设计的目的是让精通SQL技能的分析师能够对存放在HDFS中的大规模数据集执行查询。

一、Hive的安装
1.1 下载hive
地址：http://hive.apache.org/releases.html
下载需要的版本。
1.2 安装hive
将下载的hive解压到安装目录：

tar -zxvf 压缩包 安装目录
1.3 设置环境变量
编辑文件：vim /etc/profile,添加如下内容，请填写自己的实际路径：

export HIVE_INNSTALL=/probd/apache-hive-1.2.1-bin
export PATH=$PATH:$HIVE_INNSTALL/bin
然后刷新环境变量：source /etc/profile

1.4 配置hive
进入hive安装目录，修改conf/hive-site.xml文件，修改内容如下（带*号的需要请填写自己的实际值）:

<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<?xml-stylesheet type="text/xsl" href="configuration.xsl"?><configuration>

<property>
  <name>javax.jdo.option.ConnectionURL</name>
    <value>jdbc:mysql://probd03:3306/metastore?createDatabaseIfNotExist=true</value>
      <description>the URL of the MySQL database</description>
</property>

<property>
  <name>javax.jdo.option.ConnectionDriverName</name>
  <value>com.mysql.jdbc.Driver</value>
  <description>Driver class name for a JDBC metastore</description>
</property>

<property>
  <name>javax.jdo.option.ConnectionUserName</name>
  <value>***</value>
</property>

<property>
  <name>javax.jdo.option.ConnectionPassword</name>
    <value>***</value>
</property>
<property>


<property>
  <name>hive.metastore.warehouse.dir</name>
  <value>/user/hive/warehouse</value>
</property>
 <property>
  <name>hive.aux.jars.path</name>
  <value>file:////probd/probd-0.3.5/apache-hive-1.2.1-bin/lib/hive-hbase-handler-1.2.1.jar,file:////probd/probd-0.3.5/apache-hive-1.2.1-bin/lib/zookeeper-3.4.6.jar</value>
<description>The location of the plugin jars that contain implementations of user defined functions and serdes.</description>
 </property>

<property>
<name>hive.exec.scratchdir</name>
<value>/user/hive/tmp</value>
</property>

<property>
<name>hive.querylog.location</name>
<value>/user/hive/log</value>
</property>

<property>
  <name>hive.metastore.uris</name>
    <value>***</value>
      <description>格式：thrift://host:port</description>
 </property>
   <name>hive.server2.thrift.bind.host</name>
    <value>***</value>
</property>
<property>
   <name>hive.server2.thrift.port</name>
   <value>10000</value>
</property>

</configuration>

修改conf/hive-env.sh,内容如下,请填写自己的实际路径：

HADOOP_HOME=/probd/hadoop-2.6.3
从hive-site.xml配置文件中看出，这里我们使用了mysql作为hive的metastore独立数据库，所以，在运行hive之前，请确保mysql数据库已经安装并且已启动。其次还要确保metastore服务已经启动：



如果需要用到远程客户端(比如 Tableau)连接到hive数据库，还需要启动hive service:



然后由于配置过环境变量，可以直接在命令行中输入hive:



二、Hive的使用
2.1 创建表
通过上面的步骤，我们已经启动了Hive的外壳环境（shell），Hive的外壳环境是我们与Hive交互、发出HiveQL命令的主要方式，HiveQL是Hive的查询语言，相当于SQL的一种“方言”，它的设计在很大程度上受MySQL的影响。因此，如果熟悉MySQL，你会觉得Hive很亲切。
第一次启动时，我们可以通过列出Hive的表来检查Hive是否正常工作，此时应该没有任何表。

hive> show tables;
ok
Time taken: 10.429 seconds
我们看到第一次所花费的时间还是挺长的，这是因为系统采用lazy策略，所以直到此时才在机器上创建metastore数据库（该数据库把相关文件放在运行hive命令那个位置的metastore_db的目录中）。

我们有一个描述学生的文件student.txt，文件里面的内容如下：

zhangsan,14,165
cuihua,13,160
wangwu,15,168
现在我们要把这些数据存放在hive中。和RDBMS一样，Hive把数据组织成表。我们使用CREATE TABLE语言为学生的数据新建一个表：

CREATE TABLE students(name STRING,age INT,stature INT)
ROW FORMAT DELIMITED
FIELDS TERMINATED BY '\t';
第一行声明一个students表，包含三列name,age,stature。还必须指明每一列的数据类型，这里我们指定了姓名为字符串类型，年龄和身高都是整型。
第二行的ROW FORMAT DELIMITED是HiveQL所特有的，这个句子声明的是数据文件的每一行是由制表符分隔的文本。Hive按照这一格式读取数据：每行3个字段，分别对应表中的3列，以换行符分隔。
第三行FIELDS TERMINATED BY '\t'表示字段间以制表符分隔。

2.2 导入数据
现在，已经创建了表，接下来，我们可以向Hive 导入数据，导入数据有三种方式：
1）使用LOAD DATA操作，把文件复制或移动到表的目录中，从而把数据导入Hive表。
2）使用INSERT语句把数据从一个Hive表填充到另一个。
3）在新建表的时候使用CTAS结构，即CREATE TABLE ... AS SELECT。我们先来看这第一种方式：

LOAD DATA LOCAL INPATH '/student.txt'
OVERWRITE INTO TABLE students;
这一行命令告诉Hive把指定的本地文件放入其仓库目录中。这只是一个简单的文件系统操作。这个操作并不解析文件或把它存储为内部数据库格式，因为Hive并不强制使用任何特定的文件格式。文件以原样逐字存储，Hive并不会对这个文件进行修改。
在本例中，我们把Hive表存储在本地文件系统中（fs.default.name设为默认值 file:///）。在Hive的仓库目录中，表存储为目录。仓库目录由选项hive.metastore.warehouse.dir控制，默认值为/user/hive/warehouse。
这样，student表的文件便可以在本地文件系统的/user/hive/warehourse/students目录中找到：

% ls /user/hive/warehourse/students/
student.txt
LOAD DATA 语句中的OVERWRITE关键字告诉Hive删除表对应目录中已有的所有文件，如果省去这个关键字，Hive就简单的把新的文件加入目录，并只替换掉同名文件。

接下来我们来看INSERT语句导入数据：

INSERT OVERWRITE TABLE target
SELECT col1,col2
   FROM source;
OVERWRITE关键字在这种情况下是强制的。这意味着目标表中的内容会被SELECT语句的结果替换掉。
在HiveQL中，可以把ISERT语句倒过来，把FROM子句放在最前面，结果是一样的：

FROM source
ISERT OVERWRITE TABLE target
  SELECT col1,col2;
也可以在同一个查询语句中使用多个INSERT子句。此时，这样的语法会让查询的含义更加清楚。这种“多表插入”方法比使用多个单独的INSERT语句效率要高，因为只需要扫描一遍源表就可以生成多个不想交的输出：

FROM source
INSERT OVERWRITE TABLE target1
  SELECT col1,col2
INSERT OVERWRITE TABLE target2
  SELECT col1,COUNT(1)
  GROUP BY col1
INSERT OVERWRITE TABLE target3
  SELECT col1,COUNT(1)
  WHERE ...
这里只有一个源表，但有三个表用于存放针对这个源表的三个不同查询锁产生的结果。

再来看第三种方式：CREATE TABLE ... AS SELECT语句。
把hive查询的输出结果存放到一个新的表往往非常方便，但是是因为输出结果太多，不适宜显示在控制台或基于输出结果还有其它后续处理。
新表的列的定义是从SELECT子句所检索的列导出的。在下面的查询中，target表有两列，分别名为col1和col2，它们的数据类型和源表中对应的列相同：

CREATE TABLE target
AS
SELECT col1,col2
FROM source;
CTAS操作是源自原子的，因此如果SELECT查询由于某种原因失败，新表就不会被创建。

2.3 查询数据
通过前面例子的操作，数据现在已经在Hive中，我们可以对它运行一个查询：

hive> SELECT name,MAX(age)
    > FROM students
    > WHERE age!=15
    > AND (stature=160 OR stature=165)
    > GROUP BY name;

zhangsan,14,165
这个SQL查询没有什么特别的，它的优势在于把这个查询语句转化为一个MapReduce作业并为我们执行这个作业，然后把结果打印输出到控制台。

2.4 读时模式和写时模式
在传统的数据库里，表的模式是在数据加载时强制确定的。如果在加载时发现数据不符合模式，则拒绝加载数据。因为数据在写入数据库时对照模式进行检查，因此这一设计有时被称为“写时模式”。
而在Hive中，对数据的验证并不在加载数据的时候进行，而是在查询时进行，这被称为“读时模式”。
用户需要在这两种方法之间进行权衡。读时模式可以使数据加载非常迅速，这是因为它不需要读取数据，进行“解析”,再进行序列化以数据库内部格式存入磁盘。数据加载操作仅仅是问文件复制或移动。这一方法更为灵活：试想，针对不同的分析任务，同一个数据可能会有两个模式。Hive使用“外部表”时，这种情况也是可能发生的。
写时模式有利于提升查询性能。因为数据库可以对列进行索引，并对数据进行压缩。但作为权衡，此时加载数据会花更多时间。此外，在很多加载时模式未知的情况下，因为查询尚未确定，因此不能决定使用何种索引。

三、Metastore
metastore是Hive元数据的集中存放地。metastore包括两部分：服务和后台数据的存储。
3.1 内嵌metastore：默认情况下，metastore服务和Hive服务运行在同一个JVM中，它包含一个内嵌的以本地磁盘作为存储的Derby数据库实例，这种称为“内嵌metastore配置”。使用内嵌的metastore是最简单的方法，但是每次只有一个内嵌的Derby数据库可以访问某个磁盘上的数据库文件，这就意味着一次只能为每个metastore打开一个Hive会话。所以一般实际环境中是不使用这种配置的。
3.2 本地metastore： 如果要支持多会话以及多用户，需要使用一个独立的数据库。这种配置成为“本地metastore”，因为metastore服务仍然和Hive服务运行在同一个进程中，但连接的却是在另一个进行中运行的数据库，在同一台机器上或在远程机器上。任何JDBC兼容的数据库都可以通过下表列出的javax.jdo.option.*这个属性配置来供metastore使用。
重要的metastore配置属性
我们常用MySQL来作为独立的metastore，此时javax.jdo.option.ConnectionURL应该设为jdbc:mysql://host/dbname?createDatabaseIfNotExist=true,而javax.jdo.option.ConnectionDriverName则设置为com.mysql.jdbc.Driver（当然还需要设置用户名和密码）。MySQL的JDBC驱动jar包必须在Hive的类路径中，把这个包放到Hive的lib目录下即可。
3.3 远程metastore： 更进一步，还有一种metastore配置称为“远程metastore”，这种配置下，一个或多个metastore服务器和Hive服务运行在不同的进程中，这样一来，数据库层可以完全置于防火墙后，客户端则不需要数据库凭据（用户名和密码），从而提供了更好的可管理性和安全。可以通过把hive.metastore.local设为false，hive.metastore.uris设为metastore服务器URI，其值的形式为thrift://host:port（如果有多个服务器，各个URI之间用逗号分隔），即可把Hive服务设为使用远程metastore。

四、表
Hive的表在逻辑上由存储的数据和描述表中数据形式的相关元数据组成。数据一般存放在HDFS中，或是其他任何Hadoop文件系统中，包括本地文件系统或S3。Hive把元数据存储在关系型数据库中（metastore）。

4.1 托管表和外部表
在Hive中创建表时，默认情况下Hive负责管理数据，这意味着Hive把数据移到它的仓库目录中，我们称为“托管表”。另一种选择是创建一个“外部表”，这会让Hive到仓库目录以外的位置访问数据。
这两种表的区别表现在LOAD和DROP命令的语义上。先来看托管表。
加载数据到托管表时，Hive把数据移到仓库目录。列如我们上面的那个例子：

LOAD DATA LOCAL INPATH '/student.txt' INTO TABLE students;
这里因为加了LOCAL，所以是把文件复制到hive的students表的仓库目录中，如果不加，则为移动，即file:///user/hive/warehourse/students，当然也可以移到HDFS文件系统上。
如果要丢弃一个表，可以使用一下语句：

DROP TABLE students；
这个表，包括它的元数据和数据，都会被一起删除。在这里要重复强调，因为最初的LOAD是一个移动操作，而DROP是一个删除操作，所以数据会彻底消失。这就四Hive所谓的“托管数据”的含义。
对于外部表而言，这两个操作的结果就不一样了：由你自己来控制数据的创建和删除。外部数据的位置需要在创建表的时候指明：

CREATE EXTERNAL TABLE external_table(dummy STRING)
LOCATION '/user/tom/external_table';
LOAD DATA INPATH '/data.txt' INTO TABLE external_table;
使用EXTERNAL关键字后，Hive知道数据并不由自己管理，因此不会把数据移到自己的仓库目录。事实上，在定义时，它甚至不会检查这一外部位置是否存在。这是一个非常有用的特性，因为这意味着你可以把创建数据推迟到创建表之后才进行。
丢弃外部表时，Hive不会碰数据，而是只会删除元数据。
那么，应该如何选择使用哪种表呢？在多数情况下，这两种方式没有太大的区别（当然DROP除外），因此这是个人喜好的问题。作为一个经验法则，如果所有处理都是由Hive来完成，应该使用托管表。但如果要用Hive和其他工具来处理同一个数据集，应该使用外部表。普遍的用法是把存放在HDFS的初始数据集用作外部表进行使用，然后用Hive的变换功能把数据移到托管的Hive表中，这一方法反之也成立——外部表可以用于从Hive导出数据供其他应用程序使用。

4.2 表的创建
前面我们已经讲了如何创建一个表，即：

CREATE TABLE students(name STRING,age INT,stature INT)
ROW FORMAT DELIMITED
FIELDS TERMINATED BY '\t';
里面语句的意思这里不再赘述。

4.3 表的修改
由于Hive使用“读时模式”（导入数据时对数据的验证并不在导入的时候进行，而是在查询时进行），所以创建表以后，它非常灵活低支持对表定义的修改。但一般需要警惕，在很多情况下，要由我们自己来确保修改数据以符合新的结构。
可以使用 ALTER TABLE语句来重命名表：

ALTER TABLE source RENAME TO target;
在更新表的元数据以外，ALTER TABLE语句还把表目录移到新名称所对应的目录下。上面的那句结果是 /user/hive/warehouse/source 被重命名为/user/hive/warehouse/target。对于外部表，这个操作只更新元数据，而不会移动目录。
Hive允许修改列的定义，添加新的列，甚至用一组新的列替换表内已有的列。
如添加一个新列：

ALTER TABLE target ADD COLUMNS (col3 STRING)
新的col3被添加在已有列的后面。数据文件并没有更新，因此原来的查询会为col3的所有值返回空值null（当然，除非文件中原来就已经有额外的字段）。因为Hive不允许更新已有的记录，所以需要使用其它机制来更新底层的文件。为此，更常用的做法是创建一个定义了新列的新表，然后使用SELECT语句把数据填充进去。

4.4 表的删除
DROP TABLE语句用于删除表的数据和元数据。如果是外部表，就只删除元数据——数据不会受到影响。
如果要删除表内的所有数据，但要保留表的定义，删除数据文件即可，例如：

hive>
dfs -rmr /user/hive/warehouse/my_table;
Hive把缺少文件（或根本没有表对应的目录）的表认为是空表。
另外一种达到类似目的的方法是使用LIKE关键字创建一个与第一个表模式相同的新表。如：

CREATE TABLE new_table LIKE existing_table;