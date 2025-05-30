package com.epam.resourceservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;



@SpringBootApplication
@EnableRetry
public class ResourceServiceApplication {

    public static void main(String[] args) {
       
        SpringApplication.run(ResourceServiceApplication.class, args);
    }

}
