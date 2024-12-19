package com.example.user.config;

import com.example.user.client.ImapClient;
import com.example.user.client.SmtpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class RestClientConfig {
    @Value("${imapservice.url}")
    private String imapServiceUrl;
    @Value("${smtpservice.url}")
    private String smtpServiceUrl;


    @Bean
    public ImapClient imapClient() {
        RestClient restClient = RestClient.builder()
                .baseUrl(imapServiceUrl)
                .build();
        var restClientAdapter = RestClientAdapter.create(restClient);
        var httpServiceProxyFactory = HttpServiceProxyFactory.builderFor(restClientAdapter).build();
        return httpServiceProxyFactory.createClient(ImapClient.class);
    }

    @Bean
    public SmtpClient smtpClient() {
        RestClient restClient = RestClient.builder()
                .baseUrl(smtpServiceUrl)
                .build();
        var restClientAdapter = RestClientAdapter.create(restClient);
        var httpServiceProxyFactory = HttpServiceProxyFactory.builderFor(restClientAdapter).build();
        return httpServiceProxyFactory.createClient(SmtpClient.class);
    }
}
