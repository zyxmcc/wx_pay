package com.xmcc.service.impl;

import com.xmcc.common.ResultResponse;
import com.xmcc.dto.ProductCategoryDto;
import com.xmcc.entity.ProductCategory;
import com.xmcc.repository.ProductCategoryRepository;
import com.xmcc.service.ProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductCategoryServiceImpl implements ProductCategoryService{

    @Autowired
    private ProductCategoryRepository categoryRepository;

    @Override
    public ResultResponse<List<ProductCategoryDto>> findAll() {
        //查询商品分类集合
        //.collect(Collectors.toList()) 将流收集为list集合
        List<ProductCategory> list = categoryRepository.findAll();
        return ResultResponse.success(list.stream()
                .map(ProductCategory -> ProductCategoryDto.build(ProductCategory))
                .collect(Collectors.toList()));
    }
}
