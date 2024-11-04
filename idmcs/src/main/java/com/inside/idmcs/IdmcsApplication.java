package com.inside.idmcs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class IdmcsApplication {

	public static void main(String[] args) {
		SpringApplication.run(IdmcsApplication.class, args);
	}
	
}
