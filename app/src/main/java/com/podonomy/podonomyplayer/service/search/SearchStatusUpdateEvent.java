package com.podonomy.podonomyplayer.service.search;


public class SearchStatusUpdateEvent {
  protected long originalSearchEventID = 0L;

  public long getOriginalSearchEventID() {
    return originalSearchEventID;
  }
  public void setOriginalSearchEventID(long originalSearchEventID) {
    this.originalSearchEventID = originalSearchEventID;
  }
}
