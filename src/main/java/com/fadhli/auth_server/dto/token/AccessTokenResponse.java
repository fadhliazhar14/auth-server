package com.fadhli.auth_server.dto.token;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
public class AccessTokenResponse {
     private String accessToken;
     private String refreshToken;
     private String tokenType = "Bearer";
     private Long expiresIn;
 }