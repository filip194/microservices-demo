package com.microservices.demo.kafka.producer.service;

import org.apache.avro.specific.SpecificRecordBase;

import java.io.Serializable;

@FunctionalInterface
public interface KafkaProducer<K extends Serializable, V extends SpecificRecordBase> {
    // so key will extend Serializable (Long), and message will be of Avro format (TwitterAvroModel)
    // this 'key' is partition key in kafka that will set the target partition for a message
    // that is autogenerated from twitter.avsc file in kafka-model module resources
    void send(String topicName, K key, V message);
}
