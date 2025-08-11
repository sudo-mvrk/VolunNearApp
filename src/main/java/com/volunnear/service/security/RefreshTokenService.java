package com.volunnear.service.security;

import com.volunnear.entity.users.AppUser;
import com.volunnear.entity.users.RefreshToken;
import com.volunnear.exception.TokenRefreshException;
import com.volunnear.repository.user.AppUserRepository;
import com.volunnear.repository.user.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class RefreshTokenService {
    @Value("${jwt.refreshToken.lifetime}")
    private Long expireDate;

    private final AppUserRepository appUserRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken createRefreshToken(String username) {
        AppUser appUser = appUserRepository.findAppUserByUsername(username).get();
        if (!refreshTokenRepository.existsByAppUser(appUser)) {
            RefreshToken refreshToken = new RefreshToken();

            refreshToken.setAppUser(appUser);
            refreshToken.setExpiryDate(Instant.now().plusMillis(expireDate));
            refreshToken.setToken(UUID.randomUUID().toString());

            refreshTokenRepository.save(refreshToken);
            return refreshToken;
        }
        RefreshToken refreshToken = refreshTokenRepository.findByAppUser(appUser).get();
        refreshToken.setExpiryDate(Instant.now().plusMillis(expireDate));
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.deleteByToken(token.getToken());
            throw new TokenRefreshException("Refresh token was expired. Please make a new sign-in request");
        }
        return token;
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }
}
