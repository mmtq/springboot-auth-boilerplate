package com.mmtq.boilerplate.auth.services;

import com.mmtq.boilerplate.auth.DTOs.Responses.SessionResponse;
import com.mmtq.boilerplate.auth.models.Session;
import com.mmtq.boilerplate.auth.models.User;
import com.mmtq.boilerplate.auth.repositories.SessionRepository;
import com.mmtq.boilerplate.auth.utils.HashUtil;
import com.mmtq.boilerplate.auth.utils.TokenUtil;
import com.mmtq.boilerplate.common.exception.ApiException;
import com.mmtq.boilerplate.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;

    @Value("${auth.session.expiry-seconds:2592000}") // 30 days
    private long expirySeconds;

    public String createSession(
            User user,
            String ipAddress,
            String userAgent
    ) {

        String rawToken = TokenUtil.generateToken();
        String tokenHash = HashUtil.sha256(rawToken);

        Session session = Session.builder()
                .userId(user.getId())
                .tokenHash(tokenHash)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .createdAt(Instant.now())
                .lastUsedAt(Instant.now())
                .expiresAt(Instant.now().plus(30, ChronoUnit.DAYS))
                .build();

        sessionRepository.save(session);

        return rawToken;
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

    public void deleteAllSessionsByUserId (UUID userId) {
        sessionRepository.deleteAllByUserId(userId);
    }

    public List<SessionResponse> getSessions(
            UUID userId,
            UUID currentSessionId
    ) {

        List<Session> sessions =
                sessionRepository.findAllByUserIdOrderByCreatedAtDesc(userId);

        return sessions.stream()
                .map(session -> SessionResponse.builder()
                        .id(session.getId())
                        .createdAt(session.getCreatedAt())
                        .lastUsedAt(session.getLastUsedAt())
                        .expiresAt(session.getExpiresAt())
                        .ipAddress(session.getIpAddress())
                        .userAgent(session.getUserAgent())
                        .current(session.getId().equals(currentSessionId))
                        .build())
                .toList();
    }

    public void deleteSession(
            UUID userId,
            UUID sessionId
    ) {

        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new NotFoundException("Session", sessionId));

        if (!session.getUserId().equals(userId)) {
            throw new ApiException(
                    "You do not have permission to delete this session",
                    "FORBIDDEN",
                    HttpStatus.FORBIDDEN
            );
        }

        sessionRepository.delete(session);

    }

    public void deleteAllExceptCurrent(
            UUID userId,
            UUID currentSessionId
    ) {

        List<Session> sessions =
                sessionRepository.findAllByUserIdOrderByCreatedAtDesc(userId);

        for (Session session : sessions) {

            if (!session.getId().equals(currentSessionId)) {
                sessionRepository.delete(session);
            }

        }

    }
}