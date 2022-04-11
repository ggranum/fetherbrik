package com.fetherbrik.servlet.event;

import org.eclipse.jetty.util.component.LifeCycle;

public class ServerReadyEvent implements ServerLifeCycleEvent {
  public final LifeCycle lifecycle;

  public ServerReadyEvent(LifeCycle event) {
    lifecycle = event;
  }
}
