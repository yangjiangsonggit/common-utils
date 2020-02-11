# flowable预研

概念介绍:
https://www.jianshu.com/p/9757282345c0
文档:
https://flowable.com/open-source/docs/bpmn/ch02-GettingStarted/

## 什么是Flowable？

Flowable是用Java编写的轻量级业务流程引擎。Flowable流程引擎允许您部署BPMN 2.0流程定义（用于定义流程的行业XML标准），创建这些流程定义的流程实例，运行查询，访问活动或历史流程实例以及相关数据，以及更多其他功能。本节将通过示例在您自己的开发计算机上逐步介绍各种概念和API，以实现此目的。

当将Flowable添加到您的应用程序/服务/体系结构中时，它非常灵活。您可以通过包含Flowable库将引擎嵌入引擎到应用程序或服务中，该库可以作为JAR使用。由于它是一个JAR，因此您可以轻松地将其添加到任何Java环境中：Servlet容器，例如Tomcat或Jetty，Spring；Java EE服务器，例如JBoss或WebSphere，等等。或者，您可以使用Flowable REST API通过HTTP进行通信。还有一些Flowable应用程序（Flowable Modeler，Flowable Admin，Flowable IDM和Flowable Task），它们提供了用于处理流程和任务的现成示例UI。

设置Flowable的所有方式的共同点是核心引擎，它可以看作是服务的集合，这些服务公开了API来管理和执行业务流程。下面的各种教程首先介绍如何设置和使用此核心引擎。随后的各节将以前面各节中获得的知识为基础。

在首节仅使用Java SE普通的Java主：展示了如何在可能的最简单的方式运行到流动性。这里将解释许多核心概念和API。

将在可流动的REST API部分显示如何运行，并通过REST使用相同的API。

Flowable App上的这一节将指导您使用开箱即用的示例Flowable用户界面的基础知识。



通常，使用可视化建模工具（例如Flowable Designer（Eclipse）或Flowable Modeler（Web应用程序））对此类流程定义进行建模。


