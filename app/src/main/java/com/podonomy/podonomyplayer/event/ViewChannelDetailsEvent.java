package com.podonomy.podonomyplayer.event;

import org.apache.commons.lang3.ArrayUtils;

/**
 * Represent the user viewing the episodes for a channel
 */
public class ViewChannelDetailsEvent extends UserEventBase {
  private String channelID = null;
  private String channelURL = null;

  public ViewChannelDetailsEvent(String channelID, String channelURL){
    this.channelID = channelID;
    this.channelURL = channelURL;
  }

  @Override
  public Object[] getArguments() {
    return ArrayUtils.addAll(super.getArguments(), new Object[]{
       "channelID", channelID,
       "channelURL", channelURL
    });
  }
}
