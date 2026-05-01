package com.business.expensetracker.dto.response;

import com.business.expensetracker.model.Category;

/**
 * Response DTO for a category.
 * Requirements: 3.1, 3.2, 3.4
 */
public record CategoryDto(
        Long id,
        String name,
        boolean isDefault
) {
    public static CategoryDto from(Category category) {
        return new CategoryDto(category.getId(), category.getName(), category.isDefault());
    }
}
