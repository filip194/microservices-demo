package com.microservices.demo.elastic.query.service.model;

import java.time.ZonedDateTime;

import org.springframework.hateoas.RepresentationModel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
// RepresentationModel is used to add _links of HATEOAS API development principle
public class ElasticQueryServiceResponseModelV2 extends RepresentationModel<ElasticQueryServiceResponseModelV2>
{
    private Long id;
    private Long userId;
    private String text;
    private ZonedDateTime createdAt;
}

