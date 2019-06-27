package com.xmcc.service;

import com.xmcc.common.ResultResponse;
import com.xmcc.dto.OrderMasterDto;
import com.xmcc.entity.OrderMaster;

/**
 * 订单接口
 */
public interface OrderMasterService {
    //插入订单
    ResultResponse insetOrder(OrderMasterDto orderMasterDto);
    //分页查询
    ResultResponse queryList(String openId,Integer page,Integer size);
    //查询订单详情
    ResultResponse<OrderMaster> findByOrderIdAndOpenId(String orderId,String openid);
    //取消订单
    ResultResponse canelOrder(String openId,String orderId);
    //修改订单状态
    ResultResponse updateOrderStatus(OrderMaster orderMaster,int status);
}
