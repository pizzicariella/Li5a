package de.htw.ai.ema.gui;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.htw.ai.ema.R;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.core.internal.deps.dagger.internal.Preconditions.checkNotNull;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import androidx.test.espresso.contrib.RecyclerViewActions;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class JoinGameActivityTest {

    @Rule
    public ActivityTestRule<JoinGameActivity> activityRule = new ActivityTestRule<>(JoinGameActivity.class);

    @Test
    public void testElementsAreDisplayed(){
        onView(ViewMatchers.withId(R.id.activity_join_game)).check(matches(isDisplayed()));
        onView(withId(R.id.enter_player_name_join)).check(matches(isDisplayed()));
        onView(withId(R.id.enter_player_name_join)).check(matches(withText("Enter your name")));
        onView(withId(R.id.recycler_view_available_devices)).check(matches(isDisplayed()));
        onView(withId(R.id.button_connect)).check(matches(isDisplayed()));
        onView(withId(R.id.button_connect)).check(matches(withText("Connect")));
        onView(withId(R.id.wait_text_view_join)).check(matches(not(isDisplayed())));
        onView(withId(R.id.wait_text_view_join)).check(matches(withText("Waiting for other Players to join the game...")));
    }

    /*TODO if the test device has not been connected to any bluetooth devices previously, this test fails
        solve by mocking bt device?
     */

    @Test
    public void testDeviceRecyclerView(){
        onView(ViewMatchers.withId(R.id.recycler_view_available_devices))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.recycler_view_available_devices)).check(matches(atPosition(0, withBgColor(Color.GREEN))));
    }

    /*TODO if the test device has not been connected to any bluetooth devices previously, this test fails
        solve by mocking bt device?
     */
    @Test
    public void testConnectButtonDeviceSelected(){
        onView(withId(R.id.recycler_view_available_devices))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.button_connect)).perform(click());
        onView(withId(R.id.wait_text_view_join)).check(matches(isDisplayed()));
        // not testable because we don't know which device is index 0
        //onView(withId(R.id.activity_play_game)).check(matches(isDisplayed()));
    }

    @Test
    public void testConnectButtonWithNoDeviceSelected(){
        onView(withId(R.id.button_connect)).perform(click());
        onView(withId(R.id.wait_text_view_join)).check(matches(isDisplayed()));
        onView(withId(R.id.wait_text_view_join)).check(matches(withText("Please select a device")));
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
            @Override
            protected boolean matchesSafely(TextView item) {
                return color == ((ColorDrawable) item.getBackground()).getColor();
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with background color: ");
            }
        };
    }

}
