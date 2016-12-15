package com.podonomy.podonomyplayer.event;

import com.podonomy.podonomyplayer.dao.Episode;

/**
 *
 * This event tracks the download progress of an actual episode's media (ie. it's audio/video file).
 *
 */
public class DownloadEpisodeMediaProgressEvent extends EventBase {

    private Episode episode;

    public DownloadEpisodeMediaProgressEvent(Episode episode) {
        this.episode = episode;
    }

    public Episode getEpisode() {
        return episode;
    }

}
