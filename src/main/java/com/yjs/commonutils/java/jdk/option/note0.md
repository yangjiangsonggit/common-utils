Java8新特性之Optional类进阶知识

 	
 https://blog.csdn.net/jui121314/article/details/82683249
目录

●再说Optional类

●实战演练代码重构

●小结

●再说Optional类
上一篇文章概述性地介绍了一下Optional类，可能许多读者还是无法很好的掌握。笔者一开始接触这个类的时候也没有发现其有什么特别的好处，特别是对于“可以有效地避免空指针异常”这个特点理解得不够深刻，为什么这么说呢？结合一段代码，说说笔者当初的疑惑，以及是如何一步一步解决这个疑惑的：

//原始业务代码
User user = userService.getUserbyId(0);
user.setUserName("小明")；
 
 
//初次使用Optionnal
User user = userService.getUserbyId(0);
Optional<User> op = Optional.of(user);
if(op.isPresent()){
    op.get().setUserName("小明")；
}
逻辑很简单，设置id为0的用户姓名为“小明”。原始业务代码中，确实可能出现空指针异常，因为数据库中可能不存在id为0的用户。因此，应该做一个非空判断。

初次尝试使用Optional类，发现居然也出现了一个空指针异常，在Optional<User> op = Optional.of(user);这一行，觉得很奇怪，不是说Optional类可以装一个null对象吗？不是说Optional类可以有效地避免空指针异常吗？怎么还会这样呢？后来又仔细查了一下，发现原来学到的只是皮毛，如果用工厂方法Optional.of()构造一个Optional类，必须保证参数是一个非空对象。但这个时候依然有疑惑，不是说Optional可以包装一个null的对象吗，为什么还会报空指针异常呢？

顺藤摸瓜，我们来看看Optional.of()方法的源码：

  /**
     * Returns an {@code Optional} with the specified present non-null value.
     *
     * @param <T> the class of the value
     * @param value the value to be present, which must be non-null
     * @return an {@code Optional} with the value present
     * @throws NullPointerException if value is null
     */
    public static <T> Optional<T> of(T value) {
        return new Optional<>(value);
    }
 
 
  /**
     * Constructs an instance with the value present.
     *
     * @param value the non-null value to be present
     * @throws NullPointerException if value is null
     */
    private Optional(T value) {
        this.value = Objects.requireNonNull(value);
    }
 
  /**
     * Checks that the specified object reference is not {@code null}. This
     * method is designed primarily for doing parameter validation in methods
     * and constructors, as demonstrated below:
     * <blockquote><pre>
     * public Foo(Bar bar) {
     *     this.bar = Objects.requireNonNull(bar);
     * }
     * </pre></blockquote>
     *
     * @param obj the object reference to check for nullity
     * @param <T> the type of the reference
     * @return {@code obj} if not {@code null}
     * @throws NullPointerException if {@code obj} is {@code null}
     */
    public static <T> T requireNonNull(T obj) {
        if (obj == null)
            throw new NullPointerException();
        return obj;
    }
原来，使用这个工厂方法构造Optional类的时候，用到了Objects.requireNonNull()的空值校验方法，因此如果传入参数为null，确实会产生空指针异常。结合实际情况，此时应该采用另一个工厂方法Optional.ofNullable()来进行构造。

OK，既然这样，那我们将代码更改为：

User user = userService.getUserbyId(0);
Optional<User> op = Optional.ofNullable(user);
if(op.isPresent()){
    op.get().setUserName("小明")；
}
可以，问题又来了，确实，你可以说使用.isPresent()是函数调用，可是它真的体现了函数式编程的优点吗？这种写法和下面的写法不是类似的吗？

User user = userService.getUserbyId(0);
if(null != user){
    user.setUserName("小明")；
}
我如果忘记使用if(op.isPresent())对Optional对象判空，程序照样报空指针异常。网上有一种解释，使用Optional强制你每次都要做采用固定的步骤：isPresent()和get()，强制要求你做Optional非空判断，避免报错。当时笔者的第一反应是WTF？？？那我以后每个对象也采用固定的步骤：if(null != T)，强制要求你做非空判断，避免报错，这不是一样的吗？简直是强行套个解释嘛。

后来，笔者又阅读了大量Optional的文章以及《Java8函数式编程》这本书的相关章节，仔细思考后，终于有点领悟，和大家分享一下Optional的正确打开方式——

放弃掉isPresent()和get()的用法，forget it！！！
充分结合java8的Lambda与Stream新特性，来一次链式调用吧。
网上告诉你的isPresent()和get()固定套路，根本就是瞎扯，真正体现Optional“有效避免空指针异常”是其ifPresent()、orElse()、orElseGet()以及orElseThrow()这几个方法。

业务场景(op代表Optional对象)	正确用法示例	错误用法示例
如果op中的对象不为空，则进行操作	op.ifPresent(o -> o.setUserName("小明"));	
if(op.isPresent()){

        op.get().setUserName("小明"));

}

如果op中的对象不为空，则返回它；否则返回另一个值	op.orElse(initUser);	
if(op.isPresent()){

       return op.get();

}

else{

        return initUser;

}

如果op中的对象不为空，则返回它；否则进行操作	op.orElseGet(() -> new User(0, "小明"));	if(op.isPresent()){
       return op.get();

}

else{

        return new User(0, "小明");

}

如果op中的对象不为空，则返回它；否则抛出异常	
op.orElseThrow(IllegalArgumentException::new);

if(op.isPresent()){
       return op.get();

}

else{

        throw new IllegalArgumentException()

}

 

●实战演练代码重构
这么说比较抽象，我么结合具体的项目代码看一看，例如，前台发起请求，传来一个报警参数，后台提取报警的ID，去数据库中查询对应ID的报警事件，并且获取该报警事件的名字和类型。我们来看看实现这个需求，传统的JAVA7的代码会怎么写：

    public String test0(AlarmAllParmeter alarmAllParmeter) {
        String errorResult = "";
        if (null != alarmAllParmeter) {
            Integer alarmId = alarmAllParmeter.getAlarmEventInputId();
            if (null != alarmId) {
                AlarmEventInput alarmEventInput = alarmEventInputService.get(alarmId);
                if (null != alarmEventInput) {
                    String alarmName = alarmEventInput.getAlarmName();
                    int alarmType = alarmEventInput.getAlarmType();
                    return String.valueOf(alarmType) + "-" + alarmName;
                } else {
                    return errorResult;
                }
            } else {
                return errorResult;
            }
        } else {
            return errorResult;
        }
    }
可以明显看出，为了防止空指针异常，我们在代码中写了大量的if(null != T)的模板代码。而初次学习Optional类的朋友很可能会写出如下代码：

    public String test1(AlarmAllParmeter alarmAllParmeter){
        String errorResult = "";
        Optional<AlarmAllParmeter> op = Optional.ofNullable(alarmAllParmeter);
        if(op.isPresent()){
            Integer alarmId = op.get().getAlarmEventInputId();
            Optional<Integer> op1 = Optional.ofNullable(alarmId);
            if(op1.isPresent()){
                AlarmEventInput alarmEventInput = alarmEventInputService.get(op1.get());
                Optional<AlarmEventInput> op2 = Optional.ofNullable(alarmEventInput);
                if (op2.isPresent()) {
                    String alarmName = alarmEventInput.getAlarmName();
                    int alarmType = alarmEventInput.getAlarmType();
                    return String.valueOf(alarmType) + "-" + alarmName;
                } else {
                    return errorResult;
                }
            }
            else {
                return errorResult;
            }
        }
        else {
            return errorResult;
        }
    }
可以看出，其编程的思路还是停留在“命令式编程”的层面，这种强行用Optional类的做法反而显得多此一举，本质上和传统写模板代码一样，真的就只是给对象套了个Optional容器而已。接下来，我们用Optional正确的打开方式来实现这个需求，重构最初的代码：

    public String test2(AlarmAllParmeter alarmAllParmeter){
        return Optional.ofNullable(alarmAllParmeter)
                       .map(a -> a.getAlarmEventInputId())
                       .map(a -> alarmEventInputService.get(a))
                       .map(a -> String.valueOf(a.getAlarmType())+"-"+a.getAlarmName())
                       .orElse("");
    }
最终通过Junit4测试结果如下：

public class OptionalTestTest {
 
    AlarmAllParmeter alarmAllParmeter = new AlarmAllParmeter();
    @Before
    public void setAlarmAllParmeter(){
        alarmAllParmeter.setAlarmEventInputId(1001);
    }
 
    @Test
    public void test0() {
        System.out.println("Test0 is: "+new OptionalTest().test0(alarmAllParmeter));
    }
 
    @Test
    public void test1() {
        System.out.println("Test1 is: "+new OptionalTest().test1(alarmAllParmeter));
    }
 
    @Test
    public void test2() {
        System.out.println("Test2 is: "+new OptionalTest().test2(alarmAllParmeter));
    }
}
 
控制台输出结果一致————
Test0 is: 1-测试报警实体
Test1 is: 1-测试报警实体
Test2 is: 1-测试报警实体
看见了吗，这就是JAVA8的魅力所在，Optional、Lambda、Stream的综合应用，极大的简化了代码的书写（方法体中其实就只要一行代码就可以完成，但是为了方便阅读，强烈建议大家在实战中按步骤分行完成链式调用）。这似乎看上去已经不是熟悉的那个Java语言了，但时代在进步，只有接受、习惯并顺利上手高效的新技术才能追得上整个行业的潮流。

 

●小结
关于Java8新特性的文章已经写了三篇了，基本上可以用到实际的项目中了，无论是开启新的项目，或者是重构以前臃肿的代码，都是值得一试的。笔者经过学习，发现Java8的改变带给人最大的感受是函数式编程的思想，这和以前命令式编程的思想完全不同，短期要完全接受还是有点难度的，但真的习惯了这种新的编程思路你会发现效率非常的高。就好比当初学习C++的时候，对比C，感觉多了一个对象的概念，比较抽象，但之后再回头去看，才真的体会到面向对象编程相比于面向过程编程的强大优势所在。不得不说Java8的这颗语法糖真的很甜。

最后，再次强调一下，这一篇文章所提到的Optional的正确使用方式是进行链式处理，而不应该像不少网文所说的那样去做isPresent()判断，再去get()取值。今天，你学会了吗？