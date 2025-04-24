package com.medeasy.config;

import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GcpStorageConfig {
    @Bean
    public Storage storage() {
        // ADC(Application Default Credentials) 사용
        return StorageOptions.getDefaultInstance().getService();
    }
}
