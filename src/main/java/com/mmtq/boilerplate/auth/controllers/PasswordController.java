package com.mmtq.boilerplate.auth.controllers;

import com.mmtq.boilerplate.auth.DTOs.ForgotPasswordRequest;
import com.mmtq.boilerplate.auth.DTOs.ResetPasswordRequest;
import com.mmtq.boilerplate.auth.services.ResetPasswordService;
import com.mmtq.boilerplate.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/password")
@RequiredArgsConstructor
public class PasswordController {

    private final ResetPasswordService authService;

    @PostMapping("/forgot-password")
    public ApiResponse<Void> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request
    ) {

        authService.forgotPassword(request);

        return new ApiResponse<>("Password reset email sent", 200, null);

    }

    @PostMapping("/reset-password")
    public ApiResponse<Void> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request
    ) {

        authService.resetPassword(request);

        return new ApiResponse<>("Password reset successful", 200, null);

    }
}
