package com.podonomy.podonomyplayer.ui.playlists;

public class PlaylistException extends Exception {
  public PlaylistException(String message){
    this(message, null);
  }

  public PlaylistException(String message, Throwable e){
    super(message, e);
  }
}
