package com.business.expensetracker.dto.response;

import com.business.expensetracker.model.Expense;
import com.business.expensetracker.model.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Response DTO for a single expense record.
 * Requirements: 2.1, 2.4, 2.5, 2.7
 */
public record ExpenseDto(
        Long id,
        Long userId,
        Long categoryId,
        BigDecimal amount,
        LocalDate date,
        String description,
        PaymentMethod paymentMethod,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ExpenseDto from(Expense expense) {
        return new ExpenseDto(
                expense.getId(),
                expense.getUserId(),
                expense.getCategoryId(),
                expense.getAmount(),
                expense.getExpenseDate(),
                expense.getDescription(),
                expense.getPaymentMethod(),
                expense.getCreatedAt(),
                expense.getUpdatedAt()
        );
    }
}
