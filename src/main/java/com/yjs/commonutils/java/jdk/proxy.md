##Java 动态代理及 RPC 框架介绍

ImportNew  10月17日


来源：Frapples ，
frapples.github.io/articles/2018-03-30-4a97.html

所谓动态代理，指的是语言提供的一种语法，能够将对对象中不同方法的调用重定向到一个统一的处理函数中来。

python重写__getattr__函数能够做到这一点，就连世界上最好的语言也提供称为魔术方法的__call。

这种语法除了能更好的实现动态代理外，还是RPC框架实现原理的一部分。

1. 动态代理是什么

动态代理提供一种抽象，能够将对象中不同方法的调用重定向到一个统一的处理函数，做自定义的逻辑处理。但是对于调用者，对此毫无察觉，就好像调用的方法是用传统方式实现的一般。

这种语法，在java中被称为动态代理。之所以叫做动态代理，是因为它能避免传统代理模式实现中人工一个一个的将java函数转发过去，而是能够让代码自动做到这一点，这样代理类的代码是和业务无关的，不会因为业务类的方法增多而逐渐庞大。使代码更易维护更易修改，实现自动化搬砖。

实际上，被代理的类不一定位于本机类，动态代理语法提供了一种抽象方式，被代理的类也可以位于远程主机上，这也是RPC框架实现原理的一部分。

理解了动态代理的概念后不难发现，动态代理概念上有着这么几个部分：

给调用者使用的代理类。在java中，我们发现动态代理提供的抽象天然契合面向接口编程，因此它也有可能是接口。
一个统一的处理函数，收集不同函数转发过来的请求，可自定义处理逻辑集中处理。java中它可能会成为一个较独立的部分，因此也可能是类。

2. java动态代理机制

理解了概念，就不难理解java动态代理的机制了。下面来看看java动态代理机制如何代理一个本地对象。

2.1. 代理接口

首先看第一个部分，给调用者使用的代理类。在java动态代理机制中，这个角色只能是接口。我们定义一个整数运算接口：

interface NumberOperationInterface {
    int add(int a, int b);
}

2.2. 代理处理器

再看第二个角色，统一的处理函数。在java中它的确是类，通过实现InvocationHandler接口定义。

class NumberOperationImpProxyHandler implements InvocationHandler {
    private Object proxied;
    public RealObjectProxyHandler(Object proxied) {
        this.proxied = proxied;
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.printf("调用函数%s\n", method.getName());
        return method.invoke(proxied, args);
    }
}

由于我们的例子是代理本地对象，那么处理函数是需要被代理对象的信息。可以看到，我们从构造函数中将被代理对象保存在该类中，即可从处理函数中访问到。
在invoke函数中，对代理对象的所有方法的调用都被转发至该函数处理。在这里可以灵活的自定义各种你能想到的逻辑。在上面的代码中，我们使用反射调用被代理对象的同名方法实现。

2.3. 被代理类

由于我们的示例是代理本地对象，因此还需要一个被代理对象的类：

class NumberOperationImp implements NumerOperationInterface {
    @Override
    public int add(int a, int b) {
        return a + b;
    }
}

2.4. 创建代理对象

好了，各个组成部分都定义完成。现在把它们组合起来：

public NumerOperationInterface wrap(NumerOperationInterface proxied) {
    return (NumerOperationInterface) Proxy.newProxyInstance(
        NumerOperationInterface.class.getClassLoader(),
        new Class[]{NumerOperationInterface.class},
        new NumberOperationImpProxyHandler(proxied));
}

由于java提供的这个写法实在是太啰嗦了，所以把它放入一个辅助函数中。
Proxy.newProxyInstance 方法能够根据提供的接口和代理处理器创建代理对象。

java提供的写法太啰嗦了，可以考虑使用Guake提供的辅助函数简化下代码。如下：

public NumerOperationInterface wrap(NumerOperationInterface proxied) {
    return Reflection.newProxy(NumerOperationInterface.class, new NumberOperationImpProxyHandler(proxied));
}

好了，现在调用下试试：

NumerOperationInterface proxied = new NumberOperationImp();
real = wrap(proxied);
real.add(1, 2);

2.5. 总结

动态代理听起来是代理模式的动态实现，可是结合上面的最终效果，不觉得这个叫做动态装饰器更合适吗？

3. 动态代理的应用

说完了动态代理的概念和实现机制，该看看使用动态代理有哪些应用。

3.1. 应用一：代理模式/装饰器模式的动态实现

这个应用场景前面据已经提到过。代理模式和装饰器模式是编程当中很常用的技巧，用于提升代码的灵活性和可扩展性。传统代理模式的实现方式比较暴力直接，需要将所有被代理类的所有方法都写一遍，并且一个个的手动转发过去。在维护被代理类的同时，作为java码工还需要同时维护代理类的相关代码，实在是累心。

通过使用动态代理，动态代理能够自动将代理类的相关方法转发到被代理类，可以看到：

代理转发的过程自动化了，实现自动化搬砖。
代理类的代码逻辑和具体业务逻辑解耦，与业务无关。

3.2. 应用二：实现AOP

是的，利用动态代理也能实现AOP。仔细推演一下不能得出这个结论。我们知道：

动态代理提供了一种方式，能够将分散的方法调用转发到一个统一的处理函数处理。
AOP的实现需要能够提供这样一种机制，即在执行函数前和执行函数后都能执行自己定义的钩子。

那么，首先使用动态代理让代理类忠实的代理被代理类，然后处理函数中插入我们的自定义的钩子。之后让代理类替换被代理类需要使用的场景，这样，相当于对该类的所有方法定义了一个切面。

不过，使用动态代理实现AOP特别麻烦，啰嗦。这仅仅作为一个探讨的思路，来说明动态代理这一通用概念可以实现很多特定技术。实际使用中当然使用spring提供的AOP更为方便。

3.3. 应用三：实现RPC

RPC即远程过程调用，在分布式的网站架构中是一个非常重要的技术，目前现在流行的SOA架构，微服务架构，它们的核心原理之一就是RPC调用。

从概念上来说，RPC的概念是非常简洁优美的。RPC方法的调用和普通的方法并无二异，调用者不需要操心具体的实现，这是抽象提供的威力。实现上，它将函数调用方和函数的提供方分散在两个不同的进程上，中间使用网络通信来进行数据交互。

动态代理就是实现RPC的技术之一。只要理解了动态代理和RPC，我们很容易发现这样一个事实：RPC调用其实是对远程另外一台机器进程上的对象的代理。

仔细思考RPC调用的数据流流向，就能梳理出这样的思路：

调用方调用本地的RPC代理方法，将参数提供给该方法。
不同的RPC代理方法被转发到一个统一的处理中心，该处理中心知道调用的是那个函数，参数是什么。
该处理中心将调用的信息封装打包，通过网络发送给另外一个进程。
另外一个进程接受到调用进程发送过来的数据包。
该进程根据数据包中记录的RPC调用信息，将调用分发给对应的被代理对象的对应方法去执行。
返回的话思路类似。

显而易见，第二步，需要使用动态代理将分散的函数调用转发到一个统一的处理中心；第五步，将统一收集来的调用信息分发给具体的函数执行，显然使用反射做到这一点。

有了这个思路，通过利用动态代理，反射，和网络编程技术，实现一个简易版的RPC框架也就不难了。考虑到本文是介绍动态代理的，关于RPC的细节实现有时间新开一篇博文分析。

4. 最后

总得来说，通过一定的思考，个人觉得动态代理的核心在于：将分散的对对象不同方法的调用转发到一个同一的处理函数中来。

有了这个关键点，很多其它技术的实现需要借助于动态代理的这一个关键点实现，也因此动态代理也有着这么多的应用。