package com.mmtq.boilerplate.auth.repositories;

import com.mmtq.boilerplate.auth.models.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SessionRepository extends JpaRepository<Session, UUID> {
    Optional<Session> findByTokenHash(String tokenHash);
    void deleteByUserId(UUID userId);
    List<Session> findAllByUserIdOrderByCreatedAtDesc(UUID userId);

    void deleteAllByUserId(UUID userId);
}