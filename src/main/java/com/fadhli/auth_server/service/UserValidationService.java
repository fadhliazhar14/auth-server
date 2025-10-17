package com.fadhli.auth_server.service;

import com.fadhli.auth_server.constant.ResponseMessages;
import com.fadhli.auth_server.exception.BusinessValidationException;
import com.fadhli.auth_server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserValidationService {
    private final UserRepository userRepository;

    public void validateUserUniqueness(String username, String email, Long id) {
        if (id == null) {
            if (userRepository.existsByUsername(username)) {
                throw new BusinessValidationException(ResponseMessages.taken("Username"));
            }
            if (userRepository.existsByEmail(email)) {
                throw new BusinessValidationException(ResponseMessages.taken("Email"));
            }
        } else {
            if (userRepository.existsByUsernameAndIdNot(username, id)) {
                throw new BusinessValidationException(ResponseMessages.taken("Username"));
            }
            if (userRepository.existsByEmailAndIdNot(email, id)) {
                throw new BusinessValidationException(ResponseMessages.taken("Email"));
            }
        }
    }
}
