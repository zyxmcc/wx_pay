package com.xmcc.repository;

import com.xmcc.entity.ProductInfo;
import io.swagger.models.auth.In;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

//第一个参数 是实体类名称  第二个参数是主键类型
public interface ProductInfoRepository extends JpaRepository<ProductInfo,String> {

    /**
     * 根据商品类型的编号和商品的状态 查询商品信息
     * @param status 商品状态
     * @param categoryList 商品类型编号
     * @return
     */
    List<ProductInfo> findByProductStatusAndCategoryTypeIn(Integer status, List<Integer> categoryList);
}
