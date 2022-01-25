package com.microservices.demo.twitter.to.kafka.service.init;

@FunctionalInterface
public interface StreamInitializer
{
    void init();
}
