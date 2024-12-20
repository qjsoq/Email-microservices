package com.example.apigateway.routes;

import static org.springframework.cloud.gateway.server.mvc.filter.FilterFunctions.setPath;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

@Configuration
public class Routes {
    @Value("${service.user-service-url}")
    private String userServiceUrl;

    @Value("${service.imap-service-url}")
    private String imapServiceUrl;

    @Value("${service.smtp-service-url}")
    private String smtpServiceUrl;
    @Bean
    public RouterFunction<ServerResponse> authServiceRoute() {
        return GatewayRouterFunctions.route("auth_service")
                .route(RequestPredicates.path("/api/v1/auth/**"), HandlerFunctions.http(userServiceUrl))
                .build();
    }
    @Bean
    public RouterFunction<ServerResponse> userServiceRoute() {
        return GatewayRouterFunctions.route("user_service")
                .route(RequestPredicates.path("/api/v1/users/**"), HandlerFunctions.http(userServiceUrl))
                .build();
    }
    @Bean
    public RouterFunction<ServerResponse> imapServiceRoute() {
        return GatewayRouterFunctions.route("imap_service")
                .route(RequestPredicates.path("/api/v1/read/**"), HandlerFunctions.http(imapServiceUrl))
                .build();
    }
    @Bean
    public RouterFunction<ServerResponse> folderServiceRoute() {
        return GatewayRouterFunctions.route("folder_service")
                .route(RequestPredicates.path("/api/v1/folder/**"), HandlerFunctions.http(imapServiceUrl))
                .build();
    }
    @Bean
    public RouterFunction<ServerResponse> smtpServiceRoute() {
        return GatewayRouterFunctions.route("smtp_service")
                .route(RequestPredicates.path("/api/v1/email/**"), HandlerFunctions.http(smtpServiceUrl))
                .build();
    }
    @Bean
    public RouterFunction<ServerResponse> userServiceSwaggerRoute() {
        return GatewayRouterFunctions.route("user_service_swagger")
                .route(RequestPredicates.path("/aggregate/user-service/api-docs"), HandlerFunctions.http(userServiceUrl))
                .filter(setPath("/api-docs"))
                .build();
    }
    @Bean
    public RouterFunction<ServerResponse> imapServiceSwaggerRoute() {
        return GatewayRouterFunctions.route("imap_service_swagger")
                .route(RequestPredicates.path("/aggregate/imap-service/api-docs"), HandlerFunctions.http(imapServiceUrl))
                .filter(setPath("/api-docs"))
                .build();
    }
    @Bean
    public RouterFunction<ServerResponse> smtpServiceSwaggerRoute() {
        return GatewayRouterFunctions.route("smtp_service_swagger")
                .route(RequestPredicates.path("/aggregate/smtp-service/api-docs"), HandlerFunctions.http(smtpServiceUrl))
                .filter(setPath("/api-docs"))
                .build();
    }
}
