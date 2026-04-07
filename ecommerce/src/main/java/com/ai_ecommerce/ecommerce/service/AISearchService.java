package com.ai_ecommerce.ecommerce.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ai_ecommerce.ecommerce.model.Products;
import com.ai_ecommerce.ecommerce.repository.ProductRepository;

@Service
public class AISearchService {

    @Autowired
    private ProductRepository productRepository;

    public List<Products> smartSearch(String query) {

        String lowerQuery = query.toLowerCase();
        String[] words = lowerQuery.split(" ");

        double[] priceRange = {0, Double.MAX_VALUE};

        if (lowerQuery.contains("cheap")) priceRange[1] = 10000;
        if (lowerQuery.contains("expensive")) priceRange[0] = 50000;

        for (int i = 0; i < words.length; i++) {
            if (words[i].equals("under") && i + 1 < words.length) {
                try {
                    priceRange[1] = Double.parseDouble(words[i + 1]);
                } catch (Exception e) {}
            }

            if (words[i].equals("above") && i + 1 < words.length) {
                try {
                    priceRange[0] = Double.parseDouble(words[i + 1]);
                } catch (Exception e) {}
            }
        }

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

        if (keywords.contains("mobile") || keywords.contains("mobiles")) {
            keywords.add("phone");
        }

        return productRepository.findAll()
            .stream()
            .filter(p -> {

                boolean matchesKeyword = keywords.isEmpty() || keywords.stream()
                .allMatch(k -> 
                    p.getName().toLowerCase().contains(k) ||
                    p.getCategory().toLowerCase().contains(k) ||
                    p.getCompanyName().toLowerCase().contains(k)
                );

                boolean matchesPrice = p.getPrice() >= priceRange[0] && p.getPrice() <= priceRange[1];

                return matchesKeyword && matchesPrice;
            })
            .sorted((p1, p2) -> {
                int score1 = calculateScore(p1, keywords);
                int score2 = calculateScore(p2, keywords);
                return Integer.compare(score2, score1);
            })
            .toList();
    }

    private int calculateScore(Products p, List<String> keywords) {
        int score = 0;

        for (String k : keywords) {
            if (p.getName().toLowerCase().contains(k)) score += 3;
            if (p.getCategory().toLowerCase().contains(k)) score += 2;
            if (p.getCompanyName().toLowerCase().contains(k)) score += 1;
        }

        return score;
    }
}