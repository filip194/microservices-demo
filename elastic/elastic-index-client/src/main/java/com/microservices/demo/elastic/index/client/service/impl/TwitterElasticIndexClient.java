package com.microservices.demo.elastic.index.client.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexedObjectInformation;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.stereotype.Service;

import com.microservices.demo.config.ElasticConfigData;
import com.microservices.demo.elastic.index.client.service.ElasticIndexClient;
import com.microservices.demo.elastic.index.client.util.ElasticIndexUtil;
import com.microservices.demo.elastic.model.index.impl.TwitterIndexModel;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TwitterElasticIndexClient implements ElasticIndexClient<TwitterIndexModel>
{
    private final ElasticConfigData elasticConfigData;
    // class used for indexing and querying against elasticsearch; used to interact with elasticsearch while sending index queries
    private final ElasticsearchOperations elasticsearchOperations;
    private final ElasticIndexUtil<TwitterIndexModel> elasticIndexUtil;

    public TwitterElasticIndexClient(ElasticConfigData elasticConfigData,
            ElasticsearchOperations elasticsearchOperations, ElasticIndexUtil<TwitterIndexModel> elasticIndexUtil)
    {
        this.elasticConfigData = elasticConfigData;
        this.elasticsearchOperations = elasticsearchOperations;
        this.elasticIndexUtil = elasticIndexUtil;
    }

    @Override
    public List<String> save(List<TwitterIndexModel> documents)
    {
        final List<IndexQuery> indexQueries = elasticIndexUtil.getIndexQueries(documents);
        final List<String> documentIds = elasticsearchOperations.bulkIndex(
                indexQueries, IndexCoordinates.of(elasticConfigData.getIndexName()))
                .stream()
                .map(IndexedObjectInformation::getId)
                .collect(Collectors.toList());
        log.info("Documents indexed successfully with type: {} and IDs: {}", TwitterIndexModel.class.getName(),
                documentIds);
        return documentIds;
    }
}

