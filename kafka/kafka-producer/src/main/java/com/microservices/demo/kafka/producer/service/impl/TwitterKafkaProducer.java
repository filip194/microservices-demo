package com.microservices.demo.kafka.producer.service.impl;

import javax.annotation.PreDestroy;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import com.microservices.demo.kafka.avro.model.TwitterAvroModel;
import com.microservices.demo.kafka.producer.service.KafkaProducer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TwitterKafkaProducer implements KafkaProducer<Long, TwitterAvroModel>
{
    private final KafkaTemplate<Long, TwitterAvroModel> kafkaTemplate;

    public TwitterKafkaProducer(KafkaTemplate<Long, TwitterAvroModel> kafkaTemplate)
    {
        this.kafkaTemplate = kafkaTemplate;
    }

    // Normally, Spring closes this kafka template (producer) prior shutdown,
    // but we called it here explicitly to be sure kafka template is destroyed successfully before app shutdown
    @PreDestroy
    public void close()
    {
        if (kafkaTemplate != null)
        {
            log.info("Closing Kafka producer!");
            kafkaTemplate.destroy();
        }
    }

    @Override
    public void send(String topicName, Long key, TwitterAvroModel message)
    {
        log.info("Sending message='{}' to topic='{}'", message, topicName);
        // send() method is async, so we'll need to add callback
        // @ListenableFuture -> used to register callback methods for handling events when response returns
        // @ListenableFuture is Spring's extension of Future which can accept completion callbacks
        final ListenableFuture<SendResult<Long, TwitterAvroModel>> kafkaResultFuture = kafkaTemplate.send(topicName,
                key, message);

        // add callbacks
        addCallback(topicName, message, kafkaResultFuture);
    }

    // Since send() method of KafkaTemplate is an async operation, it returns Listenable Future,
    // and to get response later asynchronously, we need to add callback and override onSuccess and onFailure methods
    private void addCallback(String topicName, TwitterAvroModel message,
            ListenableFuture<SendResult<Long, TwitterAvroModel>> kafkaResultFuture)
    {
        kafkaResultFuture.addCallback(new ListenableFutureCallback<>()
        {
            @Override
            public void onFailure(Throwable ex)
            {
                log.error("Error while sending message {} to topic {}", message, topicName, ex);
            }

            @Override
            public void onSuccess(SendResult<Long, TwitterAvroModel> result)
            {
                final RecordMetadata metadata = result.getRecordMetadata();
                log.debug("Received new metadata. Topic: {}; Partition {}; Offset {}; Timestamp {}, at time {}",
                        metadata.topic(), metadata.partition(), metadata.offset(), metadata.timestamp(),
                        System.nanoTime());
            }
        });
    }
}

