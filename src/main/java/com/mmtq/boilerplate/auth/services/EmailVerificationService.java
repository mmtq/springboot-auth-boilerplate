package com.mmtq.boilerplate.auth.services;

import com.mmtq.boilerplate.auth.models.EmailVerification;
import com.mmtq.boilerplate.auth.models.User;
import com.mmtq.boilerplate.auth.repositories.EmailVerificationRepository;
import com.mmtq.boilerplate.auth.repositories.UserRepository;
import com.mmtq.boilerplate.auth.utils.HashUtil;
import com.mmtq.boilerplate.auth.utils.TokenUtil;
import com.mmtq.boilerplate.common.exception.ApiException;
import com.mmtq.boilerplate.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final EmailVerificationRepository emailVerificationRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public String createVerification(User user) {

        emailVerificationRepository.deleteByUserId(user.getId());

        String rawToken = TokenUtil.generateToken();

        String hash = HashUtil.sha256(rawToken);

        EmailVerification verification =
                EmailVerification.builder()
                        .userId(user.getId())
                        .tokenHash(hash)
                        .expiresAt(
                                Instant.now().plus(24, ChronoUnit.HOURS)
                        )
                        .build();

        emailVerificationRepository.save(verification);

        return rawToken;
    }

    public void verify(String token) {

        // Trim token to remove any whitespace
        String trimmedToken = token.trim();
        
        String hash = HashUtil.sha256(trimmedToken);

        EmailVerification verification = emailVerificationRepository.findByTokenHash(hash)
                .orElseThrow(() -> new NotFoundException("Verification token not found"));

        if (verification.getExpiresAt().isBefore(Instant.now())) {
            emailVerificationRepository.delete(verification);
            throw new ApiException("Verification token has expired", "INVALID_CREDENTIALS", HttpStatus.UNAUTHORIZED);
        }

        User user = userRepository.findById(
                verification.getUserId()
        ).orElseThrow(() -> new NotFoundException("User", verification.getUserId()));

        user.setEmailVerified(true);

        userRepository.save(user);

        emailVerificationRepository.delete(verification);

    }

    public void resendVerificationEmail(String email) {
        String normalizedEmail = email.trim().toLowerCase();
        
        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new NotFoundException("User with email " + email + " not found"));

        if  (user.isEmailVerified()) {
            throw new ApiException("Email is already verified", "EMAIL_ALREADY_VERIFIED", HttpStatus.BAD_REQUEST);
        }

        String token = createVerification(user);

        emailService.sendVerification(
                user.getEmail(),
                token
        );
    }
}
