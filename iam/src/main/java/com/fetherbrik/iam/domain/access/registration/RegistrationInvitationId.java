/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2015 Geoff M. Granum
 */
package com.fetherbrik.iam.domain.access.registration;

import com.fetherbrik.core.persistence.id.TypedId;

import javax.annotation.concurrent.Immutable;
import java.math.BigInteger;

/**
 * @author Geoff M. Granum
 */
@Immutable
public final class RegistrationInvitationId extends TypedId<RegistrationInvitation> {

  public RegistrationInvitationId(BigInteger value) {
    super(value);
  }

}
 
