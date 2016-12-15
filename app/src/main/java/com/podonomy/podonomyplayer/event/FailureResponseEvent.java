package com.podonomy.podonomyplayer.event;

/**
 *
 */
public abstract class FailureResponseEvent extends ResponseEvent {
  protected String errorMsg = null;
  protected FailureResponseEvent(String errorMsg){ this.errorMsg = errorMsg;}
}
