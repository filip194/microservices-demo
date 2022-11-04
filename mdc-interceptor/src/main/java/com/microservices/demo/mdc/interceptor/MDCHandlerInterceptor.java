package com.microservices.demo.mdc.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.MDC;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.IdGenerator;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import com.microservices.demo.mdc.Constants;

@Component
public class MDCHandlerInterceptor implements HandlerInterceptor
{
    private final IdGenerator idGenerator;

    public MDCHandlerInterceptor(IdGenerator idGenerator)
    {
        this.idGenerator = idGenerator;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
    {
        final String correlationId = request.getHeader(Constants.CORRELATION_ID_HEADER);
        if (StringUtils.hasLength(correlationId))
        {
            MDC.put(Constants.CORRELATION_ID_KEY, correlationId);
        }
        else
        {
            MDC.put(Constants.CORRELATION_ID_KEY, getNewCorrelationId());
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
            @Nullable Exception ex) throws Exception
    {
        MDC.remove(Constants.CORRELATION_ID_KEY);
    }

    private String getNewCorrelationId()
    {
        return idGenerator.generateId().toString();
    }
}

