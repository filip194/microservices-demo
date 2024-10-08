package com.microservices.demo.analytics.service.business;

import org.apache.avro.specific.SpecificRecordBase;

import java.util.List;

@FunctionalInterface
public interface KafkaConsumer<T extends SpecificRecordBase> {
    void receive(List<T> messages, List<String> keys, List<Integer> partitions, List<Long> offsets);
}
