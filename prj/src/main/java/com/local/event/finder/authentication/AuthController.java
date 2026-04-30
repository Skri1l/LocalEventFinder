package com.local.event.finder.authentication;

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

    /*
    COMMENT: Response without wrapping.
     */
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }

    /*
    COMMENT: Response without wrapping.
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody UserRequestDto dto) {
        return authService.register(dto);
    }

    /*
    COMMENT: Response without wrapping.
     */
    @PostMapping("/refresh")
    public AuthResponse refresh(@Valid @RequestBody RefreshRequestDto dto){
        return authService.refresh(dto);
    }

    /*
    COMMENT: No response. need to be as in the documentation.
    {
      "data": {
        "status": "OK"
      }
    }
     */
    @PostMapping("/logout")
    public void logout(@RequestBody RefreshRequestDto dto){
        authService.logout(dto);
    }
}
