package com.disaster.safety;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.disaster.safety", "com.disaster.safety.petmediscan"})
@EntityScan(basePackages = {"com.disaster.safety.member.entity", "com.disaster.safety.petmediscan.entity"})
@EnableJpaRepositories(basePackages = {"com.disaster.safety.member.repository", "com.disaster.safety.petmediscan.repository"})
public class SafetyApplication {

	public static void main(String[] args) {
		SpringApplication.run(SafetyApplication.class, args);
	}

}
