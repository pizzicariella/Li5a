package de.htw.ai.ema;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.htw.ai.ema.gui.StartGameActivity;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class StartGameActivityTest {

    @Rule
    public ActivityTestRule<StartGameActivity> activityRule = new ActivityTestRule<>(StartGameActivity.class);

    @Test
    public void testElementsAreDisplayed(){
        onView(withId(R.id.activity_start_game)).check(matches(isDisplayed()));
        onView(withId(R.id.enter_player_name_start)).check(matches(isDisplayed()));
        onView(withId(R.id.enter_player_name_start)).check(matches(withText("Enter your name")));
        onView(withId(R.id.button_host_game)).check(matches(isDisplayed()));
        onView(withId(R.id.button_host_game)).check(matches(withText("Host game")));
        onView(withId(R.id.wait_text_view_start)).check(matches(not(isDisplayed())));
        onView(withId(R.id.wait_text_view_start)).check(matches(withText("Waiting for other Players to join the game...")));
    }

    @Test
    public void testHostGameButtonWaiting(){
        onView(withId(R.id.button_host_game)).perform(click());
        onView(withId(R.id.wait_text_view_start)).check(matches(isDisplayed()));
    }

    // It's necessary to connect with 3 devices as Clients for this test to work
    @Test
    public void testHostGameButtonConnected(){
        onView(withId(R.id.button_host_game)).perform(click());
        onView(withId(R.id.activity_play_game)).check(matches(isDisplayed()));
    }
}
