package com.fadhli.auth_server.util;

import com.fadhli.auth_server.entity.CustomUserDetails;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuditorAwareImpl implements AuditorAware<Long> {

    @Override
    public Optional<Long> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.of(0L);
        }

        try {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            return Optional.of(userDetails.getId());
        } catch (NumberFormatException e) {
            return Optional.of(0L);
        }
    }
}
