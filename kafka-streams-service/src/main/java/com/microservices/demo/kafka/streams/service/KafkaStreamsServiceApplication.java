package com.microservices.demo.kafka.streams.service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import com.microservices.demo.kafka.streams.service.init.StreamsInitializer;
import com.microservices.demo.kafka.streams.service.runner.StreamsRunner;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
@ComponentScan(basePackages = "com.microservices.demo")
public class KafkaStreamsServiceApplication implements CommandLineRunner
{
    private final StreamsRunner<String, Long> streamsRunner;
    private final StreamsInitializer streamsInitializer;

    public KafkaStreamsServiceApplication(StreamsRunner<String, Long> streamsRunner,
            StreamsInitializer streamsInitializer)
    {
        this.streamsRunner = streamsRunner;
        this.streamsInitializer = streamsInitializer;
    }

    public static void main(String[] args)
    {
        SpringApplication.run(KafkaStreamsServiceApplication.class, args);
    }

    @Override
    public void run(String... args)
    {
        log.info("App starts...");
        streamsInitializer.init();
        streamsRunner.start();
    }
}

