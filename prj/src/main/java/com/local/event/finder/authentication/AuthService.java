package com.local.event.finder.authentication;

import com.local.event.finder.model.dto.UserRequestDto;
import com.local.event.finder.model.dto.UserResponseDto;
import com.local.event.finder.model.entity.User;
import com.local.event.finder.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private  final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;


    @Transactional
    public UserResponseDto register(final UserRequestDto userDto) {
        Objects.requireNonNull(userDto, "User must not be null");
        if (userRepository.existsByEmail(userDto.email())){
            throw new RuntimeException("User with email " + userDto.email() + " already exists");
        }
        if (userRepository.existsByUsername(userDto.username())) {
            throw new RuntimeException("User with username " + userDto.username() + " already exists");
        }
        User user = new User();
        user.setEmail(userDto.email());
        user.setUsername(userDto.username());
        user.setPasswordHash(passwordEncoder.encode(userDto.password()));
        User savedUser = userRepository.save(user);
        return new UserResponseDto(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail()
        );
    }

    @Transactional
    public UserResponseDto login(final UserRequestDto userDto) {
        Objects.requireNonNull(userDto, "User must not be null");
        if (!userRepository.existsByUsername(userDto.username())) {
            throw new RuntimeException("User with username " + userDto.username() + " does not exists");
        }
        if (!userRepository.existsByEmail(userDto.email())) {
            throw new RuntimeException("User with email " + userDto.email() + " does not exists");
        }
        if (!userRepository.existsByPass)
        return null;
    }
}
