package com.fadhli.auth_server.dto.token;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccessTokenResponseDto {
     private String accessToken;
     private String refreshToken;
     private String tokenType = "Bearer";
     private Long expiresIn;
 }