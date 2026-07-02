package com.mmtq.boilerplate.auth.DTOs;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        String name
) {
}