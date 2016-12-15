package com.podonomy.podonomyplayer.feedparser;

import java.util.Date;

/**
 * A FeedItem is what's produced by a {@link Feed} i.e. a {@link com.podonomy.podonomyplayer.dao.Episode}
 * in the podonomy nomenclature.
 */
public class FeedItem {
  private String author;
  private Date date;
  private String description;
  private String link;
  private String imageURL;
  private String title;
  private String subtitle;
  private String duration;
  private String mediaType;
  private String mediaSize;
  private String mediaLink;

  public String getAuthor() {
    // TODO Auto-generated method stub
    return this.author;
  }
  public String getDescription() {
    // TODO Auto-generated method stub
    return this.description;
  }
  public String getLink() {
    // TODO Auto-generated method stub
    return this.link;
  }
  public String getTitle() {
    // TODO Auto-generated method stub
    return this.title;
  }
  public void setAuthor(String author) {
    // TODO Auto-generated method stub
    this.author=author;

  }
  public void setDescription(String description) {
    // TODO Auto-generated method stub
    this.description=description;
  }
  public void setLink(String link) {
    // TODO Auto-generated method stub
    this.link=link;

  }
  public void setTitle(String title) {
    // TODO Auto-generated method stub
    this.title=title;

  }
  public String getImageURL() {
    return imageURL;
  }
  public void setImageURL(String imageURL) {
    this.imageURL = imageURL;
  }
  public String getDuration() {
    return duration;
  }
  public void setDuration(String duration) {
    this.duration = duration;
  }

  public String toString()
  {
    return "***written by :"+author+"/on "+date+"/at "+link+"/with title as:"+title+"***";
  }
  public String getSubtitle() {
    return subtitle;
  }
  public void setSubtitle(String subtitle) {
    this.subtitle = subtitle;
  }
  public Date getDate() {
    return date;
  }
  public void setDate(Date date) {
    this.date = date;
  }
  public String getMediaType() {
    return mediaType;
  }
  public void setMediaType(String mediaType) {
    this.mediaType = mediaType;
  }
  public String getMediaSize() {
    return mediaSize;
  }
  public void setMediaSize(String mediaSize) {
    this.mediaSize = mediaSize;
  }
  public String getMediaLink() {
    return mediaLink;
  }
  public void setMediaLink(String mediaLink) {
    this.mediaLink = mediaLink;
  }
}
