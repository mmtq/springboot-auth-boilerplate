package com.mmtq.boilerplate.auth.services;

import com.mmtq.boilerplate.auth.DTOs.ChangePasswordRequest;
import com.mmtq.boilerplate.auth.DTOs.ForgotPasswordRequest;
import com.mmtq.boilerplate.auth.DTOs.ResetPasswordRequest;
import com.mmtq.boilerplate.auth.models.PasswordReset;
import com.mmtq.boilerplate.auth.models.User;
import com.mmtq.boilerplate.auth.repositories.PasswordResetRepository;
import com.mmtq.boilerplate.auth.repositories.SessionRepository;
import com.mmtq.boilerplate.auth.repositories.UserRepository;
import com.mmtq.boilerplate.auth.utils.HashUtil;
import com.mmtq.boilerplate.auth.utils.TokenUtil;
import com.mmtq.boilerplate.common.exception.ApiException;
import com.mmtq.boilerplate.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PasswordService {

    private final UserRepository userRepository;
    private final PasswordResetRepository passwordResetRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final SessionRepository sessionRepository;
    private final SessionService sessionService;


    public void forgotPassword(ForgotPasswordRequest request) {

        Optional<User> optional = userRepository.findByEmail(request.getEmail());

        if (optional.isEmpty()) {
            return;
        }

        User user = optional.get();

        passwordResetRepository.deleteByUserId(user.getId());

        String rawToken = TokenUtil.generateToken();

        PasswordReset reset = PasswordReset.builder()
                .userId(user.getId())
                .tokenHash(HashUtil.sha256(rawToken))
                .expiresAt(Instant.now().plus(1, ChronoUnit.HOURS))
                .build();

        passwordResetRepository.save(reset);

        emailService.sendPasswordReset(
                user.getEmail(),
                rawToken
        );
    }

    public void resetPassword(ResetPasswordRequest request) {

        String hash = HashUtil.sha256(request.getToken());

        PasswordReset reset = passwordResetRepository
                .findByTokenHash(hash)
                .orElseThrow(() -> new NotFoundException("Reset token not found"));

        if (reset.getExpiresAt().isBefore(Instant.now())) {

            passwordResetRepository.delete(reset);

            throw new ApiException(
                    "Reset token has expired",
                    "INVALID_CREDENTIALS",
                    HttpStatus.UNAUTHORIZED
            );
        }

        User user = userRepository
                .findById(reset.getUserId())
                .orElseThrow(() -> new NotFoundException("User", reset.getUserId()));

        user.setPasswordHash(
                passwordEncoder.encode(request.getPassword())
        );

        userRepository.save(user);

        sessionRepository.deleteByUserId(user.getId());

        passwordResetRepository.delete(reset);
    }

    public String changePassword(
            User user,
            ChangePasswordRequest request
    ) {

        if (!passwordEncoder.matches(
                request.getCurrentPassword(),
                user.getPasswordHash()
        )) {
            throw new ApiException(
                    "Current password is incorrect",
                    "INVALID_CREDENTIALS",
                    HttpStatus.UNAUTHORIZED
            );
        }

        user.setPasswordHash(
                passwordEncoder.encode(request.getNewPassword())
        );

        userRepository.save(user);

        sessionRepository.deleteByUserId(user.getId());

        return sessionService.createSession(user);
    }
}
