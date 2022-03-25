/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */

package com.fetherbrik.helloworld;

import com.fetherbrik.helloworld.resource.HelloWorldResource;
import com.fetherbrik.helloworld.resource.HelloWorldResourceImpl;
import com.fetherbrik.servlet.GuiceResteasyServletModule;

/**
 * @author ggranum
 */
public class HelloWorldServletModule extends GuiceResteasyServletModule {

  @Override
  public void bindResources() {
    bind(HelloWorldResource.class).to(HelloWorldResourceImpl.class);
  }
}
