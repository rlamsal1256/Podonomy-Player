package com.podonomy.podonomyplayer.service;

import android.widget.Toast;

import com.podonomy.podonomyplayer.R;
import com.podonomy.podonomyplayer.dao.Channel;
import com.podonomy.podonomyplayer.dao.ChannelDAO;
import com.podonomy.podonomyplayer.dao.DAO;
import com.podonomy.podonomyplayer.event.Bus;
import com.podonomy.podonomyplayer.event.Event;
import com.podonomy.podonomyplayer.event.SubscribeToChannelEvent;

/**
 * This task is responsible for subscribing the user to the given channel.
 */
public class SubscribeToChannelTask extends TaskBase<SubscribeToChannelEvent> {
  final String TAG = "SubscribeTask";

  @Override
  public void _run() {
    if (event == null || event.getChannel() == null)
      return;
    Bus.consume(event);

    logger.debug(TAG, "Subscribing to channel ", event.getChannel().getID());
    DAO.beginTransaction(realm);
    try{
      Channel channel = ChannelDAO.find(realm, event.getChannel().getID());
      if (channel == null) {
        //this channel isn't in our database, so let's add it...
        channel = event.getChannel();
        DAO.attach(realm, channel);
      }
      channel.setSubscribed(true);
      DAO.commitTransaction(realm);
      String subscribedMsg = context.getString(R.string.subscribed_message);
    }
    catch (Exception e){
      logger.error(TAG, e, "Exception occurred trying to subscribe to channel ", event.getChannel().getID() );
      DAO.rollbackTransaction(realm);
    }
    finally {
      Event e = event.getResponseEvent();
      Bus.post(e);
    }
  }
}
