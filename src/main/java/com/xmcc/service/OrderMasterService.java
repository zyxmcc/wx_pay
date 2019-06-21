package com.xmcc.service;

import com.xmcc.common.ResultResponse;
import com.xmcc.dto.OrderMasterDto;

/**
 * 订单接口
 */
public interface OrderMasterService {
    //插入订单
    ResultResponse insetOrder(OrderMasterDto orderMasterDto);

}
