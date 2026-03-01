package com.devops.microservices.api.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class GatewayConfig {

    @Value("${authors.service.url}")
    private String authorsServiceUrl;

    @Value("${books.service.url}")
    private String booksServiceUrl;

    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("books_service", r -> r.path("/api/books/**")
                    .uri(booksServiceUrl))
            .route("authors_service", r -> r.path("/api/authors/**")
                    .uri(authorsServiceUrl))
            .build();
    }
}
