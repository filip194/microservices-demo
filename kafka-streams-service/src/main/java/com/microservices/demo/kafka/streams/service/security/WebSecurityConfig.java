package com.microservices.demo.kafka.streams.service.security;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.Arrays;

/**
 * The same as WebSecurityConfig from elastic-query-service
 */
@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {
    private final KafkaStreamsUserDetailsService kafkaStreamsUserDetailsService;
    private final OAuth2ResourceServerProperties oAuth2ResourceServerProperties;

    @Value("${security.paths-to-ignore}")
    private String[] pathsToIgnore;

    public WebSecurityConfig(KafkaStreamsUserDetailsService kafkaStreamsUserDetailsService,
                             OAuth2ResourceServerProperties oAuth2ResourceServerProperties) {
        this.kafkaStreamsUserDetailsService = kafkaStreamsUserDetailsService;
        this.oAuth2ResourceServerProperties = oAuth2ResourceServerProperties;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry -> authorizationManagerRequestMatcherRegistry
                        .requestMatchers(Arrays.stream(pathsToIgnore)
                                .map(AntPathRequestMatcher::new)
                                .toList()
                                .toArray(new RequestMatcher[]{})
                        )
                )
                .sessionManagement(httpSecuritySessionManagementConfigurer -> httpSecuritySessionManagementConfigurer
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .csrf(AbstractHttpConfigurer::disable)
                .oauth2ResourceServer(httpSecurityOAuth2ResourceServerConfigurer -> httpSecurityOAuth2ResourceServerConfigurer
                        .jwt(jwtConfigurer -> jwtConfigurer
                                .jwtAuthenticationConverter(kafkaStreamsUserJwtAuthConverter())
                        )
                );
        return http.build();
    }

    //    DEPRECATED: Spring 6 security loses and() method and replaces it with Customizers
//    {
//        http
//                .sessionManagement()
//                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                .and()
//                .csrf()
//                .disable()
//                .authorizeRequests()
//                .anyRequest()
//                .fullyAuthenticated()
//                .and()
//                .oauth2ResourceServer()
//                .jwt()
//                .jwtAuthenticationConverter(kafkaStreamsUserJwtAuthConverter());
//        return http.build();
//    }

    // using qualifier here to inject our audience validator
    @Bean
    public JwtDecoder JwtDecoder(
            @Qualifier("kafka-streams-service-audience-validator") OAuth2TokenValidator<Jwt> audienceValidator) {
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
    public Converter<Jwt, ? extends AbstractAuthenticationToken> kafkaStreamsUserJwtAuthConverter() {
        return new KafkaStreamsUserJwtConverter(kafkaStreamsUserDetailsService);
    }

//    NOT NEEDED ANYMORE AS IT IS DEFINED ABOVE
//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer() {
//        return (webSecurity) -> webSecurity.ignoring().antMatchers(pathsToIgnore);
//    }

}

