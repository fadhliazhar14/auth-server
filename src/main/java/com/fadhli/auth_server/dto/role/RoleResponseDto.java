package com.fadhli.auth_server.dto.role;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RoleResponseDto {
    private Long id;
    private String name;
    private String description;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private Long createdBy;
    private LocalDateTime updatedAt;
    private Long updatedBy;
}
