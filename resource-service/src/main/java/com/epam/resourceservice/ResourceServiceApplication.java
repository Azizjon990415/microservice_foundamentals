package com.epam.resourceservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingInt;

@SpringBootApplication
public class ResourceServiceApplication {

    public static void main(String[] args) {
       
        SpringApplication.run(ResourceServiceApplication.class, args);
    }

}
