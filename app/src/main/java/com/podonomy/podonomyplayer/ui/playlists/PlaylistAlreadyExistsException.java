package com.podonomy.podonomyplayer.ui.playlists;

import com.podonomy.podonomyplayer.PlayerApplication;
import com.podonomy.podonomyplayer.R;

public class PlaylistAlreadyExistsException extends PlaylistException{
   public PlaylistAlreadyExistsException(String playlistName){
     super(String.format(PlayerApplication.getInstance().getAppContext().getString(R.string.playlist_PlaylistAlreadyExistsException),
           playlistName));
   }
}
