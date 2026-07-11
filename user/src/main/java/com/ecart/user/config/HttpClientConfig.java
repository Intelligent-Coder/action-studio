package com.ecart.user.config;

import com.ecart.user.client.UserServiceClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class HttpClientConfig {

    @Bean
    public UserServiceClient userServiceClient(
            @Value("${client.user-service.base-url:http://localhost:8082}") String baseUrl) {
        WebClient webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Content-Type", "application/json")
                .build();

        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builderFor(WebClientAdapter.create(webClient)).build();

        return factory.createClient(UserServiceClient.class);
    }
}
