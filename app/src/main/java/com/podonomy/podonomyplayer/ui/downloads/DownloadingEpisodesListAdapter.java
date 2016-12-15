package com.podonomy.podonomyplayer.ui.downloads;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nhaarman.listviewanimations.util.Swappable;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.podonomy.podonomyplayer.R;
import com.podonomy.podonomyplayer.dao.Episode;
import com.podonomy.podonomyplayer.event.Bus;
import com.podonomy.podonomyplayer.event.DownloadEpisodeMediaProgressEvent;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Created by rlamsal on 6/22/2016.
 */
public class DownloadingEpisodesListAdapter extends BaseAdapter implements Swappable {

    private static final String TAG = "DownloadingEpisodesListAdapter";
    protected List<DownloadItemView> downloadItemViews = new ArrayList<>();

    private Handler mHandler = new Handler();

    public DownloadingEpisodesListAdapter(List<Episode> episodes) {

        for (Episode e : episodes) {
            DownloadItemView downloadItemView = new DownloadItemView(e);
            downloadItemViews.add(episodes.indexOf(e), downloadItemView);
        }

        Bus.registerForEvents(this);
    }

    private class DownloadItemView {

        protected Episode episode = null;
        protected ImageButton episodeImage = null;
        protected TextView episodeTitle = null;
        protected ProgressBar progressBar = null;
        protected TextView percentCompleted = null;
        protected int progress = 0;
        protected DownloadCheckbox downloadCheckbox = null;

        protected int progressStatus = 0;
        protected Handler mHandler = new Handler();

        public DownloadItemView(Episode episode) {
            this.episode = episode;
        }
    }

    @Override
    public int getCount() {
        if (downloadItemViews == null)
            return 0;
        return downloadItemViews.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        DownloadItemView downloadItemView = getItem(position);

        //instantiates the view
        if (view == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.downloadlist_item, parent, false);
        }

        updateView(view, downloadItemView);

        return view;
    }

    //updates the view
    private void updateView(View view, final DownloadItemView downloadItemView) {

        downloadItemView.episodeImage = (ImageButton) view.findViewById(R.id.episodeImageButton);
        downloadItemView.episodeTitle = (TextView) view.findViewById(R.id.txtvTitle);
        downloadItemView.progressBar = (ProgressBar) view.findViewById(R.id.progress);
        downloadItemView.percentCompleted = (TextView) view.findViewById(R.id.txtvPercent);
        downloadItemView.downloadCheckbox = (DownloadCheckbox) view.findViewById(R.id.checkbox_downloading_episode);

        downloadItemView.episodeTitle.setText(downloadItemView.episode.getName());

        if (!isBlank(downloadItemView.episode.getThumbnailURL()))
            ImageLoader.getInstance().displayImage(downloadItemView.episode.getThumbnailURL(), downloadItemView.episodeImage);
        else if (!isBlank(downloadItemView.episode.getChannel().getThumbnailURL()))
            ImageLoader.getInstance().displayImage(downloadItemView.episode.getChannel().getThumbnailURL(), downloadItemView.episodeImage);

        downloadItemView.downloadCheckbox.setEpisode(downloadItemView.episode);

        // Start lengthy operation in a background thread
        new Thread(new Runnable() {
            public void run() {
                while (downloadItemView.progressStatus < 100) {
                    downloadItemView.progressStatus = downloadItemView.progress;

                    // Update the progress bar
                    mHandler.post(new Runnable() {
                        public void run() {
                            downloadItemView.progressBar.setProgress(downloadItemView.progressStatus);
                            downloadItemView.percentCompleted.setText(downloadItemView.progressStatus + "%");
                        }
                    });
                }
            }
        }).start();


    }



    @Override
    public long getItemId(int position) {
        return getItem(position).episode.getId().hashCode();
    }

    @Override
    public DownloadItemView getItem(int position) {
        if (downloadItemViews == null)
            return null;
        if (position >= downloadItemViews.size() || position < 0)
            return null;

        return downloadItemViews.get(position);
    }

    //used for dragging and dropping purposes
    @Override
    public boolean hasStableIds() {
        return true;
    }

    //used for dragging and dropping purposes
    @Override
    public void swapItems(int positionOne, int positionTwo) {

        DownloadItemView firstItem = downloadItemViews.set(positionOne, getItem(positionTwo));
        notifyDataSetChanged();
        downloadItemViews.set(positionTwo, firstItem);

    }


    @Subscribe
    public void onDownloadProgressEvent(DownloadEpisodeMediaProgressEvent e) {
        if (e != null) {
            Bus.consume(e);
        }

        for (DownloadItemView downloadItemView : downloadItemViews) {
            if (downloadItemView.episode.equals(e.getEpisode())) {
                downloadItemView.progress = e.getEpisode().getProgress();

            }
        }
    }

}
