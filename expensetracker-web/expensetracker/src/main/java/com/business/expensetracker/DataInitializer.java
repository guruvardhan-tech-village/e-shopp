package com.business.expensetracker;

import com.business.expensetracker.model.Category;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Seeds the 8 default categories on application startup.
 * Skips seeding if default categories already exist to support idempotent restarts.
 */
@Component
public class DataInitializer implements ApplicationRunner {

    private static final List<String> DEFAULT_CATEGORY_NAMES = List.of(
            "Food",
            "Transport",
            "Utilities",
            "Entertainment",
            "Healthcare",
            "Shopping",
            "Education",
            "Other"
    );

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        TypedQuery<Long> countQuery = entityManager.createQuery(
                "SELECT COUNT(c) FROM Category c WHERE c.isDefault = true", Long.class);
        long existingDefaults = countQuery.getSingleResult();

        if (existingDefaults > 0) {
            return; // already seeded — nothing to do
        }

        for (String name : DEFAULT_CATEGORY_NAMES) {
            Category category = Category.builder()
                    .name(name)
                    .isDefault(true)
                    .userId(null)
                    .build();
            entityManager.persist(category);
        }
    }
}
