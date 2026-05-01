package com.business.expensetracker.exception;

/**
 * Thrown when a resource with the same unique identifier already exists.
 * Maps to HTTP 409 Conflict.
 * Requirements: 1.2, 3.3, 6.2
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}
