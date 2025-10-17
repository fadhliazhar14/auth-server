package com.fadhli.auth_server.controller;

import com.fadhli.auth_server.constant.ResponseMessages;
import com.fadhli.auth_server.dto.role.RoleRequestDto;
import com.fadhli.auth_server.dto.role.RoleResponseDto;
import com.fadhli.auth_server.entity.Role;
import com.fadhli.auth_server.service.RoleService;
import com.fadhli.auth_server.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<RoleResponseDto>>> getAll() {
        List<RoleResponseDto> roles = roleService.findAll();
        ApiResponse<List<RoleResponseDto>> response = ApiResponse.success(ResponseMessages.SUCCESS, roles);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RoleResponseDto>> getById(@PathVariable Long id) {
        RoleResponseDto role = roleService.findById(id);
        ApiResponse<RoleResponseDto> response = ApiResponse.success(ResponseMessages.SUCCESS, role);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RoleResponseDto>> create(@Valid @RequestBody RoleRequestDto roleRequest) {
        RoleResponseDto createdRole = roleService.add(roleRequest);
        ApiResponse<RoleResponseDto> response = ApiResponse.success(HttpStatus.CREATED.value(), ResponseMessages.created("Role"), createdRole);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdRole.getId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RoleResponseDto>> update(
            @PathVariable Long id,
            @Valid @RequestBody RoleRequestDto roleRequest) {
        RoleResponseDto updatedRole = roleService.edit(id, roleRequest);
        ApiResponse<RoleResponseDto> response = ApiResponse.success(ResponseMessages.updated("Role"), updatedRole);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        roleService.remove(id);
        ApiResponse<Void> response = ApiResponse.success(ResponseMessages.deleted("Role"), null);

        return ResponseEntity.ok(response);
    }
}
