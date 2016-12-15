package com.podonomy.podonomyplayer.dao;

import io.realm.Realm;
public class SearchQueryDAO {
  public static SearchQuery nnew(){ return new SearchQuery();}
  public static SearchQuery nnew(Realm realm){
    return realm.createObject(SearchQuery.class);
  }
}
