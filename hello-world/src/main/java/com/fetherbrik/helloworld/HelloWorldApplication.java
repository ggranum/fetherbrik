/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */

package com.fetherbrik.helloworld;

import com.fetherbrik.core.json.DefaultObjectMapperProvider;
import com.fetherbrik.core.log.Log;
import com.fetherbrik.helloworld.bootstrap.HelloWorldBootstrapConfiguration;
import com.fetherbrik.helloworld.bootstrap.HelloWorldSpecialModuleProvider;
import com.fetherbrik.servlet.FetherBrikApplication;
import com.fetherbrik.servlet.bootstrap.Bootstrap;
import com.fetherbrik.servlet.bootstrap.BootstrapConfiguration;
import com.fetherbrik.servlet.event.ServerLifeCycleEvent;
import com.fetherbrik.servlet.event.ServerReadyEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Stage;

/**
 * This is the main application class. That is to say, the entrance to everything that is executed first, from the
 * command line. For example:
 * <p/>
 * java -jar application.jar --http_port 9280 --log_dir ./logs --db_url localhost
 * <p>
 * <p>
 * CORS does not work on localhost directly. CORS can work on 127.0.0.1, but e.g. secure WebSockets do not.
 * You will need to add the host you specified in your dev configuration to your etc/hosts file.
 * Localhost cannot be really be configured to work in the same way as a regular hostname.
 * > sudo bash -c "echo ::1    $yourDevHost >> /etc/hosts"
 *
 * @author Geoff M. Granum
 */
public final class HelloWorldApplication extends FetherBrikApplication {

  private Bootstrap bootstrap;

  @Inject
  private HelloWorldApplication(EventBus appBus, Bootstrap bootstrap, HelloWorldBootstrapConfiguration configuration) {
    super(appBus, bootstrap, configuration);
    this.bootstrap = bootstrap;
    appBus.register(this);
  }

  public static void main(String[] commandLineArgs) throws Exception {
    Bootstrap.disableVerboseNetworkAndCertificateLogging();
    Bootstrap bootstrap = new Bootstrap.Builder()
        .appName("Hello World")
        .basePath("./")
        // If bootstrapConfigFileName not specified, uses lower-cased value of 'environmentPrefix' followed by _bootstrap.
        // Config file is optional if all filed value for configurationClass are specified in env variables.
        .bootstrapConfigFileName("hello_world_bootstrap")
        .bootstrapConfigurationClass(HelloWorldBootstrapConfiguration.class)
        .commandLineArgs(commandLineArgs)
        .environmentPrefix("HELLO_WORLD")
        .injectionStage(Stage.PRODUCTION)
        .moduleProvider(new HelloWorldSpecialModuleProvider())
        .objectMapperProvider(new DefaultObjectMapperProvider())
        .servletModule(new HelloWorldServletModule())
        .build();

    HelloWorldApplication application = bootstrap.start(HelloWorldApplication.class);
  }

  @Subscribe
  private void listen(ServerLifeCycleEvent event) {
    Log.info(getClass(), event.getClass().getCanonicalName());
  }

  @Subscribe
  private void onReady(ServerReadyEvent event) {
    BootstrapConfiguration bootstrapConfiguration = bootstrap.baseConfiguration();
    Log.info(HelloWorldApplication.class,
        "Application Ready. - try http://%1$s:%2$s/hello/world or https://%1$s:%3$s/hello/secure-world",
        bootstrapConfiguration.hostName(), bootstrapConfiguration.httpPort(), bootstrapConfiguration.httpsPort());
  }
}

