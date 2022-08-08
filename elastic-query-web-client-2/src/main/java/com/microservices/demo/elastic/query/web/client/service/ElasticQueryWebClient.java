package com.microservices.demo.elastic.query.web.client.service;

import java.util.List;

import com.microservices.demo.elastic.query.web.client.common.model.ElasticQueryWebClientRequestModel;
import com.microservices.demo.elastic.query.web.client.common.model.ElasticQueryWebClientResponseModel;

@FunctionalInterface
public interface ElasticQueryWebClient
{
    List<ElasticQueryWebClientResponseModel> getDataByText(ElasticQueryWebClientRequestModel requestModel);
}
