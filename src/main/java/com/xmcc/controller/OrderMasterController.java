package com.xmcc.controller;

import com.google.common.collect.Maps;
import com.lly835.bestpay.rest.type.Post;
import com.xmcc.common.ResultResponse;
import com.xmcc.dto.OrderMasterDto;
import com.xmcc.service.OrderDetailService;
import com.xmcc.service.OrderMasterService;
import com.xmcc.util.JsonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("buyer/order")
@Api(value = "订单相关接口",description = "完成订单的增删改查")
public class OrderMasterController {

    @Autowired
    private OrderMasterService orderMasterService;
    @Autowired
    private OrderDetailService orderDetailService;

    @PostMapping("create")
    @ApiOperation(value = "创建订单接口", httpMethod = "POST", response =ResultResponse.class)
    // bindingResult 帮助验证的类
    public ResultResponse create(
            @Valid @ApiParam(name="订单对象",value = "传入json格式",required = true)
            OrderMasterDto orderMasterDto, BindingResult bindingResult){
        Map<String,String> map = Maps.newHashMap();
        if(bindingResult.hasErrors()){
            List<String> list = bindingResult.getFieldErrors().stream()
                    .map(fieldError -> fieldError.getDefaultMessage())
                    .collect(Collectors.toList());
            map.put("参数校验出错", JsonUtil.object2string(list));
            return ResultResponse.fail(map);
        }
        return orderMasterService.insetOrder(orderMasterDto);
    }

    @GetMapping("list")
    @ApiOperation(value = "订单列表分页接口", httpMethod = "GET", response =ResultResponse.class)
    public ResultResponse list(String openId,Integer page,Integer size){
        return orderMasterService.queryList(openId, page, size);
    }

    @GetMapping("detail")
    @ApiOperation(value = "根据订单id和openId查询订单详情", httpMethod = "GET", response =ResultResponse.class)
    public ResultResponse detail(@RequestParam(value = "openid",required = true) String openId,
                                 @RequestParam(value = "orderId",required = true)String orderId){
        return orderDetailService.queryByOrderIdWithOrderMaster(openId,orderId);
    }

    //取消订单
    @PostMapping("cancel")
    @ApiOperation(value = "取消订单", httpMethod = "POST", response =ResultResponse.class)
    public ResultResponse cancel(@RequestParam(value = "openid",required = true) String openId,
                                 @RequestParam(value = "orderId",required = true)String orderId){
        return orderMasterService.canelOrder(openId,orderId);
    }

}
