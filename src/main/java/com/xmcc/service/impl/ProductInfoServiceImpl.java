package com.xmcc.service.impl;

import com.xmcc.common.ResultEnums;
import com.xmcc.common.ResultResponse;
import com.xmcc.dto.ProductCategoryDto;
import com.xmcc.dto.ProductInfoDto;
import com.xmcc.entity.ProductCategory;
import com.xmcc.entity.ProductInfo;
import com.xmcc.repository.ProductInfoRepository;
import com.xmcc.service.ProductCategoryService;
import com.xmcc.service.ProductInfoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductInfoServiceImpl implements ProductInfoService {

    //导入商品分类的编号
    @Autowired
    private ProductCategoryService productCategoryService;

    //根据商品类型的编号和商品的状态 查询商品信息
    @Autowired
    private ProductInfoRepository productInfoRepository;

    @Override
    public ResultResponse queryList() {
        //拿到分类查询的结果集
        ResultResponse<List<ProductCategoryDto>> productCategoryResult = productCategoryService.findAll();
        //从结果集中获取到分类数据
        List<ProductCategoryDto> productCategoryList = productCategoryResult.getData();


        if(CollectionUtils.isEmpty(productCategoryList)){
            return ResultResponse.fail();
        }
        //获取分类编号
        List<Integer> typeId = productCategoryList.stream()
                .map(productCategoryDto -> productCategoryDto.getCategoryType())
                .collect(Collectors.toList());

        List<ProductInfo> productInfoList = productInfoRepository.findByProductStatusAndCategoryTypeIn(ResultEnums.PRODUCT_UP.getCode(), typeId);
        /**
         * 通过多线程遍历集合 取出每个商品类型下对应的商品 设置到对应的类别中
         * 1、将productInfoList装入foods
         * 2、根据不同的分类编号过滤 进行不同的分组
         * 3、将ProductInfo转换为 Infodto
         */
        List<ProductCategoryDto> list = productCategoryList.parallelStream()
                .map(productCategoryDto -> {
                    productCategoryDto.setProductInfoDtoList(productInfoList.stream()
                            .filter(productInfo -> productInfo.getCategoryType() == productCategoryDto.getCategoryType())
                            .map(productInfo -> ProductInfoDto.build(productInfo))
                            .collect(Collectors.toList()));
            return productCategoryDto;

        }).collect(Collectors.toList());
        return ResultResponse.success(list);
    }

    @Override
    public ResultResponse<ProductInfo> queryById(String productId) {
        //判断字符串是否为空
        if(StringUtils.isBlank(productId)){
            return  ResultResponse.fail(ResultEnums.PARAM_ERROR.getMsg());
        }
        Optional<ProductInfo> byId = productInfoRepository.findById(productId);
        if(!byId.isPresent()){
            return ResultResponse.fail(productId+":"+ResultEnums.NOT_EXITS.getMsg());
        }
        ProductInfo product = byId.get();
        //判断商品是否为下架商品
        if(product.getProductStatus()==ResultEnums.PRODUCT_DOWN.getCode()){
            return ResultResponse.fail(ResultEnums.PRODUCT_DOWN.getMsg());
        }
        return ResultResponse.success(product);
    }

    @Override
    @Transactional
    public void updateProduct(ProductInfo productInfo) {

        productInfoRepository.save(productInfo);
    }
}
