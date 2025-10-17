package com.fadhli.auth_server.dto.auth;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SignupResponseDto {
    private Long id;
    private String name;
    private String username;
    private String email;
}
