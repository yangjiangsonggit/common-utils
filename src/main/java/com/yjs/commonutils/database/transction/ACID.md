前言
不知道你是否遇到过这样的情况，去小卖铺买东西，付了钱，但是店主因为处理了一些其他事，居然忘记你付了钱，又叫你重新付。又或者在网上购物明明已经
扣款，但是却告诉我没有发生交易。这一系列情况都是因为没有事务导致的。这说明了事务在生活中的一些重要性。有了事务，你去小卖铺买东西，那就是一手
交钱一手交货。有了事务，你去网上购物，扣款即产生订单交易。

事务的具体定义
事务提供一种机制将一个活动涉及的所有操作纳入到一个不可分割的执行单元，组成事务的所有操作只有在所有操作均能正常执行的情况下方能提交，只要其中
任一操作执行失败，都将导致整个事务的回滚。简单地说，事务提供一种“要么什么都不做，要么做全套（All or Nothing）”机制。

##数据库本地事务

ACID
说到数据库事务就不得不说，数据库事务中的四大特性，ACID:

A:原子性(Atomicity)

一个事务(transaction)中的所有操作，要么全部完成，要么全部不完成，不会结束在中间某个环节。事务在执行过程中发生错误，会被回滚（Rollback）
到事务开始前的状态，就像这个事务从来没有执行过一样。

就像你买东西要么交钱收货一起都执行，要么要是发不出货，就退钱。

C:一致性(Consistency)

事务的一致性指的是在一个事务执行之前和执行之后数据库都必须处于一致性状态。如果事务成功地完成，那么系统中所有变化将正确地应用，系统处于有效
状态。如果在事务中出现错误，那么系统中的所有变化将自动地回滚，系统返回到原始状态。

I:隔离性(Isolation)

指的是在并发环境中，当不同的事务同时操纵相同的数据时，每个事务都有各自的完整数据空间。由并发事务所做的修改必须与任何其他并发事务所做的修改
隔离。事务查看数据更新时，数据所处的状态要么是另一事务修改它之前的状态，要么是另一事务修改它之后的状态，事务不会查看到中间状态的数据。

打个比方，你买东西这个事情，是不影响其他人的。

D:持久性(Durability)

指的是只要事务成功结束，它对数据库所做的更新就必须永久保存下来。即使发生系统崩溃，重新启动数据库系统后，数据库还能恢复到事务成功结束时的状态。

打个比方，你买东西的时候需要记录在账本上，即使老板忘记了那也有据可查。

InnoDB实现原理
InnoDB是mysql的一个存储引擎，大部分人对mysql都比较熟悉，这里简单介绍一下数据库事务实现的一些基本原理，在本地事务中，服务和资源在事务的
包裹下可以看做是一体的:

我们的本地事务由资源管理器进行管理: 而事务的ACID是通过InnoDB日志和锁来保证。事务的隔离性是通过数据库锁的机制实现的，持久性通过redo log（
重做日志）来实现，原子性和一致性通过Undo log来实现。UndoLog的原理很简单，为了满足事务的原子性，在操作任何数据之前，首先将数据备份到一个地方
（这个存储数据备份的地方称为UndoLog）。然后进行数据的修改。如果出现了错误或者用户执行了ROLLBACK语句，系统可以利用Undo Log中的备份将数据
恢复到事务开始之前的状态。 和Undo Log相反，RedoLog记录的是新数据的备份。在事务提交前，只要将RedoLog持久化即可，不需要将数据持久化。
当系统崩溃时，虽然数据没有持久化，但是RedoLog已经持久化。系统可以根据RedoLog的内容，将所有数据恢复到最新的状态。 对具体实现过程有兴趣的
同学可以去自行搜索扩展。

