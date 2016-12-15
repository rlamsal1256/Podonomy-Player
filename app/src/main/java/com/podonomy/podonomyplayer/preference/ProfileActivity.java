package com.podonomy.podonomyplayer.preference;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.util.Log;

import com.podonomy.podonomyplayer.R;
import com.podonomy.podonomyplayer.dao.Configuration;
import com.podonomy.podonomyplayer.dao.ConfigurationDAO;
import com.podonomy.podonomyplayer.dao.DAO;

import io.realm.Realm;

public class ProfileActivity extends PreferenceActivity implements
        Preference.OnPreferenceClickListener,
        Preference.OnPreferenceChangeListener{
  /*@Override
  protected int getViewID(boolean withAds) {
    if (withAds)
      return R.layout.profile;
    return R.layout.profile_noads;
  }*/
  private static String TAG = "ProfileActivity";
  private ListPreference apply_themePreference;          //theme setting

  @Override
  public void onCreate(Bundle savedInstanceState) {
    Realm realm = DAO.getRealm(getBaseContext());
    final Configuration config = ConfigurationDAO.getConfig(realm);
    setTheme(config.getThemeID());
    super.onCreate(savedInstanceState);

    addPreferencesFromResource(R.xml.preference);
    apply_themePreference = (ListPreference) findPreference("apply_theme");
    apply_themePreference.setOnPreferenceClickListener(this);
    apply_themePreference.setOnPreferenceChangeListener(this);
  }

  // on click
  @Override
  public boolean onPreferenceClick(Preference preference) {
    Log.i(TAG, "onPreferenceClick----->"+String.valueOf(preference.getKey()));
    return false;
  }

  // if value of Preference change, if true update, false do noting
  // do change in config
  //TODO: need to refersh the project to get every view theme reset
  public boolean onPreferenceChange(Preference preference, Object objValue) {

    Realm realm = DAO.getRealm(getBaseContext());
    Log.i(TAG, "onPreferenceChange----->" + String.valueOf(preference.getKey()));
    if (preference == apply_themePreference) {
      Log.i(TAG, "  Old Value: " + apply_themePreference.getValue() + " New Value: " + objValue);
      if (objValue.equals(getResources().getStringArray(R.array.theme_value)[0]))
        ConfigurationDAO.updateConfigThemeID(realm, R.style.Podonomy_Default);
      else if (objValue.equals(getResources().getStringArray(R.array.theme_value)[1]))
        ConfigurationDAO.updateConfigThemeID(realm, R.style.Podonomy_Dark);
      System.exit(2);//this will restart the app, set all view with new theme
    }
    return true;
  }
}
