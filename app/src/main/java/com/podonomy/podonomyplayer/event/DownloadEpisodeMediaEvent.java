package com.podonomy.podonomyplayer.event;

/**
 * This event is to request the download of an actual episode's media (ie. it's audio/video file).
 */
public class DownloadEpisodeMediaEvent extends UserEventBase {
  protected String episodeID = null;

  public String getEpisodeID() {
    return episodeID;
  }
  public void setEpisodeID(String episodeID) {
    this.episodeID = episodeID;
  }
//  @Override
//  public EventBase getResponseEvent() {
//    return new CompleteEvent();
//  }
  @Override
  public EventBase getFailureResponseEvent(String errMsg) {
    return new FailedEvent(errMsg);
  }

//  public class CompleteEvent extends ResponseEvent{
//    @Override
//    public EventBase getOriginalEvent() {
//      return DownloadEpisodeMediaEvent.this;
//    }
//  }

  public class FailedEvent extends FailureResponseEvent{
    public FailedEvent(String errorMsg) {
      super(errorMsg);
    }

    @Override
    public EventBase getOriginalEvent() {
      return DownloadEpisodeMediaEvent.this;
    }
  }
}
