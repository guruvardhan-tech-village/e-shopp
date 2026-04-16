package com.ai_ecommerce.ecommerce.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.ai_ecommerce.ecommerce.dto.ProductDTO;
import com.ai_ecommerce.ecommerce.model.Products;
import com.ai_ecommerce.ecommerce.repository.ProductRepository;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    // ✅ ADD PRODUCT
    public Products addProducts(ProductDTO dto) {
        Products product = new Products();

        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setCompanyName(dto.getCompanyName());
        product.setCategory(dto.getCategory());
        product.setStatus(dto.getStatus());
        product.setQuantity(dto.getQuantity());
        product.setImageUrl(dto.getImageUrl());

        return productRepository.save(product);
    }

    // ✅ GET ALL
    public List<Products> getAllProducts() {
        return productRepository.findAll();
    }

    // ✅ GET BY ID
    public Products getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    // ✅ FILTER BY COMPANY
    public List<Products> findByCompanyName(String companyName) {
        return productRepository.findByCompanyName(companyName);
    }

    // ✅ FILTER BY PRICE
    public List<Products> findByPriceBetween(double min, double max) {
        return productRepository.findByPriceBetween(min, max);
    }

    // ✅ DELETE
    public boolean deleteProductById(Long id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // ✅ UPDATE
    public Products updateProduct(Long id, Products updatedProduct) {

        return productRepository.findById(id).map(existing -> {

            existing.setName(updatedProduct.getName());
            existing.setDescription(updatedProduct.getDescription());
            existing.setPrice(updatedProduct.getPrice());
            existing.setCompanyName(updatedProduct.getCompanyName());
            existing.setCategory(updatedProduct.getCategory());
            existing.setStatus(updatedProduct.getStatus());
            existing.setQuantity(updatedProduct.getQuantity());
            existing.setImageUrl(updatedProduct.getImageUrl());

            return productRepository.save(existing);

        }).orElseThrow(() -> new RuntimeException("Product not found"));
    }

    // ✅ PAGINATION
    public Page<Products> getProductsWithPagination(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findAll(pageable);
    }

    // ✅ PAGINATION + SORT
    public Page<Products> getProductsWithPaginationAndSort(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return productRepository.findAll(pageable);
    }

    // ✅ SEARCH
    public List<Products> searchProducts(String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword);
    }

    // ✅ RECOMMEND
    public List<Products> recommendProducts(String category) {
        return productRepository.findByCategory(category);
    }

    // ✅ SUGGESTIONS
    public List<String> getSuggestions(String keyword) {

        String lowerKeyword = keyword.toLowerCase();

        if (lowerKeyword.equals("ph")) lowerKeyword = "phone";

        final String finalKeyword = lowerKeyword;

        return productRepository.findAll()
                .stream()
                .flatMap(p -> List.of(
                        p.getName(),
                        p.getCategory(),
                        p.getCompanyName()
                ).stream())
                .filter(text -> text != null && text.toLowerCase().contains(finalKeyword))
                .distinct()
                .limit(5)
                .toList();
    }
}