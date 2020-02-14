@Transactional 注解的属性介绍
下面分别介绍一下 @Transactional 的几个属性。

value 和 transactionManager 属性
它们两个是一样的意思。当配置了多个事务管理器时，可以使用该属性指定选择哪个事务管理器。

propagation 属性
事务的传播行为，默认值为 Propagation.REQUIRED。

可选的值有：

Propagation.REQUIRED

如果当前存在事务，则加入该事务，如果当前不存在事务，则创建一个新的事务。

Propagation.SUPPORTS

如果当前存在事务，则加入该事务；如果当前不存在事务，则以非事务的方式继续运行。

Propagation.MANDATORY

如果当前存在事务，则加入该事务；如果当前不存在事务，则抛出异常。

Propagation.REQUIRES_NEW

重新创建一个新的事务，如果当前存在事务，暂停当前的事务。

Propagation.NOT_SUPPORTED

以非事务的方式运行，如果当前存在事务，暂停当前的事务。

Propagation.NEVER

以非事务的方式运行，如果当前存在事务，则抛出异常。

Propagation.NESTED

和 Propagation.REQUIRED 效果一样。

这些概念理解起来实在是有点儿抽象，后文会用代码示例解释说明。

isolation 属性
事务的隔离级别，默认值为 Isolation.DEFAULT。

可选的值有：

Isolation.DEFAULT

使用底层数据库默认的隔离级别。

Isolation.READ_UNCOMMITTED

Isolation.READ_COMMITTED
Isolation.REPEATABLE_READ
Isolation.SERIALIZABLE
timeout 属性
事务的超时时间，默认值为-1。如果超过该时间限制但事务还没有完成，则自动回滚事务。

readOnly 属性
指定事务是否为只读事务，默认值为 false；为了忽略那些不需要事务的方法，比如读取数据，可以设置 read-only 为 true。

rollbackFor 属性
用于指定能够触发事务回滚的异常类型，可以指定多个异常类型。

noRollbackFor 属性
抛出指定的异常类型，不回滚事务，也可以指定多个异常类型。


