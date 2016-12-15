package com.podonomy.podonomyplayer.dao;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * The distribution method represents how a user wishes to have his monthly allowance distributed to the publishes
 * to which he listened to.
 */
public class DistributionMethod extends RealmObject {
  @PrimaryKey
  private byte id;
  private String name;

  /**
   * The PK of this object in the database.
   */
  public byte getId() {
    return id;
  }
  public void setId(byte id) {
    this.id = id;
  }

  /**
   * The name of this distribution method.
   */
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
}
