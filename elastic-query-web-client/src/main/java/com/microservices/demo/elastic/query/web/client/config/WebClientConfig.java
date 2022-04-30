package com.microservices.demo.elastic.query.web.client.config;

import java.util.concurrent.TimeUnit;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunctions;
import org.springframework.web.reactive.function.client.WebClient;

import com.microservices.demo.config.ElasticQueryWebClientConfigData;
import com.microservices.demo.config.UserConfigData;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

@Configuration
@LoadBalancerClient(name = "elastic-query-service", configuration = ElasticQueryServiceInstanceListSupplierConfig.class)
public class WebClientConfig
{

    private final ElasticQueryWebClientConfigData.WebClient elasticQueryWebClientConfigData;
    private final UserConfigData userConfigData;

    public WebClientConfig(ElasticQueryWebClientConfigData elasticQueryWebClientConfigData,
            UserConfigData userConfigData)
    {
        this.elasticQueryWebClientConfigData = elasticQueryWebClientConfigData.getWebClient();
        this.userConfigData = userConfigData;
    }

    /*
     * @LoadBalanced -> to use this annotation we must create a bean in configuration, and we must use Builder of
     * org.springframework.web.reactive.function.client.WebClient for it have any effect
     */
    @LoadBalanced
    @Bean("webClientBuilder")
    public WebClient.Builder webClientBuilder()
    {
        return WebClient.builder()
                // setup user information for basic authentication
                .filter(ExchangeFilterFunctions.basicAuthentication(userConfigData.getUsername(),
                        userConfigData.getPassword())).baseUrl(elasticQueryWebClientConfigData.getBaseUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, elasticQueryWebClientConfigData.getContentType())
                .defaultHeader(HttpHeaders.ACCEPT, elasticQueryWebClientConfigData.getAcceptType())
                // not needed but shown as options that can be overridden; setting TCP client explicitly
                // DEPRECATED
                // .clientConnector(new ReactorClientHttpConnector(HttpClient.from(getTcpClient())))
                .clientConnector(new ReactorClientHttpConnector(getHttpClient()))
                .codecs(clientCodecConfigurer -> clientCodecConfigurer.defaultCodecs()
                        .maxInMemorySize(elasticQueryWebClientConfigData.getMaxInMemorySize()));
    }

    private HttpClient getHttpClient()
    {
        return HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, elasticQueryWebClientConfigData.getConnectTimeoutMs())
                .doOnConnected(connection -> connection.addHandlerLast(
                        new ReadTimeoutHandler(elasticQueryWebClientConfigData.getReadTimeoutMs(),
                                TimeUnit.MILLISECONDS)).addHandlerLast(
                        new WriteTimeoutHandler(elasticQueryWebClientConfigData.getWriteTimeoutMs(),
                                TimeUnit.MILLISECONDS)));
    }

    @Deprecated
    private TcpClient getTcpClient()
    {
        return TcpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, elasticQueryWebClientConfigData.getConnectTimeoutMs())
                .doOnConnected(connection -> connection.addHandlerLast(
                        new ReadTimeoutHandler(elasticQueryWebClientConfigData.getReadTimeoutMs(),
                                TimeUnit.MILLISECONDS)).addHandlerLast(
                        new WriteTimeoutHandler(elasticQueryWebClientConfigData.getWriteTimeoutMs(),
                                TimeUnit.MILLISECONDS)));
    }
}

