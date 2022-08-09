package com.microservices.demo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "kafka-streams-config")
public class KafkaStreamsConfigData
{
    private String applicationID;
    private String inputTopicName;
    private String outputTopicName;
    private String stateFileLocation;
    private String wordCountStoreName;
}

