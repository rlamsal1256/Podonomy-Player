package com.podonomy.podonomyplayer.event;

import org.apache.commons.lang3.ArrayUtils;

/**
 * This event is to request that all (up to a certain limit) episodes for a given channel ought
 * to be downloaded and stored in out database....  This event is NOT TO BE CONFUSED WITH {@link DownloadEpisodeMediaEvent}.
 */
public class DownloadChannelEpisodesEvent extends EventBase {
  protected String channelID = null; //the channel ID for which the episodes need to be downloaded
  protected int    maxDownload = 25; //by default only download 25

  public DownloadChannelEpisodesEvent(String channelID){
    this.channelID = channelID;
  }

  @Override
  public Object[] getArguments() {
    return ArrayUtils.addAll(super.getArguments(), new Object[] {
       "channelID", channelID,
       "maxDownload", Integer.toString(maxDownload)
    });
  }
  public String getChannelID() {
    return channelID;
  }
  public void setChannelID(String channelID) {
    this.channelID = channelID;
  }
  public int getMaxDownload() {
    return maxDownload;
  }
  public void setMaxDownload(int maxDownload) {
    this.maxDownload = maxDownload;
  }

  @Override
  public EventBase getResponseEvent() {
    return new CompleteEvent();
  }
  @Override
  public EventBase getFailureResponseEvent(String errorMsg) {
    return new FailedEvent(errorMsg);
  }

  public class CompleteEvent extends ResponseEvent{
    @Override
    public EventBase getOriginalEvent() {
      return DownloadChannelEpisodesEvent.this;
    }
  }
  public class FailedEvent extends FailureResponseEvent{
    public FailedEvent(String errorMsg) {
      super(errorMsg);
    }

    @Override
    public EventBase getOriginalEvent() {
      return DownloadChannelEpisodesEvent.this;
    }
  }
}
