package com.fadhli.auth_server.service;

import com.fadhli.auth_server.constant.ResponseMessages;
import com.fadhli.auth_server.dto.user.UserMapper;
import com.fadhli.auth_server.dto.user.UserRequestDto;
import com.fadhli.auth_server.dto.user.UserResponseDto;
import com.fadhli.auth_server.entity.User;
import com.fadhli.auth_server.exception.ResourceNotFoundException;
import com.fadhli.auth_server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserValidationService userValidationService;

    public List<UserResponseDto> findAll() {
        return userMapper.toDtoList(userRepository.findAll());
    }

    public UserResponseDto findById(Long id) {
        return userMapper.toDto(
                userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResponseMessages.notFound("User"))));
    }

    public UserResponseDto add(UserRequestDto userRequest) {
        userValidationService
                .validateUserUniqueness(
                        userRequest.getUsername(),
                        userRequest.getEmail(),
                        null
                );

        userRequest.setIsActive(true);

        return userMapper.toDto(
                userRepository.save(userMapper.toEntity(userRequest))
        );
    }

    public UserResponseDto edit(Long id, UserRequestDto userRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResponseMessages.notFound("User")));

        userValidationService
                .validateUserUniqueness(
                        userRequest.getUsername(),
                        userRequest.getEmail(),
                        id
                );

        userRequest.setIsActive(user.getIsActive());
        userMapper.updateFromDto(userRequest, user);
        User updatedUser = userRepository.save(user);

        return userMapper.toDto(updatedUser);
    }

    public void remove(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResponseMessages.notFound("User")));

        user.setIsActive(false);

        userRepository.save(user);
    }
}
