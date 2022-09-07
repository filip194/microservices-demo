package com.microservices.demo.gateway.service.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class WebSecurityConfig
{
    // we will allow all paths
    // we won't apply any security here because we are applying it on the forwarded services
    // that's a design decision

    // also we could have applied security here adn remove it from services or do it on both sides
    // but, it is safer to use security on service itself because service can be called from somewhere else instead of gateway

    @Bean
    public SecurityWebFilterChain webFluxSecurityConfig(ServerHttpSecurity httpSecurity)
    {
        httpSecurity
                .authorizeExchange()
                .anyExchange()
                .permitAll();
        httpSecurity
                .csrf()
                .disable(); // we disabled csrf, as we only have a back-end interaction

        return httpSecurity.build();
    }

}

