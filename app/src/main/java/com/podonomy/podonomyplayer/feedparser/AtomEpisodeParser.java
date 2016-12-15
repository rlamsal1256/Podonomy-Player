package com.podonomy.podonomyplayer.feedparser;

import org.apache.commons.lang3.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Stack;


/**
 * This class parses an Atom formatted xlm file/stream, so you can get a {@link Feed} object back.
 */
public class AtomEpisodeParser extends  FeedParser{
  public static final String ROOT_ELEMENT = "feed";
  public static final String ENTRY = "entry";
  //public static final String CHANNEL="channel";
  public static final String TITLE = "title";
  public static final String SUBTITLE = "subtitle";
  public static final String LINK = "link";
  public static final String UPDATED = "updated";
  public static final String PUBLISHED = "published";
  public static final String ID = "ID";


  private StringBuilder text = new StringBuilder();
  private Feed feed = new Feed();
  private FeedItem currentItem = null;
  private Stack<String> elementStack = new Stack<>();
  private boolean firstElement = true;

  public AtomEpisodeParser(){
    super(new String[] {

    });
  }

  @Override
  public void startDocument() throws SAXException {
    super.startDocument();
  }

  @Override
  public void endDocument() throws SAXException {
    super.endDocument();
  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
    elementStack.push(localName);

    if (firstElement && !ROOT_ELEMENT.equalsIgnoreCase(localName))
      throw new InvalidFeedFormatException("Expected " + ROOT_ELEMENT + " root element, received " + localName + " Can't handle.");

    firstElement = false;

    if (ENTRY.equalsIgnoreCase(localName)) {
      currentItem = new FeedItem();
      feed.getFeedList().add(currentItem);
    }
    else if (LINK.equalsIgnoreCase(localName)){
      String href = attributes.getValue("href");
      String rel  = attributes.getValue("rel");
      if (rel != null && href != null && rel.equalsIgnoreCase("related"))
        feed.setLink(href);
    }
  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    String top = elementStack.pop();
    if (top != null && !top.equalsIgnoreCase(localName)) {
      //throw new InvalidXMLDocument();  //close tag does not match opened tag
    }

    if (top.equalsIgnoreCase(TITLE)) {
      if (currentItem != null)
        currentItem.setTitle(StringUtils.trim(text.toString()));
      else
        feed.setTitle(StringUtils.trim(text.toString()));
    }
    else if (SUBTITLE.equalsIgnoreCase(localName)){
      if (currentItem != null)
        currentItem.setSubtitle(StringUtils.trim(text.toString()));
      else
        feed.setSubtitle(StringUtils.trim(text.toString()));
    }
    else if(UPDATED.equalsIgnoreCase(localName)){
      Date dt = parseDate(StringUtils.trim(text.toString()));
      if (dt != null)
        feed.setPubDate(dt);
    }
    else if (PUBLISHED.equalsIgnoreCase(localName)){
      Date dt = parseDate(StringUtils.trim(text.toString()));
      if (dt != null && currentItem != null)
        currentItem.setDate(dt);
    }

    text.setLength(0);
  }

  /**
   * While Parsing the XML file, if extra characters like space or enter Character
   * are encountered then this method is called. If you don't want to do anything
   * special with these characters, then you can normally leave this method blank.
   */
  public void characters(char buf[], int offset, int len) throws SAXException {
    text.append(buf, offset, len);
  }

  private String getLastKey() {
    int keySize = elementStack.size();
    if (keySize == 0)
      return null;
    return (String) elementStack.get(keySize - 1);
  }

  public Feed getFeed() {
    return feed;
  }


  private Date parseDate(String s){
    try {
      Date dt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(s);
      return dt;
    }
    catch(ParseException p){
      return null;
    }
  }
}
