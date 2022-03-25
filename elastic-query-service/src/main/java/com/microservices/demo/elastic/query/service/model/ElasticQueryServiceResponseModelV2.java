package com.microservices.demo.elastic.query.service.model;

import java.time.ZonedDateTime;

import org.springframework.hateoas.RepresentationModel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
// RepresentationModel is used to add _links of HATEOAS API development principle
public class ElasticQueryServiceResponseModelV2 extends RepresentationModel<ElasticQueryServiceResponseModel>
{
    private Long id;
    private Long userId;
    private String text;
    private String text2;
    private ZonedDateTime createdAt;
}

