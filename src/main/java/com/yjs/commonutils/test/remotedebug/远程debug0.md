# 远程调试

主动连接调试：服务端配置监控端口，本地IDE连接远程监听端口进行调试，一般调试问题用这种方式。

被动连接调试：本地IDE监听某端口，等待远程连接本地端口。一般用于远程服务启动不了，启动时连接到本地调试分析。

主动连接调试demo

我这里已经有个数据路由的项目，我以这个项目为例；

主动连接需要先启动服务端，后启动客户端

1.把项目打成jar包

选中项目右键----->Export--->Runnable JAR file，点击Next



2.选择程序入口和项目路径，点击finish完成



3.把导出的jar包放到指定盘符，我这里拷贝到了D盘，打开cmd窗口

输入命令：

jdk1.7版本之前的命令

java -agentlib:jdwp=transport=dt_socket,address=8000,server=y,suspend=y -jar xxx.jar

1.7版本之后的命令

1） java -agentlib:jdwp=transport=dt_socket,address=8000,server=y,suspend=y -jar xxx.jar

2） java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=8000 -jar test.jar



4.在eclipse中配置远程调试，右键项目----->Debug As----->Debug Configurations



第一次进来时，需要New一个Dcat



 

 

进入调试模式



被动连接调试demo

我这里还是以Dcat项目为例；

被动调试时eclipse作为服务端，运行的jar包作为客户端，所以先启动eclipse，再运行jar包；

1.在eclipse中配置远程调试，右键项目----->Debug As----->Debug Configurations



 

 2.打开cmd窗口，输入命令

 1） java -agentlib:jdwp=transport=dt_socket,address=172.18.11.216:8000,suspend=n -jar Dcat.jar 
 2） java -Xdebug -Xrunjdwp:transport=dt_socket,address=127.0.0.1:8000,suspend=y -jar Dcat.jar 


3.进入调试模式



命令参数详解

java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=8000 -jar test.jar

-Xdebug：通知JVM工作在debug模式下；

-Xrunjdwp：通知JVM使用(java debug wire protocol)来运行调试环境；

transport：监听Socket端口连接方式（也可以dt_shmem共享内存方式，但限于windows机器，并且服务提供端和调试端只能位于同一台机）；

server：server=y表示当前是调试服务端，=n表示当前是调试客户端；

suspend：suspend=n表示启动时不中断，一般用于设置主动连接；suspend=y表示启动时就进入调试模式，一般用于被动连接；

说明：不管是主动连接还是被动连接，我这里都尝试了设置suspend=y或suspend=n，最后的结果好像都一样，具体原因我也不清楚。

远程调试OSGI

说明：我这里只能演示OSGI的被动连接，主动连接由于不能连接设备展示演示不了

1.在F:\OSGICloundEnv0.1\conf文件夹下的wrapper.conf文件中添加如下配置

wrapper.java.additional.8=-Xdebug

wrapper.java.additional.9=-Xnoagent

wrapper.java.additional.10=-Djava.compiler=NONE

wrapper.java.additional.11=-Xrunjdwp:transport=dt_socket,address=172.18.11.226:8888,suspend=n



2.在eclipse中配置远程调试，右键项目----->Debug As----->Debug Configurations



3.启动osgi

clean.bat

runosgi.bat

4.进入到调试界面