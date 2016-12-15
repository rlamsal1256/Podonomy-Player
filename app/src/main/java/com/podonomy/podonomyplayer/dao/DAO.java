package com.podonomy.podonomyplayer.dao;

import android.content.Context;

import com.podonomy.podonomyplayer.PlayerApplication;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;

/**
 * This is the root class to deal with the database.  Use this class to get a database session {@Realm}, begin / commit / rollback
 * transactions, etc...
 */
public class DAO {
    private static RealmConfiguration realmConfigSingleton;  //Realm.io does not like to have multiple configurations and recommends using a singleton instead.

    /**
     * Returns the RealmConfiguration object that ought to be used when initializing the database.
     */
    public static RealmConfiguration getRealmConfig(Context ctx){
        if (realmConfigSingleton == null) {
          realmConfigSingleton = new RealmConfiguration.Builder(ctx)
            .name(ConfigurationDAO.REALM_DB_FILE_NAME)
            .schemaVersion(ConfigurationDAO.REAL_DB_SCHEMA_VERSION)
            .migration(new DBMigration())
            .build();
        }
        return realmConfigSingleton;
    }

    /**
    * This is the method to use to get a new Realm object (can be thought as a database session).
    * DO NOT USE Realm.getInstance() directly as it will not initialise the other DAO objects.
    * The principle here is that you acquire a database session {@link Realm}, use it and then return it
    * using the {@link DAO#returnRealm} method.
    */
    public static Realm getRealm(Context ctx){
     Realm r = Realm.getInstance(getRealmConfig(ctx));
     r.beginTransaction();

     init(r);

     r.commitTransaction();
     return r;
   }

    /**
    * Return the realm that was acquired via {@link DAO#getRealm}
    */
    public static void returnRealm(Realm realm){
      if (realm != null && ! realm.isClosed())
        realm.close();
    }

    /**
     * Begins a new transaction on the current session {@link r}.  If a transaction was already open
     * on the session, it is left untouched and no new transaction is began.  r cannot be null.
     * @param r - the database session
     */
    public static void beginTransaction(Realm r){
      if (r == null) throw new IllegalArgumentException("realm cannot be null");
      if (r.isInTransaction())
        return;
      r.beginTransaction();
    }

    /**
     * Commits any open transaction on the current database session r. If not transaction are open
     * this method does nothing.  r cannot be null
     * @param r
     */
    public static void commitTransaction(Realm r){
      if (r == null) throw new IllegalArgumentException("realm cannot be null");
      if (r.isInTransaction())
        r.commitTransaction();
    }

    /**
     * Rollsback any open transaction on the current session.  if not transaction is open, this method
     * does nothing.  r cannot be null.
     * @param r
     */
    public static void rollbackTransaction(Realm r){
      if (r == null) throw new IllegalArgumentException("realm cannot be null");
      if (r.isInTransaction())
        r.cancelTransaction();
    }

    /**
     * Attaches the given object to the database.  The dbObject is an object that was not
     * create by the database (i.e. its ..DAO.nnew(realm) was not used but instead ..DAO.nnew() was)
     * so this method will "attach" the object to the database as if it had been created by the
     * database.  The returned object is the database version of the received object.  Usage example:
     *    obj = ObjDAO.join(obj, realm);
     */
    public static <E extends RealmObject> E attach(Realm r, E dbObject){
      return r.copyToRealmOrUpdate(dbObject);
    }

    /**
     * Given a collection of object that are not tied to the database session, ties all object in the
     * collection are return a new collection where each object attached is now in the list.
     */
    public static <E extends RealmObject> List<E> attach(Realm r, Iterable<E> objects){
      return r.copyToRealmOrUpdate(objects);
    }

    /**
     * Detaches the given database object from the database.  this way the database connection may be
     * close yet accessing the object will not fail.  Usage:
     *   detachedObj = detach(obj, realm);
     */
    public static <E extends RealmObject> E detach(Realm r, E dbObject){
      return r.copyFromRealm(dbObject);
    }

    /**
     * Detaches all the object in the given collection from the database session. Detached object can be
     * user even if the session has been closed.
     */
    public static <E extends RealmObject> List<E> detach(Realm r, Iterable<E> objects){
      List<E> toReturn = new ArrayList<E>();
      for(E e : objects){
        if (e.isValid())
          toReturn.add(r.copyFromRealm(e));
        else
          toReturn.add(e);
      }
      return toReturn;
    }

    /**
    * Simple method which return the empty string if the given string (t) is null.
    */
    public static String es(String t){
      return t == null ? "" : t;
    }

    /**
     * For debug purposes, this creates a fake database with some data in it.
     * @param r
     */
    public static void createFakeDB(Realm r){
      r.beginTransaction();
      r.deleteAll();
      init(r);

      PlayList downloadedPlaylist = PlayListDAO.getDownloadedPlaylist(r);

      Subscriber subscriber = SubscriberDAO.nnew(r);
      subscriber.setUsername("fgagnon");
      subscriber.setEmail("fgagnon@seedthinkers.com");

      Publisher wnycStudios = PublisherDAO.nnew(r);
      wnycStudios.setID("WNYC");
      wnycStudios.setName("WNYC Studios");
      wnycStudios.setAddressLine1("1651 Memorial Drive");
      wnycStudios.setAddressLine2("Suite 201");
      wnycStudios.setCity("Boston");
      wnycStudios.setState("MA");
      wnycStudios.setZip("02123");

      Channel freakonomics = ChannelDAO.nnew(r);
      freakonomics.setID("freakonomics");
      freakonomics.setName("Freakonomics Radio");
      freakonomics.setAuthor("Michael Dubner");
      freakonomics.setDescription("Inspired by the books of the same name, Freakonomics Radio is hosted by Stephen Dubner, with co-author Steve Levitt. An award-winning podcast exploring \"the hidden side of everything\". From the economy, headline news to pop culture. Available weekly on demand from WNYC Public Radio. ");
      freakonomics.setOwnerEmail("dubner@freakonomicsradio.com");
      freakonomics.setPublisher(wnycStudios);
      freakonomics.setSubscribed(true);

      Episode episode = EpisodeDAO.nnew(r);
      episode.setId("freakonomics - The No-Tipping Point");
      episode.setName("The No-Tipping Point");
      episode.setDescription("The restaurant business model is warped: kitchen wages are too low to hire cooks, while diners are put in charge of paying the waitstaff. So what happens if you eliminate tipping, raise menu prices, and redistribute the wealth? New York restaurant maverick Danny Meyer is about to find out.");
      episode.setChannel(freakonomics);
      downloadedPlaylist.getContent().add(episode);

      episode = EpisodeDAO.nnew(r);
      episode.setId("freakonomics - The United States of Cory Booker");
      episode.setName("The United States of Cory Booker");
      episode.setDescription("The junior U.S. Senator from New Jersey thinks bipartisanship is right around the corner. Is he just an idealistic newbie or does he see a way forward that everyone else has missed?");
      episode.setChannel(freakonomics);
      downloadedPlaylist.getContent().add(episode);

      Channel radioLab = ChannelDAO.nnew(r);
      radioLab.setID("Radiolab");
      radioLab.setName("Radiolab");
      radioLab.setAuthor("Jad Abumrad");
      radioLab.setDescription("Inspiring curiosity. Each episode of the Radiolab podcast is a patchwork of people, sounds, stories, society and experiences. Hosted by Jad Abumrad and Robert Krulwich, Radiolab is produced & made available on demand by WNYC public radio.");
      radioLab.setOwnerEmail("abumrad@radioLab.com");
      radioLab.setPublisher(wnycStudios);
      radioLab.setSubscribed(true);

      episode = EpisodeDAO.nnew(r);
      episode.setId("Radiolab - Debatable");
      episode.setName("Debatable");
      episode.setDescription("Unclasp your briefcase. It’s time for a showdown. In competitive debate future presidents, supreme court justices, and titans of industry pummel each other with logic and rhetoric.  But a couple years ago Ryan Wash, a queer, Black, first-generation college student from Kansas City, Kansas joined the debate team at Emporia State University. When he started going up against fast-talking, well-funded, “name-brand” teams, it was clear he wasn’t in Kansas anymore. So Ryan became the vanguard of a movement that made everything about debate debatable. In the end, he made himself a home in a strange and hostile land. Whether he was able to change what counts as rigorous academic argument … well, that’s still up for debate. Produced by Matt Kielty. Reported by Abigail Keel Special thanks to Will Baker, Myra Milam, John Dellamore, Sam Mauer, Tiffany Dillard Knox, Mary Mudd, Darren \"Chief\" Elliot, Jodee Hobbs, Rashad Evans and Luke Hill.  Special thanks also to Torgeir Kinne Solsvik for use of the song h-lydisk / B Lydian from the album Geirr Tveitt Piano Works and Songs");
      episode.setChannel(radioLab);
      downloadedPlaylist.getContent().add(episode);

      episode = EpisodeDAO.nnew(r);
      episode.setId("Radiolab - K-poparazzi");
      episode.setName("K-poparazzi");
      episode.setDescription("In the U.S., paparazzi are pretty much synonymous with invasion of privacy. But today we travel to a place where the prying press create something more like a prison break.  K-pop is a global juggernaut - with billions in sales and millions of ");
      episode.setChannel(radioLab);
      downloadedPlaylist.getContent().add(episode);

      Publisher gunNetwork = PublisherDAO.nnew(r);
      gunNetwork.setID("Gun Radio Network");
      gunNetwork.setName(gunNetwork.getID());

      Channel twinGuns = ChannelDAO.nnew(r);
      twinGuns.setID("This week in Guns");
      twinGuns.setName(twinGuns.getID());
      twinGuns.setPublisher(gunNetwork);
      twinGuns.setSubscribed(true);

      r.commitTransaction();
    }

    static void init(Realm r){
      RatingDAO.init(r);
      PlayListDAO.init(r);
    }
}
