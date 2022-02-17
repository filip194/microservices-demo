package com.microservices.demo.elastic.config;

import java.util.Objects;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.microservices.demo.config.ElasticConfigData;

@Configuration
public class ElasticsearchConfig extends AbstractElasticsearchConfiguration
{
    private final ElasticConfigData elasticConfigData;

    public ElasticsearchConfig(ElasticConfigData elasticConfigData)
    {
        this.elasticConfigData = elasticConfigData;
    }

    @Override
    @Bean
    public RestHighLevelClient elasticsearchClient()
    {
        final UriComponents serverUri = UriComponentsBuilder.fromHttpUrl(elasticConfigData.getConnectionUrl()).build();
        return new RestHighLevelClient(RestClient.builder(
                        new HttpHost(
                                Objects.requireNonNull(serverUri.getHost()),
                                serverUri.getPort(),
                                serverUri.getScheme()))
                .setRequestConfigCallback(requestConfigBuilder -> requestConfigBuilder
                        .setConnectTimeout(elasticConfigData.getConnectTimeoutMs())
                        .setSocketTimeout(elasticConfigData.getSocketTimeoutMs())));
    }
}

