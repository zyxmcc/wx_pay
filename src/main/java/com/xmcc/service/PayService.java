package com.xmcc.service;

import com.lly835.bestpay.model.PayResponse;
import com.lly835.bestpay.model.RefundResponse;
import com.xmcc.entity.OrderMaster;

public interface PayService {
    //根据订单id查询订单
    OrderMaster findOrderMasterById(String orderId);

    //发起支付
    PayResponse create(OrderMaster orderMaster);

    void weixin_notify(String notifyData);
    //退款操作
    RefundResponse refund(OrderMaster orderMaster);
}
