package com.podonomy.podonomyplayer.ui.playlists;

import com.podonomy.podonomyplayer.PlayerApplication;
import com.podonomy.podonomyplayer.R;

public class InvalidPlaylistException extends PlaylistException {
  public InvalidPlaylistException(String playlistName){
    super(String.format(PlayerApplication.getInstance().getAppContext().getString(R.string.playlist_InvalidPlaylistException),
                        playlistName));
  }
}
