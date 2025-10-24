package com.fadhli.auth_server.dto.user;

import com.fadhli.auth_server.dto.auth.SignupRequestDto;
import com.fadhli.auth_server.dto.auth.SignupResponseDto;
import com.fadhli.auth_server.entity.User;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponseDto toDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "userRoles", ignore = true)
    User toEntity(UserRequestDto dto);

    List<UserResponseDto> toDtoList(List<User> users);

    List<User> toEntityList(List<UserRequestDto> dtos);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "userRoles", ignore = true)
    void updateFromDto(UserUpdateRequestDto userUpdateRequest, @MappingTarget User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "userRoles", ignore = true)
    User registerFromDto(SignupRequestDto signupRequestDto, @MappingTarget User user);

    SignupResponseDto registerToDto(User user);
}