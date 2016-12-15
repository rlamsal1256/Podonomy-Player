package com.podonomy.podonomyplayer.ui.player.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.podonomy.podonomyplayer.R;
import com.podonomy.podonomyplayer.dao.Episode;
import com.podonomy.podonomyplayer.service.player.Player;

/**
 * Fragement that displays the description of the current episode
 */
public class ItemDescriptionFragment extends Fragment {
    private static final String TAG = "ItemDescriptionFragment";


    private Episode currentEpisode = null;
    private WebView webvDescription;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.player_item_description_fragment, container, false);

        webvDescription= (WebView) root.findViewById(R.id.episode_description);

        currentEpisode = Player.getInstance().getCurrentEpisode();
        loadMediaInfo();

        return root;
    }

    private void loadMediaInfo() {

        if (currentEpisode != null){
            webvDescription.loadData(currentEpisode.getDescription(), "text/html", "utf-8");
        } else{
            Log.w(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> episode is null");
        }

    }
}
