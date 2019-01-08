CountDownLatch使用场景及分析
　　JDk1.5提供了一个非常有用的包，Concurrent包，这个包主要用来操作一些并发操作，提供一些并发类，可以方便在项目当中傻瓜式应用。

　　JDK1.5以前，使用并发操作，都是通过Thread，Runnable来操作多线程；但是在JDK1.5之后，提供了非常方便的线程池（ThreadExecutorPool），主要代码由大牛Doug Lea完成，其实是在jdk1.4时代，由于java语言内置对多线程编程的支持比较基础和有限，所以他写了这个，因为实在太过于优秀，所以被加入到jdk之中；

　　这次主要对CountDownLatch进行系统的讲解

　　使用场景：比如对于马拉松比赛，进行排名计算，参赛者的排名，肯定是跑完比赛之后，进行计算得出的，翻译成Java识别的预发，就是N个线程执行操作，主线程等到N个子线程执行完毕之后，在继续往下执行。

      代码示例

     

复制代码
 1 public static void testCountDownLatch(){
 2         
 3         int threadCount = 10;
 4         
 5         final CountDownLatch latch = new CountDownLatch(threadCount);
 6         
 7         for(int i=0; i< threadCount; i++){
 8              
 9             new Thread(new Runnable() {
10                 
11                 @Override
12                 public void run() {
13 
14                     System.out.println("线程" + Thread.currentThread().getId() + "开始出发");
15 
16                     try {
17                         Thread.sleep(1000);
18                     } catch (InterruptedException e) {
19                         e.printStackTrace();
20                     }
21 
22                     System.out.println("线程" + Thread.currentThread().getId() + "已到达终点");
23 
24                     latch.countDown();
25                 }
26             }).start();
27         }
28         
29         try {
30             latch.await();
31         } catch (InterruptedException e) {
32             e.printStackTrace();
33         }
34 
35         System.out.println("10个线程已经执行完毕！开始计算排名");
36     }
复制代码
　　执行结果：

复制代码
线程10开始出发
线程13开始出发
线程12开始出发
线程11开始出发
线程14开始出发
线程15开始出发
线程16开始出发
线程17开始出发
线程18开始出发
线程19开始出发
线程14已到达终点
线程15已到达终点
线程13已到达终点
线程12已到达终点
线程10已到达终点
线程11已到达终点
线程16已到达终点
线程17已到达终点
线程18已到达终点
线程19已到达终点
10个线程已经执行完毕！开始计算排名
复制代码
　　

　　源码分析：

      　　1、CountDownLatch:A synchronization aid that allows one or more threads to wait until a set of operations being performed in other threads completes.

　　　　大致意思：也就是说主线程在等待所有其它的子线程完成后再往下执行

　　　　

　　　　2、构造函数：CountDownLatch(int count)//初始化count数目的同步计数器，只有当同步计数器为0，主线程才会向下执行
　　　　　 主要方法：void	await()//当前线程等待计数器为0 
      　　　　　　　　 boolean	await(long timeout, TimeUnit unit)//与上面的方法不同，它加了一个时间限制。
     　　　　　　　　 void	countDown()//计数器减1
      　　　　　　　　long	getCount()//获取计数器的值

      　　3.它的内部有一个辅助的内部类：sync.

　　　　   它的实现如下：

　　　　　

/**
     * Synchronization control For CountDownLatch.
     * Uses AQS state to represent count.
     */
    private static final class Sync extends AbstractQueuedSynchronizer {
        private static final long serialVersionUID = 4982264981922014374L;
 
        Sync(int count) {
            setState(count);
        }
 
        int getCount() {
            return getState();
        }
 
        protected int tryAcquireShared(int acquires) {
            return (getState() == 0) ? 1 : -1;
        }
 
        protected boolean tryReleaseShared(int releases) {
            // Decrement count; signal when transition to zero
            for (;;) {
                int c = getState();
                if (c == 0)
                    return false;
                int nextc = c-1;
                if (compareAndSetState(c, nextc))
                    return nextc == 0;
            }
        }
    }
　　4.await()方法的实现
  　　sync.acquireSharedInterruptibly(1);
     -->if (tryAcquireShared(arg) < 0)//调用3中的tryAcquireShared()方法
            doAcquireSharedInterruptibly(arg);//加入到等待队列中

　　5.countDown（）方法的实现
  　　sync.releaseShared(1);
     --> if (tryReleaseShared(arg))//调用3中的tryReleaseShared（）方法
               doReleaseShared();//解锁

　　　　　　

参考文章：

           https://my.oschina.net/u/1185331/blog/502350

           http://blog.itpub.net/30024515/viewspace-1432825/