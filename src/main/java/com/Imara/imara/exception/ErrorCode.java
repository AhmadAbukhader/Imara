package com.Imara.imara.exception;

import org.springframework.http.HttpStatus;

/**
 * Standard error codes for application exceptions.
 * Each code defines the HTTP status and default message.
 */
public enum ErrorCode {

    // 401 Unauthorized
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Authentication required"),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "Invalid email or password"),

    // 403 Forbidden
    FORBIDDEN(HttpStatus.FORBIDDEN, "Access denied"),

    // 404 Not Found
    COMPANY_NOT_FOUND(HttpStatus.NOT_FOUND, "Company not found"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User not found"),

    // 409 Conflict
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "Email is already registered"),
    DUPLICATE_COMPANY_EMAIL(HttpStatus.CONFLICT, "Company with this email already exists"),
    DUPLICATE_KEY(HttpStatus.CONFLICT, "Duplicate key violation"),

    // 500 Internal (repository/DB layer)
    DATA_NOT_FOUND(HttpStatus.NOT_FOUND, "Record not found"),
    DATA_INTEGRITY_VIOLATION(HttpStatus.BAD_REQUEST, "Data integrity violation"),
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "A database error occurred");

    private final HttpStatus status;
    private final String defaultMessage;

    ErrorCode(HttpStatus status, String defaultMessage) {
        this.status = status;
        this.defaultMessage = defaultMessage;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}
