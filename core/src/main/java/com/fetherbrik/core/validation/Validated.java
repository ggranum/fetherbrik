/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2015 Geoff M. Granum
 */

package com.fetherbrik.core.validation;

import com.fetherbrik.core.log.Log;

/**
 * @author Geoff M. Granum
 * @todo ggranum:  Smarter validation exception handling/bundling.
 */
public interface Validated {

  default void checkValid() {
    Log.warn(getClass(), "Not implemented");
  }
}
