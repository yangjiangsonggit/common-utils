tomcat 高并发高性能配置及虚拟机内存设置
2017年11月23日 10:25:37 KunQian_smile 阅读数：1571
当一个进程有 500 个线程在跑的话，那性能已经是很低很低了。Tomcat 默认配置的最大请求数是 150，也就是说同时支持 150 个并发，当然了，也可以将其改大。

当某个应用拥有 250 个以上并发的时候，应考虑应用服务器的集群。

  具体能承载多少并发，需要看硬件的配置，CPU 越多性能越高，分配给 JVM 的内存越多性能也就越高，但也会加重 GC 的负担。
操作系统对于进程中的线程数有一定的限制：

 Windows 每个进程中的线程数不允许超过 2000
 Linux 每个进程中的线程数不允许超过 1000

  另外，在 Java 中每开启一个线程需要耗用 1MB 的 JVM 内存空间用于作为线程栈之用。
      Tomcat的最大并发数是可以配置的，实际运用中，最大并发数与硬件性能和CPU数量都有很大关系的。更好的硬件，更多的处理器都会使Tomcat支持更多的并发。
       Tomcat 默认的 HTTP 实现是采用阻塞式的 Socket 通信，每个请求都需要创建一个线程处理。这种模式下的并发量受到线程数的限制，但对于 Tomcat 来说几乎没有 BUG 存在了。
       Tomcat 还可以配置 NIO 方式的 Socket 通信，在性能上高于阻塞式的，每个请求也不需要创建一个线程进行处理，并发能力比前者高。但没有阻塞式的成熟。
       这个并发能力还与应用的逻辑密切相关，如果逻辑很复杂需要大量的计算，那并发能力势必会下降。如果每个请求都含有很多的数据库操作，那么对于数据库的性能也是非常高的。
       对于单台数据库服务器来说，允许客户端的连接数量是有限制的。
       并发能力问题涉及整个系统架构和业务逻辑。
       系统环境不同，Tomcat版本不同、JDK版本不同、以及修改的设定参数不同。并发量的差异还是满大的。

 

 

 

1：配置executor属性
 

打开/conf/server.xml文件，在Connector之前配置一个线程池：

 

 <Executor maxIdleTime="10000" maxThreads="5000" maxSpareThreads="2000" maxIdleTime="10000"  minSpareThreads="1000" name="tomcatThreadPool" namePrefix="catalina-exec-" prestartminSpareThreads="true"/>

 

重要参数说明：

name：共享线程池的名字。这是Connector为了共享线程池要引用的名字，该名字必须唯一。默认值：None；

namePrefix:在JVM上，每个运行线程都可以有一个name 字符串。这一属性为线程池中每个线程的name字符串设置了一个前缀，Tomcat将把线程号追加到这一前缀的后面。默认值：tomcat-exec-；

maxThreads：该线程池可以容纳的最大线程数(最大并发数)。默认值：200；

maxIdleTime：在Tomcat关闭一个空闲线程之前，允许空闲线程持续的时间(以毫秒为单位)。只有当前活跃的线程数大于minSpareThread的值，才会关闭空闲线程。默认值：60000(一分钟)。

minSpareThreads：Tomcat应该始终打开的最小不活跃线程数(初始化时创建的线程数)。默认值：25。

maxSpareThreads:一旦创建的线程超过这个值，Tomcat就会关闭不再需要的socket线程。

 prestartminSpareThreads="true" : executor启动时，是否开启最小的线程数

 

2：配置Connectoreads
<Connector URIEncoding="UTF-8" acceptCount="5000" maxProcessors="1000" connectionTimeout="20000" executor="tomcatThreadPool" port="80" protocol="HTTP/1.1" redirectPort="443" useBodyEncodingForURI="true"/>

重要参数说明：
executor：表示使用该参数值对应的线程池；

minProcessors：服务器启动时创建的处理请求的线程数；

maxProcessors：最大可以创建的处理请求的线程数(最大活跃线程数)；

acceptCount：指定当所有可以使用的处理请求的线程数都被使用时，可以放到处理队列中的请求数，超过这个数的请求将不予处理。应大于等于 maxProcessors

enableLookups：是否反查域名，取值为： true 或 false 。为了提高处理能力，应设置为 false

connectionTimeout：网络连接超时，单位：毫秒。设置为 0 表示永不超时，这样设置有隐患的。通常可设置为 30000 毫秒。
其中和最大连接数相关的参数为maxProcessors 和 acceptCount 。如果要加大并发连接数，应同时加大这两个参数。

web server允许的最大连接数还受制于操作系统的内核参数设置，通常 Windows 是 2000 个左右， Linux 是 1000 个左右。

enableLookups      若设为true, 则支持域名解析，可把 ip 地址解析为主机名

redirectPort       在需要基于安全通道的场合，把客户请求转发到基于SSL 的 redirectPort 端口

URIEncoding    URL统一编码

 

3：虚拟机内存设置
（1）设置JVM内存的参数有四个：

-Xmx   Java Heap最大值，默认值为物理内存的1/4，最佳设值应该视物理内存大小及计算机内其他内存开销而定；

-Xms   Java Heap初始值，Server端JVM最好将-Xms和-Xmx设为相同值，开发测试机JVM可以保留默认值；

-Xmn   Java Heap Young区大小，不熟悉最好保留默认值；

-Xss   每个线程的Stack大小，不熟悉最好保留默认值；

-XX:PermSize：设定内存的永久保存区域； 

-XX:MaxPermSize：设定最大内存的永久保存区域；

-XX:PermSize：设定内存的永久保存区域；

-XX:NewSize：设置JVM堆的‘新生代’的默认大小；

-XX:MaxNewSize：设置JVM堆的‘新生代’的最大大小； 

Tomcat默认的Java虚拟机JVM启动内存参数大约只有64MB或者128MB，非常小，远远没有利用现在服务器的强大内存，所以要设置Java虚拟机JVM启动内存参数。具体设置方法为：

  Tomcat修改TOMCAT_HOME/bin/catalina.bat，在[echo Using CATALINA_BASE:   "%CATALINA_BASE%"] 上面加入，比如：

  set JAVA_OPTS= -server -Xms1536m -Xmx1536m或者JAVA_OPTS="-server -Xms1536m -Xmx1536m"，

  服务器模式参数-server不加也可以 ，就变成

  set JAVA_OPTS= -Xms1536m -Xmx1536m或者JAVA_OPTS=" -Xms1536m -Xmx1536m"，

 

在下列DOS命令窗口中执行java -Xmx1024m -version，显示出JDK/JRE/JVM版本号，说明最大能使用1024MB的Java虚拟机内存

 

 

（2）. 如何设置JVM内存分配：

1>当在命令提示符下启动并使用JVM时（只对当前运行的类Test生效）：

java -Xmx128m -Xms64m -Xmn32m -Xss16m Test

2>当在集成开发环境下（如eclipse）启动并使用JVM时：

a. 在eclipse根目录下打开eclipse.ini，默认内容为（这里设置的是运行当前开发工具的JVM内存分配）：

-vmargs  
-Xms40m  
-Xmx256m
-vmargs表示以下为虚拟机设置参数，可修改其中的参数值，也可添加-Xmn，-Xss，另外，eclipse.ini内还可以设置非堆内存，如：-XX:PermSize=56m，-XX:MaxPermSize=128m。

此处设置的参数值可以通过以下配置在开发工具的状态栏显示：

在eclipse根目录下创建文件options，文件内容为：org.eclipse.ui/perf/showHeapStatus=true

修改eclipse根目录下的eclipse.ini文件，在开头处添加如下内容：

-debug  
options  
-vm  
javaw.exe
重新启动eclipse，就可以看到下方状态条多了JVM信息。

b. 打开eclipse－窗口－首选项－Java－已安装的JRE（对在当前开发环境中运行的java程序皆生效）

编辑当前使用的JRE，在缺省VM参数中输入：-Xmx128m -Xms64m -Xmn32m -Xss16m

c. 打开eclipse－运行－运行－Java应用程序（只对所设置的java类生效）

选定需设置内存分配的类－自变量，在VM自变量中输入：-Xmx128m -Xms64m -Xmn32m -Xss16m

注：如果在同一开发环境中同时进行了b和c设置，则b设置生效，c设置无效，如：

开发环境的设置为：-Xmx256m，而类Test的设置为：-Xmx128m -Xms64m，则运行Test时生效的设置为：

-Xmx256m -Xms64m

3>当在服务器环境下（如Tomcat）启动并使用JVM时（对当前服务器环境下所以Java程序生效）：

a. 设置环境变量：

变量名：CATALINA_OPTS

变量值：-Xmx128m -Xms64m -Xmn32m -Xss16m

b. 打开Tomcat根目录下的bin文件夹，编辑catalina.bat，将其中的CATALINA_OPTS%（共有四处）替换为：-Xmx128m -Xms64m -Xmn32m -Xss16m

 

4：tomcat运行状态查看
首先在conf/tomcat-users.xml文件里面，在</tomcat-users>前面添加如下代码：

<role rolename="manager-status"/>
<role rolename="manager"/>  
<role rolename="manager-jmx"/> 
<role rolename="manager-gui"/>
<role rolename="manager-script"/>
<role rolename="admin"/>
 
<user username="tomcat" password="tomcat" roles="tomcat"/> 
 
<user username="admin" password="tomcat" roles="manager,manager-gui,admin,manager-status,manager-jmx,manager-script"/>

再打开：http://localhost/manager/status/