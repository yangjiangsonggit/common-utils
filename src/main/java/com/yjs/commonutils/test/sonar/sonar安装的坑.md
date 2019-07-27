# 高版本sonar安装遇到的坑

最近安装了6.6版本的sonar，发现里面的坑还是很多，下面列举下遇到的坑

sonar插件地址：https://docs.sonarqube.org/display/PLUG/Plugin+Library

坑一 

6.6版本sonar要求jdk比较高，必须1.8以上，多以修改sonar启动项配置，修改文件/sonarqube-6.6/conf/wrapper.conf

#wrapper.java.command=/path/to/my/jdk/bin/java
#wrapper.java.command=java
wrapper.java.command=/sonar/jdk1.8.0_121/bin/java  //加入1.8jdk作为启动jdk
 

坑二

由于6.6版本加入了elasticsearch，遇到不能以root用户启动，报错信息如下：

复制代码

java.lang.RuntimeException: can not run elasticsearch as root
    at org.elasticsearch.bootstrap.Bootstrap.initializeNatives(Bootstrap.java:106) ~[elasticsearch-5.6.2.jar:5.6.2]
    at org.elasticsearch.bootstrap.Bootstrap.setup(Bootstrap.java:195) ~[elasticsearch-5.6.2.jar:5.6.2]
    at org.elasticsearch.bootstrap.Bootstrap.init(Bootstrap.java:342) [elasticsearch-5.6.2.jar:5.6.2]
    at org.elasticsearch.bootstrap.Elasticsearch.init(Elasticsearch.java:132) [elasticsearch-5.6.2.jar:5.6.2]
    at org.elasticsearch.bootstrap.Elasticsearch.execute(Elasticsearch.java:123) [elasticsearch-5.6.2.jar:5.6.2]
    at org.elasticsearch.cli.EnvironmentAwareCommand.execute(EnvironmentAwareCommand.java:67) [elasticsearch-5.6.2.jar:5.6.2]
    at org.elasticsearch.cli.Command.mainWithoutErrorHandling(Command.java:134) [elasticsearch-5.6.2.jar:5.6.2]
    at org.elasticsearch.cli.Command.main(Command.java:90) [elasticsearch-5.6.2.jar:5.6.2]
    at org.elasticsearch.bootstrap.Elasticsearch.main(Elasticsearch.java:91) [elasticsearch-5.6.2.jar:5.6.2]
    at org.elasticsearch.bootstrap.Elasticsearch.main(Elasticsearch.java:84) [elasticsearch-5.6.2.jar:5.6.2]
复制代码

解决方案：

因为安全问题elasticsearch 不让用root用户直接运行，所以要创建新用户，用新用户启动

groupadd elsearch
useradd elsearch -g elsearch -p elasticsearch
坑三

由于sonar需要用新用户启动，所以sonar需要用到的所有资源必须属于新用户（包括jdk，坑3会讲到），不然会有权限问题

chown -R elsearch /sonarqube-6.6 //把sonar资源分配给用户elsearch
chgrp -R elsearch /sonarqube-6.6 //把sonar资源分配给组elsearch
chown -R elsearch /jdk1.8 //把jdk资源分配给用户elsearch
chgrp -R elsearch /jdk1.8 //把jdk资源分配给组elsearch
坑四

如果忘记以新用户启动，而是以root启动，elasticsearch会在/sonarqube-6.6/temp里会加载一些配置文件，如果这些文件初次加载则是属于root用户的，启动也会失败，报权限问题

所以记住一定要以新用户启动sonar

坑五

6.6不兼容低版本插件，例如sonar-web插件版本低于2.5则sonar启动不了。（插件位置/sonarqube-6.6/extensions/plugins）,必须要找到合适的插件版本

坑六

因为高版本sonar使用jdk1.8，如果在做sonar扫描的时候运行jdk不是1.8也会报jdk版本问题

Caused by: java.lang.UnsupportedClassVersionError: org/sonar/api/utils/SonarException : Unsupported major.minor version 52.0
所以不过用ant或者maven运行代码扫描的时候 必须要用jdk1.8