package com.microservices.demo.twitter.to.kafka.service.runner;

import twitter4j.TwitterException;

@FunctionalInterface
public interface StreamRunner
{
    void start() throws TwitterException;
}
