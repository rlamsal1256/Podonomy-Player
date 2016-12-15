package com.podonomy.podonomyplayer.dao;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Publisher extends RealmObject {
  @PrimaryKey
  private String ID;
  @Required
  private String name;
  private String addressLine1;
  private String addressLine2;
  private String city;
  private String state;
  private String country;
  private String zip;
  private String phone;
  private RealmList<Setting> __settings;

  @Ignore
  private RealmResults<Channel> channels;
  @Ignore
  private Ex ex;

  public String getID() {
    return ID;
  }
  public void setID(String s) {
    ID = s;
  }
  public String getName() {
    return this.name;
  }
  public void setName(String s) {
    name = s;
  }
  public String getAddressLine1() {
    return DAO.es(addressLine1);
  }
  public void setAddressLine1(String s) {
    addressLine1 = s;
  }
  public String getAddressLine2() {
    return DAO.es(this.addressLine2);
  }
  public void setAddressLine2(String s) {
    addressLine2 = s;
  }
  public String getCity() {
    return DAO.es(this.city);
  }
  public void setCity(String s) {
    city = s;
  }
  public String getState() {
    return DAO.es(this.state);
  }
  public void setState(String s) {
    state = s;
  }
  public String getZip() {
    return DAO.es(this.zip);
  }
  public void setZip(String s) {
    zip = s;
  }
  public String getCountry() {
    return DAO.es(this.country);
  }
  public void setCountry(String s) {
    country = s;
  }
  public String getPhone() {
    return DAO.es(this.phone);
  }
  public void setPhone(String s) {
    phone = s;
  }
  public List<? extends Channel> getChannels(Realm r) {
    return ChannelDAO.getPublisherChannels(r, getID());
  }
  public RealmList<Setting> get__settings() {
    return __settings;
  }
  public void set__settings(RealmList<Setting> __settings) {
    this.__settings = __settings;
  }

  public Ex getEx(){
    if (ex == null)
      ex = new Ex();
    return ex;
  }
  public class Ex extends SettingProvider {
    Ex() {
//todo: Bring SettingProvider back but without its constructor create a new database transaction (which cause an exception trying to create a transaction within a transaction)
      super(__settings, null);
      //super(__settings, ConfigurationDAO.getConfig(DAO.getRealm(PlayerApplication.getInstance().getBaseContext())).getDefaultSettings());
    }
  }
}
