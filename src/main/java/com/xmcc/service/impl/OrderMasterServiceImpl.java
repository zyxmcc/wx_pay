package com.xmcc.service.impl;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xmcc.common.*;
import com.xmcc.dao.impl.BatchDaoImpl;
import com.xmcc.dto.OrderDetailDto;
import com.xmcc.dto.OrderMasterDto;
import com.xmcc.entity.OrderDetail;
import com.xmcc.entity.OrderMaster;
import com.xmcc.entity.ProductInfo;
import com.xmcc.exception.CustomException;
import com.xmcc.repository.OrderMasterRepository;
import com.xmcc.service.OrderDetailService;
import com.xmcc.service.OrderMasterService;
import com.xmcc.service.ProductInfoService;
import com.xmcc.util.BigDecimalUtil;
import com.xmcc.util.IDUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderMasterServiceImpl implements OrderMasterService {

    @Autowired
    private ProductInfoService productInfoService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private OrderMasterRepository orderMasterRepository;


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
}
