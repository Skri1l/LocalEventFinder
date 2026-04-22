package com.local.event.finder.user;

import com.local.event.finder.api.ApiResponseDto;
import com.local.event.finder.api.StatusResponseDto;
import com.local.event.finder.event.EventController;
import com.local.event.finder.logging.AppLogger;
import com.local.event.finder.logging.LoggerFactory;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PatchMapping("/{id}")
    public ApiResponseDto<UserUpdateResponseDto> updateUser(@PathVariable Long id,
                                                            @Valid @RequestBody UserUpdateRequestDto dto) {
        log.info("UserController:update");
        return new ApiResponseDto<>(userService.updateUser(id, dto));
    }

    @DeleteMapping("/{id}")
    public ApiResponseDto<StatusResponseDto> deleteUser(@PathVariable Long id) {
        log.info("UserController:delete");
        userService.deleteUser(id);
        return new ApiResponseDto<>(new StatusResponseDto("OK"));
    }

}
