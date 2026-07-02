package com.mmtq.boilerplate.auth.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    public void sendVerification(
            String email,
            String token
    ) {

        System.out.println(
                """
                ==========================

                Verification Email

                %s/auth/verify-email?token=%s

                ==========================
                """.formatted(
                        "http://localhost:8080",
                        token
                )
        );
    }

    public void sendPasswordReset(String email, String token) {

        System.out.println("""
            
            RESET PASSWORD

            http://localhost:8080/auth/reset-password?token=%s
            
            """.formatted(token));
    }

}
