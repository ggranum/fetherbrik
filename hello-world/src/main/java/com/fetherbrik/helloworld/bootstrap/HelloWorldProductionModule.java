/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */

package com.fetherbrik.helloworld.bootstrap;

import com.fetherbrik.core.log.Log;
import com.fetherbrik.servlet.bootstrap.Env;
import com.fetherbrik.servlet.util.GuiceAllowAllCorsFilter;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

/**
 * @author ggranum
 */
public class HelloWorldProductionModule extends AbstractModule {

  private final Env env;

  public HelloWorldProductionModule(Env env) {
    this.env = env;
  }

  @Override
  protected void configure() {
    Log.info(getClass(), "Configuring module in environment %s", env.key);
    bind(GuiceAllowAllCorsFilter.class).in(Scopes.SINGLETON);
  }
}
