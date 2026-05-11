package com.edulive.infrastructure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication(scanBasePackages = "com.edulive")
@EnableMongoRepositories(basePackages = "com.edulive")
public class SmartApiInfrastructureApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartApiInfrastructureApplication.class, args);
    }

}
