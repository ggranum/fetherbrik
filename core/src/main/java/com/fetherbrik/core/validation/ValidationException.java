/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2015 Geoff M. Granum
 */

package com.fetherbrik.core.validation;

import com.fetherbrik.core.exception.FormattedException;

import java.util.Set;

/**
 * @author Geoff M. Granum
 * @todo ggranum:  Smarter validation exception handling/bundling.
 */
public class ValidationException extends FormattedException {

  private static final long serialVersionUID = 1L;
  transient public final Validated builder;

  public ValidationException(Validated builder, Set<Validated> violations) {
    super("One or more failures while validating %s: %s",
        builder.getClass().getSimpleName(),
        createMessage(violations));

    this.builder = builder;
  }

  private static String createMessage(Set<Validated> violations) {
    return "Not implemented";
  }
}
 
