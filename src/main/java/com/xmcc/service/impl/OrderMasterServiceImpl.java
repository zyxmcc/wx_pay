package com.xmcc.service.impl;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xmcc.common.*;
import com.xmcc.dto.OrderDetailDto;
import com.xmcc.dto.OrderMasterDto;
import com.xmcc.entity.OrderDetail;
import com.xmcc.entity.OrderMaster;
import com.xmcc.entity.ProductInfo;
import com.xmcc.exception.CustomException;
import com.xmcc.repository.OrderMasterRepository;
import com.xmcc.service.OrderDetailService;
import com.xmcc.service.OrderMasterService;
import com.xmcc.service.PayService;
import com.xmcc.service.ProductInfoService;
import com.xmcc.util.BigDecimalUtil;
import com.xmcc.util.IDUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderMasterServiceImpl implements OrderMasterService {

    @Autowired
    private ProductInfoService productInfoService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private OrderMasterRepository orderMasterRepository;

    @Autowired
    private PayService payService;


    @Override
    @Transactional
    public ResultResponse insetOrder(OrderMasterDto orderMasterDto) {
        //参数验证交给controller验证，这里直接取出订单项
        List<OrderDetailDto> items = orderMasterDto.getItems();
        //创建订单的集合 装符合条件的订单项
        List<OrderDetail> orderDetailList = Lists.newArrayList();
        //创建一个订单的总额
        BigDecimal totalPrice = new BigDecimal("0");

        //遍历订单项
        for (OrderDetailDto item :items) {
            //查询订单 商品信息 code  msg  data
            ResultResponse<ProductInfo> resultProduct = productInfoService.queryById(item.getProductId());
            //商品是否下架 订单生成失败
            if(resultProduct.getCode()== ResultEnums.FAIL.getCode()){
                //订单生成失败 商品下架
                throw new CustomException(resultProduct.getMsg());
            }
            //获取到商品信息
            ProductInfo productInfo = resultProduct.getData();
            //比较库存 如果购买的商品大于库存数量
            if(item.getProductQuantity()>productInfo.getProductStock()){
                //订单生成失败 商品库存不足
                throw new CustomException(ProductEnums.PRODUCT_NOT_ENOUGH.getMsg());
            }

            //生成订单项
            OrderDetail orderDetail = OrderDetail.builder()
                    .detailId(IDUtils.createIdbyUUID())
                    .productIcon(productInfo.getProductIcon())
                    .productId(item.getProductId())
                    .productName(productInfo.getProductName())
                    .productPrice(productInfo.getProductPrice())
                    .productQuantity(item.getProductQuantity())
                    .build();
            orderDetailList.add(orderDetail);

            //减少商品库存
            productInfo.setProductStock(productInfo.getProductStock()-item.getProductQuantity());
            productInfoService.updateProduct(productInfo);

            //算订单的总价格
            totalPrice = BigDecimalUtil.add(totalPrice, BigDecimalUtil.multi(productInfo.getProductPrice(),item.getProductQuantity()));
        }

        //生成订单id
        String orderId = IDUtils.createIdbyUUID();
        //构建订单信息  日期等都用默认的即可
        OrderMaster orderMaster = OrderMaster.builder()
                .buyerAddress(orderMasterDto.getAddress())
                .buyerName(orderMasterDto.getName())
                .buyerOpenid(orderMasterDto.getOpenid())
                .orderStatus(OrdersEnums.NEW.getCode())
                .payStatus(PayEnums.WAIT.getCode())
                .buyerPhone(orderMasterDto.getPhone())
                .orderId(orderId).orderAmount(totalPrice).build();

        //将生成的订单id添加到
        List<OrderDetail> list = orderDetailList.stream().map(orderDetail -> {
            orderDetail.setOrderId(orderId);
            return orderDetail;
        }).collect(Collectors.toList());

        //插入订单详细信息
        orderDetailService.batchInsert(list);
        //插入订单信息
        orderMasterRepository.save(orderMaster);

        HashMap<String,String> map = Maps.newHashMap();
        map.put("orderId",orderId);
        return ResultResponse.success(map);
    }

    @Override
    public ResultResponse queryList(String openId, Integer page, Integer size) {
        if(StringUtils.isBlank(openId)){
            return ResultResponse.fail(OrdersEnums.OPENID_ERROR.getMsg());
        }
        PageRequest pageRequest = PageRequest
                .of(page == null || page - 1 < 0 ? 0 : page - 1, size == null || size < 3 ? 3 : size);
        Page<OrderMaster> byBuyerOpenid = orderMasterRepository.findByBuyerOpenid(openId, pageRequest);

        return ResultResponse.success(byBuyerOpenid.getContent());
    }

    //根据客户id和订单id查询订单
    @Override
    public ResultResponse<OrderMaster> findByOrderIdAndOpenId(String orderId, String openid) {
        if(StringUtils.isBlank(openid)){
            return ResultResponse.fail(ResultEnums.PARAM_ERROR.getMsg());
        }
        if(StringUtils.isBlank(orderId)){
            return ResultResponse.fail(ResultEnums.PARAM_ERROR.getMsg());
        }
        OrderMaster orderMaster = orderMasterRepository.findByOrderIdAndBuyerOpenid(orderId, openid);

        return ResultResponse.success(orderMaster);
    }

    /**
     * 取消订单
     * 步骤：1、判断当前订单的状态，已完成的不能取消或者已取消的不能再取消
     * 2、是新建订单可修改为取消状态
     * 3、改为取消后 需要查询相应的订单详细信息，获取到相应的商品信息
     * 4、增加商品库存
     * 5、如果是已付款 则需要退款
     * @param openId
     * @param orderId
     * @return
     */
    @Override
    @Transactional
    public ResultResponse canelOrder(String openId, String orderId) {
        //根据微信id和订单id查询订单
        ResultResponse<OrderMaster> byOrderIdAndOpenId = findByOrderIdAndOpenId(orderId, openId);
        if(byOrderIdAndOpenId.getCode()==ResultEnums.FAIL.getCode()){
            return  byOrderIdAndOpenId;
        }
        OrderMaster orderMaster = byOrderIdAndOpenId.getData();
        if(orderMaster==null){
            return ResultResponse.fail(OrdersEnums.ORDER_NOT_EXITS.getMsg());
        }
        if(orderMaster.getOrderStatus()==OrdersEnums.FINSH.getCode()
                ||orderMaster.getOrderStatus()==OrdersEnums.CANCEL.getCode()){
            return ResultResponse.fail(OrdersEnums.FINSH_CANCEL.getMsg());
        }
        //修改订单状态
        updateOrderStatus(orderMaster,OrdersEnums.CANCEL.getCode());

        //修改状态后需要查询商品详细表拿到商品
        ResultResponse<List<OrderDetail>> orderDetailByOrderId = orderDetailService.findOrderDetailByOrderId(orderId);
        List<OrderDetail> orderDetailList = orderDetailByOrderId.getData();
        if(!CollectionUtils.isEmpty(orderDetailList)){
            //修改库存
            for (OrderDetail orderDetail:orderDetailList) {
                ResultResponse<Integer> result = productInfoService.saveProductStock(orderDetail.getProductQuantity(), orderDetail.getProductId());
                if(result.getData()<1){
                    //增加库存失败 失败则回滚
                    log.error("商品库存增加失败，商品编号为:{},商品名称为:{}",orderDetail.getProductId(),orderDetail.getProductName());
                    throw new CustomException("商品库存增加失败");
                }
            }
        }
        //如果已付款，则退款
        if(orderMaster.getPayStatus()==PayEnums.FINISH.getCode()){
            //退款
            payService.refund(orderMaster);
        }
        return ResultResponse.success();
    }

    @Override
    @Transactional
    /**
     * 修改订单状态
     */
    public ResultResponse updateOrderStatus(OrderMaster orderMaster, int status) {
        if(orderMaster==null){
            throw new CustomException(OrdersEnums.ORDER_NOT_EXITS.getMsg());
        }
        orderMaster.setOrderStatus(status);
        orderMasterRepository.save(orderMaster);
        return ResultResponse.success();
    }
}
