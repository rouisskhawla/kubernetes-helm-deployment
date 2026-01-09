package com.devops.microservices.api.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("books_service", r -> r.path("/api/books/**")
                   .uri("lb://books-service"))
            .route("authors_service", r -> r.path("/api/authors/**")
                    .uri("lb://authors-service"))
                .build();
    }
}
