
package com.rentvideo.rentvideo.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.rentvideo.rentvideo.dto.RegisterRequest;
import com.rentvideo.rentvideo.dto.UserResponse;
import com.rentvideo.rentvideo.entity.Role;
import com.rentvideo.rentvideo.entity.User;
import com.rentvideo.rentvideo.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse registerUser(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException(
                    "Email is already registered");
        }

        String encodedPassword =
                passwordEncoder.encode(request.getPassword());

        // Public registration must never grant ADMIN privileges.
        User user = new User(
                request.getEmail(),
                encodedPassword,
                request.getFirstName(),
                request.getLastName(),
                Role.CUSTOMER
        );

        User savedUser = userRepository.save(user);

        return new UserResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.getRole()
        );
    }
}
