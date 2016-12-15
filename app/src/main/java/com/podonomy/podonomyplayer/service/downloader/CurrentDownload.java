package com.podonomy.podonomyplayer.service.downloader;

import com.podonomy.podonomyplayer.dao.Episode;
import com.podonomy.podonomyplayer.event.DownloadEpisodeMediaEvent;
import com.thin.downloadmanager.DownloadRequest;

/**
 * Created by rlamsal on 6/27/2016.
 */

/**
 * Structure holding the data pertaining to an episode media being downloaded.
 */
public class CurrentDownload {
    public DownloadEpisodeMediaEvent event;
    public DownloadRequest request;
    public Episode episode;
    public boolean isInit = false;
    public int downloadId;
}

