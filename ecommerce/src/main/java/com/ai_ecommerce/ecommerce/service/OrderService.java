package com.ai_ecommerce.ecommerce.service;

import com.ai_ecommerce.ecommerce.dto.*;
import com.ai_ecommerce.ecommerce.model.*;
import com.ai_ecommerce.ecommerce.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    @Autowired
    private OrdersRepository ordersRepo;

    @Autowired
    private OrderItemRepository orderItemRepo;

    @Autowired
    private ProductRepository productRepo;

    @Autowired
    private UserRepository userRepo;

    public void placeOrder(OrderRequest request) {

        // ✅ Get user
        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ✅ Create Order
        Orders order = new Orders();
        order.setUserId(user.getId());
        order.setStatus("PLACED");

        double total = 0;

        Orders savedOrder = ordersRepo.save(order);

        // ✅ Save Order Items
        for (OrderItemRequest item : request.getItems()) {

            Products product = productRepo.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            OrderItem oi = new OrderItem();
            oi.setOrderId(savedOrder.getId());
            oi.setProductId(product.getId());
            oi.setQuantity(item.getQuantity());
            oi.setPrice(product.getPrice());

            total += product.getPrice() * item.getQuantity();

            orderItemRepo.save(oi);
        }

        // ✅ Update total
        savedOrder.setTotalAmount(total);
        ordersRepo.save(savedOrder);
    }
}