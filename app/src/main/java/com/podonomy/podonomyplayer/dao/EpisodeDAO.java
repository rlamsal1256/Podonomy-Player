package com.podonomy.podonomyplayer.dao;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

/**
 * Created by fgagn_000 on 2/21/2016.
 */
public class EpisodeDAO extends DAO{
  static final String PK_FIELD_NAME = "id";

  public static Episode nnew (Realm r){
    Episode e = r.createObject(Episode.class);
    return e;
  }

  public static Episode nnew (){
    return new Episode();
  }
  public static Episode find (Realm r, String primaryKey){
    return r.where(Episode.class).equalTo(PK_FIELD_NAME, primaryKey).findFirst();
  }
  public static Episode findOrCreate(Realm r, String primaryKey){
    Episode e = find(r, primaryKey);
    if (e == null) {
      e = nnew(r);
      e.getEx().setPK(primaryKey);
    }
    return e;
  }

  public static List<Episode> forChannel(Realm r, String channelID){
    return r.where(Episode.class).equalTo("channel.ID", channelID).findAll();
  }

}
