package com.business.expensetracker.controller;

import com.business.expensetracker.dto.request.BudgetRequest;
import com.business.expensetracker.dto.response.ApiResponse;
import com.business.expensetracker.dto.response.BudgetDto;
import com.business.expensetracker.service.BudgetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<BudgetDto>>> getBudgets(
            @AuthenticationPrincipal Long userId,
            @RequestParam int month,
            @RequestParam int year) {

        List<BudgetDto> budgets = budgetService.getBudgets(userId, month, year);
        return ResponseEntity.ok(ApiResponse.success("Budgets retrieved successfully", budgets));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BudgetDto>> createBudget(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody BudgetRequest request) {

        BudgetDto created = budgetService.createBudget(userId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Budget created successfully", created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BudgetDto>> getBudgetById(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long id) {

        BudgetDto budget = budgetService.getBudgetById(userId, id);
        return ResponseEntity.ok(ApiResponse.success("Budget retrieved successfully", budget));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BudgetDto>> updateBudget(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long id,
            @Valid @RequestBody BudgetRequest request) {

        BudgetDto updated = budgetService.updateBudget(userId, id, request);
        return ResponseEntity.ok(ApiResponse.success("Budget updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBudget(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long id) {

        budgetService.deleteBudget(userId, id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.success("Budget deleted successfully", null));
    }
}
