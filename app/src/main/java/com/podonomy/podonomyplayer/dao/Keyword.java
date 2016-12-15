package com.podonomy.podonomyplayer.dao;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Francois on 3/1/2016.
 */
public class Keyword extends RealmObject {
  @PrimaryKey
  private String text;

  public String getText() {
    return text;
  }
  public void setText(String text) {
    this.text = text;
  }
}
