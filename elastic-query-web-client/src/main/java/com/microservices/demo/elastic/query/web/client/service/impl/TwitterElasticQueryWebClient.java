package com.microservices.demo.elastic.query.web.client.service.impl;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.microservices.demo.config.ElasticQueryWebClientConfigData;
import com.microservices.demo.elastic.query.web.client.common.exception.ElasticQueryWebClientException;
import com.microservices.demo.elastic.query.web.client.common.model.ElasticQueryWebClientAnalyticsResponseModel;
import com.microservices.demo.elastic.query.web.client.common.model.ElasticQueryWebClientRequestModel;
import com.microservices.demo.elastic.query.web.client.service.ElasticQueryWebClient;
import com.microservices.demo.mdc.Constants;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class TwitterElasticQueryWebClient implements ElasticQueryWebClient
{
    private final WebClient.Builder webClientBuilder;
    private final ElasticQueryWebClientConfigData elasticQueryWebClientConfigData;

    public TwitterElasticQueryWebClient(@Qualifier("webClientBuilder") WebClient.Builder webClientBuilder,
            ElasticQueryWebClientConfigData elasticQueryWebClientConfigData)
    {
        this.webClientBuilder = webClientBuilder;
        this.elasticQueryWebClientConfigData = elasticQueryWebClientConfigData;
    }

    @Override
    public ElasticQueryWebClientAnalyticsResponseModel getDataByText(ElasticQueryWebClientRequestModel requestModel)
    {
        log.info("Querying by text {}", requestModel.getText());
        // bodyToMono method matches the objects by field name
        return getWebClient(requestModel)
                .bodyToMono(ElasticQueryWebClientAnalyticsResponseModel.class)
                .block();
    }

    private WebClient.ResponseSpec getWebClient(ElasticQueryWebClientRequestModel requestModel)
    {
        return webClientBuilder.build()
                .method(HttpMethod.valueOf(elasticQueryWebClientConfigData.getQueryByText().getMethod()))
                .uri(elasticQueryWebClientConfigData.getQueryByText().getUri())
                .accept(MediaType.valueOf(elasticQueryWebClientConfigData.getQueryByText().getAccept()))
                .header(Constants.CORRELATION_ID_HEADER, MDC.get(Constants.CORRELATION_ID_KEY))
                .body(BodyInserters.fromPublisher(Mono.just(requestModel), createParameterizedTypeReference()))
                .retrieve()
                .onStatus(
                        httpStatus -> httpStatus.equals(HttpStatus.UNAUTHORIZED),
                        clientResponse -> Mono.just(new BadCredentialsException("Not authenticated!"))
                )
                .onStatus(
                        HttpStatus::is4xxClientError,
                        clientResponse -> Mono.just(new ElasticQueryWebClientException(clientResponse.statusCode().getReasonPhrase()))
                )
                .onStatus(
                        HttpStatus::is5xxServerError,
                        clientResponse -> Mono.just(new Exception(clientResponse.statusCode().getReasonPhrase()))
                );
    }

    private <T> ParameterizedTypeReference<T> createParameterizedTypeReference()
    {
        return new ParameterizedTypeReference<T>()
        {
        };
    }
}

