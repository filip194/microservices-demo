package com.microservices.demo.common.config;

import com.microservices.demo.config.RetryConfigData;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

/**
 * This class will hold a Bean definition to create RetryTemplate templates for next verifications:
 * <ul>
 *     <li>
 *         Kafka cluster is up and running healthy
 *     </li>
 *     <li>
 *         Topics are created
 *     </li>
 * </ul>
 * Then, we can start our microservices.
 * To create this bean we'll need some configuration data, it will be held in RetryConfigData class.
 */
@Configuration
public class RetryConfig {
    private final RetryConfigData retryConfigData;

    public RetryConfig(RetryConfigData retryConfigData) {
        this.retryConfigData = retryConfigData;
    }

    @Bean
    public RetryTemplate retryTemplate() {
        final RetryTemplate retryTemplate = new RetryTemplate();

        // increase wait time for each retry attempt
        final ExponentialBackOffPolicy exponentialBackOffPolicy = new ExponentialBackOffPolicy();
        exponentialBackOffPolicy.setInitialInterval(retryConfigData.getInitialIntervalMs());
        exponentialBackOffPolicy.setMaxInterval(retryConfigData.getMaxIntervalMs());
        exponentialBackOffPolicy.setMultiplier(retryConfigData.getMultiplier());

        retryTemplate.setBackOffPolicy(exponentialBackOffPolicy);

        // retry until max attempt is reached
        final SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy();
        simpleRetryPolicy.setMaxAttempts(retryConfigData.getMaxAttempts());

        retryTemplate.setRetryPolicy(simpleRetryPolicy);

        return retryTemplate;
    }
}

