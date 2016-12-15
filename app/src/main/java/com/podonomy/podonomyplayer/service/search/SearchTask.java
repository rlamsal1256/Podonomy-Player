package com.podonomy.podonomyplayer.service.search;
import com.podonomy.podonomyplayer.dao.Category;
import com.podonomy.podonomyplayer.dao.CategoryDAO;
import com.podonomy.podonomyplayer.dao.Channel;
import com.podonomy.podonomyplayer.dao.ChannelDAO;
import com.podonomy.podonomyplayer.dao.DAO;
import com.podonomy.podonomyplayer.dao.Keyword;
import com.podonomy.podonomyplayer.dao.KeywordDAO;
import com.podonomy.podonomyplayer.dao.SearchQuery;
import com.podonomy.podonomyplayer.dao.Subscriber;
import com.podonomy.podonomyplayer.dao.SubscriberDAO;
import com.podonomy.podonomyplayer.event.Bus;
import com.podonomy.podonomyplayer.event.SearchEvent;
import com.podonomy.podonomyplayer.service.HttpClient;
import com.podonomy.podonomyplayer.service.TaskBase;
import com.podonomy.podonomyplayer.service.cache.DownloadAssetEvent;
import com.podonomy.podonomyplayer.feedparser.Feed;
import com.podonomy.podonomyplayer.feedparser.FeedParser;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import io.realm.Realm;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class SearchTask extends TaskBase<SearchEvent> implements Runnable{
  private static final String TAG = "SearchTask";
  private final String SEARCH_SERVER = "http://addictpodcast.com/ws/php/v2.32/searchpodcast.php";

  @Override
  public void _run() {
    Subscriber  subscriber = SubscriberDAO.getCurrentSubscriber(realm);
    SearchQuery query      = subscriber.getLastSearchQuery();

    if (query == null)
      return;

    logger.debug(TAG, " performing search for request ", event.getEventID());

    try {
      // Submit the query to the server and get the json result
      String jsonStr    = this.fetchQueryResult(query);
      JSONObject json   = new JSONObject(jsonStr);

      JSONArray array = json.getJSONArray("results");
      for (int i = 0; i < array.length(); i++){
        JSONObject obj = array.getJSONObject(i);
        DAO.beginTransaction(realm);
        Channel channel = resultToChannel(obj, realm);
        if (channel != null)
          query.getChannelsFound().add(channel);
        DAO.commitTransaction(realm);
      }
    }
    catch (Exception e) {
      DAO.rollbackTransaction(realm);
      logger.error(TAG, e);
    }
    logger.debug(TAG, "done searching.");
    SearchCompleteEvent searchComplete = new SearchCompleteEvent();
    searchComplete.setQuery(query);
    searchComplete.setOriginalSearchEventID(event.getEventID());
    Bus.post(searchComplete);

//    for (Channel channel : query.getChannelsFound())
//      fetchChannelDetails(channel, httpClient);
  }

  ////////
  // Given a query, send it to the server and return the results as a string
  protected String fetchQueryResult(SearchQuery query ) throws IOException {
    /////
    // Build the request to send to PA
    FormBody body = new FormBody.Builder().add("query", query.getKeywords())
            .add("dateFilter", "0")
            .build();
    Request request = new Request.Builder()
            .url(SEARCH_SERVER)
            .post(body)
            .build();

    //Send the request expecting a JSON response back....
    logger.debug(TAG, "Submitting search request to ", SEARCH_SERVER);
    OkHttpClient httpClient = HttpClient.getClient();
    Response     response   = httpClient.newCall(request).execute();
    if (! response.isSuccessful()) {
      logger.error(TAG, "Received error ", response.code(), " when contacting PA search.");
      return "";
    }

    String result = response.body().string();
    response.body().close();
    return result;
  }


  /**
   * Given a JSON feed data, this will build an actual Channel object
   * and return it.  If the channel does not already exists in the database,
   * it is created.
   */
  private Channel resultToChannel(JSONObject obj, Realm realm){
    if (obj == null || realm == null)
      return null;

    try {

      String id = "PA-" + obj.getInt("id");
      logger.debug(TAG, "ID:", id);
      Channel channel = ChannelDAO.find(realm, id);

      if (channel != null)
        return channel;

      channel = ChannelDAO.nnew(realm);
      channel.setID(id);
      channel.setName(obj.getString("name"));
      channel.setFeedURL(obj.getString("url"));
      channel.setAuthor(obj.getString("author"));
      channel.setThumbnailURL(obj.getString("thumbnail"));
      channel.setDescription(obj.getString("description"));
      channel.setLanguage(obj.getString("language"));
      channel.getEx().setFormat(obj.getString("type"));

      long lastPubDate   = obj.getLong("lastPublicationDate");
      channel.setPublicationDate(new Date(lastPubDate/1000));

      //int    nbEpisodes  = obj.getInt("episodeNb");

      //Process keywords...
      String keywordStr = obj.getString("keywords");
      if (keywordStr != null) {
        for (String s : keywordStr.split(",")) {
          if (! StringUtils.isBlank(s) ) {
            Keyword keyword = KeywordDAO.findOrCreate(realm, s);
            channel.getKeywords().add(keyword);
          }
        }
      }

      //Process categories
      JSONArray categories = obj.getJSONArray("categories");
      if (categories != null){
        for(int i = 0; i < categories.length(); i++){
          JSONObject jsonCategory = categories.getJSONObject(i);
          if (jsonCategory != null){
            String name_en = jsonCategory.getString("name_en");
            if (! StringUtils.isBlank(name_en)) {
              Category category = CategoryDAO.findOrCreate(realm, name_en);
              channel.getCategories().add(category);
            }
          }
        }
      }

      return channel;
    }
    catch(JSONException j){
      logger.error(TAG, j);
    }
    return null;
  }

  /**
   * This method fetches the channel details directly from its feed.
   */
  private void fetchChannelDetails(Channel channel, OkHttpClient client)  {
    try {
     logger.debug(TAG, "Fetching feed url:", channel.getFeedURL());
      Feed feed = FeedParser.parse(new URL(channel.getFeedURL()), client);
      if (feed != null) {
//        channel.setID(feed.getLink());
        if (!StringUtils.isBlank(channel.getSiteURL()))
          Bus.post(new DownloadAssetEvent(new URL(channel.getSiteURL())));
        if (!StringUtils.isBlank(channel.getThumbnailURL()))
          Bus.post(new DownloadAssetEvent(new URL(channel.getThumbnailURL())));
      }
    }
    catch (MalformedURLException e) {
      e.printStackTrace();
    }
  }

}
