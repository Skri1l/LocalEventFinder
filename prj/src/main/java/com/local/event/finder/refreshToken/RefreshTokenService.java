package com.local.event.finder.refreshToken;

import com.local.event.finder.user.User;

import java.util.Optional;

public interface RefreshTokenService {

    Optional<RefreshToken> findByToken(String token);

    RefreshToken createRefreshToken(User user);

    RefreshToken verifyRefreshToken(String token);

    void revokeRefreshToken(RefreshToken refreshToken);
}
