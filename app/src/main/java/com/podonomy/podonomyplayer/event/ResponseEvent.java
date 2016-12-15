package com.podonomy.podonomyplayer.event;

/**
 * This represents a event created in response to another event.  For instance, supposed that an event
 * DoThisEvent is published, then in response, a new event DoneEvent (extending this class) could
 * be published to signify that the original event has been completed.
 */
public abstract class ResponseEvent extends EventBase {
  public abstract EventBase getOriginalEvent();

  @Override
  public Object[] getArguments() {
    return new String[] {
      "originalEventID", getOriginalEvent() != null ? Long.toString(getOriginalEvent().getEventID()) : "null"
    };
  }
}
