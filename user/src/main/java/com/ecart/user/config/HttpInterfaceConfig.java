package com.ecart.user.config;

import com.ecart.user.client.ProviderHttpInterface;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class HttpInterfaceConfig {

    @Bean
    public ProviderHttpInterface providerHttpInterface() {
        WebClient webClient = WebClient.builder()
                .baseUrl("http://localhost:8082") // Example base URL for the provider
                .build();

        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builderFor(WebClientAdapter.create(webClient)).build();

        return factory.createClient(ProviderHttpInterface.class);
    }
}
