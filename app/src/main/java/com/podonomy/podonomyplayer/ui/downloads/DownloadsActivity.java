package com.podonomy.podonomyplayer.ui.downloads;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;
import com.nhaarman.listviewanimations.itemmanipulation.dragdrop.OnItemMovedListener;
import com.nhaarman.listviewanimations.itemmanipulation.dragdrop.TouchViewDraggableManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.podonomy.podonomyplayer.Player_AppCompatActivity;
import com.podonomy.podonomyplayer.R;
import com.podonomy.podonomyplayer.dao.Episode;
import com.podonomy.podonomyplayer.dao.Subscriber;
import com.podonomy.podonomyplayer.dao.SubscriberDAO;
import com.podonomy.podonomyplayer.event.Bus;
import com.podonomy.podonomyplayer.event.DownloadEpisodeMediaCancelEvent;
import com.podonomy.podonomyplayer.event.DownloadEpisodeMediaCompleteEvent;
import com.podonomy.podonomyplayer.event.DownloadingEpisodeMediaCheckedEvent;
import com.podonomy.podonomyplayer.service.downloader.Downloader;
import com.podonomy.podonomyplayer.ui.playlists.PlaylistsActivity;
import com.podonomy.podonomyplayer.ui.settings.SettingsActivity;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rlamsal on 6/17/2016.
 */
public class DownloadsActivity extends Player_AppCompatActivity {

    private DynamicListView runningDownloadsListView = null;
    private TextView emptyView = null;

    private List<Episode> episodes;
    private List<Episode> selectedEpisodes;

    //int count = -1; //used to fill runningDownloadsListView when a new episode has been downloaded

    private static final String INCOMPLETE_FLAG = "incomplete-";

    /**
     * Constructor.  Expects the list of service this activity depends upon. This
     * base class will start the services dependended upon before starting the subclass.
     * Pass null if not services are depended upon.
     */
    public DownloadsActivity() {
        super(new Class[]{Downloader.class});
    }

    @Override
    protected int getViewID(boolean withAds) {
        if (withAds)
            return R.layout.downloads;
        return R.layout.downloads_noads;
    }

    @Override
    protected void _onCreate(Bundle savedInstanceState) {
        super._onCreate(savedInstanceState);

        //episodes = new ArrayList<Episode>();
        selectedEpisodes = new ArrayList<Episode>();
        // Get a handle on our list views.
        runningDownloadsListView = (DynamicListView) findViewById(R.id.running_downloads_drag_list_view);

        // Give each listview the empty list message
        emptyView = new TextView(getBaseContext());
        emptyView.setGravity(Gravity.CENTER);
        emptyView.setText(getBaseContext().getResources().getText(R.string.downloads_you_have_no_downloads));
        ((ViewGroup) runningDownloadsListView.getParent()).addView(emptyView);

        runningDownloadsListView.setEmptyView(emptyView);

        getCurrentDownloadsFromSubscriber();
        fillRunningDownloadsListview();


        final ImageButton actionBtn = (ImageButton) findViewById(R.id.actionButton);
        if (actionBtn == null)
            throw new RuntimeException("The navigation_bar layout was not included as part of your layout");

        actionBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        ActionMenu dialog = new ActionMenu(PlaylistsActivity.this);
//                        dialog.show();

                        //Creating the instance of PopupMenu
                        PopupMenu popup = new PopupMenu(DownloadsActivity.this, actionBtn);
                        //Inflating the Popup using xml file
                        popup.getMenuInflater()
                                .inflate(R.menu.downloads_action_menu, popup.getMenu());

                        //registering popup with OnMenuItemClickListener
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            public boolean onMenuItemClick(MenuItem item) {

                                if (getBaseContext().getResources().getString(R.string.cancel_download).equals(item.toString())) {

                                    //Cancel download for all selected episodes
                                    for (Episode episode: selectedEpisodes){
                                        if (episodes.contains(episode)){
                                            DownloadEpisodeMediaCancelEvent downloadEpisodeMediaCancelEvent = new DownloadEpisodeMediaCancelEvent(episode);
                                            Bus.post(downloadEpisodeMediaCancelEvent);
//                                            CancelEvent ce = new CancelEvent();
//                                            ce.setEventToCancel(ce.getEventID());
//                                            Bus.post(ce);
                                        }

                                    }


                                } else if (getBaseContext().getResources().getString(R.string.pause_download).equals(item.toString())) {



                                } else if (getBaseContext().getResources().getString(R.string.resume_download).equals(item.toString())) {

                                    //
                                } else if (getBaseContext().getResources().getString(R.string.force_download).equals(item.toString())) {

                                    //
                                } else if (getBaseContext().getResources().getString(R.string.settings_action).equals(item.toString())){
                                    Intent intent = new Intent(DownloadsActivity.this, SettingsActivity.class);
                                    startActivity(intent);
                                }
                                return true;
                            }
                        });

                        popup.show(); //showing popup menu


                    }
                }
        );

        //Initialize the image loader
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getBaseContext()).defaultDisplayImageOptions(options).build();
        ImageLoader.getInstance().init(config);

    }

    //accesses the database to get the current downloads
    private void getCurrentDownloadsFromSubscriber() {
        Subscriber subscriber = SubscriberDAO.getCurrentSubscriber(realm);
        episodes = subscriber.getCurrentDownloadEpisodes();


        //episodes = DAO.detach(realm, subscriber.getCurrentDownloadEpisodes());


    }

    private void fillRunningDownloadsListview() {
        if (runningDownloadsListView == null)
            return;
        emptyView.setText(R.string.downloads_you_have_no_downloads);



        //setup the adapter
        DownloadingEpisodesListAdapter adapter = new DownloadingEpisodesListAdapter(episodes);
        runningDownloadsListView.setAdapter(adapter);

        //enable drag and drop functionality
        runningDownloadsListView.enableDragAndDrop();
        runningDownloadsListView.setDraggableManager(new TouchViewDraggableManager(R.id.download_list_row_draganddrop_touchview));
        runningDownloadsListView.setOnItemMovedListener(new MyOnItemMovedListener(adapter));
        runningDownloadsListView.setOnItemLongClickListener(new MyOnItemLongClickListener(runningDownloadsListView));

    }


    private static class MyOnItemLongClickListener implements AdapterView.OnItemLongClickListener {

        private final DynamicListView mListView;

        MyOnItemLongClickListener(final DynamicListView listView) {
            mListView = listView;
        }

        @Override
        public boolean onItemLongClick(final AdapterView<?> parent, final View view, final int position, final long id) {
            if (mListView != null) {
                mListView.startDragging(position - mListView.getHeaderViewsCount());
            }
            return true;
        }
    }



    private class MyOnItemMovedListener implements OnItemMovedListener {

        private final BaseAdapter mAdapter;

        private Toast mToast;

        MyOnItemMovedListener(final BaseAdapter adapter) {
            mAdapter = adapter;
        }

        @Override
        public void onItemMoved(final int originalPosition, final int newPosition) {
            if (mToast != null) {
                mToast.cancel();
            }

            mToast = Toast.makeText(getApplicationContext(), getString(R.string.moved, originalPosition, newPosition), Toast.LENGTH_SHORT);
            mToast.show();
        }
    }


    @Subscribe
    public void onDownloadEpisodeCompleteEvent(DownloadEpisodeMediaCompleteEvent e) {
        if (e != null) {
            Bus.consume(e);
        }

        getCurrentDownloadsFromSubscriber();
        fillRunningDownloadsListview();
    }


    /**
     * This method gets invoked when the {@link DownloadingEpisodeMediaCheckedEvent} is raised (ie. a
     * currently downloading episode file is checked/selected.
     */
    @Subscribe
    public void onDownloadingEpisodeMediaCheckedEvent(DownloadingEpisodeMediaCheckedEvent e) {
        if (e != null) {
            Bus.consume(e);
        }

        selectedEpisodes.add(e.getEpisode());


    }

    //
    @Override
    protected void _onStart() {
        ////
        //Fire the event that the user is viewing his/her subscriptions.
        Bus.registerForEvents(this);
    }

//
//    @Override
//    protected void _onStop() {
//        Bus.unregister(this);
//    }

    @Override
    protected void _onDestroy() {
        Bus.unregister(this);
    }
}
