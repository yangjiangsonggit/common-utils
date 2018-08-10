##一、事务的基本原理

Spring事务的本质其实就是数据库对事务的支持，没有数据库的事务支持，spring是无法提供事务功能的。对于纯JDBC操作数据库，想要用到事务，
可以按照以下步骤进行：

1、获取连接 Connection con = DriverManager.getConnection()

2、开启事务con.setAutoCommit(true/false);

3、执行CRUD

4、提交事务/回滚事务 con.commit() / con.rollback();

5、关闭连接 conn.close();

使用Spring的事务管理功能后，我们可以不再写步骤 2 和 4 的代码，而是由Spirng 自动完成。那么Spring是如何在我们书写的 CRUD 之前和
之后开启事务和关闭事务的呢？解决这个问题，也就可以从整体上理解Spring的事务管理实现原理了。下面简单地介绍下，注解方式为例子

1、配置文件开启注解驱动，在相关的类和方法上通过注解@Transactional标识。

2、spring 在启动的时候会去解析生成相关的bean，这时候会查看拥有相关注解的类和方法，并且为这些类和方法生成代理，并根据@Transaction的
相关参数进行相关配置注入，这样就在代理中为我们把相关的事务处理掉了（开启正常提交事务，异常回滚事务）。

3、真正的数据库层的事务提交和回滚是通过binlog或者redo log实现的。



##二、Spring 事务的传播属性

所谓spring事务的传播属性，就是定义在存在多个事务同时存在的时候，spring应该如何处理这些事务的行为。这些属性在TransactionDefinition
中定义，具体常量的解释见下表：



常量名称常量解释

**PROPAGATION_REQUIRED支持当前事务，如果当前没有事务，就新建一个事务。这是最常见的选择，也是 Spring [默认]的事务的传播。**

**PROPAGATION_REQUIRES_NEW新建事务，如果当前存在事务，把当前事务挂起。新建的事务将和被挂起的事务没有任何关系，是两个独立的事务，外层
事务失败回滚之后，不能回滚内层事务执行的结果，内层事务失败抛出异常，外层事务捕获，也可以不处理回滚操作**

**PROPAGATION_SUPPORTS支持当前事务，如果当前没有事务，就以非事务方式执行。**

**PROPAGATION_MANDATORY支持当前事务，如果当前没有事务，就抛出异常。**

**PROPAGATION_NOT_SUPPORTED以非事务方式执行操作，如果当前存在事务，就把当前事务挂起。**

**PROPAGATION_NEVER以非事务方式执行，如果当前存在事务，则抛出异常。**

**PROPAGATION_NESTED
如果一个活动的事务存在，则运行在一个嵌套的事务中。如果没有活动事务，则按REQUIRED属性执行。它使用了一个单独的事务，这个事务拥有多个可以
回滚的保存点。内部事务的回滚不会对外部事务造成影响。它只对DataSourceTransactionManager事务管理器起效。**