package com.podonomy.podonomyplayer.feedparser;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents an RSS/Atom feed.  Simple pojo file to hold the feed information as parsed. A feed is
 * equivalent to a podonomy {@com.podonomy.podonomyplayer.dao.Channel}.
 */
public class Feed {
  private String title;
  private String subtitle;
  private String link;
  private String description;
  private Date pubDate;
  private String generator;
  private String language;
  private FeedList feedList;
  private String author;
  private Image  image;
  private String ownerName;
  private String ownerEmail;
  private Set<String> categories = new HashSet<>();
  private String advisory;

  public FeedList getFeedList() {
    if (feedList == null)
      feedList = new FeedList();
    return feedList;
  }
  public void setFeedList(FeedList feedList) {
    this.feedList = feedList;
  }
  public String getTitle() {
    return title;
  }
  public void setTitle(String title) {
    this.title = title;
  }
  public String getLink() {
    return link;
  }
  public void setLink(String link) {
    this.link = link;
  }
  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    this.description = description;
  }
  public String getGenerator() {
    return generator;
  }
  public void setGenerator(String generator) {
    this.generator = generator;
  }
  public String getLanguage() {
    return language;
  }
  public void setLanguage(String language) {
    this.language = language;
  }
  public String toString() {
    return "title:" + this.getTitle() + "/link:" + this.getLink() + "/description:" + this.getDescription() + "/pubDate:" + this.getPubDate() +
    "/generator:" + this.getGenerator() + "/language:" + this.getLanguage() + "/feedList:" + this.getFeedList();
  }
  public String getAuthor() {
    return author;
  }
  public void setAuthor(String author) {
    this.author = author;
  }
  public String getSubtitle() {
    return subtitle;
  }
  public void setSubtitle(String subtitle) {
    this.subtitle = subtitle;
  }
  public Date getPubDate() {
    return pubDate;
  }
  public void setPubDate(Date pubDate) {
    this.pubDate = pubDate;
  }
  public Image getImage() {
    return image;
  }
  public void setImage(Image image) {
    this.image = image;
  }
  public String getOwnerName() {
    return ownerName;
  }
  public void setOwnerName(String ownerName) {
    this.ownerName = ownerName;
  }
  public String getOwnerEmail() {
    return ownerEmail;
  }
  public void setOwnerEmail(String ownerEmail) {
    this.ownerEmail = ownerEmail;
  }
  public Set<String> getCategories() {
    return categories;
  }
  public void addCategory(String s){
    categories.add(s);
  }
  public String getAdvisory() {
    return advisory;
  }
  public void setAdvisory(String advisory) {
    this.advisory = advisory;
  }

}
