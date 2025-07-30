package com.example.asynchronous_processing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class AsynchronousProcessingApplication {

	public static void main(String[] args) {
		SpringApplication.run(AsynchronousProcessingApplication.class, args);
	}

}
