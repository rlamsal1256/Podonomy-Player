package com.podonomy.podonomyplayer.dao;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * This object stores a category to which a channel belongs to.
 */
public class Category extends RealmObject {
  @PrimaryKey
  private String name;
  private String name_fr;

  @Ignore
  private Ex ex;

  /**
   * The category name, in english.  This is the PK for this object and thus required.
   */
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
    if (name != null)
      this.name = name.toLowerCase().trim();
  }
  /**
   * The category name, in french.
   */
  public String getName_fr() {
    return name_fr;
  }
  public void setName_fr(String name_fr) {
    this.name_fr = name_fr;
    if (name_fr != null)
      this.name_fr = name_fr.toLowerCase().trim();
  }

  @Override
  public boolean equals(Object o) {
    return super.equals(o);
  }

  public Ex getEx(){
    if (ex == null){
      ex = new Ex();
    }
    return ex;
  }

  public class Ex{
    public boolean equals(Category c){
      if (c == null)
        return false;

      if (c.getName().equals(getName())) return true;
      return c.getName_fr().equals(getName_fr());
    }
  }
}
