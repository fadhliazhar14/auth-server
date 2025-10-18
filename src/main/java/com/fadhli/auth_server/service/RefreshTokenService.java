package com.fadhli.auth_server.service;

import com.fadhli.auth_server.constant.ResponseMessages;
import com.fadhli.auth_server.dto.token.RefreshTokenResponse;
import com.fadhli.auth_server.entity.RefreshToken;
import com.fadhli.auth_server.exception.BusinessValidationException;
import com.fadhli.auth_server.repository.RefreshTokenRepository;
import com.fadhli.auth_server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Value("${jwt.refresh-expiration}")
    private long refreshTokenExpirationInMilis;

    public String generateRefreshToken(Long userId) {
        // Delete expired refresh token
        refreshTokenRepository.deleteByUserId(userId);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(userRepository.findById(userId)
                .orElseThrow(() -> new BusinessValidationException(ResponseMessages.notFound("User"))));
        refreshToken.setExpiresAt(Instant.now().plusMillis(refreshTokenExpirationInMilis));
        refreshToken.setToken(UUID.randomUUID().toString());

        return refreshTokenRepository.save(refreshToken).getToken();
    }

    public RefreshTokenResponse generateNewRefreshToken(String oldToken) {
        RefreshToken oldRefreshToken = refreshTokenRepository.findByToken(oldToken)
                .orElseThrow(() -> new BusinessValidationException("Invalid refresh token"));

        verifyExpiration(oldRefreshToken);

        RefreshToken newRefreshToken = rotateRefreshToken(oldRefreshToken);
        return new RefreshTokenResponse(
                "",
                newRefreshToken.getToken(),
                newRefreshToken.getExpiresAt().getEpochSecond(),
                "Bearer"
        );
    }

    public RefreshToken findByToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new BusinessValidationException("Invalid refresh token"));
    }

    private void verifyExpiration(RefreshToken token) {
        if (token.getExpiresAt().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);

            throw new BusinessValidationException("Refresh token is expired. Please login again.");
        }
    }

    @Transactional
    private RefreshToken rotateRefreshToken(RefreshToken oldToken) {
        oldToken.setToken(UUID.randomUUID().toString());
        oldToken.setExpiresAt(Instant.now().plusMillis(refreshTokenExpirationInMilis));

        return refreshTokenRepository.save(oldToken);
    }
}
