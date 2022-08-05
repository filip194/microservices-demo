package com.microservices.demo.elastic.query.service.security;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.microservices.demo.elastic.query.service.common.model.ElasticQueryServiceRequestModel;
import com.microservices.demo.elastic.query.service.common.model.ElasticQueryServiceResponseModel;

// Implementing custom permission strategy
@Component
public class QueryServicePermissionEvaluator implements PermissionEvaluator
{
    private static final String SUPER_USER_ROLE = "APP_SUPER_USER_ROLE";

    // this is an alternative to calling hasRole method in preAuthorize annotation
    // HttpServletRequest is an instance of SecurityContextHolderAwareRequestWrapper
    private final HttpServletRequest httpServletRequest;

    public QueryServicePermissionEvaluator(HttpServletRequest httpServletRequest)
    {
        this.httpServletRequest = httpServletRequest;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission)
    {
        if (isSuperUser())
        {
            return true;
        }
        // if the targetDomainObject is a request object then we have to evaluate it in preAuthorize
        if (targetDomainObject instanceof ElasticQueryServiceRequestModel)
        {
            return preAuthorize(authentication, ((ElasticQueryServiceRequestModel) targetDomainObject).getId(),
                    permission);
        }
        // if the targetDomainObject is a response entity then we are returning from controller, and we have to
        // evaluate it in postAuthorize
        else if (targetDomainObject instanceof ResponseEntity || targetDomainObject == null)
        {
            // if the targetDomainObject is null that means there are no documents found, and we return true (has
            // permission for no documents)
            if (targetDomainObject == null)
            {
                return true;
            }
            final List<ElasticQueryServiceResponseModel> responseBody =
                    ((ResponseEntity<List<ElasticQueryServiceResponseModel>>) targetDomainObject).getBody();
            Objects.requireNonNull(responseBody);
            return postAuthorize(authentication, permission, responseBody);
        }

        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType,
            Object permission)
    {
        if (isSuperUser())
        {
            return true;
        }
        if (targetId == null)
        {
            return false;
        }
        return preAuthorize(authentication, (String) targetId, permission);
    }

    private boolean preAuthorize(Authentication authentication, String id, Object permission)
    {
        final TwitterQueryUser twitterQueryUser = (TwitterQueryUser) authentication.getPrincipal();
        final PermissionType userPermission = twitterQueryUser.getPermissions().get(id);
        return hasPermission((String) permission, userPermission);
    }

    private boolean postAuthorize(Authentication authentication, Object permission,
            List<ElasticQueryServiceResponseModel> responseBody)
    {
        final TwitterQueryUser twitterQueryUser = (TwitterQueryUser) authentication.getPrincipal();

        for (ElasticQueryServiceResponseModel responseModel : responseBody)
        {
            final PermissionType userPermission = twitterQueryUser.getPermissions().get(responseModel.getId());
            if (!hasPermission((String) permission, userPermission))
            {
                return false;
            }
        }
        return true;
    }

    private boolean hasPermission(String requiredPermission, PermissionType userPermission)
    {
        return userPermission != null && requiredPermission.equals(userPermission.getType());
    }

    private boolean isSuperUser()
    {
        return httpServletRequest.isUserInRole(SUPER_USER_ROLE);
    }
}

