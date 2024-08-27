package com.microservices.demo.elastic.query.service.business.impl;

import com.microservices.demo.config.ElasticQueryServiceConfigData;
import com.microservices.demo.elastic.model.index.impl.TwitterIndexModel;
import com.microservices.demo.elastic.query.client.exception.ElasticQueryClientException;
import com.microservices.demo.elastic.query.client.service.ElasticQueryClient;
import com.microservices.demo.elastic.query.service.QueryType;
import com.microservices.demo.elastic.query.service.business.ElasticQueryService;
import com.microservices.demo.elastic.query.service.common.model.ElasticQueryServiceResponseModel;
import com.microservices.demo.elastic.query.service.model.ElasticQueryServiceAnalyticsResponseModel;
import com.microservices.demo.elastic.query.service.model.ElasticQueryServiceWordCountResponseModel;
import com.microservices.demo.elastic.query.service.model.assembler.ElasticQueryServiceResponseModelAssembler;
import com.microservices.demo.mdc.Constants;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
public class TwitterElasticQueryService implements ElasticQueryService {
    private final ElasticQueryServiceResponseModelAssembler elasticQueryServiceResponseModelAssembler;
    private final ElasticQueryClient<TwitterIndexModel> elasticQueryClient;
    private final ElasticQueryServiceConfigData elasticQueryServiceConfigData;
    private final WebClient.Builder webClientBuilder;

    public TwitterElasticQueryService(
            ElasticQueryServiceResponseModelAssembler elasticQueryServiceResponseModelAssembler,
            ElasticQueryClient<TwitterIndexModel> elasticQueryClient,
            ElasticQueryServiceConfigData elasticQueryServiceConfigData,
            @Qualifier("webClientBuilder_elasticQueryService") WebClient.Builder clientBuilder) {
        this.elasticQueryServiceResponseModelAssembler = elasticQueryServiceResponseModelAssembler;
        this.elasticQueryClient = elasticQueryClient;
        this.elasticQueryServiceConfigData = elasticQueryServiceConfigData;
        this.webClientBuilder = clientBuilder;
    }

    @Override
    public ElasticQueryServiceResponseModel getDocumentById(String id) {
        log.info("Querying elasticsearch by id {}", id);
        return elasticQueryServiceResponseModelAssembler.toModel(elasticQueryClient.getIndexModelById(id));
    }

    @Override
    public ElasticQueryServiceAnalyticsResponseModel getDocumentByText(String text, String accessToken) {
        log.info("Querying elasticsearch by text {}", text);
        final List<ElasticQueryServiceResponseModel> elasticQueryServiceResponseModels =
                elasticQueryServiceResponseModelAssembler.toModels(
                        elasticQueryClient.getIndexModelByText(text));

        return ElasticQueryServiceAnalyticsResponseModel.builder()
                .elasticQueryServiceResponseModels(elasticQueryServiceResponseModels)
                .wordCount(getWordCount(text, accessToken))
                .build();
    }

    @Override
    public List<ElasticQueryServiceResponseModel> getAllDocuments() {
        log.info("Querying all documents in elasticsearch");
        return elasticQueryServiceResponseModelAssembler.toModels(elasticQueryClient.getAllIndexModels());
    }

    private Long getWordCount(String text, String accessToken) {
        if (QueryType.KAFKA_STATE_STORE.getType().equals(
                elasticQueryServiceConfigData.getWebClient().getQueryType())) {
            return getFromKafkaStateStore(text, accessToken).getWordCount();
        } else if (QueryType.ANALYTICS_DATABASE.getType().equals(
                elasticQueryServiceConfigData.getWebClient().getQueryType())) {
            return getFromAnalyticsDatabase(text, accessToken).getWordCount();
        }
        return 0L;
    }

    private ElasticQueryServiceWordCountResponseModel getFromKafkaStateStore(String text, String accessToken) {
        final ElasticQueryServiceConfigData.Query queryFromKafkaStateStore =
                elasticQueryServiceConfigData.getQueryFromKafkaStateStore();
        return retrieveResponseModel(text, accessToken, queryFromKafkaStateStore);
    }

    private ElasticQueryServiceWordCountResponseModel getFromAnalyticsDatabase(String text, String accessToken) {
        final ElasticQueryServiceConfigData.Query queryFromAnalyticsDatabase =
                elasticQueryServiceConfigData.getQueryFromAnalyticsDatabase();
        return retrieveResponseModel(text, accessToken, queryFromAnalyticsDatabase);
    }

    private ElasticQueryServiceWordCountResponseModel retrieveResponseModel(String text, String accessToken,
                                                                            ElasticQueryServiceConfigData.Query queryFromKafkaStateStore) {
        return webClientBuilder
                .build()
                .method(HttpMethod.valueOf(queryFromKafkaStateStore.getMethod()))
                .uri(queryFromKafkaStateStore.getUri(), uriBuilder -> uriBuilder.build(text))
                .headers(h -> {
                    h.setBearerAuth(accessToken);
                    h.set(Constants.CORRELATION_ID_HEADER, MDC.get(Constants.CORRELATION_ID_KEY));
                })
                .accept(MediaType.valueOf(queryFromKafkaStateStore.getAccept()))
                .retrieve()
                .onStatus(
                        s -> s.equals(HttpStatus.UNAUTHORIZED),
                        clientResponse -> Mono.just(new BadCredentialsException("Not authenticated!")))
                .onStatus(
                        httpStatus -> httpStatus.equals(HttpStatus.BAD_REQUEST),
                        clientResponse -> Mono.just(
                                new ElasticQueryClientException(clientResponse.statusCode().toString())))
                .onStatus(
                        httpStatus -> httpStatus.equals(HttpStatus.INTERNAL_SERVER_ERROR),
                        clientResponse -> Mono.just(
                                new Exception(clientResponse.statusCode().toString())))
                .bodyToMono(ElasticQueryServiceWordCountResponseModel.class)
                .log()
                .block();
    }
}

