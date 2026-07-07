package com.fadhli.auth_server.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UserSigninResponseDto {
    private String name;
    private String username;
    private String email;
    private List<String> roles;
}
