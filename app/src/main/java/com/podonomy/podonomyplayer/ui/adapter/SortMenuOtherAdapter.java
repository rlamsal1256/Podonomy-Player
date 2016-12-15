package com.podonomy.podonomyplayer.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.podonomy.podonomyplayer.R;

import java.util.ArrayList;

/**
 * Created by rlamsal on 6/6/2016.
 */
public class SortMenuOtherAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<String> originalList = new ArrayList<String>();
    private ArrayList<String> otherList = new ArrayList<String>();
    private Context context;



    public SortMenuOtherAdapter(ArrayList<String> list1, ArrayList<String> list2, Context context) {
        this.originalList = list1;
        this.otherList = list2;
        this.context = context;
    }

    @Override
    public int getCount() {
        return originalList.size();
    }

    @Override
    public Object getItem(int pos) {
        return originalList.get(pos);
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
            view = inflater.inflate(R.layout.playlist_sort_other_options_menu, null);
        }
        //Handle TextView and display string from your list
        TextView listItemText = (TextView)view.findViewById(R.id.sm_other_list_item_string);
        listItemText.setText(originalList.get(position));

        //Handle buttons and add onClickListeners
        Button ascBtn = (Button)view.findViewById(R.id.sm_ascending_btn);
        Button desBtn = (Button)view.findViewById(R.id.sm_descending_btn);

        ascBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //remove from the current list and add it to the other list
                otherList.add(originalList.get(position) + "  " + context.getString(R.string.ascending_label));
                originalList.remove(position);

                notifyDataSetChanged();
            }
        });
        desBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //remove from the current list and add it to the other list
                otherList.add(originalList.get(position) + " " + context.getString(R.string.descending_label));
                originalList.remove(position);
                notifyDataSetChanged();
            }
        });

        return view;
    }
}