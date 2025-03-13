package com.medeasy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class MedeasyApplication {

	public static void main(String[] args) {
		SpringApplication.run(MedeasyApplication.class, args);
	}

}
