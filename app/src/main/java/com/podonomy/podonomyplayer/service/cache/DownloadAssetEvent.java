package com.podonomy.podonomyplayer.service.cache;

import com.podonomy.podonomyplayer.event.EventBase;

import java.net.URL;

/**
 * This event is to request that an asset be downloaded and stored in the asset cache.
 */
public class DownloadAssetEvent extends EventBase {
  private URL assetURL = null;

  public DownloadAssetEvent(URL assetURL){
    this.assetURL = assetURL;
  }
  public URL getAssetURL() {
    return assetURL;
  }

}
