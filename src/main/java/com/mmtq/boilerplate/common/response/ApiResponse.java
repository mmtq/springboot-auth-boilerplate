package com.mmtq.boilerplate.common.response;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class ApiResponse<T> {
    private String message;
    private int status;
    private T data;

    public ApiResponse(String message, int status, T data) {
        this.message = message;
        this.status = status;
        this.data = data;
    }

    public ApiResponse(String message, T data) {
        this.message = message;
        this.data = data;
        this.status = HttpStatus.OK.value();
    }

    public ApiResponse(String message, int status) {
        this.message = message;
        this.status = status;
    }

    public ApiResponse(String message) {
        this.message = message;
        this.status = HttpStatus.OK.value();
    }

    /**
     * Factory method for error responses with custom status
     */
    public static <T> ApiResponse<T> error(String message, int status, T data) {
        return new ApiResponse<>(message, status, data);
    }

    /**
     * Factory method for error responses with default error status
     */
    public static <T> ApiResponse<T> error(String message, T data) {
        return new ApiResponse<>(message, HttpStatus.INTERNAL_SERVER_ERROR.value(), data);
    }
}
