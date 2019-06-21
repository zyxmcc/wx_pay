package com.xmcc.dao;

import java.util.List;

/**
 * 执行的批量添加操作
 * @param <T>
 */
public interface BatchDao<T> {
    void batchInsert(List<T> list);
}
