package com.ecart.user.client;

import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import reactor.core.publisher.Mono;

@HttpExchange(url = "/api/search")
public interface ProviderHttpInterface {

     @GetExchange("/resource/{id}")
     Mono<String> getResourceById(String id);

}