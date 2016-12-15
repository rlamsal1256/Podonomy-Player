package com.podonomy.podonomyplayer.event;

import com.podonomy.podonomyplayer.dao.Episode;

/**
 * This event initiates the download cancellation of an episode's media (ie. it's audio/video file)
 */
public class DownloadEpisodeMediaCancelEvent extends UserEventBase {

    private Episode episode;

    public DownloadEpisodeMediaCancelEvent(Episode episode) {
        this.episode = episode;
    }

    public Episode getEpisode() {
        return episode;
    }

}
