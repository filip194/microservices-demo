package com.microservices.demo.analytics.service.transformer;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.microservices.demo.analytics.service.dataaccess.entity.AnalyticsEntity;
import com.microservices.demo.analytics.service.model.AnalyticsResponseModel;

/**
 * EntityToResponseModelTransformer
 * <p>
 * Used for the API od this service to return the word count
 */
@Component
public class EntityToResponseModelTransformer
{
    public Optional<AnalyticsResponseModel> getResponseModel(AnalyticsEntity twitterAnalyticsEntity)
    {
        if (twitterAnalyticsEntity == null)
        {
            return Optional.empty();
        }

        return Optional.ofNullable(AnalyticsResponseModel
                .builder()
                        .id(twitterAnalyticsEntity.getId())
                        .word(twitterAnalyticsEntity.getWord())
                        .wordCount(twitterAnalyticsEntity.getWordCount())
                .build());
    }
}

