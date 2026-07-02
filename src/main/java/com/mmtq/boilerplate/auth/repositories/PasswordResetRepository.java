package com.mmtq.boilerplate.auth.repositories;

import com.mmtq.boilerplate.auth.models.PasswordReset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PasswordResetRepository extends JpaRepository<PasswordReset, UUID> {

    Optional<PasswordReset> findByTokenHash(String tokenHash);

    void deleteByUserId(UUID userId);

}