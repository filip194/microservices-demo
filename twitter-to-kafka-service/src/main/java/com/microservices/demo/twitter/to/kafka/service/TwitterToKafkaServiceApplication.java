package com.microservices.demo.twitter.to.kafka.service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import com.microservices.demo.twitter.to.kafka.service.init.StreamInitializer;
import com.microservices.demo.twitter.to.kafka.service.runner.StreamRunner;

import lombok.extern.slf4j.Slf4j;

/**
 * <h1>GOAL:</h1>
 * <p>
 * Service will not be triggered by the client, it needs to start reading data from twitter when the application starts,
 * so we need to find a way to trigger that reading logic.
 * </p>
 * Exploring options:
 * <ul>
 *     <li>@Scope - annotation for Spring Bean creation (singleton, prototype, request, session, application, websocket)</li>
 *     <li>@PostConstruct - executed after dependency injection</li>
 *     <li>implementing ApplicationListener - overriding onApplicationEvent() method, runs only once so we can use it to trigger reading data from twitter</li>
 *     <li>implementing CommandLineRunner  - overriding run() method</li>
 *     <li>@EventListener annotation - annotating method</li>
 * </ul>@Scope, @PostConstruct, implementing ApplicationListener (overriding onApplicationEvent() method), implementing CommandLineRunner
 */

// with slf4j logger we will log our data by definition provided in logback-spring.xml
@Slf4j
@SpringBootApplication
// required to find Spring beans in other modules
@ComponentScan(basePackages = "com.microservices.demo")
//@Scope(scopeName = "request") // would create new bean for every request
public class TwitterToKafkaServiceApplication /*implements ApplicationListener*/ implements CommandLineRunner
{
    private final StreamRunner streamRunner;
    private final StreamInitializer streamInitializer;

    public TwitterToKafkaServiceApplication(StreamRunner streamRunner, StreamInitializer streamInitializer)
    {
        this.streamRunner = streamRunner;
        this.streamInitializer = streamInitializer;
    }

    public static void main(String[] args)
    {
        SpringApplication.run(TwitterToKafkaServiceApplication.class, args);
    }

    // since we don't need to use ApplicationEvent(s), this is perfect option, so initialization code can rest here
    @Override
    public void run(String... args) throws Exception
    {
        log.info("Starting application...");
        // Arrays - new utility class introduced in Java 11
        streamInitializer.init();
        streamRunner.start();
    }

    // this method will only run once, so we can use it to trigger reading data from twitter
    //    @Override
    //    public void onApplicationEvent(ApplicationEvent event)
    //    {
    //
    //    }

    // executed after dependency injection
    //    @PostConstruct
    //    public void init()
    //    {
    //    }
}

