package com.fadhli.auth_server.auth;

import com.fadhli.auth_server.dto.auth.SigninRequestDto;
import com.fadhli.auth_server.dto.token.AccessTokenResponse;
import com.fadhli.auth_server.dto.user.UserMapper;
import com.fadhli.auth_server.entity.User;
import com.fadhli.auth_server.repository.UserRepository;
import com.fadhli.auth_server.service.AuthService;
import com.fadhli.auth_server.service.JwtService;
import com.fadhli.auth_server.service.UserValidationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

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
        UserDetails mockUser = mock(UserDetails.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuth);
        when(mockAuth.getPrincipal()).thenReturn(mockUser);
        when(jwtService.generateToken(mockUser)).thenReturn("jwt-token");
        when(jwtService.extractExpiration("jwt-token")).thenReturn(new Date(System.currentTimeMillis() + 3600 * 1000));
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(new User()));

        // Act
        AccessTokenResponse result = authService.authenticate(request);

        // Assert
        assertThat(result.getAccessToken()).isEqualTo("jwt-token");
        assertThat(result.getTokenType()).isEqualTo("Bearer");
        assertThat(result.getExpiresIn()).isPositive();

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService).generateToken(mockUser);
        verify(userRepository).findByUsername("john");
    }
}
