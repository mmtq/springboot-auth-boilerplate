package com.mmtq.boilerplate.auth.services;

import com.mmtq.boilerplate.auth.models.Session;
import com.mmtq.boilerplate.auth.models.User;
import com.mmtq.boilerplate.auth.repositories.SessionRepository;
import com.mmtq.boilerplate.auth.utils.HashUtil;
import com.mmtq.boilerplate.auth.utils.TokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;

    @Value("${auth.session.expiry-seconds:2592000}") // 30 days
    private long expirySeconds;

    public String createSession(User user) {

        String rawToken = TokenUtil.generateToken();
        String hashed = HashUtil.sha256(rawToken);

        Session session = Session.builder()
                .tokenHash(hashed)
                .userId(user.getId())
                .createdAt(Instant.now())
                .lastUsedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(expirySeconds))
                .build();

        sessionRepository.save(session);

        return rawToken; // only returned once
    }

    public Session validate(String rawToken) {

        String hashed = HashUtil.sha256(rawToken);

        Session session = sessionRepository.findByTokenHash(hashed)
                .orElseThrow(() -> new RuntimeException("Invalid session"));

        if (session.getExpiresAt().isBefore(Instant.now())) {
            sessionRepository.delete(session);
            throw new RuntimeException("Session expired");
        }

        session.setLastUsedAt(Instant.now());
        sessionRepository.save(session);

        return session;
    }

    public void deleteSession(String rawToken) {
        String hashed = HashUtil.sha256(rawToken);
        sessionRepository.findByTokenHash(hashed)
                .ifPresent(sessionRepository::delete);
    }
}