/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2015 Geoff M. Granum
 */
package com.fetherbrik.iam.domain.access.role.event;

import com.fetherbrik.core.guava.DomainEvent;
import com.fetherbrik.iam.domain.identity.tenant.TenantId;

/**
 * @author Geoff M. Granum
 */
public class UserUnassignedFromRole extends DomainEvent {

  private static final long serialVersionUID = 1L;
  private final TenantId tenantId;
  private final String roleName;
  private final String userName;

  public UserUnassignedFromRole(TenantId tenantId, String roleName, String userName) {
    super(System.currentTimeMillis(), (int) serialVersionUID);

    this.tenantId = tenantId;
    this.roleName = roleName;
    this.userName = userName;
  }

  public String groupName() {
    return this.userName;
  }

  public String roleName() {
    return this.roleName;
  }

  public TenantId tenantId() {
    return this.tenantId;
  }
}
 
