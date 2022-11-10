package com.microservices.demo.twitter.to.kafka.service.runner.impl;

import java.util.Arrays;

import javax.annotation.PreDestroy;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import com.microservices.demo.config.TwitterToKafkaServiceConfigData;
import com.microservices.demo.twitter.to.kafka.service.listener.TwitterKafkaStatusListener;
import com.microservices.demo.twitter.to.kafka.service.runner.StreamRunner;

import lombok.extern.slf4j.Slf4j;
import twitter4j.FilterQuery;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;

@Slf4j
@Component
@ConditionalOnExpression("${twitter-to-kafka-service.enable-mock-tweets} && not ${twitter-to-kafka-service.enable-v2-tweets}")
//@ConditionalOnProperty(name = "twitter-to-kafka-service.enable-mock-tweets", havingValue = "false", matchIfMissing = true)
public class TwitterKafkaStreamRunner implements StreamRunner
{
    private final TwitterKafkaStatusListener twitterKafkaStatusListener;
    private final TwitterToKafkaServiceConfigData twitterToKafkaServiceConfigData;

    private TwitterStream twitterStream;

    public TwitterKafkaStreamRunner(TwitterKafkaStatusListener statusListener,
            TwitterToKafkaServiceConfigData configData)
    {
        this.twitterKafkaStatusListener = statusListener;
        this.twitterToKafkaServiceConfigData = configData;
    }

    @Override
    public void start() throws TwitterException
    {
        twitterStream = new TwitterStreamFactory().getInstance();
        twitterStream.addListener(twitterKafkaStatusListener);
        addFilter();
    }

    // triggers on application shutdown, before bean is destroyed
    // @Singleton scope bean creation - Spring beans are created with singleton scope by default
    // @Prototype scope bean creation - for each injection of bean, Spring will create new instance, that is,
    // a new object is created each time the object is injected
    // @PreDestroy is not called when bean is created with @Prototype scope
    @PreDestroy
    public void shutdown()
    {
        if (twitterStream != null)
        {
            log.info("Closing twitter stream.");
            twitterStream.shutdown();
        }
    }

    private void addFilter()
    {
        final String[] keywords = twitterToKafkaServiceConfigData.getTwitterKeywords().toArray(new String[0]);
        final FilterQuery filterQuery = new FilterQuery(keywords);
        twitterStream.filter(filterQuery);

        log.info("Started filtering twitter stream for keywords: {}", Arrays.toString(keywords));
    }
}

