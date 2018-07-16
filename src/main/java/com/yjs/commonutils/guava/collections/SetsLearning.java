package com.yjs.commonutils.guava.collections;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Set;

/**
 * create by jiangsongy on 2018/7/16
 */
public class SetsLearning {

    public static void main(String[] args) {
        Set<String> colors = Sets.newHashSet("red", "orange", "yellow");
        Set<Color> colorSet = Sets.newHashSet(new Color(0, 0, 0));

        // copyOf方法
        ImmutableSet.copyOf(colors);

        // of方法
        ImmutableSet.of(colors);
        ImmutableSet.of("red", "orange", "yellow");
        ImmutableMap.of("a", 1, "b", 2);

        // Builder工具
        ImmutableSet<Color> GOOGLE_COLORS = ImmutableSet.<Color>builder()
                .addAll(colorSet)
                .add(new Color(0, 191, 255))
                .build();

        ImmutableSet<String> colors2 = ImmutableSet.of("red", "orange", "yellow");

        // ImmutableXXX.copyOf方法会尝试在安全的时候避免做拷贝
        // 在这段代码中，ImmutableList.copyOf(foobar)会智能地直接返回foobar.asList()
        // 它是一个ImmutableSet的常量时间复杂度的List视图
        ImmutableList.copyOf(colors2);

        // 所有不可变集合都有一个asList()方法提供ImmutableList视图
        ImmutableList immutableList = colors2.asList();

    }

    private static class Color{
        private Integer red;
        private Integer yellow;
        private Integer blue;

        public Color(Integer red, Integer yellow, Integer blue) {
            this.red = red;
            this.yellow = yellow;
            this.blue = blue;
        }
    }

}
