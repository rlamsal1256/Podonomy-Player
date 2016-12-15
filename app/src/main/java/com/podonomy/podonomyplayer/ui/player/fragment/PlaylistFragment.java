package com.podonomy.podonomyplayer.ui.player.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.podonomy.podonomyplayer.R;
import com.podonomy.podonomyplayer.dao.PlayList;
import com.podonomy.podonomyplayer.service.player.Player;
import com.podonomy.podonomyplayer.ui.playlists.PlayListAdapter;

/**
 * Fragement that displays the playlist of the current episode
 */
public class PlaylistFragment extends Fragment {

    private static final String TAG = "PlaylistFragment";

    private PlayList currentPlaylist = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.play_list_fragment, container, false);

        ListView episodesListView = (ListView) root.findViewById(R.id.playlist_episodeListView);

        currentPlaylist = Player.getInstance().getCurrentPlaylist();

        if (currentPlaylist != null) {
            PlayListAdapter adapter = new PlayListAdapter(currentPlaylist.getContent());
            episodesListView.setAdapter(adapter);
        }


        return root;
    }


}
