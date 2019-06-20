package com.xmcc.service;

import com.xmcc.common.ResultResponse;
import com.xmcc.dto.ProductCategoryDto;

import java.util.List;

public interface ProductCategoryService {
    //查询商品分类
    ResultResponse<List<ProductCategoryDto>> findAll();
}
