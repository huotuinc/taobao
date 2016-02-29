package com.huotu.tools.taobao.entity;

import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;

/**
 * 可储存在数据库的
 * @author CJ
 */
public interface DatabaseStorage<T> extends ParameterizedPreparedStatementSetter<T> {
    /**
     *
     * @return 用于插入SQL的语句
     */
    String insertSQL();


}
