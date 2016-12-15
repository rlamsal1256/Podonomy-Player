package com.podonomy.podonomyplayer.service.cache;

import com.podonomy.podonomyplayer.event.Bus;
import com.podonomy.podonomyplayer.event.EventLogger;
import com.podonomy.podonomyplayer.service.TaskBase;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * This class/task is responsible for downloading an asset (image, html file, ect...) and store it
 * in the {@link Cache}.
 */
public class DownloadAssetTask extends TaskBase {
  private static final String TAG = "DownloadAssetTask";
  private static final int    MAX_CONNECTION_TIMEOUT = 5;  //we don't wait long trying to fetch assets.... only a few seconds...
  private static OkHttpClient client                 = null;

  private DownloadAssetEvent event = null;

  public DownloadAssetTask(DownloadAssetEvent event) {
    this.event = event;
    if (event == null || event.getAssetURL() == null)
      throw new IllegalArgumentException("A valid event with an asset URL must be provided.");

    if (client == null) {
      client = new OkHttpClient().newBuilder()
        .readTimeout(MAX_CONNECTION_TIMEOUT, TimeUnit.SECONDS)
        .build();
    }
  }

  @Override
  public void _run() {
    URL url = event.getAssetURL();

    try {
      //Before we incur the cost of downloading, let's check if this item is already in our cache
      if (Cache.get(url.toString()) != null)
        return;
    }catch (IOException i){}


    ////
    //Build a get query to fetch the item....
    Request request = new Request.Builder()
      .url(url)
      .get()
      .build();

    InputStream  responseData = null;
    ResponseBody body         = null;
    try {
      //Fetch the asset an put it in our cache...
      logger.debug(getClass().getSimpleName(), " fetching asset at ", url);
      Response response = client.newCall(request).execute();
      if (response == null || ! response.isSuccessful()) {
        logger.debug("Failed to load asset ", url, " Code: ", response == null ? "null" : response.code());
        return;
      }

      ////
      //Get the data for this asset and put it in the cache....
      body = response.body();
      responseData = body.byteStream();
      Cache.put(url.toString(), responseData, body.contentType().toString());
      responseData.close();
      body.close();

      ////
      //Notify the caller that the asset has been downloaded successfully.
//      AssetDownloadedEvent ade = new AssetDownloadedEvent(event.getEventID());
//      Bus.post(ade);
    }
    catch (IOException e) {
      EventLogger.getLogger().error(TAG, e, "Unable to download " + url.toString());
    }
    finally {
      if (responseData != null) {
        try {
          responseData.close();
        } catch (IOException io) {
        }
      }
      if (body != null)
        body.close();
    }
  }
}
