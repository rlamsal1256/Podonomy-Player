package com.podonomy.podonomyplayer.ui.playlists;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Spinner;

import com.podonomy.podonomyplayer.Player_AppCompatActivity;
import com.podonomy.podonomyplayer.R;
import com.podonomy.podonomyplayer.dao.Episode;
import com.podonomy.podonomyplayer.dao.PlayList;
import com.podonomy.podonomyplayer.dao.PlayListDAO;
import com.podonomy.podonomyplayer.ui.settings.SettingsActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Francois on 3/7/2016.
 */
public class PlaylistsActivity extends Player_AppCompatActivity {

    protected Spinner playlistComboBox;
    protected ArrayList<String> playListNames = new ArrayList<String>();
    protected ArrayAdapter<String> adapter;
    protected ListView episodesListView = null;

    public PlaylistsActivity() {
        super(null);
    }

    @Override
    protected int getViewID(boolean withAds) {
        if (withAds)
            return R.layout.playlist;
        return R.layout.playlist_noads;
    }

    @Override
    protected void _onCreate(Bundle savedInstanceState) {
        super._onCreate(savedInstanceState);

        episodesListView = (ListView) findViewById(R.id.playlist_episodeListView);

        playlistComboBox = (Spinner) findViewById(R.id.navBar_spinner);
        playlistComboBox.setVisibility(View.VISIBLE);

        ImageButton sortButton = (ImageButton) findViewById(R.id.navBar_sort) ;
        sortButton.setVisibility(View.VISIBLE);

        updateSpinner();

        final ImageButton actionBtn = (ImageButton) findViewById(R.id.actionButton);
        if (actionBtn == null)
            throw new RuntimeException("The navigation_bar layout was not included as part of your layout");

        actionBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        ActionMenu dialog = new ActionMenu(PlaylistsActivity.this);
//                        dialog.show();

                        //Creating the instance of PopupMenu
                        PopupMenu popup = new PopupMenu(PlaylistsActivity.this, actionBtn);
                        //Inflating the Popup using xml file
                        popup.getMenuInflater()
                                .inflate(R.menu.playlist_action_menu, popup.getMenu());

                        //registering popup with OnMenuItemClickListener
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            public boolean onMenuItemClick(MenuItem item) {

                                if (getBaseContext().getResources().getString(R.string.new_playlist_action).equals(item.toString())){

                                    openNewPlaylistDialog();

                                } else if (getBaseContext().getResources().getString(R.string.delete_playlist_action).equals(item.toString())){

                                    AlertDialog.Builder builder = new AlertDialog.Builder(PlaylistsActivity.this);
                                    builder.setMessage("Are you sure you want to delete '" + playlistComboBox.getSelectedItem().toString() + "'?")
                                            .setPositiveButton("Yes", deletePlaylist)
                                            .setNegativeButton("No", deletePlaylist)
                                            .show();

                                } else if (getBaseContext().getResources().getString(R.string.settings_action).equals(item.toString())){
                                    Intent intent = new Intent(PlaylistsActivity.this, SettingsActivity.class);
                                    startActivity(intent);
                                }
                                return true;
                            }
                        });

                        popup.show(); //showing popup menu


                    }
                }
        );


        //PlayList playList = PlayListDAO.getByID(playlistComboBox.getSelectedItem().toString(), realm);
        PlayList playList = PlayListDAO.getDownloadedPlaylist(realm);

        //get all the list of episodes
        ArrayList<Episode> list = new ArrayList<Episode>(playList.getContent());

        //
        PlayListAdapter adapter = new PlayListAdapter(list);
        episodesListView.setAdapter(adapter);

        playlistComboBox.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                PlayList playList = PlayListDAO.getByID(playlistComboBox.getItemAtPosition(position).toString(), realm);

                ArrayList<Episode> list = new ArrayList<Episode>(playList.getContent());

                PlayListAdapter adapter = new PlayListAdapter(list);
                episodesListView.setAdapter(adapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void updateSpinner(){

        if (playListNames != null){
            playListNames.clear();
        }

        //accessing all playlists from database displaying in the spinner
        for (PlayList p: PlayListDAO.getPlaylists(realm)) {
            playListNames.add(p.getName());
        }
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, playListNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        playlistComboBox.setAdapter(adapter);

    }

    public void openNewPlaylistDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("New Playlist");
        alertDialog.setMessage("Playlist Name:");

        final EditText input = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);

        alertDialog.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String playlistName = input.getText().toString();
                        createNewPlaylist(playlistName);
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

    public void createNewPlaylist(String playlistName){
        realm.beginTransaction();
        PlayList playList = PlayListDAO.nnew(realm);
        playList.setName(playlistName);
        realm.commitTransaction();

        updateSpinner();
        int spinnerPosition = adapter.getPosition(playlistName);
        playlistComboBox.setSelection(spinnerPosition);
    }

    DialogInterface.OnClickListener deletePlaylist = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    //Yes button clicked. delete the playlist if possible

                    List<PlayList> p = PlayListDAO.getPlaylists(realm);
                    int spinnerPosition = adapter.getPosition(playlistComboBox.getSelectedItem().toString());
                    realm.beginTransaction();
                    for (int i=0; i < p.size(); i++){
                        if (playlistComboBox.getSelectedItem().toString().equals(p.get(i).getName())){

                            p.get(i).removeFromRealm();
                        }
                    }
                    realm.commitTransaction();

                    updateSpinner();
                    playlistComboBox.setSelection(spinnerPosition - 1);
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked. do nothing

                    break;
            }
        }
    };




}
