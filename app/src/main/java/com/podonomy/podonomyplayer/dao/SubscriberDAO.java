package com.podonomy.podonomyplayer.dao;

import io.realm.Realm;

public class SubscriberDAO {

  public static Subscriber nnew(){
    return new Subscriber();
  }

  public static Subscriber nnew(Realm r){
    Subscriber s = r.createObject(Subscriber.class);
    return s;
  }

  /**
   * Return the current subscriber of this app.  There should only be 1 subscriber per app so this will
   * return it, or null if none are found.
   */
  public static Subscriber getCurrentSubscriber(Realm realm){
     return realm.where(Subscriber.class).findFirst();
  }
}
