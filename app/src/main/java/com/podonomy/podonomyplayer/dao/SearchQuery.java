package com.podonomy.podonomyplayer.dao;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * This class contains the elements supported to perform an episode/channel search.
 */
public class SearchQuery extends RealmObject {
  private String  keywords = null;
  private String  language = null;
  private Integer numberWeeks = null;
  private Boolean audio = null;
  private Boolean video = null;
  private Boolean local = null;
  private RealmList<Channel> channelsFound = new RealmList<>();
  private RealmList<Episode> episodesFound = new RealmList<>();

  @Ignore
  private Ex ex;

  public String getKeywords() {
    return keywords;
  }
  public void setKeywords(String keywords) {
    this.keywords = keywords;
  }
  public String getLanguage() {
    return language;
  }
  public void setLanguage(String language) {
    this.language = language;
  }
  public Integer getNumberWeeks() {
    return numberWeeks;
  }
  public void setNumberWeeks(Integer numberWeeks) {
    this.numberWeeks = numberWeeks;
  }
  public Boolean getAudio() {
    return audio;
  }
  public void setAudio(Boolean audio) {
    this.audio = audio;
  }
  public Boolean getVideo() {
    return video;
  }
  public void setVideo(Boolean video) {
    this.video = video;
  }
  public Boolean getLocal() {
    return local;
  }
  public void setLocal(Boolean local) {
    this.local = local;
  }
  public RealmList<Channel> getChannelsFound() {
    return channelsFound;
  }
  public void setChannelsFound(List<Channel> channelsFound) {
    this.channelsFound.clear();
    this.channelsFound.addAll(channelsFound);
  }
  public RealmList<Episode> getEpisodesFound() {
    return episodesFound;
  }
  public void setEpisodesFound(List<Episode> foundEpisodes) {
    episodesFound.clear();
    episodesFound.addAll(foundEpisodes);
  }

  /* ===================
     Extension methods
     =================== */
  public Ex getEx(){
    if (ex == null)
      ex = new Ex();
    return ex;
  }
  public class Ex{
    public boolean hasKeywords(){
      return isBlank(keywords);
    }

    /**
     * Remove this object from the database....
     */
    public void delete() {
      removeFromRealm();
    }
  }
}
