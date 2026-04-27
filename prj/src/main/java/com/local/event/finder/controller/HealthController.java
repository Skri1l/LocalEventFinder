package com.local.event.finder.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.logging.Logger;

@RestController
public class HealthController {
    /* COMMENT: I recommend to not using statics fields when use java with spring boot.
    * Spring Boot gives u bean system that works automatically and it controls them lifecycle,
    * and when u use static fields in classes they saves however object deletes. */
    private final static Logger logger = Logger.getLogger(HealthController.class.getName());

    @GetMapping("/health")
    public String health(HttpServletResponse response){
        int status = response.getStatus();
        logger.info("Status is: "+status);
        /* COMMENT: not wrapped in api response health controller */
        return "OK";
    }
}
