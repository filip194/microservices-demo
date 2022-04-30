package com.microservices.demo.elastic.query.web.client.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.microservices.demo.config.UserConfigData;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter
{
    private final UserConfigData userConfigData;

    public WebSecurityConfig(UserConfigData userConfigData)
    {
        this.userConfigData = userConfigData;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception
    {
        http
                .httpBasic()
                .and()
                .authorizeRequests()
                .antMatchers("/").permitAll()
                .antMatchers("/**").hasRole("USER")
                .anyRequest()
                .fullyAuthenticated()
                .and()
                .logout()
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception
    {
        auth
                .inMemoryAuthentication()
                .withUser(userConfigData.getUsername())
                // .password(userConfigData.getPassword()) // using BCrypted and then encrypted password with JCE
                .password(passwordEncoder().encode(userConfigData.getPassword())) // using encrypted password with JCE, preferred way as it is working in all cases, and the above on is not
                .roles(userConfigData.getRoles());
    }

    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }
}

