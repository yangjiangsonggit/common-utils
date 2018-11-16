##Drools是一款基于Java的开源规则引擎
　　实现了将业务决策从应用程序中分离出来。

　　优点：
　　　　1、简化系统架构，优化应用
　　　　2、提高系统的可维护性和维护成本
　　　　3、方便系统的整合
　　　　4、减少编写“硬代码”业务规则的成本和风险
Drools的基本工作工程
　　我们需要传递进去数据，用于规则的检查，调用外部接口，同时还可能获取规则执行完毕之后得到的结果

Fact对象：
　　指传递给drools脚本的对象，是一个普通的javabean，原来javaBean对象的引用，可以对该对象进行读写操作，并调用该对象的方法
　　当一个java bean插入到working Memory（内存存储）中，规则使用的是原有对象的引用，规则通过对fact对象的读写，
　　实现对应用数据的读写，对其中的属性，需要提供get和set方法，规则中可以动态的前往working memory中插入删除新的fact对象
Drools的基础语法：
　　包路径，引用，规则体 (其中包路径和规则体是必须的)

Drl文件内容：
　　例子：
　　　　hello.drl文件如下：

复制代码
package rules.testword
rule "test001"
　　when 
　　　　//这里如果为空，则表示eval(true)
　　then
　　　　System.out.println("hello word");
end
复制代码
 

　　package：包路径，该路径是逻辑路径(可以随便写，但是不能不写，最好和文件目录同名，以(.)的方式隔开)，规则文件中永远是第一行
　　rule：规则体，以rule开头，以end结尾，每个文件可以包含多个rule	，规则体分为3个部分：LHS，RHS，属性 三大部分
　　LHS：(Left Hand Side)，条件部分，在一个规则当中“when”和“then”中间的部分就是LHS部分，在LHS当中，可以包含0~N个条件，如果
　　　　LHS为空的话，那么引擎会自动添加一个eval(true)的条件，由于该条件总是返回true，所以LHS为空的规则总是返回true。
　　RHS：(Right Hand Side)，在一个规则中“then”后面的部分就是RHS，只有在LHS的所有条件都满足的情况下，RHS部分才会执行。
　　　　RHS部分是规则真正做事情的部分，满足条件触发动作的操作部分，在RHS可以使用LHS部分当中的定义的绑定变量名，设置的全局变量、
　　　　或者是直接编写的java代码，可以使用import的类。
　　　　不建议有条件判断。
　　　　可以使用快速操作working Memory的宏函数和对象，比如insert/insertLogical,update/modify和retract就可以实现对当前Working Memory中的Fact对象
　　　　进行新增，修改，或者删除，可以使用drool宏对象，Drools还提供了kcontext的宏对象，该对象可以直接访问当前Working Memory的KnowledgeRuntime。

　　import：导入规则文件需要使用到的外部变量，可以导入类，也可以是这个类中的静态方法
　　　　例如：
　　　　　　import com.dinpay.dpp.rcp.service.util.RuleLogUtil; 导入类
　　　　　　import com.dinpay.dpp.rcp.service.util.RuleLogUtil.getLog;//导入静态方法

Drools的API调用
　　API可以分为三类：规则编译，规则收集，规则执行

　　1、Kmodule.xml的编译
　　　　存放在src/main/resources/META-INF/文件夹下

复制代码
<?xml version="1.0" encoding="UTF-8"?>
<kmodule xmlns="http://jboss.org/kie/6.0.0/kmodule">
    <kbase name="rules" packages="rules.testword">
        <ksession name="session"/>
    </kbase>
</kmodule>        
复制代码
　　　　1)、可包含多个kbase，任意但不能重名
　　　　2)、有个packages，就是src/main/resources下面的文件夹名称，可定义多个包，用逗号隔开
　　　　3)、ksession都一个name，任意字符串但不能重名，可以有多个
　　　　4)、在运行时、KieContainer会根据*Model对象来创建KieModule，KieBase，KieSession对象，其中KieModule和KieBase只会创建一次，而KieSession则可能创建多次

　　2、API说明，引入drools依赖jar包，drools-core，kie-api，drools-compiler


KieSession；
　　用于与规则引擎进行交互的会话
　　分为两类：
　　　　有状态KieSession：KieSession会在多次与规则引擎进行交互中，维护会话状态，type属性值是stateful，
　　　　　　　　　　　　　最后需要清理KieSession维护的状态，调用dispose()方法

　　　　无状态StatelessKieSession：StatelessKieSession隔离了每次与规则引擎的交互，不会维护会话状态，无副作用，type属性值是stateless
　　　　　　　应用场景：数据校验，运算，数据过滤，消息路由，任何能被描述成函数或公式的规则


Drools内部功能详细介绍
规则文件
　　一个标准的规则文件的结构代码：
　　　　package package-name(包名，必须的，只限制于逻辑上的管理，若自定义查询或者函数属于同一个包名，不管物理位置如何，都可以调用)
　　　　imports (需要导入的类名)
　　　　globals (全局变量)
　　　　functions (函数)
　　　　queries (查询)
　　　　rules (规则，可以多个)	

package在规则文件中是第一行，其他的顺序可以是无序的，package对于规则文件中规则的管理只限于逻辑上的管理


规则语言
　　rule “name”
　　　　attributes ---->属性
　　　　when
　　　　　　LHS ---->条件
　　　　then
　　　　　　RHS	---->结果
　　end
　　一个规则包含三部分：唯有attributes部分可选，其他都是必填信息
　　　　定义当前规则执行的一些属性等，比如是否可被重复执行，过期时间，生效时间等
　　　　LHS：定义当前规则的条件，如 when Message();判断当前workingMemory中是否存在Message对象	
　　　　RHS：可以写java代码，即当前规则条件满足执行的操作，可以直接调用Fact对象的方法来操作应用

　　　　　　LHS：如果LHS部分为空，自动添加一个eval(true)操作
　　　　　　　　   LHS部分是由一个或多个条件组成，条件又称为pattern(匹配模式)，多个pattern之间可以使用 and 或 or来进行连接，同时还可以使用小括号来确定pattern的优先级
　　　　　　　　 【绑定变量名：】Object(【filed 约束】)
　　　　　　　　 对于一个pattern来说"绑定变量名"是可选的，如果在当前规则的LHS部分的其他pattern要使用这个对象，那么可以通过为该对象绑定设定一个
　　　　　　　　绑定变量名来实现对其的引用，对于绑定变量的命名，通常的做法是为其添加一个 "$"符号作为前缀，可以和Fact对象区分开来
　　　　　　　　绑定变量可以用于对象上，可以用于对象属性上，"field约束"是指当前对象里相关字段的条件限制

复制代码
rule "rule1"
    when
        $customer:Customer()
    then
        <action>...
end            
复制代码
　　规则中LHS部分单个pattern(模式)的情形。
　　规则中"$customer"就是绑定到Customer对象的"绑定变量名"，该规则的LHS部分表示，要求Fact对象必须是Customer类型，该条件满足，那么它的LHS会返回true
复制代码
rule "rule1"
     when
           $customer:Customer(age>20,gender=="male")
           Order(customer==$customer,price>1000)
     then
           <action>...
end
复制代码
　　第一个pattern有三个约束

　　　　1、对象类型必须是Customer；
　　　　2、Customer的age要大于20
　　　　3、Customer的gender要是male
　　第二个pattern有三个约束
　　　　1、对象类型必须是Order
　　　　2、Order对应的Customer必须是前面那个Customer
　　　　3、当前这个Order的price要大于1000
　　　　这两个pattern没有符号连接，在Drools当中没有连接符号，默认是and，只有两个pattern(模式)都满足才会返回true，每行可以用";"结束

　　约束连接
　　　　对象内部的多个约束连接 "&&"(and),"||"(or)和","(and)来实现
　　　　如果记性常量的比较，必须通过eval(条件)或者对象引用比较对象属性，不能单独引用
　　　　12个类型比较操作符 >|<,>=|<=,==|!=,contains|not contains,memberOf|not memberOf,matches|not matches


Drools属性说明
　　salience优先级
　　　　作用：设置规则执行的优先级，值是一个数字，数字越大执行的优先级越高，它的值可以是一个负数，默认值是0
　　　　　　   如果我们不手动设置salience属性值，则执行顺序是随机的

　　no-loop防止死循环
　　　　在一个规则中如果条件满足就对Working Memory当中的某个Fact对象进行修改，比如使用update将其更新到当前的Working Memory当中，这时候引擎会再次检查所有的规则是否满足条件，如果满足会再执行，可能会出现死循环
　　　　作用：用来控制已经执行过的规则条件再次满足时是否再次执行，默认是false，如果属性值是true，表示该规则只会被规则引擎检查一次，如果满足条件就执行规则的RHS部分
　　　　注意：如果引擎内部因为对Fact更新引起引擎再次启动检查规则，那么它会忽略掉所有的no-loop属性设置为true的规则

　　　　例如以下情况：计算设置了no-loop为true也会出现死循环
　　　　

复制代码
package rules.testword
import com.drools.test.Person
    rule test001
        no-loop true
        when 
　　　　　　$p:Person(name=="张三");
　　　　 then
　　　　　　$p.setAge(50);
　　　　　　update($p);
　　　　　　System.out.println("设置no-loop时的效果");
　　end

rule test002
　　no-loop true
　　when 
　　　　$p:Person(age==50);
　　then
　　　　$p.setName("张三");
　　　　update($p);
　　　　System.out.println("设置no-loop时的效果");
end  
复制代码

　　date-effective日期比较小于等于

　　date-expires日期比较大于

　　Dialect方言

　　Enabled是否可用

　　lock-on-active规则执行一次
　　　　当在规则上使用ruleflow-group属性或agenda-group属性的时候，将lock-on-active属性的值设置为true，可以避免因某些Fact对象被修改而使已经执行过
　　　　的规则再次被激活执行。可以看出该属性与no-loop属性有相似之处，no-loop属性是为了避免Fact修改或调用了insert，retract，update之类导致
　　　　规则再次激活执行，这里lock-on-active属性也是这个作用，lock-on-active是no-loop的增强版。
　　　　作用：在使用ruleflow-group属性或agenda-group属性的时候，默认是false，设置为true，该规则只会执行一次

　　activation-group分组

　　agenda-greoup议程分组
　　　　规则的调用与执行是通过StatelessSession和ksession来实现的，一般的顺序是创建一个StatelessSession或ksession，
　　　　将各种经过编译的规则的package添加到session当中，接下来将规则当中可能用到的Global对象和Fact对象插入到Session当中，
　　　　最后调用fireAllRules方法来触发，执行规则，在没有调用最后一步分fireAllRules方法之前，
　　　　所有的规则及插入的Fact对象都存放在Agenda表的对象当中，这个Agenda表中的每个规则及其匹配相关的业务数据叫做Activation，
　　　　在调用fireAllRules方法后，这些Activation会依次执行，这些位于Agenda表中的Activation的执行顺序在没有设置相关用来
　　　　控制顺序的时(比如：salience属性)，它的执行顺序是随机不确定的。

　　　　agenda-group是用来在Agenda基础上，对现有的规则进行再次分组，具体的分组方法可以采用为规则添加agenda-group属性来实现，
　　　　agenda-group属性的值也是一个字符串，通过这个字符串，可以将规则分为若干个agenda group，默认情况下，引擎在调用这些
　　　　设置了agenda-group属性的规则的时候需要指定某个agenda group得到Focus(焦点)，这样位于该agenda group当中的规则才会触发执行，否则将不执行

　　　　实际应用中agenda-group可以和auto-focus属性一起使用


　　auto-focus焦点分组


　　ruleflow-group规则流

Drools drl注释的使用
　　单行// 多行/**/
Drools函数的使用
　　insert插入
　　语法格式：insert(new Object());

　　insertLogical插入

　　update修改
　　语法格式：update(Object());

　　retract删除功能

drools常用方法
　　方法名称　　　　　　　　　　	　　	用法格式	　　　　　　	　　	含义
　　getWorkingMemory()	　　drools.getWorkingMemory()	　　	获取当前WorkingMemory对象
　　halt()	　　　　　　　      drools.halt()	　　　　　　　　　  在当前规则执行完成之后，不再执行其他未执行的规则
　　getRule()	　　　　　      ——	　　　　　　　　　　	　　　获取当前规则对象
　　insert(new Object)	　　  ——	　　　　　　　　　　            插入指定对象
　　update(new Object)	　  ——                                                 更新指定对象
　　update(FactHandleObject) ——	　　　　　　　　　           更新指定对象
　　retract(new Object)	　　——	　　　　　　　　　　　　     删除指定对象

Drools语法篇之Global全局变量
　　global不是用来做数据共享的，session会影响到global的用法
　　注意：
　　　　1、常量值是不能改变的
　　　　2、包装类是不能改变的
　　　　3、类似javaBean，List这类的操作，是可以改变内容的，但内存地址是不会变的

Drools语法篇之查询Query

Drools语法篇之类的声明及元数据的用法
　　　　声明新类型：使用关键字declare，紧接着字段列表，和关键字end。
　　　　例如：

declare Address
    number：int
    streetName：String
    city：String
end