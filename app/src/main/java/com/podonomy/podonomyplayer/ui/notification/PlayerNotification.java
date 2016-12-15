package com.podonomy.podonomyplayer.ui.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RemoteViews;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.podonomy.podonomyplayer.R;
import com.podonomy.podonomyplayer.dao.Channel;
import com.podonomy.podonomyplayer.dao.Episode;
import com.podonomy.podonomyplayer.dao.Subscriber;
import com.podonomy.podonomyplayer.service.player.Player;
import com.podonomy.podonomyplayer.ui.widget.MyWidgetProvider;

import org.androidannotations.annotations.ViewById;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import io.realm.Realm;

import static com.podonomy.podonomyplayer.event.EventLogger.getLogger;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Created by lchen on 5/16/2016.
 */
public class PlayerNotification {
    private NotificationManager mNotificationManager = null;
    private Context context;
    public void setmNotificationManager( Context ctx){
        context = ctx;
    }
    public void showNotification() {

        mNotificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        //CharSequence tickerText = "PodonomyPlayer is playing";
        long when = System.currentTimeMillis();
        Notification.Builder builder = new Notification.Builder(context);
        Notification notification=builder.getNotification();
        notification.when=when;

        RemoteViews contentView=new RemoteViews(context.getPackageName(), R.layout.notification_layout);

        //notification first load, then download episode image, will refresh when bitmap downloaded.
        Player player = Player.getInstance();
        Episode episode = player.getCurrentEpisode();
        notification.icon = R.drawable.ic_fingerprint_black_24dp;

        if (episode != null){
            if (!isBlank(episode.getThumbnailURL())) {
                getLogger().debug("456 ", String.valueOf(episode.getThumbnailURL()));
                //to avoid android.os.NetworkOnMainThreadException, use Asynctask
                new DownloadBitmap(context).execute(episode.getThumbnailURL());
            }
            else if (!isBlank(episode.getChannel().getThumbnailURL())) {
                new DownloadBitmap(context).execute(episode.getChannel().getThumbnailURL());
            }

            //set title and detail on the notification
            contentView.setTextViewText(R.id.notification_episode_title, episode.getName());
            //TODO: set episode length of notification
            Channel channel = episode.getChannel();
            if (channel != null){
                contentView.setTextViewText(R.id.notification_channel_and_length, episode.getChannel().getName());
                //contentView.setTextViewText(R.id.notification_channel_and_length, String.valueOf(episode.getDurationInSeconds()));
            }
        }

        setListeners(contentView);

        notification.contentView = contentView;
        mNotificationManager.notify(001, notification);
    }

    public void setListeners(RemoteViews view) {
        //listeners for button on the notification bar
        Intent switchIntent = new Intent("com.podonomy.podonomyplayer.ACTION_PLAY");
        PendingIntent pendingSwitchIntent = PendingIntent.getBroadcast(this.context, 100, switchIntent, 0);
        view.setOnClickPendingIntent(R.id.notificationPlayPause, pendingSwitchIntent);

        Intent forwardIntent = new Intent("com.podonomy.podonomyplayer.FAST_FORWARD");
        PendingIntent pendingForwardIntentIntent = PendingIntent.getBroadcast(this.context, 100, forwardIntent, 0);
        view.setOnClickPendingIntent(R.id.notificationFastForward, pendingForwardIntentIntent);

        Intent closeNotificationIntent = new Intent("com.podonomy.podonomyplayer.CLOSE_NOTIFICATION");
        PendingIntent pendingcloseNotificationIntent = PendingIntent.getBroadcast(this.context, 100, closeNotificationIntent, 0);
        view.setOnClickPendingIntent(R.id.closeNotification, pendingcloseNotificationIntent);
    }

    public class DownloadBitmap extends AsyncTask<String, Void, Bitmap> {
        private NotificationManager mNotificationManager = null;
        private Context ctx;

        public DownloadBitmap(Context ctx){
            this.ctx = context;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        public void onPostExecute(Bitmap bitmap){
            mNotificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
            //CharSequence tickerText = "PodonomyPlayer is playing";
            long when = System.currentTimeMillis();
            Notification.Builder builder = new Notification.Builder(context);
            Notification notification=builder.getNotification();
            notification.when=when;
            notification.icon = R.drawable.ic_fingerprint_black_24dp;
            RemoteViews contentView=new RemoteViews(context.getPackageName(), R.layout.notification_layout);
            //contentView.setImageViewResource(R.id.notificationImageView, R.drawable.ic_fingerprint_black_24dp);
            //contentView.setImageViewUri(R.id.notificationImageView, Uri.parse("http://www.firearmsradio.net/wp-content/uploads/powerpress/GOH_itunes3000.jpg"));

            //only use bitmap since uri can not work. Also need to resize it otherwise will not show.
            Bitmap scalsedBitmap = bitmap.createScaledBitmap(bitmap, 120, 120, false);
            contentView.setImageViewBitmap(R.id.notificationImageView, scalsedBitmap);
            setListeners(contentView);
            notification.contentView = contentView;

            mNotificationManager.notify(001, notification);

            //also update image in widget
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_4x1);
            remoteViews.setImageViewBitmap(R.id.widget_image, scalsedBitmap);
            Player player = Player.getInstance();
            Episode episode = player.getCurrentEpisode();
            if(episode != null){
                remoteViews.setTextViewText(R.id.widget_episode_title, episode.getName());
                //TODO: set episode length of widget
                Channel channel = episode.getChannel();
                if (channel != null){
                    remoteViews.setTextViewText(R.id.widget_channel_and_length, episode.getChannel().getName());
                    //contentView.setTextViewText(R.id.notification_channel_and_length, String.valueOf(episode.getDurationInSeconds()));
                }
            }
            MyWidgetProvider.pushWidgetUpdate(context.getApplicationContext(), remoteViews);
        }
    }
}
