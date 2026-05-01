package com.business.expensetracker.repository;

import com.business.expensetracker.model.Expense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    /**
     * Returns all expenses for a user ordered by date descending.
     * Used for the default expense list (Requirement 2.4).
     */
    List<Expense> findByUserIdOrderByExpenseDateDesc(Long userId);

    /**
     * Flexible filtered query supporting all combinations of optional filters:
     * date range, category, keyword (description), and amount range.
     * All filter parameters are optional — pass null to skip a filter.
     * Results are scoped to the authenticated user and support pagination (Requirements 4.1–4.6).
     *
     * @param userId      the authenticated user's ID (always required)
     * @param startDate   inclusive lower bound on expenseDate (nullable)
     * @param endDate     inclusive upper bound on expenseDate (nullable)
     * @param categoryId  exact category match (nullable)
     * @param keyword     case-insensitive substring match on description (nullable)
     * @param minAmount   inclusive lower bound on amount (nullable)
     * @param maxAmount   inclusive upper bound on amount (nullable)
     * @param pageable    pagination and sort instructions
     */
    @Query("""
            SELECT e FROM Expense e
            WHERE e.userId = :userId
              AND (:startDate   IS NULL OR e.expenseDate >= :startDate)
              AND (:endDate     IS NULL OR e.expenseDate <= :endDate)
              AND (:categoryId  IS NULL OR e.categoryId  = :categoryId)
              AND (:keyword     IS NULL OR LOWER(e.description) LIKE LOWER(CONCAT('%', :keyword, '%')))
              AND (:minAmount   IS NULL OR e.amount >= :minAmount)
              AND (:maxAmount   IS NULL OR e.amount <= :maxAmount)
            """)
    Page<Expense> findByFilters(
            @Param("userId")     Long userId,
            @Param("startDate")  LocalDate startDate,
            @Param("endDate")    LocalDate endDate,
            @Param("categoryId") Long categoryId,
            @Param("keyword")    String keyword,
            @Param("minAmount")  BigDecimal minAmount,
            @Param("maxAmount")  BigDecimal maxAmount,
            Pageable pageable
    );

    /**
     * Returns all expenses for a user within an optional date range, used by the
     * CSV export endpoint (Requirement 8.1, 8.2).
     */
    @Query("""
            SELECT e FROM Expense e
            WHERE e.userId = :userId
              AND (:startDate IS NULL OR e.expenseDate >= :startDate)
              AND (:endDate   IS NULL OR e.expenseDate <= :endDate)
            ORDER BY e.expenseDate DESC
            """)
    List<Expense> findByUserIdAndDateRange(
            @Param("userId")    Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate")   LocalDate endDate
    );

    /**
     * Returns all expenses for a user in a given month and year.
     * Used by the summary and budget services (Requirements 5.1, 6.3).
     */
    @Query("""
            SELECT e FROM Expense e
            WHERE e.userId = :userId
              AND FUNCTION('MONTH', e.expenseDate) = :month
              AND FUNCTION('YEAR',  e.expenseDate) = :year
            """)
    List<Expense> findByUserIdAndMonthAndYear(
            @Param("userId") Long userId,
            @Param("month")  int month,
            @Param("year")   int year
    );

    /**
     * Returns all expenses for a user in a given year.
     * Used by the yearly summary service (Requirement 5.2).
     */
    @Query("""
            SELECT e FROM Expense e
            WHERE e.userId = :userId
              AND FUNCTION('YEAR', e.expenseDate) = :year
            """)
    List<Expense> findByUserIdAndYear(
            @Param("userId") Long userId,
            @Param("year")   int year
    );

    /**
     * Bulk-reassigns all expenses from one category to another.
     * Called before deleting a custom category (Requirement 3.7).
     */
    @Query("""
            UPDATE Expense e
            SET e.categoryId = :newCategoryId
            WHERE e.userId = :userId
              AND e.categoryId = :oldCategoryId
            """)
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.transaction.annotation.Transactional
    int reassignCategory(
            @Param("userId")        Long userId,
            @Param("oldCategoryId") Long oldCategoryId,
            @Param("newCategoryId") Long newCategoryId
    );
}
