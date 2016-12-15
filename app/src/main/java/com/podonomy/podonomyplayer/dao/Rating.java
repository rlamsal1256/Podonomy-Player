package com.podonomy.podonomyplayer.dao;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Francois on 3/1/2016.
 */
public class Rating extends RealmObject {
  @PrimaryKey
  private byte id;
  private String name;
  @Ignore
  private byte Ex;


  public void setName(String name) {
    this.name = name;
  }
  public String getName(){
    return this.name;
  }
  public void setId(byte id) {
    this.id = id;
  }
  public byte getId() {
    return id;
  }
  public RatingExtension getEx(){
    return new RatingExtension();
  }

  class RatingExtension {
    public boolean eq(Rating b) {
      return id == b.id;
    }
    public String  toString(Rating a){
      return a.getName();
    }
    public boolean gt(Rating b) {
      return id > b.id;
    }
    public boolean gte(Rating b) {
      return id >= b.id;
    }
    public boolean lte(Rating b) {
      return id <= b.id;
    }
  }
}
