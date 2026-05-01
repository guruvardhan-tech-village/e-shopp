package com.business.expensetracker.exception;

/**
 * Thrown when an authenticated user attempts to access or modify a resource
 * that belongs to a different user.
 * Maps to HTTP 403 Forbidden.
 * Requirements: 2.6, 2.9, 9.1, 9.2
 */
public class AccessDeniedException extends RuntimeException {

    public AccessDeniedException(String message) {
        super(message);
    }
}
