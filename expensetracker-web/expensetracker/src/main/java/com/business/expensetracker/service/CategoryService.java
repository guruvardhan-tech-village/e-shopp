package com.business.expensetracker.service;

import com.business.expensetracker.dto.response.CategoryDto;
import com.business.expensetracker.exception.AccessDeniedException;
import com.business.expensetracker.exception.DuplicateResourceException;
import com.business.expensetracker.exception.ResourceNotFoundException;
import com.business.expensetracker.model.Category;
import com.business.expensetracker.repository.CategoryRepository;
import com.business.expensetracker.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Business logic for category management.
 * Requirements: 3.1, 3.2, 3.3, 3.4, 3.5, 3.6, 3.7
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private static final String OTHER_CATEGORY_NAME = "Other";

    private final CategoryRepository categoryRepository;
    private final ExpenseRepository expenseRepository;

    /**
     * Returns the combined list of default categories and the user's custom categories.
     * Requirements: 3.1, 3.4
     */
    @Transactional(readOnly = true)
    public List<CategoryDto> getCategories(Long userId) {
        return categoryRepository.findByUserIdOrIsDefaultTrue(userId)
                .stream()
                .map(CategoryDto::from)
                .toList();
    }

    /**
     * Creates a new custom category for the user.
     * Throws DuplicateResourceException if a category with the same name already exists for this user.
     * Requirements: 3.2, 3.3
     */
    @Transactional
    public CategoryDto createCategory(Long userId, String name) {
        // Check for duplicate among user's own categories
        if (categoryRepository.findByNameAndUserId(name, userId).isPresent()) {
            throw new DuplicateResourceException(
                    "A category named '" + name + "' already exists");
        }
        // Also check against default category names to avoid confusion
        if (categoryRepository.findByNameAndIsDefaultTrue(name).isPresent()) {
            throw new DuplicateResourceException(
                    "A default category named '" + name + "' already exists");
        }

        Category category = Category.builder()
                .name(name)
                .isDefault(false)
                .userId(userId)
                .build();

        Category saved = categoryRepository.save(category);
        log.info("Created custom category id={} name='{}' for userId={}", saved.getId(), saved.getName(), userId);
        return CategoryDto.from(saved);
    }

    /**
     * Deletes a custom category belonging to the user.
     * - Throws AccessDeniedException if the category is a default category.
     * - Throws ResourceNotFoundException if the category does not exist.
     * - Reassigns all associated expenses to the "Other" default category before deleting.
     * Requirements: 3.5, 3.6, 3.7
     */
    @Transactional
    public void deleteCategory(Long userId, Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with id: " + categoryId));

        // Reject deletion of default categories (Requirement 3.6)
        if (category.isDefault()) {
            throw new AccessDeniedException("Default categories cannot be deleted");
        }

        // Reject deletion of categories belonging to another user (Requirement 9.2)
        if (!userId.equals(category.getUserId())) {
            throw new AccessDeniedException("You do not have permission to delete this category");
        }

        // Reassign all expenses in this category to "Other" (Requirement 3.7)
        Category otherCategory = categoryRepository.findByNameAndIsDefaultTrue(OTHER_CATEGORY_NAME)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Default 'Other' category not found — data integrity issue"));

        int reassigned = expenseRepository.reassignCategory(userId, categoryId, otherCategory.getId());
        log.info("Reassigned {} expense(s) from categoryId={} to 'Other' (id={}) for userId={}",
                reassigned, categoryId, otherCategory.getId(), userId);

        categoryRepository.delete(category);
        log.info("Deleted custom category id={} name='{}' for userId={}", categoryId, category.getName(), userId);
    }
}
