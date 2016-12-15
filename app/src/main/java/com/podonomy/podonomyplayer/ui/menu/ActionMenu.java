package com.podonomy.podonomyplayer.ui.menu;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.podonomy.podonomyplayer.R;
import com.podonomy.podonomyplayer.ui.playlists.PlaylistsActivity;

/**
 * Created by Rupak on 6/7/2016.
 */
public class ActionMenu extends Dialog implements TextView.OnClickListener {
    private String[] arrayItems;
    private Context context;

    public ActionMenu(Context context) {
        super(context);
        this.context = context;

        if (context instanceof Activity)
            super.setOwnerActivity((Activity) context);
    }

    public ActionMenu(Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
        if (context instanceof Activity)
            super.setOwnerActivity((Activity) context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getAttributes().gravity = Gravity.RIGHT | Gravity.BOTTOM;
        getWindow().getAttributes().width = 200;
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.playlist_action_menu);

        arrayItems = getContext().getResources().getStringArray(R.array.playlist_action_menu_items);
        ActionMenuAdapter arr = new ActionMenuAdapter(getContext(), R.layout.navigation_menu_list_itm, R.id.navmenu_list_itm_textview, arrayItems);
        ListView list = (ListView) (findViewById(R.id.playlist_action_menu_listview));
        list.setAdapter(arr);


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

        if (getContext().getResources().getString(R.string.new_playlist_action).equals(itm)){
            //TODO: create a new playlist
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
            alertDialog.setTitle("New Playlist");
            alertDialog.setMessage("Playlist Name:");

            final EditText input = new EditText(getContext());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            input.setLayoutParams(lp);
            alertDialog.setView(input);

            alertDialog.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            String playlistName = input.getText().toString();

                            PlaylistsActivity playlistsActivity = (PlaylistsActivity) getOwnerActivity();
                            playlistsActivity.createNewPlaylist(playlistName);

                            Toast.makeText(getContext(),
                                    "Playlist Name entered:  " + playlistName, Toast.LENGTH_SHORT).show();
                        }

                    });

            alertDialog.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

            alertDialog.show();



        }
        else if (getContext().getResources().getString(R.string.delete_playlist_action).equals(itm)){
            //TODO: delete the playlist currently selected in the dropdown
        }
        else if (getContext().getResources().getString(R.string.navmenu_itm_subscriptions).equals(itm)){
            //TODO: open playlist settings
        }
        else if (getContext().getResources().getString(R.string.navmenu_itm_player).equals(itm)){
            //TODO: play playlist
        }
        else
            Toast.makeText(getContext(), "UNKNOWN SELECTION", Toast.LENGTH_LONG).show();
        if (intent  == null)
            return;
    }

    class ActionMenuAdapter extends ArrayAdapter<String> {
        public ActionMenuAdapter(Context context, int resource, int textViewResourceId, String[] objects) {
            super(context, resource, textViewResourceId, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = super.getView(position, convertView, parent);
            TextView tv = (TextView) v.findViewById(R.id.navmenu_list_itm_textview);
            tv.setOnClickListener(ActionMenu.this);
            return v;
        }
    }
}

