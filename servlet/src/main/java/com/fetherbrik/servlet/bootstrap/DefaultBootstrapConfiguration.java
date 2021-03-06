/*
 * This software is licensed under the MIT License.
 *
 * Copyright (c) 2019 Geoff M. Granum
 */

package com.fetherbrik.servlet.bootstrap;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fetherbrik.core.exception.FormattedException;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.IOException;
import java.util.Optional;


/**
 * @author ggranum
 */
@JsonDeserialize(builder = DefaultBootstrapConfiguration.Builder.class)
public final class DefaultBootstrapConfiguration implements BootstrapConfiguration {

  public final @NotNull @Size(min = 1) String env;
  public final @Min(1) @Max(65535) int httpPort;
  public final @Min(1) @Max(65535) int httpsPort;
  public final @Size(min = 1, max = 200) Optional<String> jettyHome;
  private Optional<String> hostName;

  private DefaultBootstrapConfiguration(Builder builder) {
    env = builder.env;
    httpPort = builder.httpPort;
    httpsPort = builder.httpsPort;
    hostName = Optional.ofNullable(builder.hostName);
    jettyHome = Optional.ofNullable(builder.jettyHome);
  }

  public String toJson(ObjectMapper mapper) {
    try {
      return mapper.writeValueAsString(this);
    } catch (JsonProcessingException e) {
      throw new FormattedException(e, "Could not write FetherBrikBootstrapConfiguration as Json");
    }
  }

  @Override public String hostName() {
    return hostName.orElse("127.0.0.1");
  }

  @Override
  public int httpPort() {
    return this.httpPort;
  }

  @Override
  public int httpsPort() {
    return this.httpsPort;
  }

  @Override
  public String jettyHome() {
    return this.jettyHome.orElse("./");
  }

  public static DefaultBootstrapConfiguration fromJson(ObjectMapper mapper, String json) {
    try {
      return mapper.readValue(json, DefaultBootstrapConfiguration.class);
    } catch (IOException e) {
      // This will be verbose, but without it we won't know the cause of the fatal exception.
      throw new FormattedException(e, "Could not create instance from provided JSON.\n\n %s \n\n", json);
    }
  }

  public static final class Builder {

    @JsonProperty private String hostName;
    @JsonProperty private @NotNull @Size(min = 1) String env;
    @JsonProperty private @Min(1) @Max(65535) Integer httpPort = 0;
    @JsonProperty private @Min(1) @Max(65535) Integer httpsPort = 0;
    @JsonProperty private String jettyHome;

    public Builder() {
    }

    public Builder env(String env) {
      this.env = env;
      return this;
    }

    public Builder httpPort(int httpPort) {
      this.httpPort = httpPort;
      return this;
    }

    public Builder httpsPort(int httpsPort) {
      this.httpsPort = httpsPort;
      return this;
    }

    public Builder jettyHome(String jettyHome) {
      this.jettyHome = jettyHome;
      return this;
    }

    public DefaultBootstrapConfiguration build() {
      /** @todo ggranum: Implement a validation scheme that supports annotations that doesn't
       * require entire JavaEE library */
      return new DefaultBootstrapConfiguration(this);
    }
  }
}
