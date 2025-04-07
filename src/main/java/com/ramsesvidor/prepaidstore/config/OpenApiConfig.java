package com.ramsesvidor.prepaidstore.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI()
                .info(new Info()
                        .title("Prepaid Store API")
                        .description("API for managing prepaid accounts, merchants and products.")
                        .version("0.0.1-SNAPSHOT"));
    }
}
