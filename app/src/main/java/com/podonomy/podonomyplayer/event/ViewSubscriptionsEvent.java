package com.podonomy.podonomyplayer.event;

/**
 * This is the action that represents the user viewing his subscriptions.
 */
public class ViewSubscriptionsEvent extends UserEventBase {
  public ViewSubscriptionsEvent(){
    name = "ViewSubscriptionsEvent";
  }
}
