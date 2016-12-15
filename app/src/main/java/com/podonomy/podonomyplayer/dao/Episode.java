package com.podonomy.podonomyplayer.dao;

import org.apache.commons.lang3.StringUtils;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;


public class Episode extends RealmObject {
  public static final byte FORMAT_AUDIO = 1;
  public static final byte FORMAT_VIDEO = 2;
  public static final byte STATE_NOT_DOWNLOADED = 0;
  public static final byte STATE_DOWNLOADING    = 1;
  public static final byte STATE_DOWNLOADED     = 2;

//  @PrimaryKey
  private String id;
  private String name;
  private byte   format;
  private Channel channel;
  private String episodeURL;
  private Rating getRating;
  private Date   publicationDate;
  private String    durationInSeconds;
  private String author;
  private String summary;
  private String subTitle;
  private String description;
  private String mediaFileURL;
  private String fileType;
  private String thumbnailURL;
  private String GUID;
  private String fileLocation;
  private byte   state = STATE_NOT_DOWNLOADED;
  private int progress;

  @Ignore
  private Extension ex;

  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public byte getFormat() {
    return format;
  }
  public void setFormat(byte format) {
    this.format = format;
  }
  public Channel getChannel() {
    return channel;
  }
  public void setChannel(Channel channel) {
    this.channel = channel;
  }
  public String getEpisodeURL() {
    return episodeURL;
  }
  public void setEpisodeURL(String episodeURL) {
    this.episodeURL = episodeURL;
  }
  public Rating getGetRating() {
    return getRating;
  }
  public void setGetRating(Rating getRating) {
    this.getRating = getRating;
  }
  public Date getPublicationDate() {
    return publicationDate;
  }
  public void setPublicationDate(Date publicationDate) {
    this.publicationDate = publicationDate;
  }
  public String getDurationInSeconds() {
    return durationInSeconds;
  }
  public void setDurationInSeconds(String durationInSeconds) {
    this.durationInSeconds = durationInSeconds;
  }
  public String getAuthor() {
    return author;
  }
  public void setAuthor(String author) {
    this.author = author;
  }
  public String getSummary() {
    return summary;
  }
  public void setSummary(String summary) {
    this.summary = summary;
  }
  public String getSubTitle() {
    return subTitle;
  }
  public void setSubTitle(String subTitle) {
    this.subTitle = subTitle;
  }
  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    this.description = description;
  }
  public String getMediaFileURL() {
    return mediaFileURL;
  }
  public void setMediaFileURL(String mediaFileURL) {
    this.mediaFileURL = mediaFileURL;
  }
  public String getFileType() {
    return fileType;
  }
  public void setFileType(String fileType) {
    this.fileType = fileType;
  }
  public String getThumbnailURL() {
    return thumbnailURL;
  }
  public void setThumbnailURL(String thumbnailURL) {
    this.thumbnailURL = thumbnailURL;
  }
  public String getGUID() {
    return GUID;
  }
  public void setGUID(String GUID) {
    this.GUID = GUID;
  }
  public String getFileLocation() {
    return fileLocation;
  }
  public void setFileLocation(String fileLocation) {
    this.fileLocation = fileLocation;
  }

  public int getProgress() {
    return progress;
  }

  public void setProgress(int progress) {
    this.progress = progress;
  }

  /** *****************************************************
   * Episode extension methods.
   ********************************************************/
  public Extension getEx() {
    if (ex == null) ex = new Extension();
    return ex;
  }
  public class Extension extends SettingProvider{
    Extension(){
      super(getChannel() != null ? getChannel().get__settings() : null,
            getChannel() != null && getChannel().getPublicationDate() != null ?
              getChannel().getPublisher().getEx() :
              null);
    }

    public String getName() {
      return name;
    }
    public boolean isAudio() {
      return format == FORMAT_AUDIO;
    }
    public boolean isVideo() {
      return format == FORMAT_VIDEO;
    }
    public void    setPK(String s) {
      if (StringUtils.isBlank(s))
        throw new IllegalArgumentException("Primary key cannot be null or empty");
      setEpisodeURL(s);}
  }

}
