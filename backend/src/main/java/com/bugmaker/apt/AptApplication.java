package com.bugmaker.apt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
public class AptApplication {
	//테스트
	public static void main(String[] args) {
		SpringApplication.run(AptApplication.class, args);
	}

}
