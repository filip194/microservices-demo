package com.microservices.demo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * This class will hold configuration data for Retry templates creation.
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "retry-config")
public class RetryConfigData
{
    private Long initialIntervalMs;
    private Long maxIntervalMs;
    private Double multiplier;
    private Integer maxAttempts;
    private Long sleepTimeMs;
}

