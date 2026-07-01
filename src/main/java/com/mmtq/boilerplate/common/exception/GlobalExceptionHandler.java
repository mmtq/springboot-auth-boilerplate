package com.mmtq.boilerplate.common.exception;

import com.mmtq.boilerplate.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<?> handleApiException(ApiException ex) {

        Map<String, Object> error = new HashMap<>();
        error.put("code", ex.getCode());
        error.put("message", ex.getMessage());

        return ResponseEntity
                .status(ex.getStatus())
                .body(ApiResponse.error("Request failed", error));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneric(Exception ex) {

        Map<String, Object> error = new HashMap<>();
        error.put("message", ex.getMessage());

        return ResponseEntity
                .status(500)
                .body(ApiResponse.error("Internal server error", error));
    }
}