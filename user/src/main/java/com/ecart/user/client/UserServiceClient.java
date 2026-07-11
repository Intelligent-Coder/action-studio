package com.ecart.user.client;

import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.bind.annotation.PathVariable;
import reactor.core.publisher.Mono;

@HttpExchange(url = "/api/search", accept = "application/json", contentType = "application/json")
public interface UserServiceClient {

    @GetExchange("/resource/{id}")
    Mono<String> getResourceById(@PathVariable("id") String id);

}
