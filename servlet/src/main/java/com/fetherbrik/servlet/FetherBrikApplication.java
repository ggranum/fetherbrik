/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */

package com.fetherbrik.servlet;

import com.fetherbrik.core.log.Log;
import com.fetherbrik.servlet.bootstrap.Bootstrap;
import com.fetherbrik.servlet.bootstrap.BootstrapConfiguration;
import com.fetherbrik.servlet.event.*;
import com.google.common.eventbus.EventBus;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.*;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.component.LifeCycle;
import org.eclipse.jetty.util.component.LifeCycle.Listener;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ScheduledExecutorScheduler;

import javax.servlet.DispatcherType;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.EnumSet;
import java.util.Properties;

import static org.eclipse.jetty.server.CustomRequestLog.EXTENDED_NCSA_FORMAT;

/**
 * @author ggranum
 */
public abstract class FetherBrikApplication {

  private final EventBus appBus;
  private final Bootstrap bootstrap;
  private final BootstrapConfiguration baseConfig;

  public FetherBrikApplication(EventBus appBus, Bootstrap bootstrap, BootstrapConfiguration baseConfig) {
    this.appBus = appBus;
    this.bootstrap = bootstrap;
    this.baseConfig = baseConfig;
  }

  public void start() throws Exception {
    File jettyHomeDir = new File(baseConfig.jettyHome());
    File keystoreFile = new File(jettyHomeDir, "/etc/keystore");
    Properties keystorePasswords = new Properties();
    File ksProps = new File(jettyHomeDir, "etc/keystore.properties");
    try {
      keystorePasswords.load(new FileInputStream(ksProps));
    } catch (FileNotFoundException e) {
      Log.warn(getClass(),
          e,
          "No keystore.properties file found at %s." +
              " Have you created a keystore and saved the passwords to keystore.properties yet? See readme",
          ksProps.getAbsolutePath());
    }

    QueuedThreadPool threadPool = new QueuedThreadPool();
    threadPool.setMaxThreads(500);

    // Server
    Server server = new Server(threadPool);

    // Scheduler
    server.addBean(new ScheduledExecutorScheduler());

    HttpConfiguration httpConfig = createHttpsConfiguration(baseConfig.httpsPort());

    // Handler Structure
    HandlerCollection handlers = new HandlerCollection();
    ContextHandlerCollection contexts = new ContextHandlerCollection();
    handlers.setHandlers(new Handler[]{contexts, new DefaultHandler()});
    server.setHandler(handlers);

    // Extra options
    server.setDumpAfterStart(false);
    server.setDumpBeforeStop(false);
    server.setStopAtShutdown(true);

    // === jetty-http.xml ===
    ServerConnector http = new ServerConnector(server, new HttpConnectionFactory(httpConfig));
    http.setPort(baseConfig.httpPort());
    http.setIdleTimeout(30000);
    server.addConnector(http);

    // === jetty-https.xml ===
    // SSL Context Factory
    ServerConnector sslConnector = createSSLConnector(keystoreFile,
        keystorePasswords,
        server,
        httpConfig,
        baseConfig.httpsPort());
    sslConnector.setHost("fakedomain");
    server.addConnector(sslConnector);

    // === jetty-stats.xml ===
    StatisticsHandler stats = new StatisticsHandler();
    stats.setHandler(server.getHandler());
    server.setHandler(stats);

    // === jetty-requestlog.xml ===
    //    CustomRequestLog#EXTENDED_NCSA_FORMAT} with a {@link RequestLogWriter
    RequestLogWriter logWriter = new RequestLogWriter(jettyHomeDir.getPath() + "/log/yyyy_mm_dd.request.log");
    logWriter.setFilenameDateFormat("yyyy_MM_dd");
    logWriter.setRetainDays(90);
    logWriter.setAppend(true);
    logWriter.setTimeZone("GMT");
    CustomRequestLog requestLog = new CustomRequestLog(logWriter, EXTENDED_NCSA_FORMAT);
    RequestLogHandler requestLogHandler = new RequestLogHandler();
    requestLogHandler.setRequestLog(requestLog);
    handlers.addHandler(requestLogHandler);

    // === jetty-lowresources.xml ===
    LowResourceMonitor lowResourcesMonitor = new LowResourceMonitor(server);
    lowResourcesMonitor.setPeriod(1000);
    lowResourcesMonitor.setLowResourcesIdleTimeout(200);
    lowResourcesMonitor.setMonitorThreads(true);
    lowResourcesMonitor.setMaxMemory(0);
    lowResourcesMonitor.setMaxLowResourcesTime(5000);
    server.addBean(lowResourcesMonitor);
    server.addBean(new ConnectionLimit(5000, server));

    ServletContextHandler root = addServletContext(server);

    // Start the server
    doStart(server, root);
  }

  private ServletContextHandler addServletContext(Server server) {
    ServletContextHandler root = new ServletContextHandler(server, "/", ServletContextHandler.SESSIONS);

    root.addFilter(LoggingGuiceFilter.class, "/*",
        EnumSet.of(DispatcherType.FORWARD,
            DispatcherType.INCLUDE,
            DispatcherType.REQUEST,
            DispatcherType.ASYNC,
            DispatcherType.ERROR)
    );

    root.addServlet(DefaultServlet.class, "/*");

    FetherBrikServletContextListener contextListener = bootstrap.createContextListener(baseConfig);
    root.addEventListener(contextListener);
    return root;
  }

  public HttpConfiguration createHttpsConfiguration(int httpsPort) {
    HttpConfiguration httpConfig = new HttpConfiguration();
    httpConfig.setSecureScheme("https");
    httpConfig.setSecurePort(httpsPort);
    httpConfig.setOutputBufferSize(32768);
    httpConfig.setRequestHeaderSize(8192);
    httpConfig.setResponseHeaderSize(8192);
    httpConfig.setSendServerVersion(false);
    httpConfig.setSendDateHeader(true);
    return httpConfig;
  }

  public ServerConnector createSSLConnector(
      File keystoreFile,
      Properties keystorePasswords,
      Server server,
      HttpConfiguration httpConfiguration, int httpsPort) {

    SslContextFactory sslContextFactory = new SslContextFactory.Server();
    sslContextFactory.setKeyStorePath(keystoreFile.getPath());
    sslContextFactory.setKeyStorePassword(keystorePasswords.getProperty("keystore_password"));
    sslContextFactory.setKeyManagerPassword(keystorePasswords.getProperty("keystore_manager_password"));
    sslContextFactory.setTrustStorePassword(keystorePasswords.getProperty("truststore_password"));
    sslContextFactory.setTrustStorePath(keystoreFile.getPath());
    sslContextFactory.setExcludeProtocols("SSLv2Hello", "TLSv1", "TLSv1.1");

    //    sslContextFactory.setIncludeCipherSuites(".*AES_256_CBC.*",
    //                                             ".*AES_128_CBC.*"
    //    );

    sslContextFactory.setExcludeCipherSuites("^.*_(MD5|SHA|SHA1)$");
    sslContextFactory.setExcludeCipherSuites(
        "^.*_(MD5|SHA|SHA1)$",
        "TLS_RSA_WITH_AES_256_CBC_SHA256",
        "TLS_RSA_WITH_AES_128_CBC_SHA256",
        ".*RC4.*",
        ".*EXPORT.*",
        ".*NULL.*",
        ".*anon.*",
        "TLS_RSA_WITH_AES_256_GCM_SHA384",
        "TLS_RSA_WITH_AES_128_GCM_SHA256"


    );

    // SSL HTTP Configuration
    HttpConfiguration httpsConfig = new HttpConfiguration(httpConfiguration);
    httpsConfig.addCustomizer(new SecureRequestCustomizer());

    // SSL Connector
    SslConnectionFactory factory = new SslConnectionFactory(sslContextFactory, "http/1.1");
    ServerConnector sslConnector = new ServerConnector(server,
        factory,
        new HttpConnectionFactory(httpsConfig));
    sslConnector.setPort(httpsPort);
    return sslConnector;
  }

  private void doStart(final Server server, final ServletContextHandler context) {
    try {
      server.setStopAtShutdown(true);
      server.addLifeCycleListener(new Listener() {
        @Override public void lifeCycleStarting(LifeCycle event) {
          appBus.post(new ServerStartingEvent(event));
        }

        @Override public void lifeCycleStarted(LifeCycle event) {
          appBus.post(new ServerReadyEvent(event));
        }

        @Override public void lifeCycleFailure(LifeCycle event, Throwable cause) {
          appBus.post(new ServerFailureEvent(event, cause));
        }

        @Override public void lifeCycleStopping(LifeCycle event) {
          appBus.post(new ServerStoppingEvent(event));
        }

        @Override public void lifeCycleStopped(LifeCycle event) {
          appBus.post(new ServerStoppedEvent(event));
        }
      });
      server.start();
      server.join();
    } catch (Exception e) {
      try {
        Log.error(getClass(), e, "Attempting to stop server due to unhandled exception.");
        server.setStopTimeout(10000L);
        new Thread() {

          @Override
          public void run() {
            try {
              context.stop();
              server.stop();
              Log.info(getClass(), "Stop has been called. System should now exit.");
            } catch (Exception ex) {
              Log.error(getClass(), ex, "Failed to stop Jetty");
            }
          }
        }.start();
      } catch (Exception e2) {
        throw new RuntimeException(e2);
      }
    }
  }
}
