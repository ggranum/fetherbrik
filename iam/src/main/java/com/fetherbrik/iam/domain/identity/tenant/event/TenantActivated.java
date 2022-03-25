/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2015 Geoff M. Granum
 */
package com.fetherbrik.iam.domain.identity.tenant.event;


import com.fetherbrik.core.guava.DomainEvent;
import com.fetherbrik.iam.domain.identity.tenant.TenantId;

public class TenantActivated extends DomainEvent {

  private final TenantId tenantId;

  public TenantActivated(TenantId tenantId) {
    super();
    this.tenantId = tenantId;
  }

  public TenantId tenantId() {
    return this.tenantId;
  }
}
 
