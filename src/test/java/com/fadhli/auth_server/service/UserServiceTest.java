package com.fadhli.auth_server.service;

import com.fadhli.auth_server.dto.user.UserMapper;
import com.fadhli.auth_server.dto.user.UserResponseDto;
import com.fadhli.auth_server.entity.User;
import com.fadhli.auth_server.exception.ResourceNotFoundException;
import com.fadhli.auth_server.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserValidationService userValidationService;

    @InjectMocks
    private UserService userService;

    @Test
    void reactivate_UserNotFound_ShouldThrowResourceNotFoundException() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.reactivate(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found");

        verify(userRepository).findById(1L);
        verifyNoInteractions(userMapper);
    }

    @Test
    void reactivate_UserAlreadyActive_ShouldReturnDtoWithoutSaving() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setIsActive(true);

        UserResponseDto responseDto = new UserResponseDto();
        responseDto.setId(1L);
        responseDto.setIsActive(true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(responseDto);

        // Act
        UserResponseDto result = userService.reactivate(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getIsActive()).isTrue();

        verify(userRepository).findById(1L);
        verify(userMapper).toDto(user);
        verify(userRepository, never()).save(any());
    }

    @Test
    void reactivate_UserInactive_ShouldSetToActiveAndSaveAndReturnDto() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setIsActive(false);

        User activeUser = new User();
        activeUser.setId(1L);
        activeUser.setIsActive(true);

        UserResponseDto responseDto = new UserResponseDto();
        responseDto.setId(1L);
        responseDto.setIsActive(true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(activeUser);
        when(userMapper.toDto(activeUser)).thenReturn(responseDto);

        // Act
        UserResponseDto result = userService.reactivate(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getIsActive()).isTrue();
        assertThat(user.getIsActive()).isTrue();

        verify(userRepository).findById(1L);
        verify(userRepository).save(user);
        verify(userMapper).toDto(activeUser);
    }
}
