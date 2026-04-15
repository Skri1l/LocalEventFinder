package com.local.event.finder.service;

import com.local.event.finder.model.entity.RefreshToken;
import com.local.event.finder.model.entity.User;
import com.local.event.finder.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements  RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Override
    public RefreshToken createRefreshToken(User user){
        RefreshToken refreshToken = new RefreshToken();
        String uuid = UUID.randomUUID().toString();

        refreshToken.setUser(user);
        refreshToken.setToken(uuid);
        refreshToken.setExpiresAt(LocalDateTime.now().plusDays(30));
        refreshToken.setRevoked(false);

        return  refreshTokenRepository.save(refreshToken);
    }

    @Override
    public RefreshToken verifyRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));
        if (refreshToken.isRevoked()){
            throw new RuntimeException("Refresh token is revoked");
        }
        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())){
            throw new RuntimeException("Refresh token is expired");
        }
        return refreshToken;
    }

    @Override
    public void revokeRefreshToken(RefreshToken refreshToken){
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
    }
}
