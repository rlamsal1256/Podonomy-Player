package com.podonomy.podonomyplayer.dao;

import com.podonomy.podonomyplayer.PlayerApplication;
import com.podonomy.podonomyplayer.R;
import java.util.List;
import io.realm.Realm;

/**
 * This is the class to user to create / fetch / save playlist object persisted in the database.
 */
public class PlayListDAO {
  private static final String PK = "name";

  /**
   * Creates a new instance of {@link PlayList} tied to the database session so it can be persisted
   * when the transaction is committed (see {@link DAO#commitTransaction})
   */
  public static PlayList nnew(Realm r){
    return r.createObject(PlayList.class);
  }

  /**
   * Returns all the @{link PlayList}s in the system
   */
  public static List<PlayList> getPlaylists(Realm r){
    return r.where(PlayList.class).findAll();
  }

  /**
   * Returns the "Downloaded" playlist.
   */
  public static PlayList getDownloadedPlaylist(Realm r){
    return r.where(PlayList.class).equalTo(PK, getDownloadedName()).findFirst();
  }

  /**
   * Returns the play list with the given ID (name). Returns null if not found.
   */
  public static PlayList getByID(String name, Realm r){
    return r.where(PlayList.class).equalTo(PK, name).findFirst();
  }

  /**
   * This method is called during the database first creation.  It creates a playlist named "Downloaded"
   * in the language of the user.
   */
  static void init(Realm r){
    PlayList p = getDownloadedPlaylist(r);
    if (p == null){
      p = nnew(r);
      p.setName(getDownloadedName());
      p.setDeletable(false);
    }
  }

  /**
   * Returns "Downloaded" in the user's language.
   */
  private static String getDownloadedName(){
    return PlayerApplication.getInstance().getAppContext().getString(R.string.playlist_downloaded);
  }
}
