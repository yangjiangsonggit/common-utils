quartz是一个作业调度框架，用于指定工作（作业）在指定时间执行——定时工作。

quartz的核心接口有：

　　Scheduler接口：Scheduler是job的执行对象，用于工作的执行。

　　Job接口：用于指定作业的类，自己写的“定时程序”需要实现此接口的void execute(JobExecutionContext arg0)方法。

　　Trigger抽象类：指定工作在什么时候执行。调度类(Scheduler)在指定时间调用此类，再由trigger类调用指定的定时程序。　　　　

　　JobDetail类：持有某个定时工作的详细描述，包括Name,Group,JobDataMap等。
　　JobExecutionContext类：定时程序执行的run-time的上下文环境，用于得到当前执行的Job的名字，配置的参数等。
　　JobDataMap类：用于描述一个作业的参数，参数可以为任何基本类型例，也可为某个对象的引用.
　　JobListener,TriggerListener接口：用于监听触发器状态和作业扫行状态，在特写状态执行相应操作。
　　JobStore类：在哪里执行定进程序，可选的有在内存中，在数据库中。

Scheduler接口：

　　通过StdSchedulerFactory或DirectSchedulerFactory的getDefaultScheduler获取Schedule对象。

　　　　1、DirectSchedulerFactory是能对Scheduler绝对控制的。

DirectSchedulerFactory factory=DirectSchedulerFactory.getInstance();//获取DirectSchedulerFactory对象

factory.createVolatileScheduler(10);//通过调用createXXXX方法初始化Scheduler对象，但是该方法不返回Scheduler对象，createXXXX方法 用于设置Scheduler的属性

Scheduler scheduler = factory.getScheduler();//获取Scheduler对象，如果不调用createXXXX方法会抛出SchedulerException异常
　　　　2、STDSchedulerFactory类产生Scheduler实例。

　　　　　　stdSchedulerFactory有三种方式向StdScheduler提供属性以供Scheduler使用：通过 java.util.Properties 实例提供、通过外部属性文件提供、通过含有

　　　　　　属性文件内容的 java.io.InputStream 实例提供

　　　　　　　　1、通过java.util.properties提供　　　　　　 

StdSchedulerFactory factory = new StdSchedulerFactory();
Properties props = new Properties(); 
props.put(StdSchedulerFactory.PROP_THREAD_POOL_CLASS, "org.quartz.simpl.SimpleThreadPool");//实现org.quartz.spi.ThreadPool的类名
props.put("org.quartz.threadPool.threadCount", "10"); //线程数量
factory.initialize(props);   //初始化
Scheduler scheduler = factory.getScheduler();
　　　　　　　可以通过public void initialize(String filename) throws SchedulerException方法来加载初始化Scheduler的属性

　　　　　　2、通过public void initialize(InputStream propertiesStream) throws SchedulerException方法读取流的形式加载属性

　　　　　　如果没有为initialize方法指定加载文件或流，那么StdSchedulerFactory会默认的从quartz.properties文件去加载

　　　　　   3、可以用StdSchedulerFactory.getDefaultScheduler()方法来创建一个Scheduler实例

　　Scheduler通过调用start方法启动工作的执行。

　　Scheduler通过scheduleJob（JobDetail，Trigger）方法向Scheduler注册工作，JobDetail持有执行job的信息，trigger指的是什么时候触发工作。

job接口：

　　job是执行工作的Java类，要想指定自己的工作，必须实现job接口，在方法execute（JobExecutionContext arg0）中进行工作操作。

JobDetail类：

　　jobDerail持有job的相关信息，通过构造器进行实例：public JobDetail(String name, String group, Class jobClass)，通过向其中传递工作名、工作组、工作类的class实例jobdetail对象。

Trigger接口：

　　trigger接口的实现类 org.quartz.SimpleTrigger 、org.quartz.CronTrigger 和 org.quartz.NthIncludedDayTrigger

　　SimpleTrigger主要是针对一些相对简单的时间触发进行配置使用，比如在指定的时间开始然后在一定的时间间隔之内重复执行一个Job

　　CronTrigger可以配置更复杂的触发时刻表，比SimpleTrigger功能上跟加强大，主要在能配置跟加复杂的执行计划的时间

　　NthIncludedDayTrigger

　一个trigger只能对应一个job，一个job能对应多个trigger。trigger是job的触发器，指定在什么情况下执行定时计划。

JobDataMap类：

　　jobDataMap类就像它的名字说的那样，用于存储job的数据，以map的形式进行存储。jobDataMap可以向job对象传递任何数据类型的信息

JobStore接口：

　　JobStore的实现类，RAMJobStore和JDBCJobStore

　　RAMJobStore中对job操作的方法有：

　　　　public void storeJob(SchedulingContext ctxt, JobDetail newJob,boolean replaceExisting)，用于job存储进内存中去

　　　　public boolean removeJob(SchedulingContext ctxt, String jobName,String groupName);移除job

　　　　public JobDetail retrieveJob(SchedulingContext ctxt, String jobName,String groupName)；获取job的jobDerail，
　　对trigger的操作类似。