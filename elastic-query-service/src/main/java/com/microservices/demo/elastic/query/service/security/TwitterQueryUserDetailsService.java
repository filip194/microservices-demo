package com.microservices.demo.elastic.query.service.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// UserDetailsService is also from Spring core library

@Service
public class TwitterQueryUserDetailsService implements UserDetailsService
{
    // this method will be called during authentication of the client, and we will implement the logic to load
    // the username by adding authorities and permissions we want to include
    // NOTE: that we will already load the authorities from JWT, and here we will only fetch object with the permissions
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        return TwitterQueryUser.builder()
                .username(username)
                .build();
    }
}

