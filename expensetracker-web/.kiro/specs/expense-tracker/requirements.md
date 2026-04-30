# Requirements Document

## Introduction

A full-stack Expense Tracker application that allows users to record, categorize, and analyze their personal or business expenses. The backend is a Spring Boot REST API (Java 21, Spring Data JPA, relational database) and the frontend is a React single-page application styled with Tailwind CSS. The system enables users to manage expenses through a clean dashboard, view spending summaries, and filter/search their transaction history.

---

## Glossary

- **Expense_Tracker**: The full-stack application composed of the Backend API and the Frontend UI.
- **Backend_API**: The Spring Boot REST API responsible for data persistence, business logic, and serving JSON responses.
- **Frontend_UI**: The React single-page application that users interact with directly in a browser.
- **User**: An authenticated person who owns and manages their own expense records.
- **Expense**: A single financial transaction record with an amount, date, category, description, and payment method.
- **Category**: A named label used to group expenses (e.g., Food, Transport, Utilities, Entertainment).
- **Budget**: An optional monthly spending limit set by the User for a specific Category.
- **Dashboard**: The main view of the Frontend_UI that displays spending summaries and recent expenses.
- **JWT**: JSON Web Token used for stateless authentication between the Frontend_UI and the Backend_API.
- **Validator**: The component within the Backend_API responsible for validating incoming request payloads.

---

## Requirements

### Requirement 1: User Registration and Authentication

**User Story:** As a new user, I want to register an account and log in securely, so that my expense data is private and accessible only to me.

#### Acceptance Criteria

1. WHEN a registration request is received with a unique email, valid password (minimum 8 characters), and display name, THE Backend_API SHALL create a new User account and return a 201 Created response.
2. IF a registration request is received with an email that already exists, THEN THE Backend_API SHALL return a 409 Conflict response with a descriptive error message.
3. IF a registration request is received with a password shorter than 8 characters, THEN THE Validator SHALL return a 400 Bad Request response listing the validation errors.
4. WHEN a login request is received with valid credentials, THE Backend_API SHALL return a signed JWT with a 24-hour expiry and a 200 OK response.
5. IF a login request is received with invalid credentials, THEN THE Backend_API SHALL return a 401 Unauthorized response.
6. WHILE a JWT is valid and not expired, THE Backend_API SHALL accept it as proof of authentication for all protected endpoints.
7. IF a request to a protected endpoint is received without a valid JWT, THEN THE Backend_API SHALL return a 401 Unauthorized response.
8. WHEN a User logs out, THE Frontend_UI SHALL remove the stored JWT from browser storage and redirect the User to the login page.

---

### Requirement 2: Expense Management (CRUD)

**User Story:** As a user, I want to create, view, edit, and delete my expense records, so that I can maintain an accurate history of my spending.

#### Acceptance Criteria

1. WHEN a create-expense request is received with a valid amount (positive number), date, category, and description, THE Backend_API SHALL persist the Expense and return a 201 Created response containing the saved Expense.
2. IF a create-expense request is received with a missing required field (amount, date, or category), THEN THE Validator SHALL return a 400 Bad Request response listing the missing fields.
3. IF a create-expense request is received with a non-positive amount, THEN THE Validator SHALL return a 400 Bad Request response.
4. WHEN a get-all-expenses request is received for an authenticated User, THE Backend_API SHALL return only the Expenses belonging to that User, ordered by date descending.
5. WHEN a get-expense-by-id request is received for an Expense that belongs to the authenticated User, THE Backend_API SHALL return the Expense with a 200 OK response.
6. IF a get-expense-by-id request is received for an Expense that does not belong to the authenticated User, THEN THE Backend_API SHALL return a 403 Forbidden response.
7. WHEN an update-expense request is received with valid fields for an Expense belonging to the authenticated User, THE Backend_API SHALL update the Expense and return the updated record with a 200 OK response.
8. WHEN a delete-expense request is received for an Expense belonging to the authenticated User, THE Backend_API SHALL delete the Expense and return a 204 No Content response.
9. IF a delete-expense request is received for an Expense that does not belong to the authenticated User, THEN THE Backend_API SHALL return a 403 Forbidden response.
10. THE Backend_API SHALL support an optional `paymentMethod` field on each Expense, accepting values: CASH, CREDIT_CARD, DEBIT_CARD, BANK_TRANSFER, OTHER.

---

### Requirement 3: Category Management

**User Story:** As a user, I want to organize my expenses into categories, so that I can understand where my money is going.

#### Acceptance Criteria

1. THE Backend_API SHALL provide a set of default Categories (Food, Transport, Utilities, Entertainment, Healthcare, Shopping, Education, Other) available to all Users.
2. WHEN a create-category request is received with a unique name for the authenticated User, THE Backend_API SHALL persist the custom Category and return a 201 Created response.
3. IF a create-category request is received with a name that duplicates an existing Category for that User, THEN THE Backend_API SHALL return a 409 Conflict response.
4. WHEN a get-categories request is received for an authenticated User, THE Backend_API SHALL return the combined list of default Categories and the User's custom Categories.
5. WHEN a delete-category request is received for a custom Category belonging to the authenticated User, THE Backend_API SHALL delete the Category and return a 204 No Content response.
6. IF a delete-category request is received for a default Category, THEN THE Backend_API SHALL return a 403 Forbidden response.
7. IF a delete-category request is received for a Category that has associated Expenses, THEN THE Backend_API SHALL reassign those Expenses to the "Other" Category before deleting the requested Category.

---

### Requirement 4: Expense Filtering, Search, and Pagination

**User Story:** As a user, I want to filter and search my expenses by date range, category, and keyword, so that I can quickly find specific transactions.

#### Acceptance Criteria

1. WHEN a get-expenses request is received with a `startDate` and `endDate` query parameter, THE Backend_API SHALL return only Expenses with a date within the inclusive range [startDate, endDate].
2. WHEN a get-expenses request is received with a `categoryId` query parameter, THE Backend_API SHALL return only Expenses belonging to that Category.
3. WHEN a get-expenses request is received with a `keyword` query parameter, THE Backend_API SHALL return only Expenses whose description contains the keyword (case-insensitive).
4. WHEN a get-expenses request is received with a `minAmount` and `maxAmount` query parameter, THE Backend_API SHALL return only Expenses with an amount within the inclusive range [minAmount, maxAmount].
5. THE Backend_API SHALL support pagination on all list endpoints via `page` (0-indexed) and `size` query parameters, defaulting to page 0 and size 20.
6. WHEN a paginated get-expenses request is received, THE Backend_API SHALL return a response containing the list of Expenses, total element count, total page count, and current page number.
7. IF a get-expenses request is received with an invalid date format for `startDate` or `endDate`, THEN THE Validator SHALL return a 400 Bad Request response.

---

### Requirement 5: Spending Summary and Analytics

**User Story:** As a user, I want to see a summary of my spending by category and over time, so that I can understand my financial habits.

#### Acceptance Criteria

1. WHEN a get-summary request is received with a `month` and `year` parameter, THE Backend_API SHALL return the total amount spent per Category for that month.
2. WHEN a get-summary request is received with a `year` parameter only, THE Backend_API SHALL return the total amount spent per month for that year.
3. WHEN a get-summary request is received, THE Backend_API SHALL include the total overall spending for the requested period in the response.
4. WHEN a get-summary request is received, THE Backend_API SHALL return the Category with the highest total spending for the requested period.
5. THE Frontend_UI SHALL display the monthly category breakdown as a visual chart on the Dashboard.
6. THE Frontend_UI SHALL display the year-over-month spending trend as a visual chart on the Dashboard.

---

### Requirement 6: Budget Management

**User Story:** As a user, I want to set monthly spending budgets per category, so that I can track whether I am overspending.

#### Acceptance Criteria

1. WHEN a create-budget request is received with a valid Category, month, year, and positive limit amount, THE Backend_API SHALL persist the Budget and return a 201 Created response.
2. IF a create-budget request is received for a Category and month/year combination that already has a Budget for the authenticated User, THEN THE Backend_API SHALL return a 409 Conflict response.
3. WHEN a get-budgets request is received for an authenticated User with a `month` and `year` parameter, THE Backend_API SHALL return all Budgets for that period along with the current total spent for each Category.
4. WHEN the total spending for a Category in a given month reaches 80% of the Budget limit, THE Backend_API SHALL include a `warningThresholdReached: true` flag in the budget response for that Category.
5. WHEN the total spending for a Category in a given month equals or exceeds the Budget limit, THE Backend_API SHALL include an `limitExceeded: true` flag in the budget response for that Category.
6. WHEN an update-budget request is received with a valid positive limit amount for an existing Budget belonging to the authenticated User, THE Backend_API SHALL update the Budget and return the updated record with a 200 OK response.
7. WHEN a delete-budget request is received for a Budget belonging to the authenticated User, THE Backend_API SHALL delete the Budget and return a 204 No Content response.
8. THE Frontend_UI SHALL display each Budget on the Dashboard with a progress bar showing the percentage of the limit spent.

---

### Requirement 7: Dashboard and User Interface

**User Story:** As a user, I want a clear and responsive dashboard, so that I can quickly understand my financial status at a glance.

#### Acceptance Criteria

1. THE Frontend_UI SHALL display a Dashboard as the default authenticated view, showing total spending for the current month, top spending category, number of expenses recorded this month, and remaining budget across all categories.
2. THE Frontend_UI SHALL display the 5 most recent Expenses on the Dashboard with their amount, category, date, and description.
3. THE Frontend_UI SHALL provide a dedicated Expenses page with a searchable, filterable, and paginated table of all Expenses.
4. THE Frontend_UI SHALL provide a dedicated Categories page where the User can view, create, and delete Categories.
5. THE Frontend_UI SHALL provide a dedicated Budgets page where the User can view, create, update, and delete Budgets.
6. THE Frontend_UI SHALL be responsive and render correctly on screen widths from 375px (mobile) to 1440px (desktop).
7. WHEN a form submission fails due to a validation error returned by the Backend_API, THE Frontend_UI SHALL display the error message adjacent to the relevant form field.
8. WHEN a network request is in progress, THE Frontend_UI SHALL display a loading indicator to the User.
9. WHEN a destructive action (delete expense, delete category, delete budget) is triggered, THE Frontend_UI SHALL display a confirmation dialog before sending the delete request to the Backend_API.

---

### Requirement 8: Data Export

**User Story:** As a user, I want to export my expense history to a CSV file, so that I can use the data in external tools like spreadsheets.

#### Acceptance Criteria

1. WHEN an export-expenses request is received for an authenticated User, THE Backend_API SHALL return a CSV file containing all Expenses for that User with columns: id, date, amount, category, description, paymentMethod.
2. WHEN an export-expenses request is received with `startDate` and `endDate` query parameters, THE Backend_API SHALL return a CSV file containing only Expenses within that date range.
3. THE Frontend_UI SHALL provide an "Export to CSV" button on the Expenses page that triggers the export-expenses request and initiates a file download in the browser.
4. WHEN the exported CSV file is re-parsed, THE Backend_API CSV output SHALL produce records with field values identical to the original Expense records (round-trip property).

---

### Requirement 9: Security and Data Isolation

**User Story:** As a user, I want confidence that my expense data cannot be accessed or modified by other users, so that my financial information remains private.

#### Acceptance Criteria

1. THE Backend_API SHALL enforce that every data-access operation (read, create, update, delete) on Expenses, Categories, and Budgets is scoped to the authenticated User's identity extracted from the JWT.
2. IF a request attempts to access or modify a resource belonging to a different User, THEN THE Backend_API SHALL return a 403 Forbidden response without revealing the existence of the resource.
3. THE Backend_API SHALL store User passwords as bcrypt hashes with a minimum cost factor of 10.
4. THE Backend_API SHALL not include User passwords or password hashes in any API response.
5. WHEN a JWT is received, THE Backend_API SHALL validate the token signature, expiry, and issuer before granting access to any protected endpoint.

---

### Requirement 10: API Design and Error Handling

**User Story:** As a frontend developer, I want a consistent and well-structured REST API, so that I can integrate reliably without unexpected behavior.

#### Acceptance Criteria

1. THE Backend_API SHALL follow RESTful conventions with resource-based URL paths (e.g., `/api/v1/expenses`, `/api/v1/categories`).
2. THE Backend_API SHALL return all responses in JSON format with a consistent envelope containing `status`, `message`, and `data` fields.
3. IF an unhandled exception occurs, THEN THE Backend_API SHALL return a 500 Internal Server Error response with a generic error message and SHALL log the full stack trace internally.
4. THE Backend_API SHALL include CORS configuration that allows requests from the Frontend_UI origin.
5. THE Backend_API SHALL validate all incoming request bodies using Jakarta Bean Validation annotations and return structured 400 responses for constraint violations.
