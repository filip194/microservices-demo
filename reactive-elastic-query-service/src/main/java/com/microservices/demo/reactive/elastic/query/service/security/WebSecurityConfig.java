package com.microservices.demo.reactive.elastic.query.service.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class WebSecurityConfig {
    @Bean
    public SecurityWebFilterChain webFluxSecurityFilterChain(ServerHttpSecurity httpSecurity) {
        httpSecurity.authorizeExchange()
                .anyExchange()
                .permitAll();
        httpSecurity.csrf().disable();
        return httpSecurity.build();
    }
}

