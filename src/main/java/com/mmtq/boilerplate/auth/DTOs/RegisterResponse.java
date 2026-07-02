package com.mmtq.boilerplate.auth.DTOs;

import java.util.UUID;

public record RegisterResponse(
        UUID id,
        String email,
        String name
) {
}