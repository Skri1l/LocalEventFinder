package com.local.event.finder.authentication;

import com.local.event.finder.api.ApiResponseDto;
import com.local.event.finder.api.StatusResponseDto;
import com.local.event.finder.authentication.dto.AuthResponse;
import com.local.event.finder.authentication.dto.ForgotPasswordRequestDto;
import com.local.event.finder.authentication.dto.LoginRequest;
import com.local.event.finder.authentication.dto.ResetPasswordRequestDto;
import com.local.event.finder.logging.AppLogger;
import com.local.event.finder.logging.LoggerFactory;
import com.local.event.finder.refreshToken.RefreshRequestDto;
import com.local.event.finder.user.UserController;
import com.local.event.finder.user.UserRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/*
COMMENT: there is no logging information.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final static AppLogger log = LoggerFactory.getLogger(AuthController.class);


    private final AuthService authService;

    @PostMapping("/login")
    public ApiResponseDto<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("AuthController:login");
        return new ApiResponseDto<>(authService.login(loginRequest));
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponseDto<AuthResponse> register(@Valid @RequestBody UserRequestDto dto) {
        log.info("AuthController:register");
        return new ApiResponseDto<>(authService.register(dto));
    }

    @PostMapping("/refresh")
    public ApiResponseDto<AuthResponse> refresh(@Valid @RequestBody RefreshRequestDto dto) {
        log.info("AuthController:refresh");
        return new ApiResponseDto<>(authService.refresh(dto));
    }

    @PostMapping("/logout")
    public ApiResponseDto<StatusResponseDto> logout(@RequestBody RefreshRequestDto dto) {
        log.info("AuthController:logout");
        authService.logout(dto);
        return new ApiResponseDto<>(new StatusResponseDto("OK"));
    }

    @PostMapping("/forgot-password")
    public ApiResponseDto<StatusResponseDto> forgotPassword(@RequestBody ForgotPasswordRequestDto dto) {
        log.info("AuthController:forgotPassword");
        authService.forgotPassword(dto);
        return new ApiResponseDto<>(new StatusResponseDto("OK"));
    }

    @PostMapping("/reset-password")
    public ApiResponseDto<StatusResponseDto> resetPassword(@RequestBody ResetPasswordRequestDto dto) {
        log.info("AuthController:resetPassword");
        authService.resetPassword(dto);
        return new ApiResponseDto<>(new StatusResponseDto("OK"));
    }

}
