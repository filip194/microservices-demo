package com.microservices.demo.kafka.to.elastic.service.consumer.impl;

import com.microservices.demo.config.KafkaConfigData;
import com.microservices.demo.config.KafkaConsumerConfigData;
import com.microservices.demo.elastic.index.client.service.ElasticIndexClient;
import com.microservices.demo.elastic.model.index.impl.TwitterIndexModel;
import com.microservices.demo.kafka.admin.client.KafkaAdminClient;
import com.microservices.demo.kafka.avro.model.TwitterAvroModel;
import com.microservices.demo.kafka.to.elastic.service.consumer.KafkaConsumer;
import com.microservices.demo.kafka.to.elastic.service.transformer.AvroToElasticModelTransformer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class TwitterKafkaConsumer implements KafkaConsumer<Long, TwitterAvroModel> {
    private final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;
    private final KafkaAdminClient kafkaAdminClient;
    private final KafkaConfigData kafkaConfigData;
    private final KafkaConsumerConfigData kafkaConsumerConfigData;

    private final AvroToElasticModelTransformer avroToElasticModelTransformer;
    private final ElasticIndexClient<TwitterIndexModel> elasticIndexClient;

    public TwitterKafkaConsumer(KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry,
                                KafkaAdminClient kafkaAdminClient, KafkaConfigData kafkaConfigData,
                                KafkaConsumerConfigData kafkaConsumerConfigData,
                                AvroToElasticModelTransformer avroToElasticModelTransformer,
                                ElasticIndexClient<TwitterIndexModel> elasticIndexClient) {
        this.kafkaListenerEndpointRegistry = kafkaListenerEndpointRegistry;
        this.kafkaAdminClient = kafkaAdminClient;
        this.kafkaConfigData = kafkaConfigData;
        this.kafkaConsumerConfigData = kafkaConsumerConfigData;
        this.avroToElasticModelTransformer = avroToElasticModelTransformer;
        this.elasticIndexClient = elasticIndexClient;
    }

    @EventListener
    public void onAppStarted(ApplicationStartedEvent event) {
        kafkaAdminClient.checkTopicsCreated();
        log.info("Topics with names {} are ready for operations!", kafkaConfigData.getTopicNamesToCreate().toArray());
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
                        @Header(KafkaHeaders.RECEIVED_KEY) List<Integer> keys, /* kafka message/payload keys */
                        @Header(KafkaHeaders.RECEIVED_PARTITION) List<Integer> partitions, /* kafka partition IDs */
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets/* kafka partition offsets while reading data in batches */) {
        log.info(
                "{} number of messages received with keys {}, partitions {} and offsets {}, sending it to elastic: Thread id {}",
                messages.size(), keys.toString(), partitions.toString(), offsets.toString(),
                Thread.currentThread().getId());

        final List<TwitterIndexModel> twitterIndexModels = avroToElasticModelTransformer.getElasticModels(messages);
        final List<String> documentIds = elasticIndexClient.save(twitterIndexModels);
        log.info("Documents saved to elasticsearch with IDs {}", documentIds.toArray());
    }
}

