package com.business.expensetracker.repository;

import com.business.expensetracker.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Returns all categories that belong to the given user OR are default categories
     * (isDefault = true, userId = null). Used to build the combined category list
     * shown to a user (Requirement 3.4).
     */
    List<Category> findByUserIdOrIsDefaultTrue(Long userId);

    /**
     * Looks up a category by name scoped to a specific user. Used to detect
     * duplicate custom category names (Requirement 3.3).
     */
    Optional<Category> findByNameAndUserId(String name, Long userId);

    /**
     * Finds the "Other" default category by name. Used when reassigning expenses
     * after a custom category is deleted (Requirement 3.7).
     */
    Optional<Category> findByNameAndIsDefaultTrue(String name);
}
