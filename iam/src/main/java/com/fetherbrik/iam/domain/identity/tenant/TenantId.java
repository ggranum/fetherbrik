/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2015 Geoff M. Granum
 */

package com.fetherbrik.iam.domain.identity.tenant;

import com.fetherbrik.core.persistence.id.TypedId;

import javax.annotation.concurrent.Immutable;
import java.math.BigInteger;

@Immutable
public final class TenantId extends TypedId<Tenant> {

  public TenantId(BigInteger value) {
    super(value);
  }

}
