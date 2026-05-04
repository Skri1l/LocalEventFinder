package com.local.event.finder.controller;

import com.local.event.finder.api.ApiResponseDto;
import com.local.event.finder.api.StatusResponseDto;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.logging.Logger;

@RestController
public class HealthController {
    /* COMMENT: I recommend to not using statics fields when use java with spring boot.
    * Spring Boot gives u bean system that works automatically and it controls them lifecycle,
    * and when u use static fields in classes they saves however object deletes.
    * ONLY FOR CONSTANT VARIABLES U CAN USE STATIC - EXAMPLE: IN health method create "OK",
    * because it gets created anyway in static memory. */
    private final static Logger logger = Logger.getLogger(HealthController.class.getName());

    @GetMapping("/health")
    public ApiResponseDto<StatusResponseDto> health(HttpServletResponse response){
        int status = response.getStatus();
        logger.info("Status is: " + status);
        return new ApiResponseDto<>(new StatusResponseDto("OK"));
    }
}
