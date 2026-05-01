package com.business.expensetracker.dto.response;

/**
 * Standard JSON response envelope used by all API endpoints.
 * Requirements: 10.2
 *
 * <pre>
 * {
 *   "status":  "success" | "error",
 *   "message": "...",
 *   "data":    { ... } | null
 * }
 * </pre>
 */
public record ApiResponse<T>(
        String status,
        String message,
        T data
) {

    /** Convenience factory for successful responses. */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>("success", message, data);
    }

    /** Convenience factory for error responses. */
    public static <T> ApiResponse<T> error(String message, T data) {
        return new ApiResponse<>("error", message, data);
    }
}
