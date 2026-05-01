package com.business.expensetracker.dto.response;

import java.util.List;

/**
 * Paginated response wrapper for expense list queries.
 * Requirements: 4.5, 4.6
 */
public record PagedExpenseResponse(
        List<ExpenseDto> expenses,
        long totalElements,
        int totalPages,
        int currentPage
) {}
