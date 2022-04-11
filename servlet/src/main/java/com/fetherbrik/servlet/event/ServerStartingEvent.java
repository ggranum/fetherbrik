package com.fetherbrik.servlet.event;

import org.eclipse.jetty.util.component.LifeCycle;

public class ServerStartingEvent implements ServerLifeCycleEvent {
  public final LifeCycle lifecycle;

  public ServerStartingEvent(LifeCycle event) {
    lifecycle = event;
  }
}
