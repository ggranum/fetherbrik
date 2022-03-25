/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */

package com.fetherbrik.servlet.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fetherbrik.servlet.initialization.InitializationException;

/**
 * @author ggranum
 */
public interface StaticFromJson {

  static <T extends StaticFromJson> T fromJson(ObjectMapper mapper, String json) {
    String sig = "public static <T extends StaticFromJson> T fromJson(ObjectMapper mapper, String json);";
    throw new InitializationException("Implementing classes must implement a static method with signature '"+ sig + "' .");
  }
}
