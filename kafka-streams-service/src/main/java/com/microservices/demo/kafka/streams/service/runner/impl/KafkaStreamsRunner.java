package com.microservices.demo.kafka.streams.service.runner.impl;

import com.microservices.demo.config.KafkaConfigData;
import com.microservices.demo.config.KafkaStreamsConfigData;
import com.microservices.demo.kafka.avro.model.TwitterAnalyticsAvroModel;
import com.microservices.demo.kafka.avro.model.TwitterAvroModel;
import com.microservices.demo.kafka.streams.service.runner.StreamsRunner;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

@Slf4j
@Component
public class KafkaStreamsRunner implements StreamsRunner<String, Long> {
    // matches any number character in the text
    private static final String REGEX = "\\W+";

    private final KafkaStreamsConfigData kafkaStreamsConfigData;
    private final KafkaConfigData kafkaConfigData;
    private final Properties streamsConfiguration;

    private KafkaStreams kafkaStreams;
    private volatile ReadOnlyKeyValueStore<String, Long> keyValueStore;

    public KafkaStreamsRunner(KafkaStreamsConfigData kafkaStreamsConfigData, KafkaConfigData kafkaConfigData,
                              @Qualifier("streamsConfiguration") Properties streamsConfiguration) {
        this.kafkaStreamsConfigData = kafkaStreamsConfigData;
        this.kafkaConfigData = kafkaConfigData;
        this.streamsConfiguration = streamsConfiguration;
    }

    @Override
    public void start() {
        final Map<String, String> serdeConfig = Collections.singletonMap(
                kafkaConfigData.getSchemaRegistryUrlKey(),
                kafkaConfigData.getSchemaRegistryUrl()
        );

        // main class to create Kafka stream
        final StreamsBuilder streamsBuilder = new StreamsBuilder();

        final KStream<Long, TwitterAvroModel> twitterAvroModelKStream =
                getTwitterAvroModelKStream(serdeConfig, streamsBuilder);

        // Creating computation logic for aggregations which is called TOPOLOGY in Kafka streams terminology
        createTopology(twitterAvroModelKStream, serdeConfig);
        startStreaming(streamsBuilder);
    }

    @Override
    public Long getValueByKey(String word) {
        if (kafkaStreams != null && kafkaStreams.state() == KafkaStreams.State.RUNNING) {
            if (keyValueStore == null) {
                synchronized (this) {
                    if (keyValueStore == null) {
                        keyValueStore = kafkaStreams.store(StoreQueryParameters.fromNameAndType(
                                kafkaStreamsConfigData.getWordCountStoreName(),
                                QueryableStoreTypes.keyValueStore()
                        ));
                    }
                }
            }
            return keyValueStore.get(word.toLowerCase());
        }
        return 0L;
    }

    @PreDestroy
    public void close() {
        if (kafkaStreams != null) {
            kafkaStreams.close();
            log.info("Kafka streaming closed!");
        }
    }

    // TOPOLOGY in Kafka streams terminology is computation logic
    private void createTopology(KStream<Long, TwitterAvroModel> twitterAvroModelKStream,
                                Map<String, String> serdeConfig) {
        final Pattern pattern = Pattern.compile(REGEX, Pattern.UNICODE_CHARACTER_CLASS);

        // create a method getSerdeAnalyticsModel to create and return the output model Serde
        // object which will be a Serde of generic type TwitterAnalyticsAvroModel.
        final Serde<TwitterAnalyticsAvroModel> serdeTwitterAnalyticsAvroModel = getSerdeAnalyticsAvroModel(serdeConfig);

        // 1. mapping the input to list of words on the text field
        // 2. groupBy method returns a group stream and it can be aggregaed with the method count, to return
        // a KTable that holds words and count per word
        // 3. mapping stream to new TwitterAnalyticsModel generated fom Avro scheme
        // So, in new topic we will use word as key and TwitterAnalyticsModel as a value
        twitterAvroModelKStream
                .flatMapValues(value -> Arrays.asList(pattern.split(value.getText().toLowerCase())))
                .groupBy((key, word) -> word)
                .count(Materialized.as(kafkaStreamsConfigData.getWordCountStoreName())) // create state store to hold the latest state of the computation and allow it to be queried by REST API; store is of byte array; byte[]; store holds key-value pairs for word as key and word count as value
                .toStream()
                .map(mapToAnalyticsModel())
                .to(kafkaStreamsConfigData.getOutputTopicName(),
                        Produced.with(Serdes.String(), serdeTwitterAnalyticsAvroModel));
    }

    private void startStreaming(StreamsBuilder streamsBuilder) {
        final Topology topology = streamsBuilder.build();
        log.info("Defined topology: {}", topology.describe());
        kafkaStreams = new KafkaStreams(topology, streamsConfiguration);
        kafkaStreams.start();
        log.info("Kafka streaming started... ");
    }

    private KeyValueMapper<String, Long, KeyValue<? extends String, ? extends TwitterAnalyticsAvroModel>>
    mapToAnalyticsModel() {
        return (word, count) -> {
            log.info("Sending to topic {}, word {} - count {}",
                    kafkaStreamsConfigData.getOutputTopicName(), word, count);
            return new KeyValue<>(word, TwitterAnalyticsAvroModel
                    .newBuilder()
                    .setWord(word)
                    .setWordCount(count)
                    .setCreatedAt(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC))
                    .build());
        };
    }

    private Serde<TwitterAnalyticsAvroModel> getSerdeAnalyticsAvroModel(Map<String, String> serdeConfig) {
        final Serde<TwitterAnalyticsAvroModel> serdeTwitterAnalyticsAvroModel = new SpecificAvroSerde<>();
        serdeTwitterAnalyticsAvroModel.configure(serdeConfig, false);
        return serdeTwitterAnalyticsAvroModel;
    }

    private KStream<Long, TwitterAvroModel> getTwitterAvroModelKStream(Map<String, String> serdeConfig,
                                                                       StreamsBuilder streamsBuilder) {
        // Serde class specifies serialization object; there are predefined types like String and Long, or
        // we can use custom Serde serialization object with generics and use SpecificAvroSerde<> class
        // here we created spefici Serde for input topic with type TwitterAvroModel
        final Serde<TwitterAvroModel> serdeTwitterAvroModel = new SpecificAvroSerde<>();
        serdeTwitterAvroModel.configure(serdeConfig, false);

        // As you see for key we use Serdes.Long, as in the input kafka topic, we have a Long key and
        // TwitterAvromModel for the the value.
        return streamsBuilder.stream(kafkaStreamsConfigData.getInputTopicName(),
                Consumed.with(Serdes.Long(), serdeTwitterAvroModel));
    }

}

