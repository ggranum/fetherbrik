/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */

package com.fetherbrik.servlet.bootstrap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fetherbrik.core.base.Initializer;
import com.fetherbrik.core.base.VersionInfo;
import com.fetherbrik.servlet.initialization.InitializationChain;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

/**
 * @author ggranum
 */
public final class FetherBrikBootstrapModule extends AbstractModule {

  private final Bootstrap bootstrap;
  private final Env env;
  private final VersionInfo applicationVersion;
  private final InitializationChain initializationChain;

  public FetherBrikBootstrapModule(Env env,
                                   Bootstrap bootstrap,
                                   VersionInfo applicationVersion,
                                   InitializationChain initializationChain) {
    this.bootstrap = bootstrap;
    this.env = env;
    this.applicationVersion = applicationVersion;
    this.initializationChain = initializationChain;
  }

  @Override
  protected void configure() {
    bind(Env.class).toInstance(env);
    bind(Bootstrap.class).toInstance(bootstrap);
    bind(VersionInfo.class).toInstance(applicationVersion);
    bind(bootstrap.bootstrapConfigurationClass).toInstance(bootstrap.baseConfiguration());
    bind(ObjectMapper.class).toProvider(bootstrap.mapperProvider).asEagerSingleton();
    this.bindInit();
  }

  private void bindInit() {
    bind(Initializer.class).to(ApplicationInitializer.class).in(Scopes.SINGLETON);
    bind(InitializationChain.class).toInstance(this.initializationChain);
  }
}
