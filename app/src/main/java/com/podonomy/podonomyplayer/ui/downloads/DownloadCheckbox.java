package com.podonomy.podonomyplayer.ui.downloads;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.podonomy.podonomyplayer.R;
import com.podonomy.podonomyplayer.dao.Episode;
import com.podonomy.podonomyplayer.event.Bus;
import com.podonomy.podonomyplayer.event.DownloadingEpisodeMediaCheckedEvent;

/**
 * Created by rlamsal on 7/8/2016.
 */
public class DownloadCheckbox extends CheckBox implements CheckBox.OnCheckedChangeListener {

    protected Episode episode = null;

    /* ===================================================
       Mandatory Constructors to satisfy android designer
       =================================================== */
    public DownloadCheckbox(Context ctx) {
        super(ctx);
        setOnCheckedChangeListener(this);
    }

    public DownloadCheckbox(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnCheckedChangeListener(this);
    }

    public DownloadCheckbox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnCheckedChangeListener(this);
    }

    public DownloadCheckbox(Context ctx, Episode episode) {
        this(ctx);
        setEpisode(episode);
        setOnCheckedChangeListener(this);
    }

    /**
     * Sets the channel this checkbox represents.
     */
    public void setEpisode(Episode episode) {
        this.episode = episode;
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if (isChecked) {
            DownloadingEpisodeMediaCheckedEvent event = new DownloadingEpisodeMediaCheckedEvent(episode);
            Bus.post(event);
        }

    }


    protected void showChecked() {
        setButtonDrawable(getThemeImageID(R.attr.ic_checkbox_checked, R.drawable.ic_check_box_outline_blank_black_24dp));
    }

    protected void showUnchecked() {
        setButtonDrawable(getThemeImageID(R.attr.ic_checkbox_unchecked, R.drawable.ic_check_box_black_24dp));

    }

    protected int getThemeImageID(int attr, final int defaultValue) {
        Resources.Theme theme = this.getContext().getTheme();
        TypedValue typedvalueattr = new TypedValue();
        final boolean found = theme.resolveAttribute(attr, typedvalueattr, true);
        return found ? typedvalueattr.resourceId : defaultValue;
    }

}
