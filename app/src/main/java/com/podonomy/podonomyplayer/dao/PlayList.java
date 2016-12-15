package com.podonomy.podonomyplayer.dao;

import io.realm.RealmList;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * This class represents a playlist which can be created by the user.  Play lists contains episodes.
 */
public class PlayList extends io.realm.RealmObject  {
    @PrimaryKey
    private String name;
    private boolean deletable = true;
    private RealmList<Episode> content    = new RealmList<>();
    private RealmList<Setting> __settings = new RealmList<>();
    @Ignore
    private Ex ex = null;

    /**
     * The name of this playlist.
     * @return
     */
    public String getName(){
        return this.name;
    }
    public void setName(String n){
        this.name = n;
    }

    /**
     * Returns whether this playlist is deletable.  User created playlist are deletable while system
     * created playlist are not.
     */
    public boolean isDeletable() {
        return deletable;
    }
    public void setDeletable(boolean deletable) {
        this.deletable = deletable;
    }

    /**
     * Returns the list of {@link Episode episodes} contained by this playlist. Returns an emplty list
     * if none are contained.
     */
    public RealmList<Episode> getContent() {
        return content;
    }

    /**
     * Consider this a private method. DO NOT USE.  User {@link Ex#add} instead.
     */
    public void setContent(RealmList<Episode> content) {
        this.content = content;
    }

    /**
     * Consider this private. DO NOT USE.
     */
    public RealmList<Setting> get__settings() {
      return __settings;
    }
    /**
     * Consider this private. DO NOT USE.
     */
    public void set__settings(RealmList<Setting> __settings) {
      this.__settings = __settings;
    }

    /**
     * Provides access to the extension methods on the Playlist object.
     */
    public Ex getEx(){
        if (ex == null)
            ex = new Ex();
        return ex;
    }
    public class Ex extends SettingProvider {
      Ex() {
//todo: Bring SettingProvider back but without its constructor create a new database transaction (which cause an exception trying to create a transaction within a transaction)
        super(__settings, null);
        //super(__settings, ConfigurationDAO.getConfig(DAO.getRealm(PlayerApplication.getInstance().getBaseContext())).getDefaultSettings());
      }

      /**
       * Add the given episode to the current playlist.  If the episode is already in the list, it is NOT added again.
       */
      public void add(Episode episode) {
        if (episode == null)
          return;

        for (Episode e : content)
          if (e.getEx().equals(episode))
            return;
        content.add(episode);
      }

      /**
       * Removes the given episode from the current playlist.
       */
      public void remove(Episode episode) {
        if (episode == null)
          return;
        content.remove(episode);
      }
    }
}
