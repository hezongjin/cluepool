package cn.dd.apisys.utils;

import org.elasticsearch.index.query.QueryBuilder;

/**
 * FileName: CommBuilder.java
 * CreateTime: 2020/6/3 17:00.
 * Version: V1.0
 * Author: ChengTao
 * Description:
 */
public class CommBuilder {
    private Type type;
    private QueryBuilder queryBuilder;

    private CommBuilder() {

    }

    private CommBuilder(Type type, QueryBuilder queryBuilder) {
        this.type = type;
        this.queryBuilder = queryBuilder;
    }

    public static CommBuilder build(Type type, QueryBuilder queryBuilder) {
        return new CommBuilder(type, queryBuilder);
    }


    public Type getType() {
        return type;
    }

    public QueryBuilder getQueryBuilder() {
        return queryBuilder;
    }

    public static enum Type {
        DEF, FILTER
    }
}
