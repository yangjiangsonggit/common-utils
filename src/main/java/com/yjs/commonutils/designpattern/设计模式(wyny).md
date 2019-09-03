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

### 1.2 设计模式有什么用
- (1) 设计模式来源众多专家的经验和智慧，它们是从许多优秀的软件系统中总结出的成功的、能够实现可维护性复用的设计方案，使用这些方案将可以
让我们避免做一些重复性的工作，也许我们冥思苦想得到的一个“自以为很了不起”的设计方案其实就是某一个设计模式。
- (2) 设计模式提供了一套通用的设计词汇和一种通用的形式来方便开发人员之间沟通和交流，使得设计方案更加通俗易懂。
- (3) 大部分设计模式都兼顾了系统的可重用性和可扩展性，这使得我们可以更好地重用一些已有的设计方案、功能模块甚至一个完整的软件系统，
避免我们经常做一些重复的设计、编写一些重复的代码。
- (4) 合理使用设计模式并对设计模式的使用情况进行文档化，将有助于别人更快地理解系统。
- (5) 最后一点对初学者很重要，学习设计模式将有助于初学者更加深入地理解面向对象思想，让你知道：如何将代码分散在几个不同的类中？
为什么要有“接口”？何谓针对抽象编程？何时不应该使用继承？如果不修改源代码增加新功能？同时还让你能够更好地阅读和理解现有类库（如JDK）
与其他系统中的源代码.

## 2 面向对象设计原则
> 对于面向对象软件系统的设计而言，在支持可维护性的同时，提高系统的可复用性是一个至关重要的问题，如何同时提高一个软件系统的可维护性
和可复用性是面向对象设计需要解决的核心问题之一。在面向对象设计中，可维护性的复用是以设计原则为基础的。每一个原则都蕴含一些面向对象
设计的思想，可以从不同的角度提升一个软件结构的设计水平。面向对象设计原则也是我们用于评价一个设计模式的使用效果的重要指标之一，在
设计模式的学习中，大家经常会看到诸如“XXX模式符合XXX原则”、“XXX模式违反了XXX原则”这样的语句。
                            
    
设计原则名称 |	定 义 |	使用频率
------ | ------ | ------ 
单一职责原则 (Single Responsibility Principle, SRP) |	一个类只负责一个功能领域中的相应职责 |	★★★★☆
开闭原则 (Open-Closed Principle, OCP)	 | 软件实体应对扩展开放，而对修改关闭 |	★★★★★
里氏代换原则 (Liskov Substitution Principle, LSP) |	所有引用基类对象的地方能够透明地使用其子类的对象 |	★★★★★
依赖倒转原则 (Dependence Inversion Principle, DIP) |	抽象不应该依赖于细节，细节应该依赖于抽象 |	★★★★★
接口隔离原则 (Interface Segregation Principle, ISP) |	使用多个专门的接口，而不使用单一的总接口 |	★★☆☆☆
合成复用原则 (Composite Reuse Principle, CRP) |	尽量使用对象组合，而不是继承来达到复用的目的 |	★★★★☆
迪米特法则 (Law of Demeter, LoD) |	一个软件实体应当尽可能少地与其他实体发生相互作用 |	★★★☆☆


### 2.1 单一职责原则
单一职责原则是最简单的面向对象设计原则，它用于控制类的粒度大小。单一职责原则定义如下： 
单一职责原则(Single Responsibility Principle, SRP)：一个类只负责一个功能领域中的相应职责，
或者可以定义为：就一个类而言，应该只有一个引起它变化的原因。

单一职责原则告诉我们：一个类不能太“累”！在软件系统中，一个类（大到模块，小到方法）承担的职责越多，它被复用的可能性就越小，
而且一个类承担的职责过多，就相当于将这些职责耦合在一起，当其中一个职责变化时，可能会影响其他职责的运作，因此要将这些职责进行分离，
将不同的职责封装在不同的类中，即将不同的变化原因封装在不同的类中，如果多个职责总是同时发生改变则可将它们封装在同一类中。

单一职责原则是实现高内聚、低耦合的指导方针，它是最简单但又最难运用的原则，需要设计人员发现类的不同职责并将其分离，而发现类的多重职责需要
设计人员具有较强的分析设计能力和相关实践经验。

举例:

