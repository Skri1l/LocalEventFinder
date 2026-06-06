package com.local.event.finder.user;

import com.local.event.finder.refreshToken.RefreshTokenRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public UserResponseDto getUserById(Long id){
        Objects.requireNonNull(id,"User id cannot be null");

        SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return toUserResponseDto(user);
    }

    @Override
    public UserResponseDto getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return toUserResponseDto(user);
    }

    @Override
    public UserUpdateResponseDto updateCurrentUser(UserUpdateRequestDto dto) {
        Objects.requireNonNull(dto,"User dto cannot be null");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (dto.username() == null &&
                dto.email() == null &&
                dto.avatarUrl() == null &&
                dto.age() == null) {
            throw new IllegalArgumentException("At least one field must be provided for update");
        }

        if (dto.username() != null && !dto.username().equals(user.getUsername())
                && userRepository.existsByUsername(dto.username())) {
            throw new IllegalStateException("User with username " + dto.username() + " already exists");
        }

        if (dto.email() != null && !dto.email().equals(user.getEmail())
                && userRepository.existsByEmail(dto.email())) {
            throw new IllegalStateException("User with email " + dto.email() + " already exists");
        }

        if (dto.username() != null) {
            user.setUsername(dto.username());
        }

        if (dto.email() != null) {
            user.setEmail(dto.email());
        }

        if (dto.avatarUrl() != null) {
            user.setAvatarUrl(dto.avatarUrl());
        }
        if (dto.age() != null) {
            user.setAge(dto.age());
        }

        userRepository.save(user);

        return new UserUpdateResponseDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getAvatarUrl(),
                user.getAge(),
                user.getRole(),
                user.isBlocked()
        );
    }

    @Transactional
    @Override
    public void deleteCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        refreshTokenRepository.deleteByUser(user);
        userRepository.delete(user);
    }

    static private UserResponseDto toUserResponseDto(final User user) {
        return new UserResponseDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getAvatarUrl(),
                user.getAge(),
                user.getRole(),
                user.isBlocked()
        );
    }
}
