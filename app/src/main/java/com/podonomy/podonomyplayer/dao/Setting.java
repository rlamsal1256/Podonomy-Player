package com.podonomy.podonomyplayer.dao;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Setting extends RealmObject{
  public static final String PLAY_SPEED = "PlaySpeed";

  @PrimaryKey
  private String name;
  private String value;

  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public String getValue() {
    return value;
  }
  public void setValue(String value) {
    this.value = value;
  }
}
