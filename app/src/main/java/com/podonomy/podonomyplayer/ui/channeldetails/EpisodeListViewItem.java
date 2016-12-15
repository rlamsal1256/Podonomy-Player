package com.podonomy.podonomyplayer.ui.channeldetails;

import android.content.Context;
import android.content.Intent;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.podonomy.podonomyplayer.R;
import com.podonomy.podonomyplayer.dao.Episode;
import com.podonomy.podonomyplayer.event.Bus;
import com.podonomy.podonomyplayer.event.DownloadEpisodeMediaEvent;
import com.podonomy.podonomyplayer.event.PlayMediaEvent;
import com.podonomy.podonomyplayer.service.downloader.Downloader;

import java.text.SimpleDateFormat;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 *
 */
public class EpisodeListViewItem extends LinearLayout {
    protected Episode episode = null;
    protected ImageButton episodeImage = null;
    protected TextView episodeTitle = null;
    protected TextView episodeSubtitle = null;
    protected CheckBox selectedCheckbox = null;
    protected TextView episodeDuration = null;
    protected TextView fileSize = null;
    protected TextView releaseDate = null;
    protected TextView description = null;
    protected ImageButton giveTipZButton = null;
    protected ImageButton gotoWebSiteButton = null;
    protected ImageButton downloadEpisodeButton = null;
    protected ImageButton playButton = null;
    protected boolean expanded = false;

    public EpisodeListViewItem(Context context, Episode episode) {
        super(context);
        this.episode = episode;
        init(context);
    }

    protected void init(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.channel_details_list_item, this, true);

        episodeImage = (ImageButton) findViewById(R.id.channel_detail_list_itm_episodeImageButton);
        episodeTitle = (TextView) findViewById(R.id.channel_detail_list_itm_episodeTitle);
        //episodeSubtitle       = (TextView   ) findViewById(R.id.channel_detail_list_itm_episodeSubtitle);
        selectedCheckbox = (CheckBox) findViewById(R.id.channel_detail_list_itm_selectedCheckbox);
        episodeDuration = (TextView) findViewById(R.id.channel_detail_list_itm_duration);
        fileSize = (TextView) findViewById(R.id.channel_detail_list_itm_fileSize);
        releaseDate = (TextView) findViewById(R.id.channel_detail_list_itm_releaseDate);
        description = (TextView) findViewById(R.id.channel_detail_list_itm_episodeDescription);
        giveTipZButton = (ImageButton) findViewById(R.id.channel_details_giveTipButton);
        gotoWebSiteButton = (ImageButton) findViewById(R.id.channel_detail_list_itm_gotoWebSiteButton);
        downloadEpisodeButton = (ImageButton) findViewById(R.id.channel_detail_list_itm_downloadEpisodeButtons);
        playButton = (ImageButton) findViewById(R.id.channel_detail_list_itm_playButton);

        episodeImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                expandCollapse();
            }
        });
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                expandCollapse();
            }
        });
        downloadEpisodeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadEpisodeButtonClicked(v);
            }
        });
        playButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                playButtonClicked(v);
            }
        });
        updateUI();
    }

    protected void updateUI() {
        if (episode == null)
            return;

        if (!isBlank(episode.getThumbnailURL()))
            ImageLoader.getInstance().displayImage(episode.getThumbnailURL(), episodeImage);
        else if (!isBlank(episode.getChannel().getThumbnailURL()))
            ImageLoader.getInstance().displayImage(episode.getChannel().getThumbnailURL(), episodeImage);

        episodeTitle.setText(episode.getName());
        //episodeSubtitle.setText(episode.getSubTitle());
        description.setText(episode.getDescription());

        //todo: fix date format to use user's locale format.
        if (episode.getPublicationDate() != null) {
            SimpleDateFormat format = new SimpleDateFormat();
            releaseDate.setText(format.format(episode.getPublicationDate()));
        }

        episodeDuration.setText(episode.getDurationInSeconds());
    }

    /**
     * Expands/collapse the view of this episode in the list of episode for the channel.  This woudl normally
     * be invoke by the user pressing the episode image.
     */
    protected void expandCollapse() {
        expanded = !expanded;
        if (expanded) {
            //description.setVisibility(View.VISIBLE);
            int length = description.length();
            if (length > 10) {
                description.setMaxLines(10);
                description.setMovementMethod(new ScrollingMovementMethod());
            } else
                description.setMaxLines(length);
        } else {
            //description.setVisibility(View.GONE);
            description.setMaxLines(2);
        }
    }

    protected void downloadEpisodeButtonClicked(View v) {
        if (episode != null) {
            Toast.makeText(getContext(), "Episode is being downloaded",
                    Toast.LENGTH_LONG).show();
            DownloadEpisodeMediaEvent e = new DownloadEpisodeMediaEvent();
            e.setEpisodeID(episode.getId());

            Intent intent = new Intent(getContext(), Downloader.class);
            getContext().startService(intent);

            Bus.post(e);
        }
    }

    protected void playButtonClicked(View v) {
        if (episode != null) {
            if (!isBlank(episode.getFileLocation())) {
                PlayMediaEvent playMedia = new PlayMediaEvent();
                playMedia.setEpisodeID(episode.getId());
                Bus.post(playMedia);
            }
        }
    }
}
