package de.htw.ai.ema;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.htw.ai.ema.gui.MainActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void testElementsAreDisplayed(){
        onView(withId(R.id.main_activity)).check(matches(isDisplayed()));
        onView(withId(R.id.game_name)).check(matches(isDisplayed()));
        onView(withId(R.id.game_name)).check(matches(withText("Play")));
        onView(withId(R.id.start_new_game_button)).check(matches(isDisplayed()));
        onView(withId(R.id.start_new_game_button)).check(matches(withText("Start a new game")));
        onView(withId(R.id.join_game_button)).check(matches(isDisplayed()));
        onView(withId(R.id.join_game_button)).check(matches(withText("Join game")));
        onView(withId(R.id.load_game_button)).check(matches(isDisplayed()));
        onView(withId(R.id.load_game_button)).check(matches(withText("Load game")));
    }

    @Test
    public void testStartNewGameButton(){
        onView(withId(R.id.start_new_game_button)).perform(click());
        onView(withId(R.id.activity_start_game)).check(matches(isDisplayed()));
    }

    @Test
    public void testJoinGameButton(){
        onView(withId(R.id.join_game_button)).perform(click());
        onView(withId(R.id.activity_join_game)).check(matches(isDisplayed()));
    }

    @Test
    public void testLoadGameButton(){
        onView(withId(R.id.load_game_button)).perform(click());
        //TODO create view with id activity_load_game
        //onView(withId(R.id.activity_load_game)).check(matches(isDisplayed()));
        fail("Not yet implemented");
    }

}
