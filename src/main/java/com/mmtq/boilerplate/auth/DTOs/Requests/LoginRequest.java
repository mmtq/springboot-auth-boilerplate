package com.mmtq.boilerplate.auth.DTOs.Requests;

import lombok.Getter;

@Getter
public class LoginRequest {
    private String email;
    private String password;
}