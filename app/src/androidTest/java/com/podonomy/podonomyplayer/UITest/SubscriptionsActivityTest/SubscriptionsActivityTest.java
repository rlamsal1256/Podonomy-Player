package com.podonomy.podonomyplayer.UITest.SubscriptionsActivityTest;

import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;

import com.podonomy.podonomyplayer.PlayerApplication;
import com.podonomy.podonomyplayer.R;
import com.podonomy.podonomyplayer.ui.subscriptions.SubscriptionsActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by lchen on 4/19/2016.
 */
public class SubscriptionsActivityTest {
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

    @Test
    public void nav_ButtonTest() {
        onView(ViewMatchers.withId(R.id.navButton)).check(matches(isDisplayed()));
        onView(withId(R.id.navButton)).perform(click());
        onView(withId(R.id.previousButton)).check(matches(isDisplayed()));
        onView(withId(R.id.episodeImageButton)).check(matches(isDisplayed()));
        onView(withId(R.id.playPauseButton)).check(matches(isDisplayed()));
        onView(withId(R.id.nextButton)).check(matches(isDisplayed()));
        //need to test button function in future

    }

    @Test
    public void action_ButtonTest() {
        onView(withId(R.id.actionButton)).check(matches(isDisplayed()));
    }

    @Test
    public void search_ButtonTest() {
        onView(withId(R.id.navbar_search_button)).check(matches(isDisplayed()));
    }

}


