package com.ai_ecommerce.ecommerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ai_ecommerce.ecommerce.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long>{
    
    List<Product> findByCompanyName(String companyName);
    List<Product> findByPriceBetween(double min, double max);
    List<Product> findByNameContainingIgnoreCase(String name);
    List<Product> findByCategory(String category);

}
