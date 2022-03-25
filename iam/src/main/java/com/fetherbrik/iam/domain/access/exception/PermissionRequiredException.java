/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2015 Geoff M. Granum
 */
package com.fetherbrik.iam.domain.access.exception;

import com.fetherbrik.core.exception.service.ServiceException;
import com.fetherbrik.core.log.Level;
import com.fetherbrik.iam.domain.access.Permission;

/**
 * @author Geoff M. Granum
 */
public class PermissionRequiredException extends ServiceException {

  private static final long serialVersionUID = 1L;

  public PermissionRequiredException(Permission permission, String username) {
    super("Permission '%s' required to access requested resource: permission denied for user %s.",
        permission.name,
        username);
  }

  @Override
  public boolean shouldPrintStack() {
    return false;
  }

  @Override
  public Level getLogLevel() {
    return Level.INFO;
  }
}
 
