package com.fetherbrik.servlet.event;

import org.eclipse.jetty.util.component.LifeCycle;

public class ServerStoppingEvent implements ServerLifeCycleEvent {
  public final LifeCycle lifeCycle;

  public ServerStoppingEvent(LifeCycle lifeCycle) {
    this.lifeCycle = lifeCycle;
  }
}
