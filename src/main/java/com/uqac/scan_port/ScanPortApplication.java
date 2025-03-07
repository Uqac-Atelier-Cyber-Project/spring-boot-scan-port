package com.uqac.scan_port;

import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@Configuration
@EnableAsync
public class ScanPortApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScanPortApplication.class, args);
	}
}