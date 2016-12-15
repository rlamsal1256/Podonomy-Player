package com.podonomy.podonomyplayer.service;

import com.podonomy.podonomyplayer.dao.Category;
import com.podonomy.podonomyplayer.dao.Channel;
import com.podonomy.podonomyplayer.dao.Episode;
import com.podonomy.podonomyplayer.dao.EpisodeDAO;
import com.podonomy.podonomyplayer.dao.PlayList;
import com.podonomy.podonomyplayer.dao.PlayListDAO;
import com.podonomy.podonomyplayer.event.DownloadEpisodeMediaCompleteEvent;

import java.io.File;
import java.util.List;

/**
 * This task is to be invoked when a media file has been successfully downloaded.  It will add the downloaded episode
 * to the "Downloaded" playlist.
 */
public class AddToDownloadedPlaylistTask extends TaskBase<DownloadEpisodeMediaCompleteEvent> {
    private final String TAG = "AddToDownloadedPlaylistTask";

    @Override
    public void _run() {
        //get the "DownloadedPlaylist" from the database.
        PlayList downloadedPlaylist = PlayListDAO.getDownloadedPlaylist(super.realm);
        if (downloadedPlaylist == null) {
            super.logger.error(TAG, "No DownloadedPlaylist found.");
            abort();
            return;
        }

        //fetch the episode that was just downloaded
        Episode episode = EpisodeDAO.find(realm, event.getEpisodeID());
        if (episode == null) {
            super.logger.error(TAG, "Episode not found in the DB.");
            abort();
            return;
        }

        //get channel from episode
        Channel channel = episode.getChannel();

        //get categories from channel
        List<Category> categories = channel.getCategories();

        //for all categories, create a playlist if the playlist hasnt already been created
        realm.beginTransaction();
        PlayList playList = null;
        for (Category category : categories) {
            if (!playListExists(category.getName())) {
                playList = PlayListDAO.nnew(realm);
                playList.setName(category.getName());

                //add the episode within that playlist
                playList.getContent().add(episode);
            }
        }

        //Next add the episode to it and commit and save that.
        downloadedPlaylist.getContent().add(episode);
        realm.commitTransaction();
    }

    private boolean playListExists(String playListName) {
        boolean exixts = false;
        for (PlayList playList : PlayListDAO.getPlaylists(realm)) {
            if (playList.getName().equals(playListName)) {
                exixts = true;
            }
        }
        return exixts;
    }

    /**
     * Aborting means that we were not able to add the episode to the playslist.  In this case, we delete the episode
     * and its media from the system so no media files are taking disk space yet the user can never see them.
     */
    protected void abort() {
        try {
            Episode episode = EpisodeDAO.find(realm, event.getEpisodeID());
            if (episode == null)
                return;
            if (episode.getFileLocation() != null) {  //a media file was attempted to be downloaded
                File file = new File(episode.getFileLocation());
                if (file.exists())  //if the file exists, we must delete is since we were not able to add it to the playlist
                    file.delete();
            }
        } catch (Exception e) {
            super.logger.error(TAG, e, "Unable to delete media file.");
        }
    }

//  /**
//   * Fetch the episode object from the database corresponding to this event.  Returns null if not found.
//   */
//  protected Episode getEpisode() {
//    DownloadEpisodeMediaEvent dlEpisodeMediaEvent = (DownloadEpisodeMediaEvent) super.event.getOriginalEvent();
//    if (dlEpisodeMediaEvent == null)
//      return null;
//    Episode episode = EpisodeDAO.find(super.realm, dlEpisodeMediaEvent.getEpisodeID());
//    return episode;
//  }
}
