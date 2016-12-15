package com.podonomy.podonomyplayer.event;

/**
 *
 * This event informs a download completion of an actual episode's media (ie. it's audio/video file).
 *
 */
public class DownloadEpisodeMediaCompleteEvent extends EventBase{
    private String episodeID = null;

    public DownloadEpisodeMediaCompleteEvent(String episodeID) {
        this.episodeID = episodeID;
    }

    public String getEpisodeID() {
        return episodeID;
    }
}
