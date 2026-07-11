package com.ecart.user.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SwaggerConfigTest {

    private final SwaggerConfig swaggerConfig = new SwaggerConfig();

    @Test
    void customOpenAPI_BeanCreated() {
         var openAPI = swaggerConfig.customOpenAPI();
         assertNotNull(openAPI);
    }

    @Test
    void customOpenAPI_ServerConfiguration() {
         var openAPI = swaggerConfig.customOpenAPI();
         var servers = openAPI.getServers();
         assertEquals(1, servers.size());
         assertEquals("http://localhost:8081", servers.getFirst().getUrl());
    }

    @Test
    void customOpenAPI_InfoConfiguration() {
         var openAPI = swaggerConfig.customOpenAPI();
         var info = openAPI.getInfo();
         assertEquals("User Service API", info.getTitle());
         assertEquals("1.0.0", info.getVersion());
    }
}