package com.ai_ecommerce.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ai_ecommerce.ecommerce.model.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}