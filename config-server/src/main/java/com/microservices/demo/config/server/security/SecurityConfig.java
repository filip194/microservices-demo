package com.microservices.demo.config.server.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain webSecurityCustomizer(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry -> authorizationManagerRequestMatcherRegistry
                        .requestMatchers(
                                new AntPathRequestMatcher("/actuator/**"),
                                new AntPathRequestMatcher("/encrypt/**"),
                                new AntPathRequestMatcher("/decrypt/**")
                        )
                        .permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }

//    DEPRECATED IN SPRING BOOT 3
//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer()
//    {
//        // disabling authentication for simplicity...
//        return (web) -> web.ignoring()
//                .antMatchers("/actuator/**")
//                .antMatchers("/encrypt/**")
//                .antMatchers("/decrypt/**");
//    }

//    DEPRECATED: extends WebSecurityConfigurerAdapter
//    @Override
//    public void configure(WebSecurity web) throws Exception
//    {
//        // disabling authentication for simplicity...
//        web.ignoring()
//                .antMatchers("/actuator/**")
//                .antMatchers("/encrypt/**")
//                .antMatchers("/decrypt/**");
//        super.configure(web);
//    }
}

