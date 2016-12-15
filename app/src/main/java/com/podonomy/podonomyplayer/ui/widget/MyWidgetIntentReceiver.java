package com.podonomy.podonomyplayer.ui.widget;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.RemoteViews;

import com.podonomy.podonomyplayer.R;
import com.podonomy.podonomyplayer.dao.Channel;
import com.podonomy.podonomyplayer.dao.Episode;
import com.podonomy.podonomyplayer.event.Bus;
import com.podonomy.podonomyplayer.event.PauseEvent;
import com.podonomy.podonomyplayer.event.ResumeEvent;
import com.podonomy.podonomyplayer.event.SkipToNextEpisodeEvent;
import com.podonomy.podonomyplayer.event.SkipToPreviousEpisodeEvent;
import com.podonomy.podonomyplayer.service.player.Player;
import com.podonomy.podonomyplayer.ui.player.PlayerActivity;
import com.podonomy.podonomyplayer.ui.playlists.PlaylistsActivity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by lchen on 5/16/2016.
 */
public class MyWidgetIntentReceiver extends BroadcastReceiver {

    private static int clickCount = 0;
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        if(intent.getAction().equals("PLAYER_ACTIVITY")) {
            String actionString = "PLAYER_ACTIVITY";
            onImage(context, actionString);
            Log.i("onWidgetReceive", "PLAYER_ACTIVITY");
        }else if(intent.getAction().equals("PLAY_PAUSE")) {
            String actionString = "PLAY_PAUSE";
            onPlayPause();
            updateWidgetOnButtonClick(context, actionString);
            Log.i("onWidgetReceive", "PLAY_PAUSE");
        }else if(intent.getAction().equals("PREVIOUS")) {
            String actionString = "PREVIOUS";
            onSkipPrevious();
            updateWidgetOnButtonClick(context,actionString);
            Log.i("onWidgetReceive", "PREVIOUS");
        }else if(intent.getAction().equals("NEXT")) {
            String actionString = "NEXT";
            onSkipNext();
            updateWidgetOnButtonClick(context, actionString);
            Log.i("onWidgetReceive", "NEXT");
        }else if(intent.getAction().equals("PLAYLISTS")) {
            String actionString = "PLAYLISTS";
            onPlayLists(context, actionString);
            Log.i("onWidgetReceive", "PLAYLISTS");
        }

        //TODO, Click image or list button go to activity 
    }

    private void onImage(Context context, String actionString) {
        /*RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_4x1);
        remoteViews.setImageViewResource(R.id.widget_image, getImageToSet());
        //REMEMBER TO ALWAYS REFRESH YOUR BUTTON CLICK LISTENERS!!!
        remoteViews.setOnClickPendingIntent(R.id.widget_button, MyWidgetProvider.buildButtonPendingIntent(context, actionString));
        MyWidgetProvider.pushWidgetUpdate(context.getApplicationContext(), remoteViews);
        */
        Intent intent = new Intent(context, PlayerActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void onPlayLists(Context context, String actionString) {
        Intent intent = new Intent(context, PlaylistsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void onPlayPause() {
        byte state = Player.getInstance().getState();
        if (state == Player.STARTED)
            Bus.post(new PauseEvent());
        else if (state == Player.PAUSED){
            Bus.post(new ResumeEvent());
        }
    }

    protected void onSkipPrevious(){
        SkipToPreviousEpisodeEvent e = new SkipToPreviousEpisodeEvent();
        Bus.post(e);
    }
    protected void onSkipNext(){
        SkipToNextEpisodeEvent e = new SkipToNextEpisodeEvent();
        Bus.post(e);
    }

    private void updateWidgetOnButtonClick(Context context, String actionString) {
        Player player = Player.getInstance();
        Episode episode = player.getCurrentEpisode();
        if(episode != null){
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_4x1);
            remoteViews.setTextViewText(R.id.widget_episode_title, episode.getName());
            //TODO: set episode length of notification
            Channel channel = episode.getChannel();
            if (channel != null){
                remoteViews.setTextViewText(R.id.widget_channel_and_length, episode.getChannel().getName());
                //contentView.setTextViewText(R.id.notification_channel_and_length, String.valueOf(episode.getDurationInSeconds()));
            }
            MyWidgetProvider.pushWidgetUpdate(context.getApplicationContext(), remoteViews);
        }
    }

}
