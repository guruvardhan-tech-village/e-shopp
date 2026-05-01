package com.business.expensetracker.repository;

import com.business.expensetracker.model.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

    /**
     * Returns all budgets for a user in a given month and year.
     * Used to build the budget status list (Requirement 6.3).
     */
    List<Budget> findByUserIdAndMonthAndYear(Long userId, int month, int year);

    /**
     * Looks up a specific budget by user, category, month, and year.
     * Used to detect duplicate budgets on create (Requirement 6.2) and
     * to retrieve a single budget for update/delete ownership checks.
     */
    Optional<Budget> findByUserIdAndCategoryIdAndMonthAndYear(
            Long userId, Long categoryId, int month, int year);
}
