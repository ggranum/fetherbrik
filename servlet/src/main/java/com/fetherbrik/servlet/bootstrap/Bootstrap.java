/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */

package com.fetherbrik.servlet.bootstrap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fetherbrik.core.base.VersionInfo;
import com.fetherbrik.core.exception.FatalException;
import com.fetherbrik.core.json.DefaultObjectMapperProvider;
import com.fetherbrik.core.log.Log;
import com.fetherbrik.servlet.FetherBrikApplication;
import com.fetherbrik.servlet.FetherBrikServletContextListener;
import com.fetherbrik.servlet.exception.InvalidCommandLineException;
import com.fetherbrik.servlet.initialization.InitializationChain;
import com.fetherbrik.servlet.initialization.InitializationException;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;
import com.google.inject.servlet.ServletModule;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import javax.inject.Provider;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Determine the current environment and load bootstrap configuration properties.
 * <p>
 * Sets the log factory for apache commons and jboss logging to slf4j.
 * Enables verbose javax.net.debug logging by default. This is so new implementors see the ssl certs being loaded. Use the provided static method
 * to disable this.
 * <p>
 * Load Priority: defaults < configFiles < environment < commandLine
 *
 * @author ggranum
 */
public final class Bootstrap {

  /**
   * Capture the original values in case a consumer wants to reset them.
   */
  public static final @Nullable String JAVAX_NET_DEBUG_ORIGINAL = System.getProperty("javax.net.debug");
  public static final @Nullable String ORG_JBOSS_LOGGING_PROVIDER_ORIGINAL = System.getProperty(
      "org.jboss.logging.provider");
  public static final @Nullable String ORG_APACHE_COMMONS_LOGGING_LOGFACTORY_ORIGINAL = System.getProperty(
      "org.apache.commons.logging.LogFactory");
  public static final String ENV = "env";

  static {
    /*
     * This class is the second to be loaded, but has to be loaded during that classes main method, so this is a good place to init these system properties.
     */
    System.setProperty("javax.net.debug", "all");
    System.setProperty("org.jboss.logging.provider", "slf4j");
    System.setProperty("org.apache.commons.logging.LogFactory", "org.apache.commons.logging.impl.SLF4JLogFactory");
  }

  final Class<? extends BootstrapConfiguration> bootstrapConfigurationClass;
  final Provider<ObjectMapper> mapperProvider;
  private final String appName;
  private final String basePath;
  private final String bootstrapConfigFileName;
  private final String bootstrapConfigFileExtension;
  private final Map<String, String> commandLineArgs;
  private final String environmentPrefix;
  private final Optional<ModuleProvider> moduleProvider;
  private final ServletModule servletModule;
  private final Set<Module> modules;
  private final Stage injectionStage;
  private final AtomicBoolean baseConfigurationCreated = new AtomicBoolean(false);
  private final AtomicBoolean injectorCreated = new AtomicBoolean(false);
  private EnvSources envSources;
  private Injector injector;
  private BootstrapConfiguration baseConfiguration;

  private Bootstrap(Builder builder) {
    appName = builder.appName;
    bootstrapConfigurationClass = builder.bootstrapConfigurationClass;
    bootstrapConfigFileName = builder.bootstrapConfigFileName;
    bootstrapConfigFileExtension = builder.bootstrapConfigFileExtension;
    environmentPrefix = builder.environmentPrefix;
    basePath = builder.basePath;
    commandLineArgs = getConfigurationParamsFromCommandLineArgs(builder.commandLineArgs);
    modules = builder.modules;
    servletModule = builder.servletModule;
    injectionStage = builder.injectionStage;
    mapperProvider = builder.mapperProvider;
    moduleProvider = Optional.ofNullable(builder.moduleProvider);
  }

  public static void enableVerboseNetworkAndCertificateLogging() {
    System.setProperty("javax.net.debug", "all");
  }

  public static void disableVerboseNetworkAndCertificateLogging() {
    if (JAVAX_NET_DEBUG_ORIGINAL == null) {
      System.clearProperty("javax.net.debug");
    } else {
      System.setProperty("javax.net.debug", JAVAX_NET_DEBUG_ORIGINAL);
    }
  }

  private void init() {
    this.envSources = EnvReader.determineEnvironment(commandLineArgs, environmentPrefix, basePath);
    this.baseConfiguration = buildConfiguration(envSources.selectedEnv, bootstrapConfigurationClass);
    ImmutableSet.Builder<Module> modules = initModules();
    injector = createInjector(modules.build());

  }

  private ImmutableSet.Builder<Module> initModules() {
    VersionInfo applicationVersion = determineCodeVersion(basePath);
    FetherBrikBootstrapModule
        fetherBrikBootstrapModule = new FetherBrikBootstrapModule(
        envSources.selectedEnv,
        this, applicationVersion,
        new InitializationChain());

    ImmutableSet.Builder<Module> modules = ImmutableSet.<Module>builder().add(fetherBrikBootstrapModule)
        .add(servletModule)
        .addAll(this.modules);
    if (moduleProvider.isPresent()) {
      ModuleProvider provider = this.moduleProvider.get();
      Set<Module> providedModules = provider.get(envSources.selectedEnv, baseConfiguration);
      modules.addAll(providedModules);
    }
    return modules;
  }

  /**
   * @todo ggranum: Allow file and/or raw version to be specified in builder.
   * @todo ggranum: Convert app to use a Semver class, preferably someone else's.
   */
  private VersionInfo determineCodeVersion(String basePath) {
    VersionInfo versionInfo;
    File versionFile = new File(basePath, "config/version.number");
    if (!versionFile.exists()) {
      Log.warn(getClass(),
          "No 'version.number' file found at path %s. Providing a valid version for your application is highly recommended",
          versionFile.getAbsolutePath());
      versionInfo = new VersionInfo.Builder().fromVersionString("0.0.0-MISSING").build();
    } else {
      try {
        InputStream stream = new FileInputStream(versionFile);
        String versionString = IOUtils.toString(stream, StandardCharsets.UTF_8);
        versionInfo = new VersionInfo.Builder().fromVersionString(versionString).build();
      } catch (IOException e) {
        Log.warn(getClass(),
            "Could not read 'version.number' file found at path %s. Providing a valid version for your application is highly recommended",
            versionFile.getAbsolutePath());
        versionInfo = new VersionInfo.Builder().fromVersionString("0.0.0-MISSING").build();
      }
    }
    return versionInfo;
  }

  private Injector createInjector(Set<Module> modules) {

    return Guice.createInjector(injectionStage, modules);
  }

  private Injector injector() {
    Log.info(getClass(), "Injector requested.");
    // We really want to discourage access to the injector - implementors can inject it into their classes, after all. Since we only use it once...
    if (injector == null) {
      throw new FatalException("Request for injector before initialization: Injector is still null!");
    }
    return injector;
  }

  public <T extends BootstrapConfiguration> T baseConfiguration() {
    //noinspection unchecked
    return (T) this.baseConfiguration;
  }

  private <T extends BootstrapConfiguration> T buildConfiguration(Env env, Class<T> configurationClass) {
    EnvOrFileSourcedConfigurationReader<T> reader =
        new EnvOrFileSourcedConfigurationReader<>(bootstrapConfigFileName,
            bootstrapConfigFileExtension,
            env,
            new File(basePath, "config/").getPath(),
            environmentPrefix,
            configurationClass,
            mapperProvider.get());

    Map<String, String> providedConfig = new HashMap<>();
    Map<String, String> fileMap = reader.readFromOptionalFile();
    Map<String, String> envMap = reader.readFromEnvironment();

    providedConfig.put("env", '"' + env.key + '"');
    if (fileMap.containsKey(ENV)) {
      throw new InitializationException(
          "Attempting to set env in configuration file will cause fantastic confusion. Use the env.name and env.local.name "
              + "files.");
    }
    providedConfig.putAll(fileMap);
    providedConfig.putAll(envMap);
    providedConfig.putAll(commandLineArgs);

    return reader.from(providedConfig);
  }

  /**
   * Maybe redo this with https://github.com/ggranum/cli-annotations
   */
  protected Map<String, String> getConfigurationParamsFromCommandLineArgs(String[] args) {
    Map<String, String> argsConfig = Maps.newHashMap();
    for (int i = 0; i < args.length; i++) {
      String arg = args[i];
      if (arg.startsWith("--")) {
        arg = arg.substring(2);
        String value = StringUtils.strip(args[++i], "\"' ");
        if (StringUtils.isNotEmpty(value)) {
          if (value.startsWith("--")) {
            throw new InvalidCommandLineException("Argument is missing a value. Argument: '%s'. Following token: %s",
                arg, value);
          }
          argsConfig.put(arg, value);
        }
      }
    }
    return argsConfig;
  }

  public void resetForcedSystemProperties() {
    System.setProperty("javax.net.debug", "all");
    System.setProperty("org.jboss.logging.provider", "slf4j");
    System.setProperty("org.apache.commons.logging.LogFactory", "org.apache.commons.logging.impl.SLF4JLogFactory");
  }

  /**
   * Start the application and block until termination.
   *
   * @todo ggranum: Remove the return or return a promise.
   */
  public <T extends FetherBrikApplication> T start(Class<T> applicationClass) throws Exception {
    T app = this.injector().getInstance(applicationClass);
    app.start();
    return app;
  }

  public FetherBrikServletContextListener createContextListener(BootstrapConfiguration baseConfig) {
    if (injector == null) {
      throw new InitializationException("Application must be initialized using #start().");
    }
    return new FetherBrikServletContextListener(baseConfig.httpPort(), baseConfig.httpsPort(), injector);
  }

  public static final class Builder {

    private String appName;
    private String bootstrapConfigFileName;
    private String bootstrapConfigFileExtension = "json5";
    private String basePath;
    private String environmentPrefix;
    private String[] commandLineArgs = {};
    private String runtimeConfigurationFileName;
    private Stage injectionStage = Stage.PRODUCTION;
    private ModuleProvider moduleProvider;
    private Provider<ObjectMapper> mapperProvider = new DefaultObjectMapperProvider();
    private ServletModule servletModule;
    private Set<Module> modules = Collections.emptySet();
    private Class<? extends BootstrapConfiguration> bootstrapConfigurationClass;

    public Builder() {
    }

    public Builder appName(String appName) {
      this.appName = appName;
      return this;
    }

    public Builder bootstrapConfigFileName(String bootstrapConfigFileName) {
      this.bootstrapConfigFileName = bootstrapConfigFileName;
      return this;
    }

    public Builder bootstrapConfigFileExtension(String bootstrapConfigFileExtension) {
      this.bootstrapConfigFileExtension = bootstrapConfigFileExtension;
      return this;
    }

    public Builder environmentPrefix(String environmentPrefix) {
      this.environmentPrefix = environmentPrefix;
      return this;
    }

    public Builder basePath(String basePath) {
      this.basePath = basePath;
      return this;
    }

    public Builder commandLineArgs(String[] commandLineArgs) {
      this.commandLineArgs = commandLineArgs;
      return this;
    }

    public Builder injectionStage(Stage injectionStage) {
      this.injectionStage = injectionStage;
      return this;
    }

    public Builder servletModule(ServletModule servletModule) {
      this.servletModule = servletModule;
      return this;
    }

    public Builder modules(Set<Module> modules) {
      this.modules = modules;
      return this;
    }

    public Builder moduleProvider(ModuleProvider moduleProvider) {
      this.moduleProvider = moduleProvider;
      return this;
    }

    public Builder bootstrapConfigurationClass(Class<? extends BootstrapConfiguration> configurationClass) {
      this.bootstrapConfigurationClass = configurationClass;
      return this;
    }

    public Bootstrap build() {
      if (StringUtils.isEmpty(this.bootstrapConfigFileName)) {
        this.bootstrapConfigFileName = environmentPrefix.toLowerCase().replace(' ', '_');
      }
      if (StringUtils.isEmpty(runtimeConfigurationFileName)) {
        runtimeConfigurationFileName = environmentPrefix.toLowerCase();
      }
      Bootstrap bootstrap = new Bootstrap(this);
      bootstrap.init();
      return bootstrap;
    }

    public Builder objectMapperProvider(Provider<ObjectMapper> mapperProvider) {
      this.mapperProvider = mapperProvider;
      return this;
    }
  }
}
