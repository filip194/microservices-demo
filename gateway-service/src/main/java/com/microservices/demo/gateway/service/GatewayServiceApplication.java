package com.microservices.demo.gateway.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

import lombok.extern.slf4j.Slf4j;

@EnableDiscoveryClient
@Slf4j
@SpringBootApplication
@ComponentScan(basePackages = "com.microservices.demo")
public class GatewayServiceApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(GatewayServiceApplication.class, args);
    }

}

