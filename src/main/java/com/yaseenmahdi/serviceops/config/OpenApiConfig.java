package com.yaseenmahdi.serviceops.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    OpenAPI serviceOperationsOpenApi() {
        return new OpenAPI().info(new Info()
            .title("Service Operations Platform API")
            .version("1.0")
            .description("API for field-service customers, sites, assets, work orders, technician scheduling, and inventory reservations."));
    }
}
