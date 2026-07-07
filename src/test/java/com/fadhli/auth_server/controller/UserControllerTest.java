package com.fadhli.auth_server.controller;

import com.fadhli.auth_server.dto.user.UserResponseDto;
import com.fadhli.auth_server.service.UserRoleService;
import com.fadhli.auth_server.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private UserRoleService userRoleService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void reactivate_ShouldReturnOkAndUpdatedUser() throws Exception {
        // Arrange
        Long userId = 1L;
        UserResponseDto responseDto = new UserResponseDto();
        responseDto.setId(userId);
        responseDto.setName("John Doe");
        responseDto.setUsername("john");
        responseDto.setEmail("john@example.com");
        responseDto.setIsActive(true);

        when(userService.reactivate(userId)).thenReturn(responseDto);

        // Act & Assert
        mockMvc.perform(patch("/api/users/{id}/reactivate", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("User status has been updated successfully"))
                .andExpect(jsonPath("$.data.id").value(userId))
                .andExpect(jsonPath("$.data.username").value("john"))
                .andExpect(jsonPath("$.data.isActive").value(true));

        verify(userService).reactivate(userId);
    }
}
