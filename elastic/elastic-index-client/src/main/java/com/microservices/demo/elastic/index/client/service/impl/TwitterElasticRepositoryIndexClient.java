package com.microservices.demo.elastic.index.client.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.microservices.demo.elastic.index.client.repository.TwitterElasticSearchIndexRepository;
import com.microservices.demo.elastic.index.client.service.ElasticIndexClient;
import com.microservices.demo.elastic.model.index.impl.TwitterIndexModel;

import lombok.extern.slf4j.Slf4j;

/**
 * Easier to write code than with ElasticsearchOperations,
 * but you are more restricted in configuring low level elasticsearch queries
 */
@Slf4j
// @Primary // this will be primary implementation of ElasticIndexClient interface, as now we have two implementations  of the interface so running app will throw ambiguity error on startup:
//TwitterElasticIndexClient
//TwitterElasticRepositoryIndexClient (primary now)
@Service
@ConditionalOnProperty(name = "elastic-config.is-repository", havingValue = "true", matchIfMissing = true)
public class TwitterElasticRepositoryIndexClient implements ElasticIndexClient<TwitterIndexModel>
{
    private TwitterElasticSearchIndexRepository twitterElasticSearchIndexRepository;

    public TwitterElasticRepositoryIndexClient(TwitterElasticSearchIndexRepository twitterElasticSearchIndexRepository)
    {
        this.twitterElasticSearchIndexRepository = twitterElasticSearchIndexRepository;
    }

    @Override
    public List<String> save(List<TwitterIndexModel> documents)
    {
        final List<TwitterIndexModel> repositoryResponse =
                (List<TwitterIndexModel>) twitterElasticSearchIndexRepository.saveAll(documents);
        final List<String> ids = repositoryResponse.stream().map(TwitterIndexModel::getId).collect(Collectors.toList());
        log.info("Documents indexed successfully with type: {} and IDs: {}",
                TwitterElasticRepositoryIndexClient.class.getName(), ids);
        return null;
    }
}

