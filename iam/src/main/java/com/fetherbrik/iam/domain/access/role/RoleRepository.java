/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2015 Geoff M. Granum
 */
package com.fetherbrik.iam.domain.access.role;

import com.fetherbrik.iam.domain.identity.user.User;
import com.fetherbrik.iam.domain.ApplicationRepository;
import com.fetherbrik.iam.domain.identity.tenant.TenantId;

import java.util.Optional;
import java.util.Set;

public interface RoleRepository extends ApplicationRepository {

  public void add(Role role);

  void update(Role role);

  public Set<Role> allRoles(TenantId tenantId);

  public Set<Role> rolesForUser(User user);

  public void remove(Role role);

  public Optional<Role> named(TenantId tenantId, String roleName);
}
