package com.podonomy.podonomyplayer.event;

/**
 * Interface that any event must implement to be saved/persisted.  All subclasses will be logged by the EventLogger.
 * Therefore, any event that ought to be recorded in our logs (to be sent to the server) must implement this
 * interface.
 */
public interface Event {
  /**
   * The name of the event to be recorded.  Please ensure those are unique.
   */
  String getName();

  /**
   * The argument that were provided or gathered to perform the event, string formatted. The returned array should contain
   * the name of the arguments (with a column) and then the arguments. For instance:
   * new String[] { "arg1:", "foo", "arg2:", "bar", ... }
   */
  Object[] getArguments();

  /**
   * Unique ID of the event. This is can often be used to cancel an event.
   */
  long getEventID();

  /**
   * The event priority in terms of processing. Events generated by the user should have a higher priority
   * so the user does not have to wait on other less important tasks. Thie higher the number, the higher the
   * priority.
   */
  int getPriority();
}
