package com.fadhli.auth_server.service;

import com.fadhli.auth_server.constant.ResponseMessages;
import com.fadhli.auth_server.dto.common.PageRequestDto;
import com.fadhli.auth_server.dto.common.PageResponseDto;
import com.fadhli.auth_server.dto.user.UserMapper;
import com.fadhli.auth_server.dto.user.UserRequestDto;
import com.fadhli.auth_server.dto.user.UserResponseDto;
import com.fadhli.auth_server.dto.user.UserUpdateRequestDto;
import com.fadhli.auth_server.entity.User;
import com.fadhli.auth_server.exception.ResourceNotFoundException;
import com.fadhli.auth_server.repository.UserRepository;
import com.fadhli.auth_server.util.PageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserValidationService userValidationService;

    public PageResponseDto<UserResponseDto> findAll(PageRequestDto pageRequest) {
        Pageable pageable = PageUtil.createPageable(pageRequest);
        Page<User> userPage = userRepository.findAllWithPagination(pageRequest.getSearch(), pageable);
        List<UserResponseDto> userDto = userPage.stream()
                .map(userMapper::toDto)
                .toList();

        return PageUtil.createPageResponse(userDto, pageable, userPage.getTotalElements());
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

    public UserResponseDto edit(Long id, UserUpdateRequestDto userUpdateRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResponseMessages.notFound("User")));

        userValidationService
                .validateUserUniqueness(
                        userUpdateRequest.getUsername(),
                        userUpdateRequest.getEmail(),
                        id
                );


        userMapper.updateFromDto(userUpdateRequest, user);
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
