package com.fetherbrik.servlet.event;

import org.eclipse.jetty.util.component.LifeCycle;

public class ServerBootstrapCompleteEvent implements ServerLifeCycleEvent {
  public final LifeCycle lifecycle;

  public ServerBootstrapCompleteEvent(LifeCycle event) {
    lifecycle = event;
  }
}
