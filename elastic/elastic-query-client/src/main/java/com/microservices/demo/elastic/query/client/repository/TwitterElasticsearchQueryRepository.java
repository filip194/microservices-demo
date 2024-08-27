package com.microservices.demo.elastic.query.client.repository;

import com.microservices.demo.elastic.model.index.impl.TwitterIndexModel;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TwitterElasticsearchQueryRepository extends ElasticsearchRepository<TwitterIndexModel, String> {
    // implemented by spring on runtime to query elasticsearch using text field on specified index
    List<TwitterIndexModel> findByText(String text);
}
