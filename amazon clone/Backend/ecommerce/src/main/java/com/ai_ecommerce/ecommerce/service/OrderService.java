package com.ai_ecommerce.ecommerce.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ai_ecommerce.ecommerce.dto.OrderItemRequest;
import com.ai_ecommerce.ecommerce.dto.OrderRequest;
import com.ai_ecommerce.ecommerce.model.OrderItem;
import com.ai_ecommerce.ecommerce.model.Orders;
import com.ai_ecommerce.ecommerce.model.Product;
import com.ai_ecommerce.ecommerce.model.User;
import com.ai_ecommerce.ecommerce.repository.OrderItemRepository;
import com.ai_ecommerce.ecommerce.repository.OrderRepository;
import com.ai_ecommerce.ecommerce.repository.ProductRepository;
import com.ai_ecommerce.ecommerce.repository.UserRepository;

@Service
public class OrderService {

    @Autowired
    private OrderRepository ordersRepo;

    @Autowired
    private OrderItemRepository orderItemRepo;

    @Autowired
    private ProductRepository productRepo;

    @Autowired
    private UserRepository userRepository;

    public List<Orders> getOrdersForUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ordersRepo.findByUserId(user.getId());
    }

    public Orders placeOrder(String email, OrderRequest request) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Orders order = new Orders();
        order.setUserId(user.getId());
        order.setTotalAmount(0);
        order.setStatus("PLACED");

        order = ordersRepo.save(order);

        double total = 0;

        for (OrderItemRequest itemReq : request.getItems()) {

            Product product = productRepo.findById(itemReq.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            OrderItem item = new OrderItem();
            item.setOrderId(order.getId());
            item.setProductId(product.getId());
            item.setQuantity(itemReq.getQuantity());
            item.setPrice(product.getPrice());

            orderItemRepo.save(item);

            total += product.getPrice() * itemReq.getQuantity();
        }

        order.setTotalAmount(total);
        return ordersRepo.save(order);
    }
}
