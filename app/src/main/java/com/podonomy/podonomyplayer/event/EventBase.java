package com.podonomy.podonomyplayer.event;

import android.text.TextUtils;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

/**
 * Utility base class implementing the Event interface.
 */
public class EventBase implements Event {
  protected String   name     = null;
  protected long     eventID  = System.currentTimeMillis() | (long) (Math.random() * 1000000000.0);
  protected int      priority = 0;

  @Override
  public Object[] getArguments() {
    return new String[] { "id", Long.toString(eventID)};
  }
  @Override
  public String getName() {
    if (this.name == null)
      return this.getClass().getSimpleName();
    else
      return this.name;
  }
  public void setName(String name) {
    this.name = name;
  }
  @Override
  public long getEventID() {
    return eventID;
  }
  @Override
  public int getPriority(){
    return priority;
  }
  public void setPriority(int x){
    priority = x;
  }
  @Override
  public boolean equals(Object o) {
    if (o == null || ! (o instanceof Event) || !o.getClass().equals(getClass()))
      return false;
    return eventID == ((Event)o).getEventID();
  }
  @Override
  public int hashCode() {
    return (int) (eventID ^ (eventID >>> 32));
  }
  @Override
  public String toString(){
    Object[] arr = ArrayUtils.addAll(new String[]{ClassUtils.getShortCanonicalName(getClass())}, getArguments());
    return TextUtils.join(" ", arr);
  }
  public EventBase getResponseEvent(){
    throw new Error("Subclass must override");
  }
  public EventBase getFailureResponseEvent(String errorMsg){
    throw new Error("Subclass must override");
  }
}
