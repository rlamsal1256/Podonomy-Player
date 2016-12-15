package com.podonomy.podonomyplayer.ui.channeldetails;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.podonomy.podonomyplayer.Player_AppCompatActivity;
import com.podonomy.podonomyplayer.R;
import com.podonomy.podonomyplayer.dao.Channel;
import com.podonomy.podonomyplayer.dao.ChannelDAO;
import com.podonomy.podonomyplayer.dao.DAO;
import com.podonomy.podonomyplayer.dao.Episode;
import com.podonomy.podonomyplayer.dao.EpisodeDAO;
import com.podonomy.podonomyplayer.event.Bus;
import com.podonomy.podonomyplayer.event.DownloadChannelEpisodesEvent;
import com.podonomy.podonomyplayer.event.ViewChannelDetailsEvent;
import com.podonomy.podonomyplayer.service.TaskExecutorService;
import com.podonomy.podonomyplayer.service.player.Player;
import com.podonomy.podonomyplayer.ui.SubscribeCheckbox;
import com.podonomy.podonomyplayer.ui.menu.EpisodeSortMenu;
import com.podonomy.podonomyplayer.ui.menu.TipMenu;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 */
public class ChannelDetails extends Player_AppCompatActivity {
    public static final String EXTRA_CHANNEL_ID = "channelID";
    protected boolean expanded = false;
    protected ImageButton channelImage = null;
    protected TextView channelName = null;
    protected SubscribeCheckbox subscribeCheckbox = null;
    protected ImageView contentTypeImage = null;
    protected TextView shortDescription = null;
    protected TextView longDescription = null;
    protected ImageButton giveTipButton = null;
    protected ImageButton settingsButton = null;
    protected ImageButton inviteButton = null;
    protected ImageButton gotoWebSiteButton = null;
    protected ImageButton sortButton = null;
    protected ListView episodesListView = null;
    protected Channel channel = null;
    protected DownloadChannelEpisodesEvent downloadChannelEpisodes = null;


    public ChannelDetails() {
        super(new Class[]{TaskExecutorService.class, Player.class});
    }

    @Override
    protected int getViewID(boolean withAds) {
        if (withAds)
            return R.layout.channel_details;
        return R.layout.channel_details_noads;
    }

    @Override
    protected void _onCreate(Bundle savedInstanceState) {
        super._onCreate(savedInstanceState);

        channelImage = (ImageButton) findViewById(R.id.channel_details_channelImage);
        channelName = (TextView) findViewById(R.id.channel_details_channelName);
        subscribeCheckbox = (SubscribeCheckbox) findViewById(R.id.channel_details_subscribeCheckbox);
        contentTypeImage = (ImageView) findViewById(R.id.channel_details_contentType);
        shortDescription = (TextView) findViewById(R.id.channel_details_channelShortDescription);
        longDescription = (TextView) findViewById(R.id.channel_details_channelLongDescription);
        giveTipButton = (ImageButton) findViewById(R.id.channel_details_giveTipButton);
        settingsButton = (ImageButton) findViewById(R.id.channel_details_settingsButton);
        inviteButton = (ImageButton) findViewById(R.id.channel_details_inviteButton);
        gotoWebSiteButton = (ImageButton) findViewById(R.id.channel_details_gotoWebSiteButton);
        sortButton = (ImageButton) findViewById(R.id.channel_details_sortButton);
        episodesListView = (ListView) findViewById(R.id.channel_details_episodeListView);

        //Scrollable description...
        longDescription.setMovementMethod(new ScrollingMovementMethod());

        channelImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandCollapseHeader();
            }
        });

//       TODO: add on click listener to the view, not just the image
//        LayoutInflater inflater = LayoutInflater.from(this);
//        View channelView = inflater.inflate(R.layout.channel_details, null);
//        LinearLayout layout = (LinearLayout) channelView.findViewById(R.id.channel_header);
//        layout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                expandCollapseHeader();
//            }
//        });

        giveTipButton = (ImageButton) findViewById(R.id.channel_details_giveTipButton);
        if (giveTipButton == null)
            throw new RuntimeException("The navigation_bar layout was not included as part of your layout");

        giveTipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TipMenu tipDialog = new TipMenu(ChannelDetails.this);
                tipDialog.show();
            }

        });

        sortButton = (ImageButton) findViewById(R.id.channel_details_sortButton);
        if (sortButton == null)
            throw new RuntimeException("The navigation_bar layout was not included as part of your layout");

        sortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EpisodeSortMenu sortDialog = new EpisodeSortMenu(ChannelDetails.this);
                sortDialog.show();
            }

        });
    }

    public void onSort(final String sortByStr) {

        //get all the list of episodes
        List<Episode> listOfEpisodes = EpisodeDAO.forChannel(realm, channel.getID());

        //clone the list
        ArrayList<Episode> list = new ArrayList<Episode>(listOfEpisodes);

        //sort them by sortByName string obtained from the dialog
        Collections.sort(list, new Comparator<Episode>() {
            @Override
            public int compare(Episode lhs, Episode rhs) {
                return sortBy(lhs, rhs, sortByStr);
            }
        });

        //put the sorted
        EpisodeListAdapter adapter = new EpisodeListAdapter(list);
        episodesListView.setAdapter(adapter);


    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public int sortBy(Episode epi1, Episode epi2, String sortByString) {
        switch (sortByString) {
            case "Name":
                return epi1.getName().compareToIgnoreCase(epi2.getName());
            case "Publication Date":
                return epi1.getPublicationDate().compareTo(epi2.getPublicationDate());
            case "Duration":
                //return Integer.compare(epi1.getDurationInSeconds(), epi2.getDurationInSeconds());
                return 0;
            case "Size":
                //TODO: should be Integer.compare(epi1.getSize(), epi2.getSize());
                return 0;
            case "Download Date":
                //TODO: downloaded dates need to be recorded
                return 0;
            case "Channel Priority":
                //TODO: priority needs to be set as well
                return 0;

        }
        //return by name by default
        return epi1.getName().compareToIgnoreCase(epi2.getName());
    }


    @Override
    protected void _onStart() {
        //////
        //Extracts the channel ID we are supposed to display
        Intent intent = getIntent();
        if (intent == null)
            return;

        String channelId = intent.getStringExtra(EXTRA_CHANNEL_ID);
        if (channelId == null || StringUtils.isBlank(channelId))
            return;

        channel = ChannelDAO.find(realm, channelId);
        if (channel == null)
            return;

        Bus.post(new ViewChannelDetailsEvent(channel.getID(), null));

        shortDescription.setText(channel.getDescription());
        longDescription.setText(channel.getDescription());
        channelName.setText(channel.getName());
        subscribeCheckbox.setChannel(DAO.detach(realm, channel));
        ImageLoader.getInstance().displayImage(channel.getThumbnailURL(), channelImage);

        populateEpisodesList(true);
        //Get ready to receive the new episodes that are fetched...
        Bus.registerForEvents(this);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onDownloadCompletedEvent(DownloadChannelEpisodesEvent.CompleteEvent event) {
        if (downloadChannelEpisodes != null && event != null &&
                event.getOriginalEvent() != null && event.getOriginalEvent().equals(downloadChannelEpisodes)) {
            Bus.consume(event);
            populateEpisodesList(false);
        }
    }

    @Override
    protected void _onStop() {
        Bus.unregister(this);
        channel = null;
        super._onStop();
    }

    protected void expandCollapseHeader() {
        expanded = !expanded;
        if (expanded) {
            shortDescription.setVisibility(View.GONE);
            longDescription.setVisibility(View.VISIBLE);
        } else {
            shortDescription.setVisibility(View.VISIBLE);
            longDescription.setVisibility(View.GONE);
        }
    }

    /**
     * Populates the listview of episodes.  it will first look into the database if the channel has any episodes. If so it will
     * display those.  Otherwise, if this is the first load (issueEpisodeDownloadRequest == true) then it will issue a request for Channel's episodes
     * to be downloaded.
     *
     * @param issueEpisodeDownloadRequest Whether to issue the request to download the episodes if the channel has no episodes.
     */
    protected void populateEpisodesList(boolean issueEpisodeDownloadRequest) {
        if (channel == null)
            return;
        List<Episode> episodes = EpisodeDAO.forChannel(realm, channel.getID());

        if ((episodes == null || episodes.size() == 0) && issueEpisodeDownloadRequest) {
            downloadChannelEpisodes = new DownloadChannelEpisodesEvent(channel.getID());
            Bus.post(downloadChannelEpisodes);
        } else {
            EpisodeListAdapter adapter = new EpisodeListAdapter(episodes);
            episodesListView.setAdapter(adapter);
        }
    }
}
