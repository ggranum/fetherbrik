/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2015 Geoff M. Granum
 */
package com.fetherbrik.iam.domain.access;

import com.fetherbrik.core.persistence.id.Identified;
import com.fetherbrik.core.persistence.id.Identifier;

public class Permission implements Identified {

  public final PermissionId id;
  public final String name;

  public Permission(PermissionId id, String name) {
    this.id = id;
    this.name = name;
  }

  @Override
  public Identifier id() {
    return null;
  }
}
 
