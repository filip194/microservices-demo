package com.microservices.demo.elastic.query.web.client.common.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ElasticQueryWebClientAnalyticsResponseModel
{
    private List<ElasticQueryWebClientResponseModel> queryResponseModels;
    private Long wordCount;
}

