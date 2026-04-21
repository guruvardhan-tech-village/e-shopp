package com.ai_ecommerce.ecommerce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ai_ecommerce.ecommerce.dto.OrderRequest;
import com.ai_ecommerce.ecommerce.service.OrderService;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public String placeOrder(@RequestBody OrderRequest request) {
        orderService.placeOrder(request);
        return "Order Placed Successfully";
    }
    
}