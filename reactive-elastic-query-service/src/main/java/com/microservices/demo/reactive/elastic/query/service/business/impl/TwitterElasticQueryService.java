package com.microservices.demo.reactive.elastic.query.service.business.impl;

import org.springframework.stereotype.Service;

import com.microservices.demo.elastic.model.index.impl.TwitterIndexModel;
import com.microservices.demo.elastic.query.service.common.model.ElasticQueryServiceResponseModel;
import com.microservices.demo.elastic.query.service.common.transformer.ElasticToResponseModelTransformer;
import com.microservices.demo.reactive.elastic.query.service.business.ElasticQueryService;
import com.microservices.demo.reactive.elastic.query.service.business.ReactiveElasticQueryClient;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
@Service
public class TwitterElasticQueryService implements ElasticQueryService
{
    private final ReactiveElasticQueryClient<TwitterIndexModel> reactiveElasticQueryClient;
    private final ElasticToResponseModelTransformer elasticToResponseModelTransformer;

    public TwitterElasticQueryService(ReactiveElasticQueryClient<TwitterIndexModel> reactiveElasticQueryClient,
            ElasticToResponseModelTransformer elasticToResponseModelTransformer)
    {
        this.reactiveElasticQueryClient = reactiveElasticQueryClient;
        this.elasticToResponseModelTransformer = elasticToResponseModelTransformer;
    }

    @Override
    public Flux<ElasticQueryServiceResponseModel> getDocumentByText(String text)
    {
        log.info("Querying reactive elasticsearch for text: {}", text);
        return reactiveElasticQueryClient
                .getIndexModelByText(text)
                .map(elasticToResponseModelTransformer::getResponseModel);
    }
}

