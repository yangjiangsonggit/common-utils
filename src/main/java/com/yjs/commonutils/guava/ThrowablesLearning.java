package com.yjs.commonutils.guava;

import com.google.common.base.Throwables;
import java.sql.SQLException;
import java.util.List;

/**
 * create by jiangsongy on 2018/7/16
 */
public class ThrowablesLearning {

    public static void main(String[] args) throws SQLException {

        RuntimeException t = new RuntimeException("t");
        // 类型为 X 时才抛出
        Throwables.throwIfInstanceOf(t, SQLException.class);

        // 类型为Error或RuntimeException时抛出
        Throwables.throwIfUnchecked(t);

        // 获取异常原因链
        Throwable throwable = Throwables.getRootCause(t);
        List<Throwable> throwables = Throwables.getCausalChain(t);
        String str = Throwables.getStackTraceAsString(t);

    }
}
