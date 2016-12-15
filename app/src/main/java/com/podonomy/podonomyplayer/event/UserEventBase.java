package com.podonomy.podonomyplayer.event;

import android.content.Context;

import com.podonomy.podonomyplayer.PlayerApplication;
import com.podonomy.podonomyplayer.dao.DAO;
import com.podonomy.podonomyplayer.dao.Subscriber;
import com.podonomy.podonomyplayer.dao.SubscriberDAO;
import com.podonomy.podonomyplayer.event.EventBase;
import com.podonomy.podonomyplayer.event.UserEvent;

/**
 * Utility class that implements the {@link UserEvent} interface.
 */
public abstract class UserEventBase extends EventBase implements UserEvent {
  protected static final int DEFAULT_PRIORITY = 1000;
  protected String   userID = null;

  protected UserEventBase(){
    super.setPriority(DEFAULT_PRIORITY);
  }

  /**
   * sets the user ID of the user issuing this event.
   */
  public void setUserID(String userID) {
    this.userID = userID;
  }

  /**
   * Returns the user ID of the user issuing this event.  If {@link #getUserID} has not been called
   * prior to invokign this method, this method will use the subscriber id it can find from the
   * database.
   */
  @Override
  public String getUserID() {
    if (userID != null)
      return userID;

    Context    ctx  = PlayerApplication.getInstance().getAppContext();
    Subscriber subs = SubscriberDAO.getCurrentSubscriber(DAO.getRealm(ctx));
    if (subs == null)
      userID = "null";
    else
      userID = subs.getUsername();
    return userID;
  }
}
