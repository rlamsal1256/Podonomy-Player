package com.podonomy.podonomyplayer.dao;

import java.security.Key;

import io.realm.Case;
import io.realm.Realm;

/**
 *
 */
public class KeywordDAO {

  public static Keyword nnew(Realm r){
    return r.createObject(Keyword.class);
  }

  /**
   * Creates an instance of Keyword that will NOT be persisted.  Use carefully!
   */
  public static Keyword nnew(){
    return new Keyword();
  }

  public static Keyword findOrCreate(Realm realm, String pk ){
    Keyword k = realm.where(Keyword.class).equalTo("text", pk, Case.INSENSITIVE).findFirst();
    if (k == null) {
      k = nnew(realm);
      k.setText(pk);
    }
    return k;
  }
}
