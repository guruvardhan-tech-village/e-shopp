package com.business.expensetracker.dto.request;

import com.business.expensetracker.model.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request DTO for creating or updating an expense.
 * Requirements: 2.1, 2.2, 2.3
 */
public record ExpenseRequest(

        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be a positive number")
        BigDecimal amount,

        @NotNull(message = "Date is required")
        LocalDate date,

        @NotNull(message = "Category is required")
        Long categoryId,

        @NotBlank(message = "Description is required")
        String description,

        // Optional — defaults to OTHER if omitted
        PaymentMethod paymentMethod
) {}
