package com.podonomy.podonomyplayer.ui.nav;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Francois on 3/31/2016.
 */
public class ContextMenu extends Dialog implements TextView.OnClickListener{

  public ContextMenu(Context context) {
    super(context);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);


  }

  @Override
  public void onClick(View v) {

  }
}
