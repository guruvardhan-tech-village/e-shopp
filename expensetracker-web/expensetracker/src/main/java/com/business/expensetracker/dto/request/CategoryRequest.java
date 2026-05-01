package com.business.expensetracker.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * Request DTO for creating a custom category.
 * Requirements: 3.2
 */
public record CategoryRequest(
        @NotBlank(message = "Category name is required")
        String name
) {}
