package com.podonomy.podonomyplayer.event;

import com.podonomy.podonomyplayer.event.Event;

/**
 * Represents events that are raised by the user.
 */
public interface UserEvent extends Event {
  /**
   * The ID of the user generating the event.
   */
  String getUserID();
}
