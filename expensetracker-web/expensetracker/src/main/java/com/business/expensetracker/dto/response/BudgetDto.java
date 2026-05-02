package com.business.expensetracker.dto.response;

import com.business.expensetracker.model.Budget;
import com.business.expensetracker.model.Category;

import java.math.BigDecimal;

public record BudgetDto(
        Long id,
        CategoryDto category,
        int month,
        int year,
        BigDecimal limitAmount
) {
    public static BudgetDto from(Budget budget, Category category) {
        return new BudgetDto(
                budget.getId(),
                CategoryDto.from(category),
                budget.getMonth(),
                budget.getYear(),
                budget.getLimitAmount()
        );
    }
}
