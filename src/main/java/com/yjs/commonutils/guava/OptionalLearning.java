package com.yjs.commonutils.guava;

import com.google.common.base.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * create by jiangsongy on 2018/7/15
 */
public class OptionalLearning {

    private static Logger logger = LoggerFactory.getLogger(OptionalLearning.class);
    public static void main(String[] args) {

    }

    public static void testBase(){
        // 创建的三种方式
        //one
        //创建允许null值的Optional
        Optional<Integer> possible = Optional.fromNullable(5);

        //two
        //若引用为null则快速失败触发java.lang.NullPointerException
        Integer nubmerone=4;
        Optional<Integer> integerOptional = Optional.of(nubmerone);

        //three
        //创建引用缺失的Optional实例,就是为NULL的
        Optional<Integer> nullOptional=Optional.absent();
    }

    public static void commonUse(){
        //isPresent() 如果Optional包含非null的引用（引用存在），返回true
        //get() 如果Optional为NULL将触发异常

        //创建允许null值的Optional
        Optional<Integer> possible = Optional.fromNullable(5);
        //包含的引用非null的（引用存在），返回true
        if(possible.isPresent()){
            //包含的引用缺失(null)，则抛出java.lang.IllegalStateException
            logger.info("possible.value："+possible.get());
        }else{
            logger.info("possible is null");
        }
    }

    public static void returnDefaultvalue(){
        //or(defaultvalue) 包含的引用缺失(null)，返回默认的值，否则返回本身

        //null就是缺少的意思
        Optional<Integer> nulloptional = Optional.absent();
        Integer value = nulloptional.or(3);
        logger.info("【if nulloptional is null,return is 3,others is itself't value】value ="+value);

        Optional<Integer> optional = Optional.of(5);
        Integer value2 = optional.or(3);
        logger.info("【if optional is null,return is 3,others is itself't value】value ="+value2);
    }

    public static  void returnNullIfAbsent(){
        //缺失引用返回Null

        //null就是缺少的意思
        Optional<Integer> nulloptional = Optional.absent();
        Integer value1 = nulloptional.orNull();
        logger.info("value1 ="+value1);

        Optional<Integer> optional = Optional.of(5);
        Integer value2 = optional.orNull();
        logger.info("value2 ="+value2);
    }

    public static  void convertSet(){
        //null就是缺少的意思
        Optional<Integer> nulloptional = Optional.absent();
        Set<Integer> set1 = nulloptional.asSet();
        logger.info("set1 size ="+set1.size());

        Optional<Integer> optional = Optional.of(5);
        Set<Integer> set2 = optional.asSet();
        logger.info("set2 size ="+set2.size());
    }

    /**
     * 使用Optional除了赋予null语义，增加了可读性，最大的优点在于它是一种傻瓜式的防护。
     * Optional 迫使你积极思考引用缺失的情况 因为你必须显式地从Optional获取引用。
     * 如同输入参数，方法的返回值也可能是null。和其他人一样，你绝对很可能会忘记别人写的方法method(a,b)会返回一个null，
     * 就好像当你实现method(a,b)时，也很可能忘记输入参数a可以为null。将方法的返回类型指定为Optional，方法的参数设置为Optional，
     * 也可以迫使调用者思考返回的引用缺失的情形。
     */
    public static Optional<Integer> sum(Optional<Integer> a,Optional<Integer> b){
        if(a.isPresent() && b.isPresent()){
            return Optional.of(a.get()+b.get());
        }
        return Optional.absent();
    }

    /**
     * Optional 主要用作返回类型。在获取到这个类型的实例后，如果它有值，你可以取得这个值，否则可以进行一些替代行为。
     * Optional 类有一个非常有用的用例，就是将其与流或其它返回 Optional 的方法结合，以构建流畅的API。
     * 使用 Stream 返回 Optional 对象的 findFirst() 方法：
     */

    public void whenEmptyStreamThenReturnDefaultOptional() {
        List<User> users = new ArrayList<>();
        User user = users.stream().findFirst().orElse(new User("default", "1234"));
        System.out.println(user.getUsername().equals("default"));
    }

    private class User{
        private String username;
        private String password;

        public User(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    /**
     * 返回默认值
     * Optional 类提供了 API 用以返回对象值，或者在对象为空的时候返回默认值。
     * 这里你可以使用的第一个方法是 orElse()，它的工作方式非常直接，如果有值则返回该值，否则返回传递给它的参数值：
     * 两个 Optional  对象都包含非空值，两个方法都会返回对应的非空值。不过，orElse() 方法仍然创建了 User 对象。与之相反，
     * orElseGet() 方法不创建 User 对象。
     * 在执行较密集的调用时，比如调用 Web 服务或数据查询，这个差异会对性能产生重大影响。
     */


}
