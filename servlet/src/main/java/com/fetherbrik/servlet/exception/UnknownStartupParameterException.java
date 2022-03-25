/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */

package com.fetherbrik.servlet.exception;

import com.fetherbrik.core.exception.ApplicationInitializationException;

/**
 * @author Geoff M. Granum
 */
public class UnknownStartupParameterException extends ApplicationInitializationException {

  private static final long serialVersionUID = 1L;

  public UnknownStartupParameterException(String arg) {
    super("Unknown startup parameter '%s'. ", arg);
  }
}
 
