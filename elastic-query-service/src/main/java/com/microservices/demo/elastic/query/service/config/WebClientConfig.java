package com.microservices.demo.elastic.query.service.config;

import java.util.concurrent.TimeUnit;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import com.microservices.demo.config.ElasticQueryServiceConfigData;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import reactor.netty.http.client.HttpClient;

/**
 * We'll be manually setting OAuth2 web token, we'll be passing access token in the request itself
 */
@Configuration
public class WebClientConfig
{
    // data needed to construct our webclient
    private final ElasticQueryServiceConfigData.WebClient elasticQueryServiceConfigData;

    public WebClientConfig(ElasticQueryServiceConfigData elasticQueryServiceConfigData)
    {
        this.elasticQueryServiceConfigData = elasticQueryServiceConfigData.getWebClient();
    }

    @LoadBalanced
    @Bean("webClientBuilder_elasticQueryService")
    public WebClient.Builder webClientBuilder()
    {
        return WebClient.builder()
//                baseUrl is not set because we will use the same client for analytics service API and kafka streams API
//                .baseUrl(elasticQueryServiceConfigData.getBaseUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, elasticQueryServiceConfigData.getContentType())
                .defaultHeader(HttpHeaders.ACCEPT, elasticQueryServiceConfigData.getAcceptType())
                .clientConnector(new ReactorClientHttpConnector(getHttpClient()))
                .codecs(clientCodecConfigurer ->
                        clientCodecConfigurer
                                .defaultCodecs()
                                .maxInMemorySize(elasticQueryServiceConfigData.getMaxInMemorySize()));
    }

    private HttpClient getHttpClient()
    {
        return HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, elasticQueryServiceConfigData.getConnectTimeoutMs())
                .doOnConnected(connection -> {
                    connection.addHandlerLast(new ReadTimeoutHandler(
                            elasticQueryServiceConfigData.getReadTimeoutMs(), TimeUnit.MILLISECONDS));
                    connection.addHandlerLast(new WriteTimeoutHandler(
                            elasticQueryServiceConfigData.getWriteTimeoutMs(), TimeUnit.MILLISECONDS));
                });
    }

}

