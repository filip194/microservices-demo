package com.microservices.demo.gateway.service.config;

import com.microservices.demo.config.GatewayServiceConfigData;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Objects;

@Configuration
public class GatewayConfig {
    private static final String HEADER_FOR_KEY_RESOLVER = "Authorization";
    private final GatewayServiceConfigData gatewayServiceConfigData;

    public GatewayConfig(GatewayServiceConfigData gatewayServiceConfigData) {
        this.gatewayServiceConfigData = gatewayServiceConfigData;
    }

    // === RATE LIMITER === //
    // ==================== //
    // this bean for key resolver will be used by rate limiting filter
    // if we return a constant here, it will apply rate limiting  to whole application , and not per user or client
    // so, we will use the 'Authorization' header here to apply the filtering, so that different clients won't use each
    // other's tokens
    @Bean(name = "authHeaderResolver") // key resolver name set up in config-client-gateway.yml config
    public KeyResolver userKeyResolver() {
        return exchange -> Mono.just(
                Objects.requireNonNull(exchange.getRequest().getHeaders().getFirst(HEADER_FOR_KEY_RESOLVER)));
    }

    // === CIRCUIT BREAKER === //
    // ======================= //
    // set the required config to be used to decide to open the state/circuit for a service, which will cause
    // to be redirected to fallback controller.
    // we will configure ReactiveResilience4jCircuitBreakerFactory with the configureDefault method
    // first set the timeLimiterConfig, to set a timeout duration for the operations on circuit breaker
    // this is to prevent waiting indefinitely
    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> circuitBreakerFactoryCustomizer() {
        return reactiveResilience4JCircuitBreakerFactory -> reactiveResilience4JCircuitBreakerFactory.configureDefault(
                id -> new Resilience4JConfigBuilder(id).timeLimiterConfig(TimeLimiterConfig.custom()
                                .timeoutDuration(Duration.ofMillis(gatewayServiceConfigData.getTimeoutMs())).build())
                        .circuitBreakerConfig(CircuitBreakerConfig.custom()
                                .failureRateThreshold(gatewayServiceConfigData.getFailureRateThreshold())
                                .slowCallRateThreshold(gatewayServiceConfigData.getSlowCallRateThreshold())
                                .slowCallDurationThreshold(
                                        Duration.ofMillis(gatewayServiceConfigData.getSlowCallDurationThreshold()))
                                .permittedNumberOfCallsInHalfOpenState(
                                        gatewayServiceConfigData.getPermittedNumOfCallsInHalfOpenState())
                                .slidingWindowSize(gatewayServiceConfigData.getSlidingWindowSize())
                                .minimumNumberOfCalls(gatewayServiceConfigData.getMinNumberOfCalls())
                                .waitDurationInOpenState(
                                        Duration.ofMillis(gatewayServiceConfigData.getWaitDurationInOpenState()))
                                .build()).build());
    }
}

