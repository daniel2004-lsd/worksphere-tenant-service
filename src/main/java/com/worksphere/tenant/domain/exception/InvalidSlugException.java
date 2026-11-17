package com.worksphere.tenant.domain.exception;

public class InvalidSlugException extends RuntimeException {
    public InvalidSlugException(String message) {
        super(message);
    }
}
