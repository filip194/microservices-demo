package com.microservices.demo.elastic.query.service.dataaccess.entity;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Entity
@Data
public class UserPermission
{
    @NotNull
    @Id
    private UUID id;

    @NotNull
    private String username;

    @NotNull
    private String documentId;

    @NotNull
    private String permissionType;
}

