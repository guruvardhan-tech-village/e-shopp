package com.ai_ecommerce.ecommerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ai_ecommerce.ecommerce.model.Products;

public interface ProductRepository extends JpaRepository<Products, Long>{
    
    List<Products> findByCompanyName(String companyName);
    List<Products> findByPriceBetween(double min, double max);
    List<Products> findByNameContainingIgnoreCase(String name);
    List<Products> findByCategory(String category);

}
