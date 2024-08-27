package com.microservices.demo.analytics.service.business;

import com.microservices.demo.analytics.service.model.AnalyticsResponseModel;

import java.util.Optional;

@FunctionalInterface
public interface AnalyticsService {
    Optional<AnalyticsResponseModel> getWordAnalytics(String word);
}
