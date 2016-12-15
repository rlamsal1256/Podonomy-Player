package com.podonomy.podonomyplayer.ui.playlists;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.podonomy.podonomyplayer.R;
import com.podonomy.podonomyplayer.dao.Episode;
import com.podonomy.podonomyplayer.event.Bus;
import com.podonomy.podonomyplayer.event.PauseEvent;
import com.podonomy.podonomyplayer.event.PlayMediaEvent;
import com.podonomy.podonomyplayer.event.ResumeEvent;
import com.podonomy.podonomyplayer.service.player.Player;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Created by rlamsal on 8/2/2016.
 */
public class PlayListViewItem extends LinearLayout {
    protected Episode episode = null;
    protected ImageButton episodeImage = null;
    protected TextView episodeTitle = null;
    protected TextView episodeSubtitle = null;
    protected TextView episodeDuration = null;
    protected TextView releaseDate = null;
    protected ImageButton episodePlayPauseButton = null;

    public PlayListViewItem(Context context, Episode episode) {
        super(context);
        this.episode = episode;
        init(context);
    }

    protected void init(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.play_list_item, this, true);

        episodeImage    = (ImageButton) findViewById(R.id.play_list_itm_episodeImageButton);
        episodeTitle    = (TextView)    findViewById(R.id.play_list_itm_episodeTitle);
        episodeSubtitle = (TextView)    findViewById(R.id.play_list_itm_episodeSubTitle);
        episodeDuration = (TextView)    findViewById(R.id.play_list_itm_duration);
        //releaseDate     = (TextView)    findViewById(R.id.play_list_itm_releaseDate);
        episodePlayPauseButton     = (ImageButton) findViewById(R.id.play_list_itm_episodePlayButton);

        updateUI();

        episodePlayPauseButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //send an event to player to play this episode

                onPlayPause();
            }
        });
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

        //todo: fix date format to use user's locale format.
//        if (episode.getPublicationDate() != null) {
//            SimpleDateFormat format = new SimpleDateFormat();
//            releaseDate.setText(format.format(episode.getPublicationDate()));
//        }

        episodeDuration.setText(episode.getDurationInSeconds());
    }

    protected void playButtonClicked() {
        episodePlayPauseButton.setImageResource(R.drawable.ic_pause_black_24dp);
        if (episode != null) {
            if (!isBlank(episode.getFileLocation())) {
                PlayMediaEvent playMedia = new PlayMediaEvent();
                playMedia.setEpisodeID(episode.getId());
                Bus.post(playMedia);
            }
        }
    }

    public void onPlayPause(){

        byte state = getPlayerState();
        if ((state == Player.STARTED) && (episode.equals(Player.getInstance().getCurrentEpisode()))) {
            episodePlayPauseButton.setImageResource(R.drawable.ic_play_arrow_black_24dp);
            Bus.post(new PauseEvent());
        }
        else if ((state == Player.PAUSED) && (episode.equals(Player.getInstance().getCurrentEpisode()))){
            episodePlayPauseButton.setImageResource(R.drawable.ic_pause_black_24dp);
            Bus.post(new ResumeEvent());
        } else {
            playButtonClicked();
        }
    }

    private byte getPlayerState(){
        Player player = Player.getInstance();
        if (player == null)
            return Player.STOPPED;
        else
            return player.getState();
    }


}
