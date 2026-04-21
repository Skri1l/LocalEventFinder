package com.local.event.finder.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserResponseDto getUserById(Long id){
        Objects.requireNonNull(id,"User id cannot be null");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return new UserResponseDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getAvatarUrl(),
                user.getAge()
        );
    }

    @Override
    public UserUpdateResponseDto updateUser(Long id, UserRequestDto dto) {
        Objects.requireNonNull(id,"User id cannot be null");
        Objects.requireNonNull(dto,"User dto cannot be null");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (!user.getId().equals(id)) {
            throw new AccessDeniedException("You can only edit your own profile");
        }

        user.setUsername(dto.username());
        user.setEmail(dto.email());
        user.setAvatarUrl(dto.avatarUrl());
        user.setAge(dto.age());
        userRepository.save(user);

        return new UserUpdateResponseDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getAvatarUrl(),
                user.getAge()
        );

    }

    @Override
    public void deleteUser(Long id){
        Objects.requireNonNull(id,"User id cannot be null");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (!user.getId().equals(id)) {
            throw new AccessDeniedException("You can only delete your own profile");
        }
        userRepository.delete(user);
    }
}
