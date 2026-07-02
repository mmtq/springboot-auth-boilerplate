package com.mmtq.boilerplate.common.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends ApiException {

    public NotFoundException(String entityName, Long entityId) {
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
