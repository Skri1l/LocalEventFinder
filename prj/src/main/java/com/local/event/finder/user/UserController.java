package com.local.event.finder.user;

import com.local.event.finder.api.ApiResponseDto;
import com.local.event.finder.api.StatusResponseDto;
import com.local.event.finder.logging.AppLogger;
import com.local.event.finder.logging.LoggerFactory;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final static AppLogger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    @GetMapping("/{id}")
    public ApiResponseDto<UserResponseDto> getUser(@Valid @PathVariable Long id) {
        log.info("UserController:get");
        return new ApiResponseDto<>(userService.getUserById(id));
    }

    @PatchMapping("/me")
    public ApiResponseDto<UserUpdateResponseDto> updateUser(@Valid @RequestBody UserUpdateRequestDto dto) {
        log.info("UserController:update");
        return new ApiResponseDto<>(userService.updateCurrentUser(dto));
    }

    @DeleteMapping("/me")
    public ApiResponseDto<StatusResponseDto> deleteUser() {
        log.info("UserController:delete");
        userService.deleteCurrentUser();
        return new ApiResponseDto<>(new StatusResponseDto("OK"));
    }

}
