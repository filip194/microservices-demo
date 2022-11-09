package com.microservices.demo.config.server.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;

@Configuration
public class SecurityConfig
{
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer()
    {
        // disabling authentication for simplicity...
        return (web) -> web.ignoring()
                .antMatchers("/actuator/**")
                .antMatchers("/encrypt/**")
                .antMatchers("/decrypt/**");
    }

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

