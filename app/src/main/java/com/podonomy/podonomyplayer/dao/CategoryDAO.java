package com.podonomy.podonomyplayer.dao;

import io.realm.Case;
import io.realm.Realm;

/**
 * Use this object to save and retrieve Category object to/from the database.
 */
public class CategoryDAO {
  /**
   * Creates a new instance of Category and attach it to the database to be saved upon committing
   * the transaction.
   */
  public static Category nnew(Realm r){
    return r.createObject(Category.class);
  }

  /**
   * Creates an instance of Channel that will NOT be persisted.  Use carefully!
   */
  public static Category nnew(){
    return new Category();
  }

  /**
   * Look for a category object with a name matching the given pk.  If not found, create one and returns is.
   */
  public static Category findOrCreate(Realm realm, String pk ){
    Category c = realm.where(Category.class).equalTo("name", pk, Case.INSENSITIVE).findFirst();
    if (c == null) {
      c = realm.createObject(Category.class);
      c.setName(pk);
    }
    return c;
  }

}
