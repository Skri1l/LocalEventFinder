package com.local.event.finder.admin;

import com.local.event.finder.api.ApiResponseDto;
import com.local.event.finder.api.StatusResponseDto;
import com.local.event.finder.logging.AppLogger;
import com.local.event.finder.logging.LoggerFactory;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final static AppLogger log = LoggerFactory.getLogger(AdminController.class);

    private final AdminService adminService;

    @PatchMapping("/users/{id}/block")
    public ApiResponseDto<StatusResponseDto> blockUser(@Valid @PathVariable Long id) {
        log.info("AdminController:BlockUser");
        adminService.blockUser(id);
        return new ApiResponseDto<>(new StatusResponseDto("OK"));
    }

    @DeleteMapping("/users/{id}/block")
    public ApiResponseDto<StatusResponseDto> unblockUser(@Valid @PathVariable Long id) {
        log.info("AdminController:UnblockUser");
        adminService.unblockUser(id);
        return new ApiResponseDto<>(new StatusResponseDto("OK"));
    }

    @PatchMapping("/users/{id}/op")
    public ApiResponseDto<StatusResponseDto> makeUserAdmin(@Valid @PathVariable Long id) {
        log.info("AdminController:makeUserAdmin");
        adminService.makeUserAdmin(id);
        return new ApiResponseDto<>(new StatusResponseDto("OK"));
    }
}
