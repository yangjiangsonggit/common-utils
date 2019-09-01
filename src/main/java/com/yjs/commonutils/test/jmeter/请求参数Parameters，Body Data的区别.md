https://www.cnblogs.com/insane-Mr-Li/p/10686797.html

# JMeter基础：请求参数Parameters 、Body Data的区别
　　

使用Jmeter测试时，很多人不知道请求参数Parameters 、Body Data的区别和用途，这里简单介绍下

先了解一个接口的基本概念

在客户机和服务器之间进行请求-响应时，HTTP协议中包括GET和POST两种基本的请求方法，概念上两者的区别是：

get：从指定的资源请求数据。
post：向指定的资源提交要被处理的数据
最直观的表现就是：

GET把参数包含在URL中，POST通过request body传递参数将请求整体提交给服务器。所以接口测试时要区分两种方法对待。

 

Jmeter中包含Parameters和Body Data两种参数化的方式，区别是什么呢？

使用Parameters时，Content-Type不传，或者直接传application/x-www-from-urlencoded，若传application/json出错。
使用Body Data时，Content-Type可传application/x-www-from-urlencoded或application/json，两者的区别是数据格式不同。
可以从post data中看出区别：

Parameters的request请求数据：

 

 

body data的请求数据：

 

 

所以Jmeter的Parameters和Body Data两种参数化方式，具体应该怎么使用呢？

一般来说，Get请求用Parameters，Post请求用Body Data。

精确的对于Post的说法是：

普通的post请求和上传接口，选择Parameters。
json和xml点数据格式请求接口，选择Body。
详细来解释，根据post请求数据的格式，分两种情况：map格式、json格式。

post请求数据为map格式
当post请求数据为map格式，即参数名、参数值为key-value键值对，请求的参数添加在Parameters参数表中即可，如下图：

 

 

post请求数据为json格式 　　
目前阶段比较流行的是json格式传递参数，使用jmeter时，将json格式的请求数据加入到http请求BodyData中，如下图：

 

 

 

另外，由于POST请求方式，如果不设置Headers的content-type，基本默认会以 application/x-www-form-urlencoded 方式提交数据。为了Post请求确保传的是json格式，还需要在请求头中声明一下请求参数的格式为json，具体操作如下：

Thread Group右键Add》Config Element》HTTP Header Manager，打开HTTP信息头管理器，Add一个Name为Content-Type，Value为application/json，如下图，一个json格式的post请求完成了



 
