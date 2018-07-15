package com.yjs.commonutils.guava;

import com.google.common.base.Function;
import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Ints;
import java.util.Comparator;
import java.util.List;

/**
 * create by jiangsongy on 2018/7/16
 */
public class OrderingLearning {

    /**
     * 创建排序器
     * 常见的排序器可以由下面的静态方法创建
     */
    public static void creationMethod(){

        // 对可排序类型做自然排序，如数字按大小，日期按先后排序
        Ordering<String> naturalOrdering = Ordering.natural();
        // 按对象的字符串形式做字典排序
        Ordering<Object> usingToStringOrdering = Ordering.usingToString();
        // 把给定的Comparator转化为排序器
        Ordering<String> fromOrdering = Ordering.from(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.hashCode() - o2.hashCode() ;
            }
        });
        // 直接继承Ordering
        Ordering<String> byLengthOrdering = new Ordering<String>() {
            @Override
            public int compare(String left, String right) {
                return Ints.compare(left.length(), right.length());
            }
        };
    }

    /**
     * 链式调用方法
     * 通过链式调用，可以由给定的排序器衍生出其它排序器
     * 当阅读链式调用产生的排序器时，应该从后往前读，因为每次链式调用都是用后面的方法包装了前面的排序器
     * 用compound方法包装排序器时，不遵循从后往前读的原则
     */
    public static void chainingMethod(List list){

        List<String> withNullList = Lists.newArrayList(list);
        withNullList.add(null);
        System.out.println("withNullList:"+ withNullList);

        List<List> listList = Lists.newArrayList();
        listList.add(Lists.newArrayList("perter", "jerry"));
        listList.add(Lists.newArrayList("perter"));
        listList.add(Lists.newArrayList("perter", "jerry", null));
        System.out.println("listList:"+ withNullList);

        // 对可排序类型做自然排序，如数字按大小，日期按先后排序
        Ordering<String> naturalOrdering = Ordering.natural();
        // 获取语义相反的排序器
        Ordering<String> reverseOrdering = Ordering.natural().reverse();
        // 使用当前排序器，但额外把null值排到最前面
        Ordering<String> nullsFirst = Ordering.natural().nullsFirst();
        // 使用当前排序器，但额外把null值排到最后面
        Ordering<String> nullsLast = Ordering.natural().nullsLast();
        // 合成另一个比较器，以处理当前排序器中的相等情况
        Ordering<People> secondaryOrdering = new PeopleAgeOrder().compound(new PeopleNameLengthOrder());
        // 返回该类型的可迭代对象Iterable<T>的排序器
        Ordering lexicographicalOrdering = naturalOrdering.lexicographical();
        // 对集合中元素调用Function，再按返回值用当前排序器排序
        Ordering<String> resultOfOrdering = Ordering.natural().nullsFirst().onResultOf(new Function<String, Integer>() {
            @Override
            public Integer apply(String input) {
                return input == null ? null : input.length();
            }
        });
    }

    /**
     * 运用排序器
     * Guava的排序器实现有若干操纵集合或元素值的方法
     */
    public static void applicationMethod(List list) {

        // 获取可迭代对象中最大的k个元素
        System.out.println("greatestOfOrdering:"+ Ordering.natural().greatestOf(list, 3));
        System.out.println("leastOfOrdering:"+ Ordering.natural().leastOf(list, 3));

        // 判断可迭代对象是否已按排序器排序
        // 允许有排序值相等的元素
        System.out.println("isOrdered:"+ Ordering.natural().isOrdered(Ordering.natural().sortedCopy(list)));
        // 判断可迭代对象是否已严格按排序器排序
        // 不允许有排序值相等的元素
        System.out.println("isStrictlyOrdered:"+ Ordering.natural().isStrictlyOrdered(Ordering.natural().sortedCopy(list)));

        // 以列表形式返回指定元素的已排序副本
        System.out.println("isOrdered:"+ Ordering.natural().sortedCopy(list));
        // 返回包含按此排序排序的元素的不可变列表
        Ordering.natural().immutableSortedCopy(list);

        // 返回两个参数中最小的那个。如果相等，则返回第一个参数
        System.out.println("min:" + Ordering.natural().min("abc", "ab"));
        // 返回两个参数中最大的那个。如果相等，则返回第一个参数
        System.out.println("max:" + Ordering.natural().max("abc", "ab"));

        // 返回多个参数中最小的那个。如果有超过一个参数都最小，则返回第一个最小的参数
        System.out.println("min:" + Ordering.natural().min("ab", "cd", "abc"));
        // 返回多个参数中最大的那个。如果有超过一个参数都最大，则返回第一个最大的参数
        System.out.println("max:" + Ordering.natural().max("ab", "cde", "abc"));

        // 返回迭代器中最小的元素。如果可迭代对象中没有元素，则抛出NoSuchElementException
        System.out.println("min:" + Ordering.natural().min(list));
        // 返回迭代器中最大的元素。如果可迭代对象中没有元素，则抛出NoSuchElementException
        System.out.println("max:" + Ordering.natural().max(list));

    }

    private class People {
        public String name;
        public int age;

        People(String name, int age) {
            this.name = name;
            this.age = age;
        }

        @Override
        public String toString() {
            return MoreObjects
                    .toStringHelper(this)
                    .add("name", name)
                    .add("age", age)
                    .toString();
        }
    }

    private static class PeopleAgeOrder extends Ordering<People> {
        @Override
        public int compare(People left, People right) {
            return Ints.compare(left.age, right.age);
        }
    }

    private static class PeopleNameLengthOrder extends Ordering<People> {
        @Override
        public int compare(People left, People right) {
            return Ints.compare(left.name.length(), right.name.length());
        }
    }
}
