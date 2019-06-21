package com.xmcc.service;

import com.xmcc.common.ResultResponse;
import com.xmcc.entity.ProductInfo;

public interface ProductInfoService {
    //查询所有商品信息
    ResultResponse queryList();

    //根据商品id查询商品
    ResultResponse<ProductInfo> queryById(String productId);

    //更新商品的库存
    void updateProduct(ProductInfo productInfo);
}
