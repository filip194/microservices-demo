package com.microservices.demo.kafka.streams.service.security;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import com.microservices.demo.config.KafkaStreamsServiceConfigData;

@Qualifier(value = "kafka-streams-service-audience-validator")
@Component
public class AudienceValidator implements OAuth2TokenValidator<Jwt>
{
    private final KafkaStreamsServiceConfigData kafkaStreamsServiceConfigData;

    public AudienceValidator(KafkaStreamsServiceConfigData kafkaStreamsServiceConfigData)
    {
        this.kafkaStreamsServiceConfigData = kafkaStreamsServiceConfigData;
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt jwt)
    {
        if (jwt.getAudience().contains(kafkaStreamsServiceConfigData.getCustomAudience()))
        {
            return OAuth2TokenValidatorResult.success();
        }
        else
        {
            final OAuth2Error audienceError = new OAuth2Error("invalid token",
                    "The required audience " + kafkaStreamsServiceConfigData.getCustomAudience() + " is missing!",
                    null);
            return OAuth2TokenValidatorResult.failure(audienceError);
        }
    }
}

