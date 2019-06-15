# REST

最近在写公司代码时候，因为是Restful风格的微服务架构，接口上我们要写上对应的URL和请求类型，原来只是简单的理解为get查找，delete删除，post创建，put用来查找。但昨天写方法的时候组长在一个方法用post和put犹豫了下。我感觉肯定有问题。而且自己对Rest原则好像也是似懂非懂的。所以网上找了下资料，整理了下。

1.什么是Rest

    REST这个词，是Roy Thomas Fielding在他2000年的博士论文中提出的，里面有下面一段：

    "本文研究计算机科学两大前沿----软件和网络----的交叉点。长期以来，软件研究主要关注软件设计的分类、设计方法的演化，很少客观地评估不同的设计选择对系统行为的影响。而相反地，网络研究主要关注系统之间通信行为的细节、如何改进特定通信机制的表现，常常忽视了一个事实，那就是改变应用程序的互动风格比改变互动协议，对整体表现有更大的影响。我这篇文章的写作目的，就是想在符合架构原理的前提下，理解和评估以网络为基础的应用软件的架构设计，得到一个功能强、性能好、适宜通信的架构。"
    (This dissertation explores a junction on the frontiers of two research disciplines in computer science: software and networking. Software research has long been concerned with the categorization of software designs and the development of design methodologies, but has rarely been able to objectively evaluate the impact of various design choices on system behavior. Networking research, in contrast, is focused on the details of generic communication behavior between systems and improving the performance of particular communication techniques, often ignoring the fact that changing the interaction style of an application can have more impact on performance than the communication protocols used for that interaction. My work is motivated by the desire to understand and evaluate the architectural design of network-based application software through principled use of architectural constraints, thereby obtaining the functional, performance, and social properties desired of an architecture. )

   我的理解用在上面那段话标记出来了，Rest是论文里提出的一种规范，它用于规范的是在原有网络传输协议（如http协议）不变的前提之上提出了一种应用之间交互风格。

2.Rest的交互风格是啥样的？

    首先，在了解网络交互风格的时候，我们得知道我们交互的是啥...

    交互的是 -- 资源！ 啥是资源？

    所谓"资源"，就是网络上的一个实体，或者说是网络上的一个具体信息。它可以是一段文本、一张图片、一首歌曲、一种服务，一个html、一个jsp总之就是一个具体的实在。你可以用一个 URI（统一资源定位符,URL和URI的关系可以看下这个链接：http://blog.csdn.net/Inuyasha1121/article/details/49799915）指向它，每种资源对应一个特定的URI。要获取这个资源，访问它的URI就可以，因此URI就成了每一个资源的地址或独一无二的识别符。

    这种交互风格有什么要求：

        1.每个资源都应该有一个唯一的标识    URI  
        2.使用标准的方法来更改资源的状态    像HTTP下的
        3.Request和Response的自描述
        4.资源多重表述
        5.无状态的服务

        几个标准的具体含义就直接放个链接了，有时间可以深入了解下：http://blog.csdn.net/zitong_ccnu/article/details/47779547

        如果一个架构符合REST交互风格要求，就称它为RESTful架构。

        REST交互风格要求下HTTP协议的操作风格是这样的：

        客户端用到的手段，只能是HTTP协议。具体来说，就是HTTP协议里面，四个表示操作方式的动词：GET、POST、PUT、DELETE。它们分别对应四种基本操作：GET用来获取资源，POST用来新建资源（也可以用于更新资源），PUT用来更新资源，DELETE用来删除资源。

              HTTP协议下RestFul框架的交互格式就像这样：   
              POST /uri 创建
              DELETE /uri/xxx 删除
              PUT /uri/xxx 更新或创建
              GET /uri/xxx 查看

             post和put都可以用来创建，这个就得弄清楚创建的区别啦。网上查了下，基本上都是说幂等性的，我就不再说了直接说下自己理解吧，post的创建是不安全的，我们每次创建都会创建出一个资源，而put是安全的他创建的对象包含唯一的信息，如果创建了，就不会在创建。打个比方创建一个人对象，用post发{男人}{女人}{男人}包含id，他会创建出{1：男人}{2：女人}{3：男人}，而在使用put时候得发{1：男人}，他会创建一个{1：男人}，再发{1：女人}他就会修改原有的信息最后留下的是{1：女人}，这里不是说我们用了post或者put就得这么干。而是结合我们方法产生的具体效果来选择用post或者get。

3.误区

    最常见的一种设计错误，就是URI包含动词。因为"资源"表示一种实体，所以应该是名词，URI不应该有动词，动词应该放在HTTP协议中。
举例来说，某个URI是/posts/show/1，其中show是动词，这个URI就设计错了，正确的写法应该是/posts/1，然后用GET方法表示show。
如果某些动作是HTTP动词表示不了的，你就应该把动作做成一种资源。比如网上汇款，从账户1向账户2汇款500元，错误的URI是：
　　POST /accounts/1/transfer/500/to/2
正确的写法是把动词transfer改成名词transaction，资源不能是动词，但是可以是一种服务：
　　POST /transaction HTTP/1.1
　　Host: 127.0.0.1
　　from=1&to=2&amount=500.00
另一个设计误区，就是在URI中加入版本号：
　　http://www.example.com/app/1.0/foo
　　http://www.example.com/app/1.1/foo
　　http://www.example.com/app/2.0/foo
因为不同的版本，可以理解成同一种资源的不同表现形式，所以应该采用同一个URI。版本号可以在HTTP请求头信息的Accept字段中进行区分（参见Versioning REST Services）：
　　Accept: vnd.example-com.foo+json; version=1.0
　　Accept: vnd.example-com.foo+json; version=1.1
　　Accept: vnd.example-com.foo+json; version=2.0



4.Http中的GET、POST、PUT、DELETE请求

    我们常常会碰到一个问题，问get的post的区别，我想大部分学网页开发的最开始都是解除这两个请求。答案大部分会想到下面中的：

    GET在浏览器回退时是无害的，而POST会再次提交请求。
    GET产生的URL地址可以被Bookmark，而POST不可以。
    GET请求会被浏览器主动cache，而POST不会，除非手动设置。
    GET请求只能进行url编码，而POST支持多种编码方式。
    GET请求参数会被完整保留在浏览器历史记录里，而POST中的参数不会被保留。
    GET请求在URL中传送的参数是有长度限制的，而POST么有。
    对参数的数据类型，GET只接受ASCII字符，而POST没有限制。
    GET比POST更不安全，因为参数直接暴露在URL上，所以不能用来传递敏感信息。
    GET参数通过URL传递，POST放在Request body中。

     但是这些差别是http规定的吗？我们看看原理：

     http协议是做用于应用层的一种协议，计算机网络里面学了应用层 下面有传输层，而传输层就有大名鼎鼎的tcp 和udp传输协议。而socket是操作系统为一套我们操作传输层的接口，http就是基于此之上建立的。所以我们的get、post、put、delete可以理解为一次tcp的连接并发送一次请求，可能有疑惑，每次发请求都伴随一次tcp的连接与释放（有三次握手啥的，大学的忘了好多。。。）吗？那效率不是很慢？记得以前在哪看到，好像新的http协议，我们建立一次tcp连接可以传输多次请求，比方说我们建立一次连接请求了一个html，html所需的css请求也在这次链接完成。 具体的我也没有深入了解。

     而连接多了以后怎么管理呢？http协议就提出了分类，也就是get、post、put、delete。 这是一种规范，就像你可以在get的协议体里传参，也可以在post url上传参，但是这是不优雅的哈哈。那上面说的参数长度的限制是怎么回事呢?这其实是浏览器所限制的，与http协议中并没有限制他。 而http不推荐的get请求体传参，有的浏览器也会帮我们屏蔽掉，有的不会。

4.谈谈get、post、put、delete和Rest的关

    上面我们说Rest又一个要求是得有一套标准的方法操作网络上的资源，而这四种刚好满足他的要求。



5. 最后说下自己的理解吧，早期互联网不发达的是时候，网上请求仅使用get/post 一个参数在外，一个参数在内。就已经满足了要求互联网之间访问的要求。而现在随着互联网的发展，服务与服务之间交互非常平繁， 项目内部现在越来越流行微服务架构啥的，得相互调用。外部比如我们登陆很多网站可以用qq登陆。得调用腾讯的服务。 之前的get post已经很难使我们调用变得清晰明了。所以引入了Rest，他规范了我们远程调用的交互行为。

