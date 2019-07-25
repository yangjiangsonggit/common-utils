# JMeter5性能测试

https://www.jianshu.com/p/38470194b98b

ab(Apache Benchmark)用起来非常方便， 但是也缺少很多必要的功能，特别是发送不同请求，以及对结果的校验。本文介绍一下如何使用JMeter5完成性能测试

最简单执行计划
创建计划
添加Thread Group
TestPlan -> Add -> Threads(Users) -> Thread Group

添加Http请求
Thread Group -> Add -> Sampler -> Http Request

查看Http回复
Thread Group -> Add -> Listener -> View Results Tree

查看统计信息
Thread Group -> Add -> Listener -> Aggregate Report

配置计划
Thread Group： 配置线程数和循环次数


Thread Group配置
Http Request：配置请求参数：如URL，Body等


Http Request配置
执行结果
测试结果
高级功能
读取文件
上面的测试，每次发送的URL请求都是同一个，可能因为缓存等原因导致性能数据偏差。 可以使用读取CSV文件的方式，对每个请求构造不同的请求。

添加CSV参数文件
添加CSV文件
在请求中使用占位符${}
配置参数替换
解析CSV参数文件


参数读取规则
配置完成后，可以在一次执行计划中根据CSV文件中配置的参数，构造不同的请求

NoGui
不要使用GUI界面进行性能测试
不要使用GUI界面进行性能测试
不要使用GUI界面进行性能测试

GUI界面是为了方便进行配置，以及查看、分析性能测试结果。如果要执行性能测试，需要使用命令行模式，如下：

./jmeter -n -t ~/process.jmx -l result.jtl
-n: No Gui模式
-t: 指定配置文件
-l: 指定测试结果文件
性能测试结果
在No Gui模式下生成的性能测试结果result.jtl，可以在Summary Report中打开，如下图：
分析性能测试结果
注意事项
加载结果文件时，要清空之前的结果，否则数据会出现错乱
加载结果Response时，可能出现中文乱码
修复办法，在文件apache-jmeter-5.0\bin\jmeter.propertis设置sampleresult.default.encoding=UTF-8

参考
How to Save Response Data in JMeter
jmeter中response data 乱码
Jmeter CSV Data Set Config参数化