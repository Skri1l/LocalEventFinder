package com.local.event.finder.authentication;

import com.local.event.finder.authentication.dto.AuthResponse;
import com.local.event.finder.authentication.dto.LoginRequest;
import com.local.event.finder.model.dto.RefreshRequestDto;
import com.local.event.finder.model.dto.UserRequestDto;
import com.local.event.finder.model.entity.RefreshToken;
import com.local.event.finder.model.entity.User;
import com.local.event.finder.repository.UserRepository;
import com.local.event.finder.security.JwtService;
import com.local.event.finder.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Objects;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final UserDetailsService userDetailsService;
    @Value("${jwt.expiration}")
    private Long expiration;


    @Transactional
    public AuthResponse register(final UserRequestDto userDto) {
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
        user.setPasswordHash(passwordEncoder.encode(userDto.password()));
        User savedUser = userRepository.save(user);
        String email = savedUser.getEmail();
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        String accessToken = jwtService.generateToken(userDetails);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(savedUser);
        return new AuthResponse(accessToken, refreshToken.getToken(), expiration);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        Objects.requireNonNull(request, "Request must not be null");
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(request.email(), request.password());
        Authentication authentication = authenticationManager.authenticate(token);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String accessToken = jwtService.generateToken(userDetails);
        String email = userDetails.getUsername();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email " + email + " not found"));
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
        return new AuthResponse(accessToken, refreshToken.getToken(), expiration);
    }

    @Transactional
    public AuthResponse refresh(RefreshRequestDto dto){
        Objects.requireNonNull(dto, "Refresh token must not be null");
        String token = dto.refreshToken();
        RefreshToken refreshToken = refreshTokenService.verifyRefreshToken(token);
        User user = refreshToken.getUser();
        refreshTokenService.revokeRefreshToken(refreshToken);
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user);
        String email = user.getEmail();
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        String accessToken = jwtService.generateToken(userDetails);
        return  new AuthResponse(accessToken, newRefreshToken.getToken(), expiration);
    }
}
