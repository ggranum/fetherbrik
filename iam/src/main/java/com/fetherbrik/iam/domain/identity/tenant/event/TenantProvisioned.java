/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2015 Geoff M. Granum
 */
package com.fetherbrik.iam.domain.identity.tenant.event;

import com.fetherbrik.core.guava.DomainEvent;
import com.fetherbrik.iam.domain.identity.tenant.TenantId;

/**
 * @author Geoff M. Granum
 */
public class TenantProvisioned extends DomainEvent {

  private final TenantId tenantId;

  public TenantProvisioned(TenantId tenantId) {
    super();
    this.tenantId = tenantId;
  }


  public TenantId tenantId() {
    return this.tenantId;
  }
}
 
