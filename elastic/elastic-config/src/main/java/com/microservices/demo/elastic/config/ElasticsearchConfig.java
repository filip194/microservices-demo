package com.microservices.demo.elastic.config;

import java.util.Objects;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.microservices.demo.config.ElasticConfigData;

@Configuration
// required for Spring to scan and find elasticsearch repositories
@EnableElasticsearchRepositories(basePackages = "com.microservices.demo.elastic.index.client.repository")
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
                        new HttpHost(Objects.requireNonNull(serverUri.getHost()), serverUri.getPort(), serverUri.getScheme()))
                .setRequestConfigCallback(requestConfigBuilder -> requestConfigBuilder.setConnectTimeout(
                                elasticConfigData.getConnectTimeoutMs())
                        .setSocketTimeout(elasticConfigData.getSocketTimeoutMs())));
    }

    // used with ElasticsearchRepository class; provides convenient persistence methods to ease development
    @Bean
    public ElasticsearchOperations elasticsearchOperations()
    {
        return new ElasticsearchRestTemplate(elasticsearchClient());
    }
}

