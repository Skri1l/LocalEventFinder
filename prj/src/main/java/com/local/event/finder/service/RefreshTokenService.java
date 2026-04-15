package com.local.event.finder.service;

import com.local.event.finder.model.entity.RefreshToken;
import com.local.event.finder.model.entity.User;

import java.util.Optional;

public interface RefreshTokenService {

    Optional<RefreshToken> findByToken(String token);

    RefreshToken createRefreshToken(User user);

    RefreshToken verifyRefreshToken(String token);

    void revokeRefreshToken(RefreshToken refreshToken);
}
