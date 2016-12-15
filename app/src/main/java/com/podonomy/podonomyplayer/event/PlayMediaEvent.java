package com.podonomy.podonomyplayer.event;

/**
 * Event raised when the user wants to play an actual episode.
 */
public class PlayMediaEvent extends UserEventBase {
  private String episodeID = null;

  public PlayMediaEvent() {  }
  public String getEpisodeID() {
    return episodeID;
  }
  public void setEpisodeID(String episodeID) {
    this.episodeID = episodeID;
  }
}
