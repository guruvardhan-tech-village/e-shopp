package com.business.expensetracker.service;

import com.business.expensetracker.dto.request.ExpenseRequest;
import com.business.expensetracker.dto.response.ExpenseDto;
import com.business.expensetracker.dto.response.PagedExpenseResponse;
import com.business.expensetracker.exception.AccessDeniedException;
import com.business.expensetracker.exception.ResourceNotFoundException;
import com.business.expensetracker.model.Expense;
import com.business.expensetracker.model.PaymentMethod;
import com.business.expensetracker.repository.CategoryRepository;
import com.business.expensetracker.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Business logic for expense CRUD operations.
 * Requirements: 2.1–2.10, 4.1–4.7, 9.1, 9.2
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;

    // -------------------------------------------------------------------------
    // Create
    // -------------------------------------------------------------------------

    /**
     * Creates a new expense for the authenticated user.
     * Validates that the referenced category exists and is accessible to the user
     * (either a default category or one owned by the user).
     * Requirements: 2.1, 2.2, 2.3, 9.1
     */
    @Transactional
    public ExpenseDto createExpense(Long userId, ExpenseRequest request) {
        // Validate that the category exists and is accessible to this user
        validateCategoryAccess(userId, request.categoryId());

        Expense expense = Expense.builder()
                .userId(userId)
                .categoryId(request.categoryId())
                .amount(request.amount())
                .expenseDate(request.date())
                .description(request.description())
                .paymentMethod(request.paymentMethod() != null
                        ? request.paymentMethod()
                        : PaymentMethod.OTHER)
                .build();

        Expense saved = expenseRepository.save(expense);
        log.info("Created expense id={} for userId={}", saved.getId(), userId);
        return ExpenseDto.from(saved);
    }

    // -------------------------------------------------------------------------
    // Read
    // -------------------------------------------------------------------------

    /**
     * Returns a paginated, filtered list of expenses for the authenticated user.
     * All filter parameters are optional — pass null to skip a filter.
     * Results are always scoped to the authenticated user and ordered by date descending.
     * Requirements: 2.4, 4.1–4.6, 9.1
     */
    @Transactional(readOnly = true)
    public PagedExpenseResponse getExpenses(
            Long userId,
            LocalDate startDate,
            LocalDate endDate,
            Long categoryId,
            String keyword,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            int page,
            int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "expenseDate"));

        Page<Expense> expensePage = expenseRepository.findByFilters(
                userId, startDate, endDate, categoryId, keyword, minAmount, maxAmount, pageable);

        List<ExpenseDto> expenses = expensePage.getContent()
                .stream()
                .map(ExpenseDto::from)
                .toList();

        return new PagedExpenseResponse(
                expenses,
                expensePage.getTotalElements(),
                expensePage.getTotalPages(),
                expensePage.getNumber()
        );
    }

    /**
     * Returns a single expense by ID, scoped to the authenticated user.
     * Throws ResourceNotFoundException if the expense does not exist.
     * Throws AccessDeniedException if the expense belongs to a different user.
     * Requirements: 2.5, 2.6, 9.1, 9.2
     */
    @Transactional(readOnly = true)
    public ExpenseDto getExpenseById(Long userId, Long expenseId) {
        Expense expense = findExpenseAndCheckOwnership(userId, expenseId);
        return ExpenseDto.from(expense);
    }

    // -------------------------------------------------------------------------
    // Update
    // -------------------------------------------------------------------------

    /**
     * Updates an existing expense belonging to the authenticated user.
     * Validates category access before persisting.
     * Requirements: 2.7, 9.1, 9.2
     */
    @Transactional
    public ExpenseDto updateExpense(Long userId, Long expenseId, ExpenseRequest request) {
        Expense expense = findExpenseAndCheckOwnership(userId, expenseId);

        // Validate that the new category is accessible to this user
        validateCategoryAccess(userId, request.categoryId());

        expense.setCategoryId(request.categoryId());
        expense.setAmount(request.amount());
        expense.setExpenseDate(request.date());
        expense.setDescription(request.description());
        expense.setPaymentMethod(request.paymentMethod() != null
                ? request.paymentMethod()
                : PaymentMethod.OTHER);

        Expense updated = expenseRepository.save(expense);
        log.info("Updated expense id={} for userId={}", expenseId, userId);
        return ExpenseDto.from(updated);
    }

    // -------------------------------------------------------------------------
    // Delete
    // -------------------------------------------------------------------------

    /**
     * Deletes an expense belonging to the authenticated user.
     * Requirements: 2.8, 2.9, 9.1, 9.2
     */
    @Transactional
    public void deleteExpense(Long userId, Long expenseId) {
        Expense expense = findExpenseAndCheckOwnership(userId, expenseId);
        expenseRepository.delete(expense);
        log.info("Deleted expense id={} for userId={}", expenseId, userId);
    }

    // -------------------------------------------------------------------------
    // CSV export helper
    // -------------------------------------------------------------------------

    /**
     * Returns all expenses for a user within an optional date range.
     * Used by the CSV export endpoint (Requirements 8.1, 8.2).
     */
    @Transactional(readOnly = true)
    public List<Expense> getExpensesForExport(Long userId, LocalDate startDate, LocalDate endDate) {
        return expenseRepository.findByUserIdAndDateRange(userId, startDate, endDate);
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /**
     * Loads an expense by ID and verifies it belongs to the given user.
     * Throws ResourceNotFoundException if not found.
     * Throws AccessDeniedException if owned by a different user (without revealing existence).
     */
    private Expense findExpenseAndCheckOwnership(Long userId, Long expenseId) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Expense not found with id: " + expenseId));

        if (!userId.equals(expense.getUserId())) {
            // Return 403 without revealing the resource exists (Requirement 9.2)
            throw new AccessDeniedException(
                    "You do not have permission to access this expense");
        }
        return expense;
    }

    /**
     * Validates that the given category is accessible to the user —
     * either a default category (userId = null) or one owned by the user.
     * Throws ResourceNotFoundException if the category does not exist.
     * Throws AccessDeniedException if the category belongs to a different user.
     */
    private void validateCategoryAccess(Long userId, Long categoryId) {
        var category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with id: " + categoryId));

        if (!category.isDefault() && !userId.equals(category.getUserId())) {
            throw new AccessDeniedException(
                    "You do not have permission to use this category");
        }
    }
}
