
###REST的概念是什么

我们为什么需要REST

前端渲染和后端渲染的优势是什么

什么样的REST才是"正宗"的


01

REST的概念是什么

_____



维基百科
表现层状态转换（REST，英文：Representational State Transfer）是Roy Thomas Fielding博士于2000年在他的博士论文中提出来的一种万维网软件架构风格，目的是便于不同软件/程序在网络（例如互联网）中互相传递信息。

论文地址：Architectural Styles and the Design of Network-based Software Architectures
REST章节：Fielding Dissertation: CHAPTER 5: Representational State Transfer (REST)

知乎
[资源]表现层状态转换（REST，英文：[Resource] Representational State Transfer），通俗翻译为：资源在网络中以某种表现形式进行状态转移。

Resource：资源，即数据（网络的核心），比如 goods，fruits等；
Representational：某种表现形式，比如用JSON，XML，JPEG等；
State Transfer：状态变化，通过HTTP动词实现。


02

我们为什么需要REST

_____



在前面介绍了REST的概念，不过对于初次接触REST的同学来说，可能会觉得晦涩难懂，因为只有几个新的名词概念解释而已，对于我们来说它又有什么用呢？



在介绍REST的作用之前，我们先了解一下在REST没有出现之前的Web开发是什么样的?



早期的Web 项目一般是在服务器端进行渲染，服务器进程从数据库获取数据后，然后利用后端模板引擎（比如Velocity、Freemaker 等）或者直接在HTML 模板中嵌入后端语言（比如JSP、PHP），将数据加载进来生成HTML，然后通过网络传输到用户的浏览器中，最后被浏览器解析成可见的页面。具体的过程如下图所示：



服务端渲染



此时大多数服务器架构都是这种 MVC 模式，前端只需要一次HTTP请求就可以返回整个页面内容，加载速度可能会稍微快些。但是它的缺点也非常明显，前端写完静态页面，要让后台去套模板，每次前端稍有改动，后台对应的模板页面同时也需要改动，而且页面中可能会包含大量复杂的 JS代码，比如美工同学（当时的前端）需要通过JS写界面的交互，而后端同学又需要通过JS实现数据的渲染，非常地麻烦。



当然事情麻烦归麻烦，但还不至于引发新的技术革命，而真正推动REST发展的是移动互联网的出现。由于多终端设备的兼容性需求，从前的服务端渲染已经很难满足要求了。服务端不可能针对每一个Client渲染一套界面，如果服务端只提供需要的数据，而具体界面的渲染完全交给具体的Client来完成，因此催生了REST的发展和普及。



RESTful可以通过一套统一的接口为 Web、iOS和Android提供服务，另外对于很多平台来说（比如像Facebook，Twiter、微博、微信等开放平台），它们不需要有显式的前端，只需要一套提供服务的接口，于是RESTful便是它们最好的选择。



REST API



03

前端渲染和后端渲染的优势是什么

_____



随着前端渲染引擎的发展，新兴的Angular，React，Vue等的出现，真正地实现了前后端分离解耦：前端专注于UI，负责View和Controller层，后端专注于业务/数据处理，负责Model层，两端通过设计好的REST API进行交互。



图片来自阿里玉伯的文章



参考文章
https://github.com/lifesinger/blog/issues/184
https://wenku.baidu.com/view/4c7b010c17fc700abb68a98271fe910ef12dae01



虽然现在前后端分离早已不是什么新鲜话题，不过前后端分离到底有什么样的优势呢？我们可以对比一下各自的优点。



后端渲染的优点：

1、对搜索引擎友好，这样做有利于 SEO。

2、加载时间短，后端渲染加载完成后就直接显示HTML，但前端渲染在加载完成后还需要有段js 渲染的时间。



前端渲染的优点：

1、让前后端的职责更清晰，分工更合理高效。前后端业务分离，后端只需要提供数据接口，前端在开发时也不需要部署对应的后端环境，可以通过Mock数据进行并发开发。

2、计算量转移，原本由服务器执行的渲染任务转移给了客户端，这在大量用户访问的时候大大减轻后端的压力。让后端专注做后端应该做的事情，性能将大大提高，因为服务器做的事情确实减小了。



前后端分离+Node层优点：

通过 Node，Web Server 层也是 JavaScript 代码，这意味着部分代码可前后复用，需要 SEO 的场景可以在服务端同步渲染，由于异步请求太多导致的性能问题也可以通过服务端来缓解，结合了前两种模式的优点。

言归正传，前面介绍了什么是REST，也介绍了它的优势，接下来详细介绍REST的具体内容。

04

什么样的REST才是『正宗』的

_____



无状态原则
遵循REST范式的系统是无状态的，这意味着服务器不需要知道客户端处于什么状态，反之亦然。这样，即使没有看到以前的消息，服务器和客户端都可以理解收到的任何消息。这种的无状态约束是通过使用资源来实现的。资源是Web中的特定名词——它描述了任何你可能需要存储或发送到其他服务的对象或文档。

无状态原则是RESTful架构设计中一个非常重要的原则，无状态是相对于有状态而言的，我们首先看一下什么是有状态的。

Web服务的状态一般指的是请求的状态，是客户端和服务端进行交互操作时所留下来的公共信息(比如，用户的信息等)。这些信息可以被指定在不同的作用域中（如request、session、application等），通常由服务端来保存这些信息。

而无状态的Web服务是指每一个Web请求都是独立的，服务端没有保存任何客户端的状态信息，所以客户端发送的请求必须包含有能够让服务端理解请求的全部信息。

另外由于REST系统通过资源上的标准操作进行交互，因此它们不依赖于接口的实现，使得RESTful应用程序具有可靠性、快速性和可扩展性。



前后端通信机制


1、请求方式
REST要求客户端向服务端发出请求以获得或修改服务器上的数据。请求通常由以下部分组成：

一个HTTP动词，它定义了要执行的操作类型

一个头部，它允许客户端传递关于请求的信息

一条资源的路径

一个包含数据的可选消息主体

（1）对于HTTP动词

在REST系统中我们使用4个基本HTTP动词来与资源进行交互：

GET - 检索特定资源（通过id）或资源集合

POST - 创建一个新资源

PUT - 更新特定资源（通过ID）

DELETE - 按ID删除特定资源

（2）对于请求头部

客户端向服务端发送它能够接收的内容的类型，而该类型是通过一个叫Accept的字段发送的。通过这种方式可以确保服务端不会发送客户端无法理解或者无法处理的数据。

用于指定Accept字段的类型为MIME类型，它是由一个type和一个subtype通过斜线（/）分隔组成的。你可以在MDN Web文档中查看更多关于MIME类型的介绍。

例如，包含HTML的文本文件类型指定为text/html，而包含CSS的文本文件需要指定为text/css，一般的文本文件将被指定为text/plain（如果不指定，则默认值为text/plain）。假如客户期待的类型为text/css，而接收到的类型text/plain，那么客户端将无法识别它。以下列举了其他的type和subtype：

image — image/png, image/jpeg, image/gif

audio — audio/wav, image/mpeg

video — video/mp4, video/ogg

application — application/json, application/pdf, application/xml, application/octet-stream

（3）对于资源路径
在RESTful API中，每一个请求的动作都必须作用于一个资源路径上，所以资源路径的设计就是为了让客户端能够理解它所进行的操作是什么。

通常情况下，路径的第一部分应该是资源的复数形式。RESTful中这种路径嵌套方式简单易读，也更容易理解。示例如下：

https://www.alipay.com/customers/22/orders/11
该RESTful API指向的路径非常清晰，因为它具有层次性和自描述性。该示例中，我们查询了id号为22这位顾客的一笔订单，并且该订单的id号为11。

路径必须包含它所需要的能够准确定位它所代表的资源位置的信息。但是，如果引用的资源为列表或集合时，就不需要再向POST请求添加id这样的唯一标识了，因为在服务端将为该新对象生成一个唯一标识id的。例如向顾客集合中新增一位顾客：

POST https://www.alipay.com/customers
如果我们试图访问单个资源，则需要在路径后面添加一个id，例如通过指定的id来查询一位顾客：

GET https://www.alipay.com/customers/:id 
或者，通过指定的id来删除一位顾客：

DELETE https://www.alipay.com/customers/:id


2、接收内容
在服务器向客户端发送数据有效载荷的情况下，服务器必须content-type在响应的头部包含一个。这个content-type头域告诉客户端它在响应主体中发送的数据的类型。这些内容类型是MIME类型，就像它们在accept请求头的字段中一样。该content-type服务器在响应发送回应的客户机中指定的选项之一accept的请求的字段。

例如，当客户端使用此GET请求访问具有资源id23的articles资源时：

GET /articles/23 HTTP/1.1
Accept: text/html, application/xhtml
服务器可能会使用响应头发回内容：

HTTP/1.1 200 (OK)
Content-Type: text/html
这将意味着所请求的内容被返回的响应体用content-type的text/html，该客户表示，将能够接受。



3、返回状态
在服务器向客户端发送数据有效载荷的情况下，服务器必须content-type在响应的头部包含一个。这个content-type头域告诉客户端它在响应主体中发送的数据的类型。这些内容类型是MIME类型，就像它们在accept请求头的字段中一样。该content-type服务器在响应发送回应的客户机中指定的选项之一accept的请求的字段。

例如，当客户端使用此GET请求访问具有资源id23的articles资源时：

GET /articles/23 HTTP/1.1
Accept: text/html, application/xhtml
服务器可能会使用响应头发回内容：

HTTP/1.1 200 (OK)
Content-Type: text/html
来自服务器的响应包含状态代码，以提醒客户有关操作成功的信息。作为开发人员，您不需要知道每个状态代码（其中有很多），但您应该知道最常见的状态代码以及它们的使用方式：

状态码	含义
200 (OK)	这是成功HTTP请求的标准响应。
201 (CREATED)	这是导致成功创建项目的HTTP请求的标准响应。
204 (NO CONTENT)	这是成功HTTP请求的标准响应，响应正文中没有任何内容被返回。
400 (BAD REQUEST)	由于请求语法错误，大小过大或其他客户端错误，无法处理该请求。
403 (FORBIDDEN)	客户端没有权限访问此资源。
404 (NOT FOUND)	此时无法找到该资源。它可能已被删除，或尚不存在。
500 (INTERNAL SERVER ERROR)	如果没有更多可用的特定信息，则通用答案意外失败。
对于每个HTTP动词，服务器在成功时应返回预期的状态代码：

GET - 返回200（OK）。

POST - 返回201（创建）。

PUT - 返回200（OK）。

DELETE - 返回204（无内容）。如果操作失败，则返回可能对应于遇到的问题的最具体的状态码。



4、CRUD示例说明
现在我们需要设计一个班级管理的系统，其中需要有班级信息和班级的学生信息。那我们该怎么为它设计REST接口呢？可以按照以下几点思考：

使用什么样的请求方式？

服务端返回是什么样的？

通过什么样的content-type传输内容？

首先定义班级和学生的数据模型如下。

{
  “class”: {    "id": <Integer>,
    “name”: <String>,
    “num”:  <Integer>
  }
}
{
  “ student”: {
    "id": <Integer>,
    “name”: <String>,
    “age”:  <Integer>
  }
}
接口请求/响应的定义。

GET请求：

接口	请求方式	传输格式（Content-type）	返回状态
/classes	GET	application/json	200 (OK)
/classes/:id	GET	application/json	200 (OK)
/classes/:id/students	GET	application/json	200 (OK)
/classes/:id/students/:id	GET	application/json	200 (OK)
POST请求：

接口	请求方式	传输格式（Content-type）	返回状态
/classes	POST	application/json	201 (CREATED)
/classes/:id/students	POST	application/json	201 (CREATED)
PUT请求：

接口	请求方式	传输格式（Content-type）	返回状态
/classes/:id	PUT	application/json	200 (OK)
/classes/:id/students/:id	PUT	application/json	200 (OK)
DELETE请求：

接口	请求方式	传输格式（Content-type）	返回状态
/classes/:id	DELETE	application/json	204 (NO CONTENT)
/classes/:id/students/:id	DELETE	application/json	204 (NO CONTENT)
小结
本文介绍了REST的相关概念，同时介绍了我们为什么需要REST，分析了前端渲染和后端渲染的优势是什么，然后再介绍了什么样的REST才是"正宗"的，最后通过一个示例完整的演示了CRUD的操作。