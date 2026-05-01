# Implementation Plan: Expense Tracker

## Overview

Implement the full-stack Expense Tracker in two parts: the Spring Boot backend (Java 21, Spring Data JPA, PostgreSQL) and the React + Tailwind CSS frontend. Tasks are ordered so each step builds on the previous one, ending with full integration. The backend is an existing project at `expensetracker/`; the React app is created fresh alongside it.

---

## Tasks

- [x] 1. Configure backend dependencies and project foundation
  - Add missing Maven dependencies to `pom.xml`: `spring-boot-starter-security`, `spring-boot-starter-validation`, `jjwt-api` / `jjwt-impl` / `jjwt-jackson`, `postgresql` (runtime), `jqwik` (test), `testcontainers-postgresql` (test), `opencsv` (for CSV export)
  - Configure `application.properties` with PostgreSQL datasource, JPA DDL-auto, JWT secret/issuer/expiry, and CORS origin properties
  - Add a `application-test.properties` profile that uses H2 for `@DataJpaTest` and Testcontainers for integration tests
  - _Requirements: 10.1, 10.4, 9.3, 9.5_

- [x] 2. Implement domain models and JPA entities
  - [x] 2.1 Create `User`, `Category`, `Expense`, and `Budget` JPA entity classes under `model/`
    - `User`: `id`, `email` (unique), `passwordHash`, `displayName`, `createdAt`
    - `Category`: `id`, `name`, `isDefault`, `userId` (nullable for defaults); unique constraint `(name, userId)`
    - `Expense`: `id`, `userId`, `categoryId`, `amount` (`DECIMAL(15,2)`), `expenseDate` (`LocalDate`), `description`, `paymentMethod` (enum), `createdAt`, `updatedAt`
    - `Budget`: `id`, `userId`, `categoryId`, `month` (1–12), `year`, `limitAmount`; unique constraint `(userId, categoryId, month, year)`
    - Use Lombok `@Data` / `@Builder` / `@NoArgsConstructor` / `@AllArgsConstructor`
    - _Requirements: 2.10, 6.1, 9.3_

  - [x] 2.2 Create `DataInitializer` component that seeds the 8 default categories on startup
    - Default categories: Food, Transport, Utilities, Entertainment, Healthcare, Shopping, Education, Other
    - Use `CommandLineRunner` or `ApplicationRunner`; skip seeding if defaults already exist
    - _Requirements: 3.1_

- [x] 3. Implement JPA repositories
  - [x] 3.1 Create `UserRepository`, `CategoryRepository`, `ExpenseRepository`, and `BudgetRepository` extending `JpaRepository`
    - `ExpenseRepository`: add `findByUserIdOrderByExpenseDateDesc`, custom JPQL query for date-range / keyword / amount-range filters with `Pageable` support
    - `CategoryRepository`: `findByUserIdOrIsDefaultTrue`, `findByNameAndUserId`
    - `BudgetRepository`: `findByUserIdAndMonthAndYear`, `findByUserIdAndCategoryIdAndMonthAndYear`
    - _Requirements: 2.4, 4.1–4.6, 6.3_

  - [x] 3.2 Write `@DataJpaTest` repository tests
    - Test date-range, keyword, amount-range, and pagination queries against H2
    - _Requirements: 4.1, 4.2, 4.3, 4.4, 4.5, 4.6_

- [x] 4. Implement JWT security infrastructure
  - [x] 4.1 Create `JwtConfig`, `JwtTokenProvider`, and `JwtAuthenticationFilter`
    - `JwtTokenProvider`: `generateToken(userId)`, `validateToken(token)`, `getUserIdFromToken(token)`; validate signature, expiry, and issuer
    - `JwtAuthenticationFilter`: `OncePerRequestFilter` that extracts the Bearer token, validates it, and sets `SecurityContextHolder`
    - _Requirements: 1.4, 1.6, 1.7, 9.5_

  - [x] 4.2 Create `SecurityConfig` and `CorsConfig`
    - Permit `/api/v1/auth/**` without authentication; protect all other `/api/v1/**` routes
    - Register `JwtAuthenticationFilter` before `UsernamePasswordAuthenticationFilter`
    - Configure CORS to allow the frontend origin from properties
    - _Requirements: 1.6, 1.7, 10.4_

- [x] 5. Implement authentication endpoints
  - [x] 5.1 Create `RegisterRequest`, `LoginRequest`, `AuthResponse` DTOs with Bean Validation annotations
    - `RegisterRequest`: `@Email`, `@NotBlank` email; `@Size(min=8)` password; `@NotBlank` displayName
    - _Requirements: 1.1, 1.3_

  - [x] 5.2 Implement `AuthService` with `register` and `login` methods
    - `register`: check for duplicate email (throw `DuplicateResourceException` on conflict), hash password with BCrypt (cost ≥ 10), persist `User`
    - `login`: load user by email, verify BCrypt hash, generate and return JWT
    - _Requirements: 1.1, 1.2, 1.4, 1.5, 9.3_

  - [x] 5.3 Implement `AuthController` with `POST /api/v1/auth/register` and `POST /api/v1/auth/login`
    - Return `ApiResponse` envelope with 201 / 200 status codes
    - _Requirements: 1.1, 1.4, 10.1, 10.2_

  - [ ]* 5.4 Write unit tests for `AuthService`
    - Test duplicate email rejection, password hashing, invalid credentials
    - _Requirements: 1.1, 1.2, 1.5_

- [~] 6. Implement global exception handler and API response envelope
  - Create `ApiResponse<T>` record with `status`, `message`, `data` fields
  - Create domain exceptions: `ResourceNotFoundException`, `AccessDeniedException`, `DuplicateResourceException`
  - Implement `GlobalExceptionHandler` (`@RestControllerAdvice`) mapping all exceptions from the design's error table to the correct HTTP status codes and `ApiResponse` envelope
  - _Requirements: 10.2, 10.3, 10.5_

- [x] 7. Implement category management
  - [x] 7.1 Create `CategoryRequest` DTO and `CategoryDto` response DTO
    - _Requirements: 3.2_

  - [x] 7.2 Implement `CategoryService`
    - `getCategories(userId)`: return default categories + user's custom categories
    - `createCategory(userId, name)`: check for duplicate (throw `DuplicateResourceException`), persist
    - `deleteCategory(userId, categoryId)`: reject if default (throw `AccessDeniedException`); reassign all associated expenses to "Other" before deleting
    - _Requirements: 3.1, 3.2, 3.3, 3.4, 3.5, 3.6, 3.7_

  - [x] 7.3 Implement `CategoryController` with `GET`, `POST`, `DELETE /api/v1/categories`
    - _Requirements: 3.1–3.7, 10.1, 10.2_

  - [ ]* 7.4 Write property test for category deletion reassignment (Property 9)
    - **Property 9: Category deletion reassigns all associated expenses to "Other"**
    - **Validates: Requirements 3.7**
    - `// Feature: expense-tracker, Property 9: category_deletion_reassigns_expenses`
    - Use jqwik `@Property` in `CategoryDeletionPropertyTest`; generate random sets of expenses per category, delete the category, assert all expenses now reference "Other"
    - _Requirements: 3.7_

  - [ ]* 7.5 Write unit tests for `CategoryService`
    - Test duplicate name rejection, default category deletion rejection, expense reassignment
    - _Requirements: 3.2, 3.3, 3.6, 3.7_

- [x] 8. Implement expense CRUD
  - [x] 8.1 Create `ExpenseRequest`, `ExpenseDto`, and `PagedExpenseResponse` DTOs with Bean Validation annotations
    - `@NotNull @Positive` on `amount`; `@NotNull` on `date` and `categoryId`; `@NotBlank` on `description`
    - _Requirements: 2.1, 2.2, 2.3_

  - [x] 8.2 Implement `ExpenseService` CRUD methods
    - `createExpense(userId, request)`: validate ownership of category, persist expense
    - `getExpenses(userId, filters, pageable)`: scope query to userId, apply optional date/category/keyword/amount filters, return `PagedExpenseResponse`
    - `getExpenseById(userId, id)`: return expense or throw `ResourceNotFoundException`; throw `AccessDeniedException` if not owned by userId
    - `updateExpense(userId, id, request)`: ownership check, update fields, persist
    - `deleteExpense(userId, id)`: ownership check, delete
    - _Requirements: 2.1–2.10, 4.1–4.7, 9.1, 9.2_

  - [x] 8.3 Implement `ExpenseController` with full CRUD endpoints at `/api/v1/expenses`
    - Support query params: `startDate`, `endDate`, `categoryId`, `keyword`, `minAmount`, `maxAmount`, `page`, `size`
    - _Requirements: 2.1–2.10, 4.1–4.7, 10.1, 10.2_

  - [ ]* 8.4 Write property test for expense amount validation (Property 2)
    - **Property 2: Expense amount validation rejects non-positive values**
    - **Validates: Requirements 2.3**
    - `// Feature: expense-tracker, Property 2: amount_validation_rejects_non_positive`
    - Use jqwik in `ExpenseValidationPropertyTest`; generate random zero/negative `BigDecimal` values, assert 400 response and no persistence
    - _Requirements: 2.3_

  - [ ]* 8.5 Write property test for expense list scoping and ordering (Property 3)
    - **Property 3: Expense list is scoped to the authenticated user and ordered by date descending**
    - **Validates: Requirements 2.4, 9.1**
    - `// Feature: expense-tracker, Property 3: expense_list_scoped_and_ordered`
    - Use jqwik in `ExpenseListPropertyTest`; generate random expense sets for multiple users, assert response contains exactly the requesting user's expenses in descending date order
    - _Requirements: 2.4, 9.1_

  - [ ]* 8.6 Write property test for date range filter (Property 4)
    - **Property 4: Date range filter returns only expenses within the inclusive range**
    - **Validates: Requirements 4.1**
    - `// Feature: expense-tracker, Property 4: date_range_filter_correctness`
    - Use jqwik in `ExpenseFilterPropertyTest`; generate random `[startDate, endDate]` ranges and expense sets, assert every returned expense falls within the range
    - _Requirements: 4.1_

  - [ ]* 8.7 Write property test for amount range filter (Property 5)
    - **Property 5: Amount range filter returns only expenses within the inclusive range**
    - **Validates: Requirements 4.4**
    - `// Feature: expense-tracker, Property 5: amount_range_filter_correctness`
    - Use jqwik in `ExpenseFilterPropertyTest`; generate random `[minAmount, maxAmount]` ranges and expense sets, assert every returned expense amount falls within the range
    - _Requirements: 4.4_

  - [ ]* 8.8 Write property test for keyword filter (Property 6)
    - **Property 6: Keyword filter returns only expenses whose description contains the keyword (case-insensitive)**
    - **Validates: Requirements 4.3**
    - `// Feature: expense-tracker, Property 6: keyword_filter_correctness`
    - Use jqwik in `ExpenseFilterPropertyTest`; generate random keyword strings and expense descriptions, assert all returned expenses contain the keyword (case-insensitive)
    - _Requirements: 4.3_

  - [ ]* 8.9 Write property test for pagination metadata consistency (Property 7)
    - **Property 7: Pagination response metadata is consistent with the result set**
    - **Validates: Requirements 4.5, 4.6**
    - `// Feature: expense-tracker, Property 7: pagination_metadata_consistency`
    - Use jqwik in `PaginationPropertyTest`; generate random page/size combinations and expense sets, assert `totalElements`, `totalPages`, and `currentPage` are mathematically consistent
    - _Requirements: 4.5, 4.6_

  - [ ]* 8.10 Write unit tests for `ExpenseService`
    - Test ownership enforcement (403 for wrong user), missing field validation, update and delete flows
    - _Requirements: 2.1–2.10, 9.1, 9.2_

- [~] 9. Checkpoint — Ensure all backend tests pass
  - Run `./mvnw test` in `expensetracker/`; all unit and property tests must be green before proceeding. Ask the user if any questions arise.

- [ ] 10. Implement resource ownership isolation property test (Property 1)
  - [~] 10.1 Write property test for resource ownership isolation (Property 1)
    - **Property 1: Resource ownership isolation**
    - **Validates: Requirements 2.6, 2.9, 9.1, 9.2**
    - `// Feature: expense-tracker, Property 1: resource_ownership_isolation`
    - Use jqwik in `OwnershipIsolationPropertyTest`; generate random user pairs and resource IDs, assert that any read/update/delete by user B on user A's resource returns 403 and does not reveal or modify data
    - _Requirements: 2.6, 2.9, 9.1, 9.2_

  - [ ]* 10.2 Write property test for password never exposed in responses (Property 12)
    - **Property 12: Password is never exposed in any API response**
    - **Validates: Requirements 9.4**
    - `// Feature: expense-tracker, Property 12: password_never_in_response`
    - Use jqwik in `PasswordExposurePropertyTest`; generate random registration/login/API call sequences, assert no response JSON contains `password`, `passwordHash`, or variants
    - _Requirements: 9.4_

  - [ ]* 10.3 Write property test for API response envelope structure (Property 13)
    - **Property 13: API responses always conform to the standard envelope structure**
    - **Validates: Requirements 10.2**
    - `// Feature: expense-tracker, Property 13: api_response_envelope_structure`
    - Use jqwik in `ApiEnvelopePropertyTest`; generate random requests to all endpoints, assert every response body contains exactly `status`, `message`, and `data`, with `status` being `"success"` or `"error"`
    - _Requirements: 10.2_

- [ ] 11. Implement spending summary and analytics
  - [~] 11.1 Create `SummaryResponse`, `CategorySpend`, and `MonthlySpend` DTOs
    - _Requirements: 5.1, 5.2, 5.3, 5.4_

  - [~] 11.2 Implement `SummaryService`
    - `getMonthlySummary(userId, month, year)`: aggregate total per category for the given month, compute `grandTotal`, identify `topCategory`
    - `getYearlySummary(userId, year)`: aggregate total per month for the given year, compute `grandTotal`
    - _Requirements: 5.1, 5.2, 5.3, 5.4_

  - [~] 11.3 Implement `SummaryController` with `GET /api/v1/summary`
    - Accept `month` (optional) and `year` query parameters; delegate to appropriate service method
    - _Requirements: 5.1, 5.2, 5.3, 5.4, 10.1, 10.2_

  - [ ]* 11.4 Write property test for summary grand total consistency (Property 11)
    - **Property 11: Spending summary grand total equals the sum of per-category totals**
    - **Validates: Requirements 5.1, 5.3**
    - `// Feature: expense-tracker, Property 11: summary_grand_total_consistency`
    - Use jqwik in `SummaryPropertyTest`; generate random expense sets per category/month, assert `grandTotal == sum(byCategory[*].totalAmount)`
    - _Requirements: 5.1, 5.3_

  - [ ]* 11.5 Write unit tests for `SummaryService`
    - Test monthly and yearly aggregation, top-category identification, empty period handling
    - _Requirements: 5.1, 5.2, 5.3, 5.4_

- [ ] 12. Implement budget management
  - [~] 12.1 Create `BudgetRequest`, `BudgetDto`, and `BudgetStatusDto` DTOs with Bean Validation annotations
    - `@Positive` on `limitAmount`; `@NotNull` on `categoryId`, `month`, `year`
    - _Requirements: 6.1, 6.2_

  - [~] 12.2 Implement `BudgetService`
    - `createBudget(userId, request)`: check for duplicate `(userId, categoryId, month, year)` (throw `DuplicateResourceException`), persist
    - `getBudgets(userId, month, year)`: return all budgets with `totalSpent`, `warningThresholdReached` (`totalSpent >= 0.80 * limitAmount`), `limitExceeded` (`totalSpent >= limitAmount`)
    - `updateBudget(userId, id, limitAmount)`: ownership check, update
    - `deleteBudget(userId, id)`: ownership check, delete
    - _Requirements: 6.1–6.7_

  - [~] 12.3 Implement `BudgetController` with full CRUD at `/api/v1/budgets`
    - `GET /api/v1/budgets?month=&year=` returns list of `BudgetStatusDto`
    - _Requirements: 6.1–6.7, 10.1, 10.2_

  - [ ]* 12.4 Write property test for budget threshold flags (Property 8)
    - **Property 8: Budget threshold flags are consistent with spending**
    - **Validates: Requirements 6.4, 6.5**
    - `// Feature: expense-tracker, Property 8: budget_threshold_flags_consistency`
    - Use jqwik in `BudgetThresholdPropertyTest`; generate random positive `limitAmount` and `totalSpent` values, assert `warningThresholdReached == (totalSpent >= 0.80 * limitAmount)` and `limitExceeded == (totalSpent >= limitAmount)`
    - _Requirements: 6.4, 6.5_

  - [ ]* 12.5 Write unit tests for `BudgetService`
    - Test duplicate budget rejection, threshold flag calculation at boundary values, ownership enforcement
    - _Requirements: 6.1, 6.2, 6.4, 6.5, 6.6, 6.7_

- [ ] 13. Implement CSV export
  - [~] 13.1 Implement `CsvExporter` utility class
    - Write `export(List<Expense> expenses): String` producing CSV with columns: `id`, `date`, `amount`, `category`, `description`, `paymentMethod`
    - Handle special characters (commas, quotes, newlines) in description field
    - _Requirements: 8.1, 8.2, 8.4_

  - [~] 13.2 Add `GET /api/v1/expenses/export` endpoint to `ExpenseController`
    - Accept optional `startDate` / `endDate` query params; return `text/csv` response with `Content-Disposition: attachment` header
    - _Requirements: 8.1, 8.2_

  - [ ]* 13.3 Write property test for CSV round-trip fidelity (Property 10)
    - **Property 10: CSV export round-trip fidelity**
    - **Validates: Requirements 8.4**
    - `// Feature: expense-tracker, Property 10: csv_export_round_trip_fidelity`
    - Use jqwik in `CsvExportPropertyTest`; generate random expense records (all field combinations, special characters in description), export to CSV, re-parse, assert all fields are identical to originals
    - _Requirements: 8.4_

  - [ ]* 13.4 Write unit tests for `CsvExporter`
    - Test column headers, field ordering, special-character escaping with concrete examples
    - _Requirements: 8.1, 8.4_

- [~] 14. Write backend integration tests
  - [ ]* 14.1 Write Spring Boot integration tests using Testcontainers (PostgreSQL)
    - Cover full request/response cycle for auth flow, expense CRUD, category management, budget management, summary, and CSV export
    - Verify CORS headers present on responses
    - Verify 500 error logging via log capture
    - _Requirements: 1.1–1.8, 2.1–2.10, 3.1–3.7, 4.1–4.7, 5.1–5.4, 6.1–6.7, 8.1–8.2, 9.1–9.5, 10.1–10.5_

- [~] 15. Checkpoint — Ensure all backend tests pass
  - Run `./mvnw test` in `expensetracker/`; all unit, property, and integration tests must be green. Ask the user if any questions arise.

- [~] 16. Scaffold React frontend application
  - Create a new React + Vite project (TypeScript) alongside `expensetracker/` (e.g., `expense-tracker-ui/`)
  - Install dependencies: `tailwindcss`, `postcss`, `autoprefixer`, `axios`, `react-router-dom`, `@tanstack/react-query`, `recharts`
  - Configure Tailwind CSS (`tailwind.config.js`, `postcss.config.js`, import in `index.css`)
  - Set up Vite proxy to forward `/api` requests to `http://localhost:8080` during development
  - _Requirements: 7.1–7.9_

- [ ] 17. Implement frontend types, API client, and auth store
  - [~] 17.1 Create TypeScript interfaces in `src/types/` mirroring backend DTOs
    - `User`, `Expense`, `Category`, `Budget`, `BudgetStatus`, `PagedExpenseResponse`, `SummaryResponse`, `ApiResponse<T>`
    - _Requirements: 10.2_

  - [~] 17.2 Create Axios instance in `src/api/` with base URL and JWT interceptor
    - Request interceptor: attach `Authorization: Bearer <token>` header from auth store
    - Response interceptor: on 401, clear JWT and redirect to `/login`
    - _Requirements: 1.6, 1.7, 1.8_

  - [~] 17.3 Create per-resource API functions in `src/api/`
    - `authApi`: `register`, `login`
    - `expensesApi`: `getExpenses`, `getExpenseById`, `createExpense`, `updateExpense`, `deleteExpense`, `exportCsv`
    - `categoriesApi`: `getCategories`, `createCategory`, `deleteCategory`
    - `budgetsApi`: `getBudgets`, `createBudget`, `updateBudget`, `deleteBudget`
    - `summaryApi`: `getSummary`
    - _Requirements: 2.1–2.10, 3.1–3.7, 6.1–6.7, 5.1–5.4, 8.3_

  - [~] 17.4 Implement auth context/store in `src/store/`
    - Store JWT in `localStorage`; expose `login`, `logout`, `isAuthenticated`, `userId`
    - `logout` clears JWT and redirects to `/login`
    - _Requirements: 1.8_

- [ ] 18. Implement common UI components
  - [~] 18.1 Create `Button`, `Modal`, `ConfirmDialog`, `LoadingSpinner`, and `ProgressBar` components in `src/components/common/`
    - `ProgressBar`: accepts `spent` and `limit` props, renders correct width percentage
    - `ConfirmDialog`: renders confirmation message, calls correct callback on confirm/cancel
    - `LoadingSpinner`: displayed while any React Query request is in flight
    - _Requirements: 7.8, 7.9, 6.8_

  - [~] 18.2 Create `Navbar`, `Sidebar`, and `PageWrapper` layout components in `src/components/layout/`
    - Responsive layout rendering correctly from 375px to 1440px
    - _Requirements: 7.6_

  - [ ]* 18.3 Write unit tests for `ConfirmDialog` and `ProgressBar` (Vitest + React Testing Library)
    - `ConfirmDialog`: renders and calls correct callback on confirm/cancel
    - `ProgressBar`: renders correct width percentage for given spent/limit values
    - _Requirements: 7.9, 6.8_

- [ ] 19. Implement authentication pages
  - [~] 19.1 Implement `LoginPage` and `RegisterPage` in `src/pages/`
    - Forms with controlled inputs; display field-level validation errors from 400 API responses adjacent to the relevant input
    - On successful login, store JWT and redirect to `/dashboard`
    - _Requirements: 1.1, 1.3, 1.5, 7.7_

  - [~] 19.2 Implement protected route wrapper
    - Redirect unauthenticated users to `/login` for all routes except `/login` and `/register`
    - _Requirements: 1.6, 1.7_

  - [ ]* 19.3 Write unit tests for auth form validation
    - Test empty fields, password shorter than 8 characters, duplicate email error display
    - _Requirements: 1.3, 7.7_

- [~] 20. Implement custom React Query hooks
  - Create `useExpenses`, `useCategories`, `useBudgets`, and `useSummary` hooks in `src/hooks/`
  - Each hook wraps the corresponding API function with React Query (`useQuery` / `useMutation`)
  - Mutations invalidate relevant query caches on success
  - _Requirements: 7.8_

- [ ] 21. Implement Expenses page
  - [~] 21.1 Implement `ExpensesPage` with searchable, filterable, paginated table
    - Filter controls: date range, category dropdown, keyword search, amount range
    - Pagination controls using `page` and `size` from `PagedExpenseResponse`
    - Inline "Edit" and "Delete" actions; delete triggers `ConfirmDialog`
    - "Add Expense" button opens a modal form
    - _Requirements: 7.3, 7.9, 4.1–4.7_

  - [~] 21.2 Add "Export to CSV" button that calls `exportCsv` API and triggers browser file download
    - _Requirements: 8.3_

  - [ ]* 21.3 Write unit tests for `ExpensesPage` form validation
    - Test missing required fields, non-positive amount, invalid date format error display
    - _Requirements: 2.2, 2.3, 4.7, 7.7_

- [ ] 22. Implement Categories and Budgets pages
  - [~] 22.1 Implement `CategoriesPage`
    - List default and custom categories; "Add Category" form; delete button with `ConfirmDialog`
    - Display 409 conflict error when duplicate name submitted
    - _Requirements: 7.4, 3.1–3.7, 7.7, 7.9_

  - [~] 22.2 Implement `BudgetsPage`
    - List budgets for selected month/year with `ProgressBar` showing percentage spent
    - "Add Budget", "Edit Budget", "Delete Budget" actions; delete triggers `ConfirmDialog`
    - Display warning/exceeded state visually based on `warningThresholdReached` / `limitExceeded` flags
    - _Requirements: 7.5, 6.1–6.8, 7.7, 7.9_

  - [ ]* 22.3 Write unit tests for `CategoriesPage` and `BudgetsPage` form validation
    - Test duplicate category name error, non-positive budget limit error
    - _Requirements: 3.3, 6.2, 7.7_

- [ ] 23. Implement Dashboard page and charts
  - [~] 23.1 Create `CategoryPieChart` and `MonthlyTrendChart` components in `src/components/charts/` using Recharts
    - `CategoryPieChart`: renders monthly category breakdown
    - `MonthlyTrendChart`: renders year-over-month spending trend
    - _Requirements: 5.5, 5.6_

  - [~] 23.2 Implement `DashboardPage`
    - Display: total spending for current month, top spending category, number of expenses this month, remaining budget across all categories
    - Display 5 most recent expenses (amount, category, date, description)
    - Embed `CategoryPieChart` and `MonthlyTrendChart`
    - Embed `ProgressBar` per budget
    - _Requirements: 7.1, 7.2, 5.5, 5.6, 6.8_

  - [ ]* 23.3 Write snapshot tests for `CategoryPieChart` and `MonthlyTrendChart`
    - Catch unintended rendering changes
    - _Requirements: 5.5, 5.6_

- [~] 24. Final checkpoint — Ensure all tests pass
  - Run `./mvnw test` in `expensetracker/` and `npx vitest --run` in `expense-tracker-ui/`; all tests must be green. Ask the user if any questions arise.

---

## Notes

- Tasks marked with `*` are optional and can be skipped for a faster MVP
- Each task references specific requirements for traceability
- Checkpoints (tasks 9, 15, 24) ensure incremental validation before moving to the next phase
- Property tests use jqwik (`@Property`, `@ForAll`) and are tagged with `// Feature: expense-tracker, Property N: ...` comments
- The backend project lives at `expensetracker/`; the frontend is created fresh at `expense-tracker-ui/`
- PostgreSQL is the production database; H2 is used for `@DataJpaTest` and local development without Docker
