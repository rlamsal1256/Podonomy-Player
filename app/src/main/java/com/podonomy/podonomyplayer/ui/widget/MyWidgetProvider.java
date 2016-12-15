package com.podonomy.podonomyplayer.ui.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.podonomy.podonomyplayer.R;
import com.podonomy.podonomyplayer.dao.Episode;
import com.podonomy.podonomyplayer.event.Bus;
import com.podonomy.podonomyplayer.event.ResumeEvent;
import com.podonomy.podonomyplayer.service.player.Player;

/**
 * Created by lchen on 5/16/2016.
 */
public class MyWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_4x1);

        //set actions for button, actions need to be registered in AndroidManifest.xml file

        remoteViews.setOnClickPendingIntent(R.id.widget_image, buildButtonPendingIntent(context, "PLAYER_ACTIVITY"));

        remoteViews.setOnClickPendingIntent(R.id.widget_button_play_pause, buildButtonPendingIntent(context, "PLAY_PAUSE"));

        remoteViews.setOnClickPendingIntent(R.id.widget_button_previous, buildButtonPendingIntent(context, "PREVIOUS"));

        remoteViews.setOnClickPendingIntent(R.id.widget_button_next, buildButtonPendingIntent(context, "NEXT"));

        remoteViews.setOnClickPendingIntent(R.id.widget_button_playlist, buildButtonPendingIntent(context, "PLAYLISTS"));

        pushWidgetUpdate(context, remoteViews);

        //remoteViews.
    }

    public static PendingIntent buildButtonPendingIntent(Context context, String actionString) {
        Intent intent = new Intent();
        intent.setAction(actionString);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static void pushWidgetUpdate(Context context, RemoteViews remoteViews) {
        ComponentName myWidget = new ComponentName(context, MyWidgetProvider.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        manager.updateAppWidget(myWidget, remoteViews);
    }
}
