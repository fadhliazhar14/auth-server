package com.fadhli.auth_server.dto.auth;

import com.fadhli.auth_server.dto.user.UserSigninResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UserSessionResponseDto {
    private Boolean isAuthenticated;
    private UserSigninResponseDto userData;
}
