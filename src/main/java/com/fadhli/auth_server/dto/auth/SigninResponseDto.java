package com.fadhli.auth_server.dto.auth;

import com.fadhli.auth_server.dto.token.AccessTokenResponseDto;
import com.fadhli.auth_server.dto.user.UserSigninResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.ResponseCookie;

@Data
@AllArgsConstructor
public class SigninResponseDto {
    private UserSigninResponseDto userData;
    private String accessToken;
    private String refreshToken;
}
