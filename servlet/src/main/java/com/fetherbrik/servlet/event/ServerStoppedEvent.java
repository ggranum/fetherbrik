package com.fetherbrik.servlet.event;

import org.eclipse.jetty.util.component.LifeCycle;

public class ServerStoppedEvent implements ServerLifeCycleEvent {
  public final LifeCycle lifeCycle;

  public ServerStoppedEvent(LifeCycle lifeCycle) {
    this.lifeCycle = lifeCycle;
  }
}
