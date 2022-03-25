/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */

package com.fetherbrik.core.exception.service;

/**
 * @author Geoff M. Granum
 */
public interface HttpResponseAware {

  int statusCode();

  String getHttpResponseMessage();
}
