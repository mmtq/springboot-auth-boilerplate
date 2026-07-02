package com.mmtq.boilerplate.auth.controllers;

import com.mmtq.boilerplate.auth.DTOs.*;
import com.mmtq.boilerplate.auth.models.User;
import com.mmtq.boilerplate.auth.services.AuthService;
import com.mmtq.boilerplate.common.exception.ApiException;
import com.mmtq.boilerplate.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ApiResponse<RegisterResponse> register(
            @RequestBody RegisterRequest request
    ) {

        RegisterResponse response = authService.register(request);

        return new ApiResponse<>("User registered successfully", HttpStatus.OK.value(), response);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @RequestBody LoginRequest request
    ) {

        LoginResponse result = authService.login(request);

        ResponseCookie cookie = ResponseCookie.from("sid", result.getToken())
                .httpOnly(true)
                .secure(false) // true in production
                .sameSite("Lax")
                .path("/")
                .maxAge(60L * 60 * 24 * 30)
                .build();

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body( new ApiResponse<>("Login successful", HttpStatus.OK.value(), result) );
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @CookieValue(value = "sid", required = false) String token
    ) {

        authService.logout(token);

        ResponseCookie cookie = ResponseCookie.from("sid", "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new ApiResponse<>("Logout successful", HttpStatus.OK.value(), null));
    }

    @GetMapping("/me")
    public ApiResponse<UserResponse> me(
            Authentication authentication
    ) {

        if (authentication == null) {
            throw new ApiException("Unauthorized", "UNAUTHORIZED", HttpStatus.UNAUTHORIZED);
        }

        User user = (User) authentication.getPrincipal();

        assert user != null;

        UserResponse response = new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getName()
        );

        return new ApiResponse<>("User info retrieved successfully", HttpStatus.OK.value(), response);
    }
}