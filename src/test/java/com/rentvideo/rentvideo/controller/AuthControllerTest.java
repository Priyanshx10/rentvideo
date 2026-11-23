
package com.rentvideo.rentvideo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import com.rentvideo.rentvideo.dto.AuthResponse;
import com.rentvideo.rentvideo.dto.LoginRequest;
import com.rentvideo.rentvideo.dto.RegisterRequest;
import com.rentvideo.rentvideo.dto.UserResponse;
import com.rentvideo.rentvideo.entity.Role;
import com.rentvideo.rentvideo.security.JwtService;
import com.rentvideo.rentvideo.service.UserService;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthController authController;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {

        registerRequest = new RegisterRequest();
        registerRequest.setEmail("customer@test.com");
        registerRequest.setPassword("Customer123!");
        registerRequest.setFirstName("Test");
        registerRequest.setLastName("Customer");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("customer@test.com");
        loginRequest.setPassword("Customer123!");
    }

    @Test
    void registerUser_shouldReturnCreatedResponse() {

        UserResponse userResponse = new UserResponse(
                1L,
                "customer@test.com",
                "Test",
                "Customer",
                Role.CUSTOMER
        );

        when(userService.registerUser(registerRequest))
                .thenReturn(userResponse);

        ResponseEntity<UserResponse> response =
                authController.registerUser(registerRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("customer@test.com",
                response.getBody().getEmail());
        assertEquals(Role.CUSTOMER,
                response.getBody().getRole());

        verify(userService).registerUser(registerRequest);
    }

    @Test
    void login_shouldReturnJwtTokenWhenCredentialsAreValid() {

        UserDetails userDetails = User
                .withUsername("customer@test.com")
                .password("encodedPassword")
                .roles("CUSTOMER")
                .build();

        when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        when(authentication.getPrincipal())
                .thenReturn(userDetails);

        when(jwtService.generateToken(userDetails))
                .thenReturn("mock.jwt.token");

        ResponseEntity<AuthResponse> response =
                authController.login(loginRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("mock.jwt.token",
                response.getBody().getToken());
        assertEquals("Bearer",
                response.getBody().getTokenType());

        verify(authenticationManager).authenticate(
                any(UsernamePasswordAuthenticationToken.class));

        verify(jwtService).generateToken(userDetails);
    }

    @Test
    void login_shouldRejectInvalidCredentials() {

        when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException(
                        "Invalid email or password"));

        assertThrows(
                BadCredentialsException.class,
                () -> authController.login(loginRequest)
        );
    }

    @Test
    void login_shouldNotGenerateTokenWhenAuthenticationFails() {

        when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException(
                        "Invalid email or password"));

        assertThrows(
                BadCredentialsException.class,
                () -> authController.login(loginRequest)
        );

        verify(jwtService,
                org.mockito.Mockito.never()).generateToken(any());
    }
}
