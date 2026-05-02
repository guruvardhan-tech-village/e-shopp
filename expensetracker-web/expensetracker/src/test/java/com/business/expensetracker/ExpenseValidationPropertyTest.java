package com.business.expensetracker;

// Feature: expense-tracker, Property 2: amount_validation_rejects_non_positive

import com.business.expensetracker.controller.ExpenseController;
import com.business.expensetracker.exception.GlobalExceptionHandler;
import com.business.expensetracker.service.CsvExporter;
import com.business.expensetracker.service.ExpenseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.jqwik.api.*;
import net.jqwik.api.constraints.Scale;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Property-based tests for expense amount validation.
 *
 * <p>Property 2: Expense amount validation rejects non-positive values.
 * For any create-expense request where the amount is zero or negative,
 * the Backend_API SHALL return a 400 Bad Request response and SHALL NOT
 * persist any new expense record.
 *
 * <p><b>Validates: Requirements 2.3</b>
 */
class ExpenseValidationPropertyTest {

    // jqwik creates a new instance per @Property, so field initializers run for each property.
    private final ExpenseService expenseService = mock(ExpenseService.class);
    private final CsvExporter csvExporter = mock(CsvExporter.class);
    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new ExpenseController(expenseService, csvExporter))
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Property 2: A create-expense request with a zero amount must be rejected with 400
     * and must never reach the service layer (no persistence).
     *
     * <b>Validates: Requirements 2.3</b>
     */
    @Property(tries = 100)
    void zeroAmountIsRejectedWith400(
            @ForAll @Scale(2) BigDecimal ignored) throws Exception {
        // Always use exactly zero — the parameter is just to satisfy jqwik's @Property requirement
        BigDecimal zeroAmount = BigDecimal.ZERO;

        mockMvc.perform(post("/api/v1/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(buildRequestBody(zeroAmount))
                        .with(SecurityMockMvcRequestPostProcessors.user("1")))
                .andExpect(status().isBadRequest());

        // Verify the service was never called — no persistence occurred
        verify(expenseService, never()).createExpense(any(), any());
    }

    /**
     * Property 2: A create-expense request with a negative amount must be rejected with 400
     * and must never reach the service layer (no persistence).
     *
     * <b>Validates: Requirements 2.3</b>
     */
    @Property(tries = 100)
    void negativeAmountIsRejectedWith400(
            @ForAll("negativeAmounts") BigDecimal negativeAmount) throws Exception {

        mockMvc.perform(post("/api/v1/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(buildRequestBody(negativeAmount))
                        .with(SecurityMockMvcRequestPostProcessors.user("1")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"));

        // Verify the service was never called — no persistence occurred
        verify(expenseService, never()).createExpense(any(), any());
    }

    /**
     * Property 2: The 400 response for a non-positive amount must include a validation
     * error for the "amount" field, confirming the rejection is specifically about the amount.
     *
     * <b>Validates: Requirements 2.3</b>
     */
    @Property(tries = 50)
    void nonPositiveAmountResponseContainsAmountFieldError(
            @ForAll("nonPositiveAmounts") BigDecimal nonPositiveAmount) throws Exception {

        mockMvc.perform(post("/api/v1/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(buildRequestBody(nonPositiveAmount))
                        .with(SecurityMockMvcRequestPostProcessors.user("1")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.data.amount").exists());

        verify(expenseService, never()).createExpense(any(), any());
    }

    // -------------------------------------------------------------------------
    // Arbitraries (generators)
    // -------------------------------------------------------------------------

    /**
     * Generates negative BigDecimal values (strictly less than zero).
     * Uses a range from -1,000,000 to -0.01 with 2 decimal places.
     */
    @Provide
    Arbitrary<BigDecimal> negativeAmounts() {
        return Arbitraries.bigDecimals()
                .between(new BigDecimal("-1000000.00"), new BigDecimal("-0.01"))
                .ofScale(2);
    }

    /**
     * Generates non-positive BigDecimal values (zero or negative).
     * Combines zero with negative values.
     */
    @Provide
    Arbitrary<BigDecimal> nonPositiveAmounts() {
        Arbitrary<BigDecimal> negatives = Arbitraries.bigDecimals()
                .between(new BigDecimal("-1000000.00"), new BigDecimal("-0.01"))
                .ofScale(2);
        Arbitrary<BigDecimal> zero = Arbitraries.just(BigDecimal.ZERO);
        return Arbitraries.oneOf(negatives, zero);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /**
     * Builds a valid JSON request body for creating an expense, with the given amount.
     * All other fields are valid so that only the amount triggers a validation failure.
     */
    private String buildRequestBody(BigDecimal amount) throws Exception {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("amount", amount);
        body.put("date", "2024-06-15");
        body.put("categoryId", 1L);
        body.put("description", "Test expense");
        body.put("paymentMethod", "CASH");
        return objectMapper.writeValueAsString(body);
    }
}
