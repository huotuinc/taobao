package com.huotu.tools.taobao.entity;

/**
 * 是否还持有一个数据库处理器
 * @author CJ
 */
public interface DatabaseStorageHolder<T> {

    DatabaseStorage<T> secondDatabaseStorage();

}
