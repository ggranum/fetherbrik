package com.fetherbrik.servlet.event;

import org.eclipse.jetty.util.component.LifeCycle;

public class ServerFailureEvent implements ServerLifeCycleEvent {
  public final LifeCycle lifeCycle;
  public final Throwable cause;

  public ServerFailureEvent(LifeCycle lifeCycle, Throwable cause) {
    this.lifeCycle = lifeCycle;
    this.cause = cause;
  }
}
