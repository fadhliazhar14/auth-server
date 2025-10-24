package com.fadhli.auth_server.dto.token;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RefreshTokenResponseDto {
    private String accessToken;
    private String refreshToken;
    private Long refreshTokenExpiry;
    private String tokenType = "Bearer";
}