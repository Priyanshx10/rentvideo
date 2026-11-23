
package com.rentvideo.rentvideo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import com.rentvideo.rentvideo.dto.RegisterRequest;
import com.rentvideo.rentvideo.dto.UserResponse;
import com.rentvideo.rentvideo.entity.Role;
import com.rentvideo.rentvideo.entity.User;
import com.rentvideo.rentvideo.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private RegisterRequest request;

    @BeforeEach
    void setUp() {

        request = new RegisterRequest();
        request.setEmail("customer@test.com");
        request.setPassword("Customer123!");
        request.setFirstName("Test");
        request.setLastName("Customer");
    }

    @Test
    void registerUser_shouldCreateCustomerSuccessfully() {

        when(userRepository.existsByEmail(request.getEmail()))
                .thenReturn(false);

        when(passwordEncoder.encode(request.getPassword()))
                .thenReturn("encodedPassword");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> {
                    User savedUser = invocation.getArgument(0);
                    ReflectionTestUtils.setField(savedUser, "id", 1L);
                    return savedUser;
                });

        UserResponse response =
                userService.registerUser(request);

        assertEquals(1L, response.getId());
        assertEquals("customer@test.com", response.getEmail());
        assertEquals(Role.CUSTOMER, response.getRole());

        verify(passwordEncoder).encode(request.getPassword());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUser_shouldNeverAllowPublicAdminRegistration() {

        request.setRole(Role.ADMIN);

        when(userRepository.existsByEmail(request.getEmail()))
                .thenReturn(false);

        when(passwordEncoder.encode(request.getPassword()))
                .thenReturn("encodedPassword");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        userService.registerUser(request);

        ArgumentCaptor<User> userCaptor =
                ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(userCaptor.capture());

        assertEquals(Role.CUSTOMER,
                userCaptor.getValue().getRole());
    }

    @Test
    void registerUser_shouldRejectDuplicateEmail() {

        when(userRepository.existsByEmail(request.getEmail()))
                .thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.registerUser(request)
        );

        assertEquals(
                "Email is already registered",
                exception.getMessage()
        );

        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any(User.class));
    }
}
