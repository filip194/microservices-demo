package com.microservices.demo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "kafka-streams-service")
public class KafkaStreamsServiceConfigData
{
    private String version;
    private String customAudience;
}

