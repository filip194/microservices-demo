package com.microservices.demo.analytics.service.business;

import java.util.Optional;

import com.microservices.demo.analytics.service.model.AnalyticsResponseModel;

@FunctionalInterface
public interface AnalyticsService
{
    Optional<AnalyticsResponseModel> getWordAnalytics(String word);
}
