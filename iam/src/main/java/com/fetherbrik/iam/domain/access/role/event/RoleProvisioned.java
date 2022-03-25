/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2015 Geoff M. Granum
 */
package com.fetherbrik.iam.domain.access.role.event;

import com.fetherbrik.core.guava.DomainEvent;
import com.fetherbrik.iam.domain.identity.tenant.TenantId;

public class RoleProvisioned extends DomainEvent {

  private static final long serialVersionUID = 1L;
  private final TenantId tenantId;
  private final String roleName;

  public RoleProvisioned(TenantId tenantId, String roleName) {
    super(System.currentTimeMillis(), (int) serialVersionUID);

    this.tenantId = tenantId;
    this.roleName = roleName;
  }

  public String roleName() {
    return this.roleName;
  }

  public TenantId tenantId() {
    return this.tenantId;
  }
}
 
