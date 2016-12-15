package com.podonomy.podonomyplayer.dao;

import java.math.BigDecimal;
import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;

public class Subscriber extends RealmObject {
  private String ID;
  private String generatedName;
  private String username;
  private String userType;
  private String creditCardToken;
  private String email;
  private int monthlyBudgetInCents;
  private Date paymentStartDate;
  private DistributionMethod distributionMethod;
  private Episode  playingEpisode;

  private SearchQuery lastSearchQuery;

  private RealmList<Episode> currentDownloadEpisodes;


  @Ignore
  private Extension ex;

  public String getID() {
    return ID;
  }
  public void setID(String ID) {
    this.ID = ID;
  }

  public String getGeneratedName() {
    return generatedName;
  }
  public void setGeneratedName(String generatedName) {
    this.generatedName = generatedName;
  }

  public String getUsername() {
    return username;
  }
  public void setUsername(String username) {
    this.username = username;
  }

  public String getUserType() {
    return userType;
  }
  public void setUserType(String userType) {
    this.userType = userType;
  }

  public String getCreditCardToken() {
    return creditCardToken;
  }
  public void setCreditCardToken(String creditCardToken) {
    this.creditCardToken = creditCardToken;
  }

  public String getEmail() {
    return email;
  }
  public void setEmail(String email) {
    this.email = email;
  }

  public int getMonthlyBudgetInCents() {
    return monthlyBudgetInCents;
  }
  public void setMonthlyBudgetInCents(int monthlyBudgetInCents) {
    this.monthlyBudgetInCents = monthlyBudgetInCents;
  }

  public Date getPaymentStartDate() {
    return paymentStartDate;
  }
  public void setPaymentStartDate(Date paymentStartDate) {
    this.paymentStartDate = paymentStartDate;
  }

  public DistributionMethod getDistributionMethod() {
    return distributionMethod;
  }
  public void setDistributionMethod(DistributionMethod distributionMethod) {
    this.distributionMethod = distributionMethod;
  }

  public Episode getPlayingEpisode() {
    return playingEpisode;
  }
  public void setPlayingEpisode(Episode playingEpisode) {
    this.playingEpisode = playingEpisode;
  }
  public SearchQuery getLastSearchQuery() {
    return lastSearchQuery;
  }
  public void setLastSearchQuery(SearchQuery lastSearchQuery) {
    this.lastSearchQuery = lastSearchQuery;
  }

  public RealmList<Episode> getCurrentDownloadEpisodes() {
    return currentDownloadEpisodes;
  }

  public void setCurrentDownloadEpisodes(RealmList<Episode> currentDownloadEpisodes) {
    this.currentDownloadEpisodes = currentDownloadEpisodes;
  }

  /* -----------------
       * Extension methods
       * ----------------- */
  public Extension getEx(){
    if (ex == null) ex = new Extension();
    return ex;
  }

  static final BigDecimal ONE_HUNDRED = new BigDecimal(100);

  public class Extension {
    public boolean isPayingSubscriber(){
      return getCreditCardToken() != null && getMonthlyBudgetInCents() > 0;
    }

    public BigDecimal getMonthlyBudget(){
      BigDecimal b = new BigDecimal(getMonthlyBudgetInCents()).divide(ONE_HUNDRED);
      return b;
    }

    public void setMonthlyBudget(BigDecimal value){
      if (value == null) setMonthlyBudgetInCents(0);
      else setMonthlyBudgetInCents(value.multiply(ONE_HUNDRED).intValue());
    }
  }
}
