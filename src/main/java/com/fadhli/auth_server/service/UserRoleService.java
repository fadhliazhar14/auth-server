package com.fadhli.auth_server.service;

import com.fadhli.auth_server.constant.ResponseMessages;
import com.fadhli.auth_server.dto.user.UserResponseDto;
import com.fadhli.auth_server.dto.user_role.UserRoleRequestDto;
import com.fadhli.auth_server.dto.user_role.UserRoleResponseDto;
import com.fadhli.auth_server.entity.Role;
import com.fadhli.auth_server.entity.User;
import com.fadhli.auth_server.entity.UserRole;
import com.fadhli.auth_server.exception.BusinessValidationException;
import com.fadhli.auth_server.exception.ResourceNotFoundException;
import com.fadhli.auth_server.repository.RoleRepository;
import com.fadhli.auth_server.repository.UserRepository;
import com.fadhli.auth_server.repository.UserRoleRepository;
import com.fadhli.auth_server.repository.projection.RoleProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserRoleService {
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    public UserRoleResponseDto findByUserId(Long userId) {
        userService.findById(userId);

        UserRoleResponseDto userRoles = new UserRoleResponseDto();
        userRoles.setUserId(userId);

        List<RoleProjection> currentRoles = userRoleRepository.findByUserIdWithDetail(userId);
        userRoles.setRoles(
                currentRoles.stream()
                        .map(role -> new UserRoleResponseDto.Role(
                                role.getRoleId(),
                                role.getName(),
                                role.getDescription(),
                                role.getCreatedAt()
                        ))
                        .collect(Collectors.toList())
        );

        return userRoles;
    }

    public UserRoleResponseDto grantRolesToUser(Long userId, UserRoleRequestDto userRoleRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(ResponseMessages.notFound("User")));
        
        // Get valid roles
        List<Role> roles = roleRepository.findAllById(userRoleRequest.getRoleIds());

        // Get existing user roles
        List<UserRole> existingUserRoles = userRoleRepository.findByUserId(userId);

        // Check whether each role is active
        boolean hasInactiveNewRole =  roles.stream()
                .anyMatch(role -> !role.getIsActive());

        List<String> inactiveRoleNames = roles.stream()
                .filter(role -> !role.getIsActive())
                .map(Role::getName)
                .toList();

        if(hasInactiveNewRole) {
            throw new BusinessValidationException("Cannot assign inactive roles: " + inactiveRoleNames);
        }

        // Create Set from existing user roles
        Set<Long> existingRoleIds = existingUserRoles.stream()
                .map(ur -> ur.getRole().getId())
                .collect(Collectors.toSet());

        // Filtering unexisted new roles
        List<Role> newRoles = roles.stream()
                .filter(role -> !existingRoleIds.contains(role.getId()))
                .toList();

        // Listing new user roles from added roles
        List<UserRole> newUserRoles = newRoles.stream()
                .map(role -> new UserRole(null, user, role))
                .toList();

        // Save all user roles
       List<UserRole> createdRoles = userRoleRepository.saveAll(newUserRoles);

       return new UserRoleResponseDto(
         user.getId(),
         createdRoles.stream()
                 .map(cr -> new UserRoleResponseDto.Role(
                         cr.getRole().getId(),
                         cr.getRole().getName(),
                         cr.getRole().getDescription(),
                         cr.getCreatedAt()
                 ))
                 .collect(Collectors.toList())
       );
    }

    @Transactional
    public String revokeRoleFromUser(Long userId, Long roleId) {
        UserResponseDto user = userService.findById(userId);

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException(ResponseMessages.notFound("Role")));

        userRoleRepository.findByUserIdAndRoleId(user.getId(), roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role: " + role.getName() + " is not assigned yet"));

        userRoleRepository.deleteByRoleId(roleId);

        return role.getName();
    }
}
