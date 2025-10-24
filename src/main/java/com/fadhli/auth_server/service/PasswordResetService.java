package com.fadhli.auth_server.service;

import com.fadhli.auth_server.constant.ResponseMessages;
import com.fadhli.auth_server.entity.PasswordResetToken;
import com.fadhli.auth_server.entity.User;
import com.fadhli.auth_server.exception.PasswordValidationException;
import com.fadhli.auth_server.exception.ResourceNotFoundException;
import com.fadhli.auth_server.repository.PasswordResetTokenRepository;
import com.fadhli.auth_server.repository.UserRepository;
import com.fadhli.auth_server.util.PasswordValidatorUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService {
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    @Value("${app.reset-password-link}")
    private String resetPasswordLink;

    public void createPasswordResetToken(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(ResponseMessages.notFound("User")));

        if (!user.getIsActive()) {
            throw new RuntimeException("User is inactive. Please contact Administrator");
        }

        // Invalidate existing tokens for this user
        invalidateExistingToken(user);

        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(LocalDateTime.now().plusHours(1));

        passwordResetTokenRepository.save(resetToken);

        // Send password reset email
        String resetUrl = resetPasswordLink + token;

        try {
            emailService.sendPasswordResetEmail(user, token, resetUrl);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }

    public void invalidateExistingToken(User user) {
        var existingTokens = passwordResetTokenRepository.findByUser(user);

        if (!existingTokens.isEmpty()) {
            passwordResetTokenRepository.deleteAll(existingTokens);
        }
    }

    public void resetPassword(String token, String newPassword) {
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("Token cannot be null or empty");
        }

        List<String> errors = PasswordValidatorUtil.validatePassword(newPassword);
        if (!errors.isEmpty()) {
            throw new PasswordValidationException(errors);
        }

        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token.trim())
                .orElseThrow(() -> new RuntimeException("Invalid or expired reset token"));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            passwordResetTokenRepository.delete(resetToken);

            throw new RuntimeException("Reset token has expired. Please request a new password reset");
        }

        User user = resetToken.getUser();
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Clean up used token
        passwordResetTokenRepository.delete(resetToken);

        // Also invalidate any other tokens for this user
        invalidateExistingToken(user);
    }

    public boolean isValidResetToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }

        return passwordResetTokenRepository.findByToken(token.trim())
                .map(resetToken -> resetToken.getExpiryDate().isAfter(LocalDateTime.now()))
                .orElse(false);
    }
}
