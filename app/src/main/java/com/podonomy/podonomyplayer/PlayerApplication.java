package com.podonomy.podonomyplayer;

import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
import android.provider.Settings.Secure;
import android.support.annotation.NonNull;

import com.podonomy.podonomyplayer.dao.DAO;
import com.podonomy.podonomyplayer.service.cache.Cache;
import com.podonomy.podonomyplayer.service.downloader.Downloader;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.config.ACRAConfigurationException;

import io.realm.Realm;

/*
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.EntypoModule;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.joanzapata.iconify.fonts.IoniconsModule;
import com.joanzapata.iconify.fonts.MaterialCommunityModule;
import com.joanzapata.iconify.fonts.MaterialModule;
import com.joanzapata.iconify.fonts.MeteoconsModule;
import com.joanzapata.iconify.fonts.SimpleLineIconsModule;
import com.joanzapata.iconify.fonts.TypiconsModule;
import com.joanzapata.iconify.fonts.WeathericonsModule;
*/

/*
@ReportsCrashes(
  mailTo = "fgagnon@seedthinkers.com",
  logcatArguments = {"-t", "1000", "-v", "time", "*:D"},
  customReportContent = {
    ReportField.USER_CRASH_DATE,
    ReportField.AVAILABLE_MEM_SIZE,
    ReportField.TOTAL_MEM_SIZE,
    ReportField.BUILD,
    ReportField.BUILD_CONFIG,
    ReportField.DISPLAY,
    ReportField.CRASH_CONFIGURATION,
    ReportField.SHARED_PREFERENCES,
    ReportField.LOGCAT,
    ReportField.USER_COMMENT,
    ReportField.BRAND,
    ReportField.ANDROID_VERSION,
    ReportField.APP_VERSION_NAME,
    ReportField.STACK_TRACE},
  mode = ReportingInteractionMode.DIALOG,
  resDialogText = R.string.crash_dialog_text,
  resDialogTitle = R.string.crash_dialog_title,
  resDialogOkToast = R.string.crash_dialog_ok_toast
)*/
public class PlayerApplication extends Application{
  private static PlayerApplication instance;
  private Context context;
  private String  deviceID;
  private boolean isDebug;
  private com.podonomy.podonomyplayer.dao.Configuration config;

  /**
   * Returns the singleton instance of this application.
   */
  @NonNull
  public static PlayerApplication getInstance() {
    if (instance == null)
      throw new Error("Application object not instantiated by Android.");
    return instance;
  }

  public PlayerApplication() {
    super();
  }

  public Context getAppContext() {
    return super.getApplicationContext();
  }

  public void onCreate() {
    super.onCreate();
    ACRA.init(this);
    instance = this;

    context = getApplicationContext();
    isDebug = (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;

    /////
    // Initialize the database
    Realm r = DAO.getRealm(context);

    //Start the asset cache
    Cache.onStart();
    Downloader.onStart();

  }

  /**
   * Returns whether this is a debug version of this app or not.
   */
  public boolean isDebug(){
    return isDebug;
  }

  public String getDeviceID(){
    if (deviceID == null)
        deviceID = Secure.getString(getAppContext().getContentResolver(), Secure.ANDROID_ID);
    return deviceID;
  }

  @Override
  public void onTerminate() {
    Cache.onStop(); //close the asset cache
    Downloader.onStop();
    super.onTerminate();
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
  }

  @Override
  public void onLowMemory() {
    super.onLowMemory();
  }

  @Override
  public void onTrimMemory(int level) {
    super.onTrimMemory(level);
  }

  @Override
  public void registerComponentCallbacks(ComponentCallbacks callback) {
    super.registerComponentCallbacks(callback);
  }

  @Override
  public void unregisterComponentCallbacks(ComponentCallbacks callback) {
    super.unregisterComponentCallbacks(callback);
  }

  @Override
  public void registerActivityLifecycleCallbacks(ActivityLifecycleCallbacks callback) {
    super.registerActivityLifecycleCallbacks(callback);
  }

  @Override
  public void unregisterActivityLifecycleCallbacks(ActivityLifecycleCallbacks callback) {
    super.unregisterActivityLifecycleCallbacks(callback);
  }

  @Override
  public void registerOnProvideAssistDataListener(OnProvideAssistDataListener callback) {
    super.registerOnProvideAssistDataListener(callback);
  }

  @Override
  public void unregisterOnProvideAssistDataListener(OnProvideAssistDataListener callback) {
    super.unregisterOnProvideAssistDataListener(callback);
  }

  // when user decides to send report on his own, disable reporting via crash dialog, send report,
  // re-enable crash dialog
  static void sendLogs() {
    try {
      ACRA.getConfig().setMode(ReportingInteractionMode.SILENT);
      ACRA.getErrorReporter().handleException(null, false);
      ACRA.getConfig().setMode(ReportingInteractionMode.DIALOG);
    } catch (ACRAConfigurationException ex) {
      ACRA.getErrorReporter().uncaughtException(Thread.currentThread(), ex);
    }
  }

  //factory is currently used for IOC, which will be replaced in future
  public static Factory _factory = null;
  public static Factory getFactory(){
    if (_factory == null){
      setFactory(new Factory());
    }
    return _factory;
  }

  public static void setFactory(Factory factory) {
    _factory = factory;
  }
}
