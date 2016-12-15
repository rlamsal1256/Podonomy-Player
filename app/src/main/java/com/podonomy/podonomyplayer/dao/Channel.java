package com.podonomy.podonomyplayer.dao;

import org.apache.commons.lang3.StringUtils;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * Represents a Channel/Podcast/Videocast.  A channel produces episodes....
 */
public class Channel extends RealmObject {
  @PrimaryKey
  private String ID;
  private String name;
  private Publisher publisher;
  private String siteURL;
  private String paymentAddressLine1;
  private String paymentAddressLine2;
  private String paymentCity;
  private String paymentZip;
  private String paymentState;
  private String paymentCountry;
  private String paymentPhone;
  private String address;
  private Rating rating;
  private String language;
  private String author;
  private String feedURL;
  private Date publicationDate;
  private RealmList<Category> categories = new RealmList<>();
  private RealmList<Keyword> keywords = new RealmList<>();
  private byte format;  //see Episode.FORMAT_... for values
  private String thumbnailURL;
  private Thumbnail thumbnail;
  private String copyright;
  private String webmaster;
  private String description;
  private String subTitle;
  private String ownerName;
  private String ownerEmail;
  private boolean verified;
  private boolean subscribed;
  private RealmList<Setting> __settings = new RealmList<>();

  @Ignore
  private byte ex;

  /**
   * The unique ID of this channel.
   */
  public String getID() {
    return ID;
  }
  public void setID(String ID) {
    this.ID = ID;
  }

  /**
   * The name of this channel.
   * @return
   */
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }

  /**
   * The publisher of this channel.
   */
  public Publisher getPublisher() {
    return publisher;
  }
  public void setPublisher(Publisher publisher) {
    this.publisher = publisher;
  }

  /**
   * The url for this channel.  This is the website of the channel, not its RSS feed url.
   * This may be null.
   */
  public String getSiteURL() {
    return siteURL;
  }
  public void setSiteURL(String siteURL) {
    this.siteURL = siteURL;
  }
  public String getPaymentAddressLine1() {
    return paymentAddressLine1;
  }
  public void setPaymentAddressLine1(String paymentAddressLine1) {
    this.paymentAddressLine1 = paymentAddressLine1;
  }
  public String getPaymentAddressLine2() {
    return paymentAddressLine2;
  }
  public void setPaymentAddressLine2(String paymentAddressLine2) {
    this.paymentAddressLine2 = paymentAddressLine2;
  }
  public String getPaymentCity() {
    return paymentCity;
  }
  public void setPaymentCity(String paymentCity) {
    this.paymentCity = paymentCity;
  }
  public String getPaymentZip() {
    return paymentZip;
  }
  public void setPaymentZip(String paymentZip) {
    this.paymentZip = paymentZip;
  }
  public String getPaymentState() {
    return paymentState;
  }
  public void setPaymentState(String paymentState) {
    this.paymentState = paymentState;
  }
  public String getPaymentCountry() {
    return paymentCountry;
  }
  public void setPaymentCountry(String paymentCountry) {
    this.paymentCountry = paymentCountry;
  }
  public String getPaymentPhone() {
    return paymentPhone;
  }
  public void setPaymentPhone(String paymentPhone) {
    this.paymentPhone = paymentPhone;
  }
  public String getAddress() {
    return address;
  }
  public void setAddress(String address) {
    this.address = address;
  }

  /**
   * The rating for this channel.
   * This may be null.
   */
  public Rating getRating() {
    return rating;
  }
  public void setRating(Rating rating) {
    this.rating = rating;
  }

  /**
   * Returns the language in which this channel is in.  The language is what is parsed from the channel
   * feed and thus should match the standard for language, such as "en-US".
   * This may be null.
   */
  public String getLanguage() {
    return language;
  }
  public void setLanguage(String language) {
    this.language = language;
  }

  /**
   * Returns the author of the channel as received from its rss feed.
   * This may be null.
   */
  public String getAuthor() {
    return author;
  }
  public void setAuthor(String author) {
    this.author = author;
  }

  /**
   * Returns the URL where the channel rss feed can be reached.
   * This may be null.
   */
  public String getFeedURL() {
    return feedURL;
  }
  public void setFeedURL(String feedURL) {
    this.feedURL = feedURL;
  }

  /**
   * Returns the date when this channel last published.
   * This may be null.
   */
  public Date getPublicationDate() {
    return publicationDate;
  }
  public void setPublicationDate(Date publicationDate) {
    this.publicationDate = publicationDate;
  }

  /**
   * Returns the list of categories to which this channel belongs to.
   * @return - a list of Category
   */
  public RealmList<Category> getCategories() {
    return categories;
  }
  public void setCategories(RealmList<Category> categories) {
    this.categories = categories;
  }

  /**
   * Returns the list of keywords tagged to the channel.
   * @return - a list of Keywords.
   */
  public RealmList<Keyword> getKeywords() {
    return keywords;
  }
  public void setKeywords(RealmList<Keyword> keywords) {
    this.keywords = keywords;
  }
  /**
   * Returns the data format used when this channel publishes episodes.  See {@Episode.FORMAT_AUDIO} / {@Episode.FORMAT_VIDEO}.
   */
  public byte getFormat() {
    return format;
  }
  public void setFormat(byte format) {
    this.format = format;
  }

  /**
   * Returns the URL where the channel thumbnail image can be downloaded.
   */
  public String getThumbnailURL() {
    return thumbnailURL;
  }
  public void setThumbnailURL(String thumbnailURL) {
    this.thumbnailURL = thumbnailURL;
  }

  /**
   * Returns the copyright notice for this channel.
   * This may be null.
   */
  public String getCopyright() {
    return copyright;
  }
  public void setCopyright(String copyright) {
    this.copyright = copyright;
  }

  /**
   * Returns the webmaster's address for this channel.
   * This may be null.
   */
  public String getWebmaster() {
    return webmaster;
  }
  public void setWebmaster(String webmaster) {
    this.webmaster = webmaster;
  }

  /**
   * Returns the channel's description as received from the channel feed.
   * This may be null.
   */
  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Returns the channel's subtitle.
   * This may be null.
   */
  public String getSubTitle() {
    return subTitle;
  }
  public void setSubTitle(String subTitle) {
    this.subTitle = subTitle;
  }

  /**
   * Return the channel's owner name.
   * This may be null.
   */
  public String getOwnerName() {
    return ownerName;
  }
  public void setOwnerName(String ownerName) {
    this.ownerName = ownerName;
  }

  /**
   * Returns the channel's owner email address.
   * This may be null.
   */
  public String getOwnerEmail() {
    return ownerEmail;
  }
  public void setOwnerEmail(String ownerEmail) {
    this.ownerEmail = ownerEmail;
  }
  public boolean isVerified() {
    return verified;
  }
  public void setVerified(boolean verified) {
    this.verified = verified;
  }

  /**
   * Returns true if the current user has subscribed to this channel, false otherwise.
   */
  public boolean isSubscribed() {
    return subscribed;
  }
  public void setSubscribed(boolean subscribed) {
    this.subscribed = subscribed;
  }
  public Thumbnail getThumbnail() {
    return thumbnail;
  }
  public void setThumbnail(Thumbnail thumbnail) {
    this.thumbnail = thumbnail;
  }

  /**
   * Consider this private.  User @{Channel#getEx()#getSetting} instead.
   */
  public RealmList<Setting> get__settings() {
    return __settings;
  }
  /**
   * Consider this private.  User @{Channel#getEx()#setSetting} instead.
   */
  public void set__settings(RealmList<Setting> __settings) {
    this.__settings = __settings;
  }

  /* ====================
      Extension Methods
     ==================== */
  public ChannelExtension getEx() {
    return new ChannelExtension();
  }

  public class ChannelExtension extends SettingProvider {
    ChannelExtension(){super(__settings, getPublisher() != null ? getPublisher().getEx() : null);}
    public boolean isExplicit() {
      return rating.getEx().gte(RatingDAO.EXPLICIT_LANGUAGE);
    }

    public void setFormat(String s) {
      if (s == null)
        return;
      s = s.toLowerCase();
      if (s.contains("audio"))
        Channel.this.setFormat(Episode.FORMAT_AUDIO);
      else if (s.contains("video"))
        Channel.this.setFormat(Episode.FORMAT_VIDEO);
    }

    /**
     * Adds the given category ONLY if it isn't already in the list of categories of this channel.
     */
    public void addCategory(Category cat) {
      if (cat == null)
        return;

      for (Category c : getCategories()) {
        if (c != null && c.getEx().equals(cat))
          return;
      }
      getCategories().add(cat);
    }

    /**
     * Add the given keyword ONLY if it isn'ta already in the list of keywords for this channel.
     */
    public void addKeyword(Keyword key) {
      if (key == null)
        return;

      for (Keyword k : getKeywords()) {
        if (k.getText() != null && k.getText().equalsIgnoreCase(key.getText()))
          return;
      }
      getKeywords().add(key);
    }

    public void setPK(String pk){
      if (StringUtils.isBlank(pk))
        throw new IllegalArgumentException("Primary key cannot be null or empty");
      ID = pk;
    }

  }

}