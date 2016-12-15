package com.podonomy.podonomyplayer.service.cache;

import com.jakewharton.disklrucache.DiskLruCache;
import com.podonomy.podonomyplayer.BuildConfig;
import com.podonomy.podonomyplayer.event.EventLogger;
import com.podonomy.podonomyplayer.PlayerApplication;
import com.podonomy.podonomyplayer.dao.Configuration;
import com.podonomy.podonomyplayer.dao.ConfigurationDAO;
import com.podonomy.podonomyplayer.dao.DAO;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This class represents a cache of the channel and episode assets.  It uses an LRU cache so that
 * we don't use too much disk space and only keep the assets that have been used the most recently.
 */
public class Cache  {
  private static final String TAG = "Cache";
  private static DiskLruCache diskLruCache;

  public static class CacheItem{
    public byte[] content;
    public String contentType;
  }

  /**
   * Looks up key in the cache and returns an input stream containing the data for that url.  Returns null
   * if the key isn't found.
   */
  public static CacheItem get(String url) throws IOException {
    ensure();

    String key = getKey(url);
    DiskLruCache.Snapshot snapshot = diskLruCache.get(key);
    if (snapshot == null)
      return null;

    ByteArrayOutputStream bo = new ByteArrayOutputStream();
    InputStream           snapInput = null;
    try{
      CacheItem itm   = new CacheItem();
      itm.contentType = snapshot.getString(0);
      snapInput       = snapshot.getInputStream(1);

      IOUtils.copy(snapInput, bo);
      itm.content = bo.toByteArray();
      return itm;
    }
    finally {
      snapshot.close();
      bo.close();
      if (snapInput != null)
        snapInput.close();
    }
  }

  /**
   * Puts the given data in the for the provided url in the cache.
   */
  public static void put(String url, InputStream data, String contentType) throws IOException {
    ensure();
    String key = getKey(url);
    DiskLruCache.Editor editor = diskLruCache.edit(key);

    ////
    // Write the content type
    if (editor == null)
      return;
    OutputStream os = editor.newOutputStream(0);
    try {
      IOUtils.write(contentType.toCharArray(), os);
    }
    finally {
      os.close();
    }

    ////
    //Write the data...
    os = editor.newOutputStream(1);
    try {
      IOUtils.copy(data, os);
    }
    finally {
      os.close();
    }
    editor.commit();
  }

  /**
   * This method must be invoked when the system starts to start the cache.
   */
  public static void onStart(){
    Configuration config = ConfigurationDAO.getConfig(DAO.getRealm(PlayerApplication.getInstance().getAppContext()));
    File folder = new File(PlayerApplication.getInstance().getCacheDir(), "podonomy_cache");
    try {
      diskLruCache = DiskLruCache.open(folder, BuildConfig.VERSION_CODE, 2, config.getAssetCacheMaxSize());
    }
    catch(IOException io){
      EventLogger.getLogger().error(TAG, io, "Unable to open LRUDiskCache");
    }
  }

  /**
   * This method must be called to close the cache.
   */
  public static void onStop(){
    try {
      if (diskLruCache != null && !diskLruCache.isClosed())
        diskLruCache.close();
    }
    catch (IOException io) {
      EventLogger.getLogger().error(TAG, io, "Unable to close LRUDiskCache");
    }
    finally {
      diskLruCache = null;
    }
  }

  /**
   * Checks that this static class has been started (i.e. the {@link Cache#onStart} method has been invoked.
   */
  private static void ensure(){
    if (diskLruCache == null) {
      Error e = new Error("Attempt to access Cache before it has been started.");
      EventLogger.getLogger().error(TAG, e);
    }
  }

  /**
   * Given a key, convert it so that it matches the requirement imposed by DiskLRUCache (i.e.
   * matches this regular expression: "[a-z0-9_-]{1,64}").
   */
  private static String getKey(String key){
    if (key == null)
      return "null";

    StringBuilder k = new StringBuilder();
    for (int i = key.length() -1; i >= 0 && k.length() < 64; i--){
      char c = key.charAt(i);
      if ((c >= 'a' && c <= 'z' ) || (c >= '0' && c <= '9') || c == '_' || c == '-')
        k.append(c);
      else if (c >= 'A' && c <= 'Z')
        k.append(Character.toLowerCase(c));
      else
        k.append('-');
    }
    return k.toString();
  }
}
