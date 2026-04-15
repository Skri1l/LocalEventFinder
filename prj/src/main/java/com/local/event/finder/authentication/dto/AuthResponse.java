package com.local.event.finder.authentication.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        Long accessTokenExpiresIn
) {
}
