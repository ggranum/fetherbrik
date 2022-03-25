package com.fetherbrik.iam.domain.identity.tenant;

import com.fetherbrik.core.base.Verify;
import com.fetherbrik.core.persistence.id.IdGenerator;
import com.fetherbrik.iam.domain.access.IamPermission;
import com.fetherbrik.iam.domain.access.RevocablePermission;
import com.fetherbrik.iam.domain.access.registration.RegistrationInvitation;
import com.fetherbrik.iam.domain.access.role.Role;
import com.fetherbrik.iam.domain.access.role.event.RoleProvisioned;
import com.fetherbrik.iam.domain.identity.Enablement;
import com.fetherbrik.iam.domain.identity.group.Group;
import com.fetherbrik.iam.domain.identity.user.User;
import com.fetherbrik.iam.domain.identity.group.event.GroupProvisioned;
import com.fetherbrik.iam.domain.identity.tenant.event.TenantActivated;
import com.fetherbrik.iam.domain.identity.tenant.event.TenantDeactivated;
import com.google.common.eventbus.EventBus;

import javax.inject.Inject;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author ggranum
 */
public class TenantService {

  private final IdGenerator idGen;
  private final EventBus domainBus;

  @Inject
  private TenantService(IdGenerator idGen, EventBus domainBus) {
    this.idGen = idGen;
    this.domainBus = domainBus;
  }

  public Tenant activate(Tenant target) {
    Tenant tenant = target;
    if (!target.active) {
      tenant = new Tenant.Builder().copyOf(target).active(true).build();
      domainBus.post(new TenantActivated(tenant.id()));
    }
    return tenant;
  }

  public Tenant deactivate(Tenant target) {
    Tenant tenant = target;
    if (target.active) {
      tenant = new Tenant.Builder().copyOf(target).active(false).build();
      domainBus.post(new TenantDeactivated(target.id()));
    }
    return tenant;
  }

  public Group provisionGroup(Tenant target, String name, String description) {
    Verify.isTrue(target.active, IllegalStateException.class, "Tenant is not active.");

    Group group = new Group.Builder()
        .tenantId(target.id())
        .name(name)
        .description(description)
        .create()
        .build();

    domainBus.post(new GroupProvisioned(target.id(), name));

    return group;
  }

  /**
   * Provision a non-nestable Role.
   */
  public Role provisionRole(Tenant target, String aName, String aDescription, Set<IamPermission> permissions) {
    return this.provisionRole(target, aName, aDescription, permissions, false);
  }

  public Role provisionRole(Tenant target, String aName, String description, Set<IamPermission> permissions, boolean supportsNesting) {
    Verify.isTrue(target.active, IllegalStateException.class, "Tenant is not active.");
    Set<RevocablePermission> revocablePerms = IamPermission.asRevocable(permissions, false);
    Role role = new Role.Builder()
        .tenantId(target.id())
        .name(aName)
        .description(description)
        .supportsNesting(supportsNesting)
        .permissions(revocablePerms)
        .create()
        .build();

    domainBus.post(new RoleProvisioned(target.id(), aName));
    return role;
  }

  public User registerUser(
      Tenant target,
      String invitationIdentifier,
      String username,
      String passwordClearText,
      Enablement enablement) {
    // this used to require a Person object. Do we want person to exist before creating a user? Hmm.

    Verify.isTrue(target.active, IllegalStateException.class, "Tenant is not active.");

    User user;

    if (target.isRegistrationAvailableThrough(invitationIdentifier)) {
      user = new User.Builder()
          .tenantId(target.id)
          .username(username)
          .passwordClearText(passwordClearText)
          .enablement(enablement)
          .userPermissions(Collections.<RevocablePermission>emptySet())
          .create(idGen);
    } else {
      throw new IllegalStateException("Registration not available for invitation '" + invitationIdentifier + "'");
    }
    return user;
  }

  public Tenant withdrawInvitation(Tenant target, String invitationId) {
    RegistrationInvitation invitation = target.invitation(invitationId);
    Tenant result = target;
    if (invitation != null) {
      Set<RegistrationInvitation> set = new HashSet<>(target.registrationInvitations);
      set.remove(invitation);
      result = target.copy().registrationInvitations(set).build();
    }
    return result;
  }
}
