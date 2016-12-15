package com.podonomy.podonomyplayer.event;

import com.podonomy.podonomyplayer.dao.Channel;

import org.apache.commons.lang3.ArrayUtils;

public class UnsubscribeFromChannelEvent extends UserEventBase {
  private Channel channel = null;

  public UnsubscribeFromChannelEvent(Channel channel){
    this.channel = channel;
  }
  public Channel getChannel() {
    return channel;
  }
  @Override
  public Object[] getArguments() {
    return ArrayUtils.addAll(super.getArguments(), new Object[]{
    "channelID", channel.getID()
    });
  }

}