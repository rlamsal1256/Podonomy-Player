package com.podonomy.podonomyplayer.ui.downloads;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.podonomy.podonomyplayer.R;
import com.podonomy.podonomyplayer.dao.Episode;
import com.podonomy.podonomyplayer.event.Bus;
import com.podonomy.podonomyplayer.event.DownloadEpisodeMediaProgressEvent;

import org.greenrobot.eventbus.Subscribe;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Created by rlamsal on 6/22/2016.
 */
public class DownloadingEpisodeListViewItem extends LinearLayout {
    protected Episode episode = null;
    protected ImageButton episodeImage = null;
    protected TextView episodeTitle = null;
    protected ProgressBar progressBar = null;
    protected TextView percentCompleted = null;
    protected int progress = 0;
    protected DownloadCheckbox downloadCheckbox = null;

    private int progressStatus = 0;
    private Handler mHandler = new Handler();


    public DownloadingEpisodeListViewItem(Context context, Episode episode) {
        super(context);
        this.episode = episode;
        this.progress = episode.getProgress();

        Bus.registerForEvents(this);


        init(context);
    }

    protected void init(Context context) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.downloadlist_item, this, true);

        episodeImage = (ImageButton) view.findViewById(R.id.episodeImageButton);
        episodeTitle = (TextView) view.findViewById(R.id.txtvTitle);
        progressBar = (ProgressBar) view.findViewById(R.id.progress);
        percentCompleted = (TextView) view.findViewById(R.id.txtvPercent);
        downloadCheckbox = (DownloadCheckbox) view.findViewById(R.id.checkbox_downloading_episode);

        updateUI();
    }

    protected void updateUI() {


        if (episode == null)
            return;

        episodeTitle.setText(episode.getName());

        if (!isBlank(episode.getThumbnailURL()))
            ImageLoader.getInstance().displayImage(episode.getThumbnailURL(), episodeImage);
        else if (!isBlank(episode.getChannel().getThumbnailURL()))
            ImageLoader.getInstance().displayImage(episode.getChannel().getThumbnailURL(), episodeImage);

        downloadCheckbox.setEpisode(episode);

        // Start lengthy operation in a background thread
        new Thread(new Runnable() {
            public void run() {
                while (progressStatus < 100) {
                    progressStatus = progress;

                    // Update the progress bar
                    mHandler.post(new Runnable() {
                        public void run() {
                            progressBar.setProgress(progressStatus);
                            percentCompleted.setText(progressStatus + "%");
                        }
                    });
                }
            }
        }).start();
    }


    @Subscribe
    public void onDownloadProgressEvent(DownloadEpisodeMediaProgressEvent e) {
        if (e != null) {
            Bus.consume(e);
        }

        if (episode.equals(e.getEpisode())) {
            progress = e.getEpisode().getProgress();
        }

    }

}
