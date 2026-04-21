package com.ai_ecommerce.ecommerce.repository;

import com.ai_ecommerce.ecommerce.model.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdersRepository extends JpaRepository<Orders, Long> {
}