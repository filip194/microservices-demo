package com.microservices.demo.kafka.streams.service.security;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

/**
 * The same as WebSecurityConfig from elastic-query-service
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter
{
    private final KafkaStreamsUserDetailsService kafkaStreamsUserDetailsService;
    private final OAuth2ResourceServerProperties oAuth2ResourceServerProperties;

    @Value("${security.paths-to-ignore}")
    private String[] pathsToIgnore;

    public WebSecurityConfig(KafkaStreamsUserDetailsService kafkaStreamsUserDetailsService,
            OAuth2ResourceServerProperties oAuth2ResourceServerProperties)
    {
        this.kafkaStreamsUserDetailsService = kafkaStreamsUserDetailsService;
        this.oAuth2ResourceServerProperties = oAuth2ResourceServerProperties;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception
    {
        http
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .csrf()
                .disable()
                .authorizeRequests()
                .anyRequest()
                .fullyAuthenticated()
                .and()
                .oauth2ResourceServer()
                .jwt()
                .jwtAuthenticationConverter(kafkaStreamsUserJwtAuthConverter());
    }

    // using qualifier here to inject our audience validator
    @Bean
    public JwtDecoder JwtDecoder(
            @Qualifier("kafka-streams-service-audience-validator") OAuth2TokenValidator<Jwt> audienceValidator)
    {
        // Nimbus is underlying library that Spring uses for JWT operations
        final NimbusJwtDecoder jwtDecoder = JwtDecoders.fromOidcIssuerLocation(
                oAuth2ResourceServerProperties.getJwt().getIssuerUri());
        final OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(
                oAuth2ResourceServerProperties.getJwt().getIssuerUri());
        final OAuth2TokenValidator<Jwt> withAudience = new DelegatingOAuth2TokenValidator<>(
                withIssuer, audienceValidator);
        // set delegating validator to Nimbus decoder
        jwtDecoder.setJwtValidator(withAudience);
        // by default, withIssuer validator also adds timestamp expiration validator automatically
        // here we also added audience validator on top of other validators
        return jwtDecoder;
    }

    @Bean
    public Converter<Jwt, ? extends AbstractAuthenticationToken> kafkaStreamsUserJwtAuthConverter()
    {
        return new KafkaStreamsUserJwtConverter(kafkaStreamsUserDetailsService);
    }

    @Override
    public void configure(WebSecurity webSecurity)
    {
        webSecurity
                .ignoring()
                .antMatchers(pathsToIgnore);
    }

}

