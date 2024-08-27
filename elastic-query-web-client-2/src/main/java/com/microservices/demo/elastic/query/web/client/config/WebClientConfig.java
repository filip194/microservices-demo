package com.microservices.demo.elastic.query.web.client.config;

import com.microservices.demo.config.ElasticQueryWebClientConfigData;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

import java.util.concurrent.TimeUnit;

@Configuration
// Not needed as we use client side load balancing with eureka
// @LoadBalancerClient(name = "elastic-query-service", configuration = ElasticQueryServiceInstanceListSupplierConfig.class)
public class WebClientConfig {
    private final ElasticQueryWebClientConfigData.WebClient elasticQueryWebClientConfigData;

    @Value("${security.default-client-registration-id}")
    private String defaultClientRegistrationId;

    public WebClientConfig(ElasticQueryWebClientConfigData elasticQueryWebClientConfigData) {
        this.elasticQueryWebClientConfigData = elasticQueryWebClientConfigData.getWebClient();
    }

    /*
     * @LoadBalanced -> to use this annotation we must create a bean in configuration, and we must use Builder of
     * org.springframework.web.reactive.function.client.WebClient for it have any effect
     */
    @LoadBalanced
    @Bean("webClientBuilder")
    public WebClient.Builder webClientBuilder(
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository
    ) {
        final ServletOAuth2AuthorizedClientExchangeFilterFunction oauth2 =
                new ServletOAuth2AuthorizedClientExchangeFilterFunction(
                        clientRegistrationRepository,
                        oAuth2AuthorizedClientRepository
                );
        // access token will be added to each request automatically
        oauth2.setDefaultOAuth2AuthorizedClient(true);
        oauth2.setDefaultClientRegistrationId(defaultClientRegistrationId); // in config.yml file keycloak is used as/for registration

        return WebClient.builder()
                // setup user information for basic authentication - NOT NEEDED ANYMORE
//                .filter(ExchangeFilterFunctions.basicAuthentication(
//                        userConfigData.getUsername(),
//                        userConfigData.getPassword()))
                .baseUrl(elasticQueryWebClientConfigData.getBaseUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, elasticQueryWebClientConfigData.getContentType())
                .defaultHeader(HttpHeaders.ACCEPT, elasticQueryWebClientConfigData.getAcceptType())
                // not needed but shown as options that can be overridden; setting TCP client explicitly
                // DEPRECATED (TCP Client)
                // .clientConnector(new ReactorClientHttpConnector(HttpClient.from(getTcpClient())))
                .clientConnector(new ReactorClientHttpConnector(getHttpClient()))
                .apply(oauth2.oauth2Configuration())
                .codecs(clientCodecConfigurer -> clientCodecConfigurer.defaultCodecs()
                        .maxInMemorySize(elasticQueryWebClientConfigData.getMaxInMemorySize()));
    }

    private HttpClient getHttpClient() {
        return HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, elasticQueryWebClientConfigData.getConnectTimeoutMs())
                .doOnConnected(connection -> {
                    connection.addHandlerLast(new ReadTimeoutHandler(
                            elasticQueryWebClientConfigData.getReadTimeoutMs(), TimeUnit.MILLISECONDS));
                    connection.addHandlerLast(new WriteTimeoutHandler(
                            elasticQueryWebClientConfigData.getWriteTimeoutMs(), TimeUnit.MILLISECONDS));
                });
    }

    @Deprecated
    private TcpClient getTcpClient() {
        return TcpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, elasticQueryWebClientConfigData.getConnectTimeoutMs())
                .doOnConnected(connection -> connection.addHandlerLast(
                        new ReadTimeoutHandler(elasticQueryWebClientConfigData.getReadTimeoutMs(),
                                TimeUnit.MILLISECONDS)).addHandlerLast(
                        new WriteTimeoutHandler(elasticQueryWebClientConfigData.getWriteTimeoutMs(),
                                TimeUnit.MILLISECONDS)));
    }
}

