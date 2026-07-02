package com.mmtq.boilerplate.auth.services;

import com.mmtq.boilerplate.auth.DTOs.LoginRequest;
import com.mmtq.boilerplate.auth.DTOs.LoginResponse;
import com.mmtq.boilerplate.auth.DTOs.RegisterRequest;
import com.mmtq.boilerplate.auth.DTOs.RegisterResponse;
import com.mmtq.boilerplate.auth.models.User;
import com.mmtq.boilerplate.auth.repositories.UserRepository;
import com.mmtq.boilerplate.common.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final SessionService sessionService;
    private final PasswordEncoder passwordEncoder;

    public RegisterResponse register(RegisterRequest request) {

        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new ApiException("Email already in use", "EMAIL_ALREADY_IN_USE", org.springframework.http.HttpStatus.BAD_REQUEST);
        }

        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .enabled(true)
                .emailVerified(false)
                .build();

        userRepository.save(user);

        return new RegisterResponse(
                user.getId(),
                user.getEmail(),
                user.getName()
        );
    }

    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ApiException("Invalid credentials", "INVALID_CREDENTIALS", org.springframework.http.HttpStatus.UNAUTHORIZED));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new ApiException("Invalid credentials", "INVALID_CREDENTIALS", org.springframework.http.HttpStatus.UNAUTHORIZED);
        }

        String sessionToken = sessionService.createSession(user);

        return new LoginResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                sessionToken
        );
    }

    public void logout(String sessionToken) {
        if (sessionToken != null) {
            sessionService.deleteSession(sessionToken);
        }
    }
}