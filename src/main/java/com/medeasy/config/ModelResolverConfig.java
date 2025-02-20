package com.medeasy.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.core.jackson.ModelResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class ModelResolverConfig {
    @Bean
    public ModelResolver modelResolver(ObjectMapper objectMapper) {
        return new ModelResolver(objectMapper);
    }
}
