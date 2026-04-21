package com.ai_ecommerce.ecommerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ai_ecommerce.ecommerce.model.Orders;

public interface OrdersRepository extends JpaRepository<Orders, Long> {
     List<Orders> findByUserId(Long userId);
}