package com.xmcc.service.impl;

import com.alibaba.druid.sql.visitor.functions.Concat;
import com.lly835.bestpay.enums.BestPayTypeEnum;
import com.lly835.bestpay.model.PayRequest;
import com.lly835.bestpay.model.PayResponse;
import com.lly835.bestpay.model.RefundRequest;
import com.lly835.bestpay.model.RefundResponse;
import com.lly835.bestpay.service.BestPayService;
import com.xmcc.common.Constant;
import com.xmcc.common.OrdersEnums;
import com.xmcc.common.PayEnums;
import com.xmcc.entity.OrderMaster;
import com.xmcc.exception.CustomException;
import com.xmcc.repository.OrderMasterRepository;
import com.xmcc.service.PayService;
import com.xmcc.util.BigDecimalUtil;
import com.xmcc.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@Slf4j
public class PayServiceImpl implements PayService {
    @Autowired
    private OrderMasterRepository orderMasterRepository;

    @Autowired
    private BestPayService bestPayService;
    @Override
    public OrderMaster findOrderMasterById(String orderId) {
        Optional<OrderMaster> byId = orderMasterRepository.findById(orderId);
        if(!byId.isPresent()){
            //订单不存在
            throw new CustomException(OrdersEnums.ORDER_NOT_EXITS.getMsg());
        }
        OrderMaster orderMaster = byId.get();

        return orderMaster;
    }

    @Override
    public PayResponse create(OrderMaster orderMaster) {
        PayRequest payRequest = new PayRequest();
        //支付的类型
        payRequest.setPayTypeEnum(BestPayTypeEnum.WXPAY_H5);
        //订单编号
        payRequest.setOrderId(orderMaster.getOrderId());
        //订单名称
        payRequest.setOrderName(Constant.ORDER_NAME);
        //订单总额
        payRequest.setOrderAmount(orderMaster.getOrderAmount().doubleValue());
        //微信用户id
        payRequest.setOpenid(orderMaster.getBuyerOpenid());
        log.info("微信支付的请求:{}", JsonUtil.object2string(payRequest));
        PayResponse payResponse = bestPayService.pay(payRequest);
        log.info("微信支付的请求的返回结果:{}",JsonUtil.object2string(payResponse));
        return payResponse;
    }

    @Override
    public void weixin_notify(String notifyData) {
        //调用api进行验证
        PayResponse response = bestPayService.asyncNotify(notifyData);
        //查询订单
        OrderMaster orderMaster = findOrderMasterById(response.getOrderId());
        //比较金额
        if(!BigDecimalUtil.equals2(orderMaster.getOrderAmount(),
                new BigDecimal(String.valueOf(response.getOrderAmount())))){
            //订单金额不一致 打印日志
            log.error("订单金额不一致，微信：{},数据库：{}",response.getOrderAmount(),orderMaster.getOrderAmount());
            throw new CustomException(OrdersEnums.AMOUNT_CHECK_ERROR.getMsg());
        }
        //判断订单是否为可支付状态
        if(!(orderMaster.getPayStatus()== PayEnums.WAIT.getCode())){
            log.error("微信回调，订单支付状态异常:{}",orderMaster.getPayStatus());
            throw new CustomException(PayEnums.STATUS_ERROR.getMsg());
        }
        //金额一样并且订单为可支付 改变订单支付的状态(改为已支付)
        orderMaster.setPayStatus(PayEnums.FINISH.getCode());
        //只改变了支付状态 而订单状态的修改需要其它业务支持
        orderMasterRepository.save(orderMaster);
        log.info("微信支付异步回调成功，订单状态已改为支付完成");
    }

    @Override
    public RefundResponse refund(OrderMaster orderMaster) {
        RefundRequest refundRequest = new RefundRequest();
        refundRequest.setOrderAmount(orderMaster.getOrderAmount().doubleValue());
        refundRequest.setOrderId(orderMaster.getOrderId());
        refundRequest.setPayTypeEnum(BestPayTypeEnum.WXPAY_H5);
        log.info("微信退款请求:{}",refundRequest);
        //执行退款
        RefundResponse refund = bestPayService.refund(refundRequest);
        log.info("微信退款请求响应:{}",refund);
        return refund;
    }
}
