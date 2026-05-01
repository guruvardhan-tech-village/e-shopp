package com.business.expensetracker.repository;

import com.business.expensetracker.model.Budget;
import com.business.expensetracker.model.Category;
import com.business.expensetracker.model.Expense;
import com.business.expensetracker.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("ExpenseRepository Tests")
class ExpenseRepositoryTest {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BudgetRepository budgetRepository;

    @PersistenceContext
    private EntityManager entityManager;

    // Shared test data
    private User user1;
    private User user2;
    private Category foodCategory;
    private Category transportCategory;
    private Category emptyCategory;
    private Category defaultCategory;

    // user1 expenses
    private Expense expense1; // 2024-01-10, 10.00, "Coffee at Starbucks", food
    private Expense expense2; // 2024-01-20, 50.00, "Grocery shopping", food
    private Expense expense3; // 2024-02-05, 100.00, "Electric bill payment", transport
    private Expense expense4; // 2024-03-15, 200.00, "Monthly gym membership", transport

    // user2 expense
    private Expense expense5; // 2024-01-15, 75.00, "User2 expense", food

    @BeforeEach
    void setUp() {
        // Clear all data
        expenseRepository.deleteAll();
        budgetRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();

        // Create users
        user1 = userRepository.save(User.builder()
                .email("user1@example.com")
                .passwordHash("hash1")
                .displayName("User One")
                .build());

        user2 = userRepository.save(User.builder()
                .email("user2@example.com")
                .passwordHash("hash2")
                .displayName("User Two")
                .build());

        // Create categories
        foodCategory = categoryRepository.save(Category.builder()
                .name("Food")
                .isDefault(false)
                .userId(user1.getId())
                .build());

        transportCategory = categoryRepository.save(Category.builder()
                .name("Transport")
                .isDefault(false)
                .userId(user1.getId())
                .build());

        emptyCategory = categoryRepository.save(Category.builder()
                .name("EmptyCategory")
                .isDefault(false)
                .userId(user1.getId())
                .build());

        defaultCategory = categoryRepository.save(Category.builder()
                .name("Other")
                .isDefault(true)
                .userId(null)
                .build());

        // Create user1 expenses
        expense1 = expenseRepository.save(Expense.builder()
                .userId(user1.getId())
                .categoryId(foodCategory.getId())
                .amount(new BigDecimal("10.00"))
                .expenseDate(LocalDate.of(2024, 1, 10))
                .description("Coffee at Starbucks")
                .build());

        expense2 = expenseRepository.save(Expense.builder()
                .userId(user1.getId())
                .categoryId(foodCategory.getId())
                .amount(new BigDecimal("50.00"))
                .expenseDate(LocalDate.of(2024, 1, 20))
                .description("Grocery shopping")
                .build());

        expense3 = expenseRepository.save(Expense.builder()
                .userId(user1.getId())
                .categoryId(transportCategory.getId())
                .amount(new BigDecimal("100.00"))
                .expenseDate(LocalDate.of(2024, 2, 5))
                .description("Electric bill payment")
                .build());

        expense4 = expenseRepository.save(Expense.builder()
                .userId(user1.getId())
                .categoryId(transportCategory.getId())
                .amount(new BigDecimal("200.00"))
                .expenseDate(LocalDate.of(2024, 3, 15))
                .description("Monthly gym membership")
                .build());

        // Create user2 expense
        expense5 = expenseRepository.save(Expense.builder()
                .userId(user2.getId())
                .categoryId(foodCategory.getId())
                .amount(new BigDecimal("75.00"))
                .expenseDate(LocalDate.of(2024, 1, 15))
                .description("User2 expense")
                .build());
    }

    // =========================================================================
    // Group 1 — findByUserIdOrderByExpenseDateDesc
    // =========================================================================

    @Nested
    @DisplayName("Group 1: findByUserIdOrderByExpenseDateDesc")
    class FindByUserIdOrderByExpenseDateDescTests {

        @Test
        @DisplayName("Returns only expenses belonging to the given user")
        void returnsOnlyExpensesForGivenUser() {
            List<Expense> result = expenseRepository.findByUserIdOrderByExpenseDateDesc(user1.getId());

            assertThat(result).hasSize(4);
            assertThat(result).extracting(Expense::getUserId)
                    .containsOnly(user1.getId());
        }

        @Test
        @DisplayName("Results are ordered by expenseDate descending")
        void resultsAreOrderedByDateDescending() {
            List<Expense> result = expenseRepository.findByUserIdOrderByExpenseDateDesc(user1.getId());

            assertThat(result).hasSize(4);
            assertThat(result.get(0).getExpenseDate()).isEqualTo(LocalDate.of(2024, 3, 15));
            assertThat(result.get(1).getExpenseDate()).isEqualTo(LocalDate.of(2024, 2, 5));
            assertThat(result.get(2).getExpenseDate()).isEqualTo(LocalDate.of(2024, 1, 20));
            assertThat(result.get(3).getExpenseDate()).isEqualTo(LocalDate.of(2024, 1, 10));
        }
    }

    // =========================================================================
    // Group 2 — findByFilters: date range (Requirement 4.1)
    // =========================================================================

    @Nested
    @DisplayName("Group 2: findByFilters — date range (Req 4.1)")
    class DateRangeFilterTests {

        private final PageRequest unpaged = PageRequest.of(0, 100);

        @Test
        @DisplayName("startDate only — returns expenses on or after startDate")
        void startDateOnly() {
            Page<Expense> result = expenseRepository.findByFilters(
                    user1.getId(), LocalDate.of(2024, 2, 1), null, null, null, null, null, unpaged);

            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getContent()).extracting(Expense::getExpenseDate)
                    .allMatch(d -> !d.isBefore(LocalDate.of(2024, 2, 1)));
        }

        @Test
        @DisplayName("endDate only — returns expenses on or before endDate")
        void endDateOnly() {
            Page<Expense> result = expenseRepository.findByFilters(
                    user1.getId(), null, LocalDate.of(2024, 1, 31), null, null, null, null, unpaged);

            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getContent()).extracting(Expense::getExpenseDate)
                    .allMatch(d -> !d.isAfter(LocalDate.of(2024, 1, 31)));
        }

        @Test
        @DisplayName("Both startDate and endDate — returns only expenses within the inclusive range")
        void bothStartAndEndDate() {
            Page<Expense> result = expenseRepository.findByFilters(
                    user1.getId(),
                    LocalDate.of(2024, 1, 15),
                    LocalDate.of(2024, 2, 28),
                    null, null, null, null, unpaged);

            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getContent()).extracting(Expense::getExpenseDate)
                    .containsExactlyInAnyOrder(
                            LocalDate.of(2024, 1, 20),
                            LocalDate.of(2024, 2, 5));
        }

        @Test
        @DisplayName("Expenses exactly on boundary dates are included (inclusive)")
        void boundaryDatesAreInclusive() {
            // Use exact dates of expense1 and expense4 as boundaries
            Page<Expense> result = expenseRepository.findByFilters(
                    user1.getId(),
                    LocalDate.of(2024, 1, 10),
                    LocalDate.of(2024, 3, 15),
                    null, null, null, null, unpaged);

            assertThat(result.getContent()).hasSize(4);
            assertThat(result.getContent()).extracting(Expense::getId)
                    .containsExactlyInAnyOrder(
                            expense1.getId(), expense2.getId(),
                            expense3.getId(), expense4.getId());
        }

        @Test
        @DisplayName("No filters (all null) — returns all expenses for the user")
        void noFiltersReturnsAllUserExpenses() {
            Page<Expense> result = expenseRepository.findByFilters(
                    user1.getId(), null, null, null, null, null, null, unpaged);

            assertThat(result.getContent()).hasSize(4);
            assertThat(result.getContent()).extracting(Expense::getUserId)
                    .containsOnly(user1.getId());
        }
    }

    // =========================================================================
    // Group 3 — findByFilters: category filter (Requirement 4.2)
    // =========================================================================

    @Nested
    @DisplayName("Group 3: findByFilters — category filter (Req 4.2)")
    class CategoryFilterTests {

        private final PageRequest unpaged = PageRequest.of(0, 100);

        @Test
        @DisplayName("categoryId filter returns only expenses in that category")
        void categoryIdFilterReturnsMatchingExpenses() {
            Page<Expense> result = expenseRepository.findByFilters(
                    user1.getId(), null, null, foodCategory.getId(), null, null, null, unpaged);

            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getContent()).extracting(Expense::getCategoryId)
                    .containsOnly(foodCategory.getId());
        }

        @Test
        @DisplayName("categoryId for a category with no expenses returns empty page")
        void categoryWithNoExpensesReturnsEmpty() {
            Page<Expense> result = expenseRepository.findByFilters(
                    user1.getId(), null, null, emptyCategory.getId(), null, null, null, unpaged);

            assertThat(result.getContent()).isEmpty();
            assertThat(result.getTotalElements()).isZero();
        }
    }

    // =========================================================================
    // Group 4 — findByFilters: keyword filter (Requirement 4.3)
    // =========================================================================

    @Nested
    @DisplayName("Group 4: findByFilters — keyword filter (Req 4.3)")
    class KeywordFilterTests {

        private final PageRequest unpaged = PageRequest.of(0, 100);

        @Test
        @DisplayName("Keyword matches case-insensitively in description")
        void keywordMatchesCaseInsensitively() {
            // "COFFEE" should match "Coffee at Starbucks"
            Page<Expense> result = expenseRepository.findByFilters(
                    user1.getId(), null, null, null, "COFFEE", null, null, unpaged);

            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getId()).isEqualTo(expense1.getId());
        }

        @Test
        @DisplayName("Keyword with no matches returns empty page")
        void keywordWithNoMatchesReturnsEmpty() {
            Page<Expense> result = expenseRepository.findByFilters(
                    user1.getId(), null, null, null, "nonexistentxyz", null, null, unpaged);

            assertThat(result.getContent()).isEmpty();
        }

        @Test
        @DisplayName("Partial keyword match works")
        void partialKeywordMatchWorks() {
            // "bill" should match "Electric bill payment"
            Page<Expense> result = expenseRepository.findByFilters(
                    user1.getId(), null, null, null, "bill", null, null, unpaged);

            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getId()).isEqualTo(expense3.getId());
        }
    }

    // =========================================================================
    // Group 5 — findByFilters: amount range (Requirement 4.4)
    // =========================================================================

    @Nested
    @DisplayName("Group 5: findByFilters — amount range (Req 4.4)")
    class AmountRangeFilterTests {

        private final PageRequest unpaged = PageRequest.of(0, 100);

        @Test
        @DisplayName("minAmount only — returns expenses with amount >= minAmount")
        void minAmountOnly() {
            Page<Expense> result = expenseRepository.findByFilters(
                    user1.getId(), null, null, null, null, new BigDecimal("100.00"), null, unpaged);

            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getContent()).extracting(Expense::getAmount)
                    .allMatch(a -> a.compareTo(new BigDecimal("100.00")) >= 0);
        }

        @Test
        @DisplayName("maxAmount only — returns expenses with amount <= maxAmount")
        void maxAmountOnly() {
            Page<Expense> result = expenseRepository.findByFilters(
                    user1.getId(), null, null, null, null, null, new BigDecimal("50.00"), unpaged);

            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getContent()).extracting(Expense::getAmount)
                    .allMatch(a -> a.compareTo(new BigDecimal("50.00")) <= 0);
        }

        @Test
        @DisplayName("Both minAmount and maxAmount — returns only expenses within the inclusive range")
        void bothMinAndMaxAmount() {
            Page<Expense> result = expenseRepository.findByFilters(
                    user1.getId(), null, null, null, null,
                    new BigDecimal("50.00"), new BigDecimal("100.00"), unpaged);

            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getContent()).extracting(Expense::getAmount)
                    .allMatch(a -> a.compareTo(new BigDecimal("50.00")) >= 0
                            && a.compareTo(new BigDecimal("100.00")) <= 0);
        }

        @Test
        @DisplayName("Expenses exactly at boundary amounts are included (inclusive)")
        void boundaryAmountsAreInclusive() {
            // Exact boundaries: 10.00 and 200.00
            Page<Expense> result = expenseRepository.findByFilters(
                    user1.getId(), null, null, null, null,
                    new BigDecimal("10.00"), new BigDecimal("200.00"), unpaged);

            assertThat(result.getContent()).hasSize(4);
        }
    }

    // =========================================================================
    // Group 6 — findByFilters: pagination (Requirements 4.5, 4.6)
    // =========================================================================

    @Nested
    @DisplayName("Group 6: findByFilters — pagination (Req 4.5, 4.6)")
    class PaginationTests {

        @Test
        @DisplayName("Page 0 with size 2 returns first 2 results")
        void pageZeroSizeTwoReturnsFirstTwo() {
            PageRequest page0 = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "expenseDate"));
            Page<Expense> result = expenseRepository.findByFilters(
                    user1.getId(), null, null, null, null, null, null, page0);

            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getNumber()).isZero();
        }

        @Test
        @DisplayName("Page 1 with size 2 returns next 2 results")
        void pageOneSizeTwoReturnsNextTwo() {
            PageRequest page0 = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "expenseDate"));
            PageRequest page1 = PageRequest.of(1, 2, Sort.by(Sort.Direction.DESC, "expenseDate"));

            Page<Expense> firstPage = expenseRepository.findByFilters(
                    user1.getId(), null, null, null, null, null, null, page0);
            Page<Expense> secondPage = expenseRepository.findByFilters(
                    user1.getId(), null, null, null, null, null, null, page1);

            assertThat(secondPage.getContent()).hasSize(2);
            // Ensure no overlap between pages
            List<Long> firstIds = firstPage.getContent().stream().map(Expense::getId).toList();
            List<Long> secondIds = secondPage.getContent().stream().map(Expense::getId).toList();
            assertThat(firstIds).doesNotContainAnyElementsOf(secondIds);
        }

        @Test
        @DisplayName("totalElements equals total matching expenses")
        void totalElementsEqualsMatchingExpenses() {
            PageRequest page = PageRequest.of(0, 2);
            Page<Expense> result = expenseRepository.findByFilters(
                    user1.getId(), null, null, null, null, null, null, page);

            assertThat(result.getTotalElements()).isEqualTo(4L);
        }

        @Test
        @DisplayName("totalPages equals ceil(totalElements / pageSize)")
        void totalPagesEqualsExpectedValue() {
            PageRequest page = PageRequest.of(0, 2);
            Page<Expense> result = expenseRepository.findByFilters(
                    user1.getId(), null, null, null, null, null, null, page);

            // 4 expenses / 2 per page = 2 pages
            assertThat(result.getTotalPages()).isEqualTo(2);
        }

        @Test
        @DisplayName("currentPage equals the requested page index")
        void currentPageEqualsRequestedIndex() {
            PageRequest page1 = PageRequest.of(1, 2);
            Page<Expense> result = expenseRepository.findByFilters(
                    user1.getId(), null, null, null, null, null, null, page1);

            assertThat(result.getNumber()).isEqualTo(1);
        }

        @Test
        @DisplayName("Last page may have fewer elements than page size")
        void lastPageMayHaveFewerElements() {
            // 4 expenses, page size 3 → page 1 has 1 element
            PageRequest lastPage = PageRequest.of(1, 3);
            Page<Expense> result = expenseRepository.findByFilters(
                    user1.getId(), null, null, null, null, null, null, lastPage);

            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getTotalElements()).isEqualTo(4L);
        }
    }

    // =========================================================================
    // Group 7 — findByFilters: combined filters
    // =========================================================================

    @Nested
    @DisplayName("Group 7: findByFilters — combined filters")
    class CombinedFilterTests {

        private final PageRequest unpaged = PageRequest.of(0, 100);

        @Test
        @DisplayName("Date range + keyword combined filter works correctly")
        void dateRangeAndKeywordCombined() {
            // January only + "shopping" keyword → only expense2
            Page<Expense> result = expenseRepository.findByFilters(
                    user1.getId(),
                    LocalDate.of(2024, 1, 1),
                    LocalDate.of(2024, 1, 31),
                    null, "shopping", null, null, unpaged);

            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getId()).isEqualTo(expense2.getId());
        }

        @Test
        @DisplayName("Category + amount range combined filter works correctly")
        void categoryAndAmountRangeCombined() {
            // Transport category + amount >= 150 → only expense4 (200.00)
            Page<Expense> result = expenseRepository.findByFilters(
                    user1.getId(), null, null,
                    transportCategory.getId(), null,
                    new BigDecimal("150.00"), null, unpaged);

            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getId()).isEqualTo(expense4.getId());
        }
    }

    // =========================================================================
    // Group 8 — findByUserIdAndDateRange (CSV export)
    // =========================================================================

    @Nested
    @DisplayName("Group 8: findByUserIdAndDateRange (CSV export)")
    class DateRangeExportTests {

        @Test
        @DisplayName("Returns expenses within date range ordered by date desc")
        void returnsExpensesWithinDateRangeOrderedDesc() {
            List<Expense> result = expenseRepository.findByUserIdAndDateRange(
                    user1.getId(),
                    LocalDate.of(2024, 1, 1),
                    LocalDate.of(2024, 1, 31));

            assertThat(result).hasSize(2);
            // Ordered by date desc
            assertThat(result.get(0).getExpenseDate()).isEqualTo(LocalDate.of(2024, 1, 20));
            assertThat(result.get(1).getExpenseDate()).isEqualTo(LocalDate.of(2024, 1, 10));
        }

        @Test
        @DisplayName("Null startDate and endDate returns all user expenses")
        void nullDatesReturnsAllUserExpenses() {
            List<Expense> result = expenseRepository.findByUserIdAndDateRange(
                    user1.getId(), null, null);

            assertThat(result).hasSize(4);
            assertThat(result).extracting(Expense::getUserId)
                    .containsOnly(user1.getId());
        }
    }

    // =========================================================================
    // Group 9 — findByUserIdAndMonthAndYear
    // =========================================================================

    @Nested
    @DisplayName("Group 9: findByUserIdAndMonthAndYear")
    class MonthYearFilterTests {

        @Test
        @DisplayName("Returns only expenses in the specified month and year")
        void returnsExpensesInSpecifiedMonthAndYear() {
            List<Expense> result = expenseRepository.findByUserIdAndMonthAndYear(
                    user1.getId(), 1, 2024);

            assertThat(result).hasSize(2);
            assertThat(result).extracting(Expense::getId)
                    .containsExactlyInAnyOrder(expense1.getId(), expense2.getId());
        }

        @Test
        @DisplayName("Expenses in other months/years are excluded")
        void expensesInOtherMonthsAreExcluded() {
            List<Expense> result = expenseRepository.findByUserIdAndMonthAndYear(
                    user1.getId(), 2, 2024);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getId()).isEqualTo(expense3.getId());
        }
    }

    // =========================================================================
    // Group 10 — reassignCategory
    // =========================================================================

    @Nested
    @DisplayName("Group 10: reassignCategory")
    class ReassignCategoryTests {

        @Test
        @Transactional
        @DisplayName("All expenses with oldCategoryId are updated to newCategoryId")
        void reassignsExpensesFromOldToNewCategory() {
            expenseRepository.reassignCategory(
                    user1.getId(), foodCategory.getId(), transportCategory.getId());
            expenseRepository.flush();
            entityManager.clear();

            // Reload from DB to verify the update
            Expense reloaded1 = expenseRepository.findById(expense1.getId()).orElseThrow();
            Expense reloaded2 = expenseRepository.findById(expense2.getId()).orElseThrow();
            assertThat(reloaded1.getCategoryId()).isEqualTo(transportCategory.getId());
            assertThat(reloaded2.getCategoryId()).isEqualTo(transportCategory.getId());
        }

        @Test
        @Transactional
        @DisplayName("Expenses belonging to other users are not affected")
        void doesNotAffectOtherUsersExpenses() {
            expenseRepository.reassignCategory(
                    user1.getId(), foodCategory.getId(), transportCategory.getId());
            expenseRepository.flush();
            entityManager.clear();

            // user2's expense should still have foodCategory
            Optional<Expense> user2Expense = expenseRepository.findById(expense5.getId());
            assertThat(user2Expense).isPresent();
            assertThat(user2Expense.get().getCategoryId()).isEqualTo(foodCategory.getId());
        }
    }

    // =========================================================================
    // Group 11 — CategoryRepository
    // =========================================================================

    @Nested
    @DisplayName("Group 11: CategoryRepository")
    class CategoryRepositoryTests {

        @Test
        @DisplayName("findByUserIdOrIsDefaultTrue returns default categories + user's custom categories")
        void returnsDefaultAndUserCategories() {
            List<Category> result = categoryRepository.findByUserIdOrIsDefaultTrue(user1.getId());

            // Should include user1's categories (food, transport, empty) + default (Other)
            assertThat(result).hasSizeGreaterThanOrEqualTo(4);
            assertThat(result).extracting(Category::getName)
                    .contains("Food", "Transport", "EmptyCategory", "Other");
        }

        @Test
        @DisplayName("findByNameAndUserId finds a custom category by name and userId")
        void findsByNameAndUserId() {
            Optional<Category> result = categoryRepository.findByNameAndUserId("Food", user1.getId());

            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(foodCategory.getId());
        }

        @Test
        @DisplayName("findByNameAndIsDefaultTrue finds a default category by name")
        void findsByNameAndIsDefaultTrue() {
            Optional<Category> result = categoryRepository.findByNameAndIsDefaultTrue("Other");

            assertThat(result).isPresent();
            assertThat(result.get().isDefault()).isTrue();
            assertThat(result.get().getId()).isEqualTo(defaultCategory.getId());
        }
    }

    // =========================================================================
    // Group 12 — BudgetRepository
    // =========================================================================

    @Nested
    @DisplayName("Group 12: BudgetRepository")
    class BudgetRepositoryTests {

        private Budget budget1;
        private Budget budget2;

        @BeforeEach
        void setUpBudgets() {
            budget1 = budgetRepository.save(Budget.builder()
                    .userId(user1.getId())
                    .categoryId(foodCategory.getId())
                    .month(1)
                    .year(2024)
                    .limitAmount(new BigDecimal("300.00"))
                    .build());

            budget2 = budgetRepository.save(Budget.builder()
                    .userId(user1.getId())
                    .categoryId(transportCategory.getId())
                    .month(1)
                    .year(2024)
                    .limitAmount(new BigDecimal("500.00"))
                    .build());

            // Budget for different month
            budgetRepository.save(Budget.builder()
                    .userId(user1.getId())
                    .categoryId(foodCategory.getId())
                    .month(2)
                    .year(2024)
                    .limitAmount(new BigDecimal("250.00"))
                    .build());
        }

        @Test
        @DisplayName("findByUserIdAndMonthAndYear returns budgets for the correct user/month/year")
        void returnsCorrectBudgetsForMonthAndYear() {
            List<Budget> result = budgetRepository.findByUserIdAndMonthAndYear(
                    user1.getId(), 1, 2024);

            assertThat(result).hasSize(2);
            assertThat(result).extracting(Budget::getId)
                    .containsExactlyInAnyOrder(budget1.getId(), budget2.getId());
        }

        @Test
        @DisplayName("findByUserIdAndCategoryIdAndMonthAndYear returns the specific budget")
        void returnsSpecificBudget() {
            Optional<Budget> result = budgetRepository.findByUserIdAndCategoryIdAndMonthAndYear(
                    user1.getId(), foodCategory.getId(), 1, 2024);

            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(budget1.getId());
            assertThat(result.get().getLimitAmount()).isEqualByComparingTo(new BigDecimal("300.00"));
        }

        @Test
        @DisplayName("findByUserIdAndCategoryIdAndMonthAndYear returns empty for non-existent budget")
        void returnsEmptyForNonExistentBudget() {
            Optional<Budget> result = budgetRepository.findByUserIdAndCategoryIdAndMonthAndYear(
                    user1.getId(), emptyCategory.getId(), 1, 2024);

            assertThat(result).isEmpty();
        }
    }
}
