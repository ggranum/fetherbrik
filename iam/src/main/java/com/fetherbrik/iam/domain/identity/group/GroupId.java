/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2015 Geoff M. Granum
 */
package com.fetherbrik.iam.domain.identity.group;

import com.fetherbrik.core.persistence.id.TypedId;

import java.math.BigInteger;

/**
 * @author Geoff M. Granum
 */
public final class GroupId extends TypedId<Group> implements MemberOfGroupId {


  public GroupId(BigInteger value) {
    super(value);
  }

}
 
