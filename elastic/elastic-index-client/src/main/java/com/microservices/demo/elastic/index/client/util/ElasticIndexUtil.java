package com.microservices.demo.elastic.index.client.util;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.stereotype.Component;

import com.microservices.demo.elastic.model.index.IndexModel;

/**
 * Class converts list od IndexModel objects to a list of IndexQueries to be able to send them to elasticsearch
 *
 * @param <T>
 */
@Component
public class ElasticIndexUtil<T extends IndexModel>
{
    // create index query stream using IndexQueryBuilder
    public List<IndexQuery> getIndexQueries(List<T> documents)
    {
        return documents.stream()
                .map(document -> new IndexQueryBuilder().withId(document.getId()).withObject(document).build())
                .collect(Collectors.toList());
    }
}

