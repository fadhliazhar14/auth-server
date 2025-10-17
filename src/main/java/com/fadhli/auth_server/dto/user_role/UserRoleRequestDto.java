package com.fadhli.auth_server.dto.user_role;

import lombok.Data;

import java.util.List;

@Data
public class UserRoleRequestDto {
    private List<Long> roleIds;
}
