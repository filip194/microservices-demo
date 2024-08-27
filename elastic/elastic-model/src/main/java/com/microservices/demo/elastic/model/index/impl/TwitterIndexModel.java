package com.microservices.demo.elastic.model.index.impl;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.microservices.demo.elastic.model.index.IndexModel;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.ZonedDateTime;

@Data
@Builder
// Spring expression language (SpEL) to get index-name "twitter-index" defined in configuration
// If the evaluation context has been configured with a bean resolver, you can look up beans from an expression by using the '@' symbol
@Document(indexName = "#{@elasticConfigData.indexName}")
public class TwitterIndexModel implements IndexModel {
    @JsonProperty
    private String id;
    @JsonProperty
    private Long userId;
    @JsonProperty
    private String text;

    // @Field annotation from spring-boot-starter-data-elasticsearch dependency used to be able to convert 'createdAt'
    // from LocalDateTime to elasticsearch date during indexing operation.
    // (TemporalAccessor properties must have @Field or custom converters)
    // We use the same pattern for both of them.
    // CUSTOM DATE: uuuu instead of yyyy for custom elasticsearch date: https://www.elastic.co/guide/en/elasticsearch/reference/current/migrate-to-java-time.html#java-time-migration-incompatible-date-formats
    @Field(type = FieldType.Date, format = {}, pattern = "uuuu-MM-dd'T'HH:mm:ssZZ")
    @JsonProperty
    // required while parsing a JSON to this object
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ssZZ")
    private ZonedDateTime createdAt;

}

/*
What is the purpose of using @Field annotation on temporal type like LocalDateTime?

- to convert it to easticsearch field during index
- to format the date using a pattern
- to set the field type
 */

