package com.microservices.demo.twitter.to.kafka.service.runner.impl;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.microservices.demo.config.TwitterToKafkaServiceConfigData;
import com.microservices.demo.twitter.to.kafka.service.exception.TwitterToKafkaServiceException;
import com.microservices.demo.twitter.to.kafka.service.listener.TwitterKafkaStatusListener;
import com.microservices.demo.twitter.to.kafka.service.runner.StreamRunner;

import lombok.extern.slf4j.Slf4j;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;

@Slf4j
@Component
// conditionally create a Spring bean with a config variable from application.properties/yml
@ConditionalOnProperty(name = "twitter-to-kafka-service.enable-mock-tweets", havingValue = "true")
public class MockKafkaStreamRunner implements StreamRunner
{
    private static final Random RANDOM = new Random();
    private static final String[] WORDS = new String[] { "Lorem", "ipsum", "dolor", "sit", "amet", "consectetur",
            "adipiscing", "elit", "sed", "do", "eiusmod", "tempor", "incididunt", "ut", "labore", "et", "dolore",
            "magna", "aliqua" };

    private static final String tweetAsRawJson =
            "{" + "\"created_at\":\"{0}\"," + "\"id\":\"{1}\"," + "\"text\":\"{2}\"," + "\"user\":{\"id\":\"{3}\"}"
                    + "}";

    private static final String TWITTER_STATUS_DATE_FORMAT = "EEE MMM dd HH:mm:ss zzz yyyy";

    private final TwitterToKafkaServiceConfigData twitterToKafkaServiceConfigData;
    private final TwitterKafkaStatusListener twitterKafkaStatusListener;

    public MockKafkaStreamRunner(TwitterToKafkaServiceConfigData configData, TwitterKafkaStatusListener statusListener)
    {
        this.twitterToKafkaServiceConfigData = configData;
        this.twitterKafkaStatusListener = statusListener;
    }

    @Override
    public void start() throws TwitterException
    {
        final String[] keywords = twitterToKafkaServiceConfigData.getTwitterKeywords().toArray(new String[0]);
        int minTweetLength = twitterToKafkaServiceConfigData.getMockMinTweetLength();
        int maxTweetLength = twitterToKafkaServiceConfigData.getMockMaxTweetLength();
        long sleepTimeMs = twitterToKafkaServiceConfigData.getMockSleepMs();

        log.info("Starting to mock filtering twitter streams for keywords: {}", Arrays.toString(keywords));

        simulateTwitterStream(keywords, minTweetLength, maxTweetLength, sleepTimeMs);
    }

    private void simulateTwitterStream(String[] keywords, int minTweetLength, int maxTweetLength, long sleepTimeMs)
    {
        // running infinite loop in new thread to avoid blocking main thread
        Executors.newSingleThreadExecutor().submit(() -> {
            try
            {
                while (true)
                {
                    final String formattedTweetAsRawJson = getFormattedTweet(keywords, minTweetLength, maxTweetLength);
                    final Status status = TwitterObjectFactory.createStatus(formattedTweetAsRawJson);
                    twitterKafkaStatusListener.onStatus(status);
                    sleep(sleepTimeMs);
                }
            }
            catch (TwitterException e)
            {
                log.error("Error creating twitter status", e);
            }
        });
    }

    private void sleep(long sleepTimeMs)
    {
        try
        {
            Thread.sleep(sleepTimeMs);
        }
        catch (InterruptedException e)
        {
            throw new TwitterToKafkaServiceException(
                    "Error while sleeping when waiting new for new status to be created!");
        }
    }

    private String getFormattedTweet(String[] keywords, int minTweetLength, int maxTweetLength)
    {
        final String[] params = new String[] {
                // would get DateTimeError if not using english locale and machine is not in english locale
                ZonedDateTime.now().format(DateTimeFormatter.ofPattern(TWITTER_STATUS_DATE_FORMAT, Locale.ENGLISH)),
                String.valueOf(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE)),
                getRandomTweetContent(keywords, minTweetLength, maxTweetLength),
                String.valueOf(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE)) };

        return formatTweetAsJsonWithParams(params);
    }

    private String formatTweetAsJsonWithParams(String[] params)
    {
        String tweet = tweetAsRawJson;
        for (int i = 0; i < params.length; i++)
        {
            tweet = tweet.replace("{" + i + "}", params[i]);
        }
        return tweet;
    }

    private String getRandomTweetContent(String[] keywords, int minTweetLength, int maxTweetLength)
    {
        final StringBuilder tweet = new StringBuilder();
        final int tweetLength = RANDOM.nextInt(maxTweetLength - minTweetLength + 1) + minTweetLength;

        return constructRandomTweet(keywords, tweet, tweetLength);
    }

    private String constructRandomTweet(String[] keywords, StringBuilder tweet, int tweetLength)
    {
        for (int i = 0; i < tweetLength; i++)
        {
            tweet.append(WORDS[RANDOM.nextInt(WORDS.length)]).append(" ");
            if (i == tweetLength / 2)
            {
                tweet.append(WORDS[RANDOM.nextInt(keywords.length)]).append(" ");
            }
        }
        return tweet.toString().trim();
    }
}

