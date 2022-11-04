package com.microservices.demo.mdc.config;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.microservices.demo.mdc.interceptor.MDCHandlerInterceptor;

@Component
public class WebMvcConfig implements WebMvcConfigurer
{
    private final MDCHandlerInterceptor mdcHandlerInterceptor;

    public WebMvcConfig(MDCHandlerInterceptor mdcHandlerInterceptor)
    {
        this.mdcHandlerInterceptor = mdcHandlerInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry)
    {
        registry.addInterceptor(mdcHandlerInterceptor);
    }
}

