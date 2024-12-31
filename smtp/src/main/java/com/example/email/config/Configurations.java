package com.example.email.config;


import com.example.email.client.UserClient;
import java.net.URI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class Configurations {
    @Value("${service.user-service-url}")
    private String userServiceUrl;
    private String region = "us-east-1";
    private String endpointUrl = "http://localhost:9000";
    private String accessKey = "Yv0f4dA1xnW6dPqujT3y";
    private String secretKey = "IbVCXW0U49CeeyoNnrpVmjDzV7gszE3qauDxdJU1";
    @Bean
    public UserClient imapClient() {
        RestClient restClient = RestClient.builder()
                .baseUrl(userServiceUrl)
                .build();
        var restClientAdapter = RestClientAdapter.create(restClient);
        var httpServiceProxyFactory = HttpServiceProxyFactory.builderFor(restClientAdapter).build();
        return httpServiceProxyFactory.createClient(UserClient.class);
    }

}
