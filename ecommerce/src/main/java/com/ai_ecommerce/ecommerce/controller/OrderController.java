package com.ai_ecommerce.ecommerce.controller;

import java.util.ArrayList;
import java.util.HashMap;

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
import com.ai_ecommerce.ecommerce.model.OrderItem;
import com.ai_ecommerce.ecommerce.model.Orders;
import com.ai_ecommerce.ecommerce.model.User;
import com.ai_ecommerce.ecommerce.model.Product;
import com.ai_ecommerce.ecommerce.service.OrderService;
import com.ai_ecommerce.ecommerce.repository.UserRepository;
import com.ai_ecommerce.ecommerce.repository.OrderRepository;
import com.ai_ecommerce.ecommerce.repository.OrderItemRepository;
import com.ai_ecommerce.ecommerce.repository.ProductRepository;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/orders")
@CrossOrigin
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductRepository productRepository;

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

        List<Orders> orders = orderRepository.findByUserId(user.getId());

        List<Map<String, Object>> response = new ArrayList<>();

        for (Orders order : orders) {

            Map<String, Object> orderMap = new HashMap<>();
            orderMap.put("id", order.getId());
            orderMap.put("status", order.getStatus());
            orderMap.put("total", order.getTotalAmount());

            List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());

            List<Map<String, Object>> itemList = new ArrayList<>();

            for (OrderItem item : items) {
                Product product = productRepository.findById(item.getProductId()).get();

                Map<String, Object> itemMap = new HashMap<>();
                itemMap.put("name", product.getName());
                itemMap.put("image", product.getImageUrl());
                itemMap.put("price", item.getPrice());
                itemMap.put("qty", item.getQuantity());

                itemList.add(itemMap);
            }

            orderMap.put("items", itemList);

            response.add(orderMap);
        }

        return ResponseEntity.ok(response);
    }
    
}
