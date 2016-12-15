package com.podonomy.podonomyplayer.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.podonomy.podonomyplayer.PlayerApplication;
import com.podonomy.podonomyplayer.dao.ConfigurationDAO;
import com.podonomy.podonomyplayer.dao.DAO;
import com.podonomy.podonomyplayer.event.Event;
import com.podonomy.podonomyplayer.event.EventLogger;

import io.realm.Realm;

/**
 * Base class for all Podonomy Services
 */
public class ServiceBase extends Service {
  protected Realm       realm;
  private   EventLogger logger = null;

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    realm = DAO.getRealm(getBaseContext());
    if (realm == null)
      throw new RuntimeException("ServiceBase failed to acquire realm instance.");
  }

  @Override
  public void onDestroy() {
    try {
      if (realm != null && realm.isInTransaction())
        DAO.rollbackTransaction(realm);
      realm = null;
    }
    finally {
      super.onDestroy();
    }
  }

  /**
   * Returns the {@link EventLogger}.  Must be called AFTER the {@link #onCreate()} has been invoked.
   */
  protected EventLogger getLogger(){
    if (logger == null){
      logger = EventLogger.getLogger(ConfigurationDAO.getConfig(realm));
    }
    return logger;
  }
}
