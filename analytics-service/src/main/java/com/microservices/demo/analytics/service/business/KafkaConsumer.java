package com.microservices.demo.analytics.service.business;

import java.util.List;

import org.apache.avro.specific.SpecificRecordBase;

@FunctionalInterface
public interface KafkaConsumer<T extends SpecificRecordBase>
{
    void receive(List<T> messages, List<String> keys, List<Integer> partitions, List<Long> offsets);
}
