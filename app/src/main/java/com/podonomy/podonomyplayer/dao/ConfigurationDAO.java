package com.podonomy.podonomyplayer.dao;

import android.graphics.Bitmap;
import android.util.Log;

import com.podonomy.podonomyplayer.R;

import java.util.Hashtable;

import io.realm.Realm;
import io.realm.RealmList;

/**
 * This is the object to create / fetch the configuation of the system from the database.
 */
public class ConfigurationDAO extends DAO{
  /**
   * The name of the file where the database is stored.  The folder used will
   * come from context.getFilesDir()
   */
  public static final String REALM_DB_FILE_NAME     = "podonomy.db";
  /**
   * The current version of the schema.
   */
  public static final int    REAL_DB_SCHEMA_VERSION = 1;
  /**
   * The default name of the event log file produced by the EventLogger
   */
  public static final String EVENT_LOG_FILNAME = "eventlog";
  /**
   * The default cache size for the podcast assets....
   */
  public static final long  MAX_CACHE_SIZE = 1024 * 1024 * 256;  //256 MB
  /**
   * The default settings for the Publisher/Channel/Playlist
   */
  private static RealmList<Setting> defaultSettings = new RealmList<>();
  static {
    defaultSettings.add(SettingDAO.nnew(Setting.PLAY_SPEED, "1.0"));
  }
  /**
   * The default theme....
   */
  public static final int  DEFAULT_THEME_ID = R.style.Podonomy_Default;
  /**
   * We keep a cache of the configuration object. However, realm.io does not allow objects to be
   * shared across threads, so we keep 1 cache copy per thread....
   */
  private static ThreadLocal<Configuration> configuration = new ThreadLocal<>();

  /**
   * Returns the configuration for this app.  It will return the configuration as saved in the database.
   * But if the database does not exist/contain any, it will return a default version that will now
   * be persisted.
   */
  public static Configuration getConfig(Realm realm) {
    Configuration config = configuration.get();
    if (config == null) {
      config = realm.where(Configuration.class).findFirst();
      if (config == null) {
        realm.beginTransaction();
        config = realm.createObject(Configuration.class);
        setDefaults(config, realm);
        realm.commitTransaction();
        configuration.set(config);
      }
    }
    return config;
  }

  static void setDefaults(Configuration config, Realm realm) {
    config.setEventLogFileName(EVENT_LOG_FILNAME);
    config.setAssetCacheMaxSize(MAX_CACHE_SIZE);
    config.get__defaultSettings().addAll(defaultSettings);
    if (config.getThemeID() == 0)
      config.setThemeID(DEFAULT_THEME_ID);
  }

  public static void updateConfigThemeID(Realm realm, int THEME_ID){
    DAO.beginTransaction(realm);
    Configuration config = realm.where(Configuration.class).findFirst();
    config.setThemeID(THEME_ID);
    DAO.commitTransaction(realm);
  }
}
