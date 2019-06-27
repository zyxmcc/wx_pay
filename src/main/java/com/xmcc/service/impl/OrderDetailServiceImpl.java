package com.xmcc.service.impl;
import com.xmcc.common.ResultEnums;
import com.xmcc.common.ResultResponse;
import com.xmcc.dao.impl.BatchDaoImpl;
import com.xmcc.entity.OrderDetail;
import com.xmcc.entity.OrderMaster;
import com.xmcc.repository.OrderDetailRepository;
import com.xmcc.service.OrderDetailService;
import com.xmcc.service.OrderMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderDetailServiceImpl extends BatchDaoImpl<OrderDetail> implements OrderDetailService {

    @Autowired
    private OrderMasterService orderMasterService;
    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Override
    @Transactional
    public void batchInsert(List<OrderDetail> orderDetailList) {
        super.batchInsert(orderDetailList);
    }

    @Override
    public ResultResponse queryByOrderIdWithOrderMaster(String openId, String orderId) {
        ResultResponse<OrderMaster> orderMasterResult = orderMasterService.findByOrderIdAndOpenId(orderId, openId);
        OrderMaster orderMaster = orderMasterResult.getData();
        if(orderMasterResult.getCode()== ResultEnums.FAIL.getCode() || orderMaster==null){
            return orderMasterResult;
        }
        //通过订单id查询订单详细列表
        ResultResponse<List<OrderDetail>> orderDetailList = findOrderDetailByOrderId(orderId);
        List<OrderDetail> listData = orderDetailList.getData();
        orderMaster.setOrderDetailList(listData);
        return orderMasterResult;
    }

    @Override
    public ResultResponse<List<OrderDetail>> findOrderDetailByOrderId(String orderId) {
        List<OrderDetail> orderDetailList = orderDetailRepository.findByOrderId(orderId);
        //设置商品小图
        if(!CollectionUtils.isEmpty(orderDetailList)){
            orderDetailList.stream().map(orderDetail -> {
                orderDetail.setProductImage(orderDetail.getProductIcon());
                return orderDetail;
                }).collect(Collectors.toList());
        }
        return ResultResponse.success(orderDetailList);
    }


}
