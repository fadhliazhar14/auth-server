package com.fadhli.auth_server.dto.auth;

import lombok.Data;

@Data
public class ResetPasswordRequestDto {
    private String token;
    private String newPassword;
}
