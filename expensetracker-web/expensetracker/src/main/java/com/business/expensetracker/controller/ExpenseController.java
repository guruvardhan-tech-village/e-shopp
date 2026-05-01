package com.business.expensetracker.controller;

import com.business.expensetracker.dto.request.ExpenseRequest;
import com.business.expensetracker.dto.response.ApiResponse;
import com.business.expensetracker.dto.response.ExpenseDto;
import com.business.expensetracker.dto.response.PagedExpenseResponse;
import com.business.expensetracker.model.Expense;
import com.business.expensetracker.service.CsvExporter;
import com.business.expensetracker.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * REST controller for expense CRUD operations and CSV export.
 *
 * <ul>
 *   <li>GET    /api/v1/expenses            — list expenses (filters + pagination)</li>
 *   <li>POST   /api/v1/expenses            — create expense</li>
 *   <li>GET    /api/v1/expenses/{id}       — get single expense</li>
 *   <li>PUT    /api/v1/expenses/{id}       — update expense</li>
 *   <li>DELETE /api/v1/expenses/{id}       — delete expense</li>
 *   <li>GET    /api/v1/expenses/export     — export expenses as CSV</li>
 * </ul>
 *
 * Requirements: 2.1–2.10, 4.1–4.7, 8.1, 8.2, 10.1, 10.2
 */
@RestController
@RequestMapping("/api/v1/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;
    private final CsvExporter csvExporter;

    // -------------------------------------------------------------------------
    // List with filters and pagination
    // -------------------------------------------------------------------------

    /**
     * Returns a paginated, filtered list of expenses for the authenticated user.
     * All query parameters are optional.
     * Requirements: 2.4, 4.1–4.6
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PagedExpenseResponse>> getExpenses(
            @AuthenticationPrincipal Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PagedExpenseResponse result = expenseService.getExpenses(
                userId, startDate, endDate, categoryId, keyword, minAmount, maxAmount, page, size);

        return ResponseEntity.ok(ApiResponse.success("Expenses retrieved successfully", result));
    }

    // -------------------------------------------------------------------------
    // Create
    // -------------------------------------------------------------------------

    /**
     * Creates a new expense for the authenticated user.
     * Returns 201 Created with the saved expense.
     * Requirements: 2.1, 2.2, 2.3
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ExpenseDto>> createExpense(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody ExpenseRequest request) {

        ExpenseDto created = expenseService.createExpense(userId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Expense created successfully", created));
    }

    // -------------------------------------------------------------------------
    // Read single
    // -------------------------------------------------------------------------

    /**
     * Returns a single expense by ID.
     * Returns 403 Forbidden if the expense belongs to a different user.
     * Requirements: 2.5, 2.6
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ExpenseDto>> getExpenseById(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long id) {

        ExpenseDto expense = expenseService.getExpenseById(userId, id);
        return ResponseEntity.ok(ApiResponse.success("Expense retrieved successfully", expense));
    }

    // -------------------------------------------------------------------------
    // Update
    // -------------------------------------------------------------------------

    /**
     * Updates an existing expense belonging to the authenticated user.
     * Requirements: 2.7
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ExpenseDto>> updateExpense(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long id,
            @Valid @RequestBody ExpenseRequest request) {

        ExpenseDto updated = expenseService.updateExpense(userId, id, request);
        return ResponseEntity.ok(ApiResponse.success("Expense updated successfully", updated));
    }

    // -------------------------------------------------------------------------
    // Delete
    // -------------------------------------------------------------------------

    /**
     * Deletes an expense belonging to the authenticated user.
     * Returns 204 No Content on success.
     * Returns 403 Forbidden if the expense belongs to a different user.
     * Requirements: 2.8, 2.9
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteExpense(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long id) {

        expenseService.deleteExpense(userId, id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.success("Expense deleted successfully", null));
    }

    // -------------------------------------------------------------------------
    // CSV Export
    // -------------------------------------------------------------------------

    /**
     * Exports the authenticated user's expenses as a CSV file.
     * Accepts optional startDate / endDate query parameters to filter the export range.
     * Returns the file as an attachment with Content-Type: text/csv.
     * Requirements: 8.1, 8.2
     */
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportCsv(
            @AuthenticationPrincipal Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<Expense> expenses = expenseService.getExpensesForExport(userId, startDate, endDate);
        String csv = csvExporter.export(expenses);

        byte[] csvBytes = csv.getBytes(java.nio.charset.StandardCharsets.UTF_8);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "expenses.csv");
        headers.setContentLength(csvBytes.length);

        return new ResponseEntity<>(csvBytes, headers, HttpStatus.OK);
    }
}
