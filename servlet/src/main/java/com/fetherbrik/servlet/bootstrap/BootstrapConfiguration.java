/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */

package com.fetherbrik.servlet.bootstrap;

import com.fetherbrik.servlet.util.StaticFromJson;

/**
 * @author ggranum
 */
public interface BootstrapConfiguration extends StaticFromJson {

  String hostName();

  int httpPort();

  int httpsPort();

  String jettyHome();

}
