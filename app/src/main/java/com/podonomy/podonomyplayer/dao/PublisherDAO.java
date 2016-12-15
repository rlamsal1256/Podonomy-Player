package com.podonomy.podonomyplayer.dao;


import io.realm.Realm;

public class PublisherDAO {

  public static Publisher nnew(Realm r){
    return r.createObject(Publisher.class);
  }
}
