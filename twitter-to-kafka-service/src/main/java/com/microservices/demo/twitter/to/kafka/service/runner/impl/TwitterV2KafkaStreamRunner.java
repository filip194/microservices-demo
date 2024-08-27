package com.microservices.demo.twitter.to.kafka.service.runner.impl;

import com.microservices.demo.config.TwitterToKafkaServiceConfigData;
import com.microservices.demo.twitter.to.kafka.service.runner.StreamRunner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;
import twitter4j.TwitterException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 https://github.com/twitterdev/Twitter-API-v2-sample-code/blob/main/Filtered-Stream/FilteredStreamDemo.java
 */

@Slf4j
@Component
@ConditionalOnExpression("${twitter-to-kafka-service.enable-v2-tweets} && not ${twitter-to-kafka-service.enable-mock-tweets}")
//@ConditionalOnProperty(name = "twitter-to-kafka-service.enable-v2-tweets", havingValue = "true", matchIfMissing =
// true)
public class TwitterV2KafkaStreamRunner implements StreamRunner {
    private final TwitterToKafkaServiceConfigData twitterToKafkaServiceConfigData;
    private final TwitterV2StreamHelper twitterV2StreamHelper;

    public TwitterV2KafkaStreamRunner(TwitterToKafkaServiceConfigData twitterToKafkaServiceConfigData,
                                      TwitterV2StreamHelper twitterV2StreamHelper) {
        this.twitterToKafkaServiceConfigData = twitterToKafkaServiceConfigData;
        this.twitterV2StreamHelper = twitterV2StreamHelper;
    }

    @Override
    public void start() {
        final String bearerToken = twitterToKafkaServiceConfigData.getTwitterV2BearerToken();

        if (null != bearerToken) {
            try {
                twitterV2StreamHelper.setupRules(bearerToken, getRules());
                twitterV2StreamHelper.connectStream(bearerToken);
            } catch (IOException | URISyntaxException | TwitterException e) {
                final String errorMsg = "Error streaming tweets!";
                log.error(errorMsg, e);
                throw new RuntimeException(errorMsg, e);
            }
        } else {
            final String errorMsg = "There was a problem getting your bearer token. "
                    + "Please make sure you have set TWITTER_BEARER_TOKEN environment variable.";
            log.error(errorMsg);
            throw new RuntimeException(errorMsg);
        }
    }

    private Map<String, String> getRules() {
        final List<String> keywords = twitterToKafkaServiceConfigData.getTwitterKeywords();
        final Map<String, String> rules = new HashMap<>();

        for (String keyword : keywords) {
            rules.put(keyword, "Keyword: " + keyword);
        }
        log.info("Created filter for twitter stream for keywords: {}", keywords);
        return rules;
    }
}

