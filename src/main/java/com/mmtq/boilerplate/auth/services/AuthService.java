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
    private final EmailVerificationService emailVerificationService;
    private final EmailService emailService;

    public RegisterResponse register(RegisterRequest request) {

        String normalizedEmail = request.getEmail().trim().toLowerCase();
        
        if (userRepository.findByEmail(normalizedEmail).isPresent()) {
            throw new ApiException("Email already in use", "EMAIL_ALREADY_IN_USE", org.springframework.http.HttpStatus.BAD_REQUEST);
        }

        User user = User.builder()
                .name(request.getName())
                .email(normalizedEmail)
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .enabled(true)
                .emailVerified(false)
                .build();

        userRepository.save(user);

        String token =
                emailVerificationService.createVerification(user);

        emailService.sendVerification(
                user.getEmail(),
                token
        );

        RegisterResponse response = new RegisterResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setName(user.getName());
        
        return response;
    }

    public LoginResponse login(LoginRequest request) {

        String normalizedEmail = request.getEmail().trim().toLowerCase();
        
        User user = userRepository.findByEmail(normalizedEmail)
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