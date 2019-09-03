# 设计模式

## 1.概述
### 1.1 设计模式是什么
定义:设计模式(Design Pattern)是一套被反复使用、多数人知晓的、经过分类编目的、代码设计经验的总结，
使用设计模式是为了可重用代码、让代码更容易被他人理解并且保证代码可靠性。
<br>

设计模式一般包含模式名称、问题、目的、解决方案、效果等组成要素，其中关键要素是模式名称、问题、解决方案和效果。
- 问题(Problem)描述了应该在何时使用模式，它包含了设计中存在的问题以及问题存在的原因；
- 解决方案(Solution)描述了一个设计模式的组成成分，以及这些组成成分之间的相互关系，各自的职责和协作方式，通常解决方案通过UML类图和核心代码来进行描述；
- 效果(Consequences)描述了模式的优缺点以及在使用模式时应权衡的问题。
<br>

GoF设计模式只有23个，但是它们各具特色，每个模式都为某一个可重复的设计问题提供了一套解决方案。根据它们的用途，设计模式可分为
创建型(Creational)，结构型(Structural)和行为型(Behavioral)三种，其中创建型模式主要用于描述如何创建对象，结构型模式主要
用于描述如何实现类或对象的组合，行为型模式主要用于描述类或对象怎样交互以及怎样分配职责，在GoF 23种设计模式中包含5种创建型
设计模式、7种结构型设计模式和11种行为型设计模式。
<br>

类型	| 模式名称 | 学习难度 | 使用频率
------ | ------ | ------ | ------ 
创建型模式 Creational Pattern |  单例模式 Singleton Pattern |	★☆☆☆☆ |	★★★★☆
创建型模式 Creational Pattern	 | 简单工厂模式 Simple Factory Pattern |	★★☆☆☆ |	★★★☆☆
创建型模式 Creational Pattern	 | 工厂方法模式 Factory Method Pattern |	★★☆☆☆ |	★★★★★
创建型模式 Creational Pattern	 | 抽象工厂模式 Abstract Factory Pattern |	★★★★☆ |	★★★★★
创建型模式 Creational Pattern	 | 原型模式 Prototype Pattern	 | ★★★☆☆ | 	★★★☆☆
创建型模式 Creational Pattern	 | 建造者模式 Builder Pattern |	★★★★☆ |	★★☆☆☆
结构型模式 Structural Pattern	 | 适配器模式 Adapter Pattern	 | ★★☆☆☆ |	★★★★☆
结构型模式 Structural Pattern	 | 桥接模式 Bridge Pattern |	★★★☆☆ |	★★★☆☆
结构型模式 Structural Pattern	 | 组合模式 Composite Pattern |	★★★☆☆ |	★★★★☆
结构型模式 Structural Pattern	 | 装饰模式 Decorator Pattern |	★★★☆☆ |	★★★☆☆
结构型模式 Structural Pattern	 | 外观模式 Façade Pattern |	★☆☆☆☆ |	★★★★★
结构型模式 Structural Pattern |	享元模式 Flyweight Pattern |	★★★★☆ |	★☆☆☆☆
结构型模式 Structural Pattern	 | 代理模式 Proxy Pattern	 | ★★★☆☆ |	★★★★☆
行为型模式 Behavioral Pattern	 | 职责链模式 Chain of Responsibility Pattern |	★★★☆☆ |	★★☆☆☆
行为型模式 Behavioral Pattern	 | 命令模式 Command Pattern | 	★★★☆☆ |	★★★★☆
行为型模式 Behavioral Pattern | 解释器模式 Interpreter Pattern |	★★★★★ |	★☆☆☆☆
行为型模式 Behavioral Pattern	 | 迭代器模式 Iterator Pattern |	★★★☆☆ |	★★★★★
行为型模式 Behavioral Pattern	 | 中介者模式 Mediator Pattern |	★★★☆☆ |	★★☆☆☆
行为型模式 Behavioral Pattern	 | 备忘录模式 Memento Pattern |	★★☆☆☆ |	★★☆☆☆
行为型模式 Behavioral Pattern	 | 观察者模式 Observer Pattern |	★★★☆☆ |	★★★★★
行为型模式 Behavioral Pattern	 | 状态模式 State Pattern	 | ★★★☆☆ |	★★★☆☆
行为型模式 Behavioral Pattern	 | 策略模式 Strategy Pattern | 	★☆☆☆☆ |	★★★★☆
行为型模式 Behavioral Pattern	 | 模板方法模式 Template Method Pattern |	★★☆☆☆ |	★★★☆☆
行为型模式 Behavioral Pattern	 | 访问者模式 Visitor Pattern	 | ★★★★☆ |	★☆☆☆☆
<br>