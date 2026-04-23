package com.ai_ecommerce.ecommerce.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ai_ecommerce.ecommerce.model.Product;
import com.ai_ecommerce.ecommerce.repository.ProductRepository;

@Service
public class AISearchService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> smartSearch(String query) {

        String lowerQuery = query.toLowerCase();
        String[] words = lowerQuery.split(" ");

        double minPrice = 0;
        double maxPrice = Double.MAX_VALUE;

        // 🎯 Price extraction
        for (int i = 0; i < words.length; i++) {
            if (words[i].equals("under") && i + 1 < words.length) {
                try {
                    maxPrice = Double.parseDouble(words[i + 1]);
                } catch (NumberFormatException ignored) {}
            }

            if (words[i].equals("above") && i + 1 < words.length) {
                try {
                    minPrice = Double.parseDouble(words[i + 1]);
                } catch (NumberFormatException ignored) {}
            }
        }

        // modify values
        if (lowerQuery.contains("cheap")) maxPrice = 10000;
        if (lowerQuery.contains("expensive")) minPrice = 50000;

        final double finalMinPrice = minPrice;
        final double finalMaxPrice = maxPrice;

        // 🎯 Extract keywords
        List<String> keywords = new ArrayList<>();

        for (String word : words) {
            if (!word.matches("\\d+") &&
                !word.equals("under") &&
                !word.equals("above") &&
                !word.equals("cheap") &&
                !word.equals("expensive")) {

                keywords.add(word);
            }
        }

        // Synonyms
        if (keywords.contains("mobile") || keywords.contains("mobiles")) {
            keywords.add("phone");
        }

        List<Product> products = productRepository.findAll();

        return products.stream()
                .filter(p -> p.getName() != null && p.getCategory() != null)
                .filter(p -> {

                    boolean keywordMatch = keywords.isEmpty() || keywords.stream()
                            .anyMatch(k ->
                                    p.getName().toLowerCase().contains(k) ||
                                    p.getCategory().toLowerCase().contains(k) ||
                                    p.getCompanyName().toLowerCase().contains(k)
                            );

                    boolean priceMatch = p.getPrice() >= finalMinPrice && p.getPrice() <= finalMaxPrice;

                    return keywordMatch && priceMatch;
                })
                .toList();
    }
}