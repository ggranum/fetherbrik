/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2015 Geoff M. Granum
 */
package com.fetherbrik.iam.domain.identity.group.event;

import com.fetherbrik.core.guava.DomainEvent;
import com.fetherbrik.iam.domain.identity.tenant.TenantId;

public class GroupAddedToGroup extends DomainEvent {

  private final TenantId tenantId;
  private final String groupName;
  private final String nestedGroupName;

  public GroupAddedToGroup(TenantId tenantId, String groupName, String nestedGroupName) {
    super();

    this.tenantId = tenantId;
    this.groupName = groupName;
    this.nestedGroupName = nestedGroupName;
  }

  public String groupName() {
    return this.groupName;
  }

  public String nestedGroupName() {
    return this.nestedGroupName;
  }

  public TenantId tenantId() {
    return this.tenantId;
  }
}
 
