package com.microservices.demo.elastic.query.service.common.model;

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
@EqualsAndHashCode(callSuper = false) // explicitly states superclass equals nad hashcode won't be used, gets rid of the warning
// RepresentationModel is used to add _links of HATEOAS API development principle
public class ElasticQueryServiceResponseModel extends RepresentationModel<ElasticQueryServiceResponseModel>
{
    private String id;
    private Long userId;
    private String text;
    private ZonedDateTime createdAt;
}

