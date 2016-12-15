package com.podonomy.podonomyplayer.ui.menu;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;

import com.podonomy.podonomyplayer.R;

/**
 * Created by rlamsal on 6/8/2016.
 */
public class TipMenu extends Dialog {

    private Context context;

    public TipMenu(Context context) {
        super(context);
        this.context = context;
        if (context instanceof Activity)
            super.setOwnerActivity((Activity) context);
    }

    public TipMenu(Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;

        if (context instanceof Activity)
            super.setOwnerActivity((Activity) context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getAttributes().gravity = Gravity.CENTER;
        getWindow().getAttributes().height = 200;
        getWindow().getAttributes().width = 200;
//        String title = getContext().getString(R.string.episode_sort_menu_title);
        getWindow().setTitle("Give tip for...");

        setContentView(R.layout.tip_menu);

        Button cancelBtn = (Button) findViewById(R.id.episode_sort_menu_cancel_button);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do nothing. return to default
                dismiss();
                return;
            }
        });




    }


}
