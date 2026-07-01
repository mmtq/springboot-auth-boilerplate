package com.mmtq.boilerplate.auth.controllers;

import com.mmtq.boilerplate.auth.DTOs.LoginRequest;
import com.mmtq.boilerplate.auth.services.AuthService;
import com.mmtq.boilerplate.auth.services.SessionService;
import com.mmtq.boilerplate.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final SessionService sessionService;

    @PostMapping("/register")
    public String register(@RequestBody LoginRequest req) {
        return authService.register(req.getEmail(), req.getPassword());
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(
            @RequestBody LoginRequest req,
            HttpServletResponse res
    ) {

        String token = authService.login(req);

        ResponseCookie cookie = ResponseCookie.from("sid", token)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(60 * 60 * 24 * 30)
                .build();

        res.addHeader("Set-Cookie", cookie.toString());

        return ResponseEntity.ok(
                ApiResponse.success("Login successful")
        );
    }
    @PostMapping("/logout")
    public String logout(@CookieValue(name = "sid", required = false) String token,
                         HttpServletResponse res) {

        if (token != null) {
            sessionService.deleteSession(token);
        }

        ResponseCookie cookie = ResponseCookie.from("sid", "")
                .maxAge(0)
                .path("/")
                .build();

        res.addHeader("Set-Cookie", cookie.toString());

        return "logged out";
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<?>> me(Authentication auth) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Current user fetched",
                        auth.getPrincipal()
                )
        );
    }
}