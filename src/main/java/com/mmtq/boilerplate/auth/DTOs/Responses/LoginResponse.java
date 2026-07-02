package com.mmtq.boilerplate.auth.DTOs.Responses;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class LoginResponse {
    private UUID id;
    private String email;
    private String name;
    private String token;
}