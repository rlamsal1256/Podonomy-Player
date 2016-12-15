package com.podonomy.podonomyplayer.event;

import com.podonomy.podonomyplayer.dao.Episode;

/**
 * This event informs the activity when an episode's media has been checked (ie. it's audio/video file).
 */
public class DownloadingEpisodeMediaCheckedEvent  extends UserEventBase {

    private Episode episode;

    public DownloadingEpisodeMediaCheckedEvent(Episode episode) {
        this.episode = episode;
    }

    public Episode getEpisode() {
        return episode;
    }
}
