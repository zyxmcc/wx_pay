package com.xmcc.service;

import com.xmcc.entity.OrderDetail;

import java.util.List;

/**
 * 订单详情表接口
 */
public interface OrderDetailService {

    //批量插入订单项
    void batchInsert(List<OrderDetail> orderDetailList);

}
