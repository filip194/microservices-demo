package com.microservices.demo.elastic.query.service.api;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.microservices.demo.elastic.query.service.business.ElasticQueryService;
import com.microservices.demo.elastic.query.service.model.ElasticQueryServiceRequestModel;
import com.microservices.demo.elastic.query.service.model.ElasticQueryServiceResponseModel;
import com.microservices.demo.elastic.query.service.model.ElasticQueryServiceResponseModelV2;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
// @Controller + @ResponseBody; @ResponseBody not needed on methods if @RestController is defined on class level
@RequestMapping(value = "/documents", produces = "application/vnd.api.v1+json")
public class ElasticDocumentController
{
    private final ElasticQueryService elasticQueryService;

    public ElasticDocumentController(ElasticQueryService elasticQueryService)
    {
        this.elasticQueryService = elasticQueryService;
    }

    @GetMapping("")
    public @ResponseBody
    ResponseEntity<List<ElasticQueryServiceResponseModel>> getAllDocuments()
    {
        final List<ElasticQueryServiceResponseModel> response = elasticQueryService.getAllDocuments();
        log.info("Elasticsearch returned {} of documents", response.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public @ResponseBody
    ResponseEntity<ElasticQueryServiceResponseModel> getDocumentById(@PathVariable @NotEmpty String id)
    {
        final ElasticQueryServiceResponseModel elasticQueryServiceResponseModel = elasticQueryService.getDocumentById(
                id);
        log.info("Elasticsearch returned document with id {}", id);
        return ResponseEntity.ok(elasticQueryServiceResponseModel);
    }

    @GetMapping(value = "/{id}", produces = "application/vnd.api.v2+json")
    public @ResponseBody
    ResponseEntity<ElasticQueryServiceResponseModelV2> getDocumentByIdV2(@PathVariable @NotEmpty String id)
    {
        final ElasticQueryServiceResponseModel elasticQueryServiceResponseModel = elasticQueryService.getDocumentById(
                id);
        final ElasticQueryServiceResponseModelV2 responseModelV2 = getV2Model(elasticQueryServiceResponseModel);
        log.info("Elasticsearch returned document with id {}", id);
        return ResponseEntity.ok(responseModelV2);
    }

    @PostMapping("/get-document-by-text")
    public @ResponseBody
    ResponseEntity<List<ElasticQueryServiceResponseModel>> getDocumentByText(
            @RequestBody @Valid ElasticQueryServiceRequestModel elasticQueryServiceRequestModel)
    {
        final List<ElasticQueryServiceResponseModel> response = elasticQueryService.getDocumentByText(
                elasticQueryServiceRequestModel.getText());
        log.info("Elasticsearch returned {} of documents", response.size());
        return ResponseEntity.ok(response);
    }

    private ElasticQueryServiceResponseModelV2 getV2Model(ElasticQueryServiceResponseModel responseModel)
    {
        final ElasticQueryServiceResponseModelV2 responseModelV2 = ElasticQueryServiceResponseModelV2.builder()
                .id(Long.parseLong(responseModel.getId()))
                .userId(responseModel.getUserId())
                .createdAt(responseModel.getCreatedAt())
                .text(responseModel.getText())
                .text2("Version 2 text")
                .build();
        responseModelV2.add(responseModel.getLinks());
        return responseModelV2;
    }
}

