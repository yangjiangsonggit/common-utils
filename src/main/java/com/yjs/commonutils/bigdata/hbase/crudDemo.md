106. HBase通用模式
106.1。常数
当人们开始使用HBase时，他们倾向于编写如下代码：

Get get = new Get(rowkey);
Result r = table.get(get);
byte[] b = r.getValue(Bytes.toBytes("cf"), Bytes.toBytes("attr"));  // returns current version of value
但是，尤其是在内部循环（和MapReduce作业）中，将columnFamily和column-name反复转换为字节数组的开销令人惊讶。最好为字节数组使用常量，如下所示：

public static final byte[] CF = "cf".getBytes();
public static final byte[] ATTR = "attr".getBytes();
...
Get get = new Get(rowkey);
Result r = table.get(get);
byte[] b = r.getValue(CF, ATTR);  // returns current version of value
107.写入HBase
107.1。批量加载
如果可以，请使用批量加载工具。请参阅批量加载。否则，请注意以下内容。

107.2。表创建：预创建区域
默认情况下，HBase中的表最初是使用一个区域创建的。对于批量导入，这意味着所有客户端都将写入同一区域，直到该区域足够大以进行拆分并在整个群集中分布为止。加快批量导入过程的一种有用模式是预先创建空白区域。在这方面要保守一些，因为太多的区域实际上会降低性能。

有两种使用HBase API预先创建分割的方法。第一种方法是依靠默认Admin策略（在中实现Bytes.split）...

byte[] startKey = ...;      // your lowest key
byte[] endKey = ...;        // your highest key
int numberOfRegions = ...;  // # of regions to create
admin.createTable(table, startKey, endKey, numberOfRegions);
另一种使用HBase API的方法是自己定义分割...

byte[][] splits = ...;   // create your own splits
admin.createTable(table, splits);
通过指定拆分选项，可以使用HBase Shell创建表来达到类似的效果。

# create table with specific split points
hbase>create 't1','f1',SPLITS => ['\x10\x00', '\x20\x00', '\x30\x00', '\x40\x00']

# create table with four regions based on random bytes keys
hbase>create 't2','f1', { NUMREGIONS => 4 , SPLITALGO => 'UniformSplit' }

# create table with five regions based on hex keys
create 't3','f1', { NUMREGIONS => 5, SPLITALGO => 'HexStringSplit' }
有关与了解键空间和预创建区域有关的问题，请参见RowKey和Region Splits之间的关系。有关 手动预分割区域的讨论，请参见手动区域分割决策。见与HBase的壳牌切分表使用HBase的壳牌预分解表的更多细节。













