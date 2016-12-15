package com.podonomy.podonomyplayer.dao;

import java.util.List;

import io.realm.Realm;

/**
 * This is the object to use to save / fetch / create Channel object that are persisted to the database.
 */
public class ChannelDAO {
  /**
   * Creates and return an instance of Channel that will be persisted to the database.
   */
  public static Channel nnew(Realm r) {
    return r.createObject(Channel.class);
  }

  /**
   * Creates an instance of Channel that will NOT be persisted.  Use carefully!
   */
  public static Channel nnew() {
    return new Channel();
  }

  /**
   * Finds the channel with the given primary key (pk) and returns it.  Returns null
   * if no channel are found.
   */
  public static Channel find(Realm r, String pk) {
    return r.where(Channel.class)
            .equalTo("ID", pk)
            .findFirst();
  }

  /**
   * Returns all the channels belonging to the given publisher.
   * @return - a list of channel (empty list if none are subscribed to)
   */
  public static List<Channel> getPublisherChannels(Realm r, String publisherID) {
    return r.where(Channel.class)
    .equalTo("publisher.ID", publisherID)
    .findAll();
  }

  /**
   * Returns the list of all channels the current user has subscribed to.
   * @return - a list of channel (empty list if none are subscribed to)
   */
  public static List<Channel> getSubscribedChannels(Realm r) {
    return r.where(Channel.class)
    .equalTo("subscribed", true)
    .findAll();
  }

}
