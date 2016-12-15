package com.podonomy.podonomyplayer.event;

/**
 * This event allows the called to request that his previous event be cancelled.  For instance posting
 * an event X may cause the system to perform multiple task which would take some time to complete.
 * Posting the CancelEvent using the original event's ID (i.e. X.getEventID) woudl request that the
 * processing of task(s) pertaining to X be stopped.
 */
public class CancelEvent extends UserEventBase {
  protected long eventToCancel;

  /**
   * Sets the ID of the event to be cancelled.
   */
  public void setEventToCancel(long eventToCancel) {
    this.eventToCancel = eventToCancel;
  }
  public long getEventToCancel() {
    return eventToCancel;
  }

  @Override
  public Object[] getArguments() {
    return new String[] {
      "eventToCancel", Long.toString(eventToCancel)
    };
  }
}
