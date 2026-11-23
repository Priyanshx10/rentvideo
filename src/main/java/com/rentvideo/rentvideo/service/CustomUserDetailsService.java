
package com.rentvideo.rentvideo.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.rentvideo.rentvideo.entity.User;
import com.rentvideo.rentvideo.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User not found with email: " + email
                        )
                );

        UserDetails userDetails =
                org.springframework.security.core.userdetails.User
                        .withUsername(user.getEmail())
                        .password(user.getPassword())
                        .roles(user.getRole().name())
                        .build();

        System.out.println("========== SECURITY DEBUG ==========");
        System.out.println("Email: " + userDetails.getUsername());
        System.out.println("Database Role: " + user.getRole());
        System.out.println("Granted Authorities: "
                + userDetails.getAuthorities());
        System.out.println("=====================================");

        return userDetails;
    }
}
