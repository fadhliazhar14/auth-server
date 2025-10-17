package com.fadhli.auth_server.service;

import com.fadhli.auth_server.entity.CustomUserDetails;
import com.fadhli.auth_server.entity.Role;
import com.fadhli.auth_server.entity.User;
import com.fadhli.auth_server.entity.UserRole;
import com.fadhli.auth_server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameWithActiveRoles(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // Create authorities based on user role
        List<SimpleGrantedAuthority> authorities = user.getUserRoles().stream()
                .map(UserRole::getRole)
                .filter(Objects::nonNull)
                .map(Role::getName)
                .map(SimpleGrantedAuthority::new)
                .toList();

        return new CustomUserDetails(
                user.getId(),
                user.getUsername(),
                user.getPasswordHash(),
                authorities
        );
    }
}
