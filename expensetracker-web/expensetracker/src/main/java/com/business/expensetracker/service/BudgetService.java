package com.business.expensetracker.service;

import com.business.expensetracker.dto.request.BudgetRequest;
import com.business.expensetracker.dto.response.BudgetDto;
import com.business.expensetracker.exception.AccessDeniedException;
import com.business.expensetracker.exception.DuplicateResourceException;
import com.business.expensetracker.exception.ResourceNotFoundException;
import com.business.expensetracker.model.Budget;
import com.business.expensetracker.model.Category;
import com.business.expensetracker.repository.BudgetRepository;
import com.business.expensetracker.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public BudgetDto createBudget(Long userId, BudgetRequest request) {
        validateCategoryAccess(userId, request.getCategoryId());

        budgetRepository.findByUserIdAndCategoryIdAndMonthAndYear(
                userId, request.getCategoryId(), request.getMonth(), request.getYear()
        ).ifPresent(b -> {
            throw new DuplicateResourceException("A budget for this category, month, and year already exists.");
        });

        Budget budget = Budget.builder()
                .userId(userId)
                .categoryId(request.getCategoryId())
                .month(request.getMonth())
                .year(request.getYear())
                .limitAmount(request.getLimitAmount())
                .build();

        Budget saved = budgetRepository.save(budget);
        log.info("Created budget id={} for userId={}", saved.getId(), userId);
        return mapToDto(saved);
    }

    @Transactional(readOnly = true)
    public List<BudgetDto> getBudgets(Long userId, int month, int year) {
        return budgetRepository.findByUserIdAndMonthAndYear(userId, month, year)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public BudgetDto getBudgetById(Long userId, Long budgetId) {
        Budget budget = findBudgetAndCheckOwnership(userId, budgetId);
        return mapToDto(budget);
    }

    @Transactional
    public BudgetDto updateBudget(Long userId, Long budgetId, BudgetRequest request) {
        Budget budget = findBudgetAndCheckOwnership(userId, budgetId);

        if (!budget.getCategoryId().equals(request.getCategoryId()) || 
            budget.getMonth() != request.getMonth() || 
            budget.getYear() != request.getYear()) {
            
            budgetRepository.findByUserIdAndCategoryIdAndMonthAndYear(
                    userId, request.getCategoryId(), request.getMonth(), request.getYear()
            ).ifPresent(b -> {
                if (!b.getId().equals(budgetId)) {
                    throw new DuplicateResourceException("A budget for this category, month, and year already exists.");
                }
            });
        }

        validateCategoryAccess(userId, request.getCategoryId());

        budget.setCategoryId(request.getCategoryId());
        budget.setMonth(request.getMonth());
        budget.setYear(request.getYear());
        budget.setLimitAmount(request.getLimitAmount());

        Budget updated = budgetRepository.save(budget);
        log.info("Updated budget id={} for userId={}", budgetId, userId);
        return mapToDto(updated);
    }

    @Transactional
    public void deleteBudget(Long userId, Long budgetId) {
        Budget budget = findBudgetAndCheckOwnership(userId, budgetId);
        budgetRepository.delete(budget);
        log.info("Deleted budget id={} for userId={}", budgetId, userId);
    }

    private Budget findBudgetAndCheckOwnership(Long userId, Long budgetId) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found with id: " + budgetId));

        if (!userId.equals(budget.getUserId())) {
            throw new AccessDeniedException("You do not have permission to access this budget");
        }
        return budget;
    }

    private void validateCategoryAccess(Long userId, Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));

        if (!category.isDefault() && !userId.equals(category.getUserId())) {
            throw new AccessDeniedException("You do not have permission to use this category");
        }
    }

    private BudgetDto mapToDto(Budget budget) {
        Category category = categoryRepository.findById(budget.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found for budget"));
        return BudgetDto.from(budget, category);
    }
}
