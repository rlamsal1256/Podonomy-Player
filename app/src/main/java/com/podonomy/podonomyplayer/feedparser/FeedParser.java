package com.podonomy.podonomyplayer.feedparser;

import com.podonomy.podonomyplayer.event.EventLogger;

import org.apache.commons.lang3.StringUtils;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Base class of all feed parsers.
 */
public abstract class FeedParser extends DefaultHandler{
  private static final String TAG = "FeedParser";
  protected HashSet<String> nameSpaces = new HashSet<>();

  protected FeedParser(String[] supportedNameSpaces){
    if (supportedNameSpaces == null || nameSpaces == null)
      return;

    for(String s: supportedNameSpaces)
      if (s != null)
        nameSpaces.add(s);
  }

  /**
   * Given a URL, this method will download the stream received from the URL and parse it in either
   * RSS or Atom format.  It returns a {@link Feed} object or null if it was unable to generate it.
   */
  public static Feed parse(URL url, OkHttpClient client) {
//    OkHttpClient client = new OkHttpClient().newBuilder()
//                              .connectTimeout(2, TimeUnit.SECONDS)
//                              .build();
    Request request = new Request.Builder().url(url).build();
    String response = null;
    Feed feed= null;

    Response  resp = null;
    try {
      resp = client.newCall(request).execute();
      response = resp.body().string();
      resp.body().close();
      resp = null;
      client = null;
    }
    catch (IOException e) {
      EventLogger.getLogger().error(TAG, e);
    }

    if (response == null )
      return null;

    feed = parse(new ByteArrayInputStream(response.getBytes()));
    return feed;
  }

  public static Feed parse(InputStream input) {
    SAXParser        parser   = null;
    SAXParserFactory factory  = SAXParserFactory.newInstance();
    FeedParser[]     handlers = new FeedParser[]{new RSSEpisodeParser(), new AtomEpisodeParser()};

    for(FeedParser p : handlers) {
      try {
        parser = factory.newSAXParser();

        if (parser == null)
          return null;

        parser.parse(input, p);
        Feed feed = p.getFeed();
        return feed;
      }
      catch(InvalidFeedFormatException i){
      }
      catch (ParserConfigurationException | SAXException | IOException e) {
        EventLogger.getLogger().error(TAG, e);
      }
    }
    return null;
  }

  /**
   * returns true if the given name space is within our supported namespaces
   */
  protected boolean isValidNS(String ns){
    if (ns == null || StringUtils.isBlank(ns) )
       return true;
    for(String nameSpace: nameSpaces){
      if (ns.length() < nameSpace.length() && StringUtils.startsWithIgnoreCase(nameSpace, ns))
        return true;
      if (StringUtils.startsWithIgnoreCase(ns, nameSpace))
        return true;
    }
    return false;
  }
  public abstract Feed getFeed();
}
