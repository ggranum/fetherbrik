/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2015 Geoff M. Granum
 */
package com.fetherbrik.iam.domain.access.exception;

import com.fetherbrik.core.exception.service.ServiceException;
import com.fetherbrik.core.http.HttpStatus;
import com.fetherbrik.core.log.Level;
import com.fetherbrik.core.log.Log;
import com.fetherbrik.iam.domain.access.Subject;

/**
 * @author Geoff M. Granum
 */
public class PermissionDeniedException extends ServiceException {

  public PermissionDeniedException(String resource, Subject subject) {
    super(HttpStatus.FORBIDDEN.code, "Permission Denied. Username: %s.", subject.user.username);
    Log.warn(getClass(), "User attempted to access forbidden resource. Subject: %s. Resource", subject, resource);
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
 
