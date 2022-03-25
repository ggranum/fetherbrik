/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2015 Geoff M. Granum
 */
package com.fetherbrik.iam.domain.identity.group;

import com.fetherbrik.iam.domain.access.role.RoleRepository;
import com.fetherbrik.iam.domain.identity.user.UserRepository;
import com.google.inject.Inject;

public class GroupMemberService {

  private final GroupRepository groupRepository;
  private final RoleRepository roleRepository;
  private final UserRepository userRepository;

  @Inject
  public GroupMemberService(
      GroupRepository groupRepository,
      RoleRepository roleRepository,
      UserRepository userRepository) {
    this.groupRepository = groupRepository;
    this.roleRepository = roleRepository;
    this.userRepository = userRepository;
  }
}
 
