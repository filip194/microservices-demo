package com.microservices.demo.analytics.service.model;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalyticsResponseModel
{
    private UUID id;
    private String word;
    private long wordCount;
}

