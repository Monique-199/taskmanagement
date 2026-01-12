package com.kerubo.TaskManagement.security;

import org.springframework.security.web.AuthenticationEntryPoint;
import jakarta.servlet.http.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        try {
            response.getWriter().write("""
                {
                  "error": "Unauthorized",
                  "message": "Token missing, invalid or expired"
                }
            """);
        } catch (Exception ignored) {}
    }
}
