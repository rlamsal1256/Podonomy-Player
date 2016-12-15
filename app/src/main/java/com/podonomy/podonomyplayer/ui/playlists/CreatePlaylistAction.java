package com.podonomy.podonomyplayer.ui.playlists;

/**
 * Creates a playlist with the vien name.  If the name is empty or a playlist with that name already
 * exists, a exception is thrown.
 */
public class CreatePlaylistAction /*extends Action<PlayList> */ {
/*  private String playListName = null;

  public CreatePlaylistAction(String playlistName, Context context){
    super("CreatePlaylistAction", context);
  }

  @Override
  protected void _execute() throws Exception{
    if (playListName == null )
      throw new InvalidPlaylistException(playListName);

    playListName = playListName.trim();
    if (playListName.isEmpty())
      throw new InvalidPlaylistException(playListName);

    List<PlayList> playlists = PlayListDAO.getPlaylists(getRealm());
    for(PlayList p : playlists){
      if (p.getName().equalsIgnoreCase(playListName))
        throw new PlaylistAlreadyExistsException(playListName);
    }

    PlayList pl = PlayListDAO.nnew(getRealm());
    pl.setName(playListName);
    setResult(pl);
  }

  @Override
  public Object[] arguments() {
    return new String [] {
      "playlistName:", playListName
    };
  }
*/
}
