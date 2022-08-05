package com.microservices.demo.elastic.query.service.business;

import java.util.List;
import java.util.Optional;

import com.microservices.demo.elastic.query.service.dataaccess.entity.UserPermission;

public interface QueryUserService
{
    Optional<List<UserPermission>> findAllPermissionsByUsername(String username);
}
