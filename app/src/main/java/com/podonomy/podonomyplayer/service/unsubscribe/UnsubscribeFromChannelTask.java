package com.podonomy.podonomyplayer.service.unsubscribe;

import com.podonomy.podonomyplayer.dao.Channel;
import com.podonomy.podonomyplayer.dao.ChannelDAO;
import com.podonomy.podonomyplayer.dao.DAO;
import com.podonomy.podonomyplayer.dao.Episode;
import com.podonomy.podonomyplayer.dao.EpisodeDAO;
import com.podonomy.podonomyplayer.event.Bus;
import com.podonomy.podonomyplayer.event.UnsubscribeFromChannelEvent;
import com.podonomy.podonomyplayer.service.TaskBase;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class UnsubscribeFromChannelTask extends TaskBase<UnsubscribeFromChannelEvent> implements Runnable {
    final String TAG = "UnsubscribeFromChannelTask";

    @Override
    public void _run() {
        try {
            Channel channel = ChannelDAO.find(realm, event.getChannel().getID());
            if (channel == null)
                return;

            logger.debug(TAG, "Unsubscribing from ", channel.getName());
            List<Episode> episodes = EpisodeDAO.forChannel(realm, channel.getID());
            List<String> episodes2Delete = new ArrayList<>();
            for (Episode episode : episodes) {
                episodes2Delete.add(episode.getId());
            }

            DAO.beginTransaction(realm);
            for (String episodeID : episodes2Delete) {
                Episode e = EpisodeDAO.find(realm, episodeID);
                if (e != null)
                    e.removeFromRealm();
            }
            channel.removeFromRealm();
            DAO.commitTransaction(realm);

        } catch (Exception e) {
            logger.error(TAG, e, "Error unsubscribing from channel.");
        }
        logger.debug(TAG, "done unsubscribing.");
        UnsubscribeFromChannelCompleteEvent unsubscribeComplete = new UnsubscribeFromChannelCompleteEvent();
        unsubscribeComplete.setOriginalSearchEventID(event.getEventID());
        Bus.post(unsubscribeComplete);
    }
}
