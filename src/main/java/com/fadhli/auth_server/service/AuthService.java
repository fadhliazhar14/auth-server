package com.fadhli.auth_server.service;

import com.fadhli.auth_server.constant.ResponseMessages;
import com.fadhli.auth_server.dto.auth.SigninRequestDto;
import com.fadhli.auth_server.dto.auth.SignupRequestDto;
import com.fadhli.auth_server.dto.auth.SignupResponseDto;
import com.fadhli.auth_server.dto.token.AccessTokenResponseDto;
import com.fadhli.auth_server.dto.user.UserMapper;
import com.fadhli.auth_server.entity.User;
import com.fadhli.auth_server.exception.ResourceNotFoundException;
import com.fadhli.auth_server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserDetailsService userDetailsService;
    private final UserValidationService userValidationService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    
    @Value("${jwt.expiration}")
    private Long expiration;

    @Transactional
    public AccessTokenResponseDto authenticate(SigninRequestDto signinRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        signinRequest.getUsername(),
                        signinRequest.getPassword())
        );

        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        String jwt = jwtService.generateToken(userPrincipal);

        // Calculate expiration time manually to avoid verification issue
        long accessTokenExpiredAt = System.currentTimeMillis() / 1000 + (expiration / 1000);  // expiration is in milliseconds

        User user = userRepository.findByUsername(signinRequest.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException(ResponseMessages.notFound("User")));

        String refreshToken = refreshTokenService.generateRefreshToken(user.getId());

        return new AccessTokenResponseDto(
                jwt,
                refreshToken,
                "Bearer",
                accessTokenExpiredAt
        );
    }

    public SignupResponseDto register(SignupRequestDto signupRequest) {
        userValidationService
                .validateUserUniqueness(
                        signupRequest.getUsername(),
                        signupRequest.getEmail(),
                        null
                );

        signupRequest.setPasswordHash(passwordEncoder.encode(signupRequest.getPasswordHash()));
        User createdUser = userRepository.save(userMapper.registerFromDto(signupRequest, new User()));

        return userMapper.registerToDto(createdUser);
    }

    public String generateNewAccessToken(String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        return jwtService.generateToken(userDetails);
    }
}
