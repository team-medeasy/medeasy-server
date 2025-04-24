package com.medeasy.config;

import com.google.cloud.texttospeech.v1.TextToSpeechClient;
import com.google.cloud.texttospeech.v1.TextToSpeechSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GcpTtsConfig {
    /**
     * 기본 애플리케이션 자격증명(ADC)을 사용합니다.
     * 별도 API 키 기반 설정이 필요하면
     * TextToSpeechSettings.newHttpJsonBuilder() 등으로 교체하세요.
     */
    @Bean
    public TextToSpeechClient textToSpeechClient() throws Exception {
        TextToSpeechSettings settings = TextToSpeechSettings.newBuilder()
                .build();

        return TextToSpeechClient.create(settings);
    }
}
