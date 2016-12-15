package com.podonomy.podonomyplayer.dao;

import io.realm.Realm;

/**
 *
 */
public class ThumbnailDAO {
  public Thumbnail nnew(){return new Thumbnail();}
  public Thumbnail nnew(Realm r){ return r.createObject(Thumbnail.class);}
}
