package com.podonomy.podonomyplayer.ui.menu;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.podonomy.podonomyplayer.R;
import com.podonomy.podonomyplayer.ui.adapter.SortMenuChosenAdapter;
import com.podonomy.podonomyplayer.ui.adapter.SortMenuOtherAdapter;

import java.util.ArrayList;

/**
 * Created by rlamsal on 6/3/2016.
 */
public class SortMenu extends Dialog {

    private ArrayList<String> chosenList = new ArrayList<String>();
    private ArrayList<String> otherOptionsList = new ArrayList<String>();
    private Context context;

    public SortMenu(Context context) {
        super(context);
        this.context = context;

        if (context instanceof Activity)
            super.setOwnerActivity((Activity) context);
    }

    public SortMenu(Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
        if (context instanceof Activity)
            super.setOwnerActivity((Activity) context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().getAttributes().gravity = Gravity.CENTER;
//        getWindow().getAttributes().height = 200;
//        getWindow().getAttributes().width = 200;
        String title = getContext().getString(R.string.playlist_sort_menu_title);
        getWindow().setTitle(title);

        setContentView(R.layout.playlist_sort_menu);

        chosenList.add(getContext().getString(R.string.playlist_sort_name) + "  " + getContext().getString(R.string.ascending_label));

        otherOptionsList.add(getContext().getString(R.string.playlist_sort_publication_date));
        otherOptionsList.add(getContext().getString(R.string.playlist_sort_duration));
        otherOptionsList.add(getContext().getString(R.string.playlist_sort_size));
        otherOptionsList.add(getContext().getString(R.string.playlist_sort_download_date));
        otherOptionsList.add(getContext().getString(R.string.playlist_sort_channel_priority));

        SortMenuChosenAdapter arr1 = new SortMenuChosenAdapter(chosenList, otherOptionsList, getContext());
        SortMenuOtherAdapter arr2 = new SortMenuOtherAdapter(otherOptionsList, chosenList, getContext());

        ListView list1 = (ListView) (findViewById(R.id.sm_chosen_items));
        list1.setAdapter(arr1);

        ListView list2 = (ListView) (findViewById(R.id.sm_other_items));
        list2.setAdapter(arr2);

        Button cancelBtn = (Button) findViewById(R.id.playlist_sort_menu_cancel_button);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do nothing. return to default
                dismiss();
                return;
            }
        });

        Button okBtn = (Button) findViewById(R.id.playlist_sort_menu_ok_button);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO:save changes and do the sorting according to user preference

                Toast.makeText(getContext(), (String)"Playlist sorted...",
                        Toast.LENGTH_SHORT).show(); //just for showing. remove this later

                dismiss();
                return;
            }
        });
    }


}
