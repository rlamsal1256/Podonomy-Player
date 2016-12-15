package com.podonomy.podonomyplayer.ui.settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.podonomy.podonomyplayer.R;

/**
 * Created by rlamsal on 8/17/2016.
 */

public class SettingsActivityFrament extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preference);


    }
}
