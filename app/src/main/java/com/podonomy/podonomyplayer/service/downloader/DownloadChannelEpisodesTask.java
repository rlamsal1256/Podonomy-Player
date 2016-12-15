package com.podonomy.podonomyplayer.service.downloader;

import com.podonomy.podonomyplayer.dao.Category;
import com.podonomy.podonomyplayer.dao.Channel;
import com.podonomy.podonomyplayer.dao.ChannelDAO;
import com.podonomy.podonomyplayer.dao.DAO;
import com.podonomy.podonomyplayer.dao.Episode;
import com.podonomy.podonomyplayer.dao.EpisodeDAO;
import com.podonomy.podonomyplayer.event.Bus;
import com.podonomy.podonomyplayer.event.DownloadChannelEpisodesEvent;
import com.podonomy.podonomyplayer.feedparser.Feed;
import com.podonomy.podonomyplayer.feedparser.FeedItem;
import com.podonomy.podonomyplayer.feedparser.FeedParser;
import com.podonomy.podonomyplayer.service.HttpClient;
import com.podonomy.podonomyplayer.service.TaskBase;

import java.net.URL;
import java.util.List;

import okhttp3.OkHttpClient;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * This class is responsible for downloading the epidodes (descripiton) for a given channel
 * as specified in the {@link com.podonomy.podonomyplayer.event.DownloadChannelEpisodesEvent}
 */
public class DownloadChannelEpisodesTask extends TaskBase<DownloadChannelEpisodesEvent> {
  private static final String TAG = "DownloadChannelEpisodesTask";

  @Override
  public void _run() {
    OkHttpClient client = HttpClient.getClient();
    if (client == null)
      return;

    if (isBlank(event.getChannelID()))
      return;

    Channel channel = ChannelDAO.find(realm, event.getChannelID());
    if (channel == null || isBlank(channel.getFeedURL()))
      return;

    try {
      ////
      // Download the feed
      logger.debug("Parsing feed ", channel.getFeedURL());
      Feed feed = FeedParser.parse(new URL(channel.getFeedURL()), client);
      if (feed == null) {
        logger.error("Loading the feed at ", channel.getFeedURL(), " failed to return a feed object");
        Bus.post(event.getFailureResponseEvent("Failed to load "+channel.getFeedURL()));
        return;
      }

      ////
      //Let's update the channel's information with the feed's data which is likely more accurate
      DAO.beginTransaction(realm);

      if (! isBlank(feed.getDescription()))
        channel.setDescription(feed.getDescription());
      if (! isBlank(feed.getAuthor()))
        channel.setAuthor(feed.getAuthor());
      if (! isBlank(feed.getTitle()))
        channel.setName(feed.getTitle());
      if (! isBlank(feed.getSubtitle()))
        channel.setSubTitle(feed.getSubtitle());
      if (feed.getImage() != null && !isBlank(feed.getImage().getLink()))
        channel.setThumbnailURL(feed.getImage().getUrl());
      if (! feed.getCategories().isEmpty()){
        for (String category: feed.getCategories()){
          Category category1 = new Category();
          category1.setName(category);
          channel.getCategories().add(category1);
        }
      }

      DAO.commitTransaction(realm);

      ////
      //Create the episode object in the database...
      logger.debug(TAG, feed.getFeedList().size(), " episodes to process.");
      List<Episode> existingEpisodes = EpisodeDAO.forChannel(realm, channel.getID());
      int count = 0;
      for (FeedItem feedItem : feed.getFeedList()){
        if (count++ > event.getMaxDownload())
          break;

        logger.debug("Processing ", feed.getLink());

        if (findFeed(existingEpisodes, feedItem.getLink()) != null)
          continue;

        DAO.beginTransaction(realm);
        Episode episode = feedItemToEpisode(feedItem);
        if (episode == null || isBlank(episode.getMediaFileURL())) { //only keep valid episodes with a media file.
          DAO.rollbackTransaction(realm);
          continue;
        }
        episode.setId(channel.getID() + "/" + episode.getName());
        episode.setChannel(channel);
        DAO.commitTransaction(realm);
      }

      ////
      // Done, notify the caller.
      Bus.post(event.getResponseEvent());
    }
    catch (Exception e) {
      logger.error(TAG, e, "Download of url ", channel.getFeedURL(), " failed.");
      Bus.post(event.getFailureResponseEvent(e.getMessage()));
    }


  }

  private Episode findFeed(List<Episode> episodes, String url){
    for(Episode e : episodes)
      if (e.getEpisodeURL() != null && e.getEpisodeURL().equals(url))
        return e;
    return null;
  }

  /**
   * Converts a {@link FeedItem} to a {@link Episode} object and returns it.  The returned object
   * is NOT attached to the database and NOT ready for saving.
   * Returns null if unable to create the Episode object.
   */
  private Episode feedItemToEpisode(FeedItem itm){
    if (itm == null || isBlank(itm.getLink()))
      return null;
    Episode episode = EpisodeDAO.findOrCreate(realm, itm.getLink());
    episode.setAuthor(itm.getAuthor());
    episode.setPublicationDate(itm.getDate());
    episode.setDescription(itm.getDescription());
    episode.setEpisodeURL(itm.getLink());
    episode.setThumbnailURL(itm.getImageURL());
    episode.setName(itm.getTitle());
    episode.setSubTitle(itm.getSubtitle());
    episode.setMediaFileURL(itm.getMediaLink());
    episode.setFileType(itm.getMediaType());
    episode.setDurationInSeconds(itm.getDuration());
    return episode;
  }
}
