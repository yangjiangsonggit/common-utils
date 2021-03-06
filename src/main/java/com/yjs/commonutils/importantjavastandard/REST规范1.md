# REST1
REST（英文：Representational State Transfer，简称REST）描述了一个架构样式的网络系统，比如 web 应用程序。 
在服务器端，应用程序状态和功能可以分为各种资源。资源是一个有趣的概念实体，它向客户端公开。资源的例子有：应用程序对象、数据库记录、算法等等。每个资源都使用 URI (Universal Resource Identifier) 得到一个唯一的地址。所有资源都共享统一的接口，以便在客户端和服务器之间传输状态。使用的是标准的 HTTP 方法，比如 GET、PUT、POST 和 DELETE。 
GET 下载，PUT 更新，POST 新增，DELETE 删除，区别如下表格所示：

HTTP方法	数据处理	说明
POST	Create	新增一个没有id的资源
GET	Read	取得一个资源
PUT	Update	更新一个资源。或新增一个含id资源(如果id不存在)
DELETE	Delete	删除一个资源
GET操作是安全的。所谓安全是指不管进行多少次操作，资源的状态都不会改变。比如我用GET浏览文章，不管浏览多少次，那篇文章还在那，没有变化。当然，你可能说每浏览一次文章，文章的浏览数就加一，这不也改变了资源的状态么？这并不矛盾，因为这个改变不是GET操作引起的，而是用户自己设定的服务端逻辑造成的。

PUT，DELETE操作是幂等的。所谓幂等是指不管进行多少次操作，结果都一样。比如我用PUT修改一篇文章，然后在做同样的操作，每次操作后的结果并没有不同，DELETE也是一样。顺便说一句，因为GET操作是安全的，所以它自然也是幂等的。

POST操作既不是安全的，也不是幂等的，比如常见的POST重复加载问题：当我们多次发出同样的POST请求后，其结果是创建出了若干的资源。

安全和幂等的意义在于：当操作没有达到预期的目标时，我们可以不停的重试，而不会对资源产生副作用。从这个意义上说，POST操作往往是有害的，但很多时候我们还是不得不使用它。

还有一点需要注意的就是，创建操作可以使用POST，也可以使用PUT，区别在于POST 是作用在一个集合资源之上的（/uri），而PUT操作是作用在一个具体资源之上的（/uri/xxx），再通俗点说，如果URL可以在客户端确定，那么就使用PUT，如果是在服务端确定，那么就使用POST，比如说很多资源使用数据库自增主键作为标识信息，而创建的资源的标识信息到底是什么只能由服务端提供，这个时候就必须使用POST。

由于都是要传送数据，且数据格式相同（即使数据格式不同，只要能提取出相应数据）。使用的时候难免出现张冠李戴，将get数据用来存储、将post数据用来检索返回数据。但是二者还是有区别的（主要是根据其用途而“人为”[注3]造成的），get的长度限制在2048字节（由浏览器和服务器限制的，这是目前IE的数据，曾经是1024字节），很大程度上限制了get用来传递“存储数据”的数据的能力，所以还是老老实实用来做检索吧；post则无此限制（只是HTTP协议规范没有进行大小限制，但受限于服务器的处理能力），因此对于大的数据（一般来说需要存储的数据可能会比较大，比2048字节大）的传递有天然的优势，谁让它是 nature born post 呢。