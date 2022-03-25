/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2015 Geoff M. Granum
 */
package com.fetherbrik.iam.domain.identity.tenant;

import com.fetherbrik.iam.domain.ApplicationRepository;

import java.util.Optional;

/**
 * @author Geoff M. Granum
 */
public interface TenantRepository extends ApplicationRepository {

  void add(Tenant tenant);

  void put(Tenant tenant);

  Optional<Tenant> get(TenantId tenantId);

  Optional<Tenant> get(String tenantName);

  TenantId tenantIdForTenantNamed(String tenantName);

  void remove(Tenant tenant);

  Optional<Tenant> getSystemTenant();
}
