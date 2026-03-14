package com.Imara.imara.exception;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;

/**
 * Maps Spring DataAccessException to ApplicationException with appropriate ErrorCode.
 */
public final class ExceptionMapper {

    private ExceptionMapper() {
    }

    public static ApplicationException map(DataAccessException ex) {
        if (ex instanceof org.springframework.dao.DuplicateKeyException) {
            return new ApplicationException(ErrorCode.DUPLICATE_KEY, "Duplicate key: " + ex.getMessage());
        }
        if (ex instanceof DataIntegrityViolationException) {
            return new ApplicationException(ErrorCode.DATA_INTEGRITY_VIOLATION, ex.getMessage());
        }
        if (ex instanceof EmptyResultDataAccessException) {
            return new ApplicationException(ErrorCode.DATA_NOT_FOUND, "Record not found: " + ex.getMessage());
        }
        return new ApplicationException(ErrorCode.DATABASE_ERROR, "Database error: " + ex.getMessage());
    }

    public static ApplicationException map(String context, DataAccessException ex) {
        if (ex instanceof org.springframework.dao.DuplicateKeyException) {
            return new ApplicationException(ErrorCode.DUPLICATE_KEY, context + " - " + ex.getMessage());
        }
        if (ex instanceof DataIntegrityViolationException) {
            return new ApplicationException(ErrorCode.DATA_INTEGRITY_VIOLATION, context + " - " + ex.getMessage());
        }
        if (ex instanceof EmptyResultDataAccessException) {
            return new ApplicationException(ErrorCode.DATA_NOT_FOUND, context + " - " + ex.getMessage());
        }
        return new ApplicationException(ErrorCode.DATABASE_ERROR, context + " - " + ex.getMessage());
    }
}
