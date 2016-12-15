package com.podonomy.podonomyplayer.dao;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * The Configuration object stores the configuration for the entire application.
 */
public class Configuration extends RealmObject  {
  private String eventLogFileName;
  private long assetCacheMaxSize;
  private long downloadCacheMaxSize;
  private RealmList<Setting> __defaultSettings = new RealmList<>();
  private int themeID;
  @Ignore
  private DefaultSettings defaultSettings;

  /**
   * Returns the theme ID as set by the user.  For example we currently have a "dark" and "light" themes.  This returns
   * the ID as an integer for the theme as set in the android resource (R.style....)
   */
  public int getThemeID() {
    return themeID;
  }
  public void setThemeID(int themeID) {
    this.themeID = themeID;
  }
  /**
   * Returns the name of the event log file names.
   */
  public String getEventLogFileName() {
    return eventLogFileName;
  }
  public void setEventLogFileName(String eventLogFileName) {
    this.eventLogFileName = eventLogFileName;
  }

  /**
   * Returns the maximum size the asset cache is allowed to grow to on the system.
   * The asset cache contains the images and other assets pertaining to a channel or episode.
   * The value is in bytes.
   */
  public long getAssetCacheMaxSize() {
    return assetCacheMaxSize;
  }
  public void setAssetCacheMaxSize(long cacheMaxSize) {
    this.assetCacheMaxSize = cacheMaxSize;
  }

  /**
   * Returns the maximum size (in bytes) the download cache is allowed to grow. The download cache
   * contains downloaded episodes.
   */
  public long getDownloadCacheMaxSize() {
    return downloadCacheMaxSize;
  }
  public void setDownloadCacheMaxSize(long downloadCacheMaxSize) {
    this.downloadCacheMaxSize = downloadCacheMaxSize;
  }

  public RealmList<Setting> get__defaultSettings() {
    return __defaultSettings;
  }
  public void set__defaultSettings(RealmList<Setting> __defaultSettings) {
    this.__defaultSettings = __defaultSettings;
  }

  public DefaultSettings getDefaultSettings(){
    if (defaultSettings == null)
      defaultSettings = new DefaultSettings();
    return defaultSettings;
  }
  public class DefaultSettings extends SettingProvider{
    public DefaultSettings(){super(__defaultSettings, null);}

    @Override
    protected Setting find(String name) {
      Setting s = super.find(name);
      if (s == null)
        return null;
      s = SettingDAO.nnew(s.getName(), s.getValue());  //clone the setting before returning it.
      return s;
    }
  }
}
