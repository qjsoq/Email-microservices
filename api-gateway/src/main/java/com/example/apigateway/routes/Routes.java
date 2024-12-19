package com.example.apigateway.routes;

import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

@Configuration
public class Routes {
    @Bean
    public RouterFunction<ServerResponse> authServiceRoute() {
        return GatewayRouterFunctions.route("auth_service")
                .route(RequestPredicates.path("/api/v1/auth"), HandlerFunctions.http("http://localhost:8082"))
                .build();
    }
    @Bean
    public RouterFunction<ServerResponse> userServiceRoute() {
        return GatewayRouterFunctions.route("user_service")
                .route(RequestPredicates.path("/api/v1/users"), HandlerFunctions.http("http://localhost:8082"))
                .build();
    }
    @Bean
    public RouterFunction<ServerResponse> imapServiceRoute() {
        return GatewayRouterFunctions.route("imap_service")
                .route(RequestPredicates.path("/api/v1/read"), HandlerFunctions.http("http://localhost:8081"))
                .build();
    }
    @Bean
    public RouterFunction<ServerResponse> folderServiceRoute() {
        return GatewayRouterFunctions.route("folder_service")
                .route(RequestPredicates.path("/api/v1/folder"), HandlerFunctions.http("http://localhost:8081"))
                .build();
    }
    @Bean
    public RouterFunction<ServerResponse> smtpServiceRoute() {
        return GatewayRouterFunctions.route("smtp_service")
                .route(RequestPredicates.path("/api/v1/email"), HandlerFunctions.http("http://localhost:8083"))
                .build();
    }
}
