/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2015 Geoff M. Granum
 */

package com.fetherbrik.iam.domain;

import com.fetherbrik.core.base.VersionInfo;

/**
 * @author Geoff M. Granum
 */
public interface ApplicationRepository {

  String getName();

  void drop();

  void initialize();

  void migrate(VersionInfo versionInfo, VersionInfo codeVersion);
}
