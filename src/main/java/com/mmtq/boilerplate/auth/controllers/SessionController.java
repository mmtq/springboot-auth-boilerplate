package com.mmtq.boilerplate.auth.controllers;

import com.mmtq.boilerplate.auth.DTOs.Responses.SessionResponse;
import com.mmtq.boilerplate.auth.models.Session;
import com.mmtq.boilerplate.auth.models.User;
import com.mmtq.boilerplate.auth.services.SessionService;
import com.mmtq.boilerplate.auth.utils.AuthPrincipal;
import com.mmtq.boilerplate.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;

    @GetMapping
    public ApiResponse<List<SessionResponse>> getSessions(
            Authentication authentication
    ) {

        AuthPrincipal principal =
                (AuthPrincipal) authentication.getPrincipal();

        User user = principal.getUser();
        Session currentSession = principal.getSession();

        return new ApiResponse<>(
                "Sessions retrieved successfully.",
                200,
                sessionService.getSessions(user.getId(), currentSession.getId())
        );
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> logoutSession(
            Authentication authentication,
            @PathVariable UUID id
    ) {

        User user = (User) authentication.getPrincipal();

        sessionService.deleteSession(user.getId(), id);

        return new ApiResponse<>(
                "Session deleted successfully.",
                200,
                null
        );
    }

    @DeleteMapping
    public ApiResponse<Void> logoutAllExceptCurrent(
            Authentication authentication
    ) {

        AuthPrincipal principal =
                (AuthPrincipal) authentication.getPrincipal();

        User user = principal.getUser();
        Session currentSession = principal.getSession();

        sessionService.deleteAllExceptCurrent(
                user.getId(),
                currentSession.getId()
        );

        return new ApiResponse<>(
                "All sessions deleted successfully except the current one.",
                200,
                null
        );
    }
}