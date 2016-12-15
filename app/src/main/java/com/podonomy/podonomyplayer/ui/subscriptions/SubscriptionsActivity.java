package com.podonomy.podonomyplayer.ui.subscriptions;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.podonomy.podonomyplayer.Player_AppCompatActivity;
import com.podonomy.podonomyplayer.R;
import com.podonomy.podonomyplayer.dao.Channel;
import com.podonomy.podonomyplayer.dao.ChannelDAO;
import com.podonomy.podonomyplayer.dao.DAO;
import com.podonomy.podonomyplayer.dao.SearchQuery;
import com.podonomy.podonomyplayer.dao.Subscriber;
import com.podonomy.podonomyplayer.dao.SubscriberDAO;
import com.podonomy.podonomyplayer.event.Bus;
import com.podonomy.podonomyplayer.event.CancelEvent;
import com.podonomy.podonomyplayer.event.SearchEvent;
import com.podonomy.podonomyplayer.service.TaskExecutorService;
import com.podonomy.podonomyplayer.service.downloader.Downloader;
import com.podonomy.podonomyplayer.service.player.Player;
import com.podonomy.podonomyplayer.service.search.SearchCompleteEvent;
import com.podonomy.podonomyplayer.service.search.SearchStatusUpdateEvent;
import com.podonomy.podonomyplayer.service.unsubscribe.UnsubscribeFromChannelCompleteEvent;
import com.podonomy.podonomyplayer.ui.settings.SettingsActivity;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * This is the subscription activity screen showing the user his/hers subscription and providing the
 * ability to search for new channels / episodes.
 */
public class SubscriptionsActivity extends Player_AppCompatActivity {
    private ListView subscriptionsListView = null;
    //  private ListView episodeSearchResultListView = null;
    private ListView channelSearchResultsListView = null;
    private TextView emptyView = null;
    private SearchDialog dialog = null;
    private ProgressDialog progressDialog = null;
    private SearchQuery query = null;
    private TabHost tabs = null;
    private long eventID = 0L;
    private final String SUBSCRIPTION_TAB = "subscriptionTab";
    private final String SEARCH_RESULTS_TAB = "searchResultsTab";
    private final String EPISODES_TAB = "episodesTab";

    public SubscriptionsActivity() {
        super(new Class[]{TaskExecutorService.class, Downloader.class, Player.class});
    }

    @Override
    protected int getViewID(boolean withAds) {
        if (withAds)
            return R.layout.subscriptions;
        return R.layout.subscriptions_noads;
    }


    @Override
    protected void _onCreate(Bundle savedInstanceState) {
        final String episodesTab = getResources().getString(R.string.subscriptions_episodes_tab);

        //////////////
        // Setup the tabs and their content
        tabs = (TabHost) findViewById(R.id.subscriptions_tabhost);
        tabs.setup();

        TabWidget widget = tabs.getTabWidget();


        TabHost.TabSpec spec = tabs.newTabSpec(SUBSCRIPTION_TAB);
        spec.setContent(R.id.subscriptions_tab_listview);
        spec.setIndicator(makeTabIndicator(tabs.getContext(),
                getBaseContext().getString(R.string.subscriptions_subscriptions_tab),
                R.id.subscriptions_subscriptions_tag));
        tabs.addTab(spec);
/*
    spec=tabs.newTabSpec(episodesTab);
    spec.setContent(R.id.subscription_episode_results_listview);
    spec.setIndicator(episodesTab);
    tabs.addTab(spec);
*/
        spec = tabs.newTabSpec(SEARCH_RESULTS_TAB);
        spec.setContent(R.id.subscription_search_results_listview);
        spec.setIndicator(makeTabIndicator(tabs.getContext(),                getBaseContext().getString(R.string.subscriptions_search_results_tab),
                R.id.subscriptions_search_results_tab));
        tabs.addTab(spec);

        tabs.getTabWidget().getChildAt(0).getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
        tabs.getTabWidget().getChildAt(1).getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
//    tabs.getTabWidget().getChildAt(2).getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;

        ////
        // display the search button on the nav bar....
        ImageButton searchButton = (ImageButton) findViewById(R.id.navbar_search_button);
        searchButton.setVisibility(View.VISIBLE);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SubscriptionsActivity.this.onSearchButtonClicked(v);
            }
        });

        /////
        // Get a handle on our list views.
        subscriptionsListView = (ListView) findViewById(R.id.subscriptions_tab_listview);
//    episodeSearchResultListView  = (ListView) findViewById(R.id.subscription_episode_results_listview);
        channelSearchResultsListView = (ListView) findViewById(R.id.subscription_search_results_listview);

        /////
        // Give each listview the empty list message
        emptyView = new TextView(getBaseContext());
        emptyView.setGravity(Gravity.CENTER);
        emptyView.setText(getBaseContext().getResources().getText(R.string.subscriptions_you_have_no_subscriptions));
        ((ViewGroup) subscriptionsListView.getParent()).addView(emptyView);

        subscriptionsListView.setEmptyView(emptyView);
//    episodeSearchResultListView.setEmptyView(emptyView);
        channelSearchResultsListView.setEmptyView(emptyView);

        // Adjust the empty list message based on the tab the user clicks
        tabs.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                if (tabId == null) return;
                if (tabId.equals(SUBSCRIPTION_TAB)) {
                    emptyView.setText(getBaseContext().getResources().getText(R.string.subscriptions_you_have_no_subscriptions));
                    tabs.setCurrentTabByTag(SUBSCRIPTION_TAB);
                    //when tab is changed, subscription tab view needs to update
                    fillSubscriptionListview();

                } else {
                    emptyView.setText(getBaseContext().getResources().getText(R.string.subscriptions_no_search_results));
                    tabs.setCurrentTabByTag(SEARCH_RESULTS_TAB);
                    //when subscription activity is opened, search results needs to be updated from last search query
                    Subscriber subscriber = SubscriberDAO.getCurrentSubscriber(realm);
                    SearchQuery query = subscriber.getLastSearchQuery();
                    if (query != null)
                        fillChannelSearchResults(DAO.detach(realm, query.getChannelsFound()));
                }
            }
        });

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
                        PopupMenu popup = new PopupMenu(SubscriptionsActivity.this, actionBtn);
                        //Inflating the Popup using xml file
                        popup.getMenuInflater()
                                .inflate(R.menu.subscriptions_action_menu, popup.getMenu());

                        //registering popup with OnMenuItemClickListener
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            public boolean onMenuItemClick(MenuItem item) {

                                if (getBaseContext().getResources().getString(R.string.settings_action).equals(item.toString())){
                                    Intent intent = new Intent(SubscriptionsActivity.this, SettingsActivity.class);
                                    startActivity(intent);
                                }
                                return true;
                            }
                        });

                        popup.show(); //showing popup menu


                    }
                }
        );

        //setting the initial default tab as subscription tab
        fillSubscriptionListview();
        tabs.setCurrentTabByTag(SUBSCRIPTION_TAB);

        ///////
        //Initialize the image loader
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getBaseContext()).defaultDisplayImageOptions(options).build();
        ImageLoader.getInstance().init(config);
    }

    /**
     * Creates a tab indicator.
     *
     * @param context
     * @param displayStr - The string to display in the tab indicator (what the user sees)
     * @param viewID     - The ID to give to this tab indicator
     * @return
     */
    private View makeTabIndicator(Context context, String displayStr, int viewID) {
        View view = LayoutInflater.from(context).inflate(R.layout.subscriptions_tab_layout, null);
        TextView tv = (TextView) view.findViewById(R.id.textView);
        tv.setText(displayStr);
        tv.setId(viewID);
        return view;
    }


    @Override
    protected void _onStart() {
        ////
        //Fire the event that the user is viewing his/her subscriptions.
        Bus.registerForEvents(this);
        //Bus.postAny(new ViewSubscriptionsEvent());
    }


    @Override
    protected void _onStop() {
        Bus.unregister(this);
    }

    private void onSearchButtonClicked(View v) {
        tabs.setCurrentTabByTag(SEARCH_RESULTS_TAB);
        dialog = new SearchDialog(this, eLog());
        dialog.setSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                query = null;
                query = dialog.getSearchQuery();
                DAO.beginTransaction(realm);
                realm.copyToRealm(query);
                DAO.commitTransaction(realm);
                performSearch();
            }
        });
        dialog.show();
    }

    /**
     * This method gets called to perform a search given the search parameters...
     */
    private void performSearch() {
        if (query == null)
            return;

        Subscriber subscriber = SubscriberDAO.getCurrentSubscriber(realm);
        DAO.beginTransaction(realm);
        if (subscriber.getLastSearchQuery() != null)
            subscriber.getLastSearchQuery().getEx().delete();  //clear any previous search
        query = realm.copyToRealm(query);
        subscriber.setLastSearchQuery(query);
        DAO.commitTransaction(realm);

        SearchEvent event = new SearchEvent(query);
        eventID = event.getEventID();
        Bus.post(event);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.search_search_wait_msg));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                CancelEvent ce = new CancelEvent();
                ce.setEventToCancel(eventID);
                Bus.post(ce);
            }
        });
        progressDialog.show();
    }

    public void fillSubscriptionListview() {
        if (subscriptionsListView == null)
            return;
        emptyView.setText(R.string.subscriptions_you_have_no_subscriptions);

        List<Channel> subscribedChannels = ChannelDAO.getSubscribedChannels(realm);
        ChannelListAdapter adapter = new ChannelListAdapter(DAO.detach(realm, subscribedChannels));
        subscriptionsListView.setAdapter(adapter);
    }

    private void fillChannelSearchResults(List<Channel> channels) {
        if (channelSearchResultsListView == null)
            return;

        ChannelListAdapter adapter = new ChannelListAdapter(DAO.detach(realm, channels));
        channelSearchResultsListView.setAdapter(adapter);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUnsubscribeFromChannelCompleteEvent(UnsubscribeFromChannelCompleteEvent e) {
        if (e != null) {
            Bus.consume(e);
        }

        fillSubscriptionListview();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSearchComplete(SearchCompleteEvent event) {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
        if (event != null)
            Bus.consume(event);
        Subscriber subscriber = SubscriberDAO.getCurrentSubscriber(realm);
        SearchQuery query = subscriber.getLastSearchQuery();
        if (query != null)
            fillChannelSearchResults(DAO.detach(realm, query.getChannelsFound()));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSearchUpdate(SearchStatusUpdateEvent event) {
    }


    class EmptyList extends TextView {
        public EmptyList(Context ctx, int resMsgID) {
            super(ctx);
            setText(resMsgID);
            setGravity(Gravity.CENTER);
        }
    }
}
