package com.fadhli.auth_server.controller;

import com.fadhli.auth_server.constant.ResponseMessages;
import com.fadhli.auth_server.dto.auth.*;
import com.fadhli.auth_server.dto.token.RefreshTokenDto;
import com.fadhli.auth_server.dto.token.RefreshTokenResponseDto;
import com.fadhli.auth_server.dto.user.UserSigninResponseDto;
import com.fadhli.auth_server.entity.RefreshToken;
import com.fadhli.auth_server.service.AuthService;
import com.fadhli.auth_server.service.PasswordResetService;
import com.fadhli.auth_server.service.RefreshTokenService;
import com.fadhli.auth_server.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {
    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final PasswordResetService passwordResetService;
    private final String accessTokenCookieName = "ACCESS_TOKEN";
    private final String refreshTokenCookieName = "REFRESH_TOKEN";
    private final String accessTokenCookiePath = "/";
    private final String refreshTokenCookiePath = "/api/auth";
    private long accessTokenCookieExpiry;
    private long refreshTokenCookieExpiry;

    @Value("${jwt.expiration}")
    private Long accessTokenExpiration;

    @Value("${jwt.refresh-expiration}")
    private Long refreshTokenExpiration;

    @Value("${cookie.is-secure}")
    private Boolean isCookieSecure;

    @EventListener(ApplicationReadyEvent.class)
    public void setTokenExpiration() {
        accessTokenCookieExpiry = accessTokenExpiration / 60000;
        refreshTokenCookieExpiry = refreshTokenExpiration / 60000;
    }


    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignupResponseDto>> signup(@Valid @RequestBody SignupRequestDto signupRequest) {
        SignupResponseDto createdUser = authService.register(signupRequest);
        ApiResponse<SignupResponseDto> response = ApiResponse.success(HttpStatus.CREATED.value(),
                "User has been registered successfully", createdUser);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/api/users/{id}")
                .buildAndExpand(createdUser.getId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @PostMapping("/signin")
    public ResponseEntity<ApiResponse<UserSigninResponseDto>> authenticateUser(@Valid @RequestBody SigninRequestDto signinRequest) {
        var authResult = authService.authenticate(signinRequest);

        ApiResponse<UserSigninResponseDto> response = ApiResponse.success(ResponseMessages.SUCCESS, authResult.getUserData());
        ResponseCookie accessTokenCookie = authService.setCookieForToken(accessTokenCookieName, authResult.getAccessToken(), accessTokenCookiePath, accessTokenCookieExpiry, isCookieSecure);
        ResponseCookie refreshTokenCookie = authService.setCookieForToken(refreshTokenCookieName, authResult.getRefreshToken(), refreshTokenCookiePath, refreshTokenCookieExpiry, isCookieSecure);

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        responseHeaders.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        return ResponseEntity
                .ok()
                .headers(responseHeaders)
                .body(response);
    }

    @PostMapping("/signout")
    public ResponseEntity<ApiResponse<?>> signout(@CookieValue(name = refreshTokenCookieName, required = false) String refreshToken) {
        if (refreshToken != null && !refreshToken.isBlank()) {
            refreshTokenService.revoke(refreshToken);
        }

        ResponseCookie accessTokenCookie = authService.setCookieForToken(accessTokenCookieName, null, accessTokenCookiePath, 0, isCookieSecure);
        ResponseCookie refreshTokenCookie = authService.setCookieForToken(refreshTokenCookieName, null, refreshTokenCookiePath, 0, isCookieSecure);

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        responseHeaders.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        SecurityContextHolder.clearContext();

        return ResponseEntity
                .ok()
                .headers(responseHeaders)
                .body(ApiResponse.success(ResponseMessages.SUCCESS, null));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserSessionResponseDto>> getCurrentSession() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        ApiResponse<UserSessionResponseDto> response = ApiResponse.success(ResponseMessages.SUCCESS, authService.getCurrentSession(username));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/refresh-token")
    public ResponseEntity<ApiResponse<RefreshTokenResponseDto>> refreshToken(
            @Valid @CookieValue(name = refreshTokenCookieName, required = false) String refreshToken) {
        RefreshToken currentRefreshToken = refreshTokenService.findByToken(refreshToken);
        String newAccessToken = authService.generateNewAccessToken(currentRefreshToken.getUser().getUsername());
        RefreshTokenDto refreshTokenDto = refreshTokenService.generateNew(refreshToken);
        String newRefreshToken = refreshTokenDto.getRefreshToken();
        ResponseCookie accessTokenCookie = authService.setCookieForToken(accessTokenCookieName, newAccessToken, accessTokenCookiePath, accessTokenCookieExpiry, isCookieSecure);
        ResponseCookie refreshTokenCookie = authService.setCookieForToken(refreshTokenCookieName, newRefreshToken, refreshTokenCookiePath, refreshTokenCookieExpiry, isCookieSecure);
        ApiResponse<RefreshTokenResponseDto> response = ApiResponse.success(
                ResponseMessages.SUCCESS,
                new RefreshTokenResponseDto(
                        refreshTokenDto.getRefreshTokenExpiry(),
                        refreshTokenDto.getTokenType()
                )
        );

        HttpHeaders ResponseHeaders = new HttpHeaders();
        ResponseHeaders.add(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        ResponseHeaders.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        return ResponseEntity
                .ok()
                .headers(ResponseHeaders)
                .body(response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(@RequestParam String email) {
        passwordResetService.createPasswordResetToken(email);
        ApiResponse<String> response = ApiResponse.success("Reset password link sent to email!", null);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(@RequestBody ResetPasswordRequestDto request) {
        passwordResetService.resetPassword(request.getToken(), request.getNewPassword());
        ApiResponse<String> response = ApiResponse.success("Password reset successfully!", null);

        return ResponseEntity.ok(response);
    }
}
