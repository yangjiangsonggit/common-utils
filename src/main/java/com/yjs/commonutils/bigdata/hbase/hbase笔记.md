# hbase
	> https://www.cnblogs.com/steven-note/p/7209398.html


## HBase设计模型

###  1.逻辑存储模型
 
	> HBase中的每一张表就是所谓的BigTable。BigTable会存储一系列的行记录，行记录有三个基本类型的定义：
	- RowKey
	  	是行在BigTable中的唯一标识。
	- TimeStamp：
	    是每一次数据操作对应关联的时间戳，可以看作SVN的版本。
	- Column：
	    定义为<family>:<label>，通过这两部分可以指定唯一的数据的存储列，family的定义和修改需要对HBase进行类似于DB的DDL操作，
	    而label，不需要定义直接可以使用，这也为动态定制列提供了一种手段。family另一个作用体现在物理存储优化读写操作上，同family
	    的数据物理上保存的会比较接近，因此在业务设计的过程中可以利用这个特性。

	## 数据检索
		RowKey
	    	与NoSQL数据库一样，rowkey是用来检索记录的主键。访问HBase Table中的行，只有三种方式：
	- 通过单个rowkey访问
	- 通过rowkey的range
	- 全表扫描

	## 概念
	- rowkey行键可以任意字符串（最大长度64KB，实际应用中长度一般为10-100bytes），在HBase内部RowKey保存为字节数组。
	  存储时，数据按照RowKey的字典序（byte order）排序存储，设计key时，要充分了解这个特性，将经常一起读取的行存放在一起。
	  需要注意的是：行的一次读写是原子操作（不论一次读写多少列）

	- 列簇
	    HBase表中的每个列，都归属于某个列簇，列簇是表的schema的一部分（而列不是），必须在使用表之前定义。列名都以列簇作为前缀。例如：
	    courses:history,  courses:math 都属于 courses 这个列簇。
	    访问控制，磁盘和内存的使用统计都是在列簇层面进行的。
	    实际应用中，列簇上的控制权限能帮助我们管理不同类型的应用：我们允许一些应用可以添加新的基本数据、
	    一些应用可以读取基本数据并创建继承的列簇、一些应用则只允许浏览数据（设置可能因为隐私的原因不能浏览所有数据）。

	- 时间戳
	    HBase中通过row和columns确定的为一个存储单元称为cell。每个cell都保存着同一份数据的多个版本。版本通过时间戳来索引。
	    时间戳的类型是64位整型。时间戳可以由HBase在写入时自动赋值，此时时间戳是精确到毫秒的当前系统时间。时间戳也可以由客户显示赋值。
	    如果应用程序要避免数据版本冲突，就必须自己生成具有唯一性的时间戳。每个cell中在不同版本的数据按照时间倒序排序，即最新的数据排在最前面。
	    为了避免数据存在过多的版本造成的管理负担，HBase提供了两种数据版本回收方式。一是保存数据的最后n个版本，二是保存最近一段时间内的版本
	  （比如最近七天）。用户可以针对每个列簇进行设置。
	    
	- Cell
	    由{row key, column(=+), version} 唯一确定的单元。cell中的数据是没有类型的，全部是字节码形式存储。	

### 2.物理存储模型

- Table在行的方向上分割为多个HRegion，每个HRegion分散在不同的RegionServer中。
- 每个HRegion由多个Store构成，每个Store由一个MemStore和0或多个StoreFile组成，每个Store保存一个Columns Family
- StoreFile以HFile格式存储在HDFS中。

### 3.HBase存储架构
 
    从HBase的架构图上可以看出，HBase中的存储包括HMaster、HRegionSever、HRegion、HLog、Store、MemStore、StoreFile、HFile等，以下是HBase存储架构图：