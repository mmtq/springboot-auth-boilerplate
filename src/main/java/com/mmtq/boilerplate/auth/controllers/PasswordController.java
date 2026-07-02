package com.mmtq.boilerplate.auth.controllers;

import com.mmtq.boilerplate.auth.DTOs.Requests.ChangePasswordRequest;
import com.mmtq.boilerplate.auth.DTOs.Requests.ForgotPasswordRequest;
import com.mmtq.boilerplate.auth.DTOs.Requests.ResetPasswordRequest;
import com.mmtq.boilerplate.auth.models.User;
import com.mmtq.boilerplate.auth.services.PasswordService;
import com.mmtq.boilerplate.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequestMapping("/auth/password")
@RequiredArgsConstructor
public class PasswordController {

    private final PasswordService authService;

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

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequest request
    ) {

        User user = (User) authentication.getPrincipal();

        String sessionToken = authService.changePassword(user, request);

        ResponseCookie cookie = ResponseCookie.from("sid", sessionToken)
                .httpOnly(true)
                .secure(false) // true in production
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ofDays(30))
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new ApiResponse<>("Password changed successfully", 200, null));
    }
}
