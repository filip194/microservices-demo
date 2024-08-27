package com.microservices.demo.elastic.query.service.security;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
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

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {
    private final TwitterQueryUserDetailsService twitterQueryUserDetailsService;
    private final OAuth2ResourceServerProperties oAuth2ResourceServerProperties;
    private final QueryServicePermissionEvaluator queryServicePermissionEvaluator;

    @Value("${security.paths-to-ignore}")
    private String[] pathsToIgnore;

    public WebSecurityConfig(TwitterQueryUserDetailsService twitterQueryUserDetailsService,
                             OAuth2ResourceServerProperties oAuth2ResourceServerProperties,
                             QueryServicePermissionEvaluator queryServicePermissionEvaluator) {
        this.twitterQueryUserDetailsService = twitterQueryUserDetailsService;
        this.oAuth2ResourceServerProperties = oAuth2ResourceServerProperties;
        this.queryServicePermissionEvaluator = queryServicePermissionEvaluator;
    }

    // needed to use hasPermission() in method security, in Spring Boot 2 it was auto-injected,
    // in Spring Boot 3 we need to explicitly register this QueryServicePermissionEvaluator and use it in expressionHandler
    // bean to be able to use hasPermission expression
    @Bean
    public MethodSecurityExpressionHandler expressionHandler() {
        final DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setPermissionEvaluator(queryServicePermissionEvaluator);
        return expressionHandler;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry -> authorizationManagerRequestMatcherRegistry
                        .requestMatchers(Arrays.stream(pathsToIgnore).map(AntPathRequestMatcher::new).toList().toArray(new RequestMatcher[]{}))
                        .permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(httpSecuritySessionManagementConfigurer -> httpSecuritySessionManagementConfigurer
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .csrf(AbstractHttpConfigurer::disable)
                .oauth2ResourceServer(httpSecurityOAuth2ResourceServerConfigurer -> httpSecurityOAuth2ResourceServerConfigurer
                        .jwt(jwtConfigurer -> jwtConfigurer
                                .jwtAuthenticationConverter(twitterQueryUserJwtConverter())
                        )
                );
        return http.build();
    }

//    DEPRECATED and() and replaced with Customizers in Spring Boot 3
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
//                .jwtAuthenticationConverter(twitterQueryUserJwtConverter());
//        return http.build();
//    }

//    DEPRECATED: extends WebSecurityConfigurerAdapter
//    @Override
//    protected void configure(HttpSecurity http) throws Exception
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
//                .jwtAuthenticationConverter(twitterQueryUserJwtConverter());
//    }

    // using qualifier here to inject our audience validator
    @Bean
    JwtDecoder JwtDecoder(
            @Qualifier("elastic-query-service-audience-validator") OAuth2TokenValidator<Jwt> audienceValidator) {
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
    Converter<Jwt, ? extends AbstractAuthenticationToken> twitterQueryUserJwtConverter() {
        return new TwitterQueryUserJwtConverter(twitterQueryUserDetailsService);
    }

//    DEPRECATED as ignored paths are defined above
//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer() {
//        return (webSecurity) -> webSecurity.ignoring().antMatchers(pathsToIgnore);
//    }

//    DEPRECATED: extends WebSecurityConfigurerAdapter
//    @Override
//    public void configure(WebSecurity webSecurity)
//    {
//       webSecurity
//               .ignoring()
//               .antMatchers(pathsToIgnore);
//    }

}

