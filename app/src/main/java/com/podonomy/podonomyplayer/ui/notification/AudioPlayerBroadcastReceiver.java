package com.podonomy.podonomyplayer.ui.notification;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.podonomy.podonomyplayer.event.Bus;
import com.podonomy.podonomyplayer.event.PauseEvent;
import com.podonomy.podonomyplayer.event.ResumeEvent;
import com.podonomy.podonomyplayer.event.ScrollForwardEvent;
import com.podonomy.podonomyplayer.service.player.Player;
import com.podonomy.podonomyplayer.ui.player.PlayerActivity;

/**
 * Created by lchen on 6/15/2016.
 */
public class AudioPlayerBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        if(action.equalsIgnoreCase("com.podonomy.podonomyplayer.ACTION_PLAY")){
            // do our stuff to play action;
            //System.out.println("Notification Play&Pause");
            byte state = Player.getInstance().getState();
            if (state == Player.STARTED)
                Bus.post(new PauseEvent());
            else if (state == Player.PAUSED){
                Bus.post(new ResumeEvent());
            }
        }

        if(action.equalsIgnoreCase("com.podonomy.podonomyplayer.FAST_FORWARD")){
            // do our stuff to fast forward action;
            //System.out.println("Notification Fast Forward");
            ScrollForwardEvent e = new ScrollForwardEvent();
            Bus.post(e);
        }

        if(action.equalsIgnoreCase("com.podonomy.podonomyplayer.CLOSE_NOTIFICATION")){
            // do our stuff to close action;
            //System.out.println("Notification Close");
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(001);
        }
    }
}
