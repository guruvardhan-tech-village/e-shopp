package com.business.expensetracker.controller;

import com.business.expensetracker.dto.request.CategoryRequest;
import com.business.expensetracker.dto.response.ApiResponse;
import com.business.expensetracker.dto.response.CategoryDto;
import com.business.expensetracker.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for category management.
 *
 * <ul>
 *   <li>GET    /api/v1/categories        — list default + custom categories</li>
 *   <li>POST   /api/v1/categories        — create a custom category</li>
 *   <li>DELETE /api/v1/categories/{id}   — delete a custom category</li>
 * </ul>
 *
 * Requirements: 3.1–3.7, 10.1, 10.2
 */
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * Returns the combined list of default and user-specific custom categories.
     * Requirements: 3.1, 3.4
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryDto>>> getCategories(
            @AuthenticationPrincipal Long userId) {

        List<CategoryDto> categories = categoryService.getCategories(userId);
        return ResponseEntity.ok(ApiResponse.success("Categories retrieved successfully", categories));
    }

    /**
     * Creates a new custom category for the authenticated user.
     * Returns 201 Created on success, 409 Conflict on duplicate name.
     * Requirements: 3.2, 3.3
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CategoryDto>> createCategory(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody CategoryRequest request) {

        CategoryDto created = categoryService.createCategory(userId, request.name());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Category created successfully", created));
    }

    /**
     * Deletes a custom category. Reassigns associated expenses to "Other" first.
     * Returns 204 No Content on success.
     * Returns 403 Forbidden if the category is a default or belongs to another user.
     * Requirements: 3.5, 3.6, 3.7
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long id) {

        categoryService.deleteCategory(userId, id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.success("Category deleted successfully", null));
    }
}
