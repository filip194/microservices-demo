package com.microservices.demo.elastic.query.client.service;

import java.util.List;

import com.microservices.demo.elastic.model.index.IndexModel;

public interface ElasticQueryClient<T extends IndexModel>
{
    T getIndexModelById(String id);

    List<T> getIndexModelByText(String text);

    List<T> getAllIndexModels();
}
