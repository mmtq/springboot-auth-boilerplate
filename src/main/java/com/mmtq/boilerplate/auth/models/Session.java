package com.mmtq.boilerplate.auth.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String tokenHash;

    @Column(nullable = false)
    private UUID userId;

    private Instant expiresAt;

    private Instant createdAt;

    private Instant lastUsedAt;
}