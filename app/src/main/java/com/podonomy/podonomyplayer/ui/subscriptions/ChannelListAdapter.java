package com.podonomy.podonomyplayer.ui.subscriptions;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;

import com.podonomy.podonomyplayer.dao.Category;
import com.podonomy.podonomyplayer.dao.Channel;
import com.podonomy.podonomyplayer.event.EventLogger;

import java.util.ArrayList;
import java.util.List;

/**
 * This Adapter links a ListView with the list of channels the user subscribes to.  It will produce
 * {@link ChannelListViewItem} object for the list.
 */
public class ChannelListAdapter extends BaseAdapter implements ListAdapter {

  private static final String TAG = "ChannelListAdapter";
  private List<Channel> channels = new ArrayList<>();

  public ChannelListAdapter(List<Channel> channels){
    if (channels != null)
       this.channels = channels;
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public int getCount() {
    return channels.size();
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    Channel channel = channels.get(position);

    String result= "";
    for (Category c: channel.getCategories()){
      result = result.concat(c.getName() + " , ");
    }

    EventLogger.getLogger().debug(TAG, "Channel name:" + channel.getName() + "::::::::::" + "Channel categories:" + result );

    if (channel == null || parent == null )
      return null;

    ChannelListViewItem channelListItem = new ChannelListViewItem(parent.getContext(), channel);
    return channelListItem;
  }

  @Override
  public Object getItem(int position) {
    return channels.get(position);
  }
}
