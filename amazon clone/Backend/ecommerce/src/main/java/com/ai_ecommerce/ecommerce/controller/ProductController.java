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
import com.ai_ecommerce.ecommerce.model.Product;
import com.ai_ecommerce.ecommerce.response.ApiResponse;
import com.ai_ecommerce.ecommerce.service.AISearchService;
import com.ai_ecommerce.ecommerce.service.ProductService;

import jakarta.validation.Valid;

@RestController
@CrossOrigin(origins = "http://localhost:5173") // FIXED
@RequestMapping("/api/products") // FIXED
public class ProductController {

    private final ProductService productService;
    private final AISearchService aiSearchService;

    public ProductController(ProductService productService, AISearchService aiSearchService) {
        this.productService = productService;
        this.aiSearchService = aiSearchService;
    }

    // ✅ ADD PRODUCT
    @PostMapping
    public ApiResponse addProduct(@Valid @RequestBody ProductDTO dto) {
        Product result = productService.addProducts(dto);
        return new ApiResponse("Product added successfully", result, 201);
    }

    // ✅ BULK ADD
    @PostMapping("/bulk")
    public ApiResponse addProducts(@RequestBody List<ProductDTO> dtos) {
        dtos.forEach(productService::addProducts);
        return new ApiResponse("Bulk products added", dtos.size(), 201);
    }

    // ✅ GET ALL PRODUCTS
    @GetMapping
    public ApiResponse getAllProducts() {
        return new ApiResponse(
                "Products fetched",
                productService.getAllProducts(),
                200
        );
    }

    // ✅ GET SINGLE PRODUCT (IMPORTANT - ADD THIS)
    @GetMapping("/{id}")
    public ApiResponse getProductById(@PathVariable Long id) {
        return new ApiResponse(
                "Product fetched",
                productService.getProductById(id),
                200
        );
    }

    // ✅ FILTER BY COMPANY
    @GetMapping("/company")
    public ApiResponse getProductsByCompanyName(@RequestParam String companyName) {
        return new ApiResponse(
                "Products by company",
                productService.findByCompanyName(companyName),
                200
        );
    }

    // ✅ FILTER BY PRICE
    @GetMapping("/price-range")
    public ApiResponse getProductsByPriceRange(@RequestParam double min,
                                               @RequestParam double max) {
        return new ApiResponse(
                "Products in range",
                productService.findByPriceBetween(min, max),
                200
        );
    }

    // ✅ DELETE
    @DeleteMapping("/{id}")
    public ApiResponse deleteProduct(@PathVariable Long id) {
        // Note: If deleteProductById returns void, move it outside the return statement
        // like: productService.deleteProductById(id);
        return new ApiResponse(
                "Product deleted successfully",
                productService.deleteProductById(id), // Can safely pass a returned String/Object here, or replace with null if it returns void
                200
        );
    }

    // ✅ UPDATE
    @PutMapping("/{id}")
    public ApiResponse updateProduct(@PathVariable Long id,
                                     @RequestBody Product updatedProduct) {
        return new ApiResponse(
                "Product updated successfully",
                productService.updateProduct(id, updatedProduct),
                200
        );
    }

    // ✅ PAGINATION
    @GetMapping("/page")
    public Page<Product> getProductsWithPagination(@RequestParam int page,
                                                    @RequestParam int size) {
        return productService.getProductsWithPagination(page, size);
    }

    // ✅ SORT + PAGINATION
    @GetMapping("/page-sort")
    public Page<Product> getProductsWithPaginationAndSort(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam String sortBy) {
        return productService.getProductsWithPaginationAndSort(page, size, sortBy);
    }

    // ✅ NORMAL SEARCH
    @GetMapping("/search")
    public ApiResponse search(@RequestParam String keyword) {
        return new ApiResponse(
                "Search results",
                productService.searchProducts(keyword),
                200
        );
    }

    // ✅ RECOMMEND
    @GetMapping("/recommend")
    public ApiResponse recommend(@RequestParam String category) {
        return new ApiResponse(
                "Recommended products",
                productService.recommendProducts(category),
                200
        );
    }

    // ✅ SUGGESTIONS
    @GetMapping("/suggest")
    public ApiResponse suggestions(@RequestParam String keyword) {
        return new ApiResponse(
                "Suggestions",
                productService.getSuggestions(keyword),
                200
        );
    }

    // ✅ AI SEARCH
    @GetMapping("/ai-search")
    public ApiResponse aiSearch(@RequestParam String query) {
        return new ApiResponse(
                "AI search results",
                aiSearchService.smartSearch(query),
                200
        );
    }
}