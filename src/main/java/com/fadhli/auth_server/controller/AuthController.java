package com.fadhli.auth_server.controller;

import com.fadhli.auth_server.dto.auth.SigninRequestDto;
import com.fadhli.auth_server.dto.auth.SignupRequestDto;
import com.fadhli.auth_server.dto.auth.SignupResponseDto;
import com.fadhli.auth_server.dto.token.AccessTokenResponse;
import com.fadhli.auth_server.dto.token.RefreshTokenRequest;
import com.fadhli.auth_server.dto.token.RefreshTokenResponse;
import com.fadhli.auth_server.entity.RefreshToken;
import com.fadhli.auth_server.service.AuthService;
import com.fadhli.auth_server.service.RefreshTokenService;
import com.fadhli.auth_server.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {
    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignupResponseDto>> signup(@Valid @RequestBody SignupRequestDto signupRequest) {
        SignupResponseDto createdUser = authService.register(signupRequest);
        ApiResponse<SignupResponseDto> response = ApiResponse.success(HttpStatus.CREATED.value(), "User has been registered successfully", createdUser);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/api/users/{id}")
                .buildAndExpand(createdUser.getId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @PostMapping("/signin")
    public ResponseEntity<AccessTokenResponse> authenticateUser(@Valid @RequestBody SigninRequestDto signinRequest) {
        AccessTokenResponse jwtResponse = authService.authenticate(signinRequest);

        return ResponseEntity.ok(jwtResponse);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<RefreshTokenResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        RefreshToken currentRefreshToken = refreshTokenService.findByToken(refreshTokenRequest.getRefreshToken());
        String newAccessToken = authService.generateNewAccessToken(currentRefreshToken.getUser().getUsername());
        RefreshTokenResponse response = refreshTokenService.generateNewRefreshToken(refreshTokenRequest.getRefreshToken());
        response.setAccessToken(newAccessToken);

        return ResponseEntity.ok(response);
    }
}
