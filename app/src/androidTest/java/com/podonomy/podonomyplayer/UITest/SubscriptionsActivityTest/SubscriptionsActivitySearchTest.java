package com.podonomy.podonomyplayer.UITest.SubscriptionsActivityTest;

import android.os.SystemClock;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.widget.AdapterView;

import com.podonomy.podonomyplayer.PlayerApplication;
import com.podonomy.podonomyplayer.R;
import com.podonomy.podonomyplayer.dao.Channel;
import com.podonomy.podonomyplayer.ui.subscriptions.SubscriptionsActivity;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

/**
 * Created by lchen on 4/19/2016.
 */
public class SubscriptionsActivitySearchTest {
    @Before
    public void setUp() throws Exception{
        PlayerApplication playerApplication = (PlayerApplication) mActivityRule.getActivity().getApplication();
        playerApplication.setFactory(new TestFactory());
    }

    @After
    public void tearDown() throws Exception{
        PlayerApplication playerApplication = (PlayerApplication) mActivityRule.getActivity().getApplication();
        playerApplication.setFactory(null); //reset back to the default factory
    }
    @Rule
    public ActivityTestRule<SubscriptionsActivity> mActivityRule = new ActivityTestRule(SubscriptionsActivity.class);

    //click search button and search for channel related to guns
    //find expected channel, Guns of Hollywood.
    //click the image button to check detail
    @Test
    public void guns_SearchTest() {
//        onView(withId(R.id.channels_tag)).perform(click());
        onView(withId(R.id.subscriptions_subscriptions_tag)).perform(click());
        onView(withId(R.id.navbar_search_button)).check(matches(isDisplayed()));
        onView(withId(R.id.navbar_search_button)).perform(click());
        onView(withId(R.id.search_search_button)).check(matches(isDisplayed()));
        onView(withId(R.id.search_textBox)).perform(typeText("Guns"), closeSoftKeyboard());
        onView(withId(R.id.search_search_button)).perform(click());
        //working on, find the specific data in a list view
        //will not pass when result is not show fast enough. need to sleep for a few seconds after click search
        SystemClock.sleep(1000);//sleep 1 seconds

        //click on the Guns of Hollywood channel's image
        onView(withId(R.id.subscriptions_search_results_tab)).perform(click());
        onData(withChannelName("Guns of Hollywood"))//list view, use onData() function
                .inAdapterView(allOf(isAssignableFrom(AdapterView.class),isDisplayed()))
                .onChildView(withId(R.id.subscriptions_subscriptions_list_view_item_channelImageButton))
                .perform(click());

        //title match
        onView(withId(R.id.channel_details_channelName)).check(matches(withText("Guns of Hollywood")));
        //checkbox displayed, has further function to test
        onView(withId(R.id.channel_details_subscribeCheckbox)).check(matches(isDisplayed()));
        //type image displayed, should test match audio or video
        onView(withId(R.id.channel_details_contentType)).check(matches(isDisplayed()));
        //nest should test image click and text of description
    }

    public static Matcher<Object> withChannelName(final String channelName) {
        return new BoundedMatcher<Object, Channel>(Channel.class) {
            @Override
            protected boolean matchesSafely(Channel item) {
                return channelName.equals(item.getName());
            }
            @Override
            public void describeTo(Description description) {
                description.appendText("with id: " + channelName);
            }
        };
    }


}


