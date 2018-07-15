#java推荐规范                                                                                                                                 

 ##1.内部类的定义原则

    当一个类与另一个类关联非常紧密，处于从属的关系，特别是只有该类会访问它时，可定义成私有内部类以提高封装性。
    另外，内部类也常用作回调函数类，在JDK8下建议写成Lambda。
    内部类分匿名内部类，内部类，静态内部类三种。

    1) 匿名内部类 与 内部类，按需使用：

    在性能上没有区别；当内部类会被多个地方调用，或匿名内部类的长度太长，已影响对调用它的方法的阅读时，定义有名字的内部类。

    2) 静态内部类 与 内部类，优先使用静态内部类：

    非静态内部类持有外部类的引用，能访问外类的实例方法与属性。构造时多传入一个引用对性能没有太大影响，更关键的是向阅读者传递自己的意图，内部类会否访问外部类。
    非静态内部类里不能定义static的属性与方法。

 ##2.final关键字与性能无关，仅用于下列不可修改的场景
    
    1） 定义类及方法时，类不可继承，方法不可覆写；
    
    2） 定义基本类型的函数参数和变量，不可重新赋值；
    
    3） 定义对象型的函数参数和变量，仅表示变量所指向的对象不可修改，而对象自身的属性是可以修改的。

 ##3.少用if-else方式，多用哨兵语句式以减少嵌套层次
    
    if (condition) {
      ...
      return obj;
    }
    // 接着写else的业务逻辑代码;

 ##4.方法的语句在同一个抽象层级上
    
    反例：一个方法里，前20行代码在进行很复杂的基本价格计算，然后调用一个折扣计算函数，再调用一个赠品计算函数。
    
    此时可将前20行也封装成一个价格计算函数，使整个方法在同一抽象层级上。
    
 ##5.switch的规则
    
    1）在一个switch块内，每个case要么通过break/return等来终止，要么注释说明程序将继续执行到哪一个case为止；
    
    2）在一个switch块内，都必须包含一个default语句并且放在最后，即使它什么代码也没有。
    
    String animal = "tomcat";
    
    switch (animal) {
    case "cat":
      System.out.println("It's a cat.");
      break;
    case "lion": // 执行到tiger
    case "tiger":
      System.out.println("It's a beast.");
      break;
    default: 
      // 什么都不做，也要有default
      break;
    }
  
 ##6. 原子数据类型(int等)与包装类型(Integer等)的使用原则
     
     1.1 【推荐】需要序列化的POJO类属性使用包装数据类型
     
     1.2 【推荐】RPC方法的返回值和参数使用包装数据类型
     
     1.3 【推荐】局部变量尽量使用基本数据类型
     
     包装类型的坏处:
     
     1）Integer 24字节，而原子类型 int 4字节。
     
     2）包装类型每次赋予还需要额外创建对象，除非在缓存区(见Integer.IntegerCache与Long.LongCache)，Integer var = ?在缓存区间的赋值，会复用h缓存对象。默认缓存区间为-127到128，受启动参数的影响，如-XX:AutoBoxCacheMax=20000。
     
     3）包装类型还有==比较的陷阱（见规则3）
     
     包装类型的好处:
     
     1）包装类型能表达Null的语义。
     
     比如数据库的查询结果可能是null，如果用基本数据类型有NPE风险。又比如显示成交总额涨跌情况，如果调用的RPC服务不成功时，应该返回null，显示成-%，而不是0%。
     
     2）集合需要包装类型，除非使用数组，或者特殊的原子类型集合。
     
     3）泛型需要包装类型，如Result<Integer>。
     
 ##7.原子数据类型与包装类型的转换原则
    
    2.1【推荐】自动转换(AutoBoxing)有一定成本，调用者与被调用函数间尽量使用同一类型，减少默认转换
    
    //WRONG, sum 类型为Long， i类型为long，每次相加都需要AutoBoxing。
    Long sum=0L;
    
    for( long i = 0; i < 10000; i++) {
      sum+=i;
    }
    
    //RIGHT, 准确使用API返回正确的类型
    Integer i = Integer.valueOf(str);
    int i = Integer.parseInt(str);

 ##8.自动拆箱有可能产生NPE，要注意处理
    
    //如果intObject为null，产生NPE
    int i = intObject;
    
 ##9. 数值equals比较的原则
    
    3.1【强制】 所有包装类对象之间值的比较，全部使用equals方法比较
    
    \==判断对象是否同一个。Integer var = ?在缓存区间的赋值（见规则1），会复用已有对象，因此这个区间内的Integer使用 \==进行判断可通过，但是区间之外的所有数据，则会在堆上新产生，不会通过。因此如果用\== 来比较数值，很可能在小的测试数据中通过，而到了生产环境才出问题。
    
    3.2【强制】 BigDecimal需要使用compareTo()
    
    因为BigDecimal的equals()还会比对精度，2.0与2.00不一致。
    
    Facebook-Contrib: Correctness - Method calls BigDecimal.equals()
    3.3【强制】 Atomic* 系列，不能使用equals方法
    
    因为 Atomic* 系列没有覆写equals方法。
    
    //RIGHT
    if (counter1.get() == counter2.get()){...}
    Sonar-2204: ".equals()" should not be used to test the values of "Atomic" classes
    3.4【强制】 double及float的比较，要特殊处理
    
    因为精度问题，浮点数间的equals非常不可靠，在vjkit的NumberUtil中有对应的封装函数。
    
    float f1 = 0.15f;
    float f2 = 0.45f/3; //实际等于0.14999999
    
    //WRONG
    if (f1 == f2) {...}
    if (Double.compare(f1,f2)==0) 
    
    //RIGHT
    static final float EPSILON = 0.00001f;
    if (Math.abs(f1-f2)<EPSILON) {...}
    
 ##10.字符串拼接对象时，不要显式调用对象的toString()
     
     如上，+实际是StringBuilder，本身会调用对象的toString()，且能很好的处理null的情况。
     
     //WRONG
     str = "result:" + myObject.toString();  // myObject为Null时，抛NPE
     
     //RIGHT
     str = "result:" + myObject;  // myObject为Null时，输出 result:null
     
 ##11.集合如果存在并发修改的场景，需要使用线程安全的版本
     
     1) 著名的反例，HashMap扩容时，遇到并发修改可能造成100%CPU占用。
     
     推荐使用java.util.concurrent(JUC)工具包中的并发版集合，如ConcurrentHashMap等，优于使用Collections.synchronizedXXX()系列函数进行同步化封装(等价于在每个方法都加上synchronized关键字)。
     
     例外：ArrayList所对应的CopyOnWriteArrayList，每次更新时都会复制整个数组，只适合于读多写很少的场景。如果频繁写入，可能退化为使用Collections.synchronizedList(list)。
     
     2) 即使线程安全类仍然要注意函数的正确使用。
     
     例如：即使用了ConcurrentHashMap，但直接是用get/put方法，仍然可能会多线程间互相覆盖。
     
     //WRONG
     E e = map.get(key);
     if (e == null) {
       e = new E();
       map.put(key, e); //仍然能两条线程并发执行put，互相覆盖
     }
     return e;
     
     //RIGHT 
     E e = map.get(key);
     if (e == null) {
       e = new E();
       E previous = map.putIfAbsent(key, e);
       if(previous != null) {
         return previous;
       }
     }
     return e;
     
 ##12.如果Key只有有限的可选值，先将Key封装成Enum，并使用EnumMap
     
     EnumMap，以Enum为Key的Map，内部存储结构为Object[enum.size]，访问时以value = Object[enum.ordinal()]获取值，同时具备HashMap的清晰结构与数组的性能。
     
     public enum COLOR {
       RED, GREEN, BLUE, ORANGE;
     }
     
     EnumMap<COLOR, String> moodMap = new EnumMap<COLOR, String> (COLOR.class);
     
 ##13.创建线程或线程池时请指定有意义的线程名称，方便出错时回溯
      
      1）创建单条线程时直接指定线程名称
      
      Thread t = new Thread();
      t.setName("cleanup-thread");
      2） 线程池则使用guava或自行封装的ThreadFactory，指定命名规则。
      
      //guava 或自行封装的ThreadFactory
      ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat(threadNamePrefix + "-%d").build();
      
      ThreadPoolExecutor executor = new ThreadPoolExecutor(..., threadFactory, ...);
      
 ##14.线程池不允许使用 Executors去创建，避资源耗尽风险
       
       Executors返回的线程池对象的弊端 ：
       
       1）FixedThreadPool 和 SingleThreadPool:
       
       允许的请求队列长度为 Integer.MAX_VALUE，可能会堆积大量的请求，从而导致 OOM。
       
       2）CachedThreadPool 和 ScheduledThreadPool:
       
       允许的创建线程数量为 Integer.MAX_VALUE，可能会创建大量的线程，从而导致 OOM。
       
       应通过 new ThreadPoolExecutor(xxx,xxx,xxx,xxx)这样的方式，更加明确线程池的运行规则，合理设置Queue及线程池的core size和max size，建议使用vjkit封装的ThreadPoolBuilder。
       
 ##15.正确停止线程
     
     Thread.stop()不推荐使用，强行的退出太不安全，会导致逻辑不完整，操作不原子，已被定义成Deprecate方法。
     
     停止单条线程，执行Thread.interrupt()。
     
     停止线程池：
     
     ExecutorService.shutdown(): 不允许提交新任务，等待当前任务及队列中的任务全部执行完毕后退出；
     
     ExecutorService.shutdownNow(): 通过Thread.interrupt()试图停止所有正在执行的线程，并不再处理还在队列中等待的任务。
     
     最优雅的退出方式是先执行shutdown()，再执行shutdownNow()，vjkit的ThreadPoolUtil进行了封装。
     
     注意，Thread.interrupt()并不保证能中断正在运行的线程，需编写可中断退出的Runnable，见规则5。
     
 ##16.编写可停止的Runnable
      
      执行Thread.interrupt()时，如果线程处于sleep(), wait(), join(), lock.lockInterruptibly()等blocking状态，会抛出InterruptedException，如果线程未处于上述状态，则将线程状态设为interrupted。
      
      因此，如下的代码无法中断线程:
      
      public void run() {
      
        while (true) { //WRONG，无判断线程状态。
          sleep();
        }
      
        public void sleep() {
          try {
            Thread.sleep(1000);
          } catch (InterruptedException e) {
            logger.warn("Interrupted!", e); //WRONG，吃掉了异常，interrupt状态未再传递
          }
        }
      }
      5.1 正确处理InterruptException
      
      因为InterruptException异常是个必须处理的Checked Exception，所以run()所调用的子函数很容易吃掉异常并简单的处理成打印日志，但这等于停止了中断的传递，外层函数将收不到中断请求，继续原有循环或进入下一个堵塞。
      
      正确处理是调用Thread.currentThread().interrupt(); 将中断往外传递。
      
      //RIGHT
      public void myMethod() {
        try {
          ...
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      }
      Sonar-2142: "InterruptedException" should not be ignored
      5.2 主循环及进入阻塞状态前要判断线程状态
      
      //RIGHT
      public void run() {
        try {
          while (!Thread.isInterrupted()) {
            // do stuff
          }
        } catch (InterruptedException e) {
          logger.warn("Interrupted!", e);
        }
      }
      其他如Thread.sleep()的代码，在正式sleep前也会判断线程状态。
      
 ##17.Runnable中必须捕获一切异常
      
      如果Runnable中没有捕获RuntimeException而向外抛出，会发生下列情况：
      
      1) ScheduledExecutorService执行定时任务，任务会被中断，该任务将不再定时调度，但线程池里的线程还能用于其他任务。
      
      2) ExecutorService执行任务，当前线程会中断，线程池需要创建新的线程来响应后续任务。
      
      3) 如果没有在ThreadFactory设置自定义的UncaughtExceptionHanlder，则异常最终只打印在System.err，而不会打印在项目的日志中。
      
      因此建议自写的Runnable都要保证捕获异常; 如果是第三方的Runnable，可以将其再包裹一层vjkit中的SafeRunnable。
      
      executor.execute(ThreadPoolUtil.safeRunner(runner));
      
   ###17.1自定义异常处理器Thread.UncaughtExceptionHandler
  
          1.实现Thread.UncaughtExceptionHandler接口
          
          2.某个线程再调用setUncaughtExceptionHandler（）
          
          思考:与 try catch 之间的区别？？？
          
          // 定义自己的异常处理器
          //Thread.UncaughtExceptionHandler接口
          //setUncaughtExceptionHandler
          public class EXHandler
          {
              public static void main(String[] args)
              {
                  EXHandler uncaught=new EXHandler();
                  // 设置主线程的异常处理器
                  Thread.currentThread().setUncaughtExceptionHandler(uncaught.new MyHandler());
                  int a = 5 / 0;     // ①
                  System.out.println("程序正常结束！");
              }
          
              //thread 类实现Thread.UncaughtExceptionHandler接口自定义异常处理器
              class MyHandler implements Thread.UncaughtExceptionHandler
              {
                  // 实现uncaughtException方法，该方法将处理线程的未处理异常
                  public void uncaughtException(Thread t, Throwable e)
                  {
                      System.out.println(t + " 线程出现了异常：" + e);
                  }
              }
          }
          //自定义的异常处理器和 try ..catch的区别？
          //使用catch捕获异常时，异常不会向上传递给上一级调用者；
          // 使用uncaughtExceptionhandler会对于异常处理之后返回给上一级调用者,导致程序没有正常结束
          
          //如：try catch 能将最后的“程序正常结束输出”
          /*
                  try {
                      int a = 5 / 0;     // ①
                  }catch(Exception e){
                      System.out.println(e.toString());
                  }
                  System.out.println("程序正常结束！");
           */
          /*
          字节流改为字符串 获取异常信息
          ByteArrayOutputStream baos = new ByteArrayOutputStream();
          e.printStackTrace(new PrintStream(baos));
          String exception = baos.toString();
          System.out.println("baos:" + exception);
           */

  
  ##18.异常日志应包含排查问题的足够信息
       
       异常信息应包含排查问题时足够的上下文信息。
       
       捕获异常并记录异常日志的地方，同样需要记录没有包含在异常信息中，而排查问题需要的信息，比如捕获处的上下文信息。
       
       //WRONG
       new TimeoutException("timeout");
       logger.error(e.getMessage(), e);
       
       
       //RIGHT
       new TimeoutException("timeout:" + eclapsedTime + ", configuration:" + configTime);
       logger.error("user[" + userId + "] expired:" + e.getMessage(), e);
       
 ##19.如果不想处理异常，可以不进行捕获。但最外层的业务使用者，必须处理异常，将其转化为用户可以理解的内容
      
      finally块的处理原则
      
      8.1 【强制】必须对资源对象、流对象进行关闭，或使用语法try-with-resource
      
      关闭动作必需放在finally块，不能放在try块 或 catch块，这是经典的错误。
      
      更加推荐直接使用JDK7的try-with-resource语法自动关闭Closeable的资源，无需在finally块处理，避免潜在问题。
      
      try (Writer writer = ...) {
        writer.append(content);
      }
      8.2 【强制】如果处理过程中有抛出异常的可能，也要做try-catch，否则finally块中抛出的异常，将代替try块中抛出的异常
      
      //WRONG
      try {
        ...
        throw new TimeoutException();
      } finally {
        file.close();//如果file.close()抛出IOException, 将代替TimeoutException
      }
      
      //RIGHT, 在finally块中try－catch
      try {
        ...
        throw new TimeoutException();
      } finally {
        IOUtil.closeQuietly(file); //该方法中对所有异常进行了捕获
      }
     
      8.3 【强制】不能在finally块中使用return，finally块中的return将代替try块中的return及throw Exception
      
      //WRONG
      try {
        ...
        return 1;
      } finally {
        return 2; //实际return 2 而不是1
      }
      
      try {
        ...
        throw TimeoutException();
      } finally {
        return 2; //实际return 2 而不是TimeoutException
      }
  ###19.1 JDK7及其之后的资源关闭方式
        3.1 try-with-resource语法
        确实，在JDK7以前，Java没有自动关闭外部资源的语法特性，直到JDK7中新增了try-with-resource语法，才实现了这一功能。
        
        那什么是try-with-resource呢？简而言之，当一个外部资源的句柄对象（比如FileInputStream对象）实现了AutoCloseable接口，那么就可以将上面的板式代码简化为如下形式：
        
        复制代码
        public static void main(String[] args) {
            try (FileInputStream inputStream = new FileInputStream(new File("test"))) {
                System.out.println(inputStream.read());
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
        复制代码
        将外部资源的句柄对象的创建放在try关键字后面的括号中，当这个try-catch代码块执行完毕后，Java会确保外部资源的close方法被调用。代码是不是瞬间简洁许多！
        
 ##20.尽量使用异步日志
       
       低延时的应用，使用异步输出的形式(以AsyncAppender串接真正的Appender)，可减少IO造成的停顿。
       
       需要正确配置异步队列长度及队列满的行为，是丢弃还是等待可用，业务上允许丢弃的尽量选丢弃。
       
 ##21.使用warn级别而不是error级别，记录外部输入参数错误的情况
      
      如非必要，请不在此场景打印error级别日志，避免频繁报警。
      
      error级别只记录系统逻辑出错、异常或重要的错误信息。
      