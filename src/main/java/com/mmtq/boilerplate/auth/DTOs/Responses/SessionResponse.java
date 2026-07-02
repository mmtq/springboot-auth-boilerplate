package com.mmtq.boilerplate.auth.DTOs.Responses;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SessionResponse {

    private UUID id;

    private Instant createdAt;

    private Instant lastUsedAt;

    private Instant expiresAt;

    private String ipAddress;

    private String userAgent;

    private boolean current;

}