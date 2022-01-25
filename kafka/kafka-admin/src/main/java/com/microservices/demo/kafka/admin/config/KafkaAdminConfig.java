package com.microservices.demo.kafka.admin.config;

import java.util.Map;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.AdminClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;

import com.microservices.demo.config.KafkaConfigData;

// enable retry logic for our application
@EnableRetry
@Configuration
public class KafkaAdminConfig
{
    private final KafkaConfigData kafkaConfigData;

    public KafkaAdminConfig(KafkaConfigData kafkaConfigData)
    {
        this.kafkaConfigData = kafkaConfigData;
    }

    // we will create admin client from this bean using bootstrap service configuration data
    // also, Kafka AdminClient is used to inspect brokers, topics and configurations
    @Bean
    public AdminClient adminClient()
    {
        return AdminClient.create(
                Map.of(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, kafkaConfigData.getBootstrapServers()));
    }
}

