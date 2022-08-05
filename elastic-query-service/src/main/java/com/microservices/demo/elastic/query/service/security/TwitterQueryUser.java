package com.microservices.demo.elastic.query.service.security;

import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.microservices.demo.elastic.query.service.Constants;

import lombok.Builder;
import lombok.Getter;

// UserDetails is also from Spring core library

@Builder
@Getter
public class TwitterQueryUser implements UserDetails
{
    private String username;

    private Collection<? extends GrantedAuthority> authorities;

    private Map<String, PermissionType> permissions;

    // setter to pass parsed authorities from JWt into the UserDetails object
    public void setAuthorities(Collection<? extends GrantedAuthority> authorities)
    {
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities()
    {
        return authorities;
    }

    @Override
    public String getPassword()
    {
        return Constants.NA;
    }

    @Override
    public String getUsername()
    {
        return username;
    }

    @Override
    public boolean isAccountNonExpired()
    {
        return true;
    }

    @Override
    public boolean isAccountNonLocked()
    {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired()
    {
        return true;
    }

    @Override
    public boolean isEnabled()
    {
        return true;
    }
}

