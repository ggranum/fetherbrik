package com.fetherbrik.core.exception.bootstrap;

import com.fetherbrik.core.exception.FormattedException;

/**
 * @author ggranum
 */
public class IdGeneratorInitializationException extends FormattedException {
  public IdGeneratorInitializationException(Throwable cause, String msgFormat, Object... args) {
    super(cause, msgFormat, args);
  }
}
