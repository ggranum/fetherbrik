/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2015 Geoff M. Granum
 */

package com.fetherbrik.iam.domain.access;

import com.fetherbrik.core.persistence.id.TypedId;

import javax.annotation.concurrent.Immutable;
import java.math.BigInteger;

@Immutable
public final class PermissionId extends TypedId<Permission> {

  public PermissionId(BigInteger value) {
    super(value);
  }
}
