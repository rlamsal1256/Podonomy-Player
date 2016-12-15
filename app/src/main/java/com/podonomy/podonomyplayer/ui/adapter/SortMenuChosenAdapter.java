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
public class SortMenuChosenAdapter extends BaseAdapter implements ListAdapter{
    private ArrayList<String> originalList = new ArrayList<String>();
    private ArrayList<String> otherList = new ArrayList<String>();
    private Context context;



    public SortMenuChosenAdapter(ArrayList<String> list1, ArrayList<String> list2, Context context) {
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
            view = inflater.inflate(R.layout.playlist_sort_chosen_menu, null);
        }
        //Handle TextView and display string from your list
        TextView listItemText = (TextView)view.findViewById(R.id.sm_chosen_list_item_string);
        listItemText.setText(originalList.get(position));

        //Handle buttons and add onClickListeners
        Button delBtn = (Button)view.findViewById(R.id.sm_delete_option_btn);

        delBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //remove from the current list and add it to the other list
                //also delete the last 7 characters. either "  (asc)" or " (desc)"
                String str = originalList.get(position);
                if (str.length() > 7) {
                    otherList.add(str.substring(0, str.length() - 7));
                    originalList.remove(position);
                }
                notifyDataSetChanged();

            }
        });

        return view;
    }
}