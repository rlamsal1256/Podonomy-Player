package com.podonomy.podonomyplayer.UITest.PlaylistsActivityTest;

import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.podonomy.podonomyplayer.R;
import com.podonomy.podonomyplayer.ui.playlists.PlaylistsActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by lchen on 4/12/2016.
 */
@RunWith(AndroidJUnit4.class)
public class PlaylistsActivityTest {
    @Rule
    public ActivityTestRule<PlaylistsActivity> mActivityRule = new ActivityTestRule(PlaylistsActivity.class);

    @Test
    public void nav_ButtonTest() {
        onView(ViewMatchers.withId(R.id.navButton)).check(matches(isDisplayed()));
    }

    @Test
    public void action_ButtonTest() {
        onView(withId(R.id.actionButton)).check(matches(isDisplayed()));
    }
}
