package com.mmtq.boilerplate.auth.services;

import com.mmtq.boilerplate.auth.DTOs.LoginRequest;
import com.mmtq.boilerplate.auth.models.User;
import com.mmtq.boilerplate.auth.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final SessionService sessionService;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public String register(String email, String password) {

        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email exists");
        }

        User user = User.builder()
                .email(email)
                .passwordHash(encoder.encode(password))
                .build();

        userRepository.save(user);

        return "registered";
    }

    public String login(LoginRequest req) {

        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!encoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }

        return sessionService.createSession(user);
    }
}