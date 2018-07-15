package com.yjs.commonutils.guava;

import com.google.common.base.Objects;

/**
 * create by jiangsongy on 2018/7/16
 */
public class ObjectsLearning {

    public static void main(String[] args) {

    }

    /**
     * 使用Objects.equal帮助执行null敏感的equals判断，从而避免抛出NullPointerException
     * 注意：JDK7引入的Objects类提供了一样的方法Objects.equals
     */
    public static void equalsMethod() {
        // returns true
        Objects.equal("a", "a");
        // returns false
        Objects.equal(null, "a");
        // returns false
        Objects.equal("a", null);
        // returns true
        Objects.equal(null, null);

    }
}
