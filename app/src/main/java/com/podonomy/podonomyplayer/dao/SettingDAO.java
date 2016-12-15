package com.podonomy.podonomyplayer.dao;

import io.realm.Realm;
import io.realm.RealmObject;

/**
 *
 */
public class SettingDAO  {
  public static Setting nnew(String name, String value){
    Setting s = new Setting();
    init(s, name, value);
    return s;
  }

  public static Setting nnew(String name, String value, Realm realm){
    Setting s = realm.createObject(Setting.class);
    init(s, name, value);
    return s;
  }

  private static void init(Setting s, String name, String value){
    s.setName(name);
    s.setValue(value);
  }
}
