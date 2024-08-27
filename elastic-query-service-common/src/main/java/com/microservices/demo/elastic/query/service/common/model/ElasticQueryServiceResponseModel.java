package com.microservices.demo.elastic.query.service.common.model;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
// explicitly states superclass equals nad hashcode won't be used, gets rid of the warning
// RepresentationModel is used to add _links of HATEOAS API development principle
public class ElasticQueryServiceResponseModel extends RepresentationModel<ElasticQueryServiceResponseModel> {
    private String id;
    private Long userId;
    private String text;
    private ZonedDateTime createdAt;
}

