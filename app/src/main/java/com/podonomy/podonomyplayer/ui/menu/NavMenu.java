package com.podonomy.podonomyplayer.ui.menu;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.podonomy.podonomyplayer.R;
import com.podonomy.podonomyplayer.dao.DAO;
import com.podonomy.podonomyplayer.preference.ProfileActivity;
import com.podonomy.podonomyplayer.ui.downloads.DownloadsActivity;
import com.podonomy.podonomyplayer.ui.player.PlayerActivity;
import com.podonomy.podonomyplayer.ui.playlists.PlaylistsActivity;
import com.podonomy.podonomyplayer.ui.subscriptions.SubscriptionsActivity;

import io.realm.Realm;

/**
 * Created by Francois on 3/4/2016.
 */
public class NavMenu extends Dialog implements TextView.OnClickListener {
  private String[] arrayItems;
  private Context context;

  public NavMenu(Context context) {
    super(context);
    this.context = context;

    if (context instanceof Activity)
      super.setOwnerActivity((Activity) context);
  }

  public NavMenu(Context context, int themeResId) {
    super(context, themeResId);
    this.context = context;
    if (context instanceof Activity)
      super.setOwnerActivity((Activity) context);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getWindow().getAttributes().gravity = Gravity.LEFT | Gravity.BOTTOM;
    getWindow().getAttributes().width = 200;
    this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.navigation_menu);

    arrayItems = getContext().getResources().getStringArray(R.array.navmenu_items);
    NavMenuAdapter arr = new NavMenuAdapter(getContext(), R.layout.navigation_menu_list_itm, R.id.navmenu_list_itm_textview, arrayItems);
    ListView list = (ListView) (findViewById(R.id.navmenu_listview));
    list.setAdapter(arr);

    //Adjust the size of the episode image in the player row...
    ImageButton episodeImageButton = (ImageButton) findViewById(R.id.episodeImageButton);
    ImageButton previousButton = (ImageButton) findViewById(R.id.previousButton);

    if (episodeImageButton != null && previousButton != null){
      episodeImageButton.setMinimumWidth(previousButton.getWidth());
      episodeImageButton.setMaxWidth(previousButton.getWidth());
      episodeImageButton.setMinimumHeight(previousButton.getHeight());
      episodeImageButton.setMaxHeight(previousButton.getHeight());
    }

  }

  @Override
  public void onClick(View v) {
    this.dismiss();
    TextView tv = (TextView) v;
    if (tv == null || tv.getText() == null)
      return;

    String itm = tv.getText().toString();
    Intent intent = null;
    Toast toast;

    if (getContext().getResources().getString(R.string.navmenu_itm_playlists).equals(itm))
      intent = new Intent(getContext(), PlaylistsActivity.class);
    else if (getContext().getResources().getString(R.string.navmenu_itm_profile).equals(itm))
      intent = new Intent(getContext(), ProfileActivity.class);
    else if (getContext().getResources().getString(R.string.navmenu_itm_subscriptions).equals(itm))
      intent = new Intent(getContext(), SubscriptionsActivity.class);
    else if (getContext().getResources().getString(R.string.navmenu_itm_player).equals(itm))
      intent = new Intent(getContext(), PlayerActivity.class);
    else if (getContext().getResources().getString(R.string.navmenu_itm_downloads).equals(itm))
      intent = new Intent(getContext(), DownloadsActivity.class);
    else if ("Reset Database".equalsIgnoreCase(itm)){
      //Todo: Remove this prior to releasing software
      Realm r = DAO.getRealm(getContext());
      DAO.createFakeDB(r);
    }
    else
      Toast.makeText(getContext(), "UNKNOWN SELECTION", Toast.LENGTH_LONG).show();
    if (intent  == null)
      return;
    getOwnerActivity().startActivity(intent);;
  }

  class NavMenuAdapter extends ArrayAdapter<String> {
    public NavMenuAdapter(Context context, int resource, int textViewResourceId, String[] objects) {
      super(context, resource, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      View v = super.getView(position, convertView, parent);
      TextView tv = (TextView) v.findViewById(R.id.navmenu_list_itm_textview);
      tv.setOnClickListener(NavMenu.this);
      return v;
    }
  }
}

