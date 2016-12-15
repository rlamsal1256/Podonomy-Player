package com.podonomy.podonomyplayer.ui.player.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.podonomy.podonomyplayer.R;
import com.podonomy.podonomyplayer.dao.Episode;
import com.podonomy.podonomyplayer.service.player.Player;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Fragement that displays the cover image of the current episode
 */
public class CoverFragment extends Fragment {

    private static final String TAG = "CoverFragment";

    private Episode currentEpisode = null;

    private ImageView imgvCover;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.cover_fragment, container, false);
        imgvCover = (ImageView) root.findViewById(R.id.imgvCover);
        currentEpisode = Player.getInstance().getCurrentEpisode();
        loadMediaInfo();
        return root;
    }

    private void loadMediaInfo() {
        if (currentEpisode != null) {

            if (!isBlank(currentEpisode.getThumbnailURL()))
            ImageLoader.getInstance().displayImage(currentEpisode.getThumbnailURL(), imgvCover);
        else if (!isBlank(currentEpisode.getChannel().getThumbnailURL()))
            ImageLoader.getInstance().displayImage(currentEpisode.getChannel().getThumbnailURL(), imgvCover);


        } else {
            Log.w(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>loadMediaInfo was called while episode was null");
        }
    }
}
