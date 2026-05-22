package com.local.event.finder.authentication;

import com.local.event.finder.authentication.dto.AuthResponse;
import com.local.event.finder.authentication.dto.ForgotPasswordRequestDto;
import com.local.event.finder.authentication.dto.LoginRequest;
import com.local.event.finder.authentication.dto.ResetPasswordRequestDto;
import com.local.event.finder.notifications.EmailModel;
import com.local.event.finder.notifications.EmailService;
import com.local.event.finder.refreshToken.RefreshRequestDto;
import com.local.event.finder.user.UserRequestDto;
import com.local.event.finder.refreshToken.RefreshToken;
import com.local.event.finder.user.User;
import com.local.event.finder.user.UserRepository;
import com.local.event.finder.security.JwtService;
import com.local.event.finder.refreshToken.RefreshTokenService;
import com.local.event.finder.user.UserRole;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
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
    private final EmailService emailService;
    /*
    COMMENT: its better to use getter method from service, not this field here.
     */
    @Value("${jwt.expiration}")
    private Long expiration;

    private static final String TEMP_PASSWORD_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();


    @Transactional
    public AuthResponse register(final UserRequestDto userDto) {
        Objects.requireNonNull(userDto, "User must not be null");
        if (userRepository.existsByEmail(userDto.email())){
            throw new IllegalStateException("User with email " + userDto.email() + " already exists");
        }
        if (userRepository.existsByUsername(userDto.username())) {
            throw new IllegalStateException("User with username " + userDto.username() + " already exists");
        }
        /*
        COMMENT: U can create special mapper class that maps dto to user entity
         */
        User user = new User();
        user.setEmail(userDto.email());
        user.setUsername(userDto.username());
        user.setPasswordHash(passwordEncoder.encode(userDto.password()));
        user.setAvatarUrl(userDto.avatarUrl());
        user.setAge(userDto.age());
        user.setRole("USER");
        user.setBlocked(false);
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
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new AccessDeniedException("There is no user with  " + request.email() + " email"));

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(request.email(), request.password());
        Authentication authentication = authenticationManager.authenticate(token);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String accessToken = jwtService.generateToken(userDetails);
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
        return new AuthResponse(accessToken, newRefreshToken.getToken(), expiration);
    }

    @Transactional
    public void logout(RefreshRequestDto dto) {
        Objects.requireNonNull(dto, "Token must not be null");
        String refreshToken = dto.refreshToken();
        RefreshToken token = refreshTokenService.findByToken(refreshToken)
                        .orElseThrow(() -> new EntityNotFoundException("Refresh token not found"));
        refreshTokenService.revokeRefreshToken(token);
    }

    @Transactional
    public void forgotPassword(ForgotPasswordRequestDto dto) {
        Objects.requireNonNull(dto, "Email must not be null");
        User user = userRepository.findByEmail(dto.email())
                .orElseThrow(() -> new EntityNotFoundException("User with email " + dto.email() + " not found"));

        String temporaryPass = generateTemporaryPassword();
        user.setPasswordHash(passwordEncoder.encode(temporaryPass));
        userRepository.save(user);

        emailService.sendEmail(new EmailModel(
                user.getEmail(), "Temporary password", "Your password : " + temporaryPass
        ));
    }

    @Transactional
    public void resetPassword(ResetPasswordRequestDto dto) {
        Objects.requireNonNull(dto, "Password must not be null");
        if (dto.resetToken() == null || dto.resetToken().isEmpty()) {
            throw new IllegalStateException("Reset token must not be null");
        }

        User user = userRepository.findAll()
                .stream()
                .filter(currentUser -> passwordEncoder.matches(dto.resetToken(), currentUser.getPasswordHash()))
                .findFirst()
                .orElseThrow(() -> new AccessDeniedException("Invalid reset token"));

        user.setPasswordHash(passwordEncoder.encode(dto.resetToken()));
        userRepository.save(user);
    }

    private String generateTemporaryPassword() {
        int length = 12;
        StringBuilder pass =  new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = SECURE_RANDOM.nextInt(TEMP_PASSWORD_CHARS.length());
            pass.append(TEMP_PASSWORD_CHARS.charAt(index));
        }
        return pass.toString();
    }

}
