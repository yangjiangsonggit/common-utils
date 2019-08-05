在Java中，泛型与反射是两个重要的概念，我们几乎能够经常的使用到它们。而谈起Type，如果有人还比较陌生的话 ，那么说起一个它的直接实现类——Class的话，大家都应该明白了。Type是Java语言中所有类型的公共父接口。而这篇文章，主要是讲述了Type的其它四个子类——ParameterizedType、 TypeVariable、GenericArrayType、WildcardType。对想了解这几个类的朋友一个参考

《Java中的Type类型详解》

ParameterizedType：参数化类型

参数化类型即我们通常所说的泛型类型，一提到参数，最熟悉的就是定义方法时有形参，然后调用此方法时传递实参。那么参数化类型怎么理解呢？顾名思义，就是将类型由原来的具体的类型参数化，类似于方法中的变量参数，此时类型也定义成参数形式（可以称之为类型形参），然后在使用/调用时传入具体的类型（类型实参）。那么我们的ParameterizedType就是这样一个类型，下面我们来看看它的三个重要的方法：

getRawType(): Type

该方法的作用是返回当前的ParameterizedType的类型。如一个List，返回的是List的Type，即返回当前参数化类型本身的Type。

getOwnerType(): Type

返回ParameterizedType类型所在的类的Type。如Map.Entry<String, Object>这个参数化类型返回的事Map(因为Map.Entry这个类型所在的类是Map)的类型。

getActualTypeArguments(): Type[]

该方法返回参数化类型<>中的实际参数类型， 如 Map<String,Person> map 这个 ParameterizedType 返回的是 String 类,Person 类的全限定类名的 Type Array。注意: 该方法只返回最外层的<>中的类型，无论该<>内有多少个<>。

下面让我们用一段例子来看一下具体的用法：

  //是ParameterizedType
       private HashMap<String, Object> map;
       private HashSet<String> set;
       private List<String> list;
       private Class<?> clz;
      
      //不是ParameterizedType
       private Integer i;
       private String str;
  
      private static void printParameterizedType(){
      	Field[] fields = TestParameterizedTypeBean.class.getDeclaredFields();
      	for (Field f : fields){
            //打印是否是ParameterizedType类型
              System.out.println("FieldName: " + f.getName() + " instanceof ParameterizedType is : " + 
       		   (f.getGenericType() instanceof ParameterizedType));
      }
        //取map这个类型中的实际参数类型的数组
      	getParameterizedTypeWithName("map");
      	getParameterizedTypeWithName("str");
      }
  
        private static void getParameterizedTypeWithName(String name){
          Field f;
            try {
              //利用反射得到TestParameterizedTypeBean类中的所有变量
                f = TestParameterizedTypeBean.class.getDeclaredField(name);
                f.setAccessible(true);
                Type type = f.getGenericType();
                if (type instanceof ParameterizedType){
                  for(Type param : ((ParameterizedType)type).getActualTypeArguments()){
                    //打印实际参数类型
                    System.out.println("---type actualType---" + param.toString());
                  }
                  //打印所在的父类的类型
                  System.out.println("---type ownerType0---"+ ((ParameterizedType)	 					type).getOwnerType());
                  //打印其本身的类型
                  System.out.println("---type rawType---"+ ((ParameterizedType) 							type).getRawType());
          	     }
        		   } catch (NoSuchFieldException e) {
      			    e.printStackTrace();
        		   }
      }
复制代码
上面的代码主要是定义了一些变量，这些变量中间有ParameterizedType也有普通类型变量，我们来看一下上述代码的输出：

《Java中的Type类型详解》

TypeVariable：类型变量

范型信息在编译时会被转换为一个特定的类型, 而TypeVariable就是用来反映在JVM编译该泛型前的信息。(通俗的来说，TypeVariable就是我们常用的T，K这种泛型变量)

getBounds(): Type[]:

返回当前类型的上边界，如果没有指定上边界，则默认为Object。

getName(): String:

返回当前类型的类名

getGenericDeclaration(): D

返回当前类型所在的类的Type。

下面通过一个例子来加深了解：

 public class TestTypeVariableBean<K extends Number, T> {
      
        //K有指定了上边界Number
        K key;
        //T没有指定上边界，其默认上边界为Object
        T value;
        
        public static void main(String[] args){
      		Type[] types = TestTypeVariableBean.class.getTypeParameters();
      		for (Type type : types){
                TypeVariable t = (TypeVariable) type;
                int index = t.getBounds().length - 1;
                //输出上边界
                System.out.println("--getBounds()-- " + t.getBounds()[index]);
                //输出名称
                System.out.println("--getName()--" + t.getName());
                //输出所在的类的类型
                System.out.println("--getGenericDeclaration()--" + 														t.getGenericDeclaration());
      		}
        }
      }
复制代码
再来看下输出：

《Java中的Type类型详解》

GenericArrayType：泛型数组类型：

组成数组的元素中有泛型则实现了该接口; 它的组成元素是 ParameterizedType 或 TypeVariable 类型。(通俗来说，就是由参数类型组成的数组。如果仅仅是参数化类型，则不能称为泛型数组，而是参数化类型)。注意：无论从左向右有几个[]并列，这个方法仅仅脱去最右边的[]之后剩下的内容就作为这个方法的返回值。

getGenericComponentType(): Type:
返回组成泛型数组的实际参数化类型，如List[] 则返回 List。

下面还是通过一个例子来深入了解：

  public class TestGenericArrayTypeBean<T> {
      
        //泛型数组类型
        private T[] value;
        private List<String>[] list;
      
        //不是泛型数组类型
        private List<String> singleList;
        private T singleValue;
      
        public static void main(String[] args){
      	Field[] fields = TestGenericArrayTypeBean.class.getDeclaredFields();
      	for (Field field: fields){
              field.setAccessible(true);
            //输出当前变量是否为GenericArrayType类型
              System.out.println("Field: "
                  + field.getName()
                  + "; instanceof GenericArrayType"
                  + ": "
                  + (field.getGenericType() instanceof GenericArrayType));
              if (field.getGenericType() instanceof GenericArrayType){
                //如果是GenericArrayType，则输出当前泛型类型
                System.out.println("Field: "
                    + field.getName()
                    + "; getGenericComponentType()"
                    + ": "
                    + (((GenericArrayType) 																field.getGenericType()).getGenericComponentType()));
              	        	}
      			  }
        	}
      	}
复制代码
接下来看下输出：

《Java中的Type类型详解》

WildcardType: 通配符类型

表示通配符类型，比如 <?>, <? Extends Number>等

getLowerBounds(): Type[]: 得到下边界的数组
getUpperBounds(): Type[]: 得到上边界的type数组
注：如果没有指定上边界，则默认为Object，如果没有指定下边界，则默认为String

下面还是通过一个例子了解一下：

     public class TestWildcardType {
          
            public static void main(String[] args){
              //获取TestWildcardType类的所有方法(本例中即 testWildcardType 方法)
          		Method[] methods = TestWildcardType.class.getDeclaredMethods();
          		for (Method method: methods){
                    //获取方法的所有参数类型
                    Type[] types = method.getGenericParameterTypes();
                    for (Type paramsType: types){
                       System.out.println("type: " + paramsType.toString());
                      //如果不是参数化类型则直接continue，执行下一个循环条件
                       if (!(paramsType instanceof ParameterizedType)){
                            continue;
                       }
                      //将当前类型强转为参数化类型并获取其实际参数类型(即含有通配符的泛型类型)
                       Type type = ((ParameterizedType) paramsType).getActualTypeArguments()[0];
                      //输出其是否为通配符类型
                       System.out.println("type instanceof WildcardType : " + 
                                             ( type instanceof WildcardType));
                       if (type instanceof WildcardType){
                          int lowIndex = ((WildcardType) type).getLowerBounds().length - 1;
                          int upperIndex = ((WildcardType) type).getUpperBounds().length - 1;
                         //输出上边界与下边界
                          System.out.println("getLowerBounds(): "
                                + 
                   (lowIndex >= 0 ? ((WildcardType) type).getLowerBounds()[lowIndex] : "String ")
                                + "; getUpperBounds(): "
                                + 
                 (upperIndex >=0 ? ((WildcardType) type).getUpperBounds()[upperIndex]:"Object"));
              		    }
            			}
          		}
            }
            public void testWildcardType(List<? extends OutputStream> numberList, List<? super InputStream> upperList, List<Integer> list, InputStream inputStream){}
          }
复制代码
输出：

《Java中的Type类型详解》

泛型的擦除的原因以及Java中Type的作用

其实在jdk1.5之前Java中只有原始类型而没有泛型类型，而在JDK 1.5 之后引入泛型，但是这种泛型仅仅存在于编译阶段，当在JVM运行的过程中，与泛型相关的信息将会被擦除，如List与List都将会在运行时被擦除成为List这个类型。而类型擦除机制存在的原因正是因为如果在运行时存在泛型，那么将要修改JVM指令集，这是非常致命的。

此外，原始类型在会生成字节码文件对象，而泛型类型相关的类型并不会生成与其相对应的字节码文件(因为泛型类型将会被擦除)，因此，无法将泛型相关的新类型与class相统一。因此，为了程序的扩展性以及为了开发需要去反射操作这些类型，就引入了Type这个类型，并且新增了ParameterizedType, TypeVariable, GenericArrayType, WildcardType四个表示泛型相关的类型，再加上Class，这样就可以用Type类型的参数来接受以上五种子类的实参或者返回值类型就是Type类型的参数。统一了与泛型有关的类型和原始类型Class。而且这样一来，我们也可以通过反射获取泛型类型参数。