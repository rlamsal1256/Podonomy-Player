package com.podonomy.podonomyplayer;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.podonomy.podonomyplayer.dao.Configuration;
import com.podonomy.podonomyplayer.dao.ConfigurationDAO;
import com.podonomy.podonomyplayer.dao.DAO;
import com.podonomy.podonomyplayer.dao.Subscriber;
import com.podonomy.podonomyplayer.dao.SubscriberDAO;
import com.podonomy.podonomyplayer.event.Event;
import com.podonomy.podonomyplayer.event.EventLogger;
import com.podonomy.podonomyplayer.ui.nav.NavigationBar;

import io.realm.Realm;



/**
 * This is the base class of all our activity.  You should inherit from it instead of inheriting directly from
 * an android AppCompatActivity so that the typical setup is done for you.  For instance, the realm.io management
 * is handle by this class.
 */
public abstract class Player_AppCompatActivity extends AppCompatActivity {
  private static final String TAG = "Player_AppCompatActivity";
  private EventLogger eLogger = null;

  /**
   * List of services that this activity depends on for its execution.  This base class will make
   * sure that these services are up when the activity is created.
   */
  protected Class[] servicesRequired = null;
  /**
   * The realm the subclass should use to interact with our realm.io database.
   */
  protected Realm realm = null;
  /**
   * The current subscriber of the app.  The user may not be a subscriber so this MAY BE NULL.  But
   * know that this class will try to create a fake subscriber upon the app's first startup.
   */
  protected Subscriber subscriber = null;
  /**
   * The handle to the main view of this activity.
   */
  protected View view = null;
  /**
   * A handle to the ad bar of this activity. This will be null if no ads are displayed.
   */
  protected AdView adbarView = null;
  /**
   * Handle on the nav bar at the bottom .
   */
  protected NavigationBar navBar;

  /**
   * Subclass should implement.
   */
  protected void _onCreate(Bundle savedInstanceState){
  }

  /**
   * Subclass should implement.
   */
  protected void _onDestroy(){  }

  /**
   * Subclass should implement.
   */
  protected void _onResume(){  }

  /**
   * Subclass should implement.
   */
  protected void _onStop(){}

  /**
   * Subclass should implement.
   */
  protected void _onStart(){}

  /**
   * Constructor.  Expects the list of service this activity depends upon. This
   * base class will start the services dependended upon before starting the subclass.
   * Pass null if not services are depended upon.
   */
  public Player_AppCompatActivity(Class[] serviceDependencies){
    this.servicesRequired = serviceDependencies;
  }

  /**
   * Subclass must implement this method.  This method should return the ID of the view/layout that needs
   * to be loaded during the object creation.  If the user is a paying using the withAds will be set
   * to false, so the layout returned should be the layout without the ad banner.
   */
  protected abstract int getViewID(boolean withAds);

  /**
   * Returns the view ID of the google ad bar (R.id.adView)
   */
  protected int getAdBarID(){
    return R.id.ad_bar;
  }

  /**
   * Returns the ID of the nav button at the bottom left of the screen
   */
  protected int getNavButtonID(){
    return R.id.navButton;
  }

  /**
   * Returns a handle on the event logger
   */
  protected EventLogger eLog(){
    return this.eLogger;
  }

  /**
   * This is called by the android framework.  It setups the environment and then calls _onCreate on the class.
   * Subclass should implement _onCreate.
   */
  //TODO: now put the theme here, later will put in sharedPreference as where user customized settings are saved, same as method getThemeImageID
  protected static Resources.Theme theme = null;

  @Override
  protected final void onCreate(Bundle savedInstanceState){
    theme = getTheme();
    try{
      // Fetch the current theme from the database...
      realm = DAO.getRealm(getBaseContext());
      final Configuration config = ConfigurationDAO.getConfig(realm);
      setTheme(config.getThemeID());
      super.onCreate(savedInstanceState);

      ActionBar actionBar = getSupportActionBar();
      if (actionBar != null)
        actionBar.hide();

      // Setup our protected elements...
      eLogger = EventLogger.getLogger(config);
      subscriber = SubscriberDAO.getCurrentSubscriber(realm);
      if (subscriber == null)
        createSubscriber();

      ////
      // Load the layout
      int layoutID = getViewID(true);
      if (subscriber.getEx().isPayingSubscriber())
        layoutID = getViewID(false);
      this.setContentView(layoutID);
      view = findViewById(layoutID);

      ////
      // Setup the ad bar if the user isn't paying
      if (! subscriber.getEx().isPayingSubscriber()) {
        // show the ads...
        adbarView = (AdView) findViewById(getAdBarID());
        if (adbarView != null) {
          AdRequest req = new AdRequest.Builder().build();
          adbarView.loadAd(req);
        }
        else{
          eLog().error(PlayerApplication.getInstance().getDeviceID(), "User isn't paying yet unable to retrieve adBarView.");
        }
      }

      ////
      // Get the handle of the nav button
      navBar = new NavigationBar();
      navBar.onCreate(this);

      this._onCreate(savedInstanceState);

    }
    catch(Exception e){
      closeRealm();
      throw new RuntimeException(e);
    }
  }

  /**
   * This is called by the android framework.  It tears down the environment and then calls _onDestroy on the class.
   * Subclass should implement _onDestroy.
   */
  @Override
  protected final void onDestroy() {
    closeRealm();
    super.onDestroy();
  }

  /**
   * This is called by the android framework.  It resumes the environment and then calls _onResume on the class.
   * Subclass should implement _onResume.
   */
  @Override
  protected final void onResume(){
    super.onResume();
    this._onResume();
  }

  @Override
  protected final void onStop() {
    this._onStop();
    super.onStop();
  }

  @Override
  protected final void onStart(){
    super.onStart();
    //ensure that any service we depends on is started....
    if (servicesRequired != null){
      for(Class service : servicesRequired){
        Intent intent = new Intent(getBaseContext(), service);
        super.startService(intent);
      }
    }

    this._onStart();
  }

  /**
   * This methods closes any active transaction with the reaml.io currently opened.  Invoking this
   * method WILL REVERT ANY ACTIVE TRANSACTION.
   */
  protected void closeRealm(){
    if (realm != null && ! realm.isClosed())
      realm.close();
  }



  /**
   * This class will create a subscri
   */
  private void createSubscriber()
  {
    realm.beginTransaction();
    subscriber = SubscriberDAO.nnew(realm);
    subscriber.setID(PlayerApplication.getInstance().getDeviceID());
    realm.commitTransaction();
  }

  //this function return an integer of the resource ID of the given reference
  protected static int getThemeImageID(int attr, final int defaultValue){
    TypedValue typedvalueattr = new TypedValue();
    final boolean found = theme.resolveAttribute(attr, typedvalueattr, true);
    return found? typedvalueattr.resourceId : defaultValue;
  }
}
