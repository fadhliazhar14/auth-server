package com.fadhli.auth_server.auth;

import com.fadhli.auth_server.dto.auth.SigninRequestDto;
import com.fadhli.auth_server.dto.auth.SigninResponseDto;
import com.fadhli.auth_server.dto.user.UserMapper;
import com.fadhli.auth_server.entity.CustomUserDetails;
import com.fadhli.auth_server.entity.User;
import com.fadhli.auth_server.repository.UserRepository;
import com.fadhli.auth_server.repository.UserRoleRepository;
import com.fadhli.auth_server.repository.projection.RoleProjection;
import com.fadhli.auth_server.service.AuthService;
import com.fadhli.auth_server.service.JwtService;
import com.fadhli.auth_server.service.RefreshTokenService;
import com.fadhli.auth_server.service.UserValidationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtService jwtService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserRoleRepository userRoleRepository;
    @Mock
    private RefreshTokenService refreshTokenService;
    @Mock
    private UserValidationService userValidationService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private AuthService authService;

    @Test
    void authenticate_WithValidCredentials_ShouldReturnAccessTokenResponse() {
        // Arrange
        SigninRequestDto request = new SigninRequestDto();
        request.setUsername("john");
        request.setPassword("password123");

        Authentication mockAuth = mock(Authentication.class);
        CustomUserDetails mockUser = mock(CustomUserDetails.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuth);
        when(mockAuth.getPrincipal()).thenReturn(mockUser);
        when(jwtService.generateToken(mockUser)).thenReturn("jwt-token");

        User user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setUsername("john");
        user.setEmail("john@example.com");

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));

        RoleProjection mockRole = mock(RoleProjection.class);
        when(mockRole.getName()).thenReturn("ROLE_USER");
        when(userRoleRepository.findByUserIdWithDetail(1L)).thenReturn(Collections.singletonList(mockRole));
        when(refreshTokenService.generate(1L)).thenReturn("refresh-token");

        // Act
        SigninResponseDto response = authService.authenticate(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("jwt-token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
        assertThat(response.getUserData()).isNotNull();
        assertThat(response.getUserData().getUsername()).isEqualTo("john");
        assertThat(response.getUserData().getRoles()).contains("ROLE_USER");

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByUsername("john");
        verify(userRoleRepository).findByUserIdWithDetail(1L);
        verify(refreshTokenService).generate(1L);
    }
}
