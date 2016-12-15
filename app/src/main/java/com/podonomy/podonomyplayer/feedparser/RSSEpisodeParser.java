package com.podonomy.podonomyplayer.feedparser;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.trim;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Stack;


/**
 * This class parses an RSS formatted xml file/stream, so you can get a {@link Feed} object back.
 */
public class RSSEpisodeParser extends FeedParser {
  public static final String ROOT_ELEMENT = "rss";
  public static final String RSSCHANNEL="channel";
  public static final String TITLE ="title";
  public static final String LINK ="link";
  public static final String DESC ="description";
  public static final String PUBDATE ="PubDate";
  public static final String GENERATOR ="Generator";
  public static final String LANGUAGE ="Language";
  public static final String AUTHOR ="Author";
  public static final String ITEM = "item";
  public static final String CLOUD = "cloud";
  public static final String TEXTINPUT = "textinput";
  public static final String IMAGE = "image";
  public static final String URL   = "url";
  public static final String DURATION = "duration";
  public static final String ENCLOSURE = "enclosure";

  public static final String ITUNE_URI = "http://www.itunes.com/dtds/podcast-1.0.dtd";
  public static final String ITUNE_SUMMARY = "summary";
  public static final String ITUNE_AUTHOR = "author";
  public static final String ITUNE_ADVISORY = "explicit";
  public static final String ITUNE_CATEGORY = "category";
  public static final String ITUNE_SUBTITLE ="subtitle";
  public static final String ITUNE_OWNER = "owner";
  public static final String ITUNE_OWNER_NAME = "name";
  public static final String ITUNE_OWNER_EMAIL = "email";

  public RSSEpisodeParser(){
    super(new String[] {
      "http://purl.org/rss/1.0/modules/content",
      ITUNE_URI
    });
  }

  public static final SimpleDateFormat rfc822DateFormats[] = new SimpleDateFormat[] {
    new SimpleDateFormat("EEE, d MMM yy HH:mm:ss z"),
    new SimpleDateFormat("EEE, d MMM yy HH:mm z"),
    new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z"),
    new SimpleDateFormat("EEE, d MMM yyyy HH:mm z"),
    new SimpleDateFormat("d MMM yy HH:mm z"),
    new SimpleDateFormat("d MMM yy HH:mm:ss z"),
    new SimpleDateFormat("d MMM yyyy HH:mm z"),
    new SimpleDateFormat("d MMM yyyy HH:mm:ss z"),
  };

  private StringBuilder text         = new StringBuilder();
  private Feed          feed         = new Feed();
  private FeedItem      currentItem  = null;
  private Image         currentImage = null;
  private Stack<String> elementStack = new Stack<>();
  private boolean       firstElement = true;

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
    text.setLength(0);
    if (uri != null && ! isValidNS(uri))
      return;

    if (firstElement && ! ROOT_ELEMENT.equalsIgnoreCase(localName))
      throw new InvalidFeedFormatException("Expected " + ROOT_ELEMENT + " root element, received " + localName + " Can't handle.");

    firstElement = false;
    String currentState = null;
    if (elementStack.size() > 0)
      currentState = elementStack.peek();

    if (ITEM.equalsIgnoreCase(localName)){
      currentItem = new FeedItem();
      feed.getFeedList().add(currentItem);
    }
    else if (ITEM.equalsIgnoreCase(currentState)){
      /**   <ITEM ..> sub-elements...
       */
      if (IMAGE.equalsIgnoreCase(localName)){
        String url = attributes.getValue("href");
        currentItem.setImageURL(url);
      }
      else if (ENCLOSURE.equalsIgnoreCase(localName))  {
        String url    = attributes.getValue("url");
        String length = attributes.getValue("length");
        String type   = attributes.getValue("type");
        if (!isBlank(url))
          currentItem.setMediaLink(url);
        if (! isBlank(length))
          currentItem.setMediaSize(length);
        if (! isBlank(type))
          currentItem.setMediaType(type);
      }
    }
    else if (RSSCHANNEL.equalsIgnoreCase(currentState)) {
      /**   <RSS ..> sub-elements...
       */
      if (IMAGE.equalsIgnoreCase(localName)){
        currentImage = new Image();
        feed.setImage(currentImage);
        String url = attributes.getValue("href");
        if (! isBlank(url))
          currentImage.setUrl(url);
      }
      else if (ITUNE_CATEGORY.equalsIgnoreCase(localName)){
        String cat = attributes.getValue("text");
        if (! isBlank(cat))
          feed.addCategory(cat);
      }
    }

    elementStack.push(localName);
  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    if (uri != null && ! isValidNS(uri))
      return;

    String top = elementStack.pop();
    if (top != null && ! top.equalsIgnoreCase(localName)){
      //throw new InvalidXMLDocument();  //close tag does not match opened tag
    }

    String currentState = null;
    if (elementStack.size() > 0)
      currentState = elementStack.peek();

    if (ROOT_ELEMENT.equalsIgnoreCase(currentState) || RSSCHANNEL.equalsIgnoreCase(currentState))
      processRSSElement(uri, localName, qName, trim(text.toString()));
    else if (IMAGE.equalsIgnoreCase(currentState))
      processImageElement(uri, localName, qName, trim(text.toString()));
    else if (ITEM.equalsIgnoreCase(currentState))
      processItemElement(uri, localName, qName, trim(text.toString()));
    else if (ITUNE_OWNER.equalsIgnoreCase(currentState)){
      if (ITUNE_OWNER_NAME.equalsIgnoreCase(localName))
        feed.setOwnerName(trim(text.toString()));
      else if (ITUNE_OWNER_EMAIL.equalsIgnoreCase(localName))
        feed.setOwnerEmail(trim(text.toString()));
    }

    if (ITEM.equalsIgnoreCase(localName))
      currentItem = null;
    else if (IMAGE.equalsIgnoreCase(localName))
      currentImage = null;

    text.setLength(0);
  }

  /**
   * While Parsing the XML file, if extra characters like space or enter Character
   * are encountered then this method is called. If you don't want to do anything
   * special with these characters, then you can normally leave this method blank.
   */
  public void characters (char buf [], int offset, int len) {
    text.append(buf, offset, len);
  }

  private String getLastKey()
  {
    int keySize = elementStack.size();
    if (keySize==0)
      return null;
    return (String) elementStack.get(keySize-1);
  }

  public Feed getFeed(){
    return feed;
  }

  private void processRSSElement(String uri, String element, String qName, String txt) {
    if (TITLE.equalsIgnoreCase(element))
      feed.setTitle(txt);
    else if (LINK.equalsIgnoreCase(element))
      feed.setLink(txt);
    else if (DESC.equalsIgnoreCase(element))
      feed.setDescription(txt);
    else if (PUBDATE.equalsIgnoreCase(element))
      feed.setPubDate(parseDate(txt));
    else if (AUTHOR.equalsIgnoreCase(element) || ITUNE_AUTHOR.equalsIgnoreCase(element))
      feed.setAuthor(trim(text.toString()));
    else if (LANGUAGE.equalsIgnoreCase(element))
      feed.setLanguage(txt);
    else if (ITUNE_SUMMARY.equalsIgnoreCase(element) && isBlank(feed.getDescription()))
      feed.setDescription(txt);
    else if (ITUNE_SUBTITLE.equalsIgnoreCase(element))
      feed.setSubtitle(txt);
    else if (ITUNE_ADVISORY.equalsIgnoreCase(element))
      feed.setAdvisory(txt);
  }

  private Date parseDate(String txt){
    Date dt = null;
    for (SimpleDateFormat df : rfc822DateFormats) {
      try {
        dt = df.parse(txt);
        break;
      } catch (ParseException e) {
      }
    }
    return dt;
  }

  private void processItemElement(String uri, String element, String qName, String txt)  {
    if (TITLE.equalsIgnoreCase(element))
      currentItem.setTitle(txt);
    else if (LINK.equalsIgnoreCase(element))
      currentItem.setLink(txt);
    else if (DESC.equalsIgnoreCase(element))
      currentItem.setDescription(txt);
    else if (AUTHOR.equalsIgnoreCase(element))
      currentItem.setAuthor(txt);
    else if (PUBDATE.equalsIgnoreCase(element))
      currentItem.setDate(parseDate(txt));
    else if (DURATION.equalsIgnoreCase(element))
      currentItem.setDuration(txt);
    else if (ITUNE_SUMMARY.equalsIgnoreCase(element) && isBlank(currentItem.getDescription()))
      currentItem.setDescription(txt);
    else if (ITUNE_AUTHOR.equalsIgnoreCase(element))
      currentItem.setAuthor(txt);
    else if (ITUNE_SUBTITLE.equalsIgnoreCase(element))
      currentItem.setSubtitle(txt);
  }

  private void processImageElement(String uri, String element, String qName, String txt)  {
    if (URL.equalsIgnoreCase(element))
      currentImage.setUrl(txt);
    else if (TITLE.equalsIgnoreCase(element))
      currentImage.setTitle(txt);
    else if (LINK.equalsIgnoreCase(element))
      currentImage.setLink(txt);
  }
}

