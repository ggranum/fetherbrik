package com.fetherbrik.core.exception.bootstrap;

import com.fetherbrik.core.exception.FormattedException;

/**
 * @author ggranum
 */
public class IdGeneratorAlreadyInitializedException extends FormattedException {
  public IdGeneratorAlreadyInitializedException(String message) {
    super(message);
  }
}
