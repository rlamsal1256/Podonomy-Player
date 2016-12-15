package com.podonomy.podonomyplayer.ui.playlists;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;

import com.podonomy.podonomyplayer.dao.Episode;

import java.util.List;

/**
 * Created by rlamsal on 8/2/2016.
 */
public class PlayListAdapter extends BaseAdapter implements ListAdapter {
    protected List<Episode> episodes = null;

    public PlayListAdapter(List<Episode> episodeList) {
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

            view = new PlayListViewItem(parent.getContext(), episode);
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

