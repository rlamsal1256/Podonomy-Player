package com.podonomy.podonomyplayer.service.player;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.podonomy.podonomyplayer.dao.Episode;
import com.podonomy.podonomyplayer.dao.EpisodeDAO;
import com.podonomy.podonomyplayer.dao.PlayList;
import com.podonomy.podonomyplayer.dao.PlayListDAO;
import com.podonomy.podonomyplayer.event.Bus;
import com.podonomy.podonomyplayer.event.PauseEvent;
import com.podonomy.podonomyplayer.event.PlayMediaEvent;
import com.podonomy.podonomyplayer.event.ResumeEvent;
import com.podonomy.podonomyplayer.service.ServiceBase;
import com.podonomy.podonomyplayer.ui.notification.PlayerNotification;

import org.greenrobot.eventbus.Subscribe;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 *
 */
public class Player extends ServiceBase {
  public static final String TAG = "Player";
  public static final byte IDLE        = 0;
  public static final byte INITIALIZED = 1;
  public static final byte PREPARED    = 2;
  public static final byte STARTED     = 3;
  public static final byte PAUSED      = 4;
  public static final byte STOPPED     = 5;

  private static Player playerSingleton = null;
  private byte          state           = IDLE;
  private MediaPlayer   mediaPlayer     = null;

  public MediaPlayer getMediaPlayer() {
    return mediaPlayer;
  }

  private Episode episode = null;
  public Episode getEpisode() {
    return episode;
  }

  private Episode       currentEpisode  = null;
  public Episode getCurrentEpisode(){
    return this.currentEpisode;
  }

  private PlayList currentPlaylist = null;

  public PlayList getCurrentPlaylist() {
    return currentPlaylist;
  }

  /**
   * Returns the singleton instance of this PLayer.  Returns null if not player yet exists.
   * If no player exists, you must create it by sending an Intent for this server so Android
   * will create the instance.  DO NOT CREATE ONE YOURSELF.
   */
  public static Player getInstance(){
    return playerSingleton;
  }

  public Player() {
    super();
  }

  @Override
  public void onCreate() {
    super.onCreate();
    Bus.registerForEvents(this);
    playerSingleton = this;
  }
  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    return super.onStartCommand(intent, flags, startId);
  }
  @Override
  public void onDestroy() {
    Bus.unregister(this);
    if (mediaPlayer != null){
      mediaPlayer.release();
      mediaPlayer = null;
    }
    playerSingleton = null;
    super.onDestroy();
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
  public boolean onUnbind(Intent intent) {
    return super.onUnbind(intent);
  }
  @Override
  public void onRebind(Intent intent) {
    super.onRebind(intent);
  }
  @Override
  public void onTaskRemoved(Intent rootIntent) {
    super.onTaskRemoved(rootIntent);
  }
  @Override
  protected void dump(FileDescriptor fd, PrintWriter writer, String[] args) {
    super.dump(fd, writer, args);
  }
  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  /**
   * returns the state of the player.  States can be IDLE, INITIALIZED, ...
   */
  public byte getState() {
    return state;
  }

  @Subscribe(sticky = true)
  public void onResumeEvent(ResumeEvent event){
    Bus.consume(event);
    init();
    if (state == PREPARED || state == PAUSED || state == INITIALIZED) {
      mediaPlayer.start();
      state = STARTED;
    }
    sendNotification();
  }
//
//  @TargetApi(Build.VERSION_CODES.M)
//  @Subscribe(sticky = true)
//  public void onIncreasePlaySpeedEvent(IncreasePlaySpeedEvent event){
//    Bus.consume(event);
//
//    if (state == STARTED || mediaPlayer.isPlaying()) {
//      float speedRate = event.getSpeedRate();
//      speedRate += 0.2f;
//      mediaPlayer.setPlaybackParams(new PlaybackParams().setSpeed(speedRate));
//
//      Bus.post(new SpeedRateIncreasedEvent(speedRate));
//    }
//
//  }

  @Subscribe(sticky = true)
  public void onPauseEvent(PauseEvent event){
    Bus.consume(event);
    init();
    if (state == STARTED || mediaPlayer.isPlaying()) {
      mediaPlayer.pause();
      state = PAUSED;
    }
    sendNotification();
  }

  @Subscribe(sticky = true)
  public void onPlayMediaEvent(PlayMediaEvent event) {
    if (event == null || isBlank(event.getEpisodeID()))
      return; //nothing to do, invalid call....

    //setup the player object
    init();

    if (state == STARTED || mediaPlayer.isPlaying() || state == PAUSED){
      mediaPlayer.stop();
      state = STOPPED;
      mediaPlayer.reset();
      state = IDLE;
    }

    //fetch the episode from the db....
    Context context = getBaseContext();
    episode = EpisodeDAO.find(realm, event.getEpisodeID());
    if (episode == null){
      getLogger().error(TAG, "Episode with ID ", event.getEpisodeID(), " was not found. Can't play it.");
      return; //episode not found.
    }

    //start playing the new media....
    try {
      this.currentEpisode = episode;
      this.currentPlaylist = PlayListDAO.getDownloadedPlaylist(realm);
      getLogger().debug(TAG, "Playing episode (", episode.getId(), ") ", episode.getName());

      mediaPlayer.setDataSource(episode.getFileLocation());
      state = INITIALIZED;
      mediaPlayer.prepare();
      state = PREPARED;
      mediaPlayer.start();
      state = STARTED;
    }
    catch (IOException io){
      getLogger().error(TAG, io, "Exception when trying to play episode.");
      if (mediaPlayer != null)
        mediaPlayer.release();
      mediaPlayer = null;
      state = IDLE;
    }
    sendNotification();
  }



  private void init() {
    if (mediaPlayer == null) {
      mediaPlayer = new MediaPlayer();
      state       = IDLE;
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        mediaPlayer.setAudioAttributes(new AudioAttributes.Builder().build());
    }
  }

  //when play or pause the episode,will send out a local notification which contains
  //episode image, buttons
  private void sendNotification(){
    //TODO: two kinds of notification, 1)without the bitmap, will show default imageView; 2)with bitmap, show bitmap image
    PlayerNotification playerNotification = new PlayerNotification();
    Context ctx = getBaseContext();
    playerNotification.setmNotificationManager(ctx);
    playerNotification.showNotification();
  }

}
