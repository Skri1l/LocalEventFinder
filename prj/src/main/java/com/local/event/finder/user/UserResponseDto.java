package com.local.event.finder.user;

public record UserResponseDto (

        Long id,

        String username,

        String email,

        String imageUrl,

        int age,

        String role,

        boolean isBlocked
){}
