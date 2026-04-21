package com.ai_ecommerce.ecommerce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ai_ecommerce.ecommerce.dto.OrderRequest;
import com.ai_ecommerce.ecommerce.model.Orders;
import com.ai_ecommerce.ecommerce.model.User;
import com.ai_ecommerce.ecommerce.repository.OrdersRepository;
import com.ai_ecommerce.ecommerce.repository.UserRepository;
import com.ai_ecommerce.ecommerce.service.OrderService;


@RestController
@RequestMapping("/api/orders")
@CrossOrigin
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrdersRepository orderRepository;

    // @PostMapping
    // public String placeOrder(@RequestBody OrderRequest request) {
    //     orderService.placeOrder(request);
    //     return "Order Placed Successfully";
    // }

    @PostMapping
    public ResponseEntity<?> placeOrder(
            @RequestBody OrderRequest request,
            Authentication authentication
    ) {
        String email = authentication.getName(); // 🔥 comes from JWT

        Orders order = orderService.placeOrder(email, request);
        return ResponseEntity.ok(order);
    }
    @GetMapping
    public ResponseEntity<?> getMyOrders(Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(orderRepository.findByUserId(user.getId()));
    }
    
}