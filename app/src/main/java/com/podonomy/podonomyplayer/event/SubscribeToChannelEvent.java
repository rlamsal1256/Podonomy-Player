package com.podonomy.podonomyplayer.event;

import com.podonomy.podonomyplayer.dao.Channel;
import org.apache.commons.lang3.ArrayUtils;

public class SubscribeToChannelEvent extends UserEventBase {
  private Channel channel = null;

  public SubscribeToChannelEvent(Channel channel){
    this.channel = channel;
  }

  @Override
  public Object[] getArguments() {
    return ArrayUtils.addAll(super.getArguments(), new Object[]{
    "channelID", channel.getID(),
    });
  }
  public Channel getChannel() {
    return channel;
  }

  public class CompleteEvent extends ResponseEvent {
    @Override
    public EventBase getOriginalEvent() {
      return SubscribeToChannelEvent.this;
    }
  }
  public class FailedEvent extends ResponseEvent {
    @Override
    public EventBase getOriginalEvent() {
      return SubscribeToChannelEvent.this;
    }
  }

}