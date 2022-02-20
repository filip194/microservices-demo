package com.microservices.demo.kafka.to.elastic.service.transformer;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.microservices.demo.elastic.model.index.impl.TwitterIndexModel;
import com.microservices.demo.kafka.avro.model.TwitterAvroModel;

/**
 * Converts Avro model to Elastic model
 */
@Component
public class AvroToElasticModelTransformer
{
    public List<TwitterIndexModel> getElasticModels(List<TwitterAvroModel> avroModels)
    {
        return avroModels.stream()
                .map(avroModel -> TwitterIndexModel.builder()
                        .userId(avroModel.getUserId())
                        .id(String.valueOf(avroModel.getId()))
                        .text(avroModel.getText())
                        .createdAt(ZonedDateTime.ofInstant(
                                Instant.ofEpochMilli(avroModel.getCreatedAt()),
                                ZoneId.systemDefault()))
                        .build()
                ).collect(Collectors.toList());
    }
}

