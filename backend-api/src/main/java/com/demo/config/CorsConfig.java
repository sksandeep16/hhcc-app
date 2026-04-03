package com.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
            // Angular dev server (HTTP/HTTPS), React Web (Vite), Expo Web dev server
            .allowedOrigins(
                "http://localhost:4200",    // Angular HTTP
                "https://localhost:4200",   // Angular HTTPS
                "http://localhost:3000",    // React Web (Vite)
                "http://localhost:19006",   // Expo Web
                "http://localhost:8081"     // same-origin calls
            )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false)
                .maxAge(3600);
    }
}
