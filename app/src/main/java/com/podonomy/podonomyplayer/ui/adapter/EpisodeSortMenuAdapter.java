package com.podonomy.podonomyplayer.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.podonomy.podonomyplayer.R;

import java.util.ArrayList;

/**
 * Created by rlamsal on 6/6/2016.
 */
public class EpisodeSortMenuAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<String> list = new ArrayList<String>();
    private Context context;

    //keeps track of which radio button is selected
    private int selectedPosition = 0;
    private String selectedPositionName = "";

    public String getSelectedPositionName() {
        return selectedPositionName;
    }

    public EpisodeSortMenuAdapter(ArrayList<String> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int pos) {
        return list.get(pos);
    }

    @Override
    public long getItemId(int position) {
        return 0; //no ID
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.episode_sort_menu_itm, null);
        }
        //Handle TextView and display string from your list
        final TextView listItemText = (TextView) view.findViewById(R.id.episode_sort_menu_list_item_textview);
        listItemText.setText(list.get(position));

        //Handle buttons and add onClickListeners
        RadioButton radioButton = (RadioButton) view.findViewById(R.id.sort_menu_radio_btn);
        radioButton.setChecked(position == selectedPosition);

        //setting tag inside ListAdapter#getView() allows to determine the current position of radioButton
        radioButton.setTag(position);

        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectedPosition = (Integer) v.getTag();
                selectedPositionName = (String) list.get(selectedPosition);
                notifyDataSetChanged(); //all views will get updated, so only one radio button can be clicked at a time

            }
        });

        return view;
    }
}