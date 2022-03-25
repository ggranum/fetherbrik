/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2015 Geoff M. Granum
 */
package com.fetherbrik.iam.domain.identity.user.event;


import com.fetherbrik.core.guava.DomainEvent;
import com.fetherbrik.iam.domain.identity.Enablement;
import com.fetherbrik.iam.domain.identity.tenant.TenantId;

/**
 * @author Geoff M. Granum
 */
public class UserEnablementChanged extends DomainEvent {

  private final TenantId tenantId;
  private final String username;
  private final Enablement enablement;

  public UserEnablementChanged(TenantId tenantId, String username, Enablement enablement) {
    super();
    this.tenantId = tenantId;
    this.username = username;
    this.enablement = enablement;
  }

  public TenantId tenantId() {
    return tenantId;
  }

  public String username() {
    return username;
  }

  public Enablement enablement() {
    return enablement;
  }
}
 
