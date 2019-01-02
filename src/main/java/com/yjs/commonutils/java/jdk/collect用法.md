在Stream 的API中可以查到有两种collect方法，分别是：

第一种：

<R, A> R collect(Collector<? super T, A, R> collector);

第二种：

<R> R collect(Supplier<R> supplier, BiConsumer<R, ? super T> accumulator,
        BiConsumer<R, R> combiner);

对于第1个方法，我们主要是使用 Collectors（java.util.stream.Collectors）来进行各种 reduction 操作，下面举几个例子：

将数组组成字符串，注：Collectors.joining()有三个重载方法

String[] arr ={"aa","ccc","sss"};
System.out.println(Arrays.stream(arr).collect(joining()));
// aacccsss
System.out.println(Arrays.stream(arr).collect(joining("|")));
// aa|ccc|sss
System.out.println(Arrays.stream(arr).collect(joining(",","{","}")));
// {aa,ccc,sss}

将数组转为集合List

String[] arr ={"aa","ccc","sss"};
System.out.println(Arrays.stream(arr).collect(toList()));

// [aa, ccc, sss]

将list中的数据分组 并统计数量

public static class Person{
    private long id;
    private int age;
       private String name;

//   get/set
｝

List<Person> list= Lists.newArrayList();
//假装list中已有许多条数据=-=
Map<Integer, Long> personGroups = list.stream().
               collect(Collectors.groupingBy(Person::getAge,counting()));
// 这样我们就得到了一个 以年龄为key,以这个年龄的人数为value的map了

上面应该是几个比较常用的对第一个collect方法的使用了。下面着重写一下对第二collect个方法的使用过程。

对于函数

<R> R collect(Supplier<R> supplier,
                  BiConsumer<R, ? super T> accumulator,
                  BiConsumer<R, R> combiner);

来说，参数supplier 是一个生成目标类型实例的方法，代表着目标容器是什么；accumulator是将操作的目标数据填充到supplier 生成的目标类型实例中去的方法，代表着如何将元素添加到容器中；而combiner是将多个supplier 生成的实例整合到一起的方法，代表着规约操作，将多个结果合并。

如上面的保存有多个Person实例的list来说，假如我们需要将这个list做一个转变，变为以id为key,value为person的Map的话，那就可以使用这个方法了：

Map<Long,Person> personMap =list.stream().collect(Maps::newHashMap,                                                                      (map,p)>map.put(p.getId(),p),Map::putAll);                                      
1
注：lambda表达式的应用在Stream中很重要的。

上面的双冒号运算符所在的表达式转化一下就可以得到：

Map<Long,Person> personMap = list.stream().collect(() -> new HashMap<>(), 
                                 (map ,p) ->map.put(p.getId(),p),(m ,n) -> m.putAll(n));
--------------------- 
作者：第二庄 
来源：CSDN 
原文：https://blog.csdn.net/liujun03/article/details/80701999 
版权声明：本文为博主原创文章，转载请附上博文链接！