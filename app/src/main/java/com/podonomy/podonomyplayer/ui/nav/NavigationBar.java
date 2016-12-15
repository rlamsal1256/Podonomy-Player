package com.podonomy.podonomyplayer.ui.nav;

import android.app.Activity;
import android.view.View;
import android.widget.ImageButton;

import com.podonomy.podonomyplayer.R;
import com.podonomy.podonomyplayer.ui.menu.NavMenu;
import com.podonomy.podonomyplayer.ui.menu.SortMenu;

/**
 * Created by Francois on 3/7/2016.
 */
public class NavigationBar {
    private Activity activity;
    private ImageButton navButton = null;
    private ImageButton sortButton = null;

    public void onCreate(Activity act) {
        this.activity = act;

        navButton = (ImageButton) act.findViewById(R.id.navButton);
        if (navButton == null)
            throw new RuntimeException("The navigation_bar layout was not included as part of your layout");

        navButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        NavMenu dialog = new NavMenu(activity);
                        dialog.show();
                    }
                }
        );

        sortButton = (ImageButton) act.findViewById(R.id.navBar_sort);
        if (sortButton == null)
            throw new RuntimeException("The navigation_bar layout was not included as part of your layout");

        sortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SortMenu sortDialog = new SortMenu(activity);
                sortDialog.show();
            }
        });
    }

//  public void addButton(ImageButton button){
//    addView(button);
//  }
//  public void addSpinner(Spinner spinner){
//    addView(spinner);
//  }

    public void setNavImage(int resourceID) {
        if (navButton != null) {
            navButton.setImageResource(resourceID);
        }
    }

//  private void addView(View v){
//    if (activity == null || v == null )
//      return ;
//    LinearLayout middleLayout = (LinearLayout) activity.findViewById(R.id.navBarMiddleLayout);
//    middleLayout.addView(v);
//
//  }
}
