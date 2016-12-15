package com.podonomy.podonomyplayer.ui.subscriptions;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.podonomy.podonomyplayer.R;
import com.podonomy.podonomyplayer.dao.Channel;
import com.podonomy.podonomyplayer.event.Bus;
import com.podonomy.podonomyplayer.event.EventLogger;
import com.podonomy.podonomyplayer.event.ViewChannelDetailsEvent;
import com.podonomy.podonomyplayer.ui.SubscribeCheckbox;
import com.podonomy.podonomyplayer.ui.channeldetails.ChannelDetails;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This is an ListItem representing a {@link Channel} that is to be inserted in a ListView (i.e. a list of channel).
 */
public class ChannelListViewItem extends LinearLayout {
    private static final String TAG = "ChannelListViewItem";
    private Channel channel = null;
    private ImageButton channelImageButton;
    private TextView channelName;
    private TextView numberOfEpisodes;
    private TextView lastPubDate;
    private SubscribeCheckbox subscribedCheckbox;
    private long eventID = 0L;

    public ChannelListViewItem(Context context) {
        super(context);
    }

    public ChannelListViewItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ChannelListViewItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ChannelListViewItem(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);
    }

    public ChannelListViewItem(Context context, Channel channel) {
        super(context);
        this.channel = channel;
        init(context);
    }

    private void init(Context context) {
        if (channel == null)
            return;

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.subscriptions_subscription_list_view_item, this, true);
        channelImageButton = (ImageButton) findViewById(R.id.subscriptions_subscriptions_list_view_item_channelImageButton);
        channelName = (TextView) findViewById(R.id.subscriptions_subscriptions_list_view_item_channelName);
        numberOfEpisodes = (TextView) findViewById(R.id.subscriptions_subscriptions_list_view_item_numberOfEpisodes);
        lastPubDate = (TextView) findViewById(R.id.subscriptions_subscriptions_list_view_item_lastPubDate);
        subscribedCheckbox = (SubscribeCheckbox) findViewById(R.id.subscriptions_subscriptions_list_view_item_subscribedCheckbox);
        subscribedCheckbox.setChannel(channel);
        channelImageButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onImageClicked(v);
            }
        });

        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onImageClicked(v);
            }
        });


        updateUI();
    }

    public void setChannelName(String s) {
        if (s == null)
            s = "";
        if (channelName != null)
            channelName.setText(s);
    }

    public void setNumberOfEpisodes(int n) {
        if (numberOfEpisodes != null && numberOfEpisodes.getContext() != null)
            numberOfEpisodes.setText("" + n + numberOfEpisodes.getContext().getResources().getString(R.string.subscriptions_list_view_item_episodes));
    }

    public void setLastPubDate(Date date) {
        if (lastPubDate != null && date != null) {
            //todo: fix date format to use user's locale format.
            SimpleDateFormat format = new SimpleDateFormat();
            lastPubDate.setText(format.format(date));
        }
    }

    /**
     * Sets the subscribed checkbox accordingly to the channel.
     */
    public void setSubscribed(boolean s) {
        if (subscribedCheckbox != null)
            subscribedCheckbox.setChecked(s);
    }


    private void updateUI() {
        setChannelName(channel.getName());
        setLastPubDate(channel.getPublicationDate());
        try {
            if (channel.getThumbnailURL() != null) {
                ImageLoader.getInstance().displayImage(channel.getThumbnailURL(), channelImageButton);
            }
        } catch (Exception e) {
            EventLogger.getLogger().debug(TAG, e);
        }
    }

    private void onImageClicked(View v) {
        if (channel == null) {
            EventLogger.getLogger().debug(TAG, ".onImageClicked", " Channel is null so event won't fire.");
            return;
        }

        ViewChannelDetailsEvent vcd = new ViewChannelDetailsEvent(channel.getID(), channel.getSiteURL());
        Bus.post(vcd);
        Intent intent = new Intent(getContext(), ChannelDetails.class);
        intent.putExtra(ChannelDetails.EXTRA_CHANNEL_ID, channel.getID());
        getContext().startActivity(intent);
    }
}
