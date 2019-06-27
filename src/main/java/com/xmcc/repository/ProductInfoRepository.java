package com.xmcc.repository;

import com.xmcc.entity.ProductInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

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

    //当前的俩个注解适用于自定义sql语句 需在调用的地方添加事务 nativeQuery表示适用sql语句
    @Modifying
    @Query(value = "update product_info set product_stock=product_stock+?1 where product_id=?2",nativeQuery = true)
    int saveProductStock(Integer productQuantity,String productId);
}
