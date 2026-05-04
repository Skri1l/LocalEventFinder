package com.local.event.finder.authentication;

import com.local.event.finder.api.ApiResponseDto;
import com.local.event.finder.api.StatusResponseDto;
import com.local.event.finder.authentication.dto.AuthResponse;
import com.local.event.finder.authentication.dto.LoginRequest;
import com.local.event.finder.refreshToken.RefreshRequestDto;
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

    private final AuthService authService;

    @PostMapping("/login")
    public ApiResponseDto<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return new ApiResponseDto<>(authService.login(loginRequest));
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponseDto<AuthResponse> register(@Valid @RequestBody UserRequestDto dto) {
        return new ApiResponseDto<>(authService.register(dto));
    }

    @PostMapping("/refresh")
    public ApiResponseDto<AuthResponse> refresh(@Valid @RequestBody RefreshRequestDto dto) {
        return new ApiResponseDto<>(authService.refresh(dto));
    }

    @PostMapping("/logout")
    public ApiResponseDto<StatusResponseDto> logout(@RequestBody RefreshRequestDto dto) {
        authService.logout(dto);
        return new ApiResponseDto<>(new StatusResponseDto("OK"));
    }
}
