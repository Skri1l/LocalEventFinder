package com.local.event.finder.service;

import com.local.event.finder.model.dto.UserRequestDto;
import com.local.event.finder.model.dto.UserResponseDto;
import com.local.event.finder.model.entity.User;
import com.local.event.finder.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements  UserService{

    private final UserRepository userRepository;

    @Override
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
        user.setPasswordHash(userDto.password());
        User savedUser = userRepository.save(user);
        return new UserResponseDto(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail()
        );
    }
}
