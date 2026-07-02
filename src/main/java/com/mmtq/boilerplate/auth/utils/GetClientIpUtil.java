package com.mmtq.boilerplate.auth.utils;

import jakarta.servlet.http.HttpServletRequest;

public class GetClientIpUtil {
    public static String getClientIp(HttpServletRequest request) {

        String forwarded = request.getHeader("X-Forwarded-For");

        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }

        return request.getRemoteAddr();
    }
}
