package com.microservices.demo.elastic.query.web.client.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * In ElasticQueryWebClientAnalyticsResponseModel class in the web-client, please rename the field
 * queryResponseModels as elasticQueryServiceResponseModels
 * <p>
 * Because in ElasticQueryServiceAnalyticsResponseModel class of query-service the field has this name, and
 * bodyToMono method matches the objects by field name
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ElasticQueryWebClientAnalyticsResponseModel {
    private List<ElasticQueryWebClientResponseModel> elasticQueryServiceResponseModels;
    private Long wordCount;
}

