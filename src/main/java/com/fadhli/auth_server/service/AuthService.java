package com.fadhli.auth_server.service;

import com.fadhli.auth_server.constant.ResponseMessages;
import com.fadhli.auth_server.dto.auth.SigninRequestDto;
import com.fadhli.auth_server.dto.auth.SigninResponseDto;
import com.fadhli.auth_server.dto.auth.SignupRequestDto;
import com.fadhli.auth_server.dto.auth.SignupResponseDto;
import com.fadhli.auth_server.dto.auth.UserSessionResponseDto;
import com.fadhli.auth_server.dto.user.UserMapper;
import com.fadhli.auth_server.dto.user.UserSigninResponseDto;
import com.fadhli.auth_server.entity.CustomUserDetails;
import com.fadhli.auth_server.entity.User;
import com.fadhli.auth_server.exception.ResourceNotFoundException;
import com.fadhli.auth_server.repository.UserRepository;
import com.fadhli.auth_server.repository.UserRoleRepository;
import com.fadhli.auth_server.repository.projection.RoleProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final UserMapper userMapper;
    private final CustomUserDetailsService userDetailsService;
    private final UserValidationService userValidationService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Value("${jwt.expiration}")
    private Long accessTokenExpiration;

    @Value("${jwt.refresh-expiration}")
    private Long refreshTokenExpiration;

    @Value("${cookie.is-secure}")
    private Boolean isCookieSecure;


    @Transactional
    public SigninResponseDto authenticate(SigninRequestDto signinRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        signinRequest.getUsername(),
                        signinRequest.getPassword())
        );

        CustomUserDetails userPrincipal = (CustomUserDetails) authentication.getPrincipal();
        String jwt = jwtService.generateToken(userPrincipal);

        User user = userRepository.findByUsername(signinRequest.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException(ResponseMessages.notFound("User")));

        List<String> userRoles = userRoleRepository.findByUserIdWithDetail(user.getId())
                .stream()
                .map(RoleProjection::getName)
                .toList();

        UserSigninResponseDto userData = new UserSigninResponseDto(
                user.getName(),
                user.getUsername(),
                user.getEmail(),
                userRoles
        );

        return new SigninResponseDto(
                userData,
                jwt,
                refreshTokenService.generate(user.getId())
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
        CustomUserDetails userDetails = userDetailsService.loadUserByUsername(username);

        return jwtService.generateToken(userDetails);
    }

    public UserSessionResponseDto getCurrentSession(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(ResponseMessages.notFound("User")));

        List<String> userRoles = userRoleRepository.findByUserIdWithDetail(user.getId())
                .stream()
                .map(RoleProjection::getName)
                .toList();

        UserSigninResponseDto userData = new UserSigninResponseDto(
                user.getName(),
                user.getUsername(),
                user.getEmail(),
                userRoles
        );

        return new UserSessionResponseDto(true, userData);
    }

    public ResponseCookie setCookieForToken(String name, String value, String path, long age, Boolean isSecure) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(isSecure)
                .path(path)
                .maxAge(age)
                .sameSite("Lax")
                .build();
    }
}
