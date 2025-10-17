package com.fadhli.auth_server.controller;

import com.fadhli.auth_server.constant.ResponseMessages;
import com.fadhli.auth_server.dto.user.UserRequestDto;
import com.fadhli.auth_server.dto.user.UserResponseDto;
import com.fadhli.auth_server.dto.user_role.UserRoleRequestDto;
import com.fadhli.auth_server.dto.user_role.UserRoleResponseDto;
import com.fadhli.auth_server.service.UserRoleService;
import com.fadhli.auth_server.service.UserService;
import com.fadhli.auth_server.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService userService;
    private final UserRoleService userRoleService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponseDto>>> getAll() {
        List<UserResponseDto> users = userService.findAll();
        ApiResponse<List<UserResponseDto>> response = ApiResponse.success(ResponseMessages.SUCCESS, users);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDto>> getById(@PathVariable Long id) {
        UserResponseDto users = userService.findById(id);
        ApiResponse<UserResponseDto> response = ApiResponse.success(ResponseMessages.SUCCESS, users);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponseDto>> create(@Valid @RequestBody UserRequestDto userRequest) {
        UserResponseDto createdUser = userService.add(userRequest);
        ApiResponse<UserResponseDto> response = ApiResponse.success(HttpStatus.CREATED.value(),ResponseMessages.created("User"), createdUser);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdUser.getId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDto>> update(
            @PathVariable Long id,
            @Valid @RequestBody UserRequestDto userRequest) {
        UserResponseDto users = userService.edit(id, userRequest);
        ApiResponse<UserResponseDto> response = ApiResponse.success(ResponseMessages.updated("User"), users);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        userService.remove(id);
        ApiResponse<Void> response = ApiResponse.success(ResponseMessages.deleted("User"), null);

        return ResponseEntity.ok(response);
    }

    /*--- USER ROLES ---*/
    @GetMapping("/{id}/roles")
    public ResponseEntity<ApiResponse<UserRoleResponseDto>> getUserRoles(@PathVariable Long id) {
        UserRoleResponseDto userRoles = userRoleService.findByUserId(id);
        ApiResponse<UserRoleResponseDto> response = ApiResponse.success(ResponseMessages.SUCCESS, userRoles);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/roles")
    public ResponseEntity<ApiResponse<UserRoleResponseDto>> addRolesToUser(
            @PathVariable Long id,
            @RequestBody UserRoleRequestDto userRoleRequest) {
        UserRoleResponseDto createdUserRoles = userRoleService.grantRolesToUser(id, userRoleRequest);
        ApiResponse<UserRoleResponseDto> response = ApiResponse.success(ResponseMessages.granted("User role"), createdUserRoles);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}/roles/{roleId}")
    public ResponseEntity<ApiResponse<Void>> revokeRoleFromUser(
            @PathVariable Long id,
            @PathVariable Long roleId) {
        String roleName = userRoleService.revokeRoleFromUser(id, roleId);
        ApiResponse<Void> response = ApiResponse.success(ResponseMessages.revoked(roleName), null);

        return ResponseEntity.ok(response);
    }
}
