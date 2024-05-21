package com.khaphp.interactservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class InteractServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InteractServiceApplication.class, args);
	}

}
