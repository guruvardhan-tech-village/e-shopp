package com.ai_ecommerce.ecommerce.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ai_ecommerce.ecommerce.dto.ProductDTO;
import com.ai_ecommerce.ecommerce.model.Products;
import com.ai_ecommerce.ecommerce.response.ApiResponse;
import com.ai_ecommerce.ecommerce.service.AISearchService;
import com.ai_ecommerce.ecommerce.service.ProductService;

import jakarta.validation.Valid;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/products")
public class ProductController {
    
    private final ProductService productService;

    private final AISearchService aiSearchService;

    public ProductController(ProductService productService, AISearchService aiSearchService) {
        this.productService = productService;
        this.aiSearchService = aiSearchService;
    }
    @PostMapping
    public ApiResponse addProduct(@Valid @RequestBody ProductDTO dto){
        String result = productService.addProducts(dto);
        return new ApiResponse("Product added successfully", result, 201);
    }

    // @PostMapping
    // public String addProduct(@Valid @RequestBody ProductDTO dto){
    //     return productService.addProducts(dto);
    // }

    @PostMapping("/bulk")
    public ApiResponse addProducts(@RequestBody List<ProductDTO> dtos) {

        dtos.forEach(productService::addProducts);

        return new ApiResponse("Bulk products added", dtos.size(), 201);
    }

    // @GetMapping("/getAllProducts")
    // public List<Products> getAllproducts(){
    //     return productService.getAllProducts();
    // }
    @GetMapping
    public ApiResponse getAllProducts() {
        return new ApiResponse(
            "Products fetched",
            productService.getAllProducts(),
            200
        );
    }
    @GetMapping("/company")
    public List<Products> getProductsByCompanyName(@RequestParam String companyName){
        return productService.findByCompanyName(companyName);
    }
    @GetMapping("/price-range")
    public List<Products> getProductsByPriceRange(@RequestParam double min, @RequestParam double max){
        return productService.findByPriceBetween(min, max);
    }
    @DeleteMapping("/{id}")
    public String deleteProductById(@PathVariable Long id) {
        return productService.deleteProductById(id);
    }
    @GetMapping("/page")
    public Page<Products> getProductsWithPagination(@RequestParam int page, @RequestParam int size) {
        return productService.getProductsWithPagination(page, size);
    }
    @GetMapping("/page-sort")
    public Page<Products> getProductsWithPaginationAndSort(@RequestParam int page, @RequestParam int size, @RequestParam String sortBy) {
        return productService.getProductsWithPaginationAndSort(page, size, sortBy);
    }
    @PutMapping("/{id}")
    public String updateProduct(
        @PathVariable Long id, 
        @RequestBody Products updatedProduct) {
        return productService.updateProduct(id, updatedProduct);
    }
    @GetMapping("/search")
    public ApiResponse search(@RequestParam String keyword) {
        return new ApiResponse(
            "Search results",
            productService.searchProducts(keyword),
            200
        );
    }
    @GetMapping("/recommend")
    public ApiResponse searchByCategory(@RequestParam String category) {
        return new ApiResponse(
            "Recommended products"+category,
            productService.recommendProducts(category),
            200
        );
    }
    @GetMapping("/suggest")
    public ApiResponse getSuggestions(@RequestParam String keyword) {
        return new ApiResponse(
            "Suggestions",
            productService.getSuggestions(keyword),
            200
        );
    }
    @GetMapping("/ai-search")
    public ApiResponse aiSearch(@RequestParam String query) {
        return new ApiResponse(
            "AI search results",
            aiSearchService.smartSearch(query),
            200
        );
    }
}
