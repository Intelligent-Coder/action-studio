package org.example.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class ServiceClientConfig {

    @Bean
    public OrderServiceClient orderServiceClient(RestClient.Builder builder) {
        RestClient client = builder
                .baseUrl("http://localhost:8083")
                .build();
        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(client))
                .build();
        return factory.createClient(OrderServiceClient.class);
    }
}
