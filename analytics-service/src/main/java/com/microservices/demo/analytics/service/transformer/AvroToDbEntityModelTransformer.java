package com.microservices.demo.analytics.service.transformer;

import com.microservices.demo.analytics.service.dataaccess.entity.AnalyticsEntity;
import com.microservices.demo.kafka.avro.model.TwitterAnalyticsAvroModel;
import org.springframework.stereotype.Component;
import org.springframework.util.IdGenerator;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AvroToDbEntityModelTransformer {
    // creates UUIDs
    private final IdGenerator idGenerator;

    public AvroToDbEntityModelTransformer(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    public List<AnalyticsEntity> getEntityModel(List<TwitterAnalyticsAvroModel> avroModels) {
        return avroModels.stream()
                .map(avroModel -> new AnalyticsEntity(
                        idGenerator.generateId(),
                        avroModel.getWord(),
                        avroModel.getWordCount(),
                        LocalDateTime.ofInstant(Instant.ofEpochSecond(avroModel.getCreatedAt()), ZoneOffset.UTC)
                )).collect(Collectors.toList());
    }
}

