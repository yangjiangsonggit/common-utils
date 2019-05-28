# pagehelper源码
> https://www.jianshu.com/p/8800867b6240

## 关键1——数据总条数怎么查到的

```text
 private Long executeAutoCount(Executor executor, MappedStatement countMs, Object parameter, BoundSql boundSql,
                                   RowBounds rowBounds, ResultHandler resultHandler) throws IllegalAccessException, SQLException {
    Map<String, Object> additionalParameters = (Map<String, Object>) additionalParametersField.get(boundSql);
    //创建 count 查询的缓存 key
    CacheKey countKey = executor.createCacheKey(countMs, parameter, RowBounds.DEFAULT, boundSql);
    //调用方言获取 count sql
    String countSql = dialect.getCountSql(countMs, boundSql, parameter, rowBounds, countKey);
    //countKey.update(countSql);
    BoundSql countBoundSql = new BoundSql(countMs.getConfiguration(), countSql, boundSql.getParameterMappings(), parameter);
    //当使用动态 SQL 时，可能会产生临时的参数，这些参数需要手动设置到新的 BoundSql 中
    for (String key : additionalParameters.keySet()) {
        countBoundSql.setAdditionalParameter(key, additionalParameters.get(key));
    }
    //执行 count 查询
    Object countResultList = executor.query(countMs, parameter, RowBounds.DEFAULT, resultHandler, countKey, countBoundSql);
    Long count = (Long) ((List) countResultList).get(0);
    return count;
}
```    
> 在executeAutoCount中计算总数：
2018-09-29 18:08:12.701 [main] DEBUG c.e.m.m.TUserMapper.selectByEmailAndSex1_COUNT - ==>  Preparing: SELECT count(0) FROM t_user a WHERE a.email LIKE CONCAT('%', ?, '%') AND a.sex = ? 
2018-09-29 18:08:12.912 [main] DEBUG c.e.m.m.TUserMapper.selectByEmailAndSex1_COUNT - ==> Parameters: qq.com(String), 1(Byte)
2018-09-29 18:08:13.049 [main] DEBUG c.e.m.m.TUserMapper.selectByEmailAndSex1_COUNT - <==      Total: 1

## 关键2——分页怎么做到的

```text
//调用方言获取分页 sql
    String pageSql = dialect.getPageSql(ms, boundSql, parameter, rowBounds, pageKey);
    BoundSql pageBoundSql = new BoundSql(configuration, pageSql, boundSql.getParameterMappings(), parameter);
    //设置动态参数
    for (String key : additionalParameters.keySet()) {
        pageBoundSql.setAdditionalParameter(key, additionalParameters.get(key));
    }
    //执行分页查询
    resultList = executor.query(ms, parameter, RowBounds.DEFAULT, resultHandler, pageKey, pageBoundSql);
} else {
    //不执行分页的情况下，也不执行内存分页
    resultList = executor.query(ms, parameter, RowBounds.DEFAULT, resultHandler, cacheKey, boundSql);
}
```

## 分页性能
性能和 limit ?,? 一样，大数据量下会存在问题