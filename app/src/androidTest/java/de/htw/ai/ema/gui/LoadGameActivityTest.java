package de.htw.ai.ema.gui;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import de.htw.ai.ema.R;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.core.internal.deps.dagger.internal.Preconditions.checkNotNull;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoadGameActivityTest {

    @Rule
    public ActivityTestRule<LoadGameActivity> activityRule = new ActivityTestRule<>(LoadGameActivity.class);

    @Test
    public void testElementsAreDisplayed(){
        onView(withId(R.id.activity_load_game)).check(matches(isDisplayed()));
        onView(withId(R.id.text_view_select_game)).check(matches(isDisplayed()));
        onView(withId(R.id.text_view_select_game)).check(matches(withText("Select the game you want to load")));
        onView(withId(R.id.recycler_view_load_game)).check(matches(isDisplayed()));
        onView(withId(R.id.button_load_selected_game)).check(matches(isDisplayed()));
        onView(withId(R.id.button_load_selected_game)).check(matches(withText("Load game")));
    }

    //TODO if there aren't any previously saved games on the device this test will fail.
    // If there aren't any games this should be displayed to the user and tested accordingly.
    @Test
    public void testRecyclerViewLoadGame(){
        onView(withId(R.id.recycler_view_load_game)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.recycler_view_load_game)).check(matches(atPosition(0, withBgColor(Color.GREEN))));
    }

    //same problem as above
    @Test
    public void testLoadGameButtonWithGameSelected(){
        onView(withId(R.id.recycler_view_load_game)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.button_load_selected_game)).perform(click());
        onView(withId(R.id.player_names_recycler_view)).check(matches(isDisplayed()));
        onView(withId(R.id.player_names_recycler_view)).check(matches(atPosition(0, isDisplayed())));
        onView(withId(R.id.player_names_recycler_view)).check(matches(atPosition(1, isDisplayed())));
        onView(withId(R.id.player_names_recycler_view)).check(matches(atPosition(2, isDisplayed())));
        onView(withId(R.id.player_names_recycler_view)).check(matches(atPosition(3, isDisplayed())));
    }

    //TODO implement this, //same problem as above
    @Test
    public void testLoadGameButtonWithNoGameSelected(){
        onView(withId(R.id.button_load_selected_game)).perform(click());
        onView(withId(R.id.text_view_select_game)).check(matches(withText("Please select a game!")));
    }

    //TODO implement, //same problem as above
    @Test
    public void testRecyclerViewPlayerNames(){
        onView(withId(R.id.recycler_view_load_game)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.button_load_selected_game)).perform(click());
        onView(withId(R.id.player_names_recycler_view)).check(matches(isDisplayed()));
        onView(withId(R.id.player_names_recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.player_names_recycler_view)).check(matches(atPosition(0, withBgColor(Color.GREEN))));
    }

    @Test
    public void testStartAsPlayerButton(){
        //onView(withId(R.id.text_view_select_game)).check(matches(withText("Waiting for other players to join the game...")));
        //onView(withId(R.id.activity_play_game)).check(matches(isDisplayed()));
        fail("Not yet implemented");
    }



    public static Matcher<View> atPosition(final int position, @NonNull final Matcher<View> itemMatcher) {
        checkNotNull(itemMatcher);
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("has item at position " + position + ": ");
                itemMatcher.describeTo(description);
            }

            @Override
            protected boolean matchesSafely(final RecyclerView view) {
                RecyclerView.ViewHolder viewHolder = view.findViewHolderForAdapterPosition(position);
                if (viewHolder == null) {
                    // has no item on such position
                    return false;
                }
                return itemMatcher.matches(viewHolder.itemView);
            }
        };
    }

    public static Matcher<View> withBgColor(final int color) {
        checkNotNull(color);
        return new BoundedMatcher<View, TextView>(TextView.class) {

            int cd;
            @Override
            protected boolean matchesSafely(TextView item) {
                cd = ((ColorDrawable) item.getBackground()).getColor();
                return color == cd;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with background color: "+cd);
            }
        };
    }
}
