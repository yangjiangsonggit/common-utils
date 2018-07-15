package com.yjs.commonutils.guava;

import com.google.common.base.Preconditions;


/**
 * create by jiangsongy on 2018/7/15
 */
public class PreconditionsLearning {

    public static void main(String[] args) {

        int i = 2;
        int j = 3;
        String name = "jiangsongy";

        // 空值检查
        Preconditions.checkNotNull(name,"name may not be null");

        // 没有额外参数：抛出的异常中没有错误消息
        Preconditions.checkArgument(i <= 0);

        // 有一个Object对象作为额外参数
        Preconditions.checkArgument(i <= 0, "Argument Exception");

        // 有一个String对象作为额外参数，并且有一组任意数量的附加Object对象
        Preconditions.checkArgument(i < j, "Expected i < j, but %s > %s", i, j);


        /**
         * 检查boolean是否为true，用来检查传递给方法的参数
         * 检查失败时抛出 IllegalArgumentException
         */
        Preconditions.checkArgument(Boolean.TRUE);

        /**
         * 检查value是否为null，该方法直接返回value，因此可以内嵌使用checkNotNull
         * 可以在构造函数中保持字段的单行赋值风格：this.field = checkNotNull(field)
         * 检查失败时抛出 NullPointerException
         */
        Preconditions.checkNotNull(null);

        /**
         * 用来检查对象的某些状态
         * 检查失败时抛出 IllegalStateException
         */
        Preconditions.checkState(Boolean.FALSE);

        /**
         * 检查index作为索引值对某个列表、字符串或数组是否有效。index>=0 && index<size
         * 检查失败时抛出 IndexOutOfBoundsException
        */
        Preconditions.checkElementIndex(3,3);

        /**
         * 检查index作为位置值对某个列表、字符串或数组是否有效。index>=0 && index<=size
         * 检查失败时抛出 IndexOutOfBoundsException
         */
        Preconditions.checkPositionIndex(3, 3);

        /**
         * 检查[start, end]表示的位置范围对某个列表、字符串或数组是否有效
         * 检查失败时抛出 IndexOutOfBoundsException
         */
        Preconditions.checkPositionIndexes(0, 3, 3);
    }
}
