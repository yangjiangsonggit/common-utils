嵌套事务
==========
事务的嵌套通过上面的理论知识的铺垫，我们大致知道了数据库事务和spring事务的一些属性和特点，接下来我们通过分析一些嵌套事务的场景，
来深入理解spring事务传播的机制。

假设外层事务 Service A 的 Method A() 调用 内层Service B 的 Method B()

##PROPAGATION_REQUIRED(spring 默认)

**如果ServiceB.methodB() 的事务级别定义为 PROPAGATION_REQUIRED，那么执行 ServiceA.methodA() 的时候spring已经起了事务，
这时调用 ServiceB.methodB()，ServiceB.methodB() 看到自己已经运行在 ServiceA.methodA() 的事务内部，就不再起新的事务。**

假如 ServiceB.methodB() 运行的时候发现自己没有在事务中，他就会为自己分配一个事务。

这样，在 ServiceA.methodA() 或者在 ServiceB.methodB() 内的任何地方出现异常，事务都会被回滚。

##PROPAGATION_REQUIRES_NEW

比如我们设计 ServiceA.methodA() 的事务级别为 PROPAGATION_REQUIRED，ServiceB.methodB() 的事务级别为 PROPAGATION_REQUIRES_NEW。

那么当执行到 ServiceB.methodB() 的时候，ServiceA.methodA() 所在的事务就会挂起，ServiceB.methodB() 会起一个新的事务，
等待 ServiceB.methodB() 的事务完成以后，它才继续执行。

他与 PROPAGATION_REQUIRED 的事务区别在于事务的回滚程度了。因为 ServiceB.methodB() 是新起一个事务，那么就是存在两个不同的事务。
如果 ServiceB.methodB() 已经提交，那么 ServiceA.methodA() 失败回滚，ServiceB.methodB() 是不会回滚的。如果 ServiceB.methodB()
 失败回滚，如果他抛出的异常被 ServiceA.methodA() 捕获，ServiceA.methodA() 事务仍然可能提交(主要看B抛出的异常是不是A会回滚的异常)。

##PROPAGATION_SUPPORTS

假设ServiceB.methodB() 的事务级别为 PROPAGATION_SUPPORTS，那么当执行到ServiceB.methodB()时，如果发现ServiceA.methodA()已经
开启了一个事务，则加入当前的事务，如果发现ServiceA.methodA()没有开启事务，则自己也不开启事务。这种时候，内部方法的事务性完全依赖于最外层的事务。

##PROPAGATION_NESTED

现在的情况就变得比较复杂了, ServiceB.methodB() 的事务属性被配置为 PROPAGATION_NESTED, 此时两者之间又将如何协作呢? 
ServiceB#methodB 如果 rollback, 那么内部事务(即 ServiceB#methodB) 将回滚到它执行前的 SavePoint 而外部事务(即 ServiceA#methodA) 
可以有以下两种处理方式:

a、捕获异常，执行异常分支逻辑


voidmethodA(){ try { ServiceB.methodB(); } catch (SomeException) { // 执行其他业务, 如 ServiceC.methodC(); } }

这种方式也是嵌套事务最有价值的地方, 它起到了分支执行的效果, 如果 ServiceB.methodB 失败, 那么执行 ServiceC.methodC(), 而 
ServiceB.methodB 已经回滚到它执行之前的 SavePoint, 所以不会产生脏数据(相当于此方法从未执行过), 这种特性可以用在某些特殊的业务中,
 而 PROPAGATION_REQUIRED 和 PROPAGATION_REQUIRES_NEW 都没有办法做到这一点。

b、 外部事务回滚/提交 代码不做任何修改, 那么如果内部事务(ServiceB#methodB) rollback, 那么首先 ServiceB.methodB 回滚到它执行之前的
 SavePoint(在任何情况下都会如此), 外部事务(即 ServiceA#methodA) 将根据具体的配置决定自己是 commit 还是 rollback

另外三种事务传播属性基本用不到，在此不做分析。

##总结

对于项目中需要使用到事务的地方，我建议开发者还是使用spring的TransactionCallback接口来实现事务，不要盲目使用spring事务注解，如果一定
要使用注解，那么一定要对spring事务的传播机制和隔离级别有个详细的了解，否则很可能发生意想不到的效果。