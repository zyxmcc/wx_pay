package com.xmcc.controller;

import com.lly835.bestpay.model.PayResponse;
import com.xmcc.entity.OrderMaster;
import com.xmcc.service.PayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@RequestMapping("pay")
@Controller
@Slf4j
public class PayController {

    @Autowired
    private PayService payService;

    @RequestMapping("create")
    /**
     * 1、根据API文档创建接口
     * orderId: 161899085773669363 订单编号
     * returnUrl: http://xxx.com/abc/order/161899085773669363 回调地址
     */
    public ModelAndView create(@RequestParam("orderId")String orderId,
                @RequestParam("returnUrl")String returnUrl, Map map){
            //获取到订单
            OrderMaster orderMaster = payService.findOrderMasterById(orderId);

            //根据订单发起支付
            PayResponse response = payService.create(orderMaster);
            map.put("payResponse",response);
            map.put("returnUrl",returnUrl);
            return new ModelAndView("weixin/pay",map);

    }
    @RequestMapping("notify")
    public ModelAndView weixin_notify(@RequestBody String notifyData){
        log.info("微信支付异步回调");
        //调用业务层处理回调验证，修改订单的状态
        payService.weixin_notify(notifyData);
        return new ModelAndView("weixin/success");
    }

    //微信退款
}
