package com.podonomy.podonomyplayer.dao;

import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;

/**
 * Created by Francois on 3/1/2016.
 */
public class DistributionMethodDAO {
  private static final byte _PER_MIN_LISTENED     = (byte) 01;
  private static final byte _PER_EPISODE_LISTENED = (byte) 02;

  private static Map<Byte, String> methodNames = new HashMap<Byte, String>(){{
    put(_PER_MIN_LISTENED     , "Per minute listened");
    put(_PER_EPISODE_LISTENED , "Per episode listened");
  }};

  private static DistributionMethod getDistribution(Realm r, byte id){
    DistributionMethod method = r.where(DistributionMethod.class).equalTo("id", id).findFirst();
    if (method == null){
      method = r.createObject(DistributionMethod.class);
      method.setId(id);
      method.setName(methodNames.get(id));
    }
    return method;
  }

  public final static DistributionMethod PER_MIN_LISTENED(Realm r) {
    return getDistribution(r, _PER_MIN_LISTENED);
  }

  public final static DistributionMethod _ER_EPISODE_LISTENED(Realm r) {
    return getDistribution(r, _PER_EPISODE_LISTENED);
  }
}
