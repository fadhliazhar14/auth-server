package com.fadhli.auth_server.dto.user_role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRoleResponseDto {
    private Long userId;
    private List<Role> roles;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Role {
        private Long roleId;
        private String roleName;
        private String roleDescription;
        private LocalDateTime assignedAt;
    }
}
