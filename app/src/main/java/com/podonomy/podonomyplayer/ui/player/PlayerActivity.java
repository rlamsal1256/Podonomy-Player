package com.podonomy.podonomyplayer.ui.player;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.format.DateUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TableRow;
import android.widget.TextView;

import com.podonomy.podonomyplayer.Player_AppCompatActivity;
import com.podonomy.podonomyplayer.R;
import com.podonomy.podonomyplayer.dao.Episode;
import com.podonomy.podonomyplayer.event.Bus;
import com.podonomy.podonomyplayer.event.DecreasePlaySpeedEvent;
import com.podonomy.podonomyplayer.event.EventLogger;
import com.podonomy.podonomyplayer.event.PauseEvent;
import com.podonomy.podonomyplayer.event.ResumeEvent;
import com.podonomy.podonomyplayer.event.ScrollBackwardEvent;
import com.podonomy.podonomyplayer.event.ScrollForwardEvent;
import com.podonomy.podonomyplayer.event.SkipToNextEpisodeEvent;
import com.podonomy.podonomyplayer.event.SkipToPreviousEpisodeEvent;
import com.podonomy.podonomyplayer.service.TaskExecutorService;
import com.podonomy.podonomyplayer.service.player.Player;
import com.podonomy.podonomyplayer.ui.settings.SettingsActivity;
import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;


public class PlayerActivity extends Player_AppCompatActivity {

  private static final String TAG = "Player Activity";


  protected Episode currentEpisode = null;
  protected TableRow    row1 = null;
  protected TableRow    row2 = null;
  protected TableRow    row3 = null;
  protected TableRow    row4 = null;
  protected TableRow    row5 = null;
  protected ImageButton sendInviteButton = null;
  protected TextView    playlistPlayingTextview = null;
  protected TextView    episodeTitleTextview = null;
  protected TextView    channelTextview = null;
  protected ImageButton markReadUnreadButton = null;
  protected ImageButton skipPreviousButton = null;
  protected ImageButton skipNextButton = null;
  protected SeekBar mSeekBar = null;
  protected ImageButton backwardButton = null;
  protected ImageButton forwardButton = null;
  protected ImageButton playPauseButton = null;
  protected ImageButton expandControlsButton = null;
  protected ImageButton increasePlaySpeedButton = null;
  protected TextView    playSpeedTextview = null;
  protected float speedRate;
  protected ImageButton decreasePlaySpeedButton = null;
  protected ImageButton collapseControlsButton = null;
  protected boolean areControlsCollapsed = true;
  protected TextView    passedTimeTextView = null;
  protected TextView    totalTimeTextView = null;
  protected FragmentStatePagerAdapter mAdapter;
  protected ViewPager mPager;
  protected PageIndicator mIndicator;

  private Handler mHandler = new Handler();

  MediaPlayer mMediaPlayer = null;

  public PlayerActivity() {
    super(null);
  }

  @Override
  protected void _onCreate(Bundle savedInstanceState) {
    super._onCreate(savedInstanceState);
    //Bus.registerForEvents(this);

    ////////
    // Wire the UI components
    row1                    = (TableRow) findViewById(R.id.player_controls_row_1);
    row2                    = (TableRow) findViewById(R.id.player_controls_row_2);
    row3                    = (TableRow) findViewById(R.id.player_controls_row_3);
    row4                    = (TableRow) findViewById(R.id.player_controls_row_4);
    row5                    = (TableRow) findViewById(R.id.player_controls_row_5);
    sendInviteButton        = (ImageButton) findViewById(R.id.player_send_invite_button);
    playlistPlayingTextview = (TextView) findViewById(R.id.player_playlist_playing_textview);
    episodeTitleTextview    = (TextView) findViewById(R.id.player_episode_title_textview);
    channelTextview         = (TextView) findViewById(R.id.player_channel_textview);
    markReadUnreadButton    = (ImageButton) findViewById(R.id.player_mark_read_unread_button);
    skipPreviousButton      = (ImageButton) findViewById(R.id.player_skip_previous_button);
    skipNextButton          = (ImageButton) findViewById(R.id.player_skip_next_button);
    mSeekBar                = (SeekBar) findViewById(R.id.player_progressbar);
    backwardButton          = (ImageButton) findViewById(R.id.player_backward_button);
    forwardButton           = (ImageButton) findViewById(R.id.player_forward_button);
    playPauseButton         = (ImageButton) findViewById(R.id.player_play_pause_button);
    expandControlsButton    = (ImageButton) findViewById(R.id.player_expand_controls_button);
    increasePlaySpeedButton = (ImageButton) findViewById(R.id.player_increase_playspeed_button);
    playSpeedTextview       = (TextView) findViewById(R.id.player_playspeed_textview);
    decreasePlaySpeedButton = (ImageButton) findViewById(R.id.player_decrease_playspeed_button);
    collapseControlsButton  = (ImageButton) findViewById(R.id.player_collapse_controls_button);
    passedTimeTextView      = (TextView) findViewById(R.id.player_episode_passed_duration);
    totalTimeTextView       = (TextView) findViewById(R.id.player_episode_total_duration);

    mAdapter = new PlayerFragmentAdapter(getSupportFragmentManager());

    mPager = (ViewPager)findViewById(R.id.pager);
    mPager.setAdapter(mAdapter);

    mIndicator = (CirclePageIndicator)findViewById(R.id.indicator);
    mIndicator.setViewPager(mPager);
    mIndicator.setCurrentItem(mAdapter.getCount() - 2);

    mMediaPlayer = Player.getInstance().getMediaPlayer();

    if (areControlsCollapsed)
      this.collapseControls();

    /////
    //Write the expand/collapse events
    collapseControlsButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        collapseControls();
      }
    });
    expandControlsButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        expandControls();
      }
    });

    playPauseButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        onPlayPause();
      }
    });

    increasePlaySpeedButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        onIncreasePlayspeed();
      }
    });

    mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override
      public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(mMediaPlayer != null && fromUser){
          mMediaPlayer.seekTo(progress * 1000);
        }
      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {

      }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {

      }
    });

    init();

    /////
    // Make sure the task executor is running so that it can process play events....
    Intent i = new Intent(getBaseContext(), TaskExecutorService.class);
    super.startService(i);


    final ImageButton actionBtn = (ImageButton) findViewById(R.id.actionButton);
    if (actionBtn == null)
      throw new RuntimeException("The navigation_bar layout was not included as part of your layout");

    actionBtn.setOnClickListener(
            new View.OnClickListener() {
              @Override
              public void onClick(View v) {
//                        ActionMenu dialog = new ActionMenu(PlaylistsActivity.this);
//                        dialog.show();

                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(PlayerActivity.this, actionBtn);
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.player_action_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                  public boolean onMenuItemClick(MenuItem item) {

                    if (getBaseContext().getResources().getString(R.string.settings_action).equals(item.toString())){
                      Intent intent = new Intent(PlayerActivity.this, SettingsActivity.class);
                      startActivity(intent);
                    }
                    return true;
                  }
                });

                popup.show(); //showing popup menu


              }
            }
    );
  }

  private void init() {
    setPlayPauseButtonImage();

    currentEpisode = Player.getInstance().getCurrentEpisode();

    int intEpisodeDuration = 0;
    if (currentEpisode != null){
      episodeTitleTextview.setText(currentEpisode.getName());
      channelTextview.setText(currentEpisode.getChannel().getName());

      String strEpisodeDuration = currentEpisode.getDurationInSeconds();
      if (strEpisodeDuration!=null) {
        String[] values = strEpisodeDuration.split(":");

        Time time = convertDurationIntoInteger(values);
        intEpisodeDuration = time.hours * 60 * 60 + time.minutes * 60 + time.seconds;

        totalTimeTextView.setText(strEpisodeDuration);
        mSeekBar.setMax(intEpisodeDuration);
      }

      //EventLogger.getLogger().debug(TAG, ">>>>>>>!!!!!!!!!!!********** episode max duration ", intEpisodeDuration);

      runOnUiThread(new Runnable() {
        @Override
        public void run() {

          if(mMediaPlayer != null){
            int mCurrentPosition = mMediaPlayer.getCurrentPosition() / 1000;
            passedTimeTextView.setText(DateUtils.formatElapsedTime(mCurrentPosition));
            mSeekBar.setProgress(mCurrentPosition);
          }
          mHandler.postDelayed(this, 1000);
        }
      });
    } else {
      EventLogger.getLogger().debug(TAG, ">>>>>>>!!!!!!!!!!!********** episode is null ");
    }



  }

  private Time convertDurationIntoInteger(String[] values) {
    Time time = new Time();
    if (values.length == 3){
      time.hours = Integer.parseInt(values[0]);
      time.minutes = Integer.parseInt(values[1]);
      time.seconds = Integer.parseInt(values[2]);
    } else if (values.length == 2){
      time.minutes = Integer.parseInt(values[0]);
      time.seconds = Integer.parseInt(values[1]);
    }
    return time;
  }

  private class Time{
    int hours;
    int minutes;
    int seconds;

    public Time() {
      this.hours = 0;
      this.minutes = 0;
      this.seconds = 0;
    }
  }

  //checks to see if the media is being played or not and changes image accordingly
  private void setPlayPauseButtonImage() {
    byte state = getPlayerState();
    if (state == Player.STARTED) {
      playPauseButton.setImageResource(R.drawable.ic_pause_black_24dp);
    }
    else if (state == Player.PAUSED){
      playPauseButton.setImageResource(R.drawable.ic_play_arrow_black_24dp);
    }
  }

  @Override
  protected void _onDestroy() {
    super._onDestroy();
  }

  @Override
  protected void _onResume() {
    super._onResume();
  }

  @Override
  protected int getViewID(boolean withAds) {
    if (withAds)
      return R.layout.player;
    return R.layout.player_noads;
  }

  /********************************************************************
   *                   COLLAPSE/EXPAND Player Controls
   ********************************************************************/
  protected void collapseControls(){
    if (row1 != null)
      row1.setVisibility(View.GONE);
    if (row3 != null)
      row3.setVisibility(View.GONE);
    if (row5 != null)
      row5.setVisibility(View.GONE);
    if (collapseControlsButton != null)
      collapseControlsButton.setVisibility(View.GONE);
    if (expandControlsButton != null)
      expandControlsButton.setVisibility(View.VISIBLE);
  }
  protected void expandControls (){
    if (row1 != null)
      row1.setVisibility(View.VISIBLE);
    if (row3 != null)
      row3.setVisibility(View.VISIBLE);
    if (row5 != null)
      row5.setVisibility(View.VISIBLE);
    if (collapseControlsButton != null)
      collapseControlsButton.setVisibility(View.VISIBLE);
    if (expandControlsButton != null)
      expandControlsButton.setVisibility(View.GONE);
  }

  /********************************************************************
   *                    Events Generation
   ********************************************************************/

  public void onPlayPause(){

    byte state = getPlayerState();
    if (state == Player.STARTED) {
      playPauseButton.setImageResource(R.drawable.ic_play_arrow_black_24dp);
      Bus.post(new PauseEvent());
    }
    else if (state == Player.PAUSED){
      playPauseButton.setImageResource(R.drawable.ic_pause_black_24dp);
      Bus.post(new ResumeEvent());
    }
  }

  protected void onSendInvite(){}
  protected void onForward(){
    ScrollForwardEvent e = new ScrollForwardEvent();
    Bus.post(e);
  }
  protected void onBackward(){
    ScrollBackwardEvent e = new ScrollBackwardEvent();
    Bus.post(e);
  }
  protected void onMarkReadUnread(){
  }
  protected void onSkipPrevious(){
    SkipToPreviousEpisodeEvent e = new SkipToPreviousEpisodeEvent();
    Bus.post(e);
  }
  protected void onSkipNext(){
    SkipToNextEpisodeEvent e = new SkipToNextEpisodeEvent();
    Bus.post(e);
  }
  protected void onIncreasePlayspeed(){
//    if (playSpeedTextview == null){
//      speedRate = 1.0f;
//      playSpeedTextview.setText(String.valueOf(speedRate));
//    }
//
//    IncreasePlaySpeedEvent e = new IncreasePlaySpeedEvent(speedRate);
//    Bus.post(e);
  }
//
//  @Subscribe(sticky = true)
//  public void onSpeedRateIncreasedEvent(SpeedRateIncreasedEvent event) {
//    Bus.consume(event);
//
//    playSpeedTextview.setText(String.valueOf(event.getSpeedRate()));
//
//  }
  protected void onDecreasePlayspeed(){
    DecreasePlaySpeedEvent e = new DecreasePlaySpeedEvent();
    Bus.post(e);
  }

  /********************************************************************
   ********************************************************************/
  private byte getPlayerState(){
    Player player = Player.getInstance();
    if (player == null)
      return Player.STOPPED;
    else
      return player.getState();
  }

}
