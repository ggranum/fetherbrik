/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */

package com.fetherbrik.helloworld;

import com.fetherbrik.helloworld.bootstrap.HelloWorldConfiguration;
import com.fetherbrik.helloworld.bootstrap.HelloWorldSpecialModuleProvider;
import com.fetherbrik.core.log.Log;
import com.fetherbrik.servlet.FetherBrikApplication;
import com.fetherbrik.servlet.bootstrap.Bootstrap;
import com.google.inject.Inject;
import com.google.inject.Stage;

/**
 * This is the main application class. That is to say, the entrance to everything that is executed first, from the
 * command line. For example:
 * <p/>
 * java -jar application.jar --http_port 9280 --log_dir ./logs --db_url localhost
 *
 * @author Geoff M. Granum
 */
public final class HelloWorldApplication extends FetherBrikApplication {

  @Inject
  private HelloWorldApplication(Bootstrap bootstrap, HelloWorldConfiguration configuration) {
    super(bootstrap, configuration);
  }

  public static void main(String[] commandLineArgs) throws Exception {
    Bootstrap.disableVerboseNetworkAndCertificateLogging();
    Bootstrap bootstrap = new Bootstrap.Builder()
                              .appName("Hello World")
                              .basePath("./")
                              .commandLineArgs(commandLineArgs)
                              .environmentPrefix("HELLO_WORLD")
                              .injectionStage(Stage.PRODUCTION)
                              .moduleProvider(new HelloWorldSpecialModuleProvider())
                              .servletModule(new HelloWorldServletModule())
                              .configurationClass(HelloWorldConfiguration.class)
                              .build();
    Log.info(HelloWorldApplication.class, "Starting application.");

    HelloWorldApplication application = bootstrap.start(HelloWorldApplication.class);
  }
}

