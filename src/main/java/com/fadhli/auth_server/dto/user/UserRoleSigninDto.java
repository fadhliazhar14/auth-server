package com.fadhli.auth_server.dto.user;

import lombok.Data;

import java.util.List;

@Data
public class UserRoleSigninDto {
    private List<String> roles;
}
