package com.microservices.demo.analytics.service.business.impl;

import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.microservices.demo.analytics.service.business.AnalyticsService;
import com.microservices.demo.analytics.service.dataaccess.repository.AnalyticsRepository;
import com.microservices.demo.analytics.service.model.AnalyticsResponseModel;
import com.microservices.demo.analytics.service.transformer.EntityToResponseModelTransformer;

@Service
public class TwitterAnalyticsService implements AnalyticsService
{
    private final AnalyticsRepository analyticsRepository;
    private final EntityToResponseModelTransformer entityToResponseModelTransformer;

    public TwitterAnalyticsService(AnalyticsRepository analyticsRepository,
            EntityToResponseModelTransformer entityToResponseModelTransformer)
    {
        this.analyticsRepository = analyticsRepository;
        this.entityToResponseModelTransformer = entityToResponseModelTransformer;
    }

    @Override
    public Optional<AnalyticsResponseModel> getWordAnalytics(String word)
    {
        // getting only one, and the first record, which will be word count for a word, as we queried
        // using order by created date
        return entityToResponseModelTransformer.getResponseModel(
                analyticsRepository.getAnalyticsEntitiesByWord(word, PageRequest.of(0, 1))
                        .stream().findFirst().orElse(null)
        );
    }
}

