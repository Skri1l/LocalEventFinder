package com.local.event.finder.user;

public record UserUpdateResponseDto(
        Long id,
        String username,
        String email,
        String avatarUrl,
        int age
) {}