package com.microservices.demo.kafka.admin.client;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicListing;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.retry.RetryContext;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.microservices.demo.config.KafkaConfigData;
import com.microservices.demo.config.RetryConfigData;
import com.microservices.demo.kafka.admin.exception.KafkaClientException;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class KafkaAdminClient
{
    private final KafkaConfigData kafkaConfigData;
    private final RetryConfigData retryConfigData;
    private final AdminClient adminClient;
    private final RetryTemplate retryTemplate;
    private final WebClient webClient;

    public KafkaAdminClient(KafkaConfigData kafkaConfigData, RetryConfigData retryConfigData, AdminClient adminClient,
            RetryTemplate retryTemplate, WebClient webClient)
    {
        this.kafkaConfigData = kafkaConfigData;
        this.retryConfigData = retryConfigData;
        this.adminClient = adminClient;
        this.retryTemplate = retryTemplate;
        this.webClient = webClient;
    }

    // =================================================================================================================
    // Next 3 methods will be used to programmatically check Kafka and schema registry are up, and Topics are created
    // =================================================================================================================
    public void createTopics()
    {
        CreateTopicsResult createTopicsResult;
        try
        {
            createTopicsResult = retryTemplate.execute(this::doCreateTopics);
        }
        catch (Throwable t)
        {
            throw new KafkaClientException("Reached max number of retries for creating Kafka topic(s)!", t);
        }
        checkTopicsCreated();
    }

    public void checkTopicsCreated()
    {
        Collection<TopicListing> topics = getTopics();

        int retryCount = 1;
        Integer maxRetry = retryConfigData.getMaxAttempts();
        int multiplier = retryConfigData.getMultiplier().intValue();
        Long sleepTimeMs = retryConfigData.getSleepTimeMs();

        for (String topic : kafkaConfigData.getTopicNamesToCreate())
        {
            while (!isTopicCreated(topics, topic))
            {
                checkMaxRetry(retryCount++, maxRetry);
                sleep(sleepTimeMs);
                sleepTimeMs *= multiplier;
                // this might take some time to see created topics because createTopics is async operation
                topics = getTopics();
            }
        }
    }

    // check if schema registry is up and running, to do it we need to make a REST call to schema registry endpoint
    public void checkSchemaRegistry()
    {
        int retryCount = 1;
        Integer maxRetry = retryConfigData.getMaxAttempts();
        int multiplier = retryConfigData.getMultiplier().intValue();
        Long sleepTimeMs = retryConfigData.getSleepTimeMs();

        while (!getSchemaRegistryStatus().is2xxSuccessful())
        {
            checkMaxRetry(retryCount++, maxRetry);
            sleep(sleepTimeMs);
            sleepTimeMs *= multiplier;
        }
    }

    // ===============================================================================================================
    // ===============================================================================================================

    private HttpStatus getSchemaRegistryStatus()
    {
        try
        {
            return webClient.method(HttpMethod.GET).uri(kafkaConfigData.getSchemaRegistryUrl())
                    .exchangeToMono(response -> {
                        if (response.statusCode().is2xxSuccessful())
                        {
                            return Mono.just(response.statusCode());
                        }
                        else
                        {
                            return Mono.just(HttpStatus.SERVICE_UNAVAILABLE);
                        }
                    }).block();
        }
        catch (Exception e)
        {
            return HttpStatus.SERVICE_UNAVAILABLE;
        }
    }

    private void sleep(Long sleepTimeMs)
    {
        try
        {
            Thread.sleep(sleepTimeMs);
        }
        catch (InterruptedException e)
        {
            throw new KafkaClientException("Error while sleeping as to wait for newly created topics!");
        }
    }

    private void checkMaxRetry(int retry, Integer maxRetry)
    {
        if (retry > maxRetry)
        {
            throw new KafkaClientException("Reached max number of retries for creating Kafka topic(s)!");
        }
    }

    private boolean isTopicCreated(Collection<TopicListing> topics, String topicName)
    {
        if (topics == null)
        {
            return false;
        }
        return topics.stream().anyMatch(topic -> topic.name().equals(topicName));
    }

    private CreateTopicsResult doCreateTopics(RetryContext retryContext)
    {
        final List<String> topicNames = kafkaConfigData.getTopicNamesToCreate();
        log.info("Creating {} topic(s), attempt {}", topicNames.size(), retryContext.getRetryCount());

        final List<NewTopic> kafkaTopics = topicNames.stream()
                .map(topic -> new NewTopic(topic.trim(), kafkaConfigData.getNumOfPartitions(),
                        kafkaConfigData.getReplicationFactor())).collect(Collectors.toList());
        // createTopics is async operations
        return adminClient.createTopics(kafkaTopics);
    }

    private Collection<TopicListing> getTopics()
    {
        Collection<TopicListing> topics;
        try
        {
            topics = retryTemplate.execute(this::doGetTopics);
        }
        catch (Throwable t)
        {
            throw new KafkaClientException("Reached max number of retries for reading Kafka topic(s)!", t);
        }
        return topics;
    }

    private Collection<TopicListing> doGetTopics(RetryContext retryContext)
            throws ExecutionException, InterruptedException
    {
        log.info("Reading Kafka {} topic, attempt {}", kafkaConfigData.getTopicNamesToCreate().toArray(),
                retryContext.getRetryCount());
        final Collection<TopicListing> topics = adminClient.listTopics().listings().get();

        if (topics != null)
        {
            topics.forEach(topic -> log.debug("Topic with name {} is ready", topic.name()));
        }

        return topics;
    }

}

