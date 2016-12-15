package com.podonomy.podonomyplayer.UITest.PlayerActivityTest;

import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.podonomy.podonomyplayer.R;
import com.podonomy.podonomyplayer.ui.player.PlayerActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static org.hamcrest.Matchers.not;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by lchen on 4/19/2016.
 */
@RunWith(AndroidJUnit4.class)
public class PlayerActivityTest {
    @Rule
    public ActivityTestRule<PlayerActivity> mActivityRule = new ActivityTestRule(PlayerActivity.class);

    //TODO: Test the function of the buttons not only displayed or not; Three titles should have specific function

    @Test
    public void nav_ButtonTest() {
        onView(ViewMatchers.withId(R.id.navButton)).check(matches(isDisplayed()));
    }

    @Test
    public void action_ButtonTest() {
        onView(withId(R.id.actionButton)).check(matches(isDisplayed()));
    }

    @Test
    public void email_ButtonTest() {
        onView(withId(R.id.player_send_invite_button)).check(matches(isDisplayed()));
    }

    @Test
    public void check_box_outline_blank_ButtonTest() {
        onView(withId(R.id.player_mark_read_unread_button)).check(matches(isDisplayed()));
    }

    @Test
    public void fast_rewind_ButtonTest() {
        onView(withId(R.id.player_backward_button)).check(matches(isDisplayed()));
    }

    @Test
    public void play_arrow_ButtonTest() {
        onView(withId(R.id.player_play_pause_button)).check(matches(isDisplayed()));
    }

    @Test
    public void fast_forward_ButtonTest() {
        onView(withId(R.id.player_forward_button)).check(matches(isDisplayed()));
    }

    //click expand, then show five new button and one button disappear
    @Test
    public void expand_ButtonTest() {

        onView(withId(R.id.player_increase_playspeed_button)).check(matches(not(isDisplayed())));
        onView(withId(R.id.player_decrease_playspeed_button)).check(matches(not(isDisplayed())));
        onView(withId(R.id.player_collapse_controls_button)).check(matches(not(isDisplayed())));
        onView(withId(R.id.player_skip_previous_button)).check(matches(not(isDisplayed())));
        onView(withId(R.id.player_skip_next_button)).check(matches(not(isDisplayed())));
        onView(withId(R.id.player_expand_controls_button)).check(matches(isDisplayed()));

        onView(withId(R.id.player_expand_controls_button)).perform(click());

        onView(withId(R.id.player_increase_playspeed_button)).check(matches(isDisplayed()));
        onView(withId(R.id.player_decrease_playspeed_button)).check(matches(isDisplayed()));
        onView(withId(R.id.player_collapse_controls_button)).check(matches(isDisplayed()));
        onView(withId(R.id.player_skip_previous_button)).check(matches(isDisplayed()));
        onView(withId(R.id.player_skip_next_button)).check(matches(isDisplayed()));
        onView(withId(R.id.player_expand_controls_button)).check(matches(not(isDisplayed())));

    }

    //click expand first, then click collapse, got buttons disappear
    @Test
    public void collapse_ButtonTest() {
        onView(withId(R.id.player_expand_controls_button)).perform(click());
        onView(withId(R.id.player_collapse_controls_button)).perform(click());
        //onView(withId(R.id.player_expand_controls_button)).check(matches(isDisplayed()));
        onView(withId(R.id.player_increase_playspeed_button)).check(matches(not(isDisplayed())));
        onView(withId(R.id.player_decrease_playspeed_button)).check(matches(not(isDisplayed())));
        onView(withId(R.id.player_collapse_controls_button)).check(matches(not(isDisplayed())));
        onView(withId(R.id.player_skip_previous_button)).check(matches(not(isDisplayed())));
        onView(withId(R.id.player_skip_next_button)).check(matches(not(isDisplayed())));
        onView(withId(R.id.player_expand_controls_button)).check(matches(isDisplayed()));
    }

    @Test
    public void increase_playspeed_ButtonTest() {
        onView(withId(R.id.player_expand_controls_button)).perform(click());
        onView(withId(R.id.player_increase_playspeed_button)).check(matches(isDisplayed()));
        //test function
    }

    @Test
    public void decrease_playspeed_ButtonTest() {
        onView(withId(R.id.player_expand_controls_button)).perform(click());
        onView(withId(R.id.player_decrease_playspeed_button)).check(matches(isDisplayed()));
        //test function
    }

    @Test
    public void skip_previous_ButtonTest() {
        onView(withId(R.id.player_expand_controls_button)).perform(click());
        onView(withId(R.id.player_skip_previous_button)).check(matches(isDisplayed()));
    }

    @Test
    public void skip_next_ButtonTest() {
        onView(withId(R.id.player_expand_controls_button)).perform(click());
        onView(withId(R.id.player_skip_next_button)).check(matches(isDisplayed()));
    }
}
