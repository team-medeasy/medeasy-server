package com.medeasy;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootTest
class MedeasyApplicationTests {

	@TestConfiguration
	static class TestStorageConfig {
		@Bean
		public com.google.cloud.storage.Storage storage() {
			return Mockito.mock(com.google.cloud.storage.Storage.class);
		}
	}

	@Test
	void contextLoads() {
		// storage 가 목이기 때문에 자격증명 오류 없이 컨텍스트 로드
	}
}

