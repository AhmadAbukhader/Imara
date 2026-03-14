package com.Imara.imara.exception;

import org.springframework.http.HttpStatus;

/**
 * Generic application exception for business/validation errors.
 * Use ErrorCode to determine HTTP status and default message; optional override message for context.
 */
public class ApplicationException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String overrideMessage;

    public ApplicationException(ErrorCode errorCode) {
        super(errorCode.getDefaultMessage());
        this.errorCode = errorCode;
        this.overrideMessage = null;
    }

    public ApplicationException(ErrorCode errorCode, String overrideMessage) {
        super(overrideMessage != null ? overrideMessage : errorCode.getDefaultMessage());
        this.errorCode = errorCode;
        this.overrideMessage = overrideMessage;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public HttpStatus getStatus() {
        return errorCode.getStatus();
    }

    @Override
    public String getMessage() {
        return overrideMessage != null ? overrideMessage : errorCode.getDefaultMessage();
    }
}
