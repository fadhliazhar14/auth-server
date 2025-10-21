package com.fadhli.auth_server.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class JwtExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(JwtExceptionHandler.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    public void handle(HttpServletResponse response, Exception e) throws IOException {
        String message;
        int status = HttpServletResponse.SC_UNAUTHORIZED;

        if (e instanceof io.jsonwebtoken.ExpiredJwtException) {
            message = "JWT token has expired";
        } else if (e instanceof io.jsonwebtoken.MalformedJwtException) {
            message = "JWT token is malformed";
        } else if (e instanceof io.jsonwebtoken.SignatureException) {
            message = "Invalid JWT signature";
        } else if (e instanceof io.jsonwebtoken.UnsupportedJwtException) {
            message = "Unsupported JWT token";
        } else {
            message = "Invalid JWT token";
        }

        logger.warn("{}: {}", message, e.getMessage());
        writeErrorResponse(response, status, message);
    }

    private void writeErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write(mapper.writeValueAsString(Map.of("error", message)));
    }
}