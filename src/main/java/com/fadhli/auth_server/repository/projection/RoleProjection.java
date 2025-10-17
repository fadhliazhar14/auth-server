package com.fadhli.auth_server.repository.projection;

import java.time.LocalDateTime;

public interface RoleProjection {
    Long getRoleId();
    String getName();
    String getDescription();
    LocalDateTime getCreatedAt();
}
