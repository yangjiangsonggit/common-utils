Drools将对于DRL将动态生成Class。 若需要线上持续集成，切记开启以下jvm参数，开启PermGen GC和ClassUnloading:
    -XX:+UseConcMarkSweepGC
    -XX:+CMSClassUnloadingEnabled
    -XX:+CMSPermGenSweepingEnabled
    -XX:CMSScavengeBeforRemark  //在重新标记之前，年轻代完成了垃圾回收，这个时间将会大大缩短 ,在每次重新标注之前，强制年轻代的垃圾回收
    -XX:+CMSConcurrentMTEnabled  在并发阶段，可以利用多核
	-XX:+ConcGCThreads 指定线程数量
	-XX:+ParallelGCThreads 指定在stop-the-world过程中，垃圾回收的线程数，默认是cpu的个数
	-XX:+UseParNewGC 年轻代采用并行的垃圾回收器
	-XX:PermSize=512m
	-XX:+UseParNewGC   //Enables the use of parallel threads for collection in the young generation. By default, this option is disabled. It is automatically enabled when you set the -XX:+UseConcMarkSweepGC option. Using the -XX:+UseParNewGC option without the -XX:+UseConcMarkSweepGC option was deprecated in JDK 8.

    调试可选
    -XX:+TraceClassUnloading
    
    
// 线上机器使用的JVM 参数     for cloud machine
JAVA_OPTS="-server -XX:+UseConcMarkSweepGC -Xmx16g  -Xms16g -Xmn8g -XX:SurvivorRatio=20  -XX:+UseCMSCompactAtFullCollection -XX:+CMSParallelRemarkEnabled -XX:CMSInitiatingOccupancyFraction=70 -XX:+UseCMSInitiatingOccupancyOnly -XX:+CMSClassUnloadingEnabled -XX:+CMSPermGenSweepingEnabled -XX:MaxPermSize=2048m -XX:+AggressiveOpts -XX:TLABSize=64K -XX:ParallelGCThreads=8 -XX:ConcGCThreads=8 -Dfile.encoding=UTF-8"


// for physical machine
JAVA_OPTS="-server -XX:+UseConcMarkSweepGC -Xmx32g  -Xms32g -Xmn12g -XX:SurvivorRatio=30  -XX:+UseCMSCompactAtFullCollection -XX:+CMSParallelRemarkEnabled -XX:CMSInitiatingOccupancyFraction=70 -XX:+UseCMSInitiatingOccupancyOnly -XX:+CMSClassUnloadingEnabled -XX:+CMSPermGenSweepingEnabled -XX:MaxPermSize=3072m -XX:+AggressiveOpts -XX:TLABSize=128K -XX:ParallelGCThreads=14 -XX:ConcGCThreads=14 -Dfile.encoding=UTF-8"