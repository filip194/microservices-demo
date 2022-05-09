package com.microservices.demo.reactive.elastic.query.service.api;

import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microservices.demo.elastic.query.service.common.model.ElasticQueryServiceRequestModel;
import com.microservices.demo.elastic.query.service.common.model.ElasticQueryServiceResponseModel;
import com.microservices.demo.reactive.elastic.query.service.business.ElasticQueryService;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
@RestController
@RequestMapping(value = "/documents")
public class ElasticDocumentController
{
    private final ElasticQueryService elasticQueryService;

    public ElasticDocumentController(ElasticQueryService elasticQueryService)
    {
        this.elasticQueryService = elasticQueryService;
    }

    // this will be event stream endpoint and return response by chunks to client
    @PostMapping(value = "get-doc-by-text",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public Flux<ElasticQueryServiceResponseModel> getDocumentByText(
            @RequestBody @Valid ElasticQueryServiceRequestModel requestModel)
    {
        Flux<ElasticQueryServiceResponseModel> response = elasticQueryService.getDocumentByText(
                requestModel.getText());
        // log() method on response - observes all reactive stream signals and traces them using built-in logger support
        response = response.log();
        log.info("Returning from query reactive service for text: {}", requestModel.getText());
        return response;
    }
}

