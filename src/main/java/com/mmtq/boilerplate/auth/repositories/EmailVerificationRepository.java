package com.mmtq.boilerplate.auth.repositories;

import com.mmtq.boilerplate.auth.models.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EmailVerificationRepository
        extends JpaRepository<EmailVerification, UUID> {

    Optional<EmailVerification> findByTokenHash(String tokenHash);

    void deleteByUserId(UUID userId);

}