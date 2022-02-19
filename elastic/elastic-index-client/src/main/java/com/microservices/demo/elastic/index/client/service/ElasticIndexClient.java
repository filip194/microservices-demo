package com.microservices.demo.elastic.index.client.service;

import java.util.List;

import com.microservices.demo.elastic.model.index.IndexModel;

@FunctionalInterface
public interface ElasticIndexClient<T extends IndexModel>
{
    List<String> save(List<T> documents);
}
