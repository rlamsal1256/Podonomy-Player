package com.podonomy.podonomyplayer.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.podonomy.podonomyplayer.R;
import com.podonomy.podonomyplayer.dao.Channel;
import com.podonomy.podonomyplayer.event.Bus;
import com.podonomy.podonomyplayer.event.SubscribeToChannelEvent;
import com.podonomy.podonomyplayer.event.UnsubscribeFromChannelEvent;
import com.podonomy.podonomyplayer.service.unsubscribe.UnsubscribeFromChannelCompleteEvent;

import org.greenrobot.eventbus.Subscribe;

/**
 * This class represents a checkbox associated with a channel.  It implements the functionality so that
 * when the checkbox is check/unchecked the channel is subscribed/unsubscribed to.
 * Since this is a UI component, any database object passed to it should be detached from the database.
 */
public class SubscribeCheckbox extends CheckBox implements CheckBox.OnCheckedChangeListener {
    protected Channel channel = null;
    protected ProgressDialog pd = null;
    protected UnsubscribeFromChannelEvent event = null;
    private boolean initialization = false;
    /* ===================================================
       Mandatory Constructors to satisfy android designer
       =================================================== */
    public SubscribeCheckbox(Context ctx) {
        super(ctx);
        setOnCheckedChangeListener(this);
    }


    public SubscribeCheckbox(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnCheckedChangeListener(this);
    }

    public SubscribeCheckbox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnCheckedChangeListener(this);
    }


    public SubscribeCheckbox(Context ctx, Channel channel) {
        this(ctx);
        setChannel(channel);
        setOnCheckedChangeListener(this);
    }

    /**
     * Sets the channel this checkbox represents.
     */
    public void setChannel(Channel channel) {
        this.channel = channel;
        if (channel != null) {
            initialization = true;
            setChecked(channel.isSubscribed());
            initialization = false;
        }
    }

    public void unsubscribe() {

        channel.setSubscribed(false);
        event = new UnsubscribeFromChannelEvent(channel);
        Bus.post(event);
        pd = new ProgressDialog(getContext());
        pd.setMessage(getContext().getString(R.string.unsubscribing_wait_message));
        pd.setCancelable(false);
        pd.setIndeterminate(true);
        pd.show();


        Bus.registerForEvents(this);

        return;
    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    //Yes button clicked
                    unsubscribe();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    //do nothing
                    break;
            }
        }
    };


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (channel == null)
            return;

        if (initialization) {
            setCheckbox();
            return;
        }

        if (isChecked && !channel.isSubscribed()) {
            channel.setSubscribed(true);
            SubscribeToChannelEvent event = new SubscribeToChannelEvent(channel);
            Bus.post(event);
        } else {


            //display a will be deleted warning
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage("Are you sure you wish to unsubscribe from the '" + channel.getName() + "' channel? All downloaded episode will be deleted.")
                    .setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();


        }
        setCheckbox();
    }

    protected void setCheckbox() {
        if (channel.isSubscribed())
            showChecked();
        else
            showUnchecked();
    }

    protected void showChecked() {
        setButtonDrawable(getThemeImageID(R.attr.ic_checkbox_checked, R.drawable.ic_check_box_outline_blank_black_24dp));
    }

    protected void showUnchecked() {
        setButtonDrawable(getThemeImageID(R.attr.ic_checkbox_unchecked, R.drawable.ic_check_box_black_24dp));

    }

    //TODO: this function might be used multiple times, so should in some base class
    protected int getThemeImageID(int attr, final int defaultValue) {
        Resources.Theme theme = this.getContext().getTheme();
        TypedValue typedvalueattr = new TypedValue();
        final boolean found = theme.resolveAttribute(attr, typedvalueattr, true);
        return found ? typedvalueattr.resourceId : defaultValue;
    }

    @Subscribe
    public void onUnsubscribeFromChannelCompleteEvent(UnsubscribeFromChannelCompleteEvent e) {
        if (e != null && event != null) {
            Bus.consume(e);
            Bus.unregister(this);
            if (pd != null)
                pd.cancel();
        }

    }





}
