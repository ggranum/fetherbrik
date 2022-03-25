/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */
package com.fetherbrik.servlet.initialization;

import com.fetherbrik.core.exception.FormattedException;

public class InitializationException extends FormattedException {

  private static final long serialVersionUID = 1L;

  public InitializationException(String msgFormat, Object... args) {
    super(msgFormat, args);
  }

  public InitializationException(Throwable cause, String msgFormat, Object... args) {
    super(cause, msgFormat, args);
  }

  public InitializationException(Throwable cause) {
    super(cause);
  }
}
 
