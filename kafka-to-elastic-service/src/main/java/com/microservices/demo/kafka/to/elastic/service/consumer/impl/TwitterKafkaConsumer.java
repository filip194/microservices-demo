package com.microservices.demo.kafka.to.elastic.service.consumer.impl;

import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import com.microservices.demo.config.KafkaConfigData;
import com.microservices.demo.config.KafkaConsumerConfigData;
import com.microservices.demo.kafka.admin.client.KafkaAdminClient;
import com.microservices.demo.kafka.avro.model.TwitterAvroModel;
import com.microservices.demo.kafka.to.elastic.service.consumer.KafkaConsumer;

@Service
public class TwitterKafkaConsumer implements KafkaConsumer<Long, TwitterAvroModel>
{
    private static final Logger LOG = LoggerFactory.getLogger(TwitterKafkaConsumer.class);

    private final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;
    private final KafkaAdminClient kafkaAdminClient;
    private final KafkaConfigData kafkaConfigData;
    private final KafkaConsumerConfigData kafkaConsumerConfigData;

    public TwitterKafkaConsumer(KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry,
            KafkaAdminClient kafkaAdminClient, KafkaConfigData kafkaConfigData,
            KafkaConsumerConfigData kafkaConsumerConfigData)
    {
        this.kafkaListenerEndpointRegistry = kafkaListenerEndpointRegistry;
        this.kafkaAdminClient = kafkaAdminClient;
        this.kafkaConfigData = kafkaConfigData;
        this.kafkaConsumerConfigData = kafkaConsumerConfigData;
    }

    @EventListener
    public void onAppStarted(ApplicationStartedEvent event)
    {
        kafkaAdminClient.checkTopicsCreated();
        LOG.info("Topics with names {} are ready for operations!", kafkaConfigData.getTopicNamesToCreate().toArray());
        Objects.requireNonNull(
                        kafkaListenerEndpointRegistry.getListenerContainer(kafkaConsumerConfigData.getConsumerGroupId()))
                .start();
    }

    // creates kafka consumer, Spring Boot applications don't need @EnableKafka annotation but Spring apps do
    // need to use this id from config, otherwise new id will override consumer-group-id ::: id = "${kafka-consumer-config.consumer-group-id}"
    // ${kafka-config.topic-name} is taking prop value from config-client-kafka_to_elastic.yml
    @Override
    @KafkaListener(id = "${kafka-consumer-config.consumer-group-id}", topics = "${kafka-config.topic-name}")
    public void receive(@Payload List<TwitterAvroModel> messages, /* kafka payloads */
            @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) List<Integer> keys, /* kafka message/payload keys */
            @Header(KafkaHeaders.RECEIVED_PARTITION_ID) List<Integer> partitions, /* kafka partition IDs */
            @Header(KafkaHeaders.OFFSET) List<Long> offsets/* kafka partition offsets while reading data in batches */)
    {
        LOG.info(
                "{} number of messages received with keys {}, partitions {} and offsets {}, sending it to elastic: Thread id {}",
                messages.size(), keys.toString(), partitions.toString(), offsets.toString(),
                Thread.currentThread().getId());
    }
}

