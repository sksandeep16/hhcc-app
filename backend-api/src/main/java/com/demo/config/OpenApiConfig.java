package com.demo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Family & Pet Management API")
                        .description("REST API for managing users, family members, pets, schedules, payments and feedback.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Demo Admin")
                                .email("admin@demo.com")))
                .addServersItem(new Server()
                        .url("http://localhost:8081")
                        .description("Local development server"));
    }
}
