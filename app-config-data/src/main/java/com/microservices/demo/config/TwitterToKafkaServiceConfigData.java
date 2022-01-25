package com.microservices.demo.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * This class will hold configuration data for Twitter streaming service.
 */
@Data
@Configuration
// prefix must match the prefix in the application.yml file
@ConfigurationProperties(prefix = "twitter-to-kafka-service")
public class TwitterToKafkaServiceConfigData
{
    // create List of String to capture twitter keywords from application.yml file
    // name is important and should match (in camelCase) twitter-keywords from application.yml, so it should be twitterKeywords
    // twitter-keywords in application.yml
    private List<String> twitterKeywords;

    // welcome-message in application.yml
    private String welcomeMessage;

    private Boolean enableMockTweets;
    private Integer mockMinTweetLength;
    private Integer mockMaxTweetLength;
    private Long mockSleepMs;
}

