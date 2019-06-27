package com.xmcc.repository;

import com.xmcc.entity.OrderMaster;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 订单的dao层开发
 */
public interface OrderMasterRepository extends JpaRepository<OrderMaster,String> {
    //分页查询
    Page<OrderMaster> findByBuyerOpenid(String openId,Pageable pageable);

    //查询订单
    OrderMaster findByOrderIdAndBuyerOpenid(String orderId,String openid);
}
