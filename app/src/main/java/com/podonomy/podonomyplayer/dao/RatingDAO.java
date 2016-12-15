package com.podonomy.podonomyplayer.dao;

import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;

/**
 * Created by Francois on 3/1/2016.
 */
public class RatingDAO {
  private static final byte _EVERYONE           = (byte) 00;
  private static final byte _PARENTAL_GUIDANCE  = (byte) 10;
  private static final byte _PG13               = (byte) 20;
  private static final byte _RESTRICTED         = (byte) 30;
  private static final byte _NC17               = (byte) 40;
  private static final byte _EXPLICIT_LANGUAGE  = (byte) 50;
  private static final byte _SEXUAL_LANGUAGE    = (byte) 60;
  private static final byte _EXPLICIT_IMAGES    = (byte) 70;
  private static final byte _SEXUAL_IMAGES      = (byte) 80;
  private static final byte _UNKNOWN            = (byte) 90;
  private static final byte _FLAGGED            = (byte) 100;

  private static Map<Byte, String> ratingsNames = new HashMap<Byte, String>();
  static {
    ratingsNames.put(_EVERYONE, "General / Everyone");
    ratingsNames.put(_PARENTAL_GUIDANCE, "Parental Guidance");
    ratingsNames.put(_PG13, "Parents Cautioned");
    ratingsNames.put(_RESTRICTED, "Restricted");
    ratingsNames.put(_NC17, "Adults Only");
    ratingsNames.put(_EXPLICIT_LANGUAGE, "Explicit Language");
    ratingsNames.put(_SEXUAL_LANGUAGE, "Sexual Language");
    ratingsNames.put(_EXPLICIT_IMAGES, "Explicit Images");
    ratingsNames.put(_SEXUAL_IMAGES, "Sexual Images");
    ratingsNames.put(_UNKNOWN, "Unknown");
    ratingsNames.put(_FLAGGED, "Flagged");
  };

  private static Rating getRating(Realm r, byte value){
    Rating rating = r.where(Rating.class).equalTo("id", value).findFirst();
    if (rating == null){
      rating = r.createObject(Rating.class);
      rating.setId(value);
      rating.setName(ratingsNames.get(value));
    }
    return rating;
  }

  public final static Rating EVERYONE(Realm r) {
    return getRating(r, _EVERYONE);
  }

  public static Rating PARENTAL_GUIDANCE ;
  public static Rating PG13 = null ;
  public static Rating RESTRICTED = null ;
  public static Rating NC17 = null ;
  public static Rating EXPLICIT_LANGUAGE = null ;
  public static Rating SEXUAL_LANGUAGE = null ;
  public static Rating EXPLICIT_IMAGES = null ;
  public static Rating SEXUAL_IMAGES = null ;
  public static Rating UNKNOWN = null ;
  public static Rating FLAGGED = null ;

  static void init(Realm r) {
    if (r == null)
      return;

    boolean beganTransaction = false;
    if (!r.isInTransaction()) {
      r.beginTransaction();
      beganTransaction = true;
    }
    PARENTAL_GUIDANCE = getRating(r, _PARENTAL_GUIDANCE);
    PG13 = getRating(r, _PG13);
    RESTRICTED = getRating(r, _RESTRICTED);
    NC17 = getRating(r, _NC17);
    EXPLICIT_LANGUAGE = getRating(r, _EXPLICIT_LANGUAGE);
    SEXUAL_LANGUAGE = getRating(r, _SEXUAL_LANGUAGE);
    EXPLICIT_IMAGES = getRating(r, _EXPLICIT_IMAGES);
    SEXUAL_IMAGES = getRating(r, _SEXUAL_IMAGES);
    UNKNOWN = getRating(r, _UNKNOWN);
    FLAGGED = getRating(r, _FLAGGED);
    if (beganTransaction){
      r.commitTransaction();
    }
  }
}

