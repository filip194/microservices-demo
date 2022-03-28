package com.microservices.demo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "elastic-query-web-client")
public class ElasticQueryWebClientConfigData
{
    // this will automatically use prefix "webclient" from configuration yml file 'config-client-elastic_query_web_client.yml'
    private WebClient webClient;

    @Data
    public static class WebClient
    {
        private Integer connectTimeoutMs;
        private Integer readTimeoutMs;
        private Integer writeTimeoutMs;
        private Integer maxInMemorySize;
        private String contentType;
        private String acceptType;
        private String baseUrl;
    }
}

