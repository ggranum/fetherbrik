/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2015 Geoff M. Granum
 */

package com.fetherbrik.iam.domain.identity;


import com.fetherbrik.iam.domain.identity.tenant.TenantId;

public interface AuthToken {

  TenantId tenantId();

  String username();

  String password();
}
