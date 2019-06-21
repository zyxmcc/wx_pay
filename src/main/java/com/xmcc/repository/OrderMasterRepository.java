package com.xmcc.repository;

import com.xmcc.entity.OrderMaster;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 订单的dao层开发
 */
public interface OrderMasterRepository extends JpaRepository<OrderMaster,String> {
}
