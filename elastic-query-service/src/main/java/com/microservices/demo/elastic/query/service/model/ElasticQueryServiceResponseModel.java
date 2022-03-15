package com.microservices.demo.elastic.query.service.model;

import java.time.ZonedDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ElasticQueryServiceResponseModel
{
    private String id;
    private Long userId;
    private String text;
    private ZonedDateTime createdAt;
}

