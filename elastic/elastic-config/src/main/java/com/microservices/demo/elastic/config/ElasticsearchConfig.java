package com.microservices.demo.elastic.config;

import com.microservices.demo.config.ElasticConfigData;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
// required for Spring to scan and find elasticsearch repositories
@EnableElasticsearchRepositories(basePackages = "com.microservices.demo.elastic")
//public class ElasticsearchConfig extends AbstractElasticsearchConfiguration // AbstractElasticsearchConfiguration not available in Spring Boot 3
public class ElasticsearchConfig extends ElasticsearchConfiguration {
    private final ElasticConfigData elasticConfigData;

    public ElasticsearchConfig(ElasticConfigData elasticConfigData) {
        this.elasticConfigData = elasticConfigData;
    }

//    NOT NEEDED IN THIS NEW CLASS CONFIG
//    @Override
//    @Bean
//    public RestHighLevelClient elasticsearchClient() {
//        final UriComponents serverUri = UriComponentsBuilder.fromHttpUrl(elasticConfigData.getConnectionUrl()).build();
//        return new RestHighLevelClient(RestClient.builder(
//                        new HttpHost(Objects.requireNonNull(serverUri.getHost()), serverUri.getPort(), serverUri.getScheme()))
//                .setRequestConfigCallback(requestConfigBuilder -> requestConfigBuilder.setConnectTimeout(
//                                elasticConfigData.getConnectTimeoutMs())
//                        .setSocketTimeout(elasticConfigData.getSocketTimeoutMs())));
//    }
//
//    // used with ElasticsearchRepository class; provides convenient persistence methods to ease development; must be called elasticsearchOperations()
//    @Bean
//    public ElasticsearchOperations elasticsearchOperations() {
//        return new ElasticsearchRestTemplate(elasticsearchClient());
//    }

    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo(elasticConfigData.getConnectionUrl())
                .withConnectTimeout(elasticConfigData.getConnectTimeoutMs())
                .withSocketTimeout(elasticConfigData.getSocketTimeoutMs())
                .build();
    }
}

