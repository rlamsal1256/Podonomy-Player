package com.podonomy.podonomyplayer.ui.channeldetails;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;

import com.podonomy.podonomyplayer.dao.Episode;

import java.util.List;

/**
 * Created by Francois on 4/12/2016.
 */
public class EpisodeListAdapter extends BaseAdapter implements ListAdapter{
  protected List<Episode> episodes = null;

  public EpisodeListAdapter(List<Episode> episodeList) {
    episodes = episodeList;
  }

  @Override
  public int getCount() {
    if (episodes == null)
      return 0;
    return episodes.size();
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    View view = convertView;

    if (view == null){
      Episode episode = getItem(position);

      if (episode == null || parent == null)
        return null;  //nothing to do

       view = new EpisodeListViewItem(parent.getContext(), episode);
    }

    return view;
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public Episode getItem(int position) {
    if (episodes == null)
      return null;
    if (position >= episodes.size() || position < 0)
      return null;
    return episodes.get(position);
  }



}
