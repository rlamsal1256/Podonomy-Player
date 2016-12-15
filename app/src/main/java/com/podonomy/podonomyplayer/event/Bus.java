package com.podonomy.podonomyplayer.event;

import org.apache.commons.lang3.ClassUtils;
import org.greenrobot.eventbus.EventBus;

/**
 * Utility wrapper around the EventBus. Note that your methods will still need to be annotated with the
 * Subscribe annotation provided by org.greenrobot.eventbus.  Therefore, ensue you set the "sticky" attribute
 * correctly.
 */
public class Bus {
  private static final String TAG = "Bus";
  /**
   * Utility method to post an event on the event bus...  The event will remain on the bus until a subscriber
   * consumes the event.
   */
  public static void post(Event e){
    post(e, true);
  }

  /**
   * This will post the event on the Bus but the event does NOT required to be consume. This means
   * that as soon as the bus finds a subscriber for the event, it will get consume immediately (and
   * automatically) by this subscriber. If there are other subscribers to this event, the will NOT be
   * notified since the event was consume by the first subscriber.
   */
  public static void postAny(Event e){ post(e, false);}

  /**
   * Remove the sticky event from the bus
   */
  public static void consume(Event e){
    try {
      if (e == null) return;
      EventBus.getDefault().removeStickyEvent(e);
    }
    catch(Exception ex){
      EventLogger.getLogger().error(TAG, ex, "Exception caught trying to remove event ", e.getClass().getCanonicalName(), " from event bus");
    }
  }

  /**
   * Registers self on the event bus to receive events.
   */
  public static void registerForEvents(Object o){
    try {
      if (o == null) return;
      EventBus.getDefault().register(o);
    }
    catch(Exception e){
      EventLogger.getLogger().error(TAG, e, "Exception caught trying to register ", o.getClass().getCanonicalName(), " with the event bus.");
    }
  }

  /**
   * Removes self from the event bus (will no longer receive subscribed events)
   */
  public static void unregister(Object o){
    try {
      if (o == null) return;
      EventBus.getDefault().unregister(o);
    }
    catch(Exception e){
      EventLogger.getLogger().error(TAG, e, "Exception caught trying to unregister ", o.getClass().getCanonicalName(), " from the event bus.");
    }
  }

  private static void post(Event e, boolean sticky){
    if (e == null) return;
    try {
      if (! (e instanceof UserEvent)) {
        EventLogger.getLogger().debug(TAG, ">>Event<< ", e.toString());
      }
      if (sticky)
        EventBus.getDefault().postSticky(e);
      else
        EventBus.getDefault().post(e);
    }
    catch(Exception ex) {
      EventLogger.getLogger().error(TAG, ex, "Exception caught trying to post event ", e.getClass().getCanonicalName(), " onto the event bus");
    }
  }

}
