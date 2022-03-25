/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2015 Geoff M. Granum
 */
package com.fetherbrik.iam.domain.identity.user;

import com.fetherbrik.iam.domain.ApplicationRepository;
import com.fetherbrik.iam.domain.identity.tenant.TenantId;

import java.util.List;
import java.util.Optional;

/**
 * @author Geoff M. Granum
 */
public interface UserRepository extends ApplicationRepository {

  void add(TenantId tenantId, User user);

  void update(User user);

  void remove(TenantId tenantId, User user);

  Optional<User> get(TenantId tenantId, UserId userId);

  Optional<User> get(TenantId tenantId, String uniquelyNamedUser);

  List<User> get(TenantId tenantId);

  Optional<User> getUserForUniqueUsername(String username);

  Optional<User> withCredentials(TenantId tenantId, String uniqueUsername, String passwordPlaintext);
}
 
