package com.mmtq.boilerplate.common.exception;

import org.springframework.http.HttpStatus;

import java.util.UUID;

public class NotFoundException extends ApiException {

    public NotFoundException(String message) {
        super(message, "NOT_FOUND", HttpStatus.NOT_FOUND);
    }

    public NotFoundException(String entityName, UUID entityId) {
        super(
            "Request for " + entityName + " with id " + entityId + " not found",
            "NOT_FOUND",
            HttpStatus.NOT_FOUND
        );
    }

    public NotFoundException(String entityName, String entityProperty, String entityPropertyValue) {
        super(
            "Request for " + entityName + " with " + entityProperty + " " + entityPropertyValue + " not found",
            "NOT_FOUND",
            HttpStatus.NOT_FOUND
        );
    }
}
