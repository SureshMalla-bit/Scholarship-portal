package com.scholarship.management.exception;

import org.springframework.http.HttpStatus;

/** Thrown by the services when a request breaks a rule, for example "deadline has passed". */
public class ApiException extends RuntimeException {

    private final HttpStatus status;

    public ApiException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
