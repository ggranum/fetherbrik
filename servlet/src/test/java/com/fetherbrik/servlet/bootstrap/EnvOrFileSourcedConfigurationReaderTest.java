/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */

package com.fetherbrik.servlet.bootstrap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fetherbrik.core.validation.ValidationException;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.fail;

/**
 * @author ggranum
 */
public class EnvOrFileSourcedConfigurationReaderTest {

  @Test
  public void testCanReadFromMap() {

    EnvOrFileSourcedConfigurationReader<DefaultBootstrapConfiguration> fetherBrik = new EnvOrFileSourcedConfigurationReader<>("fetherBrik",
                                                                                                                         Env.DEVELOPMENT,
                                                                                                                         "./config",
                                                                                                                         "fetherBrik",
                                                                                                                         DefaultBootstrapConfiguration.class,
                                                                                                                         new ObjectMapper());

    Map<String, String> values = ImmutableMap.<String, String>builder()
                                     .put("httpPort", "8080")
                                     .put("httpsPort", "8043")
                                     .put("env", "dev")
                                     .put("logDir", "./")
                                     .build();
    DefaultBootstrapConfiguration cfg = fetherBrik.from(values);
    assertThat(cfg.httpsPort, is(8043));
  }

  @Test(expectedExceptions = ValidationException.class)
  public void testConfigurationEnforcesConstraints() {
    EnvOrFileSourcedConfigurationReader<DefaultBootstrapConfiguration> fetherBrik = new EnvOrFileSourcedConfigurationReader<>("fetherBrik",
                                                                                                                         Env.DEVELOPMENT,
                                                                                                                         "./config",
                                                                                                                         "fetherBrik",
                                                                                                                         DefaultBootstrapConfiguration.class,
                                                                                                                         new ObjectMapper());

    Map<String, String> values = ImmutableMap.<String, String>builder()
                                     .put("httpPort", "8080")
                                     .build();
    DefaultBootstrapConfiguration cfg = fetherBrik.from(values);
    fail("Should not get here.");
  }

  @Test
  public void testCanReadFromFile() {

  }
}
