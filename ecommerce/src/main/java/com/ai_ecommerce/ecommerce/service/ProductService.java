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

    public String addProducts(ProductDTO productDTO) {
        Products products = new Products();

        products.setName(productDTO.getName());
        products.setDescription(productDTO.getDescription());
        products.setPrice(productDTO.getPrice());
        products.setCompanyName(productDTO.getCompanyName());
        products.setCategory(productDTO.getCategory());
        products.setStatus(productDTO.getStatus());
        products.setQuantity(productDTO.getQuantity());

        productRepository.save(products);
        return """
               Product added successfully
               Name: """+products.getName()+
        "\nDescription: "+products.getDescription()+
        "\nPrice: "+products.getPrice()+
        "\nCompany Name: "+products.getCompanyName()+
        "\nStatus: "+products.getStatus()+
        "\nQuantity: "+products.getQuantity();
    }

    public List<Products> getAllProducts() {
        return productRepository.findAll();
    }
    public List<Products> findByCompanyName(String companyName) {
        return productRepository.findByCompanyName(companyName);
    }
    public List<Products> findByPriceBetween(double min, double max){
        return productRepository.findByPriceBetween(min, max);
    }
    public String deleteProductById(Long id) {
        if(productRepository.existsById(id)){
            productRepository.deleteById(id);
            return "Product with ID "+id+" deleted successfully.";
        } else {
            return "Product with ID "+id+" not found.";
        }
    }

    public Page<Products> getProductsWithPagination(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findAll(pageable);
    }
    public Page<Products> getProductsWithPaginationAndSort(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return productRepository.findAll(pageable);
    }
    public String updateProduct(Long id, Products products) {
        return productRepository.findById(id).map(existingProduct -> {
            existingProduct.setName(products.getName());
            existingProduct.setDescription(products.getDescription());
            existingProduct.setPrice(products.getPrice());
            existingProduct.setCompanyName(products.getCompanyName());
            existingProduct.setCategory(products.getCategory());
            existingProduct.setStatus(products.getStatus());
            existingProduct.setQuantity(products.getQuantity());
            productRepository.save(existingProduct);
            return "Product with ID "+id+" updated successfully.";
        }).orElse("Product with ID "+id+" not found.");
    }

    public List<Products> searchProducts(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }
    public List<Products> recommendProducts(String category) {
        return productRepository.findByCategory(category);
    }
    
    public List<String> getSuggestions(String keyword) {

        String lowerKeyword = keyword.toLowerCase();

        // 🔥 Smart mapping
        if (lowerKeyword.equals("ph")) lowerKeyword = "phone";

        final String finalLowerKeyword = lowerKeyword;
        return productRepository.findAll()
            .stream()
            .map(p -> List.of(
                p.getName(),
                p.getCategory(),
                p.getCompanyName()
            ))
            .flatMap(List::stream)
            .filter(text -> text != null && text.toLowerCase().contains(finalLowerKeyword))
            .distinct()
            .sorted((a, b) -> {
                boolean aStarts = a.toLowerCase().startsWith(finalLowerKeyword);
                boolean bStarts = b.toLowerCase().startsWith(finalLowerKeyword);
                return Boolean.compare(bStarts, aStarts);
            })
            .limit(5)
            .toList();
    }
}
