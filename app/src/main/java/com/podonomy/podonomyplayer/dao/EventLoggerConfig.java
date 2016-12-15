package com.podonomy.podonomyplayer.dao;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;

/**
 * Created by Francois on 3/15/2016.
 */
public class EventLoggerConfig extends RealmObject {
  private int ID;
  private String xmlConfig;

  @Ignore
  private Ex ex;

  public int getID() {
    return ID;
  }
  public void setID(int ID) {
    this.ID = ID;
  }
  public String getXmlConfig() {
    return xmlConfig;
  }
  public void setXmlConfig(String xmlConfig) {
    this.xmlConfig = xmlConfig;
  }

  public Ex getEx(){
    if (ex == null)
      ex = new Ex();
    return ex;
  }

  class Ex {

  }
}
