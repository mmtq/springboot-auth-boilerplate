package com.mmtq.boilerplate.auth.DTOs;

import lombok.Getter;

@Getter
public class LoginRequest {
    private String email;
    private String password;
}