package com.microservices.demo.elastic.query.web.client.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Configuration
//@EnableWebSecurity // should be removed if using spring-boot-starter-oauth2-client
public class WebSecurityConfig {
    private static final String GROUPS_CLAIM = "groups";
    private static final String ROLE_PREFIX = "ROLE_";

    //    private final UserConfigData userConfigData; // also removed when using spring-boot-starter-oauth2-client
    private final ClientRegistrationRepository clientRegistrationRepository;

    @Value("${security.logout-success-url}")
    private String logoutSuccessUrl;

    public WebSecurityConfig(ClientRegistrationRepository clientRegistrationRepository) {
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    OidcClientInitiatedLogoutSuccessHandler oidcLogoutSuccessHandler() {
        final OidcClientInitiatedLogoutSuccessHandler successHandler = new OidcClientInitiatedLogoutSuccessHandler(
                clientRegistrationRepository);
        successHandler.setPostLogoutRedirectUri(logoutSuccessUrl);
        return successHandler;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry -> authorizationManagerRequestMatcherRegistry
                        .requestMatchers(new AntPathRequestMatcher("/"))
                        .permitAll()
                        .anyRequest().authenticated()
                )
                .logout(httpSecurityLogoutConfigurer -> httpSecurityLogoutConfigurer
                        .logoutSuccessHandler(oidcLogoutSuccessHandler())
                )
                .oauth2Client(Customizer.withDefaults())
                .oauth2Login(httpSecurityOAuth2ClientConfigurer -> httpSecurityOAuth2ClientConfigurer
                        .userInfoEndpoint(Customizer.withDefaults())
                );
        // userAuthoritiesMapper is replaced with public bean GrantedAuthoritiesMapper userAuthoritiesMapper() and
        // will be auto-recognized and injected
        return http.build();
    }

//    DEPRECATED: Spring 6 security loses and() method and replaces it with Customizers
//    {
//        http
//                .authorizeRequests()
//                .antMatchers("/").permitAll()
//                .anyRequest()
//                .fullyAuthenticated()
//                .and()
//                .logout()
//                .logoutSuccessHandler(oidcLogoutSuccessHandler())
//                .and()
//                .oauth2Client()
//                .and()
//                .oauth2Login()
//                .userInfoEndpoint()
//                .userAuthoritiesMapper(userAuthoritiesMapper());
//        return http.build();
//    }

//    DEPRECATED: extends WebSecurityConfigurerAdapter
//    @Override
//    protected void configure(HttpSecurity http) throws Exception
//    {
//        http
//                .authorizeRequests()
//                .antMatchers("/").permitAll()
//                .anyRequest()
//                .fullyAuthenticated()
//                .and()
//                .logout()
//                .logoutSuccessHandler(oidcLogoutSuccessHandler())
//                .and()
//                .oauth2Client()
//                .and()
//                .oauth2Login()
//                .userInfoEndpoint()
//                .userAuthoritiesMapper(userAuthoritiesMapper());
    // REMOVED AS WE ARE NOT USING IN-MEMORY AUTHENTICATION ANYMORE
//                .httpBasic()
//                .and()
//                .authorizeRequests()
//                .antMatchers("/")
//                .permitAll()
//                .antMatchers("/**")
//                .hasRole("USER")
//                .anyRequest()
//                .fullyAuthenticated()
//                .and()
//                .logout()
//                .logoutSuccessUrl("/")
//                .invalidateHttpSession(true)
//                .deleteCookies("JSESSIONID");
//    }

    // customize granted authorities with custom mapper
    @Bean
    public GrantedAuthoritiesMapper userAuthoritiesMapper() {
        // map our authorities to spring security authorities, so we can do authorization logic here in client also
        // THIS WAY, AUTHENTICATED USER WILL HAVE ALL THE GROUPS FROM KEYCLOAK MAPPED TO ROLE_ AUTHORITIES IN SPRING CONTEXT
        return (authorities -> {
            final Set<GrantedAuthority> mappedAuthorities = new HashSet<>();

            authorities.forEach(authority -> {
                if (authority instanceof OidcUserAuthority) {
                    final OidcUserAuthority oidcUserAuthority = (OidcUserAuthority) authority;
                    final OidcIdToken oidcIdToken = oidcUserAuthority.getIdToken();
                    log.info("Username from ID token: {}", oidcIdToken.getPreferredUsername());
                    final OidcUserInfo oidcUserInfo = oidcUserAuthority.getUserInfo();

                    final List<SimpleGrantedAuthority> groupAuthorities = oidcUserInfo
                            .getClaimAsStringList(GROUPS_CLAIM).stream()
                            .map(group -> new SimpleGrantedAuthority(ROLE_PREFIX + group.toUpperCase()))
                            .collect(Collectors.toList());

                    mappedAuthorities.addAll(groupAuthorities);
                }
            });
            return mappedAuthorities;
        });
    }

    // REMOVED AS WE ARE NOT USING IN-MEMORY AUTHENTICATION ANYMORE
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception
//    {
//        auth.inMemoryAuthentication().withUser(userConfigData.getUsername())
//                // .password(userConfigData.getPassword()) // using BCrypted and then encrypted password with JCE
//                .password(passwordEncoder().encode(
//                        userConfigData.getPassword())) // using encrypted password with JCE, preferred way as it is
//                // working in all cases, and the above on is not
//                .roles(userConfigData.getRoles());
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder()
//    {
//        return new BCryptPasswordEncoder();
//    }
}

