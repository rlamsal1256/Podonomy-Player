package com.podonomy.podonomyplayer.ui.menu;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.podonomy.podonomyplayer.R;
import com.podonomy.podonomyplayer.ui.adapter.EpisodeSortMenuAdapter;
import com.podonomy.podonomyplayer.ui.channeldetails.ChannelDetails;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by rlamsal on 6/7/2016.
 */
public class EpisodeSortMenu extends Dialog {

    private String[] arrayItems;
    private ArrayList<String> listItems;
    private Context context;
    private String sortByString = "";

    public String getSortByString() {
        return sortByString;
    }

    public EpisodeSortMenu(Context context) {
        super(context);
        this.context = context;
        if (context instanceof Activity)
            super.setOwnerActivity((Activity) context);
    }

    public EpisodeSortMenu(Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;

        if (context instanceof Activity)
            super.setOwnerActivity((Activity) context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String title = getContext().getString(R.string.episode_sort_menu_title);
        getWindow().setTitle(title);
        getWindow().setGravity(Gravity.CENTER);
        setContentView(R.layout.episode_sort_menu);

        arrayItems = getContext().getResources().getStringArray(R.array.episode_sort_menu_items);
        listItems = new ArrayList<String>(Arrays.asList(arrayItems));

        final EpisodeSortMenuAdapter arr = new EpisodeSortMenuAdapter(listItems, getContext());

        final ListView list = (ListView) (findViewById(R.id.episode_sort_menu_listview));
        list.setAdapter(arr);

        Button cancelBtn = (Button) findViewById(R.id.episode_sort_menu_cancel_button);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do nothing. return to default
                dismiss();
                return;
            }
        });


        Button okBtn = (Button) findViewById(R.id.episode_sort_menu_ok_button);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //get position string of the radiobutton
                sortByString = arr.getSelectedPositionName();
                if (sortByString == null){
                    sortByString = "Name";//if position not changed, then set name by default
                }

                Toast.makeText(getContext(), (String)"Episode list sorted by " + sortByString,
                        Toast.LENGTH_SHORT).show(); //just for showing. remove this later

                ChannelDetails cd = (ChannelDetails) context;
                cd.onSort(sortByString);

                dismiss();
                return;
            }
        });
    }
}