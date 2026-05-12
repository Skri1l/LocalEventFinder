package com.local.event.finder.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfiguration implements WebMvcConfigurer {

    private static final String FRONTEND_PORT = "5173";
    private static final String FRONTEND_DOMAIN = "ec2-51-20-182-189.eu-north-1.compute.amazonaws.com";
    private static final String FRONTEND_IP = "51.20.182.189";

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                        "http://localhost:" + FRONTEND_PORT,
                        FRONTEND_DOMAIN + ":" + FRONTEND_PORT,
                        FRONTEND_IP + ":" + FRONTEND_PORT
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
