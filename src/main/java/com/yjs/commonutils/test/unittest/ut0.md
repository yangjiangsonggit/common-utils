## 关于java 单元测试Junit4和Mock的一些总结


1. 单元测试的必要性
最近项目有在写java代码的单元测试，然后在思考一个问题，为什么要写单元测试？？单元测试写了有什么用？？百度了一圈，如下：

软件质量最简单、最有效的保证；
是目标代码最清晰、最有效的文档；
可以优化目标代码的设计；
是代码重构的保障；
是回归测试和持续集成的基石。
由于开发经验有限，可能说的不太对，但是是我目前的个人的观点，写单元测试，有时候确实可以发现bug，+ 但是发现bug次数很少，而且目前都是项目开发完了，要上线了，公司有80%的覆盖率要求，所以都是后期上线之前补。目前而言，并没有在很认真地写UT，只是想着完成上线要求。这个东西吧，也是看成本要求，如果一个新项目要紧急上线，走紧急发布特殊流程，单元测试后期时间充裕了再补上也行。所以，在时间允许情况下，我觉得还是要写UT，做了有时候确实能发现一些问题，尤其对于一个大的项目来说，一个bug被隐藏的时间越长，修复这个bug的代价就越大。在《快速软件开发》一书中已引用了大量的研究数据指出：最后才修改一个 bug 的代价是在bug产生时修改它的代价的10倍。此外，还能学到一些单元测试的知识，也算是一种技能上的进步吧。

2. Junit4 与 Mock 的介绍
目前应用比较普遍的java单元测试工具 junit4+Mock（Mockito /jmock / powermock）或Stub（用得较少，一般不推荐），由于junit3目前用得不多，基本升级到junit4了，所以就直接简单说下junit4。

问题一：为什么需要mock或stub？它与junit什么关系？

在做单元测试的时候，我们会发现我们要测试的方法会引用很多外部依赖的对象，比如：（发送邮件，网络通讯，记录Log, 文件系统 之类的）。 而我们没法控制这些外部依赖的对象。 为了解决这个问题，我们需要用到Stub和Mock来模拟这些外部依赖的对象,从而控制它们。

JUnit是单元测试框架，可以轻松的完成关联依赖关系少或者比较简单的类的单元测试，但是对于关联到其它比较复杂的类或对运行环境有要求的类的单元测试，模拟环境或者配置环境会非常耗时，实施单元测试比较困难。而这些“mock框架”（Mockito 、jmock 、 powermock、EasyMock），可以通过mock框架模拟一个对象的行为，从而隔离开我们不关心的其他对象，使得测试变得简单。（例如service调用dao，即service依赖dao，我们可以通过mock dao来模拟真实的dao调用，从而能达到测试service的目的。）

模拟对象（Mock Object）可以取代真实对象的位置，用于测试一些与真实对象进行交互或依赖于真实对象的功能，模拟对象的背后目的就是创建一个轻量级的、可控制的对象来代替测试中需要的真实对象，模拟真实对象的行为和功能。

问题二：mock与stub什么区别？

Mock和Stub是两种测试代码功能的方法。Mock测重于对功能的模拟，Stub测重于对功能的测试重现。比如对于List接口，Mock会直接对List进行模拟，而Stub会新建一个实现了List的TestList，在其中编写测试的代码。
强烈建议优先选择Mock方式，因为Mock方式下，模拟代码与测试代码放在一起，易读性好，而且扩展性、灵活性都比Stub好。

其中EasyMock和Mockito对于Java接口使用接口代理的方式来模拟，对于Java类使用继承的方式来模拟（也即会创建一个新的Class类）。Mockito支持spy方式，可以对实例进行模拟。但它们都不能对静态方法和final类进行模拟，powermock通过修改字节码来支持了此功能。

有篇文章介绍：http://blog.csdn.net/devhubs/article/details/8018084

二、junit4相关介绍

这里有篇文章介绍了junit4的一些，包括怎么引入，使用，蛮详细。---》 http://blog.csdn.net/happylee6688/article/details/38069761

这边就记录一些常用注解，当做学习方便。

常用注解
@Before：初始化方法，在任何一个测试方法执行之前，必须执行的代码。对比 JUnit 3 ，和 setUp（）方法具有相同的功能。在该注解的方法中，可以进行一些准备工作，比如初始化对象，打开网络连接等。

@After：释放资源，在任何一个测试方法执行之后，需要进行的收尾工作。对比 JUnit 3 ，和 tearDown（）方法具有相同的功能。

@Test：测试方法，表明这是一个测试方法。在 JUnit 中将会自动被执行。对与方法的声明也有如下要求：名字可以随便取，没有任何限制，但是返回值必须为 void ，而且不能有任何参数。如果违反这些规定，会在运行时抛出一个异常。不过，为了培养一个好的编程习惯，我们一般在测试的方法名上加 test ，比如：testAdd（）。
同时，该 Annotation（@Test） 还可以测试期望异常和超时时间，如 @Test（timeout=100），我们给测试函数设定一个执行时间，超过这个时间（100毫秒），他们就会被系统强行终止，并且系统还会向你汇报该函数结束的原因是因为超时，这样你就可以发现这些 bug 了。而且，它还可以测试期望的异常，例如，我们刚刚的那个空指针异常就可以这样：@Test(expected=NullPointerException.class)。

@Ignore：忽略的测试方法，标注的含义就是“某些方法尚未完成，咱不参与此次测试”；这样的话测试结果就会提示你有几个测试被忽略，而不是失败。一旦你完成了相应的函数，只需要把 @Ignore 注解删除即可，就可以进行正常测试了。当然，这个 @Ignore 注解对于像我这样有“强迫症”的人还是大有意义的。每当看到红色条（测试失败）的时候就会全身不舒服，感觉无法忍受（除非要测试的目的就是让它失败）。当然，对代码也是一样，无法忍受那些杂乱不堪的代码。

@BeforeClass：针对所有测试，也就是整个测试类中，在所有测试方法执行前，都会先执行由它注解的方法，而且只执行一次。当然，需要注意的是，修饰符必须是 public static void xxxx ；此 Annotation 是 JUnit 4 新增的功能。

@AfterClass：针对所有测试，也就是整个测试类中，在所有测试方法都执行完之后，才会执行由它注解的方法，而且只执行一次。当然，需要注意的是，修饰符也必须是 public static void xxxx ；此 Annotation 也是 JUnit 4 新增的功能，与 @BeforeClass 是一对。

执行顺序
所以，在 JUnit 4 中，单元测试用例的执行顺序为：

三、Mock的几种比较（Mockito 、jmock 、 powermock）

介绍文章一：http://blog.csdn.net/luvinahlc/article/details/10442743

介绍文章二：http://blog.csdn.net/zhangxin09/article/details/42422643

介绍文章三（Mockito 文档）：https://static.javadoc.io/org.mockito/mockito-core/2.8.47/org/mockito/Mockito.html

Spring提供了对Junit支持，可以使用注解的方式（注解加在需要测试的类上）:

@RunWIth(SpringJunit4ClassRunner.class) ---->为了让测试在Spring容器环境下执行

@ContextConfiguration(locations = {"classpath:applicationContext.xml"} --->用来指明Spring的配置文件位置

Mockito简单运用说明

① when(mock.someMethod()).thenReturn(value):设定mock对象某个方法调用时的返回值。可以连续设定返回值，即when(mock.someMethod()).thenReturn(value1).then
Return(value2),第一次调用时返回value1,第二次返回value2。也可以表示为如下：
when(mock.someMethod()).thenReturn(value1，value2)。
② 调用以上方法时抛出异常: when(mock.someMethod()).thenThrow(new Runtime
Exception());
③ 另一种stubbing语法：
doReturn(value).when(mock.someMethod())
doThrow(new RuntimeException()).when(mock.someMethod())
④ 对void方法进行方法预期设定只能用如下语法：
doNothing().when(mock.someMethod())
doThrow(new RuntimeException()).when(mock.someMethod())
doNothing().doThrow(new RuntimeException()).when(mock.someMethod())
⑤ 方法的参数可以使用参数模拟器，可以将anyInt()传入任何参数为int的方法，即anyInt匹配任何int类型的参数，anyString()匹配任何字符串，anySet()匹配任何Set。
⑥ Mock对象只能调用stubbed方法，调用不了它真实的方法，但是Mockito可以用spy来监控一个真实对象，这样既可以stubbing这个对象的方法让它返回我们的期望值，又可以使得对其他方法调用时将会调用它的真实方法。
⑦ Mockito会自动记录自己的交互行为，可以用verify(…).methodXxx(…)语法来验证方法Xxx是否按照预期进行了调用。
(1) 验证调用次数：verify(mock,times(n)).someMethod(argument),n为被调用的次数，如果超过或少于n都算失败。除了times(n)，还有never(),atLease(n),atMost(n)。
(2) 验证超时：verify(mock, timeout(100)).someMethod();
(3) 同时验证：verify(mock, timeout(100).times(1)).someMethod();

相关注解：

MockitoAnnotations.initMocks(this);

initializes fields annotated with Mockito annotations.

Allows shorthand creation of objects required for testing.
Minimizes repetitive mock creation code.
Makes the test class more readable.
Makes the verification error easier to read because field name is used to identify the mock.

ReflectionTestUtils.setField(AopTargetUtils.getTarget(appInfoService), "openAppInfoMapper",openAppInfoMapperMock);

但是由于Spring可以使用@Autoware类似的注解方式，对私有的成员进行赋值，此时无法直接对私有的依赖设置mock对象。可以通过引入ReflectionTestUtils，解决依赖注入的问题。

(不是很理解。。。。，因为我对某个service的private dao，直接mock，并没有设置ReflectionTestUtils.setField（），照样可以运行ok，那么这个什么时候用到？。)

@InjectMocks --- injects mock or spy fields into tested object automatically.

这个注解不会把一个类变成mock或是spy，但是会把当前对象下面的Mock/Spy类注入进去,按类型注入。

@Mock 生成的类，所有方法都不是真实的方法，而且返回值都是NULL。---> when(dao.getOrder()).thenReturn("returened by mock ");

@Spy ---Creates a spy of the real object. The spy calls real methods unless they are stubbed.

生成的类，所有方法都是真实方法，返回值都是和真实方法一样的。---> doReturn("twotwo").when(ps).getPriceTwo();

Mockito可以完成对一般对象方法的模拟，但是对于静态函数、构造函数、私有函数等还是无能为力.

