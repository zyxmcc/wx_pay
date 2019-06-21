package com.xmcc.dao.impl;

import com.xmcc.dao.BatchDao;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;


public class BatchDaoImpl<T> implements BatchDao<T> {

    @PersistenceContext  //支持数据持久化的操作
    private EntityManager entityManager;

    @Override
    @Transactional
    public void batchInsert(List<T> list) {
        int length = list.size();
        //循环遍历list 将数据放入缓冲区
        for (int i=0;i<length;i++){
            entityManager.persist(list.get(i));
            //每100条执行一次添加操作 或者 长度等于0时执行
            if(i%100==0 || i==length-1){
                entityManager.flush();
                entityManager.clear();
            }
        }
    }
}
