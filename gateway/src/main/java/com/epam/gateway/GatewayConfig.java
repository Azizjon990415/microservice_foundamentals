package com.epam.gateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("resource-service", r -> r.path("/resources/**")
                        .uri("lb://resource-service"))
                .route("song-service", r -> r.path("/songs/**")
                        .uri("lb://song-service"))
                .build();
    }
}