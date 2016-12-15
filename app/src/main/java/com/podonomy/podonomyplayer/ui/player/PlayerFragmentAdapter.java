package com.podonomy.podonomyplayer.ui.player;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.podonomy.podonomyplayer.ui.player.fragment.CoverFragment;
import com.podonomy.podonomyplayer.ui.player.fragment.ItemDescriptionFragment;
import com.podonomy.podonomyplayer.ui.player.fragment.PlaylistFragment;

/**
 * Created by rlamsal on 7/25/2016.
 */
public class PlayerFragmentAdapter extends FragmentStatePagerAdapter {

    public static final String TAG = "PlayerFragmentAdapter";
    private static final int POS_PLAYLISTS = 0;
    private static final int POS_COVER = 1;
    private static final int POS_DESCRIPTIONS = 2;

    private static final int NUM_CONTENT_FRAGMENTS = 3;

    private CoverFragment coverFragment;
    private ItemDescriptionFragment itemDescriptionFragment;
    private PlaylistFragment playlistFragment;


    public PlayerFragmentAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        Log.d(TAG, "getItem(" + position + ")");
        switch (position) {
            case POS_PLAYLISTS:
                if(playlistFragment == null) {
                    playlistFragment = new PlaylistFragment();
                }
                return playlistFragment;
            case POS_COVER:
                if(coverFragment == null) {
                    coverFragment = new CoverFragment();
                }
                return coverFragment;
            case POS_DESCRIPTIONS:
                if(itemDescriptionFragment == null) {
                    itemDescriptionFragment = new ItemDescriptionFragment();
                }
                return itemDescriptionFragment;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return NUM_CONTENT_FRAGMENTS;
    }
}
