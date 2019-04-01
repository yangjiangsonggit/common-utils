
##java8
    1.Lambda表达式
        Lambda允许把函数作为一个方法的参数，或者把代码看成数据
    2.接口的默认方法与静态方法
        public interface DefaultFunctionInterface {
             default String defaultFunction() {
                 return "default function";
             }
        }
         
         public interface StaticFunctionInterface {
              static String staticFunction() {
                  return "static function";
              }
        }
    3.方法引用（含构造方法引用）
        通常与Lambda表达式联合使用，可以直接引用已有Java类或对象的方法。一般有四种不同的方法引用：
        
        构造器引用。语法是Class::new，或者更一般的Class< T >::new，要求构造器方法是没有参数；
        静态方法引用。语法是Class::static_method，要求接受一个Class类型的参数；
        特定类的任意对象方法引用。它的语法是Class::method。要求方法是没有参数的；
        特定对象的方法引用，它的语法是instance::method。要求方法接受一个参数，与3不同的地方在于，
        3是在列表元素上分别调用方法，而4是在某个对象上调用方法，将列表元素作为参数传入；
        
    4.重复注解
    5.扩展注解的支持（类型注解）
        private @NotNull String name;
    6.Optional
        Java 8引入Optional类来防止空指针异常，Optional类最先是由Google的Guava项目引入的。Optional类实际上是个容器：
        它可以保存类型T的值，或者保存null。使用Optional类我们就不用显式进行空指针检查了。
        
    7.Stream
        Stream API是把真正的函数式编程风格引入到Java中。其实简单来说可以把Stream理解为MapReduce，当然Google的MapReduce的灵感
        也是来自函数式编程。她其实是一连串支持连续、并行聚集操作的元素。从语法上看，也很像linux的管道、或者链式编程，代码写起来简洁
        明了，非常酷帅！
        
    8.Date/Time API (JSR 310)
      Java 8新的Date-Time API (JSR 310)受Joda-Time的影响，提供了新的java.time包，可以用来替代 java.util.Date和
      java.util.Calendar。一般会用到Clock、LocaleDate、LocalTime、LocaleDateTime、ZonedDateTime、Duration这些类，
      对于时间日期的改进还是非常不错的。

    9.Base64
        在Java 8中，Base64编码成为了Java类库的标准。Base64类同时还提供了对URL、MIME友好的编码器与解码器。
        
        
