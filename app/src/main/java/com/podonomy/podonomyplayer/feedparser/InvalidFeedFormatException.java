package com.podonomy.podonomyplayer.feedparser;

import org.xml.sax.SAXException;

/**
 * Created by fgagn_000 on 3/21/2016.
 */
public class InvalidFeedFormatException extends SAXException {
  public InvalidFeedFormatException(String message) {
    super(message);
  }
}
