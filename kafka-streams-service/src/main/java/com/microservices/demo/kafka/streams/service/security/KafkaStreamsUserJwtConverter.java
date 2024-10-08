package com.microservices.demo.kafka.streams.service.security;

import com.microservices.demo.kafka.streams.service.Constants;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The same as TwitterQueryUserJwtConverter from elastic-query-service to construct user authorities from JWT claims
 */
public class KafkaStreamsUserJwtConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    private static final String REALM_ACCESS_CLAIM = "realm_access";
    private static final String ROLES_CLAIM = "roles";
    private static final String SCOPE_CLAIM = "scope";
    private static final String USERNAME_CLAIM = "preferred_username";
    private static final String DEFAULT_ROLE_PREFIX = "ROLE_";
    private static final String DEFAULT_SCOPE_PREFIX = "SCOPE_";
    private static final String SCOPE_SEPARATOR = " ";

    private final KafkaStreamsUserDetailsService kafkaStreamsUserDetailsService;

    public KafkaStreamsUserJwtConverter(KafkaStreamsUserDetailsService kafkaStreamsUserDetailsService) {
        this.kafkaStreamsUserDetailsService = kafkaStreamsUserDetailsService;
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        final Collection<GrantedAuthority> authoritiesFromJwt = getAuthoritiesFromJwt(jwt);
        return Optional.ofNullable(
                        kafkaStreamsUserDetailsService.loadUserByUsername(jwt.getClaimAsString(USERNAME_CLAIM)))
                .map(userDetails -> {
                    ((KafkaStreamsUser) userDetails).setAuthorities(authoritiesFromJwt);
                    return new UsernamePasswordAuthenticationToken(userDetails, Constants.NA, authoritiesFromJwt);
                }).orElseThrow(() -> new BadCredentialsException("User could not be found"));
    }

    private Collection<GrantedAuthority> getAuthoritiesFromJwt(Jwt jwt) {
        return getCombinedAuthorities(jwt).stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    // combine results from getRoles and getScopes
    private Collection<String> getCombinedAuthorities(Jwt jwt) {
        final Collection<String> authorities = getRoles(jwt);
        authorities.addAll(getScopes(jwt));
        return authorities;
    }

    @SuppressWarnings("unchecked")
    private Collection<String> getRoles(Jwt jwt) {
        final Object roles = ((Map<String, Object>) jwt.getClaims().get(REALM_ACCESS_CLAIM)).get(ROLES_CLAIM);

        if (roles instanceof Collection) {
            return ((Collection<String>) roles).stream().map(authority -> DEFAULT_ROLE_PREFIX + authority.toUpperCase())
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private Collection<String> getScopes(Jwt jwt) {
        final Object scopes = jwt.getClaims().get(SCOPE_CLAIM);

        if (scopes instanceof String) {
            return Arrays.stream(((String) scopes).split(SCOPE_SEPARATOR))
                    .map(authority -> DEFAULT_SCOPE_PREFIX + authority.toUpperCase()).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}

