package com.microservices.demo.elastic.query.service.api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.microservices.demo.elastic.query.service.model.ElasticQueryServiceRequestModel;
import com.microservices.demo.elastic.query.service.model.ElasticQueryServiceResponseModel;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
// @Controller + @ResponseBody; @ResponseBody not needed on methods if @RestController is defined on class level
@RequestMapping(value = "/documents")
public class ElasticDocumentController
{

    @GetMapping("/")
    public @ResponseBody
    ResponseEntity<List<ElasticQueryServiceResponseModel>> getAllDocuments()
    {
        final List<ElasticQueryServiceResponseModel> response = new ArrayList<>();
        log.info("Elasticsearch returned {} of documents", response.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public @ResponseBody
    ResponseEntity<ElasticQueryServiceResponseModel> getDocumentById(@PathVariable String id)
    {
        final ElasticQueryServiceResponseModel elasticQueryServiceResponseModel =
                ElasticQueryServiceResponseModel.builder()
                .id(id).build();
        log.info("Elasticsearch returned document with id {}", id);
        return ResponseEntity.ok(elasticQueryServiceResponseModel);
    }

    @PostMapping("/get-document-by-text")
    public @ResponseBody
    ResponseEntity<List<ElasticQueryServiceResponseModel>> getDocumentByText(
            @RequestBody ElasticQueryServiceRequestModel elasticQueryServiceRequestModel)
    {
        final List<ElasticQueryServiceResponseModel> response = new ArrayList<>();
        final ElasticQueryServiceResponseModel elasticQueryServiceResponseModel =
                ElasticQueryServiceResponseModel.builder()
                .text(elasticQueryServiceRequestModel.getText()).build();
        response.add(elasticQueryServiceResponseModel);
        log.info("Elasticsearch returned {} of documents", response.size());
        return ResponseEntity.ok(response);
    }
}

